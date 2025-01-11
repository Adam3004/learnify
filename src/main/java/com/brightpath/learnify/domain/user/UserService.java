package com.brightpath.learnify.domain.user;

import com.brightpath.learnify.exception.authorization.UserNotFoundException;
import com.brightpath.learnify.domain.user.port.UserPersistencePort;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserPersistencePort userPersistencePort;

    public User createUser(String id, String email, String displayName) {
        return userPersistencePort.createUser(id, email, displayName);
    }

    public List<User> queryUsers(@Nullable String email, @Nullable String displayName) {
        return userPersistencePort.queryUsers(email, displayName);
    }

    public List<User> getUsersByIds(List<String> userIds) {
        return userPersistencePort.getUsersByIds(userIds);
    }

    public User getUserById(String userId) {
        return userPersistencePort.getUserById(userId);
    }

    public void checkIfUserExists(String userId) {
        if (!userPersistencePort.userExists(userId)) {
            throw new UserNotFoundException(userId);
        }
    }
}
