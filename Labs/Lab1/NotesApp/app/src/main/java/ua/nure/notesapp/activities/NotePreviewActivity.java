package ua.nure.notesapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

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

        Calendar c = Calendar.getInstance();
        ((DatePicker) findViewById(R.id.date_picker)).setMinDate(c.getTimeInMillis());
    }

    private void fillViewIfEdit() {
        Intent intent = getIntent();
        noteToEdit = (Note) intent.getSerializableExtra("note_to_edit");
        if (noteToEdit != null) {
            setTitle(R.string.edit_note);
            setViewValuesFromNote(noteToEdit);
        } else {
            setTitle(R.string.add_note);
        }
    }

    private void prepareSaveButton() {
        Button saveButton = (Button) findViewById(R.id.saveNoteBtn);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                Note noteToSave = getNoteToSave();
                if (noteToSave != null) {
                    intent.putExtra("saved_note", noteToSave);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });
    }

    private Note getNoteToSave() {
        Note note = noteToEdit == null ? new Note() : noteToEdit;

        if (!setTitle((note)) || !setDescription((note))) {
            return null;
        }

        setImportance(note);
        setDateTime(note);

        return note;
    }

    private void setViewValuesFromNote(Note note) {
        ((EditText) findViewById(R.id.NoteTitle)).setText(note.getTitle());
        ((EditText) findViewById(R.id.NoteDescription)).setText(note.getDescription());
        ((Spinner) findViewById(R.id.NoteImportance)).setSelection(note.getImportance().getValue());
        Date noteDate = note.getDate();
        ((DatePicker) findViewById(R.id.date_picker)).updateDate(noteDate.getYear(), noteDate.getMonth(), noteDate.getDay());
        ((TimePicker) findViewById(R.id.time_picker)).setCurrentHour(noteDate.getHours());
        ((TimePicker) findViewById(R.id.time_picker)).setCurrentMinute(noteDate.getMinutes());
    }

    private boolean setTitle(Note note) {
        EditText title = ((EditText) findViewById(R.id.NoteTitle));
        if (TextUtils.isEmpty(title.getText())) {
            title.setError(getResources().getString(R.string.title_required_error));
            return false;
        }
        note.setTitle(title.getText().toString());
        return true;
    }

    private boolean setDescription(Note note) {
        EditText description = ((EditText) findViewById(R.id.NoteDescription));
        if (TextUtils.isEmpty(description.getText())) {
            description.setError(getResources().getString(R.string.description_required_error));
            return false;
        }
        note.setDescription(description.getText().toString());
        return true;
    }

    private void setImportance(Note note) {
        String importance = ((Spinner) findViewById(R.id.NoteImportance)).getSelectedItem().toString();
        if (importance == getResources().getString(R.string.importance_high)) {
            note.setImportance(Importance.HIGH);
        } else if (importance == getResources().getString(R.string.importance_low)) {
            note.setImportance(Importance.LOW);
        } else {
            note.setImportance(Importance.NORMAL);
        }
    }

    private void setDateTime(Note note) {
        DatePicker datePicker = (DatePicker) findViewById(R.id.date_picker);
        TimePicker timePicker = (TimePicker) findViewById(R.id.time_picker);

        Calendar calendar = new GregorianCalendar(datePicker.getYear(),
                datePicker.getMonth(),
                datePicker.getDayOfMonth(),
                timePicker.getCurrentHour(),
                timePicker.getCurrentMinute());

        note.setDate(calendar.getTime());
    }
}
