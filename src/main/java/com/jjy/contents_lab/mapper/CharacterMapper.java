package com.jjy.contents_lab.mapper;

import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.jjy.contents_lab.dto.ChatRoomDto;

@Mapper
public interface CharacterMapper {
    List<Map<String, Object>> selectMessagesByRoomId(String roomId);

    List<ChatRoomDto> getRoomList(@Param("userId") long userId);

    List<Map<String, Object>> getImagesByType(@Param("userId") Long userId, @Param("imageType") String imageType);

    List<Map<String, Object>> getSavedScripts(@Param("userId") Long userId);

    Map<String, Object> selectChatRoomByRoomId(@Param("roomId") String roomId);

    Map<String, Object> selectScriptById(@Param("scriptId") String scriptId);

    Map<String, Object> selectCharacterById(@Param("charId") String charId);

    void insertMessage(Map<String, Object> messageParam);

    void insertGeneratedImage(Map<String, Object> imageParam);
}