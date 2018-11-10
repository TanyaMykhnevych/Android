package ua.nure.notesapp;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ua.nure.notesapp.models.Note;

public class NotesListViewAdapter extends ArrayAdapter<Note> implements Filterable {

    private Context _context;
    private List<Note> _notesList;
    private List<Note> _filteredNotes;
    private NoteFilter _noteFilter;

    public NotesListViewAdapter(Context context, int resourceId,
                                List<Note> items) {
        super(context, resourceId, items);
        this._context = context;
        this._notesList = items;
        this._filteredNotes = items;

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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        Note note = getItem(position);

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = initViews(holder);
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

        // TODO: find solution for notes images
    }

    private View initViews(ViewHolder viewHolder) {
        LayoutInflater mInflater = (LayoutInflater) _context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        View convertView = mInflater.inflate(R.layout.note, null);
        ViewHolder holder = initViewHolder(convertView);
        convertView.setTag(holder);
        return convertView;
    }

    private ViewHolder initViewHolder(View convertView) {
        ViewHolder holder = new ViewHolder();
        holder.txtDesc = convertView.findViewById(R.id.description);
        holder.txtTitle = convertView.findViewById(R.id.title);
        holder.imageView = convertView.findViewById(R.id.icon);
        holder.importance = convertView.findViewById(R.id.importance);
        holder.txtDate = convertView.findViewById(R.id.date);
        return holder;
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
            if (constraint != null && constraint.length() > 0) {
                List<Note> tempList = new ArrayList<>();

                for (Note note : _notesList) {
                    String constraintLowerCase = constraint.toString().toLowerCase();
                    String noteDescription = note.getDescription().toLowerCase();
                    String noteTitle = note.getTitle().toLowerCase();
                    if (noteDescription.contains(constraintLowerCase) || noteTitle.contains(constraintLowerCase)) {
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


