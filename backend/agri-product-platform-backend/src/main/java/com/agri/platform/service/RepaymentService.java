package com.agri.platform.service;

import com.agri.platform.entity.Finance;
import com.agri.platform.entity.Repayment;

import java.util.List;

/**
 * 还款计划服务：融资通过时生成等额本息计划，农户逐期还款。
 */
public interface RepaymentService {

    /** 融资通过时按等额本息生成还款计划（已存在则跳过，幂等） */
    void generatePlan(Finance finance);

    /** 某笔融资的还款计划（按期数升序，动态判定逾期） */
    List<Repayment> listByFinance(Integer financeId);

    /** 还款：标记该期已还并通知农户 */
    void pay(Integer repaymentId, String userName);
}
