package com.agri.platform.controller;

import com.agri.platform.common.Result;
import com.agri.platform.mapper.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private FinanceMapper financeMapper;

    @Autowired
    private KnowledgeMapper knowledgeMapper;

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private ReserveMapper reserveMapper;

    @GetMapping("/stats")
    public Result<Map<String, Object>> getStats() {
        Map<String, Object> stats = new HashMap<>();

        // 商品数量
        stats.put("productCount", productMapper.selectCount(null));

        // 融资申请数量
        stats.put("financeCount", financeMapper.selectCount(null));

        // 知识文章数量
        stats.put("knowledgeCount", knowledgeMapper.selectCount(null));

        // 问题数量
        stats.put("questionCount", questionMapper.selectCount(null));

        // 预约数量
        stats.put("reserveCount", reserveMapper.selectCount(null));

        return Result.success(stats);
    }

    @GetMapping("/users")
    public Result<String> getAllUsers() {
        // 需要UserService支持，待后续实现
        return Result.success("待实现");
    }
}
