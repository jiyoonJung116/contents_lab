package com.jjy.contents_lab.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.jjy.contents_lab.dto.UserDto;
import com.jjy.contents_lab.service.UserService;

import jakarta.servlet.http.HttpSession;


@Controller
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }
    
    @GetMapping("join")
    public String pageJoin() {
        return "join";
    }

    @GetMapping("login")
    public String pageLogin() {
        return "login";
    }

    @GetMapping("profile")
    public String userInfo(HttpSession session,  Model model) {
        String loginInfoJson = (String) session.getAttribute("login_info");

        if (loginInfoJson == null) {
            return "redirect:/user/login";
        }

        try {
            Long userId = Long.parseLong(session.getAttribute("userId").toString());
            UserDto user = userService.getUserById(userId);
            model.addAttribute("user", user);
        } catch (Exception e) {
            return "redirect:/user/login";
        }
        
        return "my_profile";
    }
    
    @GetMapping("logout")
    public String pageLogout(HttpSession session) {
        session.invalidate();

        return "main";
    }
}
