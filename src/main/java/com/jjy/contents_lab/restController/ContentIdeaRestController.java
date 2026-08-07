package com.jjy.contents_lab.restController;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jjy.contents_lab.dto.PromptTemplateDto;
import com.jjy.contents_lab.service.ContentIdeaService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/idea")
public class ContentIdeaRestController {
    private final ContentIdeaService contentIdeaService;

    public ContentIdeaRestController(ContentIdeaService contentIdeaService) {
        this.contentIdeaService = contentIdeaService;
    }

    @PostMapping("/save")
    public Map<String, Object> savePromptTemplate(HttpServletRequest request, 
                                                @ModelAttribute PromptTemplateDto promptTemplateDto) {
        Map<String, Object> result = new HashMap<>();   
        HttpSession session = request.getSession();

        if (session == null || session.getAttribute("userId") == null) {
            result.put("status", "error");
            result.put("message", "로그인이 필요합니다.");
            return result;
        }

        try {
            result.put("status", "success");
            result.put("prompt", contentIdeaService.savePromptTemplate(promptTemplateDto));
        } catch (Exception e) {
            e.printStackTrace();
            result.put("status", "error");
            result.put("message", "프롬프트 저장 중 오류가 발생했습니다.");
        }

        return result;
    }
}
