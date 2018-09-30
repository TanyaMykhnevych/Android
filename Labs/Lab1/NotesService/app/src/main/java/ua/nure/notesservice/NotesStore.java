package ua.nure.notesservice;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import ua.nure.notesservice.models.Importance;
import ua.nure.notesservice.models.Note;

/**
 * Created by Tanya on 10.09.2018.
 */

public class NotesStore {

    private List<Note> _notes;

    public NotesStore() {
        _notes = new ArrayList<Note>();
    }

    public void add(Note note) {
        _notes.add(note);
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

    private Note findById(UUID id) {
        for (Note note : _notes) {
            if (note.getId().equals(id)) {
                return note;
            }
        }

        return null;
    }
}
