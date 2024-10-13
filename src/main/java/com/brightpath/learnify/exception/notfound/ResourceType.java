package com.brightpath.learnify.exception.notfound;

import lombok.Getter;

@Getter
public enum ResourceType {
    NOTE("note"),
    QUIZ("quiz"),
    BOARD_PAGE("note page"),
    BOARD_NOTE_PAGE("document page");

    private final String readableValue;

    ResourceType(String toStringValue) {
        this.readableValue = toStringValue;
    }
}
