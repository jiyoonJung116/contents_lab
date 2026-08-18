package com.jjy.contents_lab.restController;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jjy.contents_lab.dto.CommunityDto;
import com.jjy.contents_lab.service.CommunityService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/community")
public class CommunityRestController {
    private final CommunityService communityService;

    public CommunityRestController(CommunityService communityService) {
        this.communityService = communityService;
    }

    @PostMapping("/save")
    public Map<String, Object> saveCommunity(HttpServletRequest request, 
                                                @ModelAttribute CommunityDto communityDto) {
        Map<String, Object> result = new HashMap<>();   
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("userId") == null) {
            result.put("status", "error");
            result.put("message", "로그인이 필요합니다.");
            return result;
        }

        try {
            Object sessionUserId = session.getAttribute("userId");
            long userId = ((Number) sessionUserId).longValue();
            communityDto.setUserId(userId);

            boolean isInsert = (communityDto.getId() == 0);
            communityService.saveCommunity(communityDto);

            result.put("status", "success");
            if (isInsert) {
                result.put("message", "게시글이 성공적으로 등록되었습니다.");
            } else {
                result.put("message", "게시글이 성공적으로 수정되었습니다.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.put("status", "error");
            result.put("message", "게시글 저장 중 오류가 발생했습니다.");
        }

        return result;
    }

    @PostMapping("/delete")
    public Map<String, Object> deleteCommunity(HttpServletRequest request,
                                            @RequestParam("communityId") long communityId) {
        Map<String, Object> result = new HashMap<>();
        HttpSession session = request.getSession();

        try {
            Object sessionUserId = session.getAttribute("userId");
            long userId = ((Number) sessionUserId).longValue();

            result.put("status", "success");
            result.put("bookmark", communityService.deleteCommunity(userId, communityId));
        } catch (Exception e) {
            e.printStackTrace();
            result.put("status", "error");
            result.put("message", "게시글 삭제 중 오류가 발생했습니다.");
        }

        return result;
    }
}
