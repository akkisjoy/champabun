package champak.champabun;

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

public class Artist extends BaseActivity implements
		SongHelper.OnQuickActionItemSelectListener {
    static public int highlight_zero = 0;
    public ListView mListView;
    public ProgressBar spinner;
    String artistname;
	ArrayList<SongDetails> img = new ArrayList<SongDetails>();
	Adapter_SongView ab;
	// ArrayList < String > img2;// = new ArrayList<String>();
	String artist_id;
	LinearLayout ll;
	ArrayList<SongDetails> play;
	TypefaceTextView tV1;// album2;
	int pos;
	int playlistid;
	Handler handler = new Handler();
	int position2;
	int albumNameCheck;
	Dialog dialog2;
	ArrayList<SongDetails> multiplecheckedListforaddtoplaylist, pl;
	Button bRBack, bRAdd, bRPlay, bRDel;
	RayMenu rayMenu;
    Animation fadeOut, fadeIn;
    // Context c;
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
        backPager.setColorFilter(getResources().getColor(R.color.yellowPager), PorterDuff.Mode.MULTIPLY);

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
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				position2 = arg2;
				if (songHelper == null) {
					songHelper = new SongHelper();
				}
				songHelper.Show(Artist.this, mListView, Artist.this,
						SongHelper.ARTIST);
				return true;
			}
		});
		mListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> a, View v, int position,
					long id) {
				Intent intent = new Intent("android.intent.action.p");
				if (play.size() > 800) {
					GlobalSongList.GetInstance().SetNowPlayingList(play);
				} else {
					GlobalSongList.GetInstance().SetNowPlayingList(
							new ArrayList<SongDetails>(play));
				}
				GlobalSongList.GetInstance().setPosition(position);
				GlobalSongList.GetInstance().setCheck(0);
				startActivity(intent);
			}
		});

		// animate_ListView( );
		Intent i = getIntent();

		artist_id = i.getStringExtra("click_no");

		int s = Integer.parseInt(artist_id);

		// if (android.os.Build.VERSION.SDK_INT >=
		// android.os.Build.VERSION_CODES.HONEYCOMB)
		// {
		new FetchListItems().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, s);
		tV1 = (TypefaceTextView) findViewById(R.id.tV1);
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
		songHelper.EditTags(play.get(position2), spinner, null,
				new SongHelper.OnEditTagsListener() {

					@Override
					public void OnEditTagsSuccessful() {
						OnRefreshListView();
					}
				},null);
	}

	@Override
	public void onResume() {
		if (!rayMenu.mRayLayout.isExpanded())
			Animate_raymenu();
		super.onResume();

	}

	@Override
	public void QuickAction_OnSetAsRingtone() {
		songHelper.SetAsRingtone(play.get( position2));
	}

	@Override
	public void QuickAction_OnViewDetails() {
	}

	@Override
	public void QuickAction_OnDeleteSong() {
		songHelper.DeleteSong(play, position2,
				new SongHelper.OnDeleteSongListener() {

					@Override
					public void OnSongDeleted() {
						OnRefreshListView();
					}
				},null);
	}

	private void OnRefreshListView() {
		if (ab == null) {
			ab = new Adapter_SongView(play,
					Artist.this.getApplicationContext(), 3);
			mListView.setAdapter(ab);
		} else {
			ab.OnUpdate(play);
		}
	}

	private void Animate_raymenu() {
		handler.removeCallbacksAndMessages(null);
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				rayMenu.mHintView.startAnimation(rayMenu
						.createHintSwitchAnimation(rayMenu.mRayLayout
								.isExpanded()));
				rayMenu.mRayLayout.switchState(true);
			}
		}, 800);
	}

	private void animate_ListView() {
		AnimationSet set = new AnimationSet(true);

		Animation animation = new AlphaAnimation(0.0f, 1.0f);
		animation.setDuration(600);
		set.addAnimation(animation);

		animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
				Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
				-1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
		animation.setDuration(600);
		set.addAnimation(animation);

		LayoutAnimationController controller = new LayoutAnimationController(
				set, 0.25f);
		mListView.setLayoutAnimation(controller);

		// controller = null;
		// animation = null;
		// set = null;
	}

	/**
	 * Sets the up listeners.
	 * 
	 * @param mCoverFlow
	 *            the new up listeners
	 */
	private void setupListeners(final EcoGallery mCoverFlow, final String artistname) {
		mCoverFlow.setOnItemSelectedListener(new EcoGalleryAdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(EcoGalleryAdapterView<?> parent,
					View view, int position, long id) {
				pos = position;
				Logger.d("Artist", "onItemSelected................." + position);
				// if (android.os.Build.VERSION.SDK_INT >=
				// android.os.Build.VERSION_CODES.HONEYCOMB)
				// {

				highlight_zero = 0;
				SparseBooleanArray checked = mListView
						.getCheckedItemPositions();
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

				new LazyLoad().executeOnExecutor(
						AsyncTask.THREAD_POOL_EXECUTOR, artistname);

			}

			@Override
			public void onNothingSelected(EcoGalleryAdapterView<?> parent) {

			}

		});
	}

    //
    private ArrayList<SongDetails> addToLisView(int position, String artistname) {
        ArrayList<SongDetails> list = new ArrayList<SongDetails>();
        this.artistname = artistname;
        String[] columns = {MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media._ID, MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ALBUM, MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.MediaColumns._ID};
        // TODO
        String where;
        String[] whereVal;
        if (position == 0) {
            where = MediaStore.Audio.Media.ARTIST + "=?";// +cursor2.getString(0);
            whereVal = new String[]{artistname};
            albumNameCheck = 2;
        } else {
            where = MediaStore.Audio.Media.ALBUM + "=?"
                    + "AND " + MediaStore.Audio.Media.ARTIST
                    + "=?";// +cursor2.getString(0);
            whereVal = new String[]{img.get(position).getAlbum(), artistname};
            albumNameCheck = 1;
        }
        where += " AND (" + MediaStore.Audio.Media.DURATION + ">=" + appSettings.getDurationFilterTime() + ")";
        // String orderBy =
        // android.provider.MediaStore.Audio.Media.TRACK;//MediaStore.Audio.Media.TITLE;
        String sortOrder = appSettings.getArtistSortKey();
        if (Utilities.IsEmpty(sortOrder)) {
            sortOrder = MediaStore.Audio.Media.TRACK;// android.provider.MediaStore.Audio.Media.TITLE;
        }

        Cursor cursor = null;
        try {

            cursor = getContentResolver().query(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, columns,
                    where, whereVal, sortOrder);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    SongDetails pl = new SongDetails();
                    pl.setID(cursor.getInt(cursor
                            .getColumnIndex(MediaStore.MediaColumns._ID)));
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

                } while (cursor.moveToNext());

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

    private void SetupButton() {
        bRAdd.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View vv) {
                if (multiplecheckedListforaddtoplaylist != null) {
                    multiplecheckedListforaddtoplaylist.clear();
                }
                SparseBooleanArray checked = mListView
                        .getCheckedItemPositions();
                multiplecheckedListforaddtoplaylist = RayMenu_Functions
                        .getCheckedList(highlight_zero, checked, play, ab);

                if (multiplecheckedListforaddtoplaylist.size() > 0) {
                    Adapter_playlist_Dialog ab;

                    dialog2 = Utilities.designdialog(330, Artist.this);

                    dialog2.setContentView(R.layout.dialog_queue);
                    dialog2.show();
                    pl = Utilities.generatePlaylists(Artist.this
                            .getApplicationContext());
                    SongDetails np = new SongDetails();
                    np.setSong(getString(R.string.now_playing));
                    pl.add(0, np);
                    np = new SongDetails();
                    np.setSong(getString(R.string.create_new));
                    pl.add(1, np);
                    np = null;
                    ab = new Adapter_playlist_Dialog(pl);
                    ListView dlgLV = (ListView) dialog2
                            .findViewById(R.id.listView1);
                    dlgLV.setAdapter(ab);
                    dlgLV.setOnItemClickListener(new OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> parent,
                                                View view, int position, long id) {
                            if (position == 0) {
                                GlobalSongList.GetInstance().Add2NowPlaying(
                                        multiplecheckedListforaddtoplaylist);
                                ActivityUtil.showCrouton(
                                        Artist.this,
                                        String.format(
                                                getString(R.string.f_song_added_to_queue),
                                                multiplecheckedListforaddtoplaylist
                                                        .size() > 1 ? multiplecheckedListforaddtoplaylist
                                                        .size() : 1));
                            } else if (position == 1) {
                                create_new_playlist();
                            } else {
                                // id of the playlist
                                String plId = pl.get(position).getArtist();
                                playlistid = Integer.parseInt(plId);
                                // if (android.os.Build.VERSION.SDK_INT >=
                                // android.os.Build.VERSION_CODES.HONEYCOMB)
                                // {
                                new AddToPlayListMultiple().executeOnExecutor(
                                        AsyncTask.THREAD_POOL_EXECUTOR,
                                        (Void) null);
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
                    ActivityUtil.showCrouton(Artist.this,
                            getString(R.string.select_an_item_to_add));
                }
            }
        });

        bRDel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                SparseBooleanArray checked = mListView
                        .getCheckedItemPositions();
                ArrayList<SongDetails> checkedList = RayMenu_Functions
                        .getCheckedList(highlight_zero, checked, play, ab);
                if (checkedList.size() > 0) {
                    delete_song_multiple(checkedList);
                } else {
                    ActivityUtil.showCrouton(Artist.this,
                            getString(R.string.select_an_item_to_play));
                }
            }
        });

        bRPlay.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                SparseBooleanArray checked = mListView
                        .getCheckedItemPositions();
                ArrayList<SongDetails> checkedList = RayMenu_Functions
                        .getCheckedList(highlight_zero, checked, play, ab);
                if (checkedList.size() > 0) {

                    Intent intent = new Intent(Artist.this, Player.class);
                    GlobalSongList.GetInstance().setPosition(0);
                    // intent.putParcelableArrayListExtra("Data1",
                    // adapter.GetData(
                    // ));
                    // intent.putExtra("Data2", position);
                    if (checkedList.size() > 800) {
                        GlobalSongList.GetInstance().SetNowPlayingList(
                                checkedList);
                    } else {
                        GlobalSongList.GetInstance().SetNowPlayingList(
                                new ArrayList<SongDetails>(checkedList));
                    }

                    GlobalSongList.GetInstance().setCheck(0);
                    startActivity(intent);
                } else {
                    ActivityUtil.showCrouton(Artist.this,
                            getString(R.string.select_an_item_to_play));
                }
                checkedList.clear();
            }
        });

        bRBack.setOnClickListener(new OnClickListener() {

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
                        SparseBooleanArray checked = mListView
                                .getCheckedItemPositions();
                        if (checked != null)
                            for (int i = 0; i < checked.size(); i++) {
                                if (checked.valueAt(i)) {
                                    mListView.setItemChecked(checked.keyAt(i),
                                            false);
                                }
                            }
                        mListView.setChoiceMode(ListView.CHOICE_MODE_NONE);
                        ab.OnUpdate(play);
                        checked = null;

                        mListView
                                .setOnItemClickListener(new OnItemClickListener() {
                                    public void onItemClick(AdapterView<?> a,
                                                            View v, int position, long id) {
                                        Intent intent = new Intent(Artist.this,
                                                Player.class);
                                        GlobalSongList.GetInstance()
                                                .setPosition(position);
                                        // intent.putParcelableArrayListExtra("Data1",
                                        // adapter.GetData(
                                        // ));
                                        // intent.putExtra("Data2", position);
                                        // if (adapter.GetData().size() > 800)
                                        // {
                                        GlobalSongList
                                                .GetInstance()
                                                .SetNowPlayingList(ab.GetData());
                                        // }
                                        // else
                                        // {
                                        // GlobalSongList.GetInstance().SetNowPlayingList(new
                                        // ArrayList < SongDetails
                                        // >(adapter.GetData()));
                                        // }
                                        GlobalSongList.GetInstance()
                                                .setCheck(0);
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

    public void create_new_playlist() {
        final Dialog dialog;
        dialog = new Dialog(Artist.this, R.style.playmee);
        final Window window = dialog.getWindow();
        float pixels = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                210, this.getResources().getDisplayMetrics());
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                (int) pixels);
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
                    int YOUR_PLAYLIST_ID = Now_Playing.createPlaylist(save
                            .getText().toString(), Artist.this);
                    if (YOUR_PLAYLIST_ID == -1)
                        return;

                    // if (android.os.Build.VERSION.SDK_INT >=
                    // android.os.Build.VERSION_CODES.HONEYCOMB)
                    // {
                    new AddToPl().executeOnExecutor(
                            AsyncTask.THREAD_POOL_EXECUTOR, YOUR_PLAYLIST_ID);
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

    private void SetupRayMenu() {
        int[] ITEM_DRAWABLES = {R.drawable.composer_button_multiselect,
                R.drawable.composer_button_sort,
                R.drawable.composer_button_shuffle};
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
                            ActivityUtil.showCrouton(Artist.this,
                                    getString(R.string.multi_select_initiated));
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
                                    mListView
                                            .setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                                    mListView
                                            .setOnItemClickListener(new OnItemClickListener() {

                                                @Override
                                                public void onItemClick(
                                                        AdapterView<?> parent,
                                                        View view, int position,
                                                        long id) {
                                                    if (position == 0) {
                                                        highlight2(position);
                                                    }
                                                    if (position > 0) {
                                                        highlight(position);
                                                    }
                                                }

                                                private void highlight2(int position) {
                                                    highlight_zero = highlight_zero + 1;
                                                    ab.OnUpdate(play);
                                                }

                                                private void highlight(int position) {
                                                    mListView.setItemChecked(
                                                            position, true);
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
                                        if (albumNameCheck == 1) {
                                            RayMenu_Functions
                                                    .sort("-2", Artist.this, ab,
                                                            play, mListView, 1,
                                                            play.get(0).getAlbum(),
                                                            artistname);
                                        } else if (albumNameCheck == 2)
                                            RayMenu_Functions.sort("-2",
                                                    Artist.this, ab, play,
                                                    mListView, 1, null, artistname);
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
                                    Intent intent = new Intent(Artist.this,
                                            Player.class);
                                    GlobalSongList.GetInstance().setPosition(0);
                                    // intent.putParcelableArrayListExtra("Data1",
                                    // adapter.GetData(
                                    // ));
                                    // intent.putExtra("Data2", position);
                                    if (play.size() > 800) {
                                        GlobalSongList.GetInstance()
                                                .SetNowPlayingList(play);
                                    } else {
                                        GlobalSongList.GetInstance()
                                                .SetNowPlayingList(
                                                        new ArrayList<SongDetails>(
                                                                play));
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
        ITEM_DRAWABLES = null;
    }

    protected void delete_song_multiple(final ArrayList<SongDetails> checkedList) {
        dialog2 = Utilities.designdialog(210, Artist.this);
        dialog2.setContentView(R.layout.dialoge_delete);

        TextView title = (TextView) dialog2.findViewById(R.id.title);
        title.setText(String.format(getString(R.string.f_confirm_delete_songs),
                checkedList.size()));
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
                    SongHelper.DeleteSongMultiple(Artist.this, checkedList, i, null, null);

                    play.remove(checkedList.get(i));
                    ab.OnUpdate(play);
                }
                highlight_zero = 0;
                ActivityUtil.showCrouton(Artist.this,
                        getString(R.string.delete_successful));
                ab.OnUpdate(play);
                for (int i = 0; i < checkedList.size(); i++) {
                    try {
                        getContentResolver().delete(
                                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                                MediaStore.MediaColumns.DATA + "=?",
                                new String[]{checkedList.get(i).getPath2()});
                    } catch (SQLiteException e) {
                    }
                    // try
                    // {
                    // MediaScannerConnection.scanFile( Artist.this, new String
                    // [ ] { checkedList.get( i ).getPath2( ) }, null,
                    // new MediaScannerConnection.OnScanCompletedListener( ) {
                    // public void onScanCompleted( String path, Uri uri )
                    // {
                    // Logger.i( "ExternalStorage", "Scanned " + path + ":" );
                    // Logger.i( "ExternalStorage", "-> uri=" + uri );
                    // Artist.this.getContentResolver( ).delete( uri, null, null
                    // );
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

    public void onActivityResult(int requestCode, int resultCode, Intent intent)

    {
        super.onActivityResult(requestCode, resultCode, intent);
        switch (requestCode) {
            case StorageAccessAPI.Code:
                // PlayMeePreferences prefs = new PlayMeePreferences(Player.this);
                if (resultCode == Activity.RESULT_OK) {

                    StorageAccessAPI.onActivityResult(requestCode, resultCode, intent, Artist.this);


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
                        ActivityUtil.showCrouton(Artist.this, getString(R.string.tag_not_edited));

                } else
                    ActivityUtil.showCrouton(Artist.this, getString(R.string.cancel));
        }
    }

    @Override
    public String GetActivityID() {
        return "Artist";
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
        return "Artist";
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

    class FetchListItems extends AsyncTask<Integer, Void, Void> {
        ArrayList<SongDetails> img__;
        int duration;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            duration = appSettings.getDurationFilterTime();
        }

        @Override
        protected Void doInBackground(Integer... arg0) {
            Uri uri = MediaStore.Audio.Artists.Albums.getContentUri("external",
                    arg0[0].intValue());
            final String[] columns = {MediaStore.Audio.Artists.Albums.ALBUM,
                    MediaStore.Audio.Artists.Albums.ALBUM_ART,
                    MediaStore.Audio.Artists.ARTIST,
                    MediaStore.Audio.Artists.Albums.NUMBER_OF_SONGS_FOR_ARTIST};
            Cursor cursor = null;
            img__ = new ArrayList<SongDetails>();
            // img2__ = new ArrayList < String >();
            try {
                cursor = getContentResolver().query(
                        uri,
                        columns,
                        null,
                        null,
                        MediaStore.Audio.Artists.Albums.ALBUM
                                + " COLLATE NOCASE ASC");
                if (cursor != null && cursor.moveToFirst()) {
                    do {
                        SongDetails alb = new SongDetails();
                        alb.setAlbum(cursor.getString(0));// name of album
                        alb.setPath2(cursor.getString(1));// path of image
                        alb.setArtist(cursor.getString(2));

                        img__.add(alb);
                    } while (cursor.moveToNext());
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

            new tV1Task().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void) null);
        }
    }

    class tV1Task extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... arg0) {
            String artistname = null;
            Uri uri2 = MediaStore.Audio.Artists.getContentUri("external");
            String[] columns2 = {MediaStore.Audio.Artists.ARTIST,
                    MediaStore.Audio.Artists._ID};
            Cursor cursor2 = null;
            try {
                cursor2 = getContentResolver().query(uri2, columns2,
                        MediaStore.Audio.Artists._ID + "=" + artist_id, null,
                        null);
                if (cursor2 != null && cursor2.moveToFirst()) {
                    artistname = cursor2.getString(0);
                }
            } finally {
                if (cursor2 != null) {
                    cursor2.close();
                    cursor2 = null;
                }
            }
            return artistname;
        }

        @Override
        protected void onPostExecute(String artistname) {
            super.onPostExecute(artistname);

            SongDetails alb = new SongDetails();
            alb.setAlbum(getString(R.string.all_songs));// name of album
            alb.setPath2(null);// path of image
            alb.setArtist(artistname);
            img.add(0, alb);
            if (Utilities.isEmpty(artistname)) {
                tV1.setText(getString(R.string.no_songs));
            } else {
                tV1.setText(artistname);
                EcoGallery ecoGallery = (EcoGallery) findViewById(R.id.gallery);
                ecoGallery.setAdapter(new Adapter_gallery(img, artistname));
                // setAlbum(cursor.getString(0));//name of album
                // setPath2(cursor.getString(1));//path of image
                // setClick_no(cursor.getString(2));// number of songs.

                setupListeners(ecoGallery, artistname);
            }
        }
    }

    class AddToPl extends AsyncTask<Integer, String, String> {
        @Override
        protected void onPostExecute(String x) {

            spinner.setVisibility(View.GONE);

            ActivityUtil.showCrouton(Artist.this,
                    getString(R.string.playlist_created));
        }

        @Override
        protected void onPreExecute() {

            spinner.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Integer... params) {
            for (int index = 0; index < multiplecheckedListforaddtoplaylist
                    .size(); index++) {
                try {
                    Now_Playing.addToPlaylist(getApplicationContext(),
                            multiplecheckedListforaddtoplaylist.get(index)
                                    .getPath2(), params[0].intValue());
                } catch (IllegalStateException e) {
                    ActivityUtil.showCrouton(Artist.this,
                            getString(R.string.playlist_name_already_exists));
                    return null;
                } catch (Exception e) {
                    ActivityUtil.showCrouton(Artist.this,
                            getString(R.string.an_error_occurred));
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
			ActivityUtil.showCrouton(Artist.this,
					getString(R.string.songs_were_added_to_playlist));
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
				Now_Playing.addToPlaylist(Artist.this.getApplicationContext(),
						multiplecheckedListforaddtoplaylist.get(i).getPath2(),
						playlistid);

			return null;
		}
	}
}
