package com.brightpath.learnify.persistance.user;

import com.brightpath.learnify.domain.user.User;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserAdapter {
    private final UserRepository userRepository;

    public User createUser(String id, String email, String displayName) {
        UserEntity toSave = new UserEntity(id, displayName, email);
        UserEntity result = userRepository.save(toSave);
        return asUser(result);
    }

    public List<User> queryUsers(@Nullable String email, @Nullable String displayName) {
        UserQueryFilter filter = new UserQueryFilter(email, displayName);
        Pageable pageRequest = PageRequest.of(0, 20);
        return userRepository.findByFilter(filter, pageRequest)
                .stream().map(this::asUser)
                .toList();
    }

    public List<User> getUsersByIds(List<String> userIds) {
        return userRepository.findAllByIds(userIds).stream()
                .map(this::asUser)
                .toList();
    }

    public User getUserById(String userId) {
        return userRepository.findById(userId).map(this::asUser).orElse(null);
    }

    public boolean userExists(String userId) {
        return userRepository.existsById(userId);
    }

    private User asUser(UserEntity result) {
        return new User(result.getId(), result.getDisplayName(), result.getEmail());
    }
}
