package com.brightpath.learnify.domain.user;

import com.brightpath.learnify.domain.common.UuidProvider;
import com.brightpath.learnify.persistance.user.UserEntity;
import com.brightpath.learnify.persistance.user.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

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

    private User asUser(UserEntity result) {
        return new User(result.getId(), result.getDisplayName(), result.getEmail());
    }

    public User getUserById(String userId) {
        return userRepository.findById(userId).map(this::asUser).orElse(null);
    }
}
