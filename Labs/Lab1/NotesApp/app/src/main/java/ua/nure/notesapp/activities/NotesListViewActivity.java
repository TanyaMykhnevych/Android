package ua.nure.notesapp.activities;

import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.content.res.Configuration;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;
import java.util.Locale;

import ua.nure.notesapp.NotesListViewAdapter;
import ua.nure.notesapp.NotesStore;
import ua.nure.notesapp.R;
import ua.nure.notesapp.models.Note;

public class NotesListViewActivity extends AppCompatActivity {
    private final static int REQUEST_CODE_1 = 1;

    Spinner spinnerctrl;
    ListView listView;
    NotesStore _store;
    NotesListViewAdapter adapter;
    Menu optionsMenu;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.notes_list_view);
        setTitle(R.string.app_name);

        _store = new NotesStore();

        listView = (ListView) findViewById(R.id.list);
        adapter = new NotesListViewAdapter(this, R.layout.note, _store.getNotes());
        listView.setAdapter(adapter);

        registerForContextMenu(listView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        optionsMenu = menu;
        setLanguageSpinner();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add:
                Intent intent = new Intent(this, NotePreviewActivity.class);
                this.startActivityForResult(intent, REQUEST_CODE_1);
                return true;
        }
        return (super.onOptionsItemSelected(item));
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent dataIntent) {
        switch (requestCode) {
            case REQUEST_CODE_1:
                if (resultCode == RESULT_OK) {
                    Note savedNote = (Note) dataIntent.getSerializableExtra("saved_note");
                    if (savedNote != null) {
                        _store.addOrUpdate(savedNote);
                        adapter.notifyDataSetChanged();
                    }
                }
        }
        super.onActivityResult(requestCode, resultCode, dataIntent);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int listPosition = info.position;

        if (item.getItemId() == R.id.edit) {
            Intent intent = new Intent(this, NotePreviewActivity.class);
            intent.putExtra("note_to_edit", _store.get(listPosition));
            this.startActivityForResult(intent, REQUEST_CODE_1);
        } else if (item.getItemId() == R.id.delete) {
            _store.remove(listPosition);
            adapter.notifyDataSetChanged();
        } else {
            return false;
        }
        return true;
    }

    public void setLocale(String lang) {
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        onConfigurationChanged(conf);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        MenuItem addItem = optionsMenu.findItem(R.id.add);
        addItem.setTitle(getResources().getString(R.string.add_note));
        setTitle(R.string.app_name);
        setLanguageSpinner();

        super.onConfigurationChanged(newConfig);
    }

    private void setLanguageSpinner(){
        MenuItem item = optionsMenu.findItem(R.id.languageSpinner);
        Spinner spinner = (Spinner) item.getActionView();

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.languages, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                if (pos == 1) {
                    Toast.makeText(parent.getContext(), "Ви обрали українську", Toast.LENGTH_SHORT)
                            .show();
                    setLocale("uk");
                } else if (pos == 2) {
                    Toast.makeText(parent.getContext(), "You have selected English", Toast.LENGTH_SHORT)
                            .show();
                    setLocale("en");
                }
            }

            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });
    }
}
