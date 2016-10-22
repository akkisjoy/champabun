package champak.champabun.view.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import champak.champabun.R;
import champak.champabun.business.dataclasses.SongDetails;

public class Adapter_playlist extends BaseAdapter {
    Context context;
    private ArrayList<SongDetails> _data;

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
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            context = parent.getContext();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_songs, parent, false);

            holder.SongView = (TextView) convertView.findViewById(R.id.Song);
            convertView.findViewById(R.id.ArtistLayout).setVisibility(View.GONE);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.SongView.setText(_data.get(position).getSong());

        return convertView;
    }

    class ViewHolder {
        TextView SongView;
    }
}