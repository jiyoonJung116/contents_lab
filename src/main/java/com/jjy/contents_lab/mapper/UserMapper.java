package com.jjy.contents_lab.mapper;

import org.apache.ibatis.annotations.Param;

import com.jjy.contents_lab.dto.UserDto;

public interface UserMapper {
    UserDto getUserById(long id);

    UserDto findByEmail(String email);

    long joinUser(UserDto userDto);

    long updateUser(UserDto userDto);

    void updatePassword(@Param("userId") Long userId, @Param("newPassword") String newPassword);
}
