package champak.champabun.view.activity;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.commit451.nativestackblur.NativeStackBlur;

import org.cmc.music.metadata.MusicMetadata;
import org.cmc.music.metadata.MusicMetadataSet;
import org.cmc.music.myid3.MyID3;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import champak.champabun.AmuzicgApp;
import champak.champabun.R;
import champak.champabun.business.dataclasses.OnSwipeTouchListener2;
import champak.champabun.business.dataclasses.SongDetails;
import champak.champabun.business.definition.IConstant;
import champak.champabun.business.definition.Logger;
import champak.champabun.business.utilities.utilClass.TypefaceTextView;
import champak.champabun.business.utilities.utilMethod.ActivityUtil;
import champak.champabun.business.utilities.utilMethod.BitmapUtil;
import champak.champabun.business.utilities.utilMethod.PlayMeePreferences;
import champak.champabun.business.utilities.utilMethod.StorageAccessAPI;
import champak.champabun.business.utilities.utilMethod.Utilities;
import champak.champabun.driver.iloader.ImageLoader;
import champak.champabun.framework.equalizer.EqualizerActivity;
import champak.champabun.framework.service.Music_service;

public class Player extends BaseActivity implements OnSeekBarChangeListener {
    private static final int SELECT_PHOTO = 99;
    Animation fadeOut, fadeIn, fadeInImage, fadeOutImage, fadeInImageBg;
    View shuffle, repeat, ringtone, NowPlaying, Equalizer, bShufflebg;
    Button brepeat;
    Bitmap album;
    Button bShuffle;
    MusicMetadata meta;
    int imagecheck;
    String oldsong = "", oldArtist = "", oldalbum = "", oldTime = "";
    ImageView PrevBgImage, NextBgImage;
    private ImageView buttonPlayStop;
    private ImageView i1, previous, next;
    private String progress2;
    private int headsetSwitch, head;
    BroadcastReceiver headsetReceiver = new BroadcastReceiver() {
        private boolean headsetConnected = false;

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.hasExtra("state")) {
                if (headsetConnected && intent.getIntExtra("state", 0) == 0) {
                    headsetConnected = false;
                    headsetSwitch = 0;
                    head = head + 1;
                } else if (!headsetConnected && intent.getIntExtra("state", 0) == 1) {
                    headsetConnected = true;
                    headsetSwitch = 1;
                    head = head + 1;
                }
            }
            if (headsetSwitch == 0 && head > 1) {
                buttonPlayStop.setBackgroundResource(R.drawable.play);
                AmuzicgApp.GetInstance().boolMusicPlaying1 =

                        false;
            } else if (headsetSwitch == 1) {
                buttonPlayStop.setBackgroundResource(R.drawable.pause2);
                AmuzicgApp.GetInstance().boolMusicPlaying1 = true;
            }
        }
    };

    private int seekMax;
    private SeekBar seekbar;
    private TypefaceTextView songname, songalbum, songartist;
    private TextView songCurrentDurationLabel;
    private TextView songTotalDurationLabel;
    private Dialog dialog;
    private LinearLayout bfake, edit;
    private boolean ActionIsRegistered, HeadsetIsRegistered, otherReceiverRegistered;
    private boolean isStartCoverOperation = false;
    private String comefrom = null;
    private boolean isFromRecentAdded = false;
    private String click_no = null;
    private ImageLoader imgLoader;
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent serviceIntent) {
            updateUI(serviceIntent);
        }
    };
    private BroadcastReceiver broadcastCoverReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent serviceIntent) {
            isStartCoverOperation = false;
            // cannot play the audio
            if (serviceIntent.getBooleanExtra("setDataSourceFailed", false)) {
                ActivityUtil.showCrouton(Player.this, getString(R.string.file_cannot_play));
            } else {
                imagecheck = 1;
                startCoverOperation();
            }
        }
    };
    private BroadcastReceiver checkagain = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent serviceIntent) {
            checkbuttonplaypause();
        }
    };

    @Override
    public int GetLayoutResID() {
        return R.layout.player;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Show ad this way.. akki
//        @Override
//        public void onShow(String arg0) {
//            if (AmuzicgApp.secondtimeplayershown == 0)
//                AmuzicgApp.secondtimeplayershown = 1;
//            else if (AmuzicgApp.secondtimeplayershown == 1)
//                AmuzicgApp.secondtimeplayershown = 2;
//            else if (AmuzicgApp.secondtimeplayershown == 2)
//                AmuzicgApp.secondtimeplayershown = 3;
//            else if (AmuzicgApp.secondtimeplayershown == 3)
//                AmuzicgApp.secondtimeplayershown = 0;
//        }

        imgLoader = new ImageLoader(Player.this);

        initViews();
        setListeners();

        head = 0;
        if (!HeadsetIsRegistered) {
            registerReceiver(headsetReceiver, new IntentFilter(Intent.ACTION_HEADSET_PLUG));
            HeadsetIsRegistered = true;
        }
        if (!ActionIsRegistered) {
            registerReceiver(broadcastReceiver, new IntentFilter(IConstant.BROADCAST_ACTION));
            ActionIsRegistered = true;
        }
        if (!otherReceiverRegistered) {
            registerReceiver(broadcastCoverReceiver, new IntentFilter(IConstant.BROADCAST_COVER));
            registerReceiver(checkagain, new IntentFilter(IConstant.BROADCAST_CHECK_AGAIN));
            otherReceiverRegistered = true;
        }
        String CMDNAME = null;
        Bundle bundle = getIntent().getExtras();
        AmuzicgApp.alreadyshuffled = false;
        if (bundle != null) {
            AmuzicgApp.alreadyshuffled = true;
            ClassLoader oldClassloader = bundle.getClassLoader();
            bundle.setClassLoader(SongDetails.class.getClassLoader());

            if (AmuzicgApp.GetInstance().getCheck() == 0) {
                try {
                    AmuzicgApp.GetInstance().setPosition(bundle.getInt("Data2", 0));
                    if (bundle.getParcelableArrayList("Data1") != null) {
                        ArrayList<SongDetails> list = bundle.getParcelableArrayList("Data1");
                        AmuzicgApp.GetInstance().SetNowPlayingList(list);
                        bundle.setClassLoader(oldClassloader);
                    }
                    CMDNAME = bundle.getString(IConstant.CMDNAME);
                } catch (Exception e) {
                    // e.printStackTrace();
                }
            }
            comefrom = bundle.getString("comefrom");
            isFromRecentAdded = bundle.getBoolean("fromRecentAdded", false);
            click_no = bundle.getString("click_no_playlist");
            try {
                if (comefrom.equals("Playlist"))
                    AmuzicgApp.alreadyshuffled = false;
            } catch (Exception ignored) {
            }
        }
        if (IConstant.CMDNEXT.equals(CMDNAME)) {
            next();
        } else if (IConstant.CMDPREVIOUS.equals(CMDNAME)) {
            prev();
        } else {
            if (AmuzicgApp.GetInstance().getCheck() == 0) {
                playAudio();
            }
        }
        AmuzicgApp.GetInstance().setCheck(1);
        checkbuttonplaypause();
        SharedPreferences preferences = this.getSharedPreferences("shuffle_setting", MODE_PRIVATE);
        AmuzicgApp.GetInstance().boolshuffled = preferences.getBoolean("shuffle_setting", false);
        if (AmuzicgApp.GetInstance().boolshuffled && !AmuzicgApp.alreadyshuffled) {
            setShuffleState();
            AmuzicgApp.alreadyshuffled = true;
        }
    }

    private void initViews() {
        NowPlaying = findViewById(R.id.bNp);
        previous = (ImageView) findViewById(R.id.bPrevious);

        next = (ImageView) findViewById(R.id.bNext);

        repeat = findViewById(R.id.bRepeatbg);
        brepeat = (Button) findViewById(R.id.bRepeat);
        ringtone = findViewById(R.id.bRing);
        shuffle = findViewById(R.id.bShuffle);
        songCurrentDurationLabel = (TextView) findViewById(R.id.songCurrentDurationLabel);
        songname = (TypefaceTextView) findViewById(R.id.songname);
        songartist = (TypefaceTextView) findViewById(R.id.artist);
        songalbum = (TypefaceTextView) findViewById(R.id.album);
        songTotalDurationLabel = (TextView) findViewById(R.id.songTotalDurationLabel);
        songname.setSelected(true);
        songartist.setSelected(true);
        songalbum.setSelected(true);
        buttonPlayStop = (ImageView) findViewById(R.id.bPlayPause);

        PrevBgImage = (ImageView) findViewById(R.id.extralayerForProperAnimation);
        NextBgImage = (ImageView) findViewById(R.id.nextImage);
        Equalizer = findViewById(R.id.bEq);
        bShufflebg = findViewById(R.id.bShufflebg);
        bShuffle = (Button) findViewById(R.id.bShuffleb);
        i1 = (ImageView) findViewById(R.id.i1);
        seekbar = (SeekBar) findViewById(R.id.songProgressBar);
        bfake = (LinearLayout) findViewById(R.id.bFake);
        edit = (LinearLayout) findViewById(R.id.edit);

        checkbuttonplaypause();
        checkButtonRandom();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        switch (requestCode) {
            case SELECT_PHOTO:
                if (resultCode == RESULT_OK) {
                    try {
                        Uri imageUri = intent.getData();
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                        if (bitmap != null) {
                            SongDetails curSongDetails = AmuzicgApp.GetInstance().GetCurSongDetails();
                            String artist = curSongDetails.getArtist();
                            String album = curSongDetails.getAlbum();

                            File file = imgLoader.getFileCache().getFile(artist + "_" + album);
                            if (file.exists()) {
                                file.delete();
                            }

                            ByteArrayOutputStream bytes = new ByteArrayOutputStream();

                            bitmap = Bitmap.createScaledBitmap(bitmap, AmuzicgApp.GetInstance().sizeofimage,
                                    AmuzicgApp.GetInstance().sizeofimage, false);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, bytes);

                            file.createNewFile();
                            FileOutputStream fo = new FileOutputStream(file);
                            fo.write(bytes.toByteArray());
                            fo.close();
                            i1.setImageBitmap(bitmap);
                            return;
                        }
                    } catch (IOException e) {
                        // e.printStackTrace();
                    }
                    return;
                } else return;
            case StorageAccessAPI.Code:
                if (resultCode == RESULT_OK) {

                    StorageAccessAPI.onActivityResult(requestCode, resultCode, intent, Player.this);


                    File file = new File(AmuzicgApp.GetInstance().GetCurSongDetails().getPath2());
                    boolean canWrite;
                    try {
                        canWrite = StorageAccessAPI.getDocumentFile(file, false).canWrite();
                    } catch (Exception e) {
                        canWrite = false;
                    }
                    if (canWrite) {
                        new EditTags().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void) null);
                    } else
                        ActivityUtil.showCrouton(Player.this, getString(R.string.tag_not_edited));

                } else
                    ActivityUtil.showCrouton(Player.this, getString(R.string.cancel));
        }
    }

    private void checkbuttonplaypause() {
        if (AmuzicgApp.GetInstance().boolMusicPlaying1) {
            buttonPlayStop.setBackgroundResource(R.drawable.pause2);
        } else {
            buttonPlayStop.setBackgroundResource(R.drawable.play);
        }
    }

    private void setListeners() {
        NowPlaying.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent newintent = new Intent(getApplicationContext(), champak.champabun.view.activity.NowPlaying.class);
                startActivity(newintent);
            }
        });

        ringtone.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                String Artist = AmuzicgApp.GetInstance().GetCurSongDetails().getArtist().trim();
                Artist = Artist.replaceAll(" ", "+");
                String Song = AmuzicgApp.GetInstance().GetCurSongDetails().getSong().trim();
                Song = Song.replaceAll(" ", "+");
                String URL = "http://app.toneshub.com/?cid=2922&artist=" + Artist + "&song=" + Song;
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(URL));
                startActivity(browserIntent);
            }
        });

        repeat.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (AmuzicgApp.GetInstance().GetRepeatMode() == AmuzicgApp.REPEAT_NONE) {
                    AmuzicgApp.GetInstance().SetRepeatMode(AmuzicgApp.REPEAT_ONCE);
                    ActivityUtil.showCrouton(Player.this, getString(R.string.repeat_one_selected));
                } else if (AmuzicgApp.GetInstance().GetRepeatMode() == AmuzicgApp.REPEAT_ONCE) {
                    AmuzicgApp.GetInstance().SetRepeatMode(AmuzicgApp.REPEAT_ALL);
                    ActivityUtil.showCrouton(Player.this, getString(R.string.repeat_all_selected));
                } else if (AmuzicgApp.GetInstance().GetRepeatMode() == AmuzicgApp.REPEAT_ALL) {
                    AmuzicgApp.GetInstance().SetRepeatMode(AmuzicgApp.REPEAT_NONE);
                    ActivityUtil.showCrouton(Player.this, getString(R.string.no_repeat_selected));
                }
                checkButtonRandom();
            }
        });
        Equalizer.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent SecondScreen = new Intent(Player.this, EqualizerActivity.class);
                startActivity(SecondScreen);
            }
        });
        buttonPlayStop.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AmuzicgApp.GetInstance().GetNowPlayingSize() > 0)
                    buttonPlayStopClick();
            }
        });
        shuffle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                changeShuffleState();
                setShuffleState();
                showshuffleCrouton();
            }
        });
        next.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AmuzicgApp.GetInstance().GetNowPlayingSize() > 0)
                    next();
            }
        });

        previous.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AmuzicgApp.GetInstance().GetNowPlayingSize() > 0)
                    prev();
            }
        });

        bfake.setOnTouchListener(new OnSwipeTouchListener2() {
            @Override
            public void onSwipeRight() {
                if (AmuzicgApp.GetInstance().GetNowPlayingSize() > 0)
                    prev();
            }

            @Override
            public void onSwipeLeft() {
                if (AmuzicgApp.GetInstance().GetNowPlayingSize() > 0)
                    next();
            }
        });

        edit.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog = Utilities.designdialog(330, Player.this);

                dialog.setContentView(R.layout.dialoge_edit_tags);
                dialog.setTitle(AmuzicgApp.GetInstance().GetCurSongDetails().getSong());
                EditText album = (EditText) dialog.findViewById(R.id.al);
                EditText artist = (EditText) dialog.findViewById(R.id.ar);
                EditText title = (EditText) dialog.findViewById(R.id.ti);
                artist.setText(AmuzicgApp.GetInstance().GetCurSongDetails().getArtist());
                album.setText(AmuzicgApp.GetInstance().GetCurSongDetails().getAlbum());
                title.setText(AmuzicgApp.GetInstance().GetCurSongDetails().getSong());

                Button bDOK = (Button) dialog.findViewById(R.id.dBOK);
                Button bDCancle = (Button) dialog.findViewById(R.id.dBCancel);

                dialog.show();
                bDOK.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        EditText album = (EditText) dialog.findViewById(R.id.al);
                        EditText artist = (EditText) dialog.findViewById(R.id.ar);
                        EditText title = (EditText) dialog.findViewById(R.id.ti);

                        String artist2 = artist.getText().toString();
                        String album2 = album.getText().toString();
                        String title2 = title.getText().toString();

                        meta = new MusicMetadata("name");
                        meta.setAlbum(album2);
                        meta.setArtist(artist2);
                        meta.setSongTitle(title2);

                        AmuzicgApp.GetInstance().GetCurSongDetails().setSong(title2);
                        AmuzicgApp.GetInstance().GetCurSongDetails().setArtist(artist2);
                        AmuzicgApp.GetInstance().GetCurSongDetails().setAlbum(album2);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            File file = new File(AmuzicgApp.GetInstance().GetCurSongDetails().getPath2());
                            boolean canWrite;
                            if (file.getAbsolutePath().contains("emulated") || file.getAbsolutePath().contains("storage0")) {
                                new EditTags().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void) null);
                            } else {
                                try {
                                    canWrite = StorageAccessAPI.getDocumentFile(file, false).canWrite();
                                } catch (Exception e) {
                                    canWrite = false;
                                }
                                if (canWrite) {
                                    new EditTags().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void) null);
                                } else {
                                    PlayMeePreferences prefs = new PlayMeePreferences(Player.this);
                                    prefs.DeleteSharedPreferenceUri();
                                    final Dialog dg = Utilities.designdialog(200, Player.this);
                                    dg.setContentView(R.layout.dialoge_delete);
                                    dg.show();
                                    TextView headingdg = (TextView) dg.findViewById(R.id.heading);
                                    headingdg.setText(getString(R.string.request_permission));
                                    TextView dgtext = (TextView) dg.findViewById(R.id.title);
                                    dgtext.setText(getString(R.string.select_sdcard));
                                    Button ok = (Button) dg.findViewById(R.id.dBOK);
                                    Button cancel = (Button) dg.findViewById(R.id.dBCancel);

                                    cancel.setOnClickListener(new OnClickListener() {

                                        @Override
                                        public void onClick(View v) {
                                            dg.dismiss();
                                            ActivityUtil.showCrouton(Player.this, getString(R.string.denied_permission));
                                        }
                                    });

                                    ok.setOnClickListener(new OnClickListener() {

                                        @Override
                                        public void onClick(View v) {
                                            StorageAccessAPI.openDocumentTree(Player.this, 1, null);// 1 for editags 0 for delete ,needed
                                            //inside onactivityresult to identify launch code
                                            dg.dismiss();
                                        }
                                    });
                                }

                            }

                        } else {
                            new EditTags().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void) null);
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
            }
        });

        seekbar.setOnSeekBarChangeListener(this);
    }

    private void changeShuffleState() {
        AmuzicgApp.GetInstance().boolshuffled = !AmuzicgApp.GetInstance().boolshuffled;
    }

    private void showshuffleCrouton() {
        if (AmuzicgApp.GetInstance().GetNowPlayingSize() > 1) {
            if (AmuzicgApp.GetInstance().boolshuffled)
                ActivityUtil.showCrouton(Player.this, getString(R.string.shuffle_on));
            else
                ActivityUtil.showCrouton(Player.this, getString(R.string.shuffle_off));
        } else if (AmuzicgApp.GetInstance().GetNowPlayingSize() == 1)

            ActivityUtil.showCrouton(Player.this, getString(R.string.only_one_song));

    }

    private void setShuffleState() {
        if (AmuzicgApp.GetInstance().GetNowPlayingSize() > 1) {
            SharedPreferences.Editor editor = this.getSharedPreferences("shuffle_setting", MODE_PRIVATE).edit();
            if (AmuzicgApp.GetInstance().boolshuffled) {
                editor.putBoolean("shuffle_setting", true);
                editor.apply();
                if (AmuzicgApp.GetInstance().GetNowPlayingList().size() > 800) // to avoid crashes due to memory
                {
                    AmuzicgApp.GetInstance().backup = AmuzicgApp.GetInstance().GetNowPlayingList();
                } else
                    AmuzicgApp.GetInstance().backup = new ArrayList<>(AmuzicgApp.GetInstance().GetNowPlayingList());
                SongDetails temp;
                temp = AmuzicgApp.GetInstance().GetCurSongDetails();
                AmuzicgApp.GetInstance().RemoveCurSongDetails();
                Collections.shuffle(AmuzicgApp.GetInstance().GetNowPlayingList());
                AmuzicgApp.GetInstance().Add2CurSongDetails(temp);
                bShufflebg.setBackgroundColor(Color.parseColor("#30000000"));
            } else {
                editor.putBoolean("shuffle_setting", false);
                editor.apply();
                int x = 0;
                try {
                    x = AmuzicgApp.GetInstance().backup.indexOf(AmuzicgApp.GetInstance().GetCurSongDetails());
                } catch (Exception ignored) {
                }
                bShufflebg.setBackgroundColor(Color.parseColor("#00ffffff"));

                AmuzicgApp.GetInstance().NP_List = AmuzicgApp.GetInstance().backup;
                AmuzicgApp.GetInstance().setPosition(x);

            }
        }
    }

    public void prev() {
        if (Music_service.mp.getCurrentPosition() > 5000) {
            Intent intentswap = new Intent(IConstant.BROADCAST_SWAP);
            intentswap.putExtra("swap", 0);
            sendBroadcast(intentswap);
            return;
        }
        if (AmuzicgApp.GetInstance().getPosition() > 0) {
            AmuzicgApp.GetInstance().decreasePosition();
            Intent intentswap = new Intent(IConstant.BROADCAST_SWAP);
            intentswap.putExtra("swap", -1);
            sendBroadcast(intentswap);
        } else if (AmuzicgApp.GetInstance().GetNowPlayingSize() > 0) {
            if (AmuzicgApp.GetInstance().GetRepeatMode() == AmuzicgApp.REPEAT_ALL) {
                AmuzicgApp.GetInstance().setPosition(AmuzicgApp.GetInstance().GetNowPlayingSize() - 1);
                Intent intentswap = new Intent(IConstant.BROADCAST_SWAP);
                intentswap.putExtra("swap", 0);
                sendBroadcast(intentswap);
            } else if (AmuzicgApp.GetInstance().GetRepeatMode() != AmuzicgApp.REPEAT_NONE) {
                if (AmuzicgApp.GetInstance().GetRepeatMode() == AmuzicgApp.REPEAT_ONCE) {
                }
            }
        }

        buttonPlayStop.setBackgroundResource(R.drawable.pause2);
        AmuzicgApp.GetInstance().boolMusicPlaying1 = true;
    }

    public void next() {
        if (AmuzicgApp.GetInstance().getPosition() < AmuzicgApp.GetInstance().GetNowPlayingSize() - 1) {
            AmuzicgApp.GetInstance().increasePosition();
            Intent intentswap = new Intent(IConstant.BROADCAST_SWAP);
            intentswap.putExtra("swap", 1);
            sendBroadcast(intentswap);
            buttonPlayStop.setBackgroundResource(R.drawable.pause2);
            AmuzicgApp.GetInstance().boolMusicPlaying1 = true;
        } else if (AmuzicgApp.GetInstance().getPosition() == AmuzicgApp.GetInstance().GetNowPlayingSize() - 1
                && AmuzicgApp.GetInstance().GetNowPlayingSize() > 0) {
            if (AmuzicgApp.GetInstance().GetRepeatMode() == AmuzicgApp.REPEAT_NONE) {
                AmuzicgApp.GetInstance().boolMusicPlaying1 = false;
            } else if (AmuzicgApp.GetInstance().GetRepeatMode() == AmuzicgApp.REPEAT_ONCE) {
                AmuzicgApp.GetInstance().boolMusicPlaying1 = false;
            } else if (AmuzicgApp.GetInstance().GetRepeatMode() == AmuzicgApp.REPEAT_ALL) {
                AmuzicgApp.GetInstance().setPosition(0);
                Intent intentswap = new Intent(IConstant.BROADCAST_SWAP);
                intentswap.putExtra("swap", 1);
                sendBroadcast(intentswap);
                buttonPlayStop.setBackgroundResource(R.drawable.pause2);
                AmuzicgApp.GetInstance().boolMusicPlaying1 = true;
            }
        }
    }

    private void buttonPlayStopClick() {
        if (AmuzicgApp.GetInstance().boolMusicPlaying1) {
            buttonPlayStop.setBackgroundResource(R.drawable.play);
            Intent intentplaypause = new Intent(IConstant.BROADCAST_PLAYPAUSE);
            intentplaypause.putExtra("playpause", 0);
            sendBroadcast(intentplaypause);
        } else {
            buttonPlayStop.setBackgroundResource(R.drawable.pause2);
            Intent intentplaypause = new Intent(IConstant.BROADCAST_PLAYPAUSE);
            intentplaypause.putExtra("playpause", 1);
            sendBroadcast(intentplaypause);
        }
    }

    private void playAudio() {
        buttonPlayStop.setBackgroundResource(R.drawable.pause2);
        AmuzicgApp.GetInstance().boolMusicPlaying1 = true;
        Intent serviceIntent = new Intent(this, Music_service.class);
        startService(serviceIntent);
    }

    @Override
    public void onProgressChanged(SeekBar sb, int progress, boolean fromUser) {
        if (fromUser) {
            int seekPos = sb.getProgress();
            buttonPlayStop.setBackgroundResource(R.drawable.pause2);
            Intent intent = new Intent(IConstant.BROADCAST_SEEKBAR);
            intent.putExtra("seekpos", seekPos);
            sendBroadcast(intent);
        }
    }

    private void updateUI(Intent serviceIntent) {
        String counter = serviceIntent.getStringExtra("counter");
        String mediamax = serviceIntent.getStringExtra("mediamax");
        serviceIntent.getStringExtra("song_ended");

        int seekProgress = Integer.parseInt(counter);
        seekMax = Integer.parseInt(mediamax);
        // The converting of milliseconds into mintues and all that shit
        // starts...
        int seekProgress2 = seekProgress / 1000;
        int seekProgressminutes = seekProgress2 / 60;
        int seekProgressseconds = seekProgress2 % 60;

        if (seekProgressseconds < 10) {
            progress2 = seekProgressminutes + ":0" + seekProgressseconds;
        } else {
            progress2 = seekProgressminutes + ":" + seekProgressseconds;
        }
        songCurrentDurationLabel.setText(progress2);
        seekbar.setMax(seekMax);
        seekbar.setProgress(seekProgress);
    }

    protected void startCoverOperation() {
        if (isStartCoverOperation) {
            return;
        }
        settextetc();
        fadeOut.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                if (AmuzicgApp.GetInstance().GetCurSongDetails() == null) // why?
                {
                    // do nothing
                    return;
                }
                songname.setText(AmuzicgApp.GetInstance().GetCurSongDetails().getSong());
                songalbum.setText(AmuzicgApp.GetInstance().GetCurSongDetails().getAlbum());
                songartist.setText(AmuzicgApp.GetInstance().GetCurSongDetails().getArtist());
                try {
                    songTotalDurationLabel.setText(AmuzicgApp.GetInstance().GetCurSongDetails().getTime());
                } catch (IndexOutOfBoundsException ignored) {
                }

                if (!oldsong.equals(AmuzicgApp.GetInstance().GetCurSongDetails().getSong())) {
                    imagecheck = 0;
                    songname.startAnimation(fadeIn);
                } else songname.setText(oldsong);
                if (!oldalbum.equals(AmuzicgApp.GetInstance().GetCurSongDetails().getAlbum())) {
                    imagecheck = 0;
                    songalbum.startAnimation(fadeIn);
                } else songalbum.setText(oldalbum);
                if (!oldArtist.equals(AmuzicgApp.GetInstance().GetCurSongDetails().getArtist())) {
                    imagecheck = 0;
                    songartist.startAnimation(fadeIn);
                } else songartist.setText(oldArtist);
                if (!oldTime.equals(AmuzicgApp.GetInstance().GetCurSongDetails().getTime())) {
                    songTotalDurationLabel.startAnimation(fadeIn);
                } else songTotalDurationLabel.setText(oldTime);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationStart(Animation animation) {
            }
        });
        fadeIn.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                Log.e("repeat test", "hello");

                if (AmuzicgApp.GetInstance().GetCurSongDetails().getAlbum().equals(oldalbum)
                        && AmuzicgApp.GetInstance().GetCurSongDetails().getArtist().equals(oldArtist)) {
                } else {
                    new StartCoverOperationTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void) null);
                }
                oldsong = songname.getText().toString();
                oldalbum = songalbum.getText().toString();
                oldArtist = songartist.getText().toString();
                oldTime = songTotalDurationLabel.getText().toString();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationStart(Animation animation) {
            }
        });

        isStartCoverOperation = true;
    }

    private void settextetc() {
        fadein(0, 200);
        fadeout(0, 500);
        if (!AmuzicgApp.GetInstance().GetCurSongDetails().getSong().equals(oldsong)) {
            songname.startAnimation(fadeOut);
        }
        if (!oldalbum.equals(AmuzicgApp.GetInstance().GetCurSongDetails().getAlbum())) {
            songalbum.startAnimation(fadeOut);
        }
        if (!oldArtist.equals(AmuzicgApp.GetInstance().GetCurSongDetails().getArtist())) {
            songartist.startAnimation(fadeOut);
        }

        if (!oldTime.equals(AmuzicgApp.GetInstance().GetCurSongDetails().getTime())) {
            songTotalDurationLabel.startAnimation(fadeOut);
        }
    }

    @Override
    protected void onPause() {
        try {
            unregisterReceiver(broadcastReceiver);
        } catch (Exception ignored) {
        }
        ActionIsRegistered = false;
        try {
            unregisterReceiver(headsetReceiver);
        } catch (Exception ignored) {
        }
        HeadsetIsRegistered = false;
        super.onPause();
    }

    public void settings(View v) {
        Intent i = new Intent(Player.this, Settings.class);
        startActivity(i);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (AmuzicgApp.secondtimeplayershown == 0)
            //try to show ad here
//            InterstitialAd.display(this);

            if (!ActionIsRegistered) {
                registerReceiver(broadcastReceiver, new IntentFilter(IConstant.BROADCAST_ACTION));
                ActionIsRegistered = true;
            }

        if (!HeadsetIsRegistered) {
            registerReceiver(headsetReceiver, new IntentFilter(Intent.ACTION_HEADSET_PLUG));
            HeadsetIsRegistered = true;
        }

        if (AmuzicgApp.GetInstance().GetNowPlayingSize() == 0) {
            songname.setText(getString(R.string.no_songs));
            songalbum.setText("");
            songartist.setText("");
            PrevBgImage.setBackgroundResource(R.drawable.grad);
        } else {
            startCoverOperation();
        }
        checkbuttonplaypause();
        checkButtonRandom();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("comefrom", comefrom);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        int seekProgress = seekBar.getProgress();
        int seekProgress2 = seekProgress / 1000;
        int seekProgressminutes = seekProgress2 / 60;
        int seekProgressseconds = seekProgress2 % 60;
        String progress2;
        if (seekProgressseconds < 10) {
            progress2 = seekProgressminutes + ":0" + seekProgressseconds;
        } else {
            progress2 = seekProgressminutes + ":" + seekProgressseconds;
        }

        songCurrentDurationLabel.setText(progress2);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onDestroy() {
        if (AmuzicgApp.secondtimeplayershown == 0)
            AmuzicgApp.secondtimeplayershown = 1;

        else if (AmuzicgApp.secondtimeplayershown == 1)
            AmuzicgApp.secondtimeplayershown = 2;
        else if (AmuzicgApp.secondtimeplayershown == 2)
            AmuzicgApp.secondtimeplayershown = 0;

        try {
            unregisterReceiver(broadcastCoverReceiver);
        } catch (IllegalArgumentException | IllegalStateException ignored) {
        }
        try {
            unregisterReceiver(checkagain);
        } catch (IllegalArgumentException | IllegalStateException ignored) {
        }
        otherReceiverRegistered = false;
        imgLoader = null;
        super.onDestroy();
    }

    protected void fadeout(int offset, int duration) {
        fadeOut = null;
        fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new AccelerateInterpolator());
        fadeOut.setStartOffset(offset);
        fadeOut.setDuration(duration);
        fadeOut.setFillAfter(true);
    }

    protected void fadein(int offset, int duration) {
        fadeIn = null;
        fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new AccelerateInterpolator()); // and this
        fadeIn.setFillAfter(true);
        fadeIn.setStartOffset(offset);
        fadeIn.setDuration(duration);
    }

    protected void fadeinimageBg(int offset, int duration) {
        fadeInImageBg = null;
        fadeInImageBg = new AlphaAnimation(0, 1);
        fadeInImageBg.setInterpolator(new AccelerateInterpolator()); // and this
        fadeInImageBg.setFillAfter(true);
        fadeInImageBg.setStartOffset(offset);
        fadeInImageBg.setDuration(duration);
    }

    protected void fadeinimage(int offset, int duration) {
        fadeInImage = null;
        fadeInImage = new AlphaAnimation(0, 1);
        fadeInImage.setInterpolator(new AccelerateInterpolator()); // and this
        fadeInImage.setFillAfter(true);
        fadeInImage.setStartOffset(offset);
        fadeInImage.setDuration(duration);
    }

    protected void fadeoutimage(int offset, int duration) {
        fadeOutImage = null;
        fadeOutImage = new AlphaAnimation(1, 0);
        fadeOutImage.setInterpolator(new AccelerateInterpolator()); // and this
        fadeOutImage.setStartOffset(offset);
        fadeOutImage.setDuration(duration);
        fadeOutImage.setFillAfter(true);
    }

    public void checkButtonRandom() {
        if (AmuzicgApp.GetInstance().GetRepeatMode() == AmuzicgApp.REPEAT_NONE) {
            brepeat.setBackgroundResource(R.drawable.repeat_off_tran);
        } else if (AmuzicgApp.GetInstance().GetRepeatMode() == AmuzicgApp.REPEAT_ONCE) {
            brepeat.setBackgroundResource(R.drawable.repeat_one_tran);
        } else if (AmuzicgApp.GetInstance().GetRepeatMode() == AmuzicgApp.REPEAT_ALL) {
            brepeat.setBackgroundResource(R.drawable.repeat_all_tran);
        }
    }

    @Override
    public String GetActivityID() {
        return "Player";
    }

    @SuppressWarnings("deprecation")
    @Override
    public void SetNullForCustomVariable() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            brepeat.setBackgroundDrawable(null);
        } else {
            brepeat.setBackground(null);
        }
    }

    @Override
    public int GetRootViewID() {
        return R.id.LL;
    }

    @Override
    public void OnBackPressed() {
        AmuzicgApp.GetInstance().SaveLastPlaylist(isFromRecentAdded, click_no);
        // check whether open Player from Music_service or not
        try {
            if ("Music_service".equals(comefrom)) {
                // check whether Activity_Fragments was removed from recent list or not
                if (!ActivityUtil.IsActivityCreated("Activity_Fragments")) {
                    Intent intent = new Intent(this, Activity_Fragments.class);
                    setResult(RESULT_OK, intent);
                    startActivity(intent);
                }
            }
        } catch (Exception ignored) {
        }
        setResult(RESULT_OK);

        finish();
    }

    @Override
    protected String GetGAScreenName() {
        return "Player";
    }

    class EditTags extends AsyncTask<Void, String, String> {
        @Override
        protected void onPostExecute(String x) {
            if (x.equals("notenoughpermission")) {
                ActivityUtil.showCrouton(Player.this, getString(R.string.denied_permission));
                return;
            }
            if (!AmuzicgApp.GetInstance().GetCurSongDetails().getSong().equals(oldsong)) {
                songname.setText(AmuzicgApp.GetInstance().GetCurSongDetails().getSong());
                oldsong = songname.getText().toString();

            }
            if (!AmuzicgApp.GetInstance().GetCurSongDetails().getAlbum().equals(oldalbum)) {
                songalbum.setText(AmuzicgApp.GetInstance().GetCurSongDetails().getAlbum());
                oldalbum = songalbum.getText().toString();
            }
            if (!AmuzicgApp.GetInstance().GetCurSongDetails().getArtist().equals(oldArtist)) {
                songartist.setText(AmuzicgApp.GetInstance().GetCurSongDetails().getArtist());
                oldArtist = songartist.getText().toString();
            }
            ActivityUtil.showCrouton(Player.this, getString(R.string.tags_edited));

            meta = null;
            new FetchFromInternet().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                File src = new File(AmuzicgApp.GetInstance().GetCurSongDetails().getPath2());
                MusicMetadataSet src_set = new MyID3().read(src);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && !(src.getAbsolutePath().contains("emulated") || src
                        .getAbsolutePath().contains("storage0"))) {
                    new MyID3().write(src, src, src_set, meta);
                } else
                    new MyID3().update(src, src_set, meta);
            }     // write updated metadata
            catch (OutOfMemoryError e1) {
                e1.printStackTrace();
            } catch (Exception ignored) {
            }
            try {
                MediaScannerConnection.scanFile(Player.this, new String[]{AmuzicgApp.GetInstance().GetCurSongDetails().getPath2()}, null,
                        new MediaScannerConnection.OnScanCompletedListener() {
                            public void onScanCompleted(String path, Uri uri) {

                            }
                        });
            } catch (Exception e) {
                e.printStackTrace();
            }

            return "x";
        }
    }

    class StartCoverOperationTask extends AsyncTask<Void, Void, Bitmap> {
        int wt_px, ht_px;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if (AmuzicgApp.GetInstance().boolshuffled) {
                bShufflebg.setBackgroundColor(Color.parseColor("#30000000"));
            } else if (!AmuzicgApp.GetInstance().boolshuffled) {
                bShufflebg.setBackgroundColor(Color.parseColor("#00ffffff"));
            }

            wt_px = (int) Player.this.getResources().getDimension(R.dimen.player_image_size);
            ht_px = (int) Player.this.getResources().getDimension(R.dimen.player_image_size);
        }

        @Override
        protected Bitmap doInBackground(Void... arg0) {
            if (AmuzicgApp.GetInstance().GetCurSongDetails() == null) {
                return null;
            }
            int albumID = AmuzicgApp.GetInstance().GetCurSongDetails().getAlbumID();
            Logger.d("Player", "doInBackground............. albumID = " + albumID);
            Cursor cursor = null;
            try {
                cursor = Player.this.getContentResolver().query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                        new String[]{MediaStore.Audio.Albums._ID, MediaStore.Audio.Albums.ALBUM_ART}, MediaStore.Audio.Albums._ID + "=?",
                        new String[]{String.valueOf(albumID)}, null);

                if (cursor != null && cursor.moveToFirst()) {
                    AmuzicgApp.GetInstance().path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART));
                    // do whatever you need to do
                }
            } catch (Exception ignored) {
            } finally {
                if (cursor != null) {
                    if (!cursor.isClosed()) {
                        cursor.close();
                    }
                }
            }
            album = imgLoader.fetchfilefromcahce(AmuzicgApp.GetInstance().GetCurSongDetails().getAlbum(),
                    AmuzicgApp.GetInstance().GetCurSongDetails().getArtist());
            if (album == null) {
                album = BitmapUtil.GetBitmapFromSongPath(wt_px, ht_px,
                        AmuzicgApp.GetInstance().GetCurSongDetails().getPath2());

            }

            return album;
        }

        @Override
        protected void onPostExecute(final Bitmap result) {
            super.onPostExecute(result);

            if (result != null) {
                album = result;
            } else {
                album = BitmapUtil.GetRandomBitmap(Player.this.getResources(), AmuzicgApp.GetInstance().GetCurSongDetails().getAlbumID(),
                        wt_px, ht_px);
                new FetchFromInternet().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
            if (album != null) {
                new SetBGTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, album);
            }
        }
    }

    class FetchFromInternet extends AsyncTask<Void, Void, Bitmap> {
        public FetchFromInternet() {
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            return imgLoader.getBitmap2(AmuzicgApp.GetInstance().GetCurSongDetails().getAlbum(), AmuzicgApp.GetInstance().GetCurSongDetails().getArtist());
        }

        @Override
        protected void onPostExecute(Bitmap v) {
            if (v != null) {
                album = v;

                new SetBGTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, album);
            }
        }
    }

    class SetBGTask extends AsyncTask<Bitmap, Void, Drawable> {
        @Override
        protected Drawable doInBackground(Bitmap... params) {
            boolean outofmemoryerror = false;
            if (params[0] == null) {
                return null;
            }
            Drawable d = null;
            try {
                Bitmap bm = NativeStackBlur.process(params[0], 20);
                d = new BitmapDrawable(getResources(), bm);
                ColorMatrix matrix = new ColorMatrix();
                matrix.setSaturation((float) 1.24);
                ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
                d.setColorFilter(filter);

                if (IConstant.OPTIMIZE_MEM_RECYCLE_BITMAP) {
                    bm.recycle();
                    bm = null;
                }
            } catch (OutOfMemoryError e) {
                outofmemoryerror = true;
            } catch (Exception ignored) {
            }
            if (d == null && !outofmemoryerror) {
                try {
                    Bitmap bm = NativeStackBlur.process(Bitmap.createScaledBitmap(params[0], 120, 120, false), 20);
                    d = new BitmapDrawable(getResources(), bm);
                    ColorMatrix matrix = new ColorMatrix();
                    matrix.setSaturation((float) 0.84);
                    ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
                    d.setColorFilter(filter);

                    if (IConstant.OPTIMIZE_MEM_RECYCLE_BITMAP) {
                        bm.recycle();
                        bm = null;
                    }
                } catch (OutOfMemoryError | Exception ignored) {
                }

            }
            return d;
        }

        @SuppressWarnings("deprecation")
        @Override
        protected void onPostExecute(final Drawable result) {
            super.onPostExecute(result);

            if (result == null) {
                i1.setImageBitmap(album);
            }
            fadeinimage(0, 400);
            fadeoutimage(0, 400);
            fadeinimageBg(0, 3000);

            if (imagecheck == 0) {
                i1.startAnimation(fadeOutImage);

                fadeOutImage.setAnimationListener(new AnimationListener() {
                    @Override
                    public void onAnimationEnd(Animation animation) {
                        NextBgImage.setAlpha(1f);
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN)
                            NextBgImage.setBackgroundDrawable(result);

                        else
                            NextBgImage.setBackground(result);

                        NextBgImage.startAnimation(fadeInImageBg);
                        i1.setImageBitmap(album);
                        i1.startAnimation(fadeInImage);
                    }

                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });
            }

            fadeInImageBg.setAnimationListener(new AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                        try {
                            PrevBgImage.setBackgroundDrawable(result);
                        } catch (Exception ignored) {
                        }
                        NextBgImage.setAlpha(0f);
                    } else {
                        try {
                            PrevBgImage.setBackground(result);
                        } catch (Exception ignored) {
                        }
                        NextBgImage.setAlpha(0f);
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

            i1.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(intent, SELECT_PHOTO);
                }
            });
        }
    }
}
