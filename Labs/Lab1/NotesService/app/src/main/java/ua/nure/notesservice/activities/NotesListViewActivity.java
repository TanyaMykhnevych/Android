package ua.nure.notesservice.activities;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import ua.nure.notesservice.NotesListViewAdapter;
import ua.nure.notesservice.R;
import ua.nure.notesservice.models.Importance;
import ua.nure.notesservice.models.Note;

public class NotesListViewActivity extends AppCompatActivity {

    public static final String[] titles = new String[] { "Strawberry",
            "test test test test test test test test test test test test", "Orange", "Mixed" };

    public static final String[] descriptions = new String[] {
            "It is an aggregate accessory fruit",
            "It is the largest herbaceous flowering plant test test test test test test test test test test test test test test test test test test test test test test test test", "Citrus Fruit",
            "Mixed Fruits test test test test test test test test test test test test test test test test test test test test test test test test" };

    public static final String[] images = { "", "", "", "" };

    public static final Importance[] importances = { Importance.LOW, Importance.HIGH, Importance.NORMAL, Importance.LOW };

    ListView listView;
    List<Note> rowItems;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rowItems = new ArrayList<Note>();
        for (int i = 0; i < titles.length; i++) {
            Note item = new Note(titles[i], descriptions[i], importances[i], new Date(), images[i]);
            rowItems.add(item);
        }

        listView = (ListView) findViewById(R.id.list);
        NotesListViewAdapter adapter = new NotesListViewAdapter(this,
                R.layout.note, rowItems);
        listView.setAdapter(adapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.add:
                //add the function to perform here
                return(true);
        }
        return(super.onOptionsItemSelected(item));
    }
}
