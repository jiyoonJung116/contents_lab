package com.jjy.contents_lab.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.jjy.contents_lab.dto.UserDto;
import com.jjy.contents_lab.mapper.UserMapper;

@Service
public class UserService {
    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder;
    
    public UserService(UserMapper userMapper, BCryptPasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public UserDto getUserById(long id) {
        return userMapper.getUserById(id);
    }

    public long loginUser(String email, String password) {
        UserDto user = userMapper.findByEmail(email);
        if (user == null) {
            return 0;
        }

        if (passwordEncoder.matches(password, user.getPassword())) {
            return user.getId();
        } else {
            return 0;
        }
    }

    public long joinUser(UserDto userDto) {
        if (userDto.getPassword() != null) {
            // 비밀번호 암호화 로직 추가
            String encryptedPassword = passwordEncoder.encode(userDto.getPassword()); 
            userDto.setPassword(encryptedPassword);
        }


        return userMapper.joinUser(userDto);
    }

    public void updateUser(UserDto userDto) {
        if (userDto != null) {
            userMapper.updateUser(userDto);
        }
    }

    public boolean changePassword(Long userId, String currentPassword, String newPassword) throws Exception {
        UserDto user = userMapper.getUserById(userId);
        if (user == null) {
            return false;
        }

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            return false; 
        }

        String encryptedPassword = passwordEncoder.encode(newPassword); 
        userMapper.updatePassword(userId, encryptedPassword); 

        return true; 
    }
}
