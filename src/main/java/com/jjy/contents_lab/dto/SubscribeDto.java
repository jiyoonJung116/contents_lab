package com.jjy.contents_lab.dto;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SubscribeDto {
    private long id;
    private String subscribeType;
    private String subscribeName;
    private long price;
    private String billingCycle;
    private int discountRate;
    private String description;
}
