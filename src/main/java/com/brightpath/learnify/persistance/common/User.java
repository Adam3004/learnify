package com.brightpath.learnify.persistance.common;

import com.brightpath.learnify.model.UserSummaryDto;

import java.util.UUID;

public record User(
        UUID uuid,
        String displayName,
        String email
) {
    public UserSummaryDto convertToUserSummaryDto(){
        return new UserSummaryDto(uuid.toString(), displayName, email);
    }
}
