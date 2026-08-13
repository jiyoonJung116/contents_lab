package com.jjy.contents_lab.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.jjy.contents_lab.dto.CommunityDto;
import com.jjy.contents_lab.service.CommunityService;

import jakarta.servlet.http.HttpSession;

@Controller
public class CommunityController {
    private final CommunityService communityService;

    public CommunityController(CommunityService communityService) {
        this.communityService = communityService;
    }

    @GetMapping("/community")
    public String communityPage(Model model, 
                            HttpSession session,
                            @RequestParam(name = "page", defaultValue = "0") int page,
                            @RequestParam(name = "size", defaultValue = "10") int size) {

        Object sessionUserId = session.getAttribute("userId");
        if (sessionUserId == null) {
            return "redirect:/"; 
        }
        
        try {
            List<CommunityDto> communityList = communityService.getCommunityList(page, size);
            
            model.addAttribute("community_list", communityList);
        } catch (Exception e) {
            System.out.println("Error in sendLog: " + e.getMessage());
        }

        return "community";
    }
}
