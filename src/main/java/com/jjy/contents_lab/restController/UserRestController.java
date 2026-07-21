package com.jjy.contents_lab.restController;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jjy.contents_lab.dto.UserDto;
import com.jjy.contents_lab.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import tools.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/api/user")
public class UserRestController {
    private final UserService userService;    

    public UserRestController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/join")
    public Map<String, Object> joinUser(@ModelAttribute UserDto userDto) {
        Map<String, Object> result = new HashMap<>();

        if (userDto.getEmail() == null || userDto.getEmail().isEmpty()) {
            result.put("status", "error");
            result.put("message", "이메일은 필수입니다.");
            return result;
        }

        if (userDto.getPassword() == null || userDto.getPassword().isEmpty()) {
            result.put("status", "error");
            result.put("message", "비밀번호는 필수입니다.");
            return result;
        }

        if (userDto.getUserName() == null || userDto.getUserName().isEmpty()) {
            result.put("status", "error");
            result.put("message", "사용자 이름은 필수입니다.");
            return result;
        }

        if (userDto.getEmail() == null || userDto.getEmail().isEmpty()) {
            result.put("status", "error");
            result.put("message", "이메일은 필수입니다.");
            return result;
        }

        try {
            long userId = userService.joinUser(userDto);
            result.put("status", "success");
            result.put("userId", userId);
        } catch (Exception e) {
            result.put("status", "error");
            result.put("message", "회원 가입 중 오류가 발생했습니다.");
        }

        return result;
    }

    @PostMapping("/login")
    public Map<String, Object> loginUser(HttpServletRequest request,
                                        @ModelAttribute UserDto userDto) { 
        Map<String, Object> result = new HashMap<>();
        HttpSession session = request.getSession();

        if (userDto == null || userDto.getEmail() == null || userDto.getEmail().isEmpty() || userDto.getPassword() == null || userDto.getPassword().isEmpty()) {
            result.put("status", "error");
            result.put("message", "잘못된 요청입니다.");

            return result;
        }

        try {
            long userId = userService.loginUser(userDto.getEmail(), userDto.getPassword());
            result.put("status", userId != 0 ? "success" : "error");
            
            if (userId == 0) {
                result.put("error", "아이디 또는 비밀번호가 올바르지 않습니다.");
            } else {
                UserDto user = userService.getUserById(userId);
                ObjectMapper objectMapper = new ObjectMapper();
                String jsonData = objectMapper.writeValueAsString(user);

                session.setAttribute("login_info", jsonData);
                session.setAttribute("userId", userId);

                result.put("login_info", user);
            }
        } catch (Exception e) {
            result.put("status", "error");
            System.err.println("로그인 중 오류: " + e.getMessage());
            result.put("message", "로그인 중 오류가 발생했습니다.");
        }

        return result;
    }

    @PostMapping("/info/update")
    public Map<String, Object> saveUser(HttpServletRequest request,
                                        @ModelAttribute UserDto userDto) {
        Map<String, Object> result = new HashMap<>();
        HttpSession session = request.getSession();

        if (session == null || session.getAttribute("userId") == null) {
            result.put("status", "error");
            result.put("message", "로그인이 필요합니다.");
            return result;
        }

        try {
            Long userId = Long.parseLong(session.getAttribute("userId").toString());
            userDto.setId(userId); 
            userService.updateUser(userDto); 
            
            UserDto updatedUser = userService.getUserById(userId); 

            // 세션에 업데이트된 사용자 정보 저장
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonStr = objectMapper.writeValueAsString(updatedUser);
            session.setAttribute("login_info", jsonStr);

            result.put("status", "success");
            result.put("message", "회원 정보가 성공적으로 수정되었습니다.");
        } catch (Exception e) {
            result.put("status", "error");
            System.err.println("회원 정보 수정 중 오류: " + e.getMessage());
            result.put("message", "회원 정보 수정 중 오류가 발생했습니다.");
        }

        return result;
    }

    @PostMapping("/password/update")
    public Map<String, Object> changePassword(HttpServletRequest request,
                                              @RequestParam("currentPassword") String currentPassword,
                                              @RequestParam("newPassword") String newPassword) {
        Map<String, Object> result = new HashMap<>();
        HttpSession session = request.getSession();

        if (session == null || session.getAttribute("userId") == null) {
            result.put("status", "error");
            result.put("message", "로그인이 필요합니다.");
            return result;
        }

        try {
            Object sessionUserId = session.getAttribute("userId");
            long userId = ((Number) sessionUserId).longValue();
            boolean isChanged = userService.changePassword(userId, currentPassword, newPassword);
            
            if (isChanged) {
                result.put("status", "success");
                result.put("message", "비밀번호가 안전하게 변경되었습니다.");
            } else {
                result.put("status", "error");
                result.put("message", "현재 비밀번호가 일치하지 않습니다.");
            }
        } catch (Exception e) {
            result.put("status", "error");
            System.err.println("비밀번호 변경 중 오류: " + e.getMessage());
            result.put("message", "비밀번호 변경 중 오류가 발생했습니다.");
        }

        return result;
    }
}
