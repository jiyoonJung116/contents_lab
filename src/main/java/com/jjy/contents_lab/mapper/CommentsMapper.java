package com.jjy.contents_lab.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.jjy.contents_lab.dto.CommentsDto;

@Mapper
public interface CommentsMapper {
    List<CommentsDto> getCommentsList(Map<String, Object> param);

    long insertComment(CommentsDto commentsDto);

    long updateComment(CommentsDto commentsDto);

    long deleteComment(Map<String, Object> params);
}
