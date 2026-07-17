package com.agri.platform.service.impl;

import com.agri.platform.config.AlipayProperties;
import com.agri.platform.entity.Payment;
import com.agri.platform.entity.Purchase;
import com.agri.platform.mapper.PaymentMapper;
import com.agri.platform.service.AlipayService;
import com.agri.platform.service.OrderService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePrecreateRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
public class AlipayServiceImpl implements AlipayService {

    @Autowired
    private AlipayProperties alipayProperties;

    @Autowired
    private OrderService orderService;

    @Autowired
    private PaymentMapper paymentMapper;

    private AlipayClient buildClient() {
        return new DefaultAlipayClient(
                alipayProperties.getGateway(),
                alipayProperties.getAppId(),
                alipayProperties.getAppPrivateKey(),
                alipayProperties.getFormat(),
                alipayProperties.getCharset(),
                alipayProperties.getAlipayPublicKey(),
                alipayProperties.getSignType()
        );
    }

    @Override
    public String createQrCode(Integer purchaseId, String userName) {
        // 校验订单归属：非本人订单会抛异常
        Purchase order = orderService.getOrderDetail(purchaseId, userName);
        Integer status = order.getPurchaseStatus();
        if (status == null || status != 1) {
            throw new RuntimeException("订单当前状态不允许支付");
        }

        BigDecimal totalAmount = order.getTotalPrice() == null
                ? BigDecimal.ZERO
                : order.getTotalPrice().setScale(2, RoundingMode.HALF_UP);
        String amount = totalAmount.toPlainString();

        // 用订单主键作为支付宝商户订单号 out_trade_no（便于回调/查询时反查）
        String outTradeNo = String.valueOf(purchaseId);
        String subject = "农产品订单#" + purchaseId;

        // 当面付扫码支付（precreate）：返回二维码链接 qr_code，前端渲染成二维码让买家用支付宝App扫码付款
        AlipayTradePrecreateRequest request = new AlipayTradePrecreateRequest();
        request.setNotifyUrl(alipayProperties.getNotifyUrl());
        String bizContent = "{\"out_trade_no\":\"" + outTradeNo + "\","
                + "\"total_amount\":\"" + amount + "\","
                + "\"subject\":\"" + subject + "\"}";
        request.setBizContent(bizContent);

        try {
            AlipayTradePrecreateResponse response = buildClient().execute(request);
            if (!response.isSuccess()) {
                throw new RuntimeException("生成支付二维码失败：" + response.getSubMsg());
            }
            return response.getQrCode();
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("生成支付二维码失败：" + e.getMessage(), e);
        }
    }

    @Override
    public Map<String, Object> queryAndMarkPaid(Integer purchaseId) {
        Map<String, Object> result = new HashMap<>();
        result.put("purchaseId", purchaseId);

        try {
            AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
            request.setBizContent("{\"out_trade_no\":\"" + purchaseId + "\"}");
            AlipayTradeQueryResponse response = buildClient().execute(request);

            result.put("tradeStatus", response.getTradeStatus());

            if (response.isSuccess() && "TRADE_SUCCESS".equals(response.getTradeStatus())) {
                orderService.markOrderPaid(purchaseId);
                String amtStr = response.getTotalAmount();
                BigDecimal paidAmount = (amtStr != null && !amtStr.isEmpty())
                        ? new BigDecimal(amtStr) : BigDecimal.ZERO;
                savePaymentRecord(purchaseId, response.getTradeNo(), paidAmount, response.getTradeStatus(), null);
                result.put("paid", true);
            } else {
                result.put("paid", false);
            }
        } catch (Exception e) {
            result.put("paid", false);
            result.put("error", e.getMessage());
        }
        return result;
    }

    @Override
    public String handleNotify(Map<String, String> params) {
        try {
            boolean signOk = AlipaySignature.rsaCheckV1(
                    params,
                    alipayProperties.getAlipayPublicKey(),
                    alipayProperties.getCharset(),
                    alipayProperties.getSignType()
            );
            if (!signOk) {
                return "fail";
            }
            String tradeStatus = params.get("trade_status");
            String outTradeNo = params.get("out_trade_no");
            if ("TRADE_SUCCESS".equals(tradeStatus) && outTradeNo != null && !outTradeNo.isEmpty()) {
                Integer purchaseId = Integer.valueOf(outTradeNo);
                orderService.markOrderPaid(purchaseId);
                String amtStr = params.get("total_amount");
                BigDecimal paidAmount = (amtStr != null && !amtStr.isEmpty())
                        ? new BigDecimal(amtStr) : BigDecimal.ZERO;
                savePaymentRecord(purchaseId, params.get("trade_no"), paidAmount, tradeStatus, params.get("gmt_payment"));
            }
            return "success";
        } catch (Exception e) {
            return "fail";
        }
    }

    /**
     * 幂等记录支付流水：按 out_trade_no(=purchaseId) 唯一键查重，存在则更新，不存在则插入。
     * 主动查询走 gmtPayment=null（用当前时间）；异步回调带支付宝返回的 gmt_payment。
     */
    private void savePaymentRecord(Integer purchaseId, String alipayTradeNo, BigDecimal totalAmount,
                                   String tradeStatus, String gmtPayment) {
        String outTradeNo = String.valueOf(purchaseId);
        Payment existing = paymentMapper.selectOne(
                new LambdaQueryWrapper<Payment>().eq(Payment::getOutTradeNo, outTradeNo));
        LocalDateTime payTime = parsePayTime(gmtPayment);
        if (existing == null) {
            Payment payment = new Payment();
            payment.setPurchaseId(purchaseId);
            payment.setOutTradeNo(outTradeNo);
            payment.setAlipayTradeNo(alipayTradeNo);
            payment.setTotalAmount(totalAmount);
            payment.setTradeStatus(tradeStatus);
            payment.setPayTime(payTime);
            paymentMapper.insert(payment);
        } else {
            existing.setAlipayTradeNo(alipayTradeNo);
            existing.setTotalAmount(totalAmount);
            existing.setTradeStatus(tradeStatus);
            existing.setPayTime(payTime);
            paymentMapper.updateById(existing);
        }
    }

    /** 解析支付宝回调里的 gmt_payment（形如 2024-01-01 12:00:00，可能带毫秒/时区后缀），失败回退当前时间。 */
    private LocalDateTime parsePayTime(String gmtPayment) {
        if (gmtPayment == null || gmtPayment.isEmpty()) {
            return LocalDateTime.now();
        }
        try {
            return LocalDateTime.parse(gmtPayment.substring(0, 19),
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        } catch (Exception e) {
            return LocalDateTime.now();
        }
    }
}
