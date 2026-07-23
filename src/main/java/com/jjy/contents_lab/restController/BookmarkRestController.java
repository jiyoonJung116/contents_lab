package com.jjy.contents_lab.restController;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jjy.contents_lab.dto.UserBookmarkDto;
import com.jjy.contents_lab.service.BookmarkService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/bookmark")
public class BookmarkRestController {
    private final BookmarkService bookmarkService;

    public BookmarkRestController(BookmarkService bookmarkService) {
        this.bookmarkService = bookmarkService;
    }

    @PostMapping("/save")
    public Map<String, Object> saveBookmark(HttpServletRequest request, 
                                            @ModelAttribute UserBookmarkDto userBookmarkDto) {
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
            userBookmarkDto.setUserId(userId);

            result.put("status", "success");
            result.put("bookmark", bookmarkService.saveBookmark(userBookmarkDto));
        } catch (Exception e) {
            e.printStackTrace();
            result.put("status", "error");
            result.put("message", "북마크 저장 중 오류가 발생했습니다.");
        }

        return result;
    }

    @PostMapping("/delete")
    public Map<String, Object> deleteBookmark(HttpServletRequest request,
                                            @RequestParam("bookmarkId") long bookmarkId) {
        Map<String, Object> result = new HashMap<>();
        HttpSession session = request.getSession();

        try {
            Object sessionUserId = session.getAttribute("userId");
            long userId = ((Number) sessionUserId).longValue();

            result.put("status", "success");
            result.put("bookmark", bookmarkService.deleteBookmark(userId, bookmarkId));
        } catch (Exception e) {
            e.printStackTrace();
            result.put("status", "error");
            result.put("message", "북마크 삭제 중 오류가 발생했습니다.");
        }

        return result;
    }
}
