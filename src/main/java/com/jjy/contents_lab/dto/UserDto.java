package com.jjy.contents_lab.dto;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private long id;
    private String email;
    private String password;
    private String userName;
    private String nickname;
    private String profileImageUrl;
    private String planType;
    private String interestContent;
    private long storageLimit;
    private String adminYn;
    private String createDate;
    private String updateDate;
}
