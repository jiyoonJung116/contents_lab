package com.jjy.contents_lab.restController;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jjy.contents_lab.dto.InquiriesDto;
import com.jjy.contents_lab.service.InquiriesService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/inquiries")
public class InquiriesRestController {
    private final InquiriesService inquiriesService;

    public InquiriesRestController(InquiriesService inquiriesService) {
        this.inquiriesService = inquiriesService;
    }

    @PostMapping("/save")
    public Map<String, Object> saveInquiries(HttpServletRequest request, 
                                            @ModelAttribute InquiriesDto inquiriesDto) {
        Map<String, Object> result = new HashMap<>();   
        HttpSession session = request.getSession();

        if (session == null || session.getAttribute("userId") == null) {
            result.put("status", "error");
            result.put("message", "로그인이 필요합니다.");
            return result;
        }

        try {
            Object sessionUserId = session.getAttribute("userId");
            long userId = ((Number) sessionUserId).longValue();
            inquiriesDto.setUserId(userId);

            result.put("status", "success");
            result.put("bookmark", inquiriesService.saveInquiries(inquiriesDto));
        } catch (Exception e) {
            e.printStackTrace();
            result.put("status", "error");
            result.put("message", "문의사항 저장 중 오류가 발생했습니다.");
        }

        return result;
    }

    @PostMapping("/reply")
    public Map<String, Object> saveAdminInquiries(HttpServletRequest request, 
                                                @RequestParam("id") long id,
                                                @RequestParam("adminReply") String adminReply) {
        Map<String, Object> result = new HashMap<>();   
        HttpSession session = request.getSession();

        Object adminYnObj = session.getAttribute("adminYn");
        String adminYn = (adminYnObj != null) ? adminYnObj.toString() : "";

        if (!"Y".equals(adminYn)) {
            result.put("status", "error");
            result.put("message", "관리자 권한이 없습니다.");
            return result;
        }

        try {
            result.put("status", "success");
            result.put("reply", inquiriesService.saveInquiryReply(id, adminReply));
        } catch (Exception e) {
            e.printStackTrace();
            result.put("status", "error");
            result.put("message", "문의사항 저장 중 오류가 발생했습니다.");
        }

        return result;
    }
}
