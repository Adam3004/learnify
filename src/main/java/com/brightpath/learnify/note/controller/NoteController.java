package com.brightpath.learnify.note.controller;

import com.brightpath.learnify.note.model.Note;
import com.brightpath.learnify.note.model.dto.NoteDto;
import com.brightpath.learnify.note.service.NoteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/notes")
public class NoteController {
    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Note> getNoteById(@PathVariable("id") long id) {
        Optional<Note> foundNote = noteService.getNoteById(id);
        return foundNote.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<List<Note>> getUserNotes(@PathVariable("id") long userId) {
        return ResponseEntity.ok(noteService.getNotesByUserId(userId));
    }

    @GetMapping("/workspaces/{id}")
    public ResponseEntity<List<Note>> getWorkspaceNotes(@PathVariable("id") long workspaceID) {
        return ResponseEntity.ok(noteService.getNotesByWorkspaceId(workspaceID));
    }

    //todo consider if we need different endpoint to updateNote? we need
    @PostMapping("")
    public ResponseEntity<Note> saveNote(@RequestBody NoteDto noteDto) {
        Note note = new Note(noteDto);
        noteService.saveNote(note);
        return new ResponseEntity<>(CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Note> deleteNoteById(@PathVariable("id") long id) {
        Optional<Note> foundNote = noteService.getNoteById(id);
        if (foundNote.isPresent()) {
            deleteNoteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
