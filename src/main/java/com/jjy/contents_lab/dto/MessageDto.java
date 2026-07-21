package com.jjy.contents_lab.dto;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MessageDto {
    private long id;
    private long roomId;
    private String senderType;
    private long senderId;
    private String content;
    private String hasReference;
    private String createDate;
}
