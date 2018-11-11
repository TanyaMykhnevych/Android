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

public class NotesListViewAdapter extends ArrayAdapter<Note> implements Filterable {

    private Activity _context;
    private List<Note> _notesList;
    private List<Note> _filteredNotes;
    private NoteFilter _noteFilter;
    private Importance _importanceFilterCriteria;

    public NotesListViewAdapter(Activity context, int resourceId,
                                List<Note> items) {
        super(context, resourceId, items);
        this._context = context;
        this._notesList = items;
        this._filteredNotes = items;

        getFilter();
    }

    public void updateNotesList(List<Note> notes) {
        _notesList = notes;
        notifyDataSetChanged();
    }

    public void updateImportanceFilter(Importance importance) {
        _importanceFilterCriteria = importance;
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
        return _filteredNotes.size();
    }

    @Override
    public Note getItem(int i) {
        return _filteredNotes.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public void setNoteList(ArrayList<Note> newNotes) {
        _notesList = newNotes;
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

    private void updateHolderData(ViewHolder holder, Note note) {
        holder.txtDesc.setText(note.getDescription());
        holder.txtTitle.setText(note.getTitle());
        holder.importance.setImageResource(note.getImportanceIcon());
        holder.txtDate.setText(new SimpleDateFormat("MM/dd/yyyy HH:mm").format(note.getDate()));

        ImageHelper.DrawImage(note.getImagePath(), _context, holder.imageView);

        return convertView;
    }

    @Override
    public Filter getFilter() {
        if (_noteFilter == null) {
            _noteFilter = new NoteFilter();
        }

        return _noteFilter;
    }

    private class NoteFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();

            Boolean stringConstraintPresented = constraint != null && constraint.length() > 0;
            Boolean importanceConstraintPresented = _importanceFilterCriteria != null;
            if (stringConstraintPresented || importanceConstraintPresented) {
                List<Note> tempList = new ArrayList<>();

                for (Note note : _notesList) {
                    String constraintLowerCase = constraint.toString().toLowerCase();
                    String noteDescription = note.getDescription().toLowerCase();
                    String noteTitle = note.getTitle().toLowerCase();
                    Boolean passedStringConstraint = !stringConstraintPresented
                            || noteDescription.contains(constraintLowerCase)
                            || noteTitle.contains(constraintLowerCase);
                    Boolean passedImportanceConstraint = !importanceConstraintPresented || note.getImportance() == _importanceFilterCriteria;
                    if (passedStringConstraint && passedImportanceConstraint) {
                        tempList.add(note);
                    }
                }

                filterResults.count = tempList.size();
                filterResults.values = tempList;
            } else {
                filterResults.count = _notesList.size();
                filterResults.values = _notesList;
            }

            return filterResults;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            _filteredNotes = (List<Note>) results.values;
            notifyDataSetChanged();
        }
    }
}


