package com.agri.platform.controller;

import com.agri.platform.common.Result;
import com.agri.platform.dto.QuestionRequest;
import com.agri.platform.entity.Question;
import com.agri.platform.service.QuestionService;
import com.agri.platform.util.JwtUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/question")
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/list")
    public Result<Page<Question>> list(@RequestParam(defaultValue = "1") int page,
                                        @RequestParam(defaultValue = "10") int pageSize,
                                        @RequestParam(required = false) Integer status,
                                        @RequestParam(required = false) String questioner,
                                        @RequestParam(required = false) String expertName) {
        return Result.success(questionService.getQuestionList(page, pageSize, status, questioner, expertName));
    }

    @GetMapping("/{questionId}")
    public Result<Question> getById(@PathVariable Integer questionId) {
        return Result.success(questionService.getQuestionById(questionId));
    }

    @PostMapping
    public Result<String> ask(@RequestHeader("Authorization") String token,
                              @RequestBody QuestionRequest request) {
        String userName = jwtUtil.getUsernameFromToken(token.replace("Bearer ", ""));
        questionService.askQuestion(userName, request);
        return Result.success("提问成功");
    }

    @PutMapping("/{questionId}/answer")
    public Result<String> answer(@PathVariable Integer questionId,
                                  @RequestHeader("Authorization") String token,
                                  @RequestBody Map<String, String> body) {
        String userName = jwtUtil.getUsernameFromToken(token.replace("Bearer ", ""));
        String answer = body != null ? body.get("answer") : null;
        if (answer == null || answer.trim().isEmpty()) {
            return Result.error("回答内容不能为空");
        }
        questionService.answerQuestion(questionId, userName, answer);
        return Result.success("回答成功");
    }

    @PutMapping("/{questionId}/close")
    public Result<String> close(@PathVariable Integer questionId,
                                 @RequestHeader("Authorization") String token) {
        String userName = jwtUtil.getUsernameFromToken(token.replace("Bearer ", ""));
        questionService.closeQuestion(questionId, userName);
        return Result.success("关闭成功");
    }

    /**
     * 删除问题（提问人本人或管理员）
     */
    @DeleteMapping("/{questionId}")
    public Result<String> delete(@PathVariable Integer questionId,
                                  @RequestHeader("Authorization") String token) {
        questionService.deleteQuestion(questionId);
        return Result.success("删除成功");
    }
}
