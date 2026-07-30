package com.jjy.contents_lab.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.jjy.contents_lab.dto.PaymentsDto;
import com.jjy.contents_lab.mapper.PaymentsMapper;

@Service
public class PaymentsService {
    @Value("${portone.api.secret:SECRET}")
    private String PORTONE_API_SECRET;
    private final RestTemplate restTemplate = new RestTemplate();
    private final PaymentsMapper paymentsMapper;

    public PaymentsService(PaymentsMapper paymentsMapper) {
        this.paymentsMapper = paymentsMapper;
    }

    public List<PaymentsDto> getPaymentsList(long userId, int page, int size) {
        Map<String, Object> param = new HashMap<>();
        param.put("userId", userId);
        param.put("page", page);
        param.put("size", size);
        param.put("offset", page * size);
        
        return paymentsMapper.getPaymentsList(param);
    }

    public PaymentsDto getPaymentsInfo(long userId) {
        return paymentsMapper.getPaymentsInfo(userId);
    }

    public boolean executePaymentWithBillingKey(String billingKey, String paymentId, int amount, String orderName) {
        String url = "https://api.portone.io/payments/" + paymentId + "/billing-key";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "PortOne " + PORTONE_API_SECRET);

        Map<String, Object> body = new HashMap<>();
        body.put("billingKey", billingKey);

        Map<String, Object> amountObj = new HashMap<>();
        amountObj.put("total", amount);
        body.put("amount", amountObj);

        body.put("orderName", orderName);
        body.put("currency", "KRW");

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, Map.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                System.out.println("포트원 결제 승인 성공: " + response.getBody());
                return true;
            }
        } catch (Exception e) {
            System.err.println("포트원 결제 요청 중 에러 발생: " + e.getMessage());
        }

        return false;
    }

    // 구독 신청
    @Transactional
    public boolean processSubscription(PaymentsDto paymentsDto) {
        String paymentId = "pay_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8);
        String orderName = "정기 구독 결제 (플랜 #" + paymentsDto.getSubscribeId() + ")";
        boolean isPaid = executePaymentWithBillingKey(
                paymentsDto.getBillingKey(),
                paymentId,
                (int) paymentsDto.getAmount(),
                orderName
        );

        if (isPaid) {
            // 결제 내역 저장
            paymentsMapper.insertPayment(paymentsDto);

            // 회원 구독 정보 저장/갱신
            paymentsMapper.insertUserSubscription(paymentsDto);

            return true;
        }

        return false;
    }
}
