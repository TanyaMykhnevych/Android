package ua.nure.notesapp.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TimePicker;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import ua.nure.notesapp.R;
import ua.nure.notesapp.helpers.ImageHelper;
import ua.nure.notesapp.models.Importance;
import ua.nure.notesapp.models.Note;

public class NotePreviewActivity extends AppCompatActivity {
    private static final int SELECT_PICTURE = 1;
    private Note noteToEdit;
    Button btnSelectImageFromGalery;
    ImageView vImageDisplay;
    String currentImagePath;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note_preview);

        btnSelectImageFromGalery = (Button) findViewById(R.id.btnSelectImageFromGalery);
        btnSelectImageFromGalery.setOnClickListener(btnSelectImageFromGaleryListener);

        vImageDisplay = (ImageView) findViewById(R.id.imageViewEditForm);

        fillViewIfEdit();
        prepareSaveButton();

        Calendar c = Calendar.getInstance();
        ((DatePicker) findViewById(R.id.date_picker)).setMinDate(c.getTimeInMillis());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();
                currentImagePath = selectedImageUri.toString();
                ImageHelper.DrawImage(currentImagePath, this, vImageDisplay);
            }
        }
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

        note.setImagePath(currentImagePath);
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
        ImageHelper.DrawImage(note.getImagePath(), this, vImageDisplay);
        currentImagePath = note.getImagePath();
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

    private View.OnClickListener btnSelectImageFromGaleryListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent,
                    "Select Picture"), SELECT_PICTURE);
        }
    };
}
