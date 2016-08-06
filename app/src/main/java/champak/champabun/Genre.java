package champak.champabun;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
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
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;
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

import champak.champabun.adapters.Adapter_SongView;
import champak.champabun.adapters.Adapter_gallery;
import champak.champabun.adapters.Adapter_playlist_Dialog;
import champak.champabun.classes.SongDetails;
import champak.champabun.ecogallery.EcoGallery;
import champak.champabun.ecogallery.EcoGalleryAdapterView;
import champak.champabun.raymenu.RayMenu;
import champak.champabun.ui.TypefaceTextView;
import champak.champabun.util.ActivityUtil;
import champak.champabun.util.RayMenu_Functions;
import champak.champabun.util.SongHelper;
import champak.champabun.util.StorageAccessAPI;
import champak.champabun.util.Utilities;

public class Genre extends BaseActivity implements SongHelper.OnQuickActionItemSelectListener {
    String sexy;
    ArrayList<SongDetails> img = new ArrayList<SongDetails>();
    Adapter_SongView ab;
    // ArrayList < String > img2;// = new ArrayList<String>();
    String genre_id, genre_title;
    static public int highlight_zero = 0;
    LinearLayout ll;
    ArrayList<SongDetails> play;
    TypefaceTextView tV1;// album2;
    int pos;
    int playlistid;
    Handler handler = new Handler();
    int position2;
    Dialog dialog2;
    ArrayList<SongDetails> multiplecheckedListforaddtoplaylist, pl;
    public ListView mListView;
    public ProgressBar spinner;

    Button bRBack, bRAdd, bRPlay, bRDel;
    RayMenu rayMenu;
    // Context c;
    private SongHelper songHelper;
    Animation fadeOut, fadeIn;

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
    public int GetLayoutResID() {
        return R.layout.artist;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
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

        // img2 = new ArrayList < String >();
        play = new ArrayList<SongDetails>();
        spinner = (ProgressBar) findViewById(R.id.progressBar1);
        spinner.setVisibility(View.GONE);

        mListView = (ListView) findViewById(R.id.listview);
        mListView.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                position2 = arg2;
                if (songHelper == null) {
                    songHelper = new SongHelper();
                }
                songHelper.Show(Genre.this, mListView, Genre.this, SongHelper.ARTIST);
                return true;
            }
        });
        mListView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                Intent intent = new Intent("android.intent.action.p");
                if (play.size() > 800) {
                    GlobalSongList.GetInstance().SetNowPlayingList(play);
                } else {
                    GlobalSongList.GetInstance().SetNowPlayingList(new ArrayList<SongDetails>(play));
                }
                GlobalSongList.GetInstance().setPosition(position);
                GlobalSongList.GetInstance().setCheck(0);
                startActivity(intent);
            }
        });

        animate_ListView();
        Intent i = getIntent();

        genre_id = i.getStringExtra("click_no");
        genre_title = i.getStringExtra("genre_title");

        int s = Integer.parseInt(genre_id);

        // if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB)
        // {
        new FetchListItems().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, s);
        // }
        // else
        // {
        // new FetchListItems().execute(s);
        // }

        // PaintDrawable p = Utilities.returnbg();
        // ll = (LinearLayout) findViewById(R.id.ll);
        // ll.setBackground(p);
        tV1 = (TypefaceTextView) findViewById(R.id.tV1);
        // album2 = (TypefaceTextView) findViewById(R.id.album);
        tV1.setSelected(true);
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
        songHelper.EditTags(play.get(position2), spinner, null, new SongHelper.OnEditTagsListener() {

            @Override
            public void OnEditTagsSuccessful() {
                OnRefreshListView();
            }
        }, null);
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
                OnRefreshListView();
            }
        }, null);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent)

    {
        super.onActivityResult(requestCode, resultCode, intent);
        switch (requestCode) {
            case StorageAccessAPI.Code:
                // PlayMeePreferences prefs = new PlayMeePreferences(Player.this);
                if (resultCode == Activity.RESULT_OK) {

                    StorageAccessAPI.onActivityResult(requestCode, resultCode, intent, Genre.this);


                    File file = new File(play.get(position2).getPath2());
                    boolean canwrite = false;
                    try {
                        canwrite = StorageAccessAPI.getDocumentFile(file, false).canWrite();
                    } catch (Exception e) {
                        canwrite = false;
                    }
                    if (canwrite) {
                        songHelper.EditTags(play.get(position2), spinner, null, new SongHelper.OnEditTagsListener() {

                            @Override
                            public void OnEditTagsSuccessful() {
                                OnRefreshListView();
                            }
                        }, null);

                    } else
                        ActivityUtil.showCrouton(Genre.this, getString(R.string.tag_not_edited));

                } else
                    ActivityUtil.showCrouton(Genre.this, getString(R.string.cancel));
        }
    }

    private void OnRefreshListView() {
        if (ab == null) {
            ab = new Adapter_SongView(play, Genre.this.getApplicationContext(), 5);
            mListView.setAdapter(ab);
        } else {
            ab.OnUpdate(play);
        }
    }

    private void animate_ListView() {
        AnimationSet set = new AnimationSet(true);

        Animation animation = new AlphaAnimation(0.0f, 1.0f);
        animation.setDuration(600);
        set.addAnimation(animation);

        animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, -1.0f,
                Animation.RELATIVE_TO_SELF, 0.0f);
        animation.setDuration(600);
        set.addAnimation(animation);

        LayoutAnimationController controller = new LayoutAnimationController(set, 0.25f);
        mListView.setLayoutAnimation(controller);

        // controller = null;
        // animation = null;
        // set = null;
    }

    /**
     * Sets the up listeners.
     *
     * @param mCoverFlow the new up listeners
     */
    private void setupListeners(final EcoGallery mCoverFlow, final String sexy) {
        mCoverFlow.setOnItemSelectedListener(new EcoGalleryAdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(EcoGalleryAdapterView<?> parent, View view, int position, long id) {
                pos = position;
                Logger.d("Artist", "onItemSelected................." + position);
                // if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB)
                // {

                highlight_zero = 0;
                SparseBooleanArray checked = mListView.getCheckedItemPositions();
                if (checked != null)
                    for (int i = 0; i < checked.size(); i++) {
                        if (checked.valueAt(i)) {
                            mListView.setItemChecked(checked.keyAt(i), false);
                        }
                    }
                if (mListView.getChoiceMode() == ListView.CHOICE_MODE_NONE)
                    mListView.setChoiceMode(ListView.CHOICE_MODE_NONE);
                OnRefreshListView();
                // ab.OnUpdate(play);
                checked = null;

                new LazyLoad().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, img.get(position).getArtist());

            }

            @Override
            public void onNothingSelected(EcoGalleryAdapterView<?> parent) {

            }
        });
    }

    private void setupListeners(final EcoGallery mCoverFlow) {
        mCoverFlow.setOnItemSelectedListener(new EcoGalleryAdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(EcoGalleryAdapterView<?> parent, View view, int position, long id) {
                pos = position;
                Logger.d("Artist", "onItemSelected................." + position);
                // if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB)
                // {

                highlight_zero = 0;
                SparseBooleanArray checked = mListView.getCheckedItemPositions();
                if (checked != null)
                    for (int i = 0; i < checked.size(); i++) {
                        if (checked.valueAt(i)) {
                            mListView.setItemChecked(checked.keyAt(i), false);
                        }
                    }
                if (mListView.getChoiceMode() == ListView.CHOICE_MODE_NONE)
                    mListView.setChoiceMode(ListView.CHOICE_MODE_NONE);
                OnRefreshListView();
                // ab.OnUpdate(play);
                checked = null;

                new LazyLoad().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, img.get(position).getArtist());

            }

            @Override
            public void onNothingSelected(EcoGalleryAdapterView<?> parent) {

            }
        });
    }

    class LazyLoad extends AsyncTask<String, String, ArrayList<SongDetails>> {
        @Override
        protected void onPreExecute() {
            // album2.setText(img.get(pos).getAlbum());
            play.clear();
            OnRefreshListView();
            super.onPreExecute();
        }

        @Override
        protected ArrayList<SongDetails> doInBackground(String... params) {
            return addToLisView(pos, params[0]);
        }

        @Override
        protected void onPostExecute(ArrayList<SongDetails> x) {
            if (x != null) {
                play.addAll(x);
                // album2.setText(img.get(pos).getAlbum());
                OnRefreshListView();
            }
        }
    }

    private ArrayList<SongDetails> addToLisView(int position, String sexy) {
        ArrayList<SongDetails> list = new ArrayList<SongDetails>();
        this.sexy = sexy;
        String[] columns = {MediaStore.Audio.Media.DATA, MediaStore.Audio.Media._ID, MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.DURATION, MediaStore.Audio.Media.DISPLAY_NAME, MediaStore.MediaColumns._ID};

        String where = MediaStore.Audio.Media.ALBUM + "=?" + "AND " + MediaStore.Audio.Media.ARTIST + "=?";// +cursor2.getString(0);
        String whereVal[] = {img.get(position).getAlbum(), img.get(position).getArtist()};

        // String orderBy = android.provider.MediaStore.Audio.Media.TRACK;//MediaStore.Audio.Media.TITLE;
        String sortOrder = appSettings.getGenreSortKey();
        if (Utilities.IsEmpty(sortOrder)) {
            sortOrder = MediaStore.Audio.Media.TRACK;// android.provider.MediaStore.Audio.Media.TITLE;
        }

        Cursor cursor = null;
        try {
            cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, columns, where, whereVal, sortOrder);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    SongDetails pl = new SongDetails();
                    pl.setID(cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID)));
                    pl.setSong(cursor.getString(2));
                    pl.setPath2(cursor.getString(0));
                    pl.setArtist(cursor.getString(4));
                    pl.setAlbum(cursor.getString(3));
                    pl.setsortBy(pl.getAlbum());
                    pl.setTime(cursor.getString(5));
                    try {
                        int intTime = Integer.parseInt(cursor.getString(5));
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
                    list.add(pl);
                }
                while (cursor.moveToNext());
            }
        } catch (SQLiteException e) {
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        return list;
    }

    class FetchListItems extends AsyncTask<Integer, Void, Void> {
        int duration;
        ArrayList<SongDetails> img__ = new ArrayList<SongDetails>();
        ArrayList<String> albumIdArray = new ArrayList<String>();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            duration = appSettings.getDurationFilterTime();
        }

        @Override
        protected Void doInBackground(Integer... arg0) {
            Uri uri = MediaStore.Audio.Genres.Members.getContentUri("external", Long.parseLong(genre_id));
            final String[] columns = {MediaStore.Audio.Genres.Members.ALBUM_ID, MediaStore.Audio.Genres.Members.ALBUM,
                    MediaStore.Audio.Genres.Members.ARTIST, MediaStore.Audio.Genres.Members.DATA, MediaStore.Audio.AudioColumns.DURATION};
            Cursor cursor = null;

            try {
                cursor = getContentResolver().query(uri, columns, MediaStore.Audio.Media.DURATION + ">=" + duration, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    do {
                        String albumId = cursor.getString(0);
                        if (!albumIdArray.contains(albumId)) {
                            albumIdArray.add(albumId);
                            SongDetails alb = new SongDetails();
                            alb.setAlbum(cursor.getString(1));
                            alb.setArtist(cursor.getString(2));

                            String[] columns1 = {MediaStore.Audio.Albums.ALBUM_ART};
                            Cursor cursor1 = getContentResolver().query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, columns1,
                                    MediaStore.Audio.Albums._ID + "=?", new String[]{String.valueOf(albumId)}, null);
                            if (cursor1.moveToFirst()) {
                                alb.setPath2(cursor1.getString(0));
                                cursor1.close();
                            }
                            img__.add(alb);
                        }
                    }
                    while (cursor.moveToNext());
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                    cursor = null;
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            if (img != null) {
                img.clear();
            } else {
                img = new ArrayList<SongDetails>();
            }
            img.addAll(img__);
            img__.clear();

            if (Utilities.isEmpty(genre_title)) {
                tV1.setText(getString(R.string.no_songs));
            } else {
                tV1.setText(genre_title);
            }

            EcoGallery ecoGallery = (EcoGallery) findViewById(R.id.gallery);
            try {
                ecoGallery.setAdapter(new Adapter_gallery(img));
                setupListeners(ecoGallery);
            } catch (NullPointerException e) {
            }

            // if (img2 != null)
            {
                // img2.clear();
            }
            // else
            {
                // img2 = new ArrayList < String >();
            }
            // img2.addAll(img2__);
            // img2__.clear();

            // if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB)
            // {
            // new tV1Task().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void) null);
            // }
            // else
            // {
            // new tV1Task().execute((Void) null);
            // }
        }
    }

    class tV1Task extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... arg0) {
            String sexy = null;
            Uri uri2 = MediaStore.Audio.Genres.Members.getContentUri("external", Long.parseLong(genre_id));
            String[] columns2 = {MediaStore.Audio.Genres.Members.ARTIST, MediaStore.Audio.Genres.Members.ARTIST_ID};
            Cursor cursor2 = null;
            try {
                cursor2 = getContentResolver().query(uri2, columns2, null, null, null);
                if (cursor2 != null && cursor2.moveToFirst()) {
                    sexy = cursor2.getString(0);
                }
            } finally {
                if (cursor2 != null) {
                    cursor2.close();
                    cursor2 = null;
                }
            }
            return sexy;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (Utilities.isEmpty(result)) {
                tV1.setText(getString(R.string.no_songs));
            } else {
                // tV1.setText(result);
                tV1.setText(genre_title);

                EcoGallery ecoGallery = (EcoGallery) findViewById(R.id.gallery);
                ecoGallery.setAdapter(new Adapter_gallery(img));
                // setAlbum(cursor.getString(0));//name of album
                // setPath2(cursor.getString(1));//path of image
                // setClick_no(cursor.getString(2));// number of songs.

                setupListeners(ecoGallery, result);
            }
        }
    }

    private void SetupButton() {
        bRAdd.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View vv) {
                if (multiplecheckedListforaddtoplaylist != null) {
                    multiplecheckedListforaddtoplaylist.clear();
                }
                SparseBooleanArray checked = mListView.getCheckedItemPositions();
                multiplecheckedListforaddtoplaylist = RayMenu_Functions.getCheckedList(highlight_zero, checked, play, ab);

                if (multiplecheckedListforaddtoplaylist.size() > 0) {
                    Adapter_playlist_Dialog ab;

                    dialog2 = Utilities.designdialog(330, Genre.this);
                    dialog2.setContentView(R.layout.dialog_queue);
                    dialog2.show();
                    pl = Utilities.generatePlaylists(Genre.this.getApplicationContext());
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
                                GlobalSongList.GetInstance().Add2NowPlaying(multiplecheckedListforaddtoplaylist);
                                ActivityUtil.showCrouton(Genre.this, String.format(getString(R.string.f_song_added_to_queue),
                                        multiplecheckedListforaddtoplaylist.size() > 1 ? multiplecheckedListforaddtoplaylist.size() : 1));
                            } else if (position == 1) {
                                create_new_playlist();
                            } else {
                                // id of the playlist
                                String plId = pl.get(position).getArtist();
                                playlistid = Integer.parseInt(plId);
                                // if (android.os.Build.VERSION.SDK_INT >=
                                // android.os.Build.VERSION_CODES.HONEYCOMB)
                                // {
                                new AddToPlayListMultiple().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void) null);
                                // }
                                // else
                                // {
                                // new AddToPlayListMultiple().execute((Void
                                // ) null);
                                // }
                            }
                            dialog2.dismiss();
                        }
                    });
                } else {
                    ActivityUtil.showCrouton(Genre.this, getString(R.string.select_an_item_to_add));
                }
            }
        });

        bRDel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                SparseBooleanArray checked = mListView.getCheckedItemPositions();
                ArrayList<SongDetails> checkedList = RayMenu_Functions.getCheckedList(highlight_zero, checked, play, ab);
                if (checkedList.size() > 0) {
                    delete_song_multiple(checkedList);
                } else {
                    ActivityUtil.showCrouton(Genre.this, getString(R.string.select_an_item_to_play));
                }
            }
        });

        bRPlay.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                SparseBooleanArray checked = mListView.getCheckedItemPositions();
                ArrayList<SongDetails> checkedList = RayMenu_Functions.getCheckedList(highlight_zero, checked, play, ab);
                if (checkedList.size() > 0) {

                    Intent intent = new Intent(Genre.this, Player.class);
                    GlobalSongList.GetInstance().setPosition(0);
                    // intent.putParcelableArrayListExtra("Data1", adapter.GetData(
                    // ));
                    // intent.putExtra("Data2", position);
                    if (checkedList.size() > 800) {
                        GlobalSongList.GetInstance().SetNowPlayingList(checkedList);
                    } else {
                        GlobalSongList.GetInstance().SetNowPlayingList(new ArrayList<SongDetails>(checkedList));
                    }

                    GlobalSongList.GetInstance().setCheck(0);
                    startActivity(intent);
                } else {
                    ActivityUtil.showCrouton(Genre.this, getString(R.string.select_an_item_to_play));
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
                        ab.OnUpdate(play);
                        checked = null;

                        mListView.setOnItemClickListener(new OnItemClickListener() {
                            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                                Intent intent = new Intent(Genre.this, Player.class);
                                GlobalSongList.GetInstance().setPosition(position);
                                // intent.putParcelableArrayListExtra("Data1", adapter.GetData(
                                // ));
                                // intent.putExtra("Data2", position);
                                // if (adapter.GetData().size() > 800)
                                // {
                                GlobalSongList.GetInstance().SetNowPlayingList(ab.GetData());
                                // }
                                // else
                                // {
                                // GlobalSongList.GetInstance().SetNowPlayingList(new ArrayList < SongDetails >(adapter.GetData()));
                                // }
                                GlobalSongList.GetInstance().setCheck(0);
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

    public void create_new_playlist() { // TODO
        final Dialog dialog;
        dialog = new Dialog(Genre.this, R.style.playmee);
        final Window window = dialog.getWindow();
        float pixels = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 210, this.getResources().getDisplayMetrics());
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, (int) pixels);
        window.setBackgroundDrawableResource(R.drawable.dialogbg);
        // window.setBackgroundDrawable(new ColorDrawable(0x99000000));
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        ;
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
                if (save.getText().toString() != null) {
                    int YOUR_PLAYLIST_ID = Now_Playing.createPlaylist(save.getText().toString(), Genre.this);
                    if (YOUR_PLAYLIST_ID == -1)
                        return;
                    // TODO
                    // if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB)
                    // {
                    new AddToPl().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, YOUR_PLAYLIST_ID);
                    // }
                    // else
                    // {
                    // new AddToPl().execute(YOUR_PLAYLIST_ID);
                    // }
                }
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

    class AddToPl extends AsyncTask<Integer, String, String> {
        @Override
        protected void onPostExecute(String x) {
            spinner.setVisibility(View.GONE);
            ActivityUtil.showCrouton(Genre.this, getString(R.string.playlist_created));
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
                    Now_Playing.addToPlaylist(getApplicationContext(), multiplecheckedListforaddtoplaylist.get(index).getPath2(),
                            params[0].intValue());
                } catch (IllegalStateException e) {
                    ActivityUtil.showCrouton(Genre.this, getString(R.string.playlist_name_already_exists));
                    return null;
                } catch (Exception e) {
                    ActivityUtil.showCrouton(Genre.this, getString(R.string.an_error_occurred));
                    return null;
                }
            }

            return null;
        }
    }

    private void SetupRayMenu() {
        int[] ITEM_DRAWABLES = {R.drawable.composer_button_multiselect, R.drawable.composer_button_sort, R.drawable.composer_button_shuffle};
        for (int i = 0; i < ITEM_DRAWABLES.length; i++) {
            ImageView item = new ImageView(this);
            item.setTag(ITEM_DRAWABLES[i]);
            item.setImageResource(ITEM_DRAWABLES[i]);
            rayMenu.addItem(item, new OnClickListener() {

                @Override
                public void onClick(View v) {
                    final int resID = (Integer) v.getTag();
                    switch (resID) {
                        case R.drawable.composer_button_multiselect: {
                            fadeout(00, 700);
                            ActivityUtil.showCrouton(Genre.this, getString(R.string.multi_select_initiated));
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
                                            ab.OnUpdate(play);
                                        }

                                        private void highlight(int position) {
                                            mListView.setItemChecked(position, true);
                                            ab.OnUpdate(play);
                                            return;
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
                            if (play != null && play.size() > 0) {
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        RayMenu_Functions.sort("-2", Genre.this, ab, play, mListView, 1, play.get(0).getAlbum(), sexy);
                                        // 1 for album
                                    }
                                }, 760);
                            }
                            break;
                        }
                        case R.drawable.composer_button_shuffle: {
                            if (play != null && play.size() > 0) {
                                Collections.shuffle(play);
                            }
                            handler.removeCallbacksAndMessages(null);
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    // delay "shuffle and play" so that animation is
                                    // smooth
                                    Intent intent = new Intent(Genre.this, Player.class);
                                    GlobalSongList.GetInstance().setPosition(0);
                                    // intent.putParcelableArrayListExtra("Data1",
                                    // adapter.GetData(
                                    // ));
                                    // intent.putExtra("Data2", position);
                                    if (play.size() > 800) {
                                        GlobalSongList.GetInstance().SetNowPlayingList(play);
                                    } else {
                                        GlobalSongList.GetInstance().SetNowPlayingList(new ArrayList<SongDetails>(play));
                                    }

                                    GlobalSongList.GetInstance().setCheck(0);
                                    startActivity(intent);
                                }
                            }, 540);
                            break;
                        }
                        // case 3:
                        // {
                        // fadein( 0, 700 );
                        // fadeIn.setAnimationListener( new AnimationListener( ) {
                        //
                        // @Override
                        // public void onAnimationStart( Animation animation )
                        // {
                        // }
                        //
                        // @Override
                        // public void onAnimationEnd( Animation animation )
                        // {
                        //
                        // }
                        //
                        // @Override
                        // public void onAnimationRepeat( Animation animation )
                        // {
                        // }
                        // } );
                        //
                        // fadeOut.setAnimationListener( new AnimationListener( ) {
                        // @Override
                        // public void onAnimationEnd( Animation animation )
                        // {
                        //
                        // rayMenu.setVisibility( View.INVISIBLE );
                        // }
                        //
                        // @Override
                        // public void onAnimationStart( Animation animation )
                        // {
                        // }
                        //
                        // @Override
                        // public void onAnimationRepeat( Animation animation )
                        // {
                        // }
                        // } );
                        // rayMenu.startAnimation( fadeOut );
                        //
                        // break;
                        // }
                        default:
                            break;
                    }
                }
            });
        }
    }

    protected void delete_song_multiple(final ArrayList<SongDetails> checkedList) {
        dialog2 = Utilities.designdialog(210, Genre.this);


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
                    SongHelper.DeleteSongMultiple(Genre.this, checkedList, i, null, null);

                    play.remove(checkedList.get(i));
                    ab.OnUpdate(play);
                }
                highlight_zero = 0;
                ActivityUtil.showCrouton(Genre.this, getString(R.string.delete_successful));
                ab.OnUpdate(play);
                for (int i = 0; i < checkedList.size(); i++) {
                    try {
                        getContentResolver().delete(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, MediaStore.MediaColumns.DATA + "=?",
                                new String[]{checkedList.get(i).getPath2()});
                    } catch (SQLiteException e) {
                    }
                    // try
                    // {
                    // MediaScannerConnection.scanFile( Genre.this, new String [ ] { checkedList.get( i ).getPath2( ) }, null,
                    // new MediaScannerConnection.OnScanCompletedListener( ) {
                    // public void onScanCompleted( String path, Uri uri )
                    // {
                    // Logger.i( "ExternalStorage", "Scanned " + path + ":" );
                    // Logger.i( "ExternalStorage", "-> uri=" + uri );
                    // Genre.this.getContentResolver( ).delete( uri, null, null );
                    // }
                    // } );
                    // }
                    // catch ( Exception e )
                    // {
                    // e.printStackTrace( );
                    // }
                }
                dialog2.dismiss();
            }
        });
        dialog2.show();
    }

    @Override
    public String GetActivityID() {
        return "Genre";
    }

    @Override
    public void SetNullForCustomVariable() {
        songHelper = null;
    }

    @Override
    public int GetRootViewID() {
        return R.id.ll;
    }

    @Override
    public void OnBackPressed() {
        if (songHelper != null) {
            songHelper.ShouldDismiss();
        }
        finish();
    }

    @Override
    public void QuickAction_OnRemoveSong() {
    }

    @Override
    public void QuickAction_OnSendSong() {
    }

    @Override
    protected String GetGAScreenName() {
        return "Genre";
    }

    class AddToPlayListMultiple extends AsyncTask<Void, String, String> {
        @Override
        protected void onPostExecute(String x) {
            spinner.setVisibility(View.GONE);
            multiplecheckedListforaddtoplaylist.clear();
            highlight_zero = 0;
            ActivityUtil.showCrouton(Genre.this, getString(R.string.songs_were_added_to_playlist));
            SparseBooleanArray checked = mListView.getCheckedItemPositions();
            if (checked != null)
                for (int i = 0; i < checked.size(); i++) {
                    if (checked.valueAt(i)) {
                        mListView.setItemChecked(checked.keyAt(i), false);
                    }
                }
            mListView.setChoiceMode(ListView.CHOICE_MODE_NONE);
            mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
            ab.OnUpdate(play);
            checked = null;
        }

        @Override
        protected void onPreExecute() {
            spinner.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            for (int i = 0; i < multiplecheckedListforaddtoplaylist.size(); i++)
                Now_Playing.addToPlaylist(Genre.this.getApplicationContext(), multiplecheckedListforaddtoplaylist.get(i).getPath2(), playlistid);

            return null;
        }
    }
}
