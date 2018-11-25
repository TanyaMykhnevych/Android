package ua.nure.musicplayer.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.List;

import ua.nure.musicplayer.R;
import ua.nure.musicplayer.models.Audio;

public class PlayListAdapter extends ArrayAdapter<Audio> {

    private Activity _context;
    private List<Audio> _audioList;

    public PlayListAdapter(Activity context, int resourceId,
                                List<Audio> items) {
        super(context, resourceId, items);
        this._context = context;
        this._audioList = items;
    }

    private class ViewHolder {
        TextView txtTitle;
        TextView txtArtist;
        TextView txtAlbum;
    }

    @Override
    public int getCount() {
        return _audioList.size();
    }

    @Override
    public Audio getItem(int i) {
        return _audioList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        Audio audio = getItem(position);

        LayoutInflater mInflater = (LayoutInflater) _context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.playlist_item, null);
            holder = new ViewHolder();
            holder.txtArtist = (TextView) convertView.findViewById(R.id.artist);
            holder.txtTitle = (TextView) convertView.findViewById(R.id.title);
            holder.txtAlbum = (TextView) convertView.findViewById(R.id.album);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        updateHolderData(holder, audio);

        return convertView;
    }

    private void updateHolderData(ViewHolder holder, Audio audio) {
        holder.txtArtist.setText(audio.getArtist());
        holder.txtTitle.setText(audio.getTitle());
        holder.txtAlbum.setText(audio.getAlbum());
    }
}
