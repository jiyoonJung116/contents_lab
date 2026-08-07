package com.jjy.contents_lab.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.jjy.contents_lab.dto.PromptTemplateDto;

@Mapper
public interface ContentIdeaMapper {
    List<PromptTemplateDto> getPromptList(Map<String, Object> param);

    long savePromptTemplate(PromptTemplateDto promptTemplateDto);
}
