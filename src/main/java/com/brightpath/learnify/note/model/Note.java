package com.brightpath.learnify.note.model;

import com.brightpath.learnify.note.model.dto.NoteDto;

public record Note(
        long id,
        String content
) {
    public Note(NoteDto noteDto) {
        this(noteDto.id(), noteDto.content());
    }
}
