package com.jjy.contents_lab.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;

@Controller
public class InquiriesController {
    @GetMapping("/faq_question")
    public String faqQuestionPage(HttpSession session) {
        Object sessionUserId = session.getAttribute("userId");
        if (sessionUserId == null) {
            return "redirect:/";
        }
        
        return "faq_question"; 
    }
}
