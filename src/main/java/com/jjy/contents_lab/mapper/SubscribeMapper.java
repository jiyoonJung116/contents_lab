package com.jjy.contents_lab.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.jjy.contents_lab.dto.SubscribeDto;

@Mapper
public interface SubscribeMapper {
    List<SubscribeDto> getSubscribeList();
}
