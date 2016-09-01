package champak.champabun.view.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;

import java.util.ArrayList;

import champak.champabun.R;
import champak.champabun.business.dataclasses.SongDetails;
import champak.champabun.business.definition.Logger;
import champak.champabun.business.utilities.utilClass.TypefaceTextView;
import champak.champabun.business.utilities.utilMethod.Utilities;
import champak.champabun.view.activity.Album;
import champak.champabun.view.activity.Artist;
import champak.champabun.view.activity.Genre;
import champak.champabun.view.activity.Playlist;
import champak.champabun.view.fragment.F_Songs;

public class Adapter_SongView extends BaseAdapter implements Filterable {
    Context context;
    int _whichactivity;
    private ArrayList<SongDetails> _originalData;
    private ArrayList<SongDetails> _data;

    public Adapter_SongView(ArrayList<SongDetails> data, Context context, int whichactivity) {   //whichactivity 1 for album,2 for f_songs 3 for artist,4 for playlist
        _originalData = data;
        _data = data;
        _whichactivity = whichactivity;
    }

    public void OnUpdate(ArrayList<SongDetails> data) {
        _originalData = data;
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
        ViewHolder viewHolder = null;

        if (convertView == null) {
            context = parent.getContext();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_songs, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.SongView = (TypefaceTextView) convertView.findViewById(R.id.Song);
            viewHolder.TimeView = (TypefaceTextView) convertView.findViewById(R.id.Time);
            viewHolder.ArtistView = (TypefaceTextView) convertView.findViewById(R.id.Artist);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (position == 0) {
            if (_whichactivity == 2) {
                if (F_Songs.highlight_zero % 2 == 0) {
                    convertView.setBackgroundColor(Color.TRANSPARENT);
                } else if (F_Songs.highlight_zero % 2 == 1) {
                    convertView.setBackgroundColor(parent.getContext().getResources().getColor(R.color.multi_highlight));
                }
            }


            if (_whichactivity == 1) {
                if (Album.highlight_zero % 2 == 0) {
                    convertView.setBackgroundColor(Color.TRANSPARENT);
                } else if (Album.highlight_zero % 2 == 1) {
                    convertView.setBackgroundColor(parent.getContext().getResources().getColor(R.color.multi_highlight));
                }
            }


            if (_whichactivity == 3) {
                if (Artist.highlight_zero % 2 == 0) {
                    convertView.setBackgroundColor(Color.TRANSPARENT);
                } else if (Artist.highlight_zero % 2 == 1) {
                    convertView.setBackgroundColor(parent.getContext().getResources().getColor(R.color.multi_highlight));
                }
            }


            if (_whichactivity == 4) {
                if (Playlist.highlight_zero % 2 == 0) {
                    convertView.setBackgroundColor(Color.TRANSPARENT);
                } else if (Playlist.highlight_zero % 2 == 1) {
                    convertView.setBackgroundColor(parent.getContext().getResources().getColor(R.color.multi_highlight));
                }
            }

            if (_whichactivity == 5) {
                if (Genre.highlight_zero % 2 == 0) {
                    convertView.setBackgroundColor(Color.TRANSPARENT);
                } else if (Genre.highlight_zero % 2 == 1) {
                    convertView.setBackgroundColor(parent.getContext().getResources().getColor(R.color.multi_highlight));
                }
            }
        } else {
            if (((ListView) parent).isItemChecked(position)) {
                convertView.setBackgroundColor(parent.getContext().getResources().getColor(R.color.multi_highlight));
            } else {
                convertView.setBackgroundColor(Color.TRANSPARENT);
            }
        }

        viewHolder.SongView.setText(_data.get(position).getSong());
        viewHolder.TimeView.setText(_data.get(position).getTime());
        viewHolder.ArtistView.setText(_data.get(position).getsortBy());

        return convertView;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                Logger.d("F_Songs", "performFiltering.................. " + charSequence);
                FilterResults results = new FilterResults();

                // If there's nothing to filter on, return the original data for your list
                if (charSequence == null || charSequence.length() == 0) {
                    results.values = _originalData;
                    results.count = _originalData != null ? _originalData.size() : 0;
                } else {
                    ArrayList<SongDetails> _values_ = new ArrayList<SongDetails>();
                    for (int i = 0; i < _originalData.size(); i++) {
                        String key = Utilities.RemoveVietnameseStringSign(charSequence.toString());
                        key = Utilities.ToUpperCase(key);
                        String song = Utilities.RemoveVietnameseStringSign(_originalData.get(i).getSong());
                        song = Utilities.ToUpperCase(song);
                        String sortBy = Utilities.RemoveVietnameseStringSign(_originalData.get(i).getsortBy());
                        sortBy = Utilities.ToUpperCase(sortBy);

                        if (song.contains(key) || sortBy.contains(key)) {
                            _values_.add(_originalData.get(i));
                        }
                    }
                    results.values = _values_;
                    results.count = _values_.size();
                }
                return results;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                _data = (ArrayList<SongDetails>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public ArrayList<SongDetails> GetData() {
        return _data;
    }

    class ViewHolder {
        TypefaceTextView SongView, ArtistView;
        TypefaceTextView TimeView;
    }
}