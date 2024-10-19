package com.brightpath.learnify.exception.notfound;

import lombok.Getter;

@Getter
public enum ResourceType {
    NOTE("note"),
    QUIZ("quiz"),
    BOARD_NOTE_PAGE("note page"),
    DOCUMENT_NOTE_PAGE("document page");

    private final String readableValue;

    ResourceType(String toStringValue) {
        this.readableValue = toStringValue;
    }
}
