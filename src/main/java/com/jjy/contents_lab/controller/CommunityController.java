package com.jjy.contents_lab.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.jjy.contents_lab.dto.CommentsDto;
import com.jjy.contents_lab.dto.CommunityDto;
import com.jjy.contents_lab.service.CommentsService;
import com.jjy.contents_lab.service.CommunityService;

import jakarta.servlet.http.HttpSession;

@Controller
public class CommunityController {
    private final CommunityService communityService;
    private final CommentsService commentsService;

    public CommunityController(CommunityService communityService, CommentsService commentsService) {
        this.communityService = communityService;
        this.commentsService = commentsService;
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

    @GetMapping("/community/post")
    public String communityPostPage(@RequestParam(name = "id", required = false) Long id,
                                    Model model, 
                                    HttpSession session) {

        if (session == null || session.getAttribute("userId") == null) {
            return "redirect:/";
        }

        if (id != null && id > 0) {
            CommunityDto post = communityService.getCommunityById(id);
            model.addAttribute("post", post != null ? post : new CommunityDto());
        } else {
            model.addAttribute("post", new CommunityDto());
        }

        return "community_post";
    }

    @GetMapping("/detail")
    public String communityPage(Model model, 
                                HttpSession session,
                                @PathVariable("id") Long id) {

        Object sessionUserId = session.getAttribute("userId");
        if (sessionUserId == null) {
            return "redirect:/"; 
        }
        
        try {
            CommunityDto community = communityService.getCommunityById(id);
            List<CommentsDto> commentsList = commentsService.getCommentsList(id);
            
            model.addAttribute("community", community);
            model.addAttribute("commentsList", commentsList);
        } catch (Exception e) {
            System.out.println("Error in sendLog: " + e.getMessage());
        }

        return "community_detail";
    }
}
