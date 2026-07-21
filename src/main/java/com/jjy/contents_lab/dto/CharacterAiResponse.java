package com.jjy.contents_lab.dto;

import lombok.Data;

@Data
public class CharacterAiResponse {
    private String name;           // 7단계: 자동 생성된 캐릭터 이름 (예: 돈토리)
    private String species;        // 2단계: 젤리
    private String concept;        // 2단계: 투자
    private String color;          // 2단계: 하늘색
    private String personality;    // 2단계: 똑똑함
    
    // 4단계: Prompt Builder 조립용 영문 필드
    private String englishSpecies; // jelly
    private String englishConcept; // investment, finance
    private String englishColor;   // light blue
}
