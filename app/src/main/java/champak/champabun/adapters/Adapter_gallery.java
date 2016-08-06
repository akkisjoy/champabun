package champak.champabun.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.ArrayList;

import champak.champabun.R;
import champak.champabun.albums.lazylist.ImageLoader;
import champak.champabun.classes.SongDetails;
import champak.champabun.ui.TypefaceTextView;
import champak.champabun.util.BitmapUtil;

public class Adapter_gallery extends BaseAdapter {
    private ArrayList<SongDetails> _data;
    Context context;
    public ImageLoader imageLoader;
    ViewHolder holder = null;
    int wt_px = 300;
    //String artist;

    public Adapter_gallery(ArrayList<SongDetails> data) {
        _data = data;
    }

    public Adapter_gallery(ArrayList<SongDetails> data, String artist) {
        //this.artist = new String();
        _data = data;
        //this.artist = artist;

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

        if (convertView == null) {

            holder = new ViewHolder();
            context = parent.getContext();
            imageLoader = new ImageLoader(context);
            convertView = LayoutInflater.from(context).inflate(R.layout.item_artist_albums, parent, false);

            holder.albumart = (ImageView) convertView.findViewById(R.id.albumart);
            holder.albumname = (TypefaceTextView) convertView.findViewById(R.id.albumname);

            //convertView.findViewById(R.id.ArtistLayout).setVisibility(View.GONE);
            // holder.TimeView = (TextView) convertView.findViewById(R.id.Time);
            // holder.ArtistView = (TextView) convertView.findViewById(R.id.Artist);
            wt_px = (int) context.getResources().getDimension(R.dimen.player_image_width);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.albumname.setText(_data.get(position).getAlbum());
        Bitmap album = imageLoader.fetchfilefromcahce(_data.get(position).getAlbum(), _data.get(position).getArtist());
        if (album == null)
            album = BitmapUtil.SetAlbumArtBG(context, _data.get(position).getPath2(), _data.get(position).getAlbum(), _data.get(position).getArtist());
        if (album == null)
            album = BitmapUtil.GetRandomBitmap(context.getResources(), 0,
                    wt_px, wt_px);

        Drawable d = new BitmapDrawable(context.getResources(), album);
        holder.albumart.setImageDrawable(d);
        //imageLoader.DisplayImage(_data.get(position).getPath2(), holder.albumart, _data.get(position).getAlbum(), _data.get(position).getArtist());

        return convertView;
    }

    class ViewHolder {

        TypefaceTextView albumname;
        ImageView albumart;
        // TextView TimeView, ArtistView;
    }
}