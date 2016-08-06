package champak.champabun.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

import champak.champabun.R;
import champak.champabun.classes.SongDetails;
import champak.champabun.ui.TypefaceTextView;

public class Adapter_playlist extends BaseAdapter {
    private ArrayList<SongDetails> _data;
    Context context;

    public Adapter_playlist(ArrayList<SongDetails> data) {
        _data = data;
    }

    public void OnUpdate(ArrayList<SongDetails> data) {
        _data = data;
        notifyDataSetChanged();
    }

    public int getCount() {
        if (_data == null) {
            return 0;
        } else {
            return _data.size();
        }
    }

    public SongDetails getItem(int position) {
        return _data.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            context = parent.getContext();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_songs, parent, false);

            holder.SongView = (TypefaceTextView) convertView.findViewById(R.id.Song);
            convertView.findViewById(R.id.ArtistLayout).setVisibility(View.GONE);
            // holder.TimeView = (TextView) convertView.findViewById(R.id.Time);
            // holder.ArtistView = (TextView) convertView.findViewById(R.id.Artist);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.SongView.setText(_data.get(position).getSong());
        // holder.TimeView.setText(_data.get(position).getTime());
        // holder.ArtistView.setText(_data.get(position).getArtist());

        return convertView;
    }

    class ViewHolder {
        TypefaceTextView SongView;
        // TextView TimeView, ArtistView;
    }
}