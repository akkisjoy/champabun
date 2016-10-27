package champak.champabun.view.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.graphics.PorterDuff;
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
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import champak.champabun.AmuzicgApp;
import champak.champabun.R;
import champak.champabun.business.dataclasses.SongDetails;
import champak.champabun.business.definition.Logger;
import champak.champabun.business.utilities.ecoGallery.EcoGallery;
import champak.champabun.business.utilities.ecoGallery.EcoGalleryAdapterView;
import champak.champabun.business.utilities.rayMenu.RayMenu;
import champak.champabun.business.utilities.utilMethod.ActivityUtil;
import champak.champabun.business.utilities.utilMethod.RayMenu_Functions;
import champak.champabun.business.utilities.utilMethod.SongHelper;
import champak.champabun.business.utilities.utilMethod.StorageAccessAPI;
import champak.champabun.business.utilities.utilMethod.Utilities;
import champak.champabun.view.adapters.Adapter_SongView;
import champak.champabun.view.adapters.Adapter_gallery;
import champak.champabun.view.adapters.Adapter_playlist_Dialog;

public class Genre extends BaseActivity implements SongHelper.OnQuickActionItemSelectListener {
    static public int highlight_zero = 0;
    public ListView mListView;
    public ProgressBar spinner;
    String sexy;
    ArrayList<SongDetails> img = new ArrayList<>();
    Adapter_SongView ab;
    String genre_id, genre_title;
    ArrayList<SongDetails> play;
    TextView tV1;// album2;
    int pos;
    int playlistid;
    Handler handler = new Handler();
    int position2;
    Dialog dialog2;
    ArrayList<SongDetails> multiplecheckedListforaddtoplaylist, pl;
    Button bRBack, bRAdd, bRPlay, bRDel;
    RayMenu rayMenu;
    Animation fadeOut, fadeIn;
    private SongHelper songHelper;
    private ImageView backPager;

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

        backPager = (ImageView) findViewById(R.id.backPager);
        backPager.setColorFilter(getResources().getColor(R.color.redPager), PorterDuff.Mode.MULTIPLY);

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

        play = new ArrayList<>();
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
                    AmuzicgApp.GetInstance().SetNowPlayingList(play);
                } else {
                    AmuzicgApp.GetInstance().SetNowPlayingList(new ArrayList<SongDetails>(play));
                }
                AmuzicgApp.GetInstance().setPosition(position);
                AmuzicgApp.GetInstance().setCheck(0);
                startActivity(intent);
            }
        });

        animate_ListView();
        Intent i = getIntent();

        genre_id = i.getStringExtra("click_no");
        genre_title = i.getStringExtra("genre_title");

        int s = Integer.parseInt(genre_id);

        new FetchListItems().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, s);
        tV1 = (TextView) findViewById(R.id.tV1);
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
        songHelper.EditTags(play.get(position2), new SongHelper.OnEditTagsListener() {

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
                if (resultCode == Activity.RESULT_OK) {
                    StorageAccessAPI.onActivityResult(requestCode, resultCode, intent, Genre.this);

                    File file = new File(play.get(position2).getPath2());
                    boolean canWrite;
                    try {
                        canWrite = StorageAccessAPI.getDocumentFile(file, false).canWrite();
                    } catch (Exception e) {
                        canWrite = false;
                    }
                    if (canWrite) {
                        songHelper.EditTags(play.get(position2), new SongHelper.OnEditTagsListener() {

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

                new LazyLoad().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, img.get(position).getArtist());

            }

            @Override
            public void onNothingSelected(EcoGalleryAdapterView<?> parent) {

            }
        });
    }

    private ArrayList<SongDetails> addToLisView(int position, String sexy) {
        ArrayList<SongDetails> list = new ArrayList<SongDetails>();
        this.sexy = sexy;
        String[] columns = {MediaStore.Audio.Media.DATA, MediaStore.Audio.Media._ID, MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.DURATION, MediaStore.Audio.Media.DISPLAY_NAME, MediaStore.MediaColumns._ID};

        String where = MediaStore.Audio.Media.ALBUM + "=?" + "AND " + MediaStore.Audio.Media.ARTIST + "=?";// +cursor2.getString(0);
        String whereVal[] = {img.get(position).getAlbum(), img.get(position).getArtist()};

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
                    } catch (Exception ignored) {
                    }
                    list.add(pl);
                }
                while (cursor.moveToNext());
            }
        } catch (SQLiteException e) {
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return list;
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
                    ab = new Adapter_playlist_Dialog(pl);
                    ListView dlgLV = (ListView) dialog2.findViewById(R.id.listView1);
                    dlgLV.setAdapter(ab);
                    dlgLV.setOnItemClickListener(new OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            if (position == 0) {
                                AmuzicgApp.GetInstance().Add2NowPlaying(multiplecheckedListforaddtoplaylist);
                                ActivityUtil.showCrouton(Genre.this, String.format(getString(R.string.f_song_added_to_queue),
                                        multiplecheckedListforaddtoplaylist.size() > 1 ? multiplecheckedListforaddtoplaylist.size() : 1));
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
                    AmuzicgApp.GetInstance().setPosition(0);
                    if (checkedList.size() > 800) {
                        AmuzicgApp.GetInstance().SetNowPlayingList(checkedList);
                    } else {
                        AmuzicgApp.GetInstance().SetNowPlayingList(new ArrayList<SongDetails>(checkedList));
                    }

                    AmuzicgApp.GetInstance().setCheck(0);
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
                fadeout(0, 700);

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

                        mListView.setOnItemClickListener(new OnItemClickListener() {
                            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                                Intent intent = new Intent(Genre.this, Player.class);
                                AmuzicgApp.GetInstance().setPosition(position);
                                AmuzicgApp.GetInstance().SetNowPlayingList(ab.GetData());
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

    public void create_new_playlist() { // TODO
        final Dialog dialog;
        dialog = new Dialog(Genre.this, R.style.AmuzeTheme);
        final Window window = dialog.getWindow();
        float pixels = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 210, this.getResources().getDisplayMetrics());
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, (int) pixels);
        window.setBackgroundDrawableResource(R.drawable.dialogbg);
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
                if (save.getText().toString() != null) {
                    int YOUR_PLAYLIST_ID = NowPlaying.createPlaylist(save.getText().toString(), Genre.this);
                    if (YOUR_PLAYLIST_ID == -1)
                        return;
                    new AddToPl().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, YOUR_PLAYLIST_ID);
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
                                    AmuzicgApp.GetInstance().setPosition(0);
                                    if (play.size() > 800) {
                                        AmuzicgApp.GetInstance().SetNowPlayingList(play);
                                    } else {
                                        AmuzicgApp.GetInstance().SetNowPlayingList(new ArrayList<SongDetails>(play));
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
                    } catch (SQLiteException ignored) {
                    }
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

    class LazyLoad extends AsyncTask<String, String, ArrayList<SongDetails>> {
        @Override
        protected void onPreExecute() {
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
                OnRefreshListView();
            }
        }
    }

    class FetchListItems extends AsyncTask<Integer, Void, Void> {
        int duration;
        ArrayList<SongDetails> img__ = new ArrayList<>();
        ArrayList<String> albumIdArray = new ArrayList<>();

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
                            if (cursor1 != null && cursor1.moveToFirst()) {
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
                img = new ArrayList<>();
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
            } catch (NullPointerException ignored) {
            }
        }
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
                    NowPlaying.addToPlaylist(getApplicationContext(), multiplecheckedListforaddtoplaylist.get(index).getPath2(),
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
        }

        @Override
        protected void onPreExecute() {
            spinner.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            for (int i = 0; i < multiplecheckedListforaddtoplaylist.size(); i++)
                NowPlaying.addToPlaylist(Genre.this.getApplicationContext(), multiplecheckedListforaddtoplaylist.get(i).getPath2(), playlistid);

            return null;
        }
    }
}
