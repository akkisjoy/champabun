package champak.champabun.adapters;

import android.content.Context;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import champak.champabun.F_Songs;
import champak.champabun.R;
import champak.champabun.classes.SongDetails;
import champak.champabun.ui.TypefaceTextView;

public class Adapter_ListView extends BaseAdapter {
    private ArrayList<SongDetails> _data;
    // album;
    Context context;

    static class ViewHolder {
        TypefaceTextView SongView, ArtistView;
        TextView TimeView;
    }

    public Adapter_ListView(ArrayList<SongDetails> data, DisplayMetrics metrics) {
        _data = data;
    }

    public Adapter_ListView(ArrayList<SongDetails> data, Context context) {
        _data = data;
        // Antipasto_regular
        // varela and station streh timeburner dosis

        // album = Typeface.createFromAsset(context.getAssets(), "fonts/"
        // + "Bellota-Bold.otf"
        // "Comic.ttf"
        // "Antipasto_regular.otf"
        //);
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
        ViewHolder viewHolder;

        if (convertView == null) {
            context = parent.getContext();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_songs, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.SongView = (TypefaceTextView) convertView.findViewById(R.id.Song);
            viewHolder.TimeView = (TextView) convertView.findViewById(R.id.Time);
            viewHolder.ArtistView = (TypefaceTextView) convertView.findViewById(R.id.Artist);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        SongDetails sng = _data.get(position);
        ListView list = (ListView) parent;
        if (position == 0) {
            if (F_Songs.highlight_zero % 2 == 0) {
                convertView.setBackgroundColor(Color.TRANSPARENT);
            } else if (F_Songs.highlight_zero % 2 == 1) {
                convertView.setBackgroundColor(Color.parseColor("#7033b5e5"));
            }
        } else {
            if (list.isItemChecked(position)) {
                convertView.setBackgroundColor(Color.parseColor("#7033b5e5"));
            } else {
                convertView.setBackgroundColor(Color.TRANSPARENT);
            }
        }

        viewHolder.SongView.setText(sng.getSong());
        viewHolder.TimeView.setText(sng.getTime());
        viewHolder.ArtistView.setText(sng.getArtist());

        return convertView;
    }
}