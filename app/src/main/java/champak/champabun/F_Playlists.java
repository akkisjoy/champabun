package champak.champabun;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import champak.champabun.adapters.Adapter_playlist;
import champak.champabun.classes.SongDetails;
import champak.champabun.util.PlayMeePreferences;
import champak.champabun.util.SongHelper;
import champak.champabun.util.Utilities;

public class F_Playlists extends BaseFragment implements SongHelper.OnQuickActionItemSelectListener {
    ImageView backPager;
    private ListView mListView;
    private Adapter_playlist ab;
    private ArrayList<SongDetails> Playlists;
    private SongHelper songHelper;
    private int curPosition;
    private Dialog dialog;
    private PlayMeePreferences prefs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            Playlists = savedInstanceState.getParcelableArrayList("F_Playlists.Playlists");
            curPosition = savedInstanceState.getInt("curPosition");
        }
        prefs = new PlayMeePreferences(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.l_playlists, container, false);
        backPager = (ImageView) view.findViewById(R.id.backPager);
        backPager.setColorFilter(getResources().getColor(R.color.greenPager), PorterDuff.Mode.MULTIPLY);
        mListView = (ListView) view.findViewById(R.id.PlayList);
        ab = null;
        mListView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                Intent intent = new Intent("android.intent.action.pl");
                intent.putExtra("click_no_playlist", Playlists.get(position).getClick_no());
                intent.putExtra("name", Playlists.get(position).getSong());
                startActivity(intent);
            }
        });

        mListView.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                curPosition = arg2;
                if (songHelper == null) {
                    songHelper = new SongHelper();
                }
                songHelper.ShowF_Playlist_QA(getActivity(), getView(), F_Playlists.this);
                return false;
            }
        });

        if (Playlists == null || Playlists.size() == 0) {
            // if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB)
            // {
            new FetchPlayList().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void) null);
            // }
            // else
            // {
            // new FetchPlayList().execute((Void) null);
            // }
        }

        return view;
    }

    private void OnRefreshListview() {
        if (ab == null) {
            ab = new Adapter_playlist(Playlists);
            mListView.setAdapter(ab);
        } else {
            ab.OnUpdate(Playlists);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (GlobalSongList.GetInstance().isPlaylistAdded) {
            // if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB)
            // {
            new FetchPlayList().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void) null);
            // }
            // else
            // {
            // new FetchPlayList().execute((Void) null);
            // }
            GlobalSongList.GetInstance().isPlaylistAdded = false;
        } else {
            OnRefreshListview();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("F_Playlists.Playlists", Playlists);
        outState.putInt("curPosition", curPosition);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // PlayList = null;
        // ab = null;
        // intent = null;
        if (IConstant.USE_SYSTEM_GC) {
            System.gc();
        }
    }

    @Override
    public void QuickAction_OnPlaySong() {
        songHelper.PlaySong(GetSonglist(), 0);
    }

    @Override
    public void QuickAction_OnAdd2Playlist() {
        // never call for this case
    }

    @Override
    public void QuickAction_OnEditTags() {
        dialog = songHelper.GetDialog(210);

        LayoutInflater li = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = li.inflate(R.layout.dialog_edit_playlist, null, false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(v);

        EditText editText = (EditText) dialog.findViewById(R.id.ti);
        editText.setText(Playlists.get(curPosition).getSong());
        editText.requestFocus(Playlists.get(curPosition).getSong().length());

        Button okButton = (Button) dialog.findViewById(R.id.dBOK);
        okButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                EditText editText = (EditText) dialog.findViewById(R.id.ti);
                String name = editText.getText().toString();
                if (!Utilities.isEmpty(name) && !name.equals(Playlists.get(curPosition).getSong())) {
                    if (prefs.GetPlaylistRecentName().equals(Playlists.get(curPosition).getSong()))// "Recently Added"
                    {
                        Playlists.get(curPosition).setSong(name);
                        prefs.SetPlaylistRecentName(name);
                    } else {
                        ContentValues values = new ContentValues();
                        values.put(MediaStore.Audio.Playlists.NAME, name);
                        getActivity().getContentResolver().update(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, values,
                                MediaStore.Audio.Playlists._ID + "=?", new String[]{Playlists.get(curPosition).getClick_no()});
                        Playlists.get(curPosition).setSong(name);
                    }
                    OnRefreshListview();
                }
                dialog.dismiss();
            }
        });

        Button cButton = (Button) dialog.findViewById(R.id.dBCancel);
        cButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    @Override
    public void QuickAction_OnSetAsRingtone() {
        // never call for this case
    }

    @Override
    public void QuickAction_OnViewDetails() {
        // never call for this case
    }

    @Override
    public void QuickAction_OnDeleteSong() {
        if (prefs.GetPlaylistRecentName().equals(Playlists.get(curPosition).getSong()))// "Recently Added"
        {
            // do nothing
            return;
        }

        // delete playlist
        dialog = songHelper.GetDialog(210);

        LayoutInflater li = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = li.inflate(R.layout.dialoge_delete, null, false);
        dialog.setContentView(v);
        dialog.setTitle(getString(R.string.delete));
        TextView title = (TextView) v.findViewById(R.id.title);
        title.setText(String.format(getString(R.string.f_confirm_delete), Playlists.get(curPosition).getSong()));
        Button cancel = (Button) v.findViewById(R.id.dBCancel);

        cancel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        Button ok = (Button) v.findViewById(R.id.dBOK);
        ok.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                getActivity().getContentResolver().delete(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, MediaStore.Audio.Playlists._ID + "=?",
                        new String[]{Playlists.get(curPosition).getClick_no()});
                Playlists.remove(curPosition);
                OnRefreshListview();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    public void QuickAction_OnRemoveSong() {
        // never call for this case
    }

    @Override
    public void QuickAction_OnSendSong() {
        // never call for this case
    }

    private ArrayList<SongDetails> GetSonglist() {
        ArrayList<SongDetails> play = null;
        if (Playlists.get(curPosition).getClick_no() == null && prefs.GetPlaylistRecentName().equals(Playlists.get(curPosition).getSong())) {
            play = sort(MediaStore.Audio.Media.DATE_ADDED + " COLLATE NOCASE " + " DESC");
        } else {
            try {
                play = GetSongs(Integer.parseInt(Playlists.get(curPosition).getClick_no()));
            } catch (NumberFormatException e) {
                play = GetSongs(0);

            } catch (Exception e) {
            }
        }
        return play;
    }

    private ArrayList<SongDetails> sort(String sortOrder) {
        ArrayList<SongDetails> play = null;
        Cursor songCursor = null;
        int duration = ((BaseActivity) getActivity()).appSettings.getDurationFilterTime();
        try {
            String[] TRACK_COLUMNS = new String[]{MediaStore.Audio.AudioColumns.ALBUM, MediaStore.Audio.AudioColumns.ARTIST,
                    MediaStore.MediaColumns.DATA, MediaStore.MediaColumns.DISPLAY_NAME, MediaStore.Audio.AudioColumns.DURATION,
                    MediaStore.Audio.AudioColumns.TRACK, MediaStore.Audio.AudioColumns.YEAR, MediaStore.MediaColumns.SIZE,
                    MediaStore.MediaColumns.TITLE, MediaStore.Audio.Media._ID, MediaStore.Audio.Media.DATE_ADDED,
                    MediaStore.Audio.AudioColumns.ALBUM_ID};
            songCursor = getActivity().getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, TRACK_COLUMNS,
                    MediaStore.Audio.Media.DURATION + ">=" + duration, null, sortOrder);
            if (songCursor != null && songCursor.moveToFirst()) {
                play = new ArrayList<SongDetails>();
                int i = 0;
                do {
                    i = i + 1;
                    play.add(new SongDetails(songCursor.getInt(songCursor.getColumnIndex(MediaStore.Audio.Media._ID)), null, songCursor
                            .getString(0), songCursor.getString(1), songCursor.getString(2), Utilities.getTime(songCursor.getString(4)),
                            songCursor.getString(8), songCursor.getString(1), 0));
                    if (i == 25) {
                        break;
                    }
                }
                while (songCursor.moveToNext());

            }
            TRACK_COLUMNS = null;
        } finally {
            if (songCursor != null) {
                songCursor.close();
                songCursor = null;
            }
        }
        return play;
    }

    private ArrayList<SongDetails> GetSongs(int s) {
        ArrayList<SongDetails> _play = null;

        Uri uri2 = MediaStore.Audio.Playlists.Members.getContentUri("external", s);

        String[] projection1 = {MediaStore.Audio.Playlists.Members.AUDIO_ID, MediaStore.Audio.Playlists.Members.ARTIST,
                MediaStore.Audio.Playlists.Members.TITLE, MediaStore.Audio.Playlists.Members._ID, MediaStore.Audio.Playlists.Members.DATA,
                MediaStore.Audio.Playlists.Members.ALBUM, MediaStore.Audio.Playlists.Members.DURATION,};
        Cursor cursor = null;

        try {
            cursor = getActivity().getContentResolver().query(uri2, projection1, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                _play = new ArrayList<SongDetails>();

                do {
                    SongDetails pl = new SongDetails();

                    pl.setSong(cursor.getString(2));
                    pl.setPath2(cursor.getString(4));
                    pl.setArtist(cursor.getString(1));
                    pl.setAlbum(cursor.getString(5));
                    pl.setTime(cursor.getString(6));
                    pl.setAudioID(cursor.getString(0));
                    try {
                        int intTime = Integer.parseInt(cursor.getString(6));
                        int newTime = intTime / 1000;
                        int newTimeMinutes = newTime / 60;
                        int newTimeSeconds = newTime % 60;
                        String max2;
                        if (newTimeSeconds < 10) {
                            max2 = newTimeMinutes + ":0" + newTimeSeconds;
                        } else {
                            max2 = newTimeMinutes + ":" + newTimeSeconds;
                        }
                        pl.setTime(max2);
                    } catch (Exception e) {
                    }

                    // pl.Path=cursor.getString(1);
                    // pl.Album=cursor.getString(0);
                    _play.add(pl);
                }
                while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        return _play;
    }

    @Override
    public void Update() {
        new FetchPlayList().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void) null);
    }

    @Override
    protected String GetGAScreenName() {
        return "F_Playlists";
    }

    class FetchPlayList extends AsyncTask<Void, Void, ArrayList<SongDetails>> {
        @Override
        protected ArrayList<SongDetails> doInBackground(Void... arg0) {
            ArrayList<SongDetails> playlists = new ArrayList<SongDetails>();
            SongDetails Latest = new SongDetails();
            Latest.setSong(prefs.GetPlaylistRecentName());// name
            playlists.add(Latest);
            String click_no = prefs.GetLastplayClickNo();
            try {
                if (!Utilities.isEmpty(click_no)) {
                    SongDetails LastPlayed = new SongDetails();
                    LastPlayed.setSong(getResources().getString(R.string.last_played));// name
                    LastPlayed.setClick_no(click_no);
                    playlists.add(LastPlayed);
                } else {
                    if (prefs.HasLastPlayed()) {
                        SongDetails LastPlayed = new SongDetails();
                        LastPlayed.setSong(getResources().getString(R.string.last_played));// name
                        playlists.add(LastPlayed);
                    }
                }
            } catch (NullPointerException e) {
            } catch (Exception e) {
            }

            Cursor cursor = null;
            try {
                cursor = getActivity().getContentResolver().query(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,
                        new String[]{MediaStore.Audio.Playlists._ID, MediaStore.Audio.Playlists.NAME}, null, null,
                        MediaStore.Audio.Playlists.NAME + " COLLATE NOCASE ASC");
                if (cursor != null && cursor.moveToFirst()) {
                    int _ID_index = cursor.getColumnIndex(MediaStore.Audio.Playlists._ID);
                    int NAME_index = cursor.getColumnIndex(MediaStore.Audio.Playlists.NAME);
                    do {
                        Logger.d("F_Playlists", ".............." + cursor.getString(1));
                        playlists.add(new SongDetails(cursor.getInt(_ID_index), cursor.getString(_ID_index), cursor.getString(_ID_index),
                                cursor.getString(NAME_index)));
                    }
                    while (cursor.moveToNext());
                }
            } catch (NullPointerException e) {
            } catch (Exception e) {
            } finally {
                if (cursor != null) {
                    cursor.close();
                    cursor = null;
                }
            }

            return playlists;
        }

        @Override
        protected void onPostExecute(ArrayList<SongDetails> result) {
            super.onPostExecute(result);
            if (Playlists != null) {
                Playlists.clear();
            } else {
                Playlists = new ArrayList<SongDetails>();
            }
            Playlists.addAll(result);
            result.clear();
            OnRefreshListview();
        }
    }
}
