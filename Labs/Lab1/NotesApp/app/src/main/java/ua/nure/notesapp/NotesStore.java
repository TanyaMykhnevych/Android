package ua.nure.notesapp;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import ua.nure.notesapp.models.Importance;
import ua.nure.notesapp.models.Note;

public class NotesStore {

    private List<Note> _notes;

    public NotesStore() {
        _notes = new ArrayList<Note>();
    }

    public List<Note> getNotes() {
        return _notes;
    }

    public void remove(Note note) {
        _notes.remove(note);
    }

    public void update(Note note) {
        int index = _notes.indexOf(findById(note.getId()));

        if (index > -1) {
            _notes.set(index, note);
        }
    }

    public Integer getTotalCount() {
        return _notes.size();
    }

    public void clear() {
        _notes.clear();
    }

    public List<Note> getByImportance(Importance importance) {
        List<Note> filteredList = new ArrayList<Note>();
        for (Note note : _notes) {
            if (note.getImportance().equals(importance)) {
                filteredList.add(note);
            }
        }
        return filteredList;
    }

    public List<Note> getByDescription(String description) {
        List<Note> filteredList = new ArrayList<Note>();
        for (Note note : _notes) {
            if (note.getDescription().contains(description)) {
                filteredList.add(note);
            }
        }
        return filteredList;
    }

    public Note get(int position) {
        return _notes.get(position);
    }

    public void remove(int position) {
        _notes.remove(position);
    }


    public void add(Note note) {
        _notes.add(note);
    }


    public void addOrUpdate(Note note) {
        Note existingNote = findById(note.getId());
        if (existingNote != null) {
            existingNote.setTitle(note.getTitle());
            existingNote.setDescription(note.getDescription());
            existingNote.setDate(note.getDate());
            existingNote.setImportance(note.getImportance());
            existingNote.setImagePath(note.getImagePath());
        } else {
            _notes.add(note);
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

