package com.bhushan.linkedin.user_service.service;

import com.bhushan.linkedin.user_service.dto.LoginRequestDto;
import com.bhushan.linkedin.user_service.dto.SignUpRequestDto;
import com.bhushan.linkedin.user_service.dto.UserDto;
import com.bhushan.linkedin.user_service.entity.User;
import com.bhushan.linkedin.user_service.exception.BadRequestException;
import com.bhushan.linkedin.user_service.exception.ResourceNotFoundException;
import com.bhushan.linkedin.user_service.repository.UserRepository;
import com.bhushan.linkedin.user_service.utils.PasswordUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final JwtService jwtService;

    public UserDto signup(SignUpRequestDto signUpRequestDto) {

        boolean exists = userRepository.existsByEmail(signUpRequestDto.getEmail());

        if(exists){
            throw new BadRequestException("User already exist, cannot signup again");
        }

        User user = modelMapper.map(signUpRequestDto, User.class);
        user.setPassword(PasswordUtil.hashPassword(signUpRequestDto.getPassword()));

        User savedUser = userRepository.save(user);
        return modelMapper.map(savedUser, UserDto.class);
    }

    public String login(LoginRequestDto loginRequestDto) {
        User user = userRepository.findByEmail(loginRequestDto.getEmail())
                .orElseThrow(()-> new ResourceNotFoundException("user not found with email : "+loginRequestDto.getEmail()));

        boolean isPasswordMatch = PasswordUtil.checkPassword(loginRequestDto.getPassword(),user.getPassword());

        if(!isPasswordMatch){
            throw new BadRequestException("Incorrect Password");
        }

        return jwtService.generateAccessToken(user);

    }
}
