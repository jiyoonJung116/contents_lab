package com.jjy.contents_lab.dto;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PromptTemplateDto {
    private long id;
    private String category;
    private String targetSize;
    private String title;
    private String description;
    private String promptText;
    private String isVisible;
    private String createDate;
}
