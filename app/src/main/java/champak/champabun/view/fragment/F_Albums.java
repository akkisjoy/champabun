package champak.champabun.view.fragment;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;

import com.romainpiel.shimmer.Shimmer;
import com.romainpiel.shimmer.ShimmerTextView;

import java.util.ArrayList;

import champak.champabun.R;
import champak.champabun.business.dataclasses.SongDetails;
import champak.champabun.iloader.LazyAdapter;
import champak.champabun.view.activity.MainActivity;

import static champak.champabun.view.activity.MainActivity.miniBack;

public class F_Albums extends BaseFragment {
    GridView list;
    LazyAdapter adapter;
    ArrayList<SongDetails> songdetails;
    ImageView backPager;
    private ShimmerTextView titleHeader;
    private Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            songdetails = savedInstanceState.getParcelableArrayList("F_Albums.songdetails");
        } else {
            songdetails = new ArrayList<>();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (isVisibleToUser) {
            new Shimmer().setRepeatCount(0)
                    .setDuration(2000)
                    .setStartDelay(100)
                    .start(titleHeader);

//            ImageView miniBack = (ImageView) getActivity().findViewById(R.id.miniBack);
            miniBack.setColorFilter(ContextCompat.getColor(MainActivity.getMainActivity(), R.color.pinkPager), PorterDuff.Mode.OVERLAY);
        }
        super.setUserVisibleHint(isVisibleToUser);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.l_albums, container, false);
        context = getActivity().getApplicationContext();
        backPager = (ImageView) view.findViewById(R.id.backPager);
        backPager.setColorFilter(ContextCompat.getColor(getActivity(), R.color.bluePager), PorterDuff.Mode.MULTIPLY);

        titleHeader = (ShimmerTextView) view.findViewById(R.id.titleHeader);

        list = (GridView) view.findViewById(R.id.grid_view);
        adapter = null;
        list.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                Intent intent = new Intent("android.intent.action.al");
                intent.putExtra("click_no", songdetails.get(position).getClick_no());
                intent.putExtra("image_path", songdetails.get(position).getPath2());
                intent.putExtra("name", songdetails.get(position).getSong());
                intent.putExtra("artist", songdetails.get(position).getArtist());
                startActivity(intent);
            }
        });

        if (songdetails == null || songdetails.size() == 0) {
            new FetchAlbumList().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void) null);
        }

        return view;
    }

    private void OnRefreshListview() {
        if (adapter == null) {
            adapter = new LazyAdapter(context, songdetails);
            list.setAdapter(adapter);
        } else {
            adapter.OnUpdate(songdetails);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        OnRefreshListview();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("F_Albums.songdetails", songdetails);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        adapter.clearCache();
        adapter = null;
    }

    @Override
    public void Update() {
    }

    @Override
    protected String GetGAScreenName() {
        return "F_Albums";
    }

    class FetchAlbumList extends AsyncTask<Void, Void, ArrayList<SongDetails>> {
        @Override
        protected ArrayList<SongDetails> doInBackground(Void... arg0) {
            ArrayList<SongDetails> songs = new ArrayList<>();

            String[] columns = new String[]{MediaStore.Audio.Albums._ID, MediaStore.Audio.Albums.ALBUM, MediaStore.Audio.Albums.ALBUM_ART,
                    MediaStore.Audio.Albums.ARTIST, MediaStore.MediaColumns._ID};
            Cursor cursor = null;
            try {
                cursor = getActivity().getContentResolver().query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, columns, null, null,
                        MediaStore.Audio.Albums.ALBUM + " COLLATE NOCASE ASC");
                if (cursor != null && cursor.moveToFirst()) {
                    do {
                        int x = cursor.getPosition();
                        String uri = cursor.getString(2);
                        if (uri == null || uri.equals("")) {
                            uri = "asdasdad" + x;
                        }
                        songs.add(new SongDetails(cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID)), cursor.getString(0),
                                cursor.getString(3), cursor.getString(1), uri));
                    }
                    while (cursor.moveToNext());
                }
            } catch (SQLiteException ignored) {
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }

            return songs;
        }

        @Override
        protected void onPostExecute(ArrayList<SongDetails> result) {
            super.onPostExecute(result);
            if (songdetails != null) {
                songdetails.clear();
            } else {
                songdetails = new ArrayList<>();
            }
            songdetails.addAll(result);
            result.clear();
            OnRefreshListview();
        }
    }
}