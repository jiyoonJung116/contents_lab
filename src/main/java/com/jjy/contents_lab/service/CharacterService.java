package com.jjy.contents_lab.service;

import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jjy.contents_lab.dto.CharacterAiResponse;
import com.jjy.contents_lab.dto.ChatRoomDto;
import com.jjy.contents_lab.mapper.CharacterMapper;
import com.jjy.contents_lab.util.ToonCompositeUtil;

@Service
public class CharacterService {

    private final CharacterMapper characterMapper;
    private final ObjectMapper objectMapper; 
    private final RestTemplate restTemplate;

    public CharacterService(CharacterMapper characterMapper) {
        this.characterMapper = characterMapper;
        this.objectMapper = new ObjectMapper(); // 빈 컨테이너 조회 없이 즉시 직접 인스턴스 생성
        this.restTemplate = new RestTemplate();
    }

    @Value("${huggingface.token}")
    private String hfToken;

    // 고정 손그림/낙서 스타일 프롬프트
    private static final String BASE_STYLE = 
        "Cute Korean Instagram mascot, tiny dot eyes, pink blush cheeks, thin dark brown outline, hand drawn, amateur doodle style, slightly uneven lines, casual Korean webtoon style, cute diary doodle, simple facial features, slightly awkward proportions, soft pastel colors, minimal shading, warm and cozy feeling, homemade character design, illustrated by hand";

    // 캐릭터 일관성 유지
    private static final String TAIL_STYLE = 
        "same character, consistent mascot design, recurring character, brand mascot, white background";

    public List<ChatRoomDto> getRoomList(Long userId) {
        return characterMapper.getRoomList(userId);
    }

    // 내 캐릭터 목록 조회
    public List<Map<String, Object>> getImagesByType(Long userId, String imageType) {
        return characterMapper.getImagesByType(userId, imageType);
    }

    // 생성된 대본 목록 조회
    public List<Map<String, Object>> getSavedScripts(Long userId) {
        return characterMapper.getSavedScripts(userId);
    }

    // 채팅 내역 조회
    public List<Map<String, Object>> selectMessagesByRoomId(String roomId) {
        return characterMapper.selectMessagesByRoomId(roomId);
    }

    // AI 분석, 이미지 생성, MyBatis DB 저장을 한 번에 처리
    @Transactional
    public Map<String, Object> processCharacterPipeline(Map<String, Object> request) throws Exception {
        Object roomIdObj = request.get("roomId");
        Long roomId = 1L;
        if (roomIdObj instanceof Number) {
            roomId = ((Number) roomIdObj).longValue();
        } else if (roomIdObj instanceof String) {
            try {
                roomId = Long.parseLong((String) roomIdObj);
            } catch (NumberFormatException e) {
                roomId = 1L;
            }
        }
        
        String userInput = request.get("userInput").toString();

        Map<String, Object> userMsgParam = new HashMap<>();
        userMsgParam.put("roomId", roomId);
        userMsgParam.put("senderType", "USER");
        userMsgParam.put("content", userInput);
        
        characterMapper.insertMessage(userMsgParam);
        
        Long userMessageId = Long.parseLong(userMsgParam.get("id").toString());
        CharacterAiResponse aiAnalysis = analyzeUserInputWithAi(userInput);

        String finalPrompt = String.format("%s, %s mascot, %s theme, %s color palette, %s",
                BASE_STYLE,
                aiAnalysis.getEnglishSpecies(),
                aiAnalysis.getEnglishConcept(),
                aiAnalysis.getEnglishColor(),
                TAIL_STYLE
        );

        String base64Image = generateFluxImage(finalPrompt);
        String imageUrl = saveImageToFile(base64Image);

        Map<String, Object> imgParam = new HashMap<>();
        imgParam.put("messageId", userMessageId);
        imgParam.put("userId", request.get("userId"));
        imgParam.put("imageUrl", imageUrl);
        imgParam.put("thumbnailUrl", imageUrl);
        imgParam.put("actualPrompt", finalPrompt);
        imgParam.put("imageType", "character");
        characterMapper.insertGeneratedImage(imgParam);

        String botNoticeText = String.format("요청하신 속성을 분석해 캐릭터를 만들었어요.\n\n" +
                "■ 이름: %s\n■ 종족: %s\n■ 컨셉: %s\n■ 색상: %s\n■ 성격: %s", 
                aiAnalysis.getName(), aiAnalysis.getSpecies(), aiAnalysis.getConcept(), aiAnalysis.getColor(), aiAnalysis.getPersonality());
        
        Map<String, Object> botMsgParam = new HashMap<>();
        botMsgParam.put("roomId", roomId);
        botMsgParam.put("senderType", "BOT");
        botMsgParam.put("content", botNoticeText);
        characterMapper.insertMessage(botMsgParam);

        return Map.of(
            "success", true,
            "characterName", aiAnalysis.getName(),
            "botReply", botNoticeText,
            "imageUrl", imageUrl,
            "actualPrompt", finalPrompt
        );
    }

    public CharacterAiResponse analyzeAndBuildPrompt(String userInput) {
        try {
            return analyzeUserInputWithAi(userInput);
        } catch (Exception e) {
            throw new RuntimeException("AI 속성 및 이름 분석 실패: " + e.getMessage(), e);
        }
    }

    public String generateFluxImage(String prompt) {
        try {
            String encodedPrompt = URLEncoder.encode(prompt, StandardCharsets.UTF_8);
            String url = "https://image.pollinations.ai/p/" + encodedPrompt + "?width=512&height=512&enhance=false";

            RestTemplate simpleRestTemplate = new RestTemplate();
            ResponseEntity<byte[]> response = simpleRestTemplate.getForEntity(url, byte[].class);

            return Base64.getEncoder().encodeToString(response.getBody());
        } catch (Exception e) {
            return ""; 
        }
    }

    // 캐릭터 이미지를 기반으로 표정/행동만 바꾸는 메서드
    public String generateFluxImage(String prompt, String base64OriginalImage) {
        try {
            String encodedPrompt = java.net.URLEncoder.encode(prompt, java.nio.charset.StandardCharsets.UTF_8);
            String url = "https://image.pollinations.ai/p/" + encodedPrompt + "?width=512&height=512&enhance=false";
            org.springframework.web.client.RestTemplate simpleRestTemplate = new org.springframework.web.client.RestTemplate();
            ResponseEntity<byte[]> response = simpleRestTemplate.getForEntity(url, byte[].class);

            return Base64.getEncoder().encodeToString(response.getBody());
        } catch (Exception e) {
            return "";
        }
    }

    public String buildFinalPrompt(CharacterAiResponse aiData) {
        return String.format("%s, %s mascot, %s theme, %s color palette, %s",
                BASE_STYLE,
                aiData.getEnglishSpecies(),
                aiData.getEnglishConcept(),
                aiData.getEnglishColor(),
                TAIL_STYLE
        );
    }

    private CharacterAiResponse analyzeUserInputWithAi(String userInput) throws Exception {
        String url = "http://localhost:11434/api/generate";

        String systemInstruction = "너는 자연어 문장을 분석해서 인스타툰 캐릭터 속성을 추출하고 어울리는 귀여운 한국어 이름을 지어주는 전문가야. "
                + "사용자의 입력을 분석해서 반드시 아래 지정된 JSON 포맷으로만 답변해. 부연 설명이나 마크다운 태그(`)는 절대 포함하지마.\n\n"
                + "응답 형식 예시:\n"
                + "{\n"
                + "  \"name\": \"돈토리\",\n"
                + "  \"species\": \"붕어빵\",\n"
                + "  \"concept\": \"재테크\",\n"
                + "  \"color\": \"주황색\",\n"
                + "  \"personality\": \"똑똑함\",\n"
                + "  \"englishSpecies\": \"fish-shaped pastry\",\n"
                + "  \"englishConcept\": \"investment\",\n"
                + "  \"englishColor\": \"orange\"\n"
                + "}\n\n"
                + "사용자 입력: " + userInput;

        Map<String, Object> body = new HashMap<>();
        body.put("model", "gemma3:4b");
        body.put("prompt", systemInstruction);
        body.put("stream", false);
        body.put("format", "json"); 

        Map<String, Object> response = restTemplate.postForObject(url, body, Map.class);
        String jsonText = response.get("response").toString();

        return objectMapper.readValue(jsonText, CharacterAiResponse.class);
    }

    private String saveImageToFile(String base64Image) throws Exception {
        byte[] imageBytes = Base64.getDecoder().decode(base64Image);

        String fileName = UUID.randomUUID() + ".png";

        Path uploadDir = Paths.get("C:/upload");

        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }

        Path filePath = uploadDir.resolve(fileName);

        Files.write(filePath, imageBytes);

        return "/uploads/" + fileName;
    }

    @Transactional 
    public String processStory(String roomId, String userInput, int cutCount) {
        Map<String, Object> roomInfo = characterMapper.selectChatRoomByRoomId(roomId);
        
        String roomName = (roomInfo != null) ? (String) roomInfo.get("name") : "스토리메이커";
        String description = (roomInfo != null) ? (String) roomInfo.get("description") : "콘텐츠 카피 및 시나리오 제작";
        
        Map<String, Object> userMsgParam = new HashMap<>();
        userMsgParam.put("roomId", roomId);
        userMsgParam.put("senderType", "USER");
        userMsgParam.put("content", userInput);
        characterMapper.insertMessage(userMsgParam);
        
        String systemInstruction = String.format(
            "너는 사용자의 요구에 실시간으로 대응하는 고도화된 인스타툰 전문 창작 AI 엔진이야.\n" +
            "현재 작업 중인 크리에이티브 룸 이름: [%s]\n" +
            "이 룸의 핵심 목적/콘셉트: [%s]\n\n" +
            
            "사용자가 요구하는 주제에 맞추어, 유저가 마음에 드는 스타일을 고를 수 있도록 [1안: 코믹/유쾌], [2안: 직관적/정보전달], [3안: 일상/공감] 총 3가지 버전의 만화 시나리오를 창작해줘.\n\n" +
            
            "[중요 규칙]:\n" +
            "- 각 버전(안)은 반드시 정확히 총 [%d개의 컷](cutIndex 1~%d)으로 구성되어야 해. 절대로 이 개수를 넘기거나 줄이지 마.\n" +
            "- 'sceneDescription'에는 캐릭터의 이름이나 구체적인 외모(예: 분홍머리, 햄스터 등)를 직접 쓰지 마. 오직 캐릭터가 처한 '상황, 행동, 표정, 배경 소품'만 한국어로 상세히 묘사해줘.\n" +
            "- 결과물은 프론트엔드 파싱을 위해 반드시 아래의 JSON 배열 형식으로만 출력하고, 다른 설명이나 수다, 마크다운 기호(```json 등)는 앞뒤에 절대 포함하지 마.\n\n" +
            
            "반드시 아래 JSON 배열 포맷 문자열만 출력하십시오 (예시는 %d컷 기준):\n" +
            "[\n" +
            "  {\n" +
            "    \"versionName\": \"1안 (코믹/유쾌 버전)\",\n" +
            "    \"cuts\": [\n" +
            "      {\"cutIndex\": 1, \"content\": \"(이 컷의 대사 또는 나레이션)\", \"sceneDescription\": \"스마트폰 화면을 보며 머리를 쥐어짜며 당황해하는 모습...\"},\n" +
            "      {\"cutIndex\": %d, \"content\": \"...\", \"sceneDescription\": \"...\"}\n" +
            "    ]\n" +
            "  },\n" +
            "  {\n" +
            "    \"versionName\": \"2안 (직관적/정보전달 버전)\",\n" +
            "    \"cuts\": [ ... ]\n" +
            "  },\n" +
            "  {\n" +
            "    \"versionName\": \"3안 (일상/공감 버전)\",\n" +
            "    \"cuts\": [ ... ]\n" +
            "  }\n" +
            "]",
            roomName, description, cutCount, cutCount, cutCount, cutCount
        );

        String botReply = callExternalGeminiApi(systemInstruction, userInput);

        Map<String, Object> botMsgParam = new HashMap<>();
        botMsgParam.put("roomId", roomId);
        botMsgParam.put("senderType", "BOT");
        botMsgParam.put("content", botReply.trim()); 
        characterMapper.insertMessage(botMsgParam);

        return botReply.trim();
    }

    private String callExternalGeminiApi(String systemPrompt, String userPrompt) {
        String url = "http://localhost:11434/api/generate";
        String combinedPrompt = String.format("%s\n\n[사용자 창작 명령어]: %s", systemPrompt, userPrompt);

        Map<String, Object> body = new HashMap<>();
        body.put("model", "gemma3:4b"); 
        body.put("prompt", combinedPrompt);
        body.put("stream", false);

        try {
            Map<String, Object> response = restTemplate.postForObject(url, body, Map.class);
            
            if (response != null && response.containsKey("response")) {
                return response.get("response").toString();
            }
            return "Ollama 시스템 연동 응답을 파싱하는 과정에서 오류가 발생했습니다.";
            
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    @Transactional
    public String processToonBuilding(Map<String, Object> params) {
        // 1. 프론트엔드에서 넘어온 캐릭터 ID 리스트 및 필수 변수 추출
        List<String> characterIds = (List<String>) params.get("characterIds");
        String targetContent = (String) params.get("content");
        String targetSceneDescription = (String) params.get("sceneDescription");
        String targetCutIndex = String.valueOf(params.get("cutIndex"));
        String roomId = String.valueOf(params.get("roomId"));
        String characterBase64Image = "";

        if (characterIds != null && !characterIds.isEmpty()) {
            String selectedCharId = characterIds.get(0);
            Map<String, Object> characterData = characterMapper.selectCharacterById(selectedCharId);
            
            if (characterData != null) {
                characterBase64Image = (String) characterData.get("image_base64");
            } 
        } 

        String bgOnlyPrompt = targetSceneDescription + ", 2D cute webtoon background style, rich details, vibrant colors, " + System.currentTimeMillis();
        String bgBase64 = generateFluxImage(bgOnlyPrompt, "");
        String captionedBase64Image = ToonCompositeUtil.createFinalToonCut(bgBase64, characterBase64Image, targetContent);
        String imageUrl = "data:image/jpeg;base64," + captionedBase64Image;
        
        StringBuilder finalWebtoonResult = new StringBuilder();
        finalWebtoonResult.append("<div class='p-4 bg-white border border-gray-100 rounded-2xl shadow-sm space-y-3 mb-4'>\n");
        finalWebtoonResult.append("  <div class='flex items-center space-x-2'>\n");
        finalWebtoonResult.append("    <span class='px-2.5 py-1 bg-indigo-600 text-white text-xs font-bold rounded-lg'>").append(targetCutIndex).append("페이지</span>\n");
        finalWebtoonResult.append("  </div>\n");
        finalWebtoonResult.append("  <p class='text-sm font-semibold text-gray-800 bg-gray-50 p-3 rounded-xl border border-gray-100'>💬 대사: ").append(targetContent).append("</p>\n");
        finalWebtoonResult.append("  <div class='overflow-hidden rounded-xl border border-gray-100 shadow-inner flex justify-center'>\n");
        finalWebtoonResult.append("    <img src='").append(imageUrl).append("' class='w-full max-w-sm h-auto object-cover' alt='인스타툰'>\n");
        finalWebtoonResult.append("  </div>\n");
        finalWebtoonResult.append("</div>\n");

        Map<String, Object> botMsgParam = new HashMap<>();
        botMsgParam.put("roomId", roomId);
        botMsgParam.put("senderType", "BOT");
        botMsgParam.put("content", finalWebtoonResult.toString()); 
        characterMapper.insertMessage(botMsgParam);

        return finalWebtoonResult.toString();
    }

    // 아이디어 생성
    @Transactional 
    public String processIdea(String roomId, String userInput) {
        Map<String, Object> roomInfo = characterMapper.selectChatRoomByRoomId(roomId);
        
        String roomName = (roomInfo != null) ? (String) roomInfo.get("name") : "아이디어 챗봇";
        String description = (roomInfo != null) ? (String) roomInfo.get("description") : "인스타툰 콘셉트 및 소재 발굴";
        
        Map<String, Object> userMsgParam = new HashMap<>();
        userMsgParam.put("roomId", roomId);
        userMsgParam.put("senderType", "USER");
        userMsgParam.put("content", userInput);
        characterMapper.insertMessage(userMsgParam);
        
        String systemInstruction = String.format(
            "너는 사용자의 요구에 실시간으로 대응하며 독자들의 공감을 유도하는 고도화된 인스타툰(컷툰) 전문 기획 AI 엔진이야.\n" +
            "현재 작업 중인 크리에이티브 룸 이름: [%s]\n" +
            "이 룸의 핵심 목적/콘셉트: [%s]\n\n" +
            
            "사용자가 입력한 주제/키워드를 기반으로, SNS(인스타그램)에서 공감대와 높은 조회수(좋아요)를 이끌어낼 수 있는 " +
            "참신하고 기발한 인스타툰 콘셉트 및 핵심 소재 아이디어를 최소 10개 이상 발굴하여 리스트 형태로 작성해줘.\n\n" +
            
            "[작성 규칙]:\n" +
            "- 결과물은 사용자가 보기 좋고 가독성이 뛰어나도록 깔끔한 마크다운(Markdown) 포맷(목록 기호 -, 대괄호 [] 등)을 사용해서 출력해줘.\n" +
            "- 각 아이디어마다 독자의 반응을 유도할 수 있는 [핵심 콘셉트 요약], [컷 연출 팁], [추천 대사]를 유기적으로 엮어서 친절하게 제안해줘.\n" +
            "- JSON 파싱을 위한 형식이 아니므로 개발용 코드나 JSON 데이터는 일절 섞지 말고 사용자가 즉시 활용할 수 있는 기획 가이드북 느낌으로 순수 텍스트만 깔끔하게 답변해줘.",
            roomName, description
        );

        String botReply = callExternalGeminiApi(systemInstruction, userInput);

        Map<String, Object> botMsgParam = new HashMap<>();
        botMsgParam.put("roomId", roomId);
        botMsgParam.put("senderType", "BOT");
        botMsgParam.put("content", botReply.trim()); 
        characterMapper.insertMessage(botMsgParam);

        return botReply.trim();
    }
}