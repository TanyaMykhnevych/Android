package ua.nure.notesapp.adapters;

import android.app.Activity;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import ua.nure.notesapp.models.Importance;
import ua.nure.notesapp.R;
import ua.nure.notesapp.helpers.ImageHelper;
import ua.nure.notesapp.models.Note;

public class NotesListViewAdapter extends ArrayAdapter<Note> {

    private Activity _context;
    private List<Note> _notesList;

    public NotesListViewAdapter(Activity context, int resourceId,
                                List<Note> items) {
        super(context, resourceId, items);
        this._context = context;
        this._notesList = items;

        getFilter();
    }

    private class ViewHolder {
        ImageView imageView;
        ImageView importance;
        TextView txtDate;
        TextView txtTitle;
        TextView txtDesc;
    }

    @Override
    public int getCount() {
        return _notesList.size();
    }

    @Override
    public Note getItem(int i) {
        return _notesList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        Note note = getItem(position);

        LayoutInflater mInflater = (LayoutInflater) _context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.note, null);
            holder = new ViewHolder();
            holder.txtDesc = (TextView) convertView.findViewById(R.id.description);
            holder.txtTitle = (TextView) convertView.findViewById(R.id.title);
            holder.imageView = (ImageView) convertView.findViewById(R.id.image);
            holder.importance = (ImageView) convertView.findViewById(R.id.importance);
            holder.txtDate = (TextView) convertView.findViewById(R.id.date);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        updateHolderData(holder, note);

        return convertView;
    }

    public void setNewNoteList(ArrayList<Note> newNotes) {
        _notesList = newNotes;
    }

    private void updateHolderData(ViewHolder holder, Note note) {
        holder.txtDesc.setText(note.getDescription());
        holder.txtTitle.setText(note.getTitle());
        holder.importance.setImageResource(note.getImportanceIcon());
        holder.txtDate.setText(new SimpleDateFormat("MM/dd/yyyy HH:mm").format(note.getDate()));

        ImageHelper.DrawImage(note.getImagePath(), _context, holder.imageView);
    }
}


