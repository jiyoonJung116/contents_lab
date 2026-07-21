package com.jjy.contents_lab.dto;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GeneratedImageDto {
    private long id;
    private long messageId;
    private String imageUrl;
    private String thumbnailUrl;
    private String actualPrompt;
    private long fileSize;
    private String isBookmark;
    private String createDate;
}
