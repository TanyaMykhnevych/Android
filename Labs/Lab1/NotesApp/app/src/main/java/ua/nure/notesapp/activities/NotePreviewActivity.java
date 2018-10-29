package ua.nure.notesapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import ua.nure.notesapp.R;
import ua.nure.notesapp.models.Importance;
import ua.nure.notesapp.models.Note;

public class NotePreviewActivity extends AppCompatActivity {
    private Note noteToEdit;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note_preview);
        fillViewIfEdit();
        prepareSaveButton();
    }

    private void fillViewIfEdit() {
        Intent intent = getIntent();
        noteToEdit = (Note) intent.getSerializableExtra("note_to_edit");
        if (noteToEdit != null) {
            setViewValuesFromNote(noteToEdit);
        }
    }

    private void prepareSaveButton() {
        Button saveButton = (Button) findViewById(R.id.saveNoteBtn);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                Note noteToSave = getNoteToSave();
                intent.putExtra("saved_note", noteToSave);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    private Note getNoteToSave() {
        Note note = noteToEdit == null ? new Note() : noteToEdit;
        note.setTitle(((EditText) findViewById(R.id.NoteTitle)).getText().toString());
        note.setDescription(((EditText) findViewById(R.id.NoteDescription)).getText().toString());

        String importance = ((Spinner) findViewById(R.id.NoteImportance)).getSelectedItem().toString();

        if (importance == getResources().getString(R.string.importance_high)) {
            note.setImportance(Importance.HIGH);
        } else if (importance == getResources().getString(R.string.importance_low)) {
            note.setImportance(Importance.LOW);
        } else {
            note.setImportance(Importance.NORMAL);
        }
        return note;
    }

    private void setViewValuesFromNote(Note note) {
        ((EditText) findViewById(R.id.NoteTitle)).setText(note.getTitle());
        ((EditText) findViewById(R.id.NoteDescription)).setText(note.getDescription());
        ((Spinner) findViewById(R.id.NoteImportance)).setSelection(note.getImportance().getValue());
    }
}
