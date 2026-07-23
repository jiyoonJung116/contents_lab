package com.jjy.contents_lab.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.jjy.contents_lab.dto.PromptTemplateDto;
import com.jjy.contents_lab.mapper.ContentIdeaMapper;

@Service
public class ContentIdeaService {
    private final ContentIdeaMapper contentIdeaMapper;

    public ContentIdeaService(ContentIdeaMapper contentIdeaMapper) {
        this.contentIdeaMapper = contentIdeaMapper;
    }

    public List<PromptTemplateDto> getPromptList(String targetSize, int page, int size) {
        Map<String, Object> param = new HashMap<>();
        param.put("targetSize", targetSize);
        param.put("page", page);
        param.put("size", size);
        param.put("offset", page * size);
        
        return contentIdeaMapper.getPromptList(param);
    }
}
