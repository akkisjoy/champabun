package champak.champabun.view.activity;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.SparseBooleanArray;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import champak.champabun.AmuzicgApp;
import champak.champabun.R;
import champak.champabun.business.dataclasses.SongDetails;
import champak.champabun.business.utilities.rayMenu.RayMenu;
import champak.champabun.business.utilities.utilMethod.ActivityUtil;
import champak.champabun.business.utilities.utilMethod.BitmapUtil;
import champak.champabun.business.utilities.utilMethod.RayMenu_Functions;
import champak.champabun.business.utilities.utilMethod.SongHelper;
import champak.champabun.business.utilities.utilMethod.StorageAccessAPI;
import champak.champabun.business.utilities.utilMethod.Utilities;
import champak.champabun.framework.listener.EditTagsListener;
import champak.champabun.framework.listener.OptionItemSelectListener;
import champak.champabun.view.adapters.Adapter_SongView;
import champak.champabun.view.adapters.Adapter_playlist_Dialog;

public class Album extends BaseActivity implements OptionItemSelectListener {
    static public int highlight_zero = 0;
    ArrayList<SongDetails> songs;
    Adapter_SongView adapter;
    int curItemSelect;
    Dialog dialog2;
    String click_no;
    ImageView albumart;
    Animation fadeOut, fadeIn;
    LinearLayout bg;
    int playlistid;
    TextView tV1, artistname, totaltracks, totaltime;
    ProgressBar spinner;
    Handler handler = new Handler();
    String artist, image_path, name;
    ArrayList<SongDetails> multiplecheckedListforaddtoplaylist, pl;
    Button bRBack, bRAdd, bRPlay, bRDel;
    RayMenu rayMenu;
    private ListView mListView;
    private SongHelper songHelper;

    @Override
    public int GetLayoutResID() {
        return R.layout.album;
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bRBack = (Button) findViewById(R.id.bRBack);
        bRAdd = (Button) findViewById(R.id.bRAdd);
        bRPlay = (Button) findViewById(R.id.bRPlay);
        bRDel = (Button) findViewById(R.id.bRDelete);

        bRBack.setVisibility(View.INVISIBLE);
        bRAdd.setVisibility(View.INVISIBLE);
        bRPlay.setVisibility(View.INVISIBLE);
        bRDel.setVisibility(View.INVISIBLE);

        rayMenu = (RayMenu) findViewById(R.id.ray);
        SetupButton();
        SetupRayMenu();
        Intent i = getIntent();
        click_no = i.getStringExtra("click_no");
        image_path = i.getStringExtra("image_path");
        name = i.getStringExtra("name");
        artist = i.getStringExtra("artist");

        mListView = (ListView) findViewById(R.id.listViewA);
        spinner = (ProgressBar) findViewById(R.id.progressBar1);
        spinner.setVisibility(View.GONE);

        tV1 = (TextView) findViewById(R.id.albumname);

        artistname = (TextView) findViewById(R.id.artistname);
        totaltracks = (TextView) findViewById(R.id.totaltracks);
        totaltime = (TextView) findViewById(R.id.totaltime);
        bg = (LinearLayout) findViewById(R.id.ll);
        albumart = (ImageView) findViewById(R.id.albumart);

        new FetchAlbum().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void) null);

        mListView.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                curItemSelect = arg2;
                if (songHelper == null) {
                    songHelper = new SongHelper();
                }
                songHelper.Show(Album.this, adapter.GetData().get(curItemSelect).getSong(), adapter.GetData().get(curItemSelect).getArtist(), Album.this, SongHelper.ALBUM);
                return true;
            }
        });
        mListView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                Intent intent = new Intent(Album.this, Player.class);

                if (songs.size() > 800) {
                    AmuzicgApp.GetInstance().SetNowPlayingList(songs);
                } else {
                    AmuzicgApp.GetInstance().SetNowPlayingList(new ArrayList<SongDetails>(songs));
                }

                AmuzicgApp.GetInstance().setPosition(position);
                AmuzicgApp.GetInstance().setCheck(0);
                startActivity(intent);
            }
        });

        tV1.setSelected(true);
        tV1.setText(name);
        artistname.setText(String.format(getString(R.string.f_by_artist), artist));
        Bitmap b = BitmapUtil.SetAlbumArtBG(this, image_path, name, artist);
        BitmapDrawable d = BitmapUtil.SetBG(b, this);
        albumart.setImageBitmap(b);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            bg.setBackgroundDrawable(d);
        } else {
            bg.setBackground(d);
        }
    }

    protected void delete_song_multiple(final ArrayList<SongDetails> checkedList) {
        dialog2 = Utilities.designdialog(210, Album.this);
        dialog2.setContentView(R.layout.dialoge_delete);

        TextView title = (TextView) dialog2.findViewById(R.id.title);
        title.setText(String.format(getString(R.string.f_confirm_delete_songs), checkedList.size()));
        Button ok = (Button) dialog2.findViewById(R.id.dBOK);
        Button cancel = (Button) dialog2.findViewById(R.id.dBCancel);

        cancel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog2.dismiss();
            }
        });

        ok.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                for (int i = 0; i < checkedList.size(); i++) {
                    SongHelper.DeleteSongMultiple(Album.this, checkedList, i, null, null);

                    songs.remove(checkedList.get(i));
                    adapter.OnUpdate(songs);
                }
                highlight_zero = 0;
                ActivityUtil.showCrouton(Album.this, getString(R.string.delete_successful));
                adapter.OnUpdate(songs);
                for (int i = 0; i < checkedList.size(); i++) {
                    try {
                        getContentResolver().delete(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, MediaStore.MediaColumns.DATA + "=?",
                                new String[]{checkedList.get(i).getPath2()});
                    } catch (SQLiteException ignored) {
                    }
                }
                dialog2.dismiss();
            }
        });
        dialog2.show();
    }

    private void SetupButton() {
        bRAdd.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View vv) {
                if (multiplecheckedListforaddtoplaylist != null) {
                    multiplecheckedListforaddtoplaylist.clear();
                }
                SparseBooleanArray checked = mListView.getCheckedItemPositions();
                multiplecheckedListforaddtoplaylist = RayMenu_Functions.getCheckedList(highlight_zero, checked, songs, adapter);

                if (multiplecheckedListforaddtoplaylist.size() > 0) {
                    Adapter_playlist_Dialog ab;

                    dialog2 = Utilities.designdialog(330, Album.this);
                    dialog2.setContentView(R.layout.dialog_queue);
                    dialog2.show();
                    pl = Utilities.generatePlaylists(Album.this.getApplicationContext());
                    SongDetails np = new SongDetails();
                    np.setSong(getString(R.string.now_playing));
                    pl.add(0, np);
                    np = new SongDetails();
                    np.setSong(getString(R.string.create_new));
                    pl.add(1, np);
                    np = null;
                    ab = new Adapter_playlist_Dialog(pl);
                    ListView dlgLV = (ListView) dialog2.findViewById(R.id.listView1);
                    dlgLV.setAdapter(ab);
                    dlgLV.setOnItemClickListener(new OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            if (position == 0) {
                                AmuzicgApp.GetInstance().Add2NowPlaying(multiplecheckedListforaddtoplaylist);
                                String ss = String.format(getString(R.string.f_song_added_to_queue),
                                        multiplecheckedListforaddtoplaylist.size() > 1 ? multiplecheckedListforaddtoplaylist.size() : 1);
                                ActivityUtil.showCrouton(Album.this, ss);
                            } else if (position == 1) {
                                create_new_playlist();
                            } else {
                                // id of the playlist
                                String plId = pl.get(position).getArtist();
                                playlistid = Integer.parseInt(plId);
                                new AddToPlayListMultiple().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void) null);
                            }
                            dialog2.dismiss();
                        }
                    });
                } else {
                    ActivityUtil.showCrouton(Album.this, getString(R.string.select_an_item_to_add));
                }
            }
        });

        bRDel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                SparseBooleanArray checked = mListView.getCheckedItemPositions();
                ArrayList<SongDetails> checkedList = RayMenu_Functions.getCheckedList(highlight_zero, checked, songs, adapter);
                if (checkedList.size() > 0) {
                    delete_song_multiple(checkedList);
                } else {
                    ActivityUtil.showCrouton(Album.this, getString(R.string.select_an_item_to_play));
                }
            }
        });

        bRPlay.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                SparseBooleanArray checked = mListView.getCheckedItemPositions();
                ArrayList<SongDetails> checkedList = RayMenu_Functions.getCheckedList(highlight_zero, checked, songs, adapter);
                if (checkedList.size() > 0) {

                    Intent intent = new Intent(Album.this, Player.class);
                    AmuzicgApp.GetInstance().setPosition(0);
                    if (checkedList.size() > 800) {
                        AmuzicgApp.GetInstance().SetNowPlayingList(checkedList);
                    } else {
                        AmuzicgApp.GetInstance().SetNowPlayingList(new ArrayList<SongDetails>(checkedList));
                    }

                    AmuzicgApp.GetInstance().setCheck(0);
                    startActivity(intent);
                } else {
                    ActivityUtil.showCrouton(Album.this, getString(R.string.select_an_item_to_play));
                }
                checkedList.clear();
            }
        });

        bRBack.setOnClickListener(new OnClickListener() {
            // TODO
            @Override
            public void onClick(View v) {
                fadeout(00, 700);

                bRBack.startAnimation(fadeOut);
                bRAdd.startAnimation(fadeOut);
                bRPlay.startAnimation(fadeOut);
                bRDel.startAnimation(fadeOut);
                fadeOut.setAnimationListener(new AnimationListener() {
                    @Override
                    public void onAnimationEnd(Animation animation) {
                        fadein(0, 700);
                        rayMenu.startAnimation(fadeIn);
                        bRBack.setVisibility(View.GONE);
                        bRAdd.setVisibility(View.GONE);
                        bRPlay.setVisibility(View.GONE);
                        bRDel.setVisibility(View.GONE);

                        highlight_zero = 0;
                        SparseBooleanArray checked = mListView.getCheckedItemPositions();
                        if (checked != null)
                            for (int i = 0; i < checked.size(); i++) {
                                if (checked.valueAt(i)) {
                                    mListView.setItemChecked(checked.keyAt(i), false);
                                }
                            }
                        mListView.setChoiceMode(ListView.CHOICE_MODE_NONE);
                        adapter.OnUpdate(songs);

                        mListView.setOnItemClickListener(new OnItemClickListener() {
                            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                                Intent intent = new Intent(Album.this, Player.class);
                                AmuzicgApp.GetInstance().setPosition(position);
                                AmuzicgApp.GetInstance().SetNowPlayingList(adapter.GetData());
                                AmuzicgApp.GetInstance().setCheck(0);
                                startActivity(intent);
                            }
                        });
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }

                    @Override
                    public void onAnimationStart(Animation animation) {
                    }
                });
            }
        });

    }

    private void SetupRayMenu() {
        int[] ITEM_DRAWABLES = {R.drawable.composer_button_multiselect, R.drawable.composer_button_sort, R.drawable.composer_button_shuffle};
        for (int ITEM_DRAWABLE : ITEM_DRAWABLES) {
            ImageView item = new ImageView(this);
            item.setTag(ITEM_DRAWABLE);
            item.setImageResource(ITEM_DRAWABLE);
            rayMenu.addItem(item, new OnClickListener() {

                @Override
                public void onClick(View v) {
                    final int resID = (Integer) v.getTag();
                    switch (resID) {
                        case R.drawable.composer_button_multiselect: {
                            fadeout(0, 700);
                            ActivityUtil.showCrouton(Album.this, getString(R.string.multi_select_initiated));
                            rayMenu.startAnimation(fadeOut);
                            fadeOut.setAnimationListener(new AnimationListener() {
                                @Override
                                public void onAnimationEnd(Animation animation) {
                                    fadein(0, 700);
                                    bRBack.setVisibility(View.INVISIBLE);
                                    bRAdd.setVisibility(View.INVISIBLE);
                                    bRPlay.setVisibility(View.INVISIBLE);
                                    bRDel.setVisibility(View.INVISIBLE);
                                    bRBack.startAnimation(fadeIn);
                                    bRAdd.startAnimation(fadeIn);
                                    bRPlay.startAnimation(fadeIn);
                                    bRDel.startAnimation(fadeIn);
                                    rayMenu.setVisibility(View.INVISIBLE);
                                    mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                                    mListView.setOnItemClickListener(new OnItemClickListener() {

                                        @Override
                                        public void onItemClick(

                                                AdapterView<?> parent, View view, int position, long id) {
                                            if (position == 0) {
                                                highlight2();
                                            }
                                            if (position > 0) {
                                                highlight(position);
                                            }
                                        }

                                        private void highlight2() {
                                            highlight_zero = highlight_zero + 1;
                                            adapter.OnUpdate(songs);
                                        }

                                        private void highlight(int position) {
                                            mListView.setItemChecked(position, true);
                                            adapter.OnUpdate(songs);
                                        }
                                    });
                                }

                                @Override
                                public void onAnimationRepeat(Animation animation) {
                                }

                                @Override
                                public void onAnimationStart(Animation animation) {
                                }
                            });
                            break;
                        }
                        case R.drawable.composer_button_sort: {
                            handler.removeCallbacksAndMessages(null);
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    RayMenu_Functions.sort(click_no, Album.this, adapter, songs, mListView, 1, null, null);
                                    // 1 for album
                                }
                            }, 760);
                            break;
                        }
                        case R.drawable.composer_button_shuffle: {
                            if (songs != null && songs.size() > 0) {
                                Collections.shuffle(songs);
                            }
                            handler.removeCallbacksAndMessages(null);
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    // delay "shuffle and play" so that animation is
                                    // smooth
                                    Intent intent = new Intent(Album.this, Player.class);
                                    AmuzicgApp.GetInstance().setPosition(0);
                                    if (songs.size() > 800) {
                                        AmuzicgApp.GetInstance().SetNowPlayingList(songs);
                                    } else {
                                        AmuzicgApp.GetInstance().SetNowPlayingList(new ArrayList<>(songs));
                                    }

                                    AmuzicgApp.GetInstance().setCheck(0);
                                    startActivity(intent);
                                }
                            }, 540);
                            break;
                        }
                        default:
                            break;
                    }
                }
            });
        }
        ITEM_DRAWABLES = null;
    }

    public void create_new_playlist() { // TODO
        final Dialog dialog;
        dialog = new Dialog(Album.this, R.style.AmuzeTheme);
        final Window window = dialog.getWindow();
        float pixels = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 210, this.getResources().getDisplayMetrics());
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, (int) pixels);
        window.setBackgroundDrawableResource(R.drawable.miniback2);
        // window.setBackgroundDrawable(new ColorDrawable(0x99000000));
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.dimAmount = 0.8f; // Dim level. 0.0 - no dim, 1.0 - completely opaque
        dialog.getWindow().setAttributes(lp);
        window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        dialog.setContentView(R.layout.dialog_create_new_playlist);
        Button bDOK = (Button) dialog.findViewById(R.id.dBOK);
        Button bDCancle = (Button) dialog.findViewById(R.id.dBCancel);

        bDOK.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                EditText save = (EditText) dialog.findViewById(R.id.save_as);
                int YOUR_PLAYLIST_ID = NowPlaying.createPlaylist(save.getText().toString(), Album.this);
                if (YOUR_PLAYLIST_ID == -1)
                    return;
                new AddToPl().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, YOUR_PLAYLIST_ID);
                dialog.dismiss();
            }
        });
        bDCancle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
            }

        });
        dialog.show();
    }

    @Override
    public void action_OnPlaySong() {
        songHelper.PlaySong(songs, curItemSelect);
    }

    @Override
    public void action_OnAdd2Playlist() {
        songHelper.Add2Playlist(songs.get(curItemSelect));
    }

    @Override
    public void action_OnEditTags() {
        songHelper.EditTags(songs.get(curItemSelect), new EditTagsListener() {

            @Override
            public void onEditTagsSuccessful() {
                OnRefreshListView();
            }
        }, null);
    }

    @Override
    public void action_OnSetAsRingtone() {
        songHelper.SetAsRingtone(songs.get(curItemSelect));
    }

    @Override
    public void action_OnViewDetails() {
    }

    @Override
    public void action_OnDeleteSong() {
        songHelper.DeleteSong(songs, curItemSelect, new SongHelper.OnDeleteSongListener() {

            @Override
            public void OnSongDeleted() {
                OnRefreshListView();
            }
        }, null);
    }

    private void OnRefreshListView() {
        if (adapter == null) {
            adapter = new Adapter_SongView(songs, getApplicationContext(), 1);
            mListView.setAdapter(adapter);
        } else {
            adapter.OnUpdate(songs);
        }
    }

    @Override
    public String GetActivityID() {
        return "Album";
    }

    @Override
    public void SetNullForCustomVariable() {
        songHelper = null;
    }

    @Override
    public int GetRootViewID() {
        return R.id.rootview;
    }

    protected void fadeout(int offset, int duration) {
        fadeOut = null;
        fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new AccelerateInterpolator());
        fadeOut.setFillAfter(true);
        fadeOut.setStartOffset(offset);
        fadeOut.setDuration(duration);
    }

    protected void fadein(int offset, int duration) {
        fadeIn = null;
        fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new AccelerateInterpolator()); // and this
        fadeIn.setFillAfter(true);
        fadeIn.setStartOffset(offset);
        fadeIn.setDuration(duration);
    }

    @Override
    public void OnBackPressed() {
        if (songHelper != null) {
            songHelper.ShouldDismiss();
        }
        finish();
    }

    @Override
    public void action_OnRemoveSong() {
    }

    public void onResume() {
        if (!rayMenu.mRayLayout.isExpanded())
            Animate_raymenu();
        super.onResume();

    }

    private void Animate_raymenu() {
        handler.removeCallbacksAndMessages(null);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                rayMenu.mHintView.startAnimation(rayMenu.createHintSwitchAnimation(rayMenu.mRayLayout.isExpanded()));
                rayMenu.mRayLayout.switchState(true);
            }
        }, 800);
    }

    @Override
    public void action_OnSendSong() {
        // never call for this case
    }

    @Override
    protected String GetGAScreenName() {
        return "Album";
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent)

    {
        super.onActivityResult(requestCode, resultCode, intent);
        switch (requestCode) {
            case StorageAccessAPI.Code:
                // AmuzePreferences prefs = new AmuzePreferences(Player.this);
                if (resultCode == RESULT_OK) {

                    StorageAccessAPI.onActivityResult(requestCode, resultCode, intent, Album.this);


                    File file = new File(songs.get(curItemSelect).getPath2());
                    boolean canWrite;
                    try {
                        canWrite = StorageAccessAPI.getDocumentFile(file, false).canWrite();
                    } catch (Exception e) {
                        canWrite = false;
                    }
                    if (canWrite) {
                        songHelper.EditTags(songs.get(curItemSelect), new EditTagsListener() {

                            @Override
                            public void onEditTagsSuccessful() {
                                OnRefreshListView();
                            }
                        }, null);

                    } else
                        ActivityUtil.showCrouton(Album.this, getString(R.string.tag_not_edited));

                } else
                    ActivityUtil.showCrouton(Album.this, getString(R.string.cancel));
        }
    }

    class AddToPl extends AsyncTask<Integer, String, String> {
        @Override
        protected void onPostExecute(String x) {
            spinner.setVisibility(View.GONE);
            ActivityUtil.showCrouton(Album.this, getString(R.string.playlist_created));
        }

        @Override
        protected void onPreExecute() {
            spinner.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Integer... params) {
            for (int index = 0; index < multiplecheckedListforaddtoplaylist.size(); index++) {
                try {
                    NowPlaying.addToPlaylist(getApplicationContext(), multiplecheckedListforaddtoplaylist.get(index).getPath2(),
                            params[0]);
                } catch (IllegalStateException e) {
                    ActivityUtil.showCrouton(Album.this, getString(R.string.playlist_name_already_exists));
                    return null;
                } catch (Exception e) {
                    ActivityUtil.showCrouton(Album.this, getString(R.string.an_error_occurred));
                    return null;
                }
            }

            return null;
        }
    }

    class FetchAlbum extends AsyncTask<Void, Void, ArrayList<SongDetails>> {
        int totalTime = 0;
        int duration;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            duration = appSettings.getDurationFilterTime();
        }

        @Override
        protected ArrayList<SongDetails> doInBackground(Void... params) {
            String[] columns = {MediaStore.Audio.Media.DATA, MediaStore.Audio.Media._ID, MediaStore.Audio.Media.TITLE,
                    MediaStore.Audio.Media.DISPLAY_NAME, MediaStore.Audio.Media.ALBUM, MediaStore.Audio.Media.ARTIST,
                    MediaStore.Audio.Media.DURATION, MediaStore.MediaColumns._ID};

            String where = MediaStore.Audio.Media.ALBUM_ID + "=? AND " + MediaStore.Audio.Media.DURATION + ">=?";
            String whereVal[] = {click_no, String.valueOf(duration)};
            String sortOrder = appSettings.getAlbumSortKey();
            if (TextUtils.isEmpty(appSettings.getAlbumSortKey())) {
                sortOrder = MediaStore.Audio.Media.TRACK;// android.provider.MediaStore.Audio.Media.TITLE;
            }
            Cursor cursor = null;
            ArrayList<SongDetails> songs__ = new ArrayList<SongDetails>();
            try {
                cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, columns, where, whereVal, sortOrder);
                if (cursor != null && cursor.moveToFirst()) {
                    do {
                        int x = Integer.parseInt(click_no);
                        songs__.add(new SongDetails(cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID)), click_no, cursor
                                .getString(4), cursor.getString(5), cursor.getString(0), Utilities.getTime(cursor.getString(6)), cursor
                                .getString(2), cursor.getString(5), x));
                        int time = 0;
                        try {
                            time = Integer.parseInt(cursor.getString(6));
                        } catch (NumberFormatException e) {
                            time = Utilities.parseTime(cursor.getString(6));
                        }
                        totalTime += time;
                    }
                    while (cursor.moveToNext());
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
            return songs__;
        }

        @Override
        protected void onPostExecute(ArrayList<SongDetails> result) {
            super.onPostExecute(result);
            if (songs != null) {
                songs.clear();
            } else {
                songs = new ArrayList<>();
            }
            songs.addAll(result);
            result.clear();
            OnRefreshListView();

            totaltracks.setText(String.format(getString(R.string.f_total_tracks), songs.size()));
            totaltime.setText(String.format(getString(R.string.f_total_time), Utilities.getTime(totalTime)));
            Animate_raymenu();
        }
    }

    class AddToPlayListMultiple extends AsyncTask<Void, String, String> {
        @Override
        protected void onPostExecute(String x) {
            spinner.setVisibility(View.GONE);
            multiplecheckedListforaddtoplaylist.clear();
            highlight_zero = 0;
            ActivityUtil.showCrouton(Album.this, getString(R.string.songs_were_added_to_playlist));
            SparseBooleanArray checked = mListView.getCheckedItemPositions();
            if (checked != null)
                for (int i = 0; i < checked.size(); i++) {
                    if (checked.valueAt(i)) {
                        mListView.setItemChecked(checked.keyAt(i), false);
                    }
                }
            mListView.setChoiceMode(ListView.CHOICE_MODE_NONE);
            mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
            adapter.OnUpdate(songs);
        }

        @Override
        protected void onPreExecute() {
            spinner.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            for (int i = 0; i < multiplecheckedListforaddtoplaylist.size(); i++)
                NowPlaying.addToPlaylist(Album.this.getApplicationContext(), multiplecheckedListforaddtoplaylist.get(i).getPath2(), playlistid);

            return null;
        }
    }
}
