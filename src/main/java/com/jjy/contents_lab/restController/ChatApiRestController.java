package com.jjy.contents_lab.restController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.*;
import com.jjy.contents_lab.mapper.CharacterMapper;
import com.jjy.contents_lab.service.CharacterService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatApiRestController {
    private final CharacterMapper characterMapper;
    private final CharacterService characterService;

    @GetMapping("/room/{roomId}")
    public List<Map<String, Object>> getChatHistory(@PathVariable("roomId") String roomId) {
        return characterMapper.selectMessagesByRoomId(roomId);
    }

    // 내 캐릭터 목록 조회 API
    @GetMapping("/characters")
    public Map<String, Object> getMyCharacters(HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            result.put("status", "success");
            result.put("character_list", characterService.getImagesByType(Long.parseLong(session.getAttribute("userId").toString()), "character"));
        } catch (Exception e) {
            result.put("status", "error");
            result.put("message", e.getMessage());
        }

        return result;
    }

    // 생성된 대본 목록 조회 API
    @GetMapping("/scripts")
    public Map<String, Object> getMyScripts(HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            result.put("status", "success");
            result.put("script_info", characterService.getSavedScripts(Long.parseLong(session.getAttribute("userId").toString())));
        } catch (Exception e) {
            result.put("status", "error");
            result.put("message", e.getMessage());
        }

        return result;
    }

    @PostMapping("/story/generate")
    public Map<String, Object> generateStory(@RequestBody Map<String, Object> payload) {
        Map<String, Object> result = new HashMap<>();
        try {
            String roomId = (String) payload.get("roomId");
            String userInput = (String) payload.get("userInput");
            int cutCount = 4;
            if (payload.get("cutCount") != null) {
                cutCount = Integer.parseInt(payload.get("cutCount").toString());
            }

            String botReply = characterService.processStory(roomId, userInput, cutCount);

            result.put("success", true);
            result.put("bot_reply", botReply);
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
        }

        return result;
    }

    @PostMapping("/toon/generate")
    public Map<String, Object> generateToon(@RequestBody Map<String, Object> payload) {
        Map<String, Object> result = new HashMap<>();

        try {
            String roomId = (String) payload.get("roomId");
            String scriptId = payload.get("scriptId") != null ? payload.get("scriptId").toString() : "";
            List<String> characterIds = (List<String>) payload.get("characterIds");
            List<String> characterPrompts = (List<String>) payload.get("characterPrompts");
            
            String additionalPrompt = (String) payload.get("additionalPrompt");
            Integer cutIndex = payload.get("cutIndex") != null ? Integer.parseInt(payload.get("cutIndex").toString()) : 1;
            String content = (String) payload.get("content");
            String sceneDescription = (String) payload.get("sceneDescription");

            Map<String, Object> toonParams = new HashMap<>();
            toonParams.put("roomId", roomId);
            toonParams.put("scriptId", scriptId);
            toonParams.put("characterIds", characterIds);      
            toonParams.put("characterPrompts", characterPrompts);
            toonParams.put("additionalPrompt", additionalPrompt);
            toonParams.put("cutIndex", cutIndex);
            toonParams.put("content", content);
            toonParams.put("sceneDescription", sceneDescription);


            result.put("success", true);
            result.put("content", characterService.processToonBuilding(toonParams));
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
        }

        return result;
    }

    
    // 아이디어 10개 생성
    @PostMapping("/idea/generate")
    public Map<String, Object> generateIdeas(@RequestBody Map<String, Object> payload) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            String roomId = payload.get("roomId") != null ? payload.get("roomId").toString() : null;
            String userInput = (String) payload.get("userInput");

            if (userInput == null || userInput.trim().isEmpty()) {
                result.put("success", false);
                result.put("error", "주제 또는 키워드를 입력해주세요.");
                return result; // ResponseEntity.badRequest() 대신 바로 result 반환
            }

            String aiResponse = characterService.processIdea(roomId, userInput);

            result.put("success", true);
            result.put("ai_response", aiResponse);
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "서버 오류가 발생했습니다: " + e.getMessage());
        }
        
        return result; 
    }
}
