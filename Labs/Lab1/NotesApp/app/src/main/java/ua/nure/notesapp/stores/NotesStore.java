package ua.nure.notesapp.stores;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import ua.nure.notesapp.models.Importance;
import ua.nure.notesapp.models.Note;

public class NotesStore {

    private final List<Note> _notes = new ArrayList<Note>();

    public NotesStore() {
        fillTestData();
    }

    public void fillTestData() {
        _notes.add(new Note("Test Title 1", "Test Description 1", Importance.LOW, new Date(), ""));
        _notes.add(new Note("Test Title 2", "Test Description 2", Importance.LOW, new Date(), ""));
        _notes.add(new Note("Test Title 3", "Test Description 3", Importance.NORMAL, new Date(), ""));
        _notes.add(new Note("Test Title 4", "Test Description 4", Importance.NORMAL, new Date(), ""));
        _notes.add(new Note("Test Title 5", "Test Description 5", Importance.HIGH, new Date(), ""));
        _notes.add(new Note("Test Title 6", "Test Description 6", Importance.HIGH, new Date(), ""));
    }

    public List<Note> getNotes() {
        return _notes;
    }

    public Note get(int position) {
        return _notes.get(position);
    }

    public void add(Note note) {
        _notes.add(note);
    }

//    public void replace(Note note) {
//        UUID noteId = note.getId();
//        Note noteFromList = findById(noteId);
//        int index = _notes.indexOf(noteFromList);
//
//        if (index > -1) {
//            _notes.set(index, note);
//        }
//    }

    public void update(Note note) {
        UUID noteId = note.getId();
        Note existingNote = findById(noteId);

        if (existingNote != null) {
            existingNote.setTitle(note.getTitle());
            existingNote.setDescription(note.getDescription());
            existingNote.setDate(note.getDate());
            existingNote.setImportance(note.getImportance());
            existingNote.setImagePath(note.getImagePath());
        }
    }

    public void remove(int position) {
        _notes.remove(position);
    }

    public void remove(Note note) {
        UUID noteId = note.getId();
        Note noteFromList = findById(noteId);

        _notes.remove(noteFromList);
    }

    public void addOrUpdate(Note note) {
        Note existingNote = findById(note.getId());
        if (existingNote != null) {
            update(note);
        } else {
            add(note);
        }
    }

    private Note findById(UUID id) {
        for (Note note : _notes) {
            if (note.getId().equals(id)) {
                return note;
            }
        }

        return null;
    }
}

