package com.jjy.contents_lab.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.jjy.contents_lab.dto.CommentsDto;
import com.jjy.contents_lab.mapper.CommentsMapper;

@Service
public class CommentsService {
    private final CommentsMapper commentsMapper;

    public CommentsService(CommentsMapper commentsMapper) {
        this.commentsMapper = commentsMapper;
    }

    public List<CommentsDto> getCommentsList(Long communityId) {
        Map<String, Object> params = new HashMap<>();
        params.put("communityId", communityId);
        
        return commentsMapper.getCommentsList(params);
    }

    public long saveComment(CommentsDto commentsDto) {
        if (commentsDto.getId() == 0 || commentsDto.getId() == 0) {
            commentsMapper.insertComment(commentsDto);
        } else {
            commentsMapper.updateComment(commentsDto);
        }

        return commentsDto.getId();
    }

    public long deleteComment(long id, long userId) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        params.put("userId", userId);

        return commentsMapper.deleteComment(params);
    }
}
