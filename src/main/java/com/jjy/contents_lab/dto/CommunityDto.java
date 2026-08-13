package com.jjy.contents_lab.dto;


import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CommunityDto {
    private long id;
    private long userId;
    private String content;
    private String mediaUrl; 
    private long viewCount;
    private String createDate;
    private String updateDate;
    private long commentCount;
    private String userName;
}
