package com.jjy.contents_lab.dto;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InquiriesDto {
    private long id;
    private long userId;
    private String inquiryType;
    private String priority;
    private String title;
    private String content;
    private String attachmentUrl;
    private String status;
    private String adminReply;
    private String createDate;
    private String repliedDate;
}
