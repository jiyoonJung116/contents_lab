package com.jjy.contents_lab.dto;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FaqDto {
    private long id;
    private String category;
    private String question;
    private String answer;
    private String createDate;
}
