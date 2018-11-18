package ua.nure.notesapp.stores;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import ua.nure.notesapp.helpers.NotesDbHelper;
import ua.nure.notesapp.models.Importance;
import ua.nure.notesapp.models.Note;

public class NotesStore {
    String dateFormat = "MM/dd/yyyy HH:mm";
    NotesDbHelper dbHelper;
    SQLiteDatabase db;

    public NotesStore(NotesDbHelper dbHelper) {
        this.dbHelper = dbHelper;
        db = dbHelper.getWritableDatabase();
    }

    public ArrayList<Note> getAll() {
        Cursor c = db.query("notes", null, null, null, null, null, null);
        ArrayList<Note> result = new ArrayList<>();

        if (c.moveToFirst()) {
            do {
                Note note = parseNote(c);
                result.add(note);
            } while (c.moveToNext());
        }
        c.close();

        return result;
    }

    public ArrayList<Note> searchAndFilter(int importance, String searchText) {
        boolean shouldFilter = importance < 4 && importance > 0;
        boolean shouldSearch = searchText.length() > 0;

        String importanceStringValue = String.valueOf(importance);

        String query = "";
        if (shouldFilter) query += "importance = " + importanceStringValue;

        if (shouldSearch) {
            if (!query.equals("")) {
                query = "(" + query + ") and";
            }
            query += "(title like '%" + searchText + "%' or description like '%" + searchText + "%')";
        }

        Cursor c = db.query("notes", null, query, null, null, null, null);
        ArrayList<Note> result = new ArrayList<>();

        if (c.moveToFirst()) {
            do {
                Note note = parseNote(c);
                result.add(note);
            } while (c.moveToNext());
        }
        c.close();

        return result;
    }

    public Note getById(int id) {
        Cursor c = db.query("notes", null, "id = ?", new String[]{String.valueOf(id)}, null, null, null);
        Note note = null;

        if (c.moveToFirst()) {
            note = parseNote(c);
        }
        c.close();

        return note;
    }

    public void update(Note note) {
        ContentValues cv = getContentValuesByNote(note);
        int updCount = db.update("notes", cv, "id = ?",
                new String[]{String.valueOf(note.getId())});
    }

    public void add(Note note) {
        ContentValues cv = getContentValuesByNote(note);
        long rowID = db.insert("notes", null, cv);
        note.setId((int) rowID);
    }

    public void delete(int id) {
        int delCount = db.delete("notes", "id = ?", new String[]{String.valueOf(id)});
    }

    private ContentValues getContentValuesByNote(Note note) {
        ContentValues cv = new ContentValues();
        cv.put("title", note.getTitle());
        cv.put("description", note.getDescription());
        cv.put("importance", note.getImportance().getValue());

        Date date = note.getDate();
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        cv.put("date", formatter.format(date));

        cv.put("imagePath", note.getImagePath());

        return cv;
    }

    private Note parseNote(Cursor c) {
        int idColIndex = c.getColumnIndex("id");
        int titleColIndex = c.getColumnIndex("title");
        int descriptionColIndex = c.getColumnIndex("description");
        int importanceColIndex = c.getColumnIndex("importance");
        int dateColIndex = c.getColumnIndex("date");
        int imagePathColIndex = c.getColumnIndex("imagePath");

        Note note = new Note();
        note.setId(c.getInt(idColIndex));
        note.setTitle(c.getString(titleColIndex));
        note.setDescription(c.getString(descriptionColIndex));
        int importanceIntValue = c.getInt(importanceColIndex);
        note.setImportance(Importance.values()[importanceIntValue]);

        String dateString = c.getString(dateColIndex);
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        try {
            note.setDate(formatter.parse(dateString));
        } catch (ParseException e) {
            note.setDate(new Date());
        }
        note.setImagePath(c.getString(imagePathColIndex));

        return note;
    }
}
