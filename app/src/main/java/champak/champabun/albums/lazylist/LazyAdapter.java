package champak.champabun.albums.lazylist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.ArrayList;

import champak.champabun.R;
import champak.champabun.classes.SongDetails;
import champak.champabun.ui.TypefaceTextView;

public class LazyAdapter extends BaseAdapter {
    private ArrayList<SongDetails> data;
    Context x;
    public ImageLoader imageLoader;

    static class ViewHolder {
        TypefaceTextView text;
        ImageView image;
    }

    public LazyAdapter(Context parent, ArrayList<SongDetails> songdetails) {
        x = parent;
        data = songdetails;
        imageLoader = new ImageLoader(parent.getApplicationContext());
    }

    public void OnUpdate(ArrayList<SongDetails> songdetails) {
        data = songdetails;
        notifyDataSetChanged();
    }

    public int getCount() {
        if (data == null) {
            return 0;
        } else {
            return data.size();
        }
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public void clearCache() {
        imageLoader.clearMCache();
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder = null;
        // used view holder pattern to increase performance
        if (convertView == null) {
            x = parent.getContext();
            convertView = LayoutInflater.from(x).inflate(R.layout.item_album, parent, false);
            viewHolder = new ViewHolder();

            viewHolder.text = (TypefaceTextView) convertView.findViewById(R.id.text);
            viewHolder.image = (ImageView) convertView.findViewById(R.id.image);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.text.setText(data.get(position).getSong());
        imageLoader.DisplayImage(data.get(position).getPath2(), viewHolder.image, data.get(position).getSong()
                , data.get(position).getArtist());

        return convertView;
    }
}