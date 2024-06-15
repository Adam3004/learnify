package com.brightpath.learnify.domain.user;

import com.brightpath.learnify.domain.common.UuidProvider;
import com.brightpath.learnify.persistance.common.User;
import com.brightpath.learnify.persistance.user.UserEntity;
import com.brightpath.learnify.persistance.user.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UuidProvider uuidProvider;
    private final UserRepository userRepository;

    public UserService(UuidProvider uuidProvider, UserRepository userRepository) {
        this.uuidProvider = uuidProvider;
        this.userRepository = userRepository;
    }

    public User createUser(String email, String displayName) {
        var toSave = new UserEntity(uuidProvider.generateUuid(), displayName, email);
        var result = userRepository.save(toSave);
        return asUser(result);
    }

    private User asUser(UserEntity result) {
        return new User(result.getId(), result.getDisplayName(), result.getEmail());
    }
}
