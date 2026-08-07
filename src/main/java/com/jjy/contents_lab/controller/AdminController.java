package com.jjy.contents_lab.controller;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jjy.contents_lab.dto.ChatRoomDto;
import com.jjy.contents_lab.service.CharacterService;
import com.jjy.contents_lab.service.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final UserService userService;
    private final CharacterService characterService;

    public AdminController(UserService userService, CharacterService characterService) {
        this.userService = userService;
        this.characterService = characterService;
    }

    @GetMapping("list")
    public String pageUserList(Model model, 
                                HttpSession session,
                                @RequestParam(name = "page", defaultValue = "1") int page,
                                @RequestParam(name = "size", defaultValue = "10") int size) {
        Object adminYnObj = session.getAttribute("adminYn");
        String adminYn = (adminYnObj != null) ? adminYnObj.toString() : "";
        if (!"Y".equals(adminYn)) {
            return "redirect:/"; 
        }

        try {
            int totalCount = userService.getUserCount();

            int totalPages = (int) Math.ceil((double) totalCount / size);
            if (totalPages == 0) {
                totalPages = 1;
            }
            int pageBlock = 5;
            int startPage = ((page - 1) / pageBlock) * pageBlock + 1;
            int endPage = Math.min(startPage + pageBlock - 1, totalPages);

            model.addAttribute("user_list", userService.getUserList(page, size));
            model.addAttribute("totalCount", totalCount);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("startPage", startPage);
            model.addAttribute("endPage", endPage);
            model.addAttribute("size", size);
        } catch (Exception e) {
            System.out.println("Error in sendLog: " + e.getMessage());
        }

        return "admin_dashboard";
    }

    @GetMapping("/chat")
    public String chatRoomListPage(@RequestParam(value = "page", defaultValue = "1") int page,
                                    @RequestParam(value = "size", defaultValue = "10") int size,
                                    Model model) {

        List<ChatRoomDto> chatRooms = characterService.getChatRoomList(page, size);
        int totalCount = characterService.getChatRoomCount();
        int totalPages = (int) Math.ceil((double) totalCount / size);

        model.addAttribute("chatRooms", chatRooms);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages == 0 ? 1 : totalPages);

        return "admin_result";
    }

    @GetMapping("/chat/messages")
    @ResponseBody
    public List<Map<String, Object>> getRoomMessages(@RequestParam("roomId") String roomId) {
        return characterService.selectMessagesByRoomId(roomId);
    }
}
