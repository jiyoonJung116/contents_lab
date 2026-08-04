package com.jjy.contents_lab.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.jjy.contents_lab.dto.InquiriesDto;

@Mapper
public interface InquiriesMapper {
    List<InquiriesDto> getInquiriesList(Map<String, Object> param);
}
