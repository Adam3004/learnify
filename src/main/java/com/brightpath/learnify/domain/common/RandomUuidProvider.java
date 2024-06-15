package com.brightpath.learnify.domain.common;


import java.util.UUID;

public class RandomUuidProvider implements UuidProvider {
    @Override
    public UUID generateUuid() {
        return UUID.randomUUID();
    }
}
