package com.jjy.contents_lab.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.jjy.contents_lab.dto.PaymentsDto;

@Mapper
public interface PaymentsMapper {
    List<PaymentsDto> getPaymentsList(Map<String, Object> param);

    PaymentsDto getPaymentsInfo(long userId);
}
