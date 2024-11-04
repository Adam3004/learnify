package com.brightpath.learnify.config;

import com.brightpath.learnify.persistance.user.UserEntity;
import com.brightpath.learnify.persistance.user.UserRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.ListUsersPage;
import com.google.firebase.auth.UserRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;

@Configuration
@RequiredArgsConstructor
public class FirebaseDatabaseConfigurator {

    private final UserRepository userRepository;

    @Bean
    CommandLineRunner firebaseUsersSync() {
        return args -> {
            try {
                ListUsersPage page = FirebaseAuth.getInstance().listUsers(null);

                // Iterate over each page of users
                while (page != null) {
                    for (UserRecord userRecord : page.getValues()) {
                        // Map Firebase UserRecord to your application User entity
                        String uid = userRecord.getUid();
                        if(userRepository.findById(uid).isPresent()) {
                            continue;
                        }
                        UserEntity userEntity = new UserEntity();
                        userEntity.setId(userRecord.getUid());
                        userEntity.setEmail(userRecord.getEmail());
                        String displayName = Optional.ofNullable(userRecord.getDisplayName())
                                .orElse(userRecord.getEmail().substring(0, userRecord.getEmail().indexOf("@")));
                        userEntity.setDisplayName(displayName);
                        userRepository.save(userEntity);
                    }
                    // Get the next page
                    page = page.getNextPage();
                }
            } catch (FirebaseAuthException e) {
                e.printStackTrace();
                // Handle errors, possibly logging or rethrowing as needed
            }
        };
    }
}
