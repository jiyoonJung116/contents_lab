package com.jjy.contents_lab.dto;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CommentsDto {
    private long id;
    private long communityId;
    private long parentCommentId;
    private String content;
    private String createDate;
    private String updateDate;
    private String userName;
}
