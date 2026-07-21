package com.jjy.contents_lab.restController;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.jjy.contents_lab.service.CharacterService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AiRestController {

    @Value("${huggingface.token}")
    private String hfToken;

    private final CharacterService characterService;

    @PostMapping("/character/create")
    public Map<String, Object> createMascotCharacter(HttpSession session, @RequestBody Map<String, Object> request) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            request.put("userId", Long.parseLong(session.getAttribute("userId").toString()));

            result.put("status", "success");
            result.put("pipeline_result", characterService.processCharacterPipeline(request));
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
        }

        return result;
    }

    
    // 시나리오 , 카드뉴스 대본
    @PostMapping("/generate")
    public Map<String, String> generate(@RequestBody Map<String, String> request) {
        String prompt = request.get("prompt");
        String roomId = request.getOrDefault("roomId", "toon_room"); 

        RestTemplate restTemplate = new RestTemplate();

        String systemInstruction = "";
        if ("toon_room".equals(roomId)) {
            systemInstruction = "너는 인스타툰 시나리오 작가야. 다음 주제로 4~5컷 분량의 대본을 짜줘.\n"
                              + "반드시 아래 포맷을 엄격하게 지켜서 작성해야 해.\n\n"
                              + "1. 컷:\n"
                              + "* 제목: [이 컷의 제목]\n"
                              + "* 이미지: [그림 생성에 쓸 구체적인 묘사]\n"
                              + "* 본문: [캐릭터 대사나 나레이션]\n\n"
                              + "2. 컷:\n"
                              + "... 이런 식으로 작성하고 마지막엔 CTA와 #태그를 붙여줘.\n\n"
                              + "주제: ";
        } else {
            systemInstruction = "너는 카드뉴스 기획자야. 다음 주제로 4~5페이지 분량의 카드뉴스를 기획해줘.\n"
                              + "반전히 아래 포맷을 엄격하게 지켜서 작성해야 해.\n\n"
                              + "1. 카드:\n"
                              + "* 제목: [이 페이지의 제목]\n"
                              + "* 이미지: [그림 생성에 쓸 인포그래픽 묘사]\n"
                              + "* 본문: [핵심 정보 설명]\n\n"
                              + "주제: ";
        }

        Map<String, Object> body = new HashMap<>();
        body.put("model", "gemma3:4b");
        body.put("prompt", systemInstruction + prompt); 
        body.put("stream", false);

        String url = "http://localhost:11434/api/generate";

        Map response = restTemplate.postForObject(url, body, Map.class);
        String result = response.get("response").toString();

        return Map.of("content", result);
    }

    
    // 이미지 생성용
    @PostMapping("/image")
    public String generateImage(@RequestBody Map<String, String> body) {
        String prompt = body.get("prompt");

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(hfToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(java.util.List.of(MediaType.IMAGE_PNG));

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("inputs", prompt);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<byte[]> response = restTemplate.exchange(
                "https://router.huggingface.co/hf-inference/models/black-forest-labs/FLUX.1-schnell",
                HttpMethod.POST,
                entity,
                byte[].class
        );

        return Base64.getEncoder().encodeToString(response.getBody());
    }
}