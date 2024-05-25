package com.brightpath.learnify.note.service;


import com.brightpath.learnify.note.model.Note;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NoteService {
    public Optional<Note> getNoteById(long id) {
        return Optional.empty();
    }

    public List<Note> getNotesByUserId(long userId) {
        return null;
    }

    public List<Note> getNotesByWorkspaceId(long userId) {
        return null;
    }

    public void saveNote(Note note) {

    }

    public void deleteNoteById(long id){

    }
}
