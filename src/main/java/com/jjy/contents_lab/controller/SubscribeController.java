package com.jjy.contents_lab.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import com.jjy.contents_lab.service.SubscribeService;

import org.springframework.ui.Model;

import jakarta.servlet.http.HttpSession;

@Controller
public class SubscribeController {
    private final SubscribeService subscribeService;

    public SubscribeController(SubscribeService subscribeService) {
        this.subscribeService = subscribeService;
    }

    @GetMapping("/subscribe")
    public String contentIdeaPage(Model model, HttpSession session) {

        Object sessionUserId = session.getAttribute("userId");
        if (sessionUserId == null) {
            return "redirect:/"; 
        }
        
        try {
            model.addAttribute("user_id", sessionUserId);
            model.addAttribute("subscribe_list", subscribeService.getSubscribeList());
            System.out.println("here!!");
            System.out.println(subscribeService.getSubscribeList());
        } catch (Exception e) {
            System.out.println("Error in sendLog: " + e.getMessage());
        }

        return "pay";
    }
}
