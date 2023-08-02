package com.example.cfnote;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NoteService {
    @Autowired
    private NoteRepository noteRepository;

    public List<Note> getAllNotes() {
        return this.noteRepository.findAll();
    }

    public void saveCourse(Note note) {
        this.noteRepository.save(note);
    }

    public Note getNoteById(Long id){
        Optional<Note> optionalNote = this.noteRepository.findById(id);
        Note note = null;
        if (optionalNote.isPresent()){
            note = optionalNote.get();
        }
        else{
            throw new RuntimeException("Note not found for id: " + id);
        }
        return note;
    }

    public void deleteNoteById(Long id){
        this.noteRepository.deleteById(id);
    }

    public void addNewNote(Note note){
        this.noteRepository.save(note);
    }

    public void updateNote(Note updateNote, long note_id) {
        Optional<Note> optionalNote = this.noteRepository.findById(note_id);
        Note note = null;
        if (optionalNote.isPresent()){
            note = optionalNote.get();
        }
        else{
            throw new RuntimeException("Note not found for id: " + note_id);
        }
        note.setText(updateNote.getText());

    }
}
