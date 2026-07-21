package com.jjy.contents_lab.dto;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MediaAssetDto {
    private long id;
    private long userId;
    private long messageId;
    private String fileUrl;
    private String fileType;
    private long fileSize;  
    private String isAutoCompressed;
    private String createDate;
}
