package com.jjy.contents_lab.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.jjy.contents_lab.dto.CommunityDto;
import com.jjy.contents_lab.mapper.CommunityMapper;

@Service
public class CommunityService {
    private final CommunityMapper communityMapper;

    public CommunityService(CommunityMapper communityMapper) {
        this.communityMapper = communityMapper;
    }

    public List<CommunityDto> getCommunityList(int page, int size) {
        Map<String, Object> param = new HashMap<>();
        param.put("page", page);
        param.put("size", size);
        param.put("offset", page * size);
        
        return communityMapper.getCommunityList(param);
    }
}
