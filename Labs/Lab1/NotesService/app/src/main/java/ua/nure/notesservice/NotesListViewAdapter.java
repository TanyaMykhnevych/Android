package ua.nure.notesservice;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import ua.nure.notesservice.models.Note;

public class NotesListViewAdapter extends ArrayAdapter<Note> {

    Context context;

    public NotesListViewAdapter(Context context, int resourceId,
                                 List<Note> items) {
        super(context, resourceId, items);
        this.context = context;
    }

    private class ViewHolder {
        ImageView imageView;
        ImageView importance;
        TextView txtDate;
        TextView txtTitle;
        TextView txtDesc;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        Note note = getItem(position);

        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.note, null);
            holder = new ViewHolder();
            holder.txtDesc = (TextView) convertView.findViewById(R.id.description);
            holder.txtTitle = (TextView) convertView.findViewById(R.id.title);
           // holder.imageView = (ImageView) convertView.findViewById(R.id.icon);
            holder.importance = (ImageView) convertView.findViewById(R.id.importance);
            holder.txtDate = (TextView) convertView.findViewById(R.id.date);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();

        holder.txtDesc.setText(note.getDescription());
        holder.txtTitle.setText(note.getTitle());
        holder.importance.setImageResource(note.getImportanceIcon());
        holder.txtDate.setText(new SimpleDateFormat("MM/dd/yyyy HH:mm").format(new Date()).toString());

        // TODO: find solution for notes images

        return convertView;
    }
}

