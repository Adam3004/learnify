package com.brightpath.learnify.quiz.model.note;

import com.brightpath.learnify.model.NoteDto;

public record Note(
        String id,
        String content
) {
    public Note(NoteDto noteDto) {
        this(noteDto.getId(), noteDto.getContent());
    }
}
