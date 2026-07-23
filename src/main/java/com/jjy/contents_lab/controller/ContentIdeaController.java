package com.jjy.contents_lab.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.jjy.contents_lab.dto.PromptTemplateDto;
import com.jjy.contents_lab.dto.UserBookmarkDto;
import com.jjy.contents_lab.service.BookmarkService;
import com.jjy.contents_lab.service.ContentIdeaService;

import org.springframework.ui.Model;

import jakarta.servlet.http.HttpSession;

@Controller
public class ContentIdeaController {
    private final ContentIdeaService contentIdeaService;
    private final BookmarkService bookmarkService;

    public ContentIdeaController(ContentIdeaService contentIdeaService, BookmarkService bookmarkService) {
        this.contentIdeaService = contentIdeaService;
        this.bookmarkService = bookmarkService;
    }

    @GetMapping("/idea")
    public String contentIdeaPage(Model model, 
                            HttpSession session,
                            @RequestParam(name = "targetSize", defaultValue = "ALL") String targetSize,
                            @RequestParam(name = "page", defaultValue = "0") int page,
                            @RequestParam(name = "size", defaultValue = "10") int size) {

        Object sessionUserId = session.getAttribute("userId");
        if (sessionUserId == null) {
            return "redirect:/"; 
        }
        
        try {
            Long userId = Long.parseLong(session.getAttribute("userId").toString());

            List<PromptTemplateDto> contentIdeaList = contentIdeaService.getPromptList(targetSize, page, size);
            List<UserBookmarkDto> bookmarkList = bookmarkService.getBookmarkList(userId, page, size);
            
            model.addAttribute("content_idea_list", contentIdeaList);
            model.addAttribute("bookmark_list", bookmarkList);
        } catch (Exception e) {
            System.out.println("Error in sendLog: " + e.getMessage());
        }

        return "content_idea";
    }
}
