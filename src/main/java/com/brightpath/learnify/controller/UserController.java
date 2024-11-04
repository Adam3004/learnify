package com.brightpath.learnify.controller;

import com.brightpath.learnify.api.UsersApi;
import com.brightpath.learnify.controller.mapper.DtoMapper;
import com.brightpath.learnify.domain.auth.UserIdentityService;
import com.brightpath.learnify.domain.user.User;
import com.brightpath.learnify.domain.user.UserService;
import com.brightpath.learnify.model.UserCreateDto;
import com.brightpath.learnify.model.UserSummaryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class UserController implements UsersApi {

    private final UserIdentityService userIdentityService;
    private final UserService userService;
    private final DtoMapper dtoMapper;

    @Override
    public ResponseEntity<UserSummaryDto> registerUser(UserCreateDto userCreateDto) {
        String userId = userIdentityService.getCurrentUserId();
        User user = userService.createUser(userId, userCreateDto.getEmail(), userCreateDto.getDisplayName());
        return ResponseEntity.ok(dtoMapper.asUserSummaryDto(user));
    }

    @Override
    public ResponseEntity<List<UserSummaryDto>> searchUsers(String email, String displayName) {
        List<User> users = userService.queryUsers(email, displayName);
        return ResponseEntity.ok(users.stream().map(dtoMapper::asUserSummaryDto).toList());
    }
}
