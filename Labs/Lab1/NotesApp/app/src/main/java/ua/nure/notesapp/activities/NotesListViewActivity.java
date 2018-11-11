package ua.nure.notesapp.activities;

import android.app.SearchManager;
import android.content.Context;
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
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

import java.util.Locale;

import ua.nure.notesapp.adapters.NotesListViewAdapter;
import ua.nure.notesapp.stores.NotesStore;
import ua.nure.notesapp.R;
import ua.nure.notesapp.models.Note;

public class NotesListViewActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    private final static int REQUEST_CODE_1 = 1;

    ListView listView;
    NotesStore _store;
    NotesListViewAdapter adapter;
    Menu optionsMenu;
    String locale = "en";

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.notes_list_view);
        setTitle(R.string.app_name);

        _store = new NotesStore();

        listView = findViewById(R.id.list);
        adapter = new NotesListViewAdapter(this, R.layout.note, _store.getNotes());
        listView.setAdapter(adapter);

        registerForContextMenu(listView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        optionsMenu = menu;
        setLanguageSpinner();
        setImportanceSpinner();
        setSearchView();

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
        Note note =  adapter.getItem(info.position);

        if (item.getItemId() == R.id.edit) {
            Intent intent = new Intent(this, NotePreviewActivity.class);
            intent.putExtra("note_to_edit", note);
            this.startActivityForResult(intent, REQUEST_CODE_1);
        } else if (item.getItemId() == R.id.delete) {
            _store.remove(note);
            adapter.notifyDataSetChanged();
        } else {
            return false;
        }
        return true;
    }

    public void setLocale(boolean changeCong) {
        Locale myLocale = new Locale(locale);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        if (changeCong) onConfigurationChanged(conf);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        setLocale(false);
        MenuItem addItem = optionsMenu.findItem(R.id.add);
        addItem.setTitle(getResources().getString(R.string.add_note));
        setTitle(R.string.app_name);
        setLanguageSpinner();
        setImportanceSpinner();

        super.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        adapter.getFilter().filter(s);
        return true;
    }

    private void setLanguageSpinner() {
        MenuItem item = optionsMenu.findItem(R.id.languageSpinner);
        Spinner spinner = (Spinner) item.getActionView();

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.languages, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                if (pos == 1) {
                    locale = "uk";
                    setLocale(true);
                    Toast.makeText(parent.getContext(), getResources().getString(R.string.choose_language_message), Toast.LENGTH_SHORT)
                            .show();
                } else if (pos == 2) {
                    locale = "en";
                    setLocale(true);
                    Toast.makeText(parent.getContext(), getResources().getString(R.string.choose_language_message), Toast.LENGTH_SHORT)
                            .show();
                }
            }

            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
    }

    private void setImportanceSpinner() {
        MenuItem item = optionsMenu.findItem(R.id.importanceSpinner);
        Spinner spinner = (Spinner) item.getActionView();

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.importance, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            }

            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
    }

    private void setSearchView() {
        SearchManager searchManager = (SearchManager)
                getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchMenuItem = optionsMenu.findItem(R.id.app_bar_search);
        SearchView searchView = (SearchView) searchMenuItem.getActionView();

        searchView.setSearchableInfo(searchManager.
                getSearchableInfo(getComponentName()));
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(this);
    }

}

