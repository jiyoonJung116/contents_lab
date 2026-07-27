package com.jjy.contents_lab.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.jjy.contents_lab.dto.SubscribeDto;
import com.jjy.contents_lab.mapper.SubscribeMapper;

@Service
public class SubscribeService {
    private final SubscribeMapper subscribeMapper;

    public SubscribeService(SubscribeMapper subscribeMapper) {
        this.subscribeMapper = subscribeMapper;
    }

    public List<SubscribeDto> getSubscribeList() {
        return subscribeMapper.getSubscribeList();
    }
}
