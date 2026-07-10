package com.agri.platform.service;

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
}