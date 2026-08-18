package com.jjy.contents_lab.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.jjy.contents_lab.dto.CommunityDto;

@Mapper
public interface CommunityMapper {
    List<CommunityDto> getCommunityList(Map<String, Object> param);

    CommunityDto getCommunityById(long id);

    long insertCommunity(CommunityDto communityDto);

    long updateCommunity(CommunityDto communityDto);

    long deleteCommunity(@Param("userId") long userId, @Param("communityId") long communityId);
}
