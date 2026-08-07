package com.jjy.contents_lab.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.jjy.contents_lab.dto.InquiriesDto;

@Mapper
public interface InquiriesMapper {
    List<InquiriesDto> getInquiriesList(Map<String, Object> param);

    InquiriesDto getInquiryById(@Param("id") long id);

    long saveInquiries(InquiriesDto inquiriesDto);

    long saveInquiryReply(@Param("id") long id, @Param("adminReply") String adminReply);
}
