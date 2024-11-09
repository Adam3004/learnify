package com.brightpath.learnify.domain.note;

public record NotePage(
        int pageNumber,
        String content,
        int version
) {

    public NotePage(int pageNumber, String content, int version) {
        this.pageNumber = pageNumber;
        this.content = content;
        this.version = version;
    }
}
