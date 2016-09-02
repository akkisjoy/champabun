package champak.champabun.view.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import champak.champabun.R;
import champak.champabun.business.dataclasses.MediaFilesObject;
import champak.champabun.business.utilities.utilClass.TypefaceTextView;
import champak.champabun.view.fragment.F_Songs;

public class Adapter_Folder extends BaseAdapter {
    private ArrayList<MediaFilesObject> _data;
    private Context context;

    public Adapter_Folder(Context context, ArrayList<MediaFilesObject> data) {
        this.context = context;
        _data = data;
    }

    public void OnUpdate(ArrayList<MediaFilesObject> data) {
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

    public MediaFilesObject getItem(int position) {
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
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_songs, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.SongView = (TypefaceTextView) convertView.findViewById(R.id.Song);
            viewHolder.TimeView = (TypefaceTextView) convertView.findViewById(R.id.Time);
            viewHolder.ArtistView = (TypefaceTextView) convertView.findViewById(R.id.Artist);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (position == 0) {
            if (F_Songs.highlight_zero % 2 == 0) {
                convertView.setBackgroundColor(Color.TRANSPARENT);
            } else if (F_Songs.highlight_zero % 2 == 1) {
                convertView.setBackgroundColor(Color.parseColor("#7033b5e5"));
            }
        } else {
            if (((ListView) parent).isItemChecked(position)) {
                convertView.setBackgroundColor(Color.parseColor("#7033b5e5"));
            } else {
                convertView.setBackgroundColor(Color.TRANSPARENT);
            }
        }

        if (_data.get(position).isSongdetails()) {
            viewHolder.SongView.setText(_data.get(position).getSongdetails().getSong());
            viewHolder.TimeView.setText(_data.get(position).getSongdetails().getTime());
            viewHolder.ArtistView.setText(_data.get(position).getSongdetails().getsortBy());
        } else {
            viewHolder.SongView.setText(_data.get(position).getFolderName());
            viewHolder.ArtistView.setText("");
            viewHolder.TimeView.setText(String.format(context.getResources().getString(R.string.f_total_song), _data.get(position)
                    .getTotalSongs()));
        }

        return convertView;
    }

    class ViewHolder {
        TypefaceTextView SongView, ArtistView;
        TypefaceTextView TimeView;
    }
}