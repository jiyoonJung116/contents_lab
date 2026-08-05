package com.jjy.contents_lab.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.jjy.contents_lab.dto.InquiriesDto;
import com.jjy.contents_lab.mapper.InquiriesMapper;

@Service
public class InquiriesService {
    private final InquiriesMapper inquiriesMapper;

    public InquiriesService(InquiriesMapper inquiriesMapper) {
        this.inquiriesMapper = inquiriesMapper;
    }

    public List<InquiriesDto> getInquiriesList(long userId, String status, int page, int size) {
        Map<String, Object> param = new HashMap<>();
        param.put("userId", userId);
        param.put("page", page);
        param.put("size", size);
        param.put("offset", page * size);
        if ("ALL".equalsIgnoreCase(status)) {
            status = null; 
        } else {
            param.put("status", status);
        }
        
        return inquiriesMapper.getInquiriesList(param);
    }

    public long saveInquiries(InquiriesDto inquiriesDto) {
        return inquiriesMapper.saveInquiries(inquiriesDto);
    }
}
