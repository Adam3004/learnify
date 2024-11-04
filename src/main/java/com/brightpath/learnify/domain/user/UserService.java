package com.brightpath.learnify.domain.user;

import com.brightpath.learnify.domain.common.UuidProvider;
import com.brightpath.learnify.exception.authorization.UserNotFoundException;
import com.brightpath.learnify.persistance.user.UserEntity;
import com.brightpath.learnify.persistance.user.UserQueryFilter;
import com.brightpath.learnify.persistance.user.UserRepository;
import jakarta.annotation.Nullable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UuidProvider uuidProvider;
    private final UserRepository userRepository;

    public UserService(UuidProvider uuidProvider, UserRepository userRepository) {
        this.uuidProvider = uuidProvider;
        this.userRepository = userRepository;
    }

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

    private User asUser(UserEntity result) {
        return new User(result.getId(), result.getDisplayName(), result.getEmail());
    }

    public User getUserById(String userId) {
        return userRepository.findById(userId).map(this::asUser).orElse(null);
    }

    public void checkIfUserExists(String userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }
    }
}
