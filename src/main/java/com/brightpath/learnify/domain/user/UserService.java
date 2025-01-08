package com.brightpath.learnify.domain.user;

import com.brightpath.learnify.exception.authorization.UserNotFoundException;
import com.brightpath.learnify.persistance.user.UserAdapter;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserAdapter userAdapter;

    public User createUser(String id, String email, String displayName) {
        return userAdapter.createUser(id, email, displayName);
    }

    public List<User> queryUsers(@Nullable String email, @Nullable String displayName) {
        return userAdapter.queryUsers(email, displayName);
    }

    public List<User> getUsersByIds(List<String> userIds) {
        return userAdapter.getUsersByIds(userIds);
    }

    public User getUserById(String userId) {
        return userAdapter.getUserById(userId);
    }

    public void checkIfUserExists(String userId) {
        if (!userAdapter.userExists(userId)) {
            throw new UserNotFoundException(userId);
        }
    }
}
