package champak.champabun.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.io.File;
import java.util.ArrayList;

import champak.champabun.AmuzicgApp;
import champak.champabun.R;
import champak.champabun.business.dataclasses.SongDetails;
import champak.champabun.business.utilities.utilClass.TypefaceTextView;
import champak.champabun.business.utilities.utilMethod.ActivityUtil;
import champak.champabun.business.utilities.utilMethod.PlayMeePreferences;
import champak.champabun.business.utilities.utilMethod.SongHelper;
import champak.champabun.business.utilities.utilMethod.SongListUtil;
import champak.champabun.business.utilities.utilMethod.StorageAccessAPI;
import champak.champabun.business.utilities.utilMethod.Utilities;
import champak.champabun.view.adapters.Adapter_SongView;

public class Playlist extends BaseActivity implements SongHelper.OnQuickActionItemSelectListener {
    static public int highlight_zero = 0;
    String click_no;
    ArrayList<SongDetails> play = new ArrayList<>();
    Adapter_SongView ab;
    ListView SngList;
    String plName;
    TypefaceTextView playlistname;
    ProgressBar spinner;
    int position2;
    private SongHelper songHelper;
    private PlayMeePreferences prefs;
    private ImageView backPager;

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
            } else if (getResources().getString(R.string.last_played).equals(plName)) {
                FetchLastPlayed();
            }
        } else {
            initializesongs();
        }
        SngList.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                Intent intent = new Intent("android.intent.action.p");
                AmuzicgApp.GetInstance().setPosition(position);
                intent.putExtra("Data2", position);// don't remove this line

                if (play.size() > 800) {
                    AmuzicgApp.GetInstance().SetNowPlayingList(play);
                } else {
                    AmuzicgApp.GetInstance().SetNowPlayingList(new ArrayList<SongDetails>(play));
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
                AmuzicgApp.GetInstance().setCheck(0);
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
    }

    private void initializesongs() {
        new InitializeSongsTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, click_no);
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
        songHelper.RemoveSong(Playlist.this.getContentResolver(), play.get(position2).getAudioID(), Integer.parseInt(click_no));
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
                play = new ArrayList<>();
            }
            if (p != null && p.size() > 0) {
                play.addAll(p);
                p.clear();
            }
            OnRefreshSongList();
            util = null;
        }
    }
}
