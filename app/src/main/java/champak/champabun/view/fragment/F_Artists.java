package champak.champabun.view.fragment;

import android.content.Intent;
import android.database.Cursor;
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
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;

import champak.champabun.R;
import champak.champabun.business.dataclasses.SongDetails;
import champak.champabun.business.definition.IConstant;
import champak.champabun.view.adapters.Adapter_playlist;

public class F_Artists extends BaseFragment {
    ListView mListView;
    Adapter_playlist adapter;
    ArrayList<SongDetails> Artistdetails;
    ImageView backPager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            Artistdetails = savedInstanceState.getParcelableArrayList("F_Artists.Artistdetails");
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.l_artists, container, false);
        backPager = (ImageView) view.findViewById(R.id.backPager);
        backPager.setColorFilter(ContextCompat.getColor(getActivity(), R.color.yellowPager), PorterDuff.Mode.MULTIPLY);

        mListView = (ListView) view.findViewById(R.id.PlayList);
        adapter = null;
        mListView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                Intent intent = new Intent("android.intent.action.ar");
                intent.putExtra("click_no", Artistdetails.get(position).getClick_no());
                startActivity(intent);
            }
        });
        registerForContextMenu(mListView);

        if (Artistdetails == null || Artistdetails.size() == 0) {
            new FetchArtistList().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void) null);
        }
        return view;
    }

    private void OnRefreshListview() {
        if (adapter == null) {
            adapter = new Adapter_playlist(Artistdetails);
            mListView.setAdapter(adapter);
        } else {
            adapter.OnUpdate(Artistdetails);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        OnRefreshListview();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("F_Artists.Artistdetails", Artistdetails);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (Artistdetails != null) {
            Artistdetails.clear();
            Artistdetails = null;
        }
        if (IConstant.USE_SYSTEM_GC) {
            System.gc();
        }
    }

    @Override
    public void Update() {
    }

    @Override
    protected String GetGAScreenName() {
        return "F_Artists";
    }

    class FetchArtistList extends AsyncTask<Void, Void, ArrayList<SongDetails>> {
        @Override
        protected ArrayList<SongDetails> doInBackground(Void... arg0) {
            ArrayList<SongDetails> details = new ArrayList<>();

            String[] columns = {MediaStore.Audio.Artists._ID, MediaStore.Audio.Artists.ARTIST, MediaStore.MediaColumns._ID};

            Cursor cursor = null;
            try {
                cursor = getActivity().getContentResolver().query(MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI, columns, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    do {
                        details.add(new SongDetails(cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID)), cursor.getString(0),
                                cursor.getString(0), cursor.getString(1)));
                    }
                    while (cursor.moveToNext());
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }

            return details;
        }

        @Override
        protected void onPostExecute(ArrayList<SongDetails> result) {
            super.onPostExecute(result);
            if (Artistdetails != null) {
                Artistdetails.clear();
            } else {
                Artistdetails = new ArrayList<>();
            }
            Artistdetails.addAll(result);
            result.clear();
            OnRefreshListview();
        }
    }
}