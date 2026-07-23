package com.jjy.contents_lab.dto;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserBookmarkDto {
    private long id;
    private long userId;
    private long targetId;
    private String targetType;
    private String createDate;

    // 북마크
    private String targetSize;
    private String title;
    private String promptText;
}
