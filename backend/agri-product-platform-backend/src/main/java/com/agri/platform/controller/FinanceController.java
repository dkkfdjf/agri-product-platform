package com.agri.platform.controller;

import com.agri.platform.common.Result;
import com.agri.platform.dto.FinanceApprovalRequest;
import com.agri.platform.dto.FinanceProductRequest;
import com.agri.platform.dto.FinanceRequest;
import com.agri.platform.dto.FinancingIntentionRequest;
import com.agri.platform.entity.Finance;
import com.agri.platform.entity.FinanceProduct;
import com.agri.platform.entity.FinancingIntention;
import com.agri.platform.service.FinanceService;
import com.agri.platform.service.FinancingIntentionService;
import com.agri.platform.util.JwtUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/finance")
public class FinanceController {

    @Autowired
    private FinanceService financeService;

    @Autowired
    private FinancingIntentionService intentionService;

    @Autowired
    private JwtUtil jwtUtil;

    // 融资产品相关
    @PostMapping("/product/publish")
    public Result<String> publishProduct(@RequestHeader("Authorization") String token,
                                          @RequestBody FinanceProductRequest request) {
        String userName = jwtUtil.getUsernameFromToken(token.replace("Bearer ", ""));
        financeService.publishProduct(userName, request);
        return Result.success("产品发布成功");
    }

    @GetMapping("/product/list")
    public Result<Page<FinanceProduct>> productList(@RequestParam(defaultValue = "1") int page,
                                                      @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(financeService.getProductList(page, pageSize));
    }

    @GetMapping("/product/{productId}")
    public Result<FinanceProduct> productDetail(@PathVariable Integer productId) {
        return Result.success(financeService.getProductById(productId));
    }

    @PostMapping("/apply")
    public Result<String> apply(@RequestHeader("Authorization") String token,
                                @RequestBody FinanceRequest request) {
        String userName = jwtUtil.getUsernameFromToken(token.replace("Bearer ", ""));
        financeService.applyFinance(userName, request);
        return Result.success("申请提交成功");
    }

    @GetMapping("/apply/list")
    public Result<Page<Finance>> myApply(@RequestHeader("Authorization") String token,
                                          @RequestParam(defaultValue = "1") int page,
                                          @RequestParam(defaultValue = "10") int pageSize) {
        String userName = jwtUtil.getUsernameFromToken(token.replace("Bearer ", ""));
        return Result.success(financeService.getApplyList(userName, page, pageSize));
    }

    @GetMapping("/apply/bank")
    public Result<Page<Finance>> bankApprovalList(@RequestParam(defaultValue = "1") int page,
                                                   @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(financeService.getBankApprovalList(page, pageSize));
    }

    @PutMapping("/apply/{financeId}")
    public Result<String> approve(@PathVariable Integer financeId,
                                  @RequestBody FinanceApprovalRequest request) {
        financeService.approveFinance(financeId, request);
        return Result.success("审批完成");
    }

    @PutMapping("/apply/update/{financeId}")
    public Result<String> updateFinance(@PathVariable Integer financeId,
                                        @RequestHeader("Authorization") String token,
                                        @RequestBody FinanceRequest request) {
        String userName = jwtUtil.getUsernameFromToken(token.replace("Bearer ", ""));
        financeService.updateFinance(financeId, userName, request);
        return Result.success("申请更新成功");
    }

    // 融资意向相关
    @PostMapping("/intention")
    public Result<String> submitIntention(@RequestHeader("Authorization") String token,
                                           @RequestBody FinancingIntentionRequest request) {
        String userName = jwtUtil.getUsernameFromToken(token.replace("Bearer ", ""));
        intentionService.submitIntention(userName, request);
        return Result.success("意向提交成功");
    }

    @GetMapping("/intention/match")
    public Result<Page<FinancingIntention>> matchList(@RequestParam(required = false) Integer minAmount,
                                                       @RequestParam(required = false) Integer maxAmount,
                                                       @RequestParam(required = false) String item,
                                                       @RequestParam(defaultValue = "1") int page,
                                                       @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(intentionService.getMatchedList(
                minAmount != null ? minAmount : 0,
                maxAmount != null ? maxAmount : 0,
                item,
                page,
                pageSize
        ));
    }
}