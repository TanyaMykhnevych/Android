package ua.nure.notesapp.helpers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class NotesDbHelper extends SQLiteOpenHelper {

    public NotesDbHelper(Context context) {
        super(context, "notesDb", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table notes ("
                + "id integer primary key autoincrement,"
                + "title text,"
                + "description text,"
                + "importance integer,"
                + "date text,"
                + "imagePath text"
                + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
