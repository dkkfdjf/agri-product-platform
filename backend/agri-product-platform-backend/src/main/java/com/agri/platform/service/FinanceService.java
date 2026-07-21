package com.agri.platform.service;

import com.agri.platform.dto.BankFinanceStats;
import com.agri.platform.dto.FinanceApprovalRequest;
import com.agri.platform.dto.FinanceProductRequest;
import com.agri.platform.dto.FinanceRequest;
import com.agri.platform.entity.Finance;
import com.agri.platform.entity.FinanceProduct;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.math.BigDecimal;

public interface FinanceService {
    void publishProduct(String userName, FinanceProductRequest request);
    Page<FinanceProduct> getProductList(int page, int pageSize);
    FinanceProduct getProductById(Integer productId);
    void applyFinance(String userName, FinanceRequest request);
    Page<Finance> getApplyList(String userName, int page, int pageSize);
    Page<Finance> getBankApprovalList(int page, int pageSize);
    void approveFinance(Integer financeId, FinanceApprovalRequest request);
    void updateFinance(Integer financeId, String userName, FinanceRequest request);
    void deleteFinance(Integer financeId, String userName);

    /** 银行工作台：全状态融资申请列表（支持状态/关键词/产品/时间区间过滤） */
    Page<Finance> getBankApplications(Integer status, String keyword, Integer productId, String startDate, String endDate, int page, int pageSize);

    /**
     * 银行智能匹配：在「申请中」的真实融资申请(tb_finance, status=0)里，按金额区间/期限/作物关键字筛选，
     * 并回填 productName + credit + productNames(该农户的农产品)，按信用分高低优先展示，便于银行主动对接。
     * @param minMoney  金额下限（元），null 忽略
     * @param maxMoney  金额上限（元），null 忽略
     * @param repayment 期限（月），null/0 忽略
     * @param keyword   作物或用途关键字：匹配融资用途 或 该农户发布的农产品标题/类型
     */
    Page<Finance> getBankMatch(BigDecimal minMoney, BigDecimal maxMoney, Integer repayment, String keyword, int page, int pageSize);

    /** 银行工作台：数据概览聚合统计 */
    BankFinanceStats getBankStats();

    /** 银行工作台：更新融资产品（仅银行角色）。核心条款(额度/利率/期限)发布后锁定，仅可改介绍与电话 */
    void updateProduct(String userName, Integer productId, FinanceProductRequest request);

    /** 银行工作台：融资产品不支持删除，仅切换上下架状态（0在售 / 1暂停供应） */
    void setProductStatus(String userName, Integer productId, Integer status);

    /** 银行工作台：本行全部融资产品（含已暂停），供产品管理页展示 */
    Page<FinanceProduct> getBankProductList(int page, int pageSize);
}