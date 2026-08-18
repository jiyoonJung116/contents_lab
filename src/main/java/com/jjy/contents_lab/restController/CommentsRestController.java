package com.jjy.contents_lab.restController;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jjy.contents_lab.dto.CommentsDto;
import com.jjy.contents_lab.service.CommentsService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/comments")
public class CommentsRestController {
    private final CommentsService commentsService;

    public CommentsRestController(CommentsService commentsService) {
        this.commentsService = commentsService;
    }

    @PostMapping("/save")
    public Map<String, Object> saveComment(HttpServletRequest request, 
                                                @ModelAttribute CommentsDto commentsDto) {
        Map<String, Object> result = new HashMap<>();   
        HttpSession session = request.getSession();

        if (session == null || session.getAttribute("userId") == null) {
            result.put("status", "error");
            result.put("message", "로그인이 필요합니다.");
            return result;
        }

        try {
            commentsService.saveComment(commentsDto);

            result.put("status", "success");
            if (commentsDto.getId() == 0) {
                result.put("message", "댓글이 등록되었습니다.");
            } else {
                result.put("message", "댓글이 수정되었습니다.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.put("status", "error");
            result.put("message", "댓글 저장 중 오류가 발생했습니다.");
        }

        return result;
    }

    @PostMapping("/delete")
    public Map<String, Object> deleteComment(HttpServletRequest request,
                                            @RequestParam("id") long id) {
        Map<String, Object> result = new HashMap<>();
        HttpSession session = request.getSession();

        try {
            Object sessionUserId = session.getAttribute("userId");
            long userId = ((Number) sessionUserId).longValue();

            result.put("status", "success");
            result.put("comments", commentsService.deleteComment(id, userId));
        } catch (Exception e) {
            e.printStackTrace();
            result.put("status", "error");
            result.put("message", "게시글 삭제 중 오류가 발생했습니다.");
        }

        return result;
    }
}
