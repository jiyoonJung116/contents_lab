package com.jjy.contents_lab.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.jjy.contents_lab.dto.CommunityDto;

@Mapper
public interface CommunityMapper {
    List<CommunityDto> getCommunityList(Map<String, Object> param);
}
