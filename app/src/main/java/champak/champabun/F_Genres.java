package champak.champabun;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;

import champak.champabun.adapters.Adapter_playlist;
import champak.champabun.classes.SongDetails;

public class F_Genres extends BaseFragment {
    ListView mListView;
    Adapter_playlist adapter;
    ArrayList<SongDetails> Genredetails;
    ImageView backPager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            Genredetails = savedInstanceState.getParcelableArrayList("F_Artists.Artistdetails");
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.l_playlists, container, false);

        backPager = (ImageView) view.findViewById(R.id.backPager);
        backPager.setColorFilter(getResources().getColor(R.color.redPager), PorterDuff.Mode.MULTIPLY);
        mListView = (ListView) view.findViewById(R.id.PlayList);
        adapter = null;
        mListView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                Intent intent = new Intent("android.intent.action.ge");
                intent.putExtra("click_no", Genredetails.get(position).getClick_no());
                intent.putExtra("genre_title", Genredetails.get(position).getSong());
                startActivity(intent);
            }
        });
        registerForContextMenu(mListView);

        if (Genredetails == null || Genredetails.size() == 0) {
            // if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB)
            // {
            new FetchGenreList().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void) null);
            // }
            // else
            // {
            // new FetchArtistList().execute((Void) null);
            // }
        }
        return view;
    }

    private void OnRefreshListview() {
        if (adapter == null) {
            adapter = new Adapter_playlist(Genredetails);
            mListView.setAdapter(adapter);
        } else {
            adapter.OnUpdate(Genredetails);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        OnRefreshListview();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("F_Artists.Artistdetails", Genredetails);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (Genredetails != null) {
            Genredetails.clear();
            Genredetails = null;
        }
        // intent = null;
        // adapter.clearCache();
        // adapter = null;
        if (IConstant.USE_SYSTEM_GC) {
            System.gc();
        }
    }

    @Override
    public void Update() {
    }

    @Override
    protected String GetGAScreenName() {
        return "F_Genres";
    }

    class FetchGenreList extends AsyncTask<Void, Void, ArrayList<SongDetails>> {
        @Override
        protected ArrayList<SongDetails> doInBackground(Void... arg0) {
            ArrayList<SongDetails> details = new ArrayList<SongDetails>();

            // final String artist_id = MediaStore.Audio.Artists.ALBUM_ID;
            // final String album_name =MediaStore.Audio.Artists.ALBUM;
            String[] columns = {MediaStore.Audio.Genres._ID, MediaStore.Audio.Genres.NAME};

            Cursor cursor = null;
            try {
                cursor = getActivity().getContentResolver().query(MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI, columns, null, null, "name ASC");
                if (cursor != null && cursor.moveToFirst()) {
                    do {
                        Uri uri = MediaStore.Audio.Genres.Members.getContentUri("external", Long.parseLong(cursor.getString(0)));
                        String[] columns1 = {"DISTINCT " + MediaStore.Audio.Genres.Members.ALBUM_ID};
                        Cursor cursor1 = getActivity().getContentResolver().query(uri, columns1, null, null, null);
                        if (cursor1.getCount() > 0)
                            details.add(new SongDetails(cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID)), cursor.getString(0),
                                    cursor.getString(0), cursor.getString(1)));

                        if (cursor1 != null) {
                            cursor1.close();
                            cursor1 = null;
                        }
                    }
                    while (cursor.moveToNext());
                }
            } catch (Exception e) {
            } finally {
                if (cursor != null) {
                    cursor.close();
                    cursor = null;
                }
                columns = null;
            }

            return details;
        }

        @Override
        protected void onPostExecute(ArrayList<SongDetails> result) {
            super.onPostExecute(result);
            if (Genredetails != null) {
                Genredetails.clear();
            } else {
                Genredetails = new ArrayList<SongDetails>();
            }
            Genredetails.addAll(result);
            result.clear();
            OnRefreshListview();
        }
    }
}