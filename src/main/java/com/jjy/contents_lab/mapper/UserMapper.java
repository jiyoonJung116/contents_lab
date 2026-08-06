package com.jjy.contents_lab.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.jjy.contents_lab.dto.UserDto;

public interface UserMapper {
    List<UserDto> getUserList(@Param("offset") int offset, @Param("size") int size);

    int getUserCount();
    
    UserDto getUserById(long id);

    UserDto findByEmail(String email);

    long joinUser(UserDto userDto);

    long updateUser(UserDto userDto);

    void updatePassword(@Param("userId") Long userId, @Param("newPassword") String newPassword);
}
