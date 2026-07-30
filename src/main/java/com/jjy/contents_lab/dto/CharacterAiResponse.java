package com.jjy.contents_lab.dto;

import lombok.Data;

@Data
public class CharacterAiResponse {
    private String name; 
    private String species;        
    private String concept;        
    private String color;         
    private String personality;   
    
    private String englishSpecies; 
    private String englishConcept; 
    private String englishColor; 
}
