package com.jjy.contents_lab.dto;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChatRoomDto {
    private long id;
    private long userId;
    private String name;
    private String description;
    private String aspectRatio;
    private long custom_width;
    private long custom_height;
    private String settingAutoText;
    private String settingStyleConsistent;
    private String settingAutoMetadata;
    private long saveLocationId;
    private long roomId;
    private String imageUrl;
    private String roomType;
    private String createDate;
    private String updateDate;    
}
