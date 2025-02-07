package com.brightpath.learnify.domain.user.port;

import com.brightpath.learnify.domain.user.User;
import jakarta.annotation.Nullable;

import java.util.List;

public interface UserPersistencePort {
    User createUser(String id, String email, String displayName);

    List<User> queryUsers(@Nullable String email, @Nullable String displayName);

    List<User> getUsersByIds(List<String> userIds);

    User getUserById(String userId);

    boolean userExists(String userId);
}
