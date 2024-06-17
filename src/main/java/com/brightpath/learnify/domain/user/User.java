package com.brightpath.learnify.domain.user;

import com.brightpath.learnify.model.UserSummaryDto;

import java.util.UUID;

public record User(
        UUID id,
        String displayName,
        String email
) {
    public UserSummaryDto convertToUserSummaryDto(){
        return new UserSummaryDto(id.toString(), displayName, email);
    }
}
