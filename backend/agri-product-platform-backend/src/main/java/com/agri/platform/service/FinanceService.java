package com.agri.platform.service;

import com.agri.platform.dto.BankFinanceStats;
import com.agri.platform.dto.FinanceApprovalRequest;
import com.agri.platform.dto.FinanceProductRequest;
import com.agri.platform.dto.FinanceRequest;
import com.agri.platform.entity.Finance;
import com.agri.platform.entity.FinanceProduct;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

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

    /** 银行工作台：全状态融资申请列表（支持状态/关键词/时间区间过滤） */
    Page<Finance> getBankApplications(Integer status, String keyword, String startDate, String endDate, int page, int pageSize);

    /** 银行工作台：数据概览聚合统计 */
    BankFinanceStats getBankStats();

    /** 银行工作台：更新融资产品（仅银行角色） */
    void updateProduct(String userName, Integer productId, FinanceProductRequest request);

    /** 银行工作台：删除融资产品（仅银行角色） */
    void deleteProduct(String userName, Integer productId);
}