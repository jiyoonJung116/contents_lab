package com.jjy.contents_lab.dto;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AiLogDto {
    private long id;
    private long userId;
    private long roomId;
    private long promptTemplateId;
    private String actionType;
    private String inputText;
    private String createDate;
}
