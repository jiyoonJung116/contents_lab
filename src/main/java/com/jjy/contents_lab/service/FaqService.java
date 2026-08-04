package com.jjy.contents_lab.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.jjy.contents_lab.dto.FaqDto;
import com.jjy.contents_lab.mapper.FaqMapper;

@Service
public class FaqService {
    private final FaqMapper faqMapper;

    public FaqService(FaqMapper faqMapper) {
        this.faqMapper = faqMapper;
    }

    public List<FaqDto> getFaqList(String content, int page, int size) {
        Map<String, Object> param = new HashMap<>();
        param.put("content", content);
        param.put("page", page);
        param.put("size", size);
        param.put("offset", page * size);
        
        return faqMapper.getFaqList(param);
    }
}
