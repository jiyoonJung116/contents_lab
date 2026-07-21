package com.jjy.contents_lab.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.jjy.contents_lab.dto.UserDto;

import jakarta.servlet.http.HttpSession;
import tools.jackson.databind.ObjectMapper;

@Controller
public class IndexController {

    @GetMapping("/")
    public String mainPage(Model model, HttpSession session, @SessionAttribute(name = "userId", required = false) Long userId) {
        String loginInfoJson = (String) session.getAttribute("login_info");
        if (userId == null) {
            model.addAttribute("isLogin", false);
            return "main";
        }
        
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            UserDto user = objectMapper.readValue(loginInfoJson, UserDto.class);
            
            model.addAttribute("isLogin", true);
            model.addAttribute("userName", user.getUserName());
        } catch (Exception e) {
            model.addAttribute("isLogin", false);
            return "main";
        }
        
        return "main";
    }
}
