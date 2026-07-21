package com.jjy.contents_lab.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.jjy.contents_lab.dto.ChatRoomDto;
import com.jjy.contents_lab.service.CharacterService;

import jakarta.servlet.http.HttpSession;

@Controller
public class AiController {
    private final CharacterService characterService;

    public AiController(CharacterService characterService) {
        this.characterService = characterService;
    }

    @GetMapping("/ai")
    public String aiPage() {
        return "ai";
    }

    @GetMapping("/chatbot")
    public String chatBotPage(Model model, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            return "redirect:/user/login"; 
        }

        List<ChatRoomDto> roomList = characterService.getRoomList(userId);
        model.addAttribute("roomList", roomList);

        return "chatbot";
    }
}
