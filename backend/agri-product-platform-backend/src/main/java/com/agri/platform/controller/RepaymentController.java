package com.agri.platform.controller;

import com.agri.platform.common.Result;
import com.agri.platform.entity.Repayment;
import com.agri.platform.service.RepaymentService;
import com.agri.platform.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 还款计划：查看某笔融资的各期计划、逐期还款。
 */
@RestController
@RequestMapping("/repayment")
public class RepaymentController {

    @Autowired
    private RepaymentService repaymentService;

    @Autowired
    private JwtUtil jwtUtil;

    /** 某笔融资的还款计划（按期数升序，含逾期动态判定） */
    @GetMapping("/finance/{financeId}")
    public Result<List<Repayment>> listByFinance(@PathVariable Integer financeId) {
        return Result.success(repaymentService.listByFinance(financeId));
    }

    /** 还款：标记该期已还 */
    @PostMapping("/{id}/pay")
    public Result<String> pay(@RequestHeader("Authorization") String token,
                              @PathVariable Integer id) {
        String userName = jwtUtil.getUsernameFromToken(token.replace("Bearer ", ""));
        repaymentService.pay(id, userName);
        return Result.success("还款成功");
    }
}
