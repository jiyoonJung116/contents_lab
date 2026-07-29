package com.jjy.contents_lab.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.jjy.contents_lab.dto.PaymentsDto;
import com.jjy.contents_lab.mapper.PaymentsMapper;

@Service
public class PaymentsService {
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
}
