package com.jjy.contents_lab.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.jjy.contents_lab.dto.InquiriesDto;
import com.jjy.contents_lab.service.FaqService;
import com.jjy.contents_lab.service.InquiriesService;

import jakarta.servlet.http.HttpSession;

@Controller
public class FaqController {
    private final FaqService faqService;
    private final InquiriesService inquiriesService;

    public FaqController(FaqService faqService, InquiriesService inquiriesService) {
        this.faqService = faqService;
        this.inquiriesService = inquiriesService;
    }

    @GetMapping("/faq")
    public String contentIdeaPage(Model model, 
                                HttpSession session,
                                @RequestParam(name = "content", defaultValue = "") String content,
                                @RequestParam(name = "status", defaultValue = "ALL") String status,
                                @RequestParam(name = "page", defaultValue = "0") int page,
                                @RequestParam(name = "size", defaultValue = "10") int size) {

        Object sessionUserId = session.getAttribute("userId");
        if (sessionUserId == null) {
            return "redirect:/"; 
        }
        
        try {
            Long userId = Long.parseLong(sessionUserId.toString());
            List<InquiriesDto> allInquiries = inquiriesService.getInquiriesList(userId, status, page, size);
            
            int totalCount = allInquiries.size();
            int totalPages = (int) Math.ceil((double) totalCount / size);
            if (totalPages == 0) {
                totalPages = 1;
            }

            int fromIndex = page * size;
            int toIndex = Math.min(fromIndex + size, totalCount);
            
            List<InquiriesDto> pagedList = new ArrayList<>();
            if (fromIndex < totalCount) {
                pagedList = allInquiries.subList(fromIndex, toIndex);
            }

            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("status", status);
            model.addAttribute("inquiries_list", pagedList);
            model.addAttribute("faq_list", faqService.getFaqList(content, page, size));
        } catch (Exception e) {
            System.out.println("Error in faq page: " + e.getMessage());
        }

        return "faq";
    }

    @GetMapping("/faq/detail")
    public String detailPage(Model model, 
                            HttpSession session,
                            @RequestParam("id") long id) {
        
        if (session == null || session.getAttribute("userId") == null) {
            return "redirect:/login";
        }

        InquiriesDto inquiry = inquiriesService.getInquiryById(id);
        long userId = ((Number) session.getAttribute("userId")).longValue();
        String adminYn = session.getAttribute("adminYn") != null ? session.getAttribute("adminYn").toString() : "N";

        if (inquiry.getUserId() != userId && !"Y".equals(adminYn)) {
            return "redirect:/faq";
        }

        model.addAttribute("inquiry", inquiry);

        return "faq_detail";
    }
}
