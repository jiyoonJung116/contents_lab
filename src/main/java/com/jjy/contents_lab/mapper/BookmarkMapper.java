package com.jjy.contents_lab.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.jjy.contents_lab.dto.UserBookmarkDto;

@Mapper
public interface BookmarkMapper {
    List<UserBookmarkDto> getBookmarkList(Map<String, Object> param);

    long saveBookmark(UserBookmarkDto userBookmarkDto);

    long deleteBookmark(@Param("userId") long userId, @Param("bookmarkId") long bookmarkId);
}
