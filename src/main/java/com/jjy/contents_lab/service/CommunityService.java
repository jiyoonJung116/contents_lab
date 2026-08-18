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

    public CommunityDto getCommunityById(long id) {
        return communityMapper.getCommunityById(id);
    }

    public long saveCommunity(CommunityDto communityDto) {
        if (communityDto.getId() == 0) {
            communityMapper.insertCommunity(communityDto);
        } else {
            communityMapper.updateCommunity(communityDto);
        }

        return communityDto.getId();
    }

    public long deleteCommunity(long userId, long communityId) {
        return communityMapper.deleteCommunity(userId, communityId);
    }
}
