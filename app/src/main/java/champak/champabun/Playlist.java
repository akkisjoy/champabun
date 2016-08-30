package champak.champabun;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.io.File;
import java.util.ArrayList;

import champak.champabun.adapters.Adapter_SongView;
import champak.champabun.classes.SongDetails;
import champak.champabun.ui.TypefaceTextView;
import champak.champabun.util.ActivityUtil;
import champak.champabun.util.PlayMeePreferences;
import champak.champabun.util.SongHelper;
import champak.champabun.util.SongListUtil;
import champak.champabun.util.StorageAccessAPI;
import champak.champabun.util.Utilities;

public class Playlist extends BaseActivity implements SongHelper.OnQuickActionItemSelectListener {
    static public int highlight_zero = 0;
    String click_no;
    ArrayList<SongDetails> play = new ArrayList<SongDetails>();
    Adapter_SongView ab;
    ListView SngList;
    String plName;
    TypefaceTextView playlistname;
    ProgressBar spinner;
    int position2;
    Intent intent;
    //private LinearLayout ll;
    private SongHelper songHelper;
    private PlayMeePreferences prefs;
    private ImageView backPager;

    public static void addToPlaylist(ContentResolver resolver, int audioId, int YOUR_PLAYLIST_ID) {
        String[] cols = new String[]{"count(*)"};
        Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", YOUR_PLAYLIST_ID);
        Cursor cur = null;
        try {
            cur = resolver.query(uri, cols, null, null, null);
            if (cur != null) {
                cur.moveToFirst();
                final int base = cur.getInt(0);
                ContentValues values = new ContentValues();
                values.put(MediaStore.Audio.Playlists.Members.PLAY_ORDER, Integer.valueOf(base + audioId));
                values.put(MediaStore.Audio.Playlists.Members.AUDIO_ID, audioId);
                resolver.insert(uri, values);
            }
        } catch (SQLiteException e) {
        } finally {
            if (cur != null) {
                cur.close();
                cur = null;
            }
        }
    }

    @Override
    public int GetLayoutResID() {
        return R.layout.playlist;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        spinner = (ProgressBar) findViewById(R.id.progressBar1);
        spinner.setVisibility(View.GONE);

        backPager = (ImageView) findViewById(R.id.backPager);
        backPager.setColorFilter(getResources().getColor(R.color.purplePager), PorterDuff.Mode.MULTIPLY);

        prefs = new PlayMeePreferences(getApplicationContext());

        Intent i = getIntent();
        SngList = (ListView) findViewById(R.id.listView1);
        playlistname = (TypefaceTextView) findViewById(R.id.playlistname);
        Typeface face = Typeface.createFromAsset(this.getAssets(), "fonts/julius-sans-one.ttf");
        playlistname.setTypeface(face);
        playlistname.setSelected(true);
        click_no = i.getStringExtra("click_no_playlist");
        plName = i.getStringExtra("name");
        playlistname.setText(plName);
        if (Utilities.isEmpty(click_no)) {
            if (prefs.GetPlaylistRecentName().equals(plName)) {
                SongListUtil util = new SongListUtil(getApplicationContext());
                play = util.GetRecentAdded();

                if (ab == null) {
                    ab = new Adapter_SongView(play, this, 4);
                    SngList.setAdapter(ab);
                } else {
                    SngList.setAdapter(ab);
                }
                util = null;
            } else if (getResources().getString(R.string.last_played).equals(plName)) {
                FetchLastPlayed();
            }
        } else {
            initializesongs();
        }
        SngList.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                Intent intent = new Intent("android.intent.action.p");
                GlobalSongList.GetInstance().setPosition(position);
                intent.putExtra("Data2", position);// don't remove this line

                if (play.size() > 800) {
                    GlobalSongList.GetInstance().SetNowPlayingList(play);
                } else {
                    GlobalSongList.GetInstance().SetNowPlayingList(new ArrayList<SongDetails>(play));
                }

                if (prefs.GetPlaylistRecentName().equals(plName)) {
                    intent.putExtra("fromRecentAdded", true);
                } else if (getResources().getString(R.string.last_played).equals(plName)) {
                    intent.putExtra("fromRecentAdded", prefs.IsFromRecentAdded());
                } else {
                    intent.putExtra("fromRecentAdded", false);
                }
                intent.putExtra("comefrom", "Playlist");
                intent.putExtra("click_no_playlist", click_no);
                GlobalSongList.GetInstance().setCheck(0);
                startActivity(intent);
            }
        });

        SngList.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                position2 = arg2;
                if (songHelper == null) {
                    songHelper = new SongHelper();
                }

                if (click_no == null && prefs.GetPlaylistRecentName().equals(plName)) {
                    songHelper.Show(Playlist.this, GetRootView(), Playlist.this, SongHelper.DEFAULT);
                } else {
                    songHelper.Show(Playlist.this, GetRootView(), Playlist.this, SongHelper.PLAYLIST);
                }
                return true;
            }
        });
    }

    private void FetchLastPlayed() {
        SongListUtil util = new SongListUtil(getApplicationContext());
        if (prefs.IsFromRecentAdded()) {
            play = util.GetRecentAdded();
        } else {
            play = util.FetchLastPlayed();
        }

        if (ab == null) {
            ab = new Adapter_SongView(play, this, 4);
            SngList.setAdapter(ab);
        } else {
            SngList.setAdapter(ab);
        }
        util = null;
    }

    private void initializesongs() {
        new InitializeSongsTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, click_no);
    }

    public void removeFromPlaylist(int YOUR_PLAYLIST_ID) {
        // Logger.v("made it to add",""+audioId);
        String[] cols = new String[]{"count(*)"};
        Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", YOUR_PLAYLIST_ID);
        Cursor cur = null;
        try {
            cur = getContentResolver().query(uri, cols, null, null, null);
            if (cur != null && cur.moveToFirst()) {
                // ContentValues values = new ContentValues();
                // resolver.delete(uri, MediaStore.Audio.Playlists.Members.DATA +" = "+play.get(position2).Path, null);
                getContentResolver().delete(uri, MediaStore.Audio.Playlists.Members.AUDIO_ID + " = " + play.get(position2).getAudioID(), null);
            }
        } catch (SQLiteException e) {
        } finally {
            if (cur != null) {
                cur.close();
                cur = null;
            }
        }
        initializesongs();
    }

    @Override
    public void QuickAction_OnPlaySong() {
        songHelper.PlaySong(play, position2);
    }

    @Override
    public void QuickAction_OnAdd2Playlist() {
        songHelper.Add2Playlist(play.get(position2));
    }

    @Override
    public void QuickAction_OnEditTags() {
        try {
            songHelper.EditTags(play.get(position2), spinner, null, new SongHelper.OnEditTagsListener() {

                @Override
                public void OnEditTagsSuccessful() {
                    try {
                        OnRefreshSongList();
                    } catch (Exception ignored) {
                    }
                }
            }, null);
        } catch (Exception ignored) {
        }
    }

    @Override
    public void QuickAction_OnSetAsRingtone() {
        songHelper.SetAsRingtone(play.get(position2));
    }

    @Override
    public void QuickAction_OnViewDetails() {
    }

    @Override
    public void QuickAction_OnDeleteSong() {
        songHelper.DeleteSong(play, position2, new SongHelper.OnDeleteSongListener() {

            @Override
            public void OnSongDeleted() {
                OnRefreshSongList();
            }
        }, null);
    }

    private void OnRefreshSongList() {
        if (ab == null) {
            ab = new Adapter_SongView(play, this, 4);
            SngList.setAdapter(ab);
        } else {
            ab.OnUpdate(play);
        }
    }

    @Override
    public String GetActivityID() {
        return "Playlist";
    }

    @Override
    public void SetNullForCustomVariable() {
        songHelper = null;
    }

    @Override
    public int GetRootViewID() {
        return R.id.bg;
    }

    @Override
    public void OnBackPressed() {
        if (songHelper != null) {
            songHelper.ShouldDismiss();
        }
        finish();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent)

    {
        super.onActivityResult(requestCode, resultCode, intent);
        switch (requestCode) {
            case StorageAccessAPI.Code:
                // PlayMeePreferences prefs = new PlayMeePreferences(Player.this);
                if (resultCode == Activity.RESULT_OK) {

                    StorageAccessAPI.onActivityResult(requestCode, resultCode, intent, Playlist.this);

                    File file = new File(play.get(position2).getPath2());
                    boolean canWrite;
                    try {
                        canWrite = StorageAccessAPI.getDocumentFile(file, false).canWrite();
                    } catch (Exception e) {
                        canWrite = false;
                    }
                    if (canWrite) {
                        songHelper.EditTags(play.get(position2), spinner, null, new SongHelper.OnEditTagsListener() {

                            @Override
                            public void OnEditTagsSuccessful() {
                                OnRefreshSongList();
                            }
                        }, null);

                    } else
                        ActivityUtil.showCrouton(Playlist.this, getString(R.string.tag_not_edited));

                } else
                    ActivityUtil.showCrouton(Playlist.this, getString(R.string.cancel));
        }
    }

    @Override
    public void QuickAction_OnRemoveSong() {
        songHelper.RemoveSong(Playlist.this.getContentResolver(), play.get(position2).getAudioID(), position2, Integer.parseInt(click_no));
        initializesongs();
    }

    @Override
    public void QuickAction_OnSendSong() {
        // never call for this case
    }

    @Override
    protected String GetGAScreenName() {
        return "Playlist";
    }

    private View GetRootView() {
        return findViewById(GetRootViewID());
    }

    class InitializeSongsTask extends AsyncTask<String, Void, ArrayList<SongDetails>> {
        private SongListUtil util;

        public InitializeSongsTask() {
            util = new SongListUtil(Playlist.this);
        }

        @Override
        protected ArrayList<SongDetails> doInBackground(String... arg0) {
            return util.GetSonglistByClickNo(arg0[0]);
        }

        protected void onPostExecute(ArrayList<SongDetails> p) {
            super.onPostExecute(p);

            if (play != null) {
                play.clear();
            } else {
                play = new ArrayList<SongDetails>();
            }
            if (p != null && p.size() > 0) {
                play.addAll(p);
                p.clear();
                p = null;
            }
            OnRefreshSongList();
            util = null;
        }
    }
}
