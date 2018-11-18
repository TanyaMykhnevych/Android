package ua.nure.notesapp.services;

import android.content.Context;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import ua.nure.notesapp.helpers.NotesDbHelper;
import ua.nure.notesapp.models.Importance;
import ua.nure.notesapp.models.Note;
import ua.nure.notesapp.stores.NotesStore;

public class NotesService {

    private NotesStore _notesStore;

    public NotesService(Context context) {
        NotesDbHelper dbHelper = new NotesDbHelper(context);
        _notesStore = new NotesStore(dbHelper);
    }

    public ArrayList<Note> getAllNotes() {
        return _notesStore.getAll();
    }

    public void add(Note note) {
        _notesStore.add(note);
    }

    public void update(Note note) {
        Note existingNote = findById(note.getId());

        if (existingNote != null) {
            existingNote.setTitle(note.getTitle());
            existingNote.setDescription(note.getDescription());
            existingNote.setDate(note.getDate());
            existingNote.setImportance(note.getImportance());
            existingNote.setImagePath(note.getImagePath());

            _notesStore.update(existingNote);
        }
    }

    public void remove(Note note) {
        Note noteToDelete = findById(note.getId());
        if (noteToDelete != null) {
            _notesStore.delete(note.getId());
        }
    }

    public void addOrUpdate(Note note) {
        Note existingNote = findById(note.getId());
        if (existingNote != null) {
            update(note);
        } else {
            add(note);
        }
    }

    public ArrayList<Note> FilterAndSearch(Importance importance, String text) {
        if (importance == Importance.ALL && text.trim().length() < 1) return  getAllNotes();

        String searchText = text.trim().toLowerCase();
        return _notesStore.searchAndFilter(importance.getValue(), searchText);
    }

    private Note findById(Integer id) {
        return _notesStore.getById(id);
    }
}

