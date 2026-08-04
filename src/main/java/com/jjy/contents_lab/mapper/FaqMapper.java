package com.jjy.contents_lab.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.jjy.contents_lab.dto.FaqDto;

@Mapper
public interface FaqMapper {
    List<FaqDto> getFaqList(Map<String, Object> param);
}
