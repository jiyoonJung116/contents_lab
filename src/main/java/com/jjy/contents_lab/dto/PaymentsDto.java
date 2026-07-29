package com.jjy.contents_lab.dto;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentsDto {
    private long id;
    private long subscribeId;
    private long userId;
    private long amount;
    private String cardNumber;
    private String paymentMethod;
    private String status;
    private String receiptUrl;
    private String createDate;
    private String startDate;
    private String endDate;

    private String subscribeName;
    private String priceInfo;
}
