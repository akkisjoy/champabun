package champak.champabun;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.SparseBooleanArray;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.inputmethod.InputMethodManager;
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

import champak.champabun.adapters.Activity_Fragments;
import champak.champabun.adapters.Adapter_SongView;
import champak.champabun.adapters.Adapter_playlist_Dialog;
import champak.champabun.classes.DownloadProVersion;
import champak.champabun.classes.RateThisApp;
import champak.champabun.classes.SongDetails;
import champak.champabun.raymenu.RayMenu;
import champak.champabun.service.Music_service;
import champak.champabun.ui.TypefaceTextView;
import champak.champabun.util.ActivityUtil;
import champak.champabun.util.RayMenu_Functions;
import champak.champabun.util.SongHelper;
import champak.champabun.util.StorageAccessAPI;
import champak.champabun.util.Utilities;

public class F_Songs extends BaseFragment implements SongHelper.OnQuickActionItemSelectListener {
    static final int ANIMATION_DURATION = 300;
    static public int highlight_zero = 0;
    public ProgressBar spinner;
    int whichanimation = 0;
    int playlistid;
    Animation fadeOut, fadeIn;
    ArrayList<SongDetails> songdetails;
    TypefaceTextView songs, album2;
    ArrayList<SongDetails> multiplecheckedListforaddtoplaylist, pl;
    View openactivity;
    Button bRBack, bRAdd, bRPlay, bRDel, bRBackSearch;
    RayMenu rayMenu;

    ImageView play2, backPager;
    AdapterView.AdapterContextMenuInfo info;
    String oldalbum, oldsong;
    public BroadcastReceiver broadcastCoverReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent serviceIntent) {
            // check that whether the audio file can play or not
            if (!serviceIntent.getBooleanExtra("setDataSourceFailed", false)) {
                // ok, the audio file can play
                inandout();
            }
        }
    };
    Handler handler = new Handler();
    Dialog dialog2;
    Fragment fragment;
    ListView mListView;
    Adapter_SongView adapter;
    int i, curItemSelect;
    View local;
    private EditText searchView;
    private Activity_Fragments mActivity;
    private SongHelper songHelper;
    // private Dialog dialog;
    private FetchSongList fetchSongList;
    private BroadcastReceiver checkagain = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent serviceIntent) {
            checkbuttonplaypause();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragment = this;
        if (savedInstanceState != null) {
            songdetails = savedInstanceState.getParcelableArrayList("F_Songs.songdetails");
        } else {
            songdetails = new ArrayList<SongDetails>();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.l_songs, container, false);

        oldsong = getString(R.string.total_songs);
        mActivity = (Activity_Fragments) getActivity();

        backPager = (ImageView) view.findViewById(R.id.backPager);
        backPager.setColorFilter(getResources().getColor(R.color.pinkPager), PorterDuff.Mode.MULTIPLY);
        mActivity.registerReceiver(broadcastCoverReceiver, new IntentFilter(IConstant.BROADCAST_COVER));
        songs = (TypefaceTextView) view.findViewById(R.id.songstop);
        album2 = (TypefaceTextView) view.findViewById(R.id.albumtop);
        openactivity = view.findViewById(R.id.openactivity);
        if (!IConstant.IS_PRO_VERSION) {
            DownloadProVersion.onStart(getActivity());
            // Show a dialog if criteria is satisfied
            DownloadProVersion.showRateDialogIfNeeded(getActivity());
        }

        RateThisApp.onStart(getActivity());
        // Show a dialog if criteria is satisfied
        RateThisApp.showRateDialogIfNeeded(getActivity());

        spinner = (ProgressBar) view.findViewById(R.id.progressBar1);
        spinner.setVisibility(View.GONE);

        bRBack = (Button) view.findViewById(R.id.bRBack);
        bRBackSearch = (Button) view.findViewById(R.id.bRBackSearch);
        bRAdd = (Button) view.findViewById(R.id.bRAdd);
        bRPlay = (Button) view.findViewById(R.id.bRPlay);
        bRDel = (Button) view.findViewById(R.id.bRDelete);

        bRBack.setVisibility(View.INVISIBLE);
        bRBackSearch.setVisibility(View.INVISIBLE);
        bRAdd.setVisibility(View.INVISIBLE);
        bRPlay.setVisibility(View.INVISIBLE);
        bRDel.setVisibility(View.INVISIBLE);

        rayMenu = (RayMenu) view.findViewById(R.id.ray);

        play2 = (ImageView) view.findViewById(R.id.play2);
        mListView = (ListView) view.findViewById(R.id.list);

        getActivity().registerReceiver(checkagain, new IntentFilter(IConstant.BROADCAST_CHECK_AGAIN));

        searchView = (EditText) view.findViewById(R.id.search_view);
        SetupSearchView();

        openactivity.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (GlobalSongList.GetInstance().GetNowPlayingSize() > 0) {
                    Intent intent = new Intent(mActivity, Player.class);
                    intent.putExtra("shouldStartCoverOperation", true);
                    startActivity(intent);
                }
            }
        });

        SetupButton();
        SetupRawMenu();
        mListView.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                curItemSelect = arg2;
                if (songHelper == null) {
                    songHelper = new SongHelper();
                }
                songHelper.Show(mActivity, getView(), F_Songs.this, SongHelper.F_SONG);
                return true;
            }
        });
        mListView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                Intent intent = new Intent(mActivity, Player.class);
                GlobalSongList.GetInstance().setPosition(position);
                if (adapter.GetData().size() > 800) {
                    GlobalSongList.GetInstance().SetNowPlayingList(adapter.GetData());
                } else {
                    GlobalSongList.GetInstance().SetNowPlayingList(new ArrayList<SongDetails>(adapter.GetData()));
                }
                GlobalSongList.GetInstance().setCheck(0);
                startActivity(intent);
            }
        });
        if (songdetails == null || songdetails.size() == 0) {
            fetchSongList = new FetchSongList();
            fetchSongList.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void) null);
        }

        return view;
    }

    private void SetupButton() {
        bRAdd.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View vv) {
                if (multiplecheckedListforaddtoplaylist != null) {
                    multiplecheckedListforaddtoplaylist.clear();
                }
                SparseBooleanArray checked = mListView.getCheckedItemPositions();
                multiplecheckedListforaddtoplaylist = RayMenu_Functions.getCheckedList(highlight_zero, checked, songdetails, adapter);

                if (multiplecheckedListforaddtoplaylist.size() > 0) {
                    Adapter_playlist_Dialog ab;

                    dialog2 = Utilities.designdialog(400, mActivity);

                    //LayoutInflater li = ( LayoutInflater ) mActivity.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
                    //View list = li.inflate( , null, false );
                    dialog2.setContentView(R.layout.dialog_queue);
                    dialog2.show();
                    pl = Utilities.generatePlaylists(mActivity.getApplicationContext());
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
                                ActivityUtil.showCrouton(mActivity, String.format(getString(R.string.f_song_added_to_queue),
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
                    ActivityUtil.showCrouton(mActivity, getString(R.string.select_an_item_to_add));
                }
            }
        });

        bRDel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                SparseBooleanArray checked = mListView.getCheckedItemPositions();
                ArrayList<SongDetails> checkedList = RayMenu_Functions.getCheckedList(highlight_zero, checked, songdetails, adapter);
                if (checkedList.size() > 0) {
                    delete_song_multiple(checkedList);
                } else {
                    ActivityUtil.showCrouton(mActivity, getString(R.string.select_an_item_to_play));
                }
            }
        });

        bRPlay.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                SparseBooleanArray checked = mListView.getCheckedItemPositions();
                ArrayList<SongDetails> checkedList = RayMenu_Functions.getCheckedList(highlight_zero, checked, songdetails, adapter);
                if (checkedList.size() > 0) {

                    Intent intent = new Intent(mActivity, Player.class);
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
                    ActivityUtil.showCrouton(mActivity, getString(R.string.select_an_item_to_play));
                }
                checkedList.clear();
            }
        });
        bRBackSearch.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                fadeout(00, 700);
                bRBackSearch.startAnimation(fadeOut);
                searchView.startAnimation(fadeOut);
                fadeOut.setAnimationListener(new AnimationListener() {
                    @Override
                    public void onAnimationEnd(Animation animation) {
                        fadein(0, 700);
                        rayMenu.startAnimation(fadeIn);
                        bRBackSearch.setVisibility(View.GONE);
                        searchView.setVisibility(View.GONE);
                        InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
                        adapter.getFilter().filter(null);
                    }

                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });

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
                        adapter.OnUpdate(songdetails);
                        checked = null;

                        mListView.setOnItemClickListener(new OnItemClickListener() {
                            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                                Intent intent = new Intent(mActivity, Player.class);
                                GlobalSongList.GetInstance().setPosition(position);
                                // intent.putParcelableArrayListExtra("Data1", adapter.GetData(
                                // ));
                                // intent.putExtra("Data2", position);
                                // if (adapter.GetData().size() > 800)
                                // {
                                GlobalSongList.GetInstance().SetNowPlayingList(adapter.GetData());
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

        play2.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                buttonPlayStopClick();
            }
        });
    }

    // private void set_listview_animation()
    // {
    // AnimationSet set = new AnimationSet(true);
    //
    // Animation animation = new AlphaAnimation(0.0f, 1.0f);
    // animation.setDuration(600);
    // set.addAnimation(animation);
    //
    // animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
    // Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, -1.0f,
    // Animation.RELATIVE_TO_SELF, 0.0f);
    // animation.setDuration(600);
    // set.addAnimation(animation);
    //
    // LayoutAnimationController controller = new LayoutAnimationController(
    // set, 0.25f);
    // mListView.setLayoutAnimation(controller);
    // }

    private void SetupRawMenu() {
        int[] ITEM_DRAWABLES = {R.drawable.composer_button_multiselect, R.drawable.composer_button_sort, R.drawable.composer_button_shuffle,
                R.drawable.composer_icn_search, R.drawable.composer_icn_settings};
        for (int i = 0; i < ITEM_DRAWABLES.length; i++) {
            ImageView item = new ImageView(mActivity);
            item.setTag(ITEM_DRAWABLES[i]);
            item.setImageResource(ITEM_DRAWABLES[i]);
            rayMenu.addItem(item, new OnClickListener() {

                @Override
                public void onClick(View v) {
                    final int resID = (Integer) v.getTag();
                    switch (resID) {
                        case R.drawable.composer_button_multiselect: {
                            fadeout(00, 700);
                            ActivityUtil.showCrouton(mActivity, getString(R.string.multi_select_initiated));
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
                                                highlight2(position);
                                            }
                                            if (position > 0) {
                                                highlight();
                                            }
                                        }

                                        private void highlight2(int position) {
                                            highlight_zero = highlight_zero + 1;
                                            mListView.setItemChecked(position, true);
                                            adapter.OnUpdate(songdetails);
                                            return;
                                        }

                                        private void highlight() {
                                            mListView.setItemChecked(0, true);
                                            adapter.OnUpdate(songdetails);
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
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    RayMenu_Functions.sort("-10", mActivity, adapter, songdetails, mListView, 2, null, null);
                                }
                            }, 760);
                            break;
                        }
                        case R.drawable.composer_button_shuffle: {
                            if (songdetails != null && songdetails.size() > 0) {
                                Collections.shuffle(songdetails);
                            }
                            handler.removeCallbacksAndMessages(null);
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    // delay "shuffle and play" so that animation is smooth
                                    Intent intent = new Intent(mActivity, Player.class);
                                    GlobalSongList.GetInstance().setPosition(0);
                                    // intent.putParcelableArrayListExtra("Data1", adapter.GetData(
                                    // ));
                                    // intent.putExtra("Data2", position);
                                    if (songdetails.size() > 800) {
                                        GlobalSongList.GetInstance().SetNowPlayingList(songdetails);
                                    } else {
                                        GlobalSongList.GetInstance().SetNowPlayingList(new ArrayList<SongDetails>(songdetails));
                                    }

                                    GlobalSongList.GetInstance().setCheck(0);
                                    startActivity(intent);
                                }
                            }, 540);
                            break;
                        }
                        case R.drawable.composer_icn_search: {
                            fadein(0, 700);
                            fadeIn.setAnimationListener(new AnimationListener() {

                                @Override
                                public void onAnimationStart(Animation animation) {
                                }

                                @Override
                                public void onAnimationEnd(Animation animation) {
                                    searchView.setVisibility(View.VISIBLE);
                                    searchView.requestFocus();
                                    InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                                    imm.showSoftInput(searchView, InputMethodManager.SHOW_IMPLICIT);
                                }

                                @Override
                                public void onAnimationRepeat(Animation animation) {
                                }
                            });

                            fadeOut.setAnimationListener(new AnimationListener() {
                                @Override
                                public void onAnimationEnd(Animation animation) {
                                    bRBackSearch.setVisibility(View.INVISIBLE);
                                    bRBackSearch.startAnimation(fadeIn);
                                    searchView.setVisibility(View.INVISIBLE);
                                    searchView.startAnimation(fadeIn);

                                    rayMenu.setVisibility(View.INVISIBLE);
                                }

                                @Override
                                public void onAnimationStart(Animation animation) {
                                }

                                @Override
                                public void onAnimationRepeat(Animation animation) {
                                }
                            });
                            rayMenu.startAnimation(fadeOut);

                            break;
                        }
                        case R.drawable.composer_icn_settings: {
                            handler.removeCallbacksAndMessages(null);
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    // open setting
                                    Intent intent = new Intent(getActivity(), Settings.class);
                                    getActivity().startActivityForResult(intent, IConstant.REQUEST_CODE_CHANGE_SETTINGS);
                                }
                            }, 760);
                            break;
                        }
                        default:
                            break;
                    }
                }
            });
        }
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
    public void QuickAction_OnPlaySong() {
        songHelper.PlaySong(adapter.GetData(), curItemSelect);
    }

    @Override
    public void QuickAction_OnAdd2Playlist() {
        songHelper.Add2Playlist(adapter.getItem(curItemSelect));
    }

    @Override
    public void QuickAction_OnEditTags() {
        songHelper.EditTags(adapter.getItem(curItemSelect), spinner, play2, new SongHelper.OnEditTagsListener() {

            @Override
            public void OnEditTagsSuccessful() {

                OnRefreshSongList();
            }
        }, fragment);
    }

    @Override
    public void QuickAction_OnSetAsRingtone() {
        songHelper.SetAsRingtone(adapter.getItem(curItemSelect));
    }

    @Override
    public void QuickAction_OnViewDetails() {
    }

    @Override
    public void QuickAction_OnDeleteSong() {
        songHelper.DeleteSong(adapter.GetData(), curItemSelect, new SongHelper.OnDeleteSongListener() {

            @Override
            public void OnSongDeleted() {
                OnRefreshSongList();
            }
        }, fragment);
    }

    protected void delete_song_multiple(final ArrayList<SongDetails> checkedList) {
        dialog2 = Utilities.designdialog(210, mActivity);
        //LayoutInflater li = ( LayoutInflater ) mActivity.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        //View v = li.inflate( , null, false );
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
                    SongHelper.DeleteSongMultiple(mActivity, checkedList, i, null, fragment);
                    songdetails.remove(checkedList.get(i));
                    adapter.OnUpdate(songdetails);
                }
                highlight_zero = 0;
                ActivityUtil.showCrouton(mActivity, getString(R.string.delete_successful));
                adapter.OnUpdate(songdetails);
                for (int i = 0; i < checkedList.size(); i++) {
                    try {
                        mActivity.getContentResolver().delete(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, MediaStore.MediaColumns.DATA + "=?",
                                new String[]{checkedList.get(i).getPath2()});
                    } catch (SQLiteException e) {
                    }
                }
                dialog2.dismiss();
            }
        });
        dialog2.show();
    }

    @Override
    public void onResume() {
        super.onResume();

        OnRefreshSongList();
        if (!rayMenu.mRayLayout.isExpanded())
            Animate_raymenu();
        if (GlobalSongList.GetInstance().GetNowPlayingSize() > 0) {
            play2.setVisibility(View.VISIBLE);
            checkbuttonplaypause();
        } else {
            songs.setText(getString(R.string.total_songs));
            play2.setVisibility(View.GONE);
            // album2.setText(String.valueOf(songdetails.size()));
            // fadein(00, 1500);
            // album2.startAnimation(fadeIn);
        }
    }

    private void checkbuttonplaypause() {
        if (GlobalSongList.GetInstance().boolMusicPlaying1 == true) {
            play2.setImageResource(R.drawable.pause2);
        } else {
            play2.setImageResource(R.drawable.play);
        }
        inandout();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("F_Songs.songdetails", songdetails);
        super.onSaveInstanceState(outState);
    }

    protected void inandout() {
        fadein(00, 1500);
        fadeout(00, 1500);

        if (GlobalSongList.GetInstance().GetNowPlayingSize() == 0) {
            album2.setText(String.valueOf(songdetails.size()));
            album2.startAnimation(fadeIn);
        } else {
            if (GlobalSongList.GetInstance().GetCurSongDetails().getSong().equals(oldsong)
                    && GlobalSongList.GetInstance().GetCurSongDetails().getAlbum().equals(oldalbum)) {
            } else if (!GlobalSongList.GetInstance().GetCurSongDetails().getSong().equals(oldsong)
                    && GlobalSongList.GetInstance().GetCurSongDetails().getAlbum().equals(oldalbum)) {
                songs.startAnimation(fadeOut);
                // album2.startAnimation(fadeOut);
                whichanimation = 1;
            } else if (GlobalSongList.GetInstance().GetCurSongDetails().getSong().equals(oldsong)
                    && !GlobalSongList.GetInstance().GetCurSongDetails().getAlbum().equals(oldalbum)) {
                // songs.startAnimation(fadeOut);
                album2.startAnimation(fadeOut);
                whichanimation = 2;
            } else if (!GlobalSongList.GetInstance().GetCurSongDetails().getSong().equals(oldsong)
                    && !GlobalSongList.GetInstance().GetCurSongDetails().getAlbum().equals(oldalbum)) {
                songs.startAnimation(fadeOut);
                album2.startAnimation(fadeOut);
                whichanimation = 3;
            }
            fadeOut.setAnimationListener(new AnimationListener() {
                @Override
                public void onAnimationEnd(Animation animation) {
                    // mSwitcher.setText("New Text");
                    if (GlobalSongList.GetInstance().GetNowPlayingSize() > 0) {
                        if (whichanimation == 1) {
                            songs.setText(GlobalSongList.GetInstance().GetCurSongDetails().getSong());
                            songs.startAnimation(fadeIn);
                        } else if (whichanimation == 2) {
                            album2.setText(GlobalSongList.GetInstance().GetCurSongDetails().getAlbum());
                            album2.startAnimation(fadeIn);
                        } else if (whichanimation == 3) {
                            album2.setText(GlobalSongList.GetInstance().GetCurSongDetails().getAlbum());
                            album2.startAnimation(fadeIn);
                            songs.setText(GlobalSongList.GetInstance().GetCurSongDetails().getSong());
                            songs.startAnimation(fadeIn);
                        }
                    }
                    oldsong = songs.getText().toString();
                    oldalbum = album2.getText().toString();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }

                @Override
                public void onAnimationStart(Animation animation) {
                }
            });
        }
    }

    private void buttonPlayStopClick() {
        if (!GlobalSongList.GetInstance().boolMusicPlaying1) {
            Intent intentplaypause = new Intent(IConstant.BROADCAST_PLAYPAUSE);
            play2.setImageResource(R.drawable.pause2);
            intentplaypause.putExtra("playpause", 1);
            mActivity.sendBroadcast(intentplaypause);
            GlobalSongList.GetInstance().boolMusicPlaying1 = true;
        } else {
            if (GlobalSongList.GetInstance().boolMusicPlaying1) {
                play2.setImageResource(R.drawable.play);
                GlobalSongList.GetInstance().boolMusicPlaying1 = false;
                Intent intentplaypause = new Intent(IConstant.BROADCAST_PLAYPAUSE);
                intentplaypause.putExtra("playpause", 0);
                mActivity.sendBroadcast(intentplaypause);
                GlobalSongList.GetInstance().boolMusicPlaying1 = false;
            }
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onDestroy() {
        super.onDestroy();

        if (songHelper != null) {
            songHelper.ShouldDismiss();
            songHelper = null;
        }
        try {
            mActivity.unregisterReceiver(broadcastCoverReceiver);
        } catch (Exception e) {
        }
        try {
            getActivity().unregisterReceiver(checkagain);
        } catch (Exception e) {
        }

        if (songdetails != null) {
            songdetails.clear();
            songdetails = null;
        }

        try {
            handler.removeCallbacksAndMessages(null);
            handler = null;
        } catch (NullPointerException e) {
        } catch (Exception e) {
        }

        if (pl != null) {
            pl.clear();
            pl = null;
        }
        if (GlobalSongList.GetInstance().boolMusicPlaying1 == false) {
            mActivity.getApplicationContext().stopService(new Intent(this.mActivity, Music_service.class));
            GlobalSongList.GetInstance().CLearNowPlaying();
            System.runFinalizersOnExit(true);
            System.exit(0);
        }
        if (IConstant.USE_SYSTEM_GC) {
            mActivity = null;
            System.gc();
        }
        mActivity = null;
        if (fetchSongList != null && fetchSongList.isCancelled()) {
            fetchSongList.cancel(true);
            fetchSongList = null;
        }
    }

    private void OnRefreshSongList() {
        if (adapter == null) {
            adapter = new Adapter_SongView(songdetails, mActivity, 2);
            mListView.setAdapter(adapter);
        } else {
            adapter.OnUpdate(songdetails);
        }
    }

    private void SetupSearchView() {
        searchView.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Logger.d("F_Songs", "onTextChanged.................. " + searchView.getText().toString() + " s = " + s);

                try {
                    adapter.getFilter().filter(searchView.getText().toString());
                } catch (NullPointerException e) {
                }

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void toggleSearchView() {
        if (searchView.getVisibility() == View.GONE) {
            searchView.setVisibility(View.VISIBLE);
            searchView.requestFocus();
            InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(searchView, InputMethodManager.SHOW_IMPLICIT);
        } else {
            searchView.setVisibility(View.GONE);
            InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
            adapter.getFilter().filter(null);
        }
    }

    @Override
    public void QuickAction_OnRemoveSong() {
    }

    @Override
    public void QuickAction_OnSendSong() {
        SendSong();
    }

    public void create_new_playlist() { // TODO
        final Dialog dialog;
        dialog = new Dialog(mActivity, R.style.playmee);
        final Window window = dialog.getWindow();
        float pixels = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 210, this.getResources().getDisplayMetrics());
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, (int) pixels);
        window.setBackgroundDrawableResource(R.drawable.dialogbg);
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
                if (save.getText().toString() != null) {
                    int YOUR_PLAYLIST_ID = Now_Playing.createPlaylist(save.getText().toString(), mActivity);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent)

    {
        super.onActivityResult(requestCode, resultCode, intent);

        switch (requestCode) {
            case StorageAccessAPI.CodetoDelte: {
                StorageAccessAPI.onActivityResult(requestCode, resultCode, intent, F_Songs.this.getActivity());
                File file = new File(adapter.getItem(curItemSelect).getPath2());
                boolean canwrite = false;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP
                        && !(file.getAbsolutePath().toString().contains("emulated") || file.getAbsolutePath().toString().contains("storage0"))
                        ) {

                    try {
                        canwrite = StorageAccessAPI.getDocumentFile(file, false).canWrite();
                    } catch (Exception e) {
                        canwrite = false;
                    }

                    if (canwrite) {
                        try {
                            StorageAccessAPI.getDocumentFile(file, false).delete();
                            SongHelper.removefromMediastore(adapter.GetData(), curItemSelect, new SongHelper.OnDeleteSongListener() {

                                        @Override
                                        public void OnSongDeleted() {
                                            OnRefreshSongList();
                                        }
                                    }
                                    , mActivity);
                        } catch (Exception e) {
                        }

                    } else
                        ActivityUtil.showCrouton(F_Songs.this.getActivity(), getString(R.string.song_not_deleted));
                }
                break;
            }
            case StorageAccessAPI.Code: {
                if (resultCode == Activity.RESULT_OK) {

                    StorageAccessAPI.onActivityResult(requestCode, resultCode, intent, F_Songs.this.getActivity());


                    File file = new File(adapter.getItem(curItemSelect).getPath2());
                    boolean canwrite = false;
                    try {
                        canwrite = StorageAccessAPI.getDocumentFile(file, false).canWrite();
                    } catch (Exception e) {
                        canwrite = false;
                    }
                    if (canwrite) {
                        songHelper.EditTags(adapter.getItem(curItemSelect), spinner, play2, new SongHelper.OnEditTagsListener() {

                            @Override
                            public void OnEditTagsSuccessful() {
                                OnRefreshSongList();
                            }
                        }, fragment);

                    } else
                        ActivityUtil.showCrouton(F_Songs.this.getActivity(), getString(R.string.tag_not_edited));

                } else
                    ActivityUtil.showCrouton(F_Songs.this.getActivity(), getString(R.string.cancel));
            }
            break;
        }


    }

    private void SendSong() {
        Uri uri = Uri.parse("file://" + songdetails.get(curItemSelect).getPath2());
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("audio/*");
        share.putExtra(Intent.EXTRA_STREAM, uri);
        getActivity().startActivityForResult(Intent.createChooser(share, getString(R.string.complete_action_using)),
                IConstant.REQUEST_CODE_SEND_AUDIO);
    }

    @Override
    public void Update() {
        // Logger.d( "Activity_Fragments", "............................Update" );
        fetchSongList = new FetchSongList();
        fetchSongList.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void) null);
    }

    @Override
    protected String GetGAScreenName() {
        return "F_Songs";
    }

    class AddToPlayListMultiple extends AsyncTask<Void, String, String> {
        @Override
        protected void onPostExecute(String x) {
            spinner.setVisibility(View.GONE);
            multiplecheckedListforaddtoplaylist.clear();
            highlight_zero = 0;
            ActivityUtil.showCrouton(mActivity, getString(R.string.songs_were_added_to_playlist));
            SparseBooleanArray checked = mListView.getCheckedItemPositions();
            if (checked != null)
                for (int i = 0; i < checked.size(); i++) {
                    if (checked.valueAt(i)) {
                        mListView.setItemChecked(checked.keyAt(i), false);
                    }
                }
            mListView.setChoiceMode(ListView.CHOICE_MODE_NONE);
            mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
            adapter.OnUpdate(songdetails);
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
                Now_Playing.addToPlaylist(mActivity.getApplicationContext(), multiplecheckedListforaddtoplaylist.get(i).getPath2(), playlistid);

            return null;
        }
    }

    class FetchSongList extends AsyncTask<Void, Void, ArrayList<SongDetails>> {
        int duration;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            duration = ((Activity_Fragments) getActivity()).appSettings.getDurationFilterTime();
        }

        @Override
        protected ArrayList<SongDetails> doInBackground(Void... arg0) {
            ArrayList<SongDetails> songs = new ArrayList<SongDetails>();

            String sortOrder = ((Activity_Fragments) getActivity()).appSettings.getSongSortKey();
            if (Utilities.IsEmpty(sortOrder)) {
                sortOrder = "REPLACE ('<BEGIN>' || " + MediaStore.Audio.Media.TITLE + ", '<BEGIN>The ', '<BEGIN>')" + " COLLATE NOCASE ASC";
            }

            Cursor songCursor = null;
            String[] TRACK_COLUMNS = new String[]{MediaStore.Audio.AudioColumns.ALBUM, MediaStore.Audio.AudioColumns.ARTIST,
                    MediaStore.MediaColumns.DATA, MediaStore.MediaColumns.DISPLAY_NAME, MediaStore.Audio.AudioColumns.DURATION,
                    MediaStore.Audio.AudioColumns.TRACK, MediaStore.Audio.AudioColumns.YEAR, MediaStore.MediaColumns.SIZE,
                    MediaStore.MediaColumns.TITLE, MediaStore.Audio.Media._ID, MediaStore.Audio.Media.DATE_ADDED,
                    MediaStore.Audio.AudioColumns.ALBUM_ID, MediaStore.MediaColumns._ID};
            try {
                songCursor = mActivity.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, TRACK_COLUMNS,
                        MediaStore.Audio.Media.DURATION + ">=" + duration, null, sortOrder);
                if (songCursor != null && songCursor.moveToFirst()) {
                    do {
                        int ALBUM_ID = -1;
                        try {
                            ALBUM_ID = Integer.parseInt(songCursor.getString(songCursor.getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM_ID)));
                        } catch (NumberFormatException e) {
                        }
                        songs.add(new SongDetails(songCursor.getInt(songCursor.getColumnIndex(MediaStore.MediaColumns._ID)), null, songCursor
                                .getString(0), songCursor.getString(1), songCursor.getString(2),
                                Utilities.getTime(songCursor.getString(4)), songCursor.getString(8), songCursor.getString(1), ALBUM_ID));
                    }
                    while (songCursor.moveToNext());
                }
            } finally {
                if (songCursor != null) {
                    songCursor.close();
                    songCursor = null;
                }
                TRACK_COLUMNS = null;
            }

            return songs;
        }

        @Override
        protected void onPostExecute(ArrayList<SongDetails> result) {
            super.onPostExecute(result);
            if (songdetails != null) {
                songdetails.clear();
            } else {
                songdetails = new ArrayList<SongDetails>();
            }
            songdetails.addAll(result);
            result.clear();
            OnRefreshSongList();
            inandout();

            Animate_raymenu();

            // broadcast to F_Folder know
            Intent intent = new Intent(IConstant.BROADCAST_ON_SONGDETAILS_UPDATED);
            intent.putParcelableArrayListExtra("songdetails", songdetails);
            mActivity.sendBroadcast(intent);
        }
    }

    class AddToPl extends AsyncTask<Integer, String, String> {
        @Override
        protected void onPostExecute(String x) {
            spinner.setVisibility(View.GONE);
            ActivityUtil.showCrouton(mActivity, getString(R.string.playlist_created));
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
                    Now_Playing.addToPlaylist(mActivity.getApplicationContext(), multiplecheckedListforaddtoplaylist.get(index).getPath2(),
                            params[0].intValue());
                } catch (IllegalStateException e) {
                    ActivityUtil.showCrouton(mActivity, getString(R.string.playlist_name_already_exists));
                    return null;
                } catch (Exception e) {
                    ActivityUtil.showCrouton(mActivity, getString(R.string.an_error_occurred));
                    return null;
                }
            }

            return null;
        }
    }
}
