package com.jjy.contents_lab.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.jjy.contents_lab.dto.UserBookmarkDto;
import com.jjy.contents_lab.mapper.BookmarkMapper;

@Service
public class BookmarkService {
    private final BookmarkMapper bookmarkMapper;

    public BookmarkService(BookmarkMapper bookmarkMapper) {
        this.bookmarkMapper = bookmarkMapper;
    }

    public List<UserBookmarkDto> getBookmarkList(long userId, int page, int size) {
        Map<String, Object> param = new HashMap<>();
        param.put("userId", userId);
        param.put("page", page);
        param.put("size", size);
        param.put("offset", page * size);
        
        return bookmarkMapper.getBookmarkList(param);
    }

    public long saveBookmark(UserBookmarkDto userBookmarkDto) {
        return bookmarkMapper.saveBookmark(userBookmarkDto);
    }

    public long deleteBookmark(long bookmarkId, long userId) {
        return bookmarkMapper.deleteBookmark(bookmarkId, userId);
    }
}
