package champak.champabun;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
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

import com.enrique.stackblur.StackBlurManager;
import com.heyzap.sdk.ads.HeyzapAds.OnStatusListener;
import com.heyzap.sdk.ads.InterstitialAd;

import org.cmc.music.common.ID3WriteException;
import org.cmc.music.metadata.MusicMetadata;
import org.cmc.music.metadata.MusicMetadataSet;
import org.cmc.music.myid3.MyID3;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;

import champak.champabun.adapters.Activity_Fragments;
import champak.champabun.albums.lazylist.ImageLoader;
import champak.champabun.classes.OnSwipeTouchListener2;
import champak.champabun.classes.SongDetails;
import champak.champabun.equalizer.EqualizerActivity;
import champak.champabun.service.Music_service;
import champak.champabun.ui.TypefaceTextView;
import champak.champabun.util.ActivityUtil;
import champak.champabun.util.BitmapUtil;
import champak.champabun.util.PlayMeePreferences;
import champak.champabun.util.StorageAccessAPI;
import champak.champabun.util.Utilities;

// import android.widget.ProgressBar;

public class Player extends BaseActivity implements OnSeekBarChangeListener// , ImageLoadedInURLImageViewListener
{
    private ImageView buttonPlayStop;
    boolean fpanel = false;// Visualizervis = false;
    private ImageView i1, previous, next;
    Animation fadeOut, fadeIn, fadeInImage, fadeOutImage, fadeInImageBg;
    View shuffle, repeat, ringtone, NowPlaying, Equalizer, bShufflebg;
    // private ProgressBar spinner;
    private String progress2;
    Button brepeat;
    View v;
    String KEY_AdNo;
    String bitmap, adnumber;
    String text, apk;
    // InterstitialAd mInterstitialAd;
    Bitmap album;
    Button bShuffle;
    MusicMetadata meta;

    private int headsetSwitch, head;
    int imagecheck;
    private int seekMax;
    String oldsong = "", oldArtist = "", oldalbum = "", oldTime = "";
    private SeekBar seekbar;
    private TypefaceTextView songname, songalbum, songartist;
    private TextView songCurrentDurationLabel;
    private TextView songTotalDurationLabel;
    View PrevBgImage, NextBgImage;
    // ImageView imageBG;
    private Dialog dialog;
    private LinearLayout bfake, edit;
    private boolean ActionIsRegistered, HeadsetIsRegistered, otherReceiverRegistered;

    private boolean isStartCoverOperation = false;
    private String comefrom = null;
    private boolean isFromRecentAdded = false;
    private String click_no = null;
    private ImageLoader imgLoader;

    @Override
    public int GetLayoutResID() {
        return R.layout.player;
    }

    @Override
    public boolean isAdsOffer() {
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!IConstant.IS_PRO_VERSION) {
            InterstitialAd.setOnStatusListener(new OnStatusListener() {

                @Override
                public void onAudioFinished() {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onAudioStarted() {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onAvailable(String arg0) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onClick(String arg0) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onFailedToFetch(String arg0) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onFailedToShow(String arg0) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onHide(String arg0) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onShow(String arg0) {
                    if (GlobalSongList.secondtimeplayershown == 0)
                        GlobalSongList.secondtimeplayershown = 1;

                    else if (GlobalSongList.secondtimeplayershown == 1)
                        GlobalSongList.secondtimeplayershown = 2;
                    else if (GlobalSongList.secondtimeplayershown == 2)
                        GlobalSongList.secondtimeplayershown = 3;
                    else if (GlobalSongList.secondtimeplayershown == 3)
                        GlobalSongList.secondtimeplayershown = 0;

                }
            });


        }

        // mInterstitialAd = new InterstitialAd( this );
        // mInterstitialAd.setAdUnitId( getResources( ).getString( R.string.amazonbannerUnitId ) );
        // if ( IConstant.IS_AMAZONVERSION )
        // {
        // requestNewInterstitial( );
        // }
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
        // PackageManager pm = getPackageManager();
        // pm.setComponentEnabledSetting(MyWidget.broadcastCoverReceiver, head,
        // head) ;
        String CMDNAME = null;
        Bundle bundle = getIntent().getExtras();
        GlobalSongList.alreadyshuffled = false;
        if (bundle != null) {
            GlobalSongList.alreadyshuffled = true;
            ClassLoader oldClassloader = bundle.getClassLoader();
            bundle.setClassLoader(SongDetails.class.getClassLoader());

            // bundle.setClassLoader (getClass().getClassLoader());
            if (GlobalSongList.GetInstance().getCheck() == 0) {
                try {
                    GlobalSongList.GetInstance().setPosition(bundle.getInt("Data2", 0));
                    if (bundle.getParcelableArrayList("Data1") != null) {
                        ArrayList<SongDetails> list = bundle.getParcelableArrayList("Data1");
                        GlobalSongList.GetInstance().SetNowPlayingList(list);
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
                    GlobalSongList.alreadyshuffled = false;
                // comefrom="";
            } catch (Exception e) {
            }
        }
        if (IConstant.CMDNEXT.equals(CMDNAME)) {
            next();
        } else if (IConstant.CMDPREVIOUS.equals(CMDNAME)) {
            prev();
        } else {
            if (GlobalSongList.GetInstance().getCheck() == 0) {
                playAudio(GlobalSongList.GetInstance().getPosition());
            }
        }
        GlobalSongList.GetInstance().setCheck(1);
        checkbuttonplaypause();
        SharedPreferences preferences = this.getSharedPreferences("shuffle_setting", MODE_PRIVATE);
        GlobalSongList.GetInstance().boolshuffled = preferences.getBoolean("shuffle_setting", false);
        if (GlobalSongList.GetInstance().boolshuffled && GlobalSongList.alreadyshuffled == false) {
            setShuffleState();
            GlobalSongList.alreadyshuffled = true;
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
        // spinner = ( ProgressBar ) findViewById( R.id.progressBar1 );
        // spinner.setVisibility( View.GONE );
        // if ( IConstant.SHOW_TONESHUB )
        // {
        // ringtone.setVisibility( View.VISIBLE );

        // }

        PrevBgImage = findViewById(R.id.extralayerForProperAnimation);
        NextBgImage = findViewById(R.id.nextImage);
        // imageBG = (ImageView) findViewById(R.id.imageBG);
        Equalizer = findViewById(R.id.bEq);
        bShufflebg = findViewById(R.id.bShufflebg);
        bShuffle = (Button) findViewById(R.id.bShuffleb);
        i1 = (ImageView) findViewById(R.id.i1);
        seekbar = (SeekBar) findViewById(R.id.songProgressBar);
        bfake = (LinearLayout) findViewById(R.id.bFake);
        edit = (LinearLayout) findViewById(R.id.edit);

        checkbuttonplaypause();
        checkButtonRandom();

        // setShuffleState();
    }

    private static final int SELECT_PHOTO = 99;

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
                            SongDetails curSongDetails = GlobalSongList.GetInstance().GetCurSongDetails();
                            String artist = curSongDetails.getArtist();
                            String album = curSongDetails.getAlbum();

                            File file = imgLoader.getFileCache().getFile(artist + "_" + album);
                            if (file.exists()) {
                                file.delete();
                            }

                            ByteArrayOutputStream bytes = new ByteArrayOutputStream();

                            bitmap = Bitmap.createScaledBitmap(bitmap, GlobalSongList.GetInstance().sizeofimage,
                                    GlobalSongList.GetInstance().sizeofimage, false);
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
                // PlayMeePreferences prefs = new PlayMeePreferences(Player.this);
                if (resultCode == RESULT_OK) {

                    StorageAccessAPI.onActivityResult(requestCode, resultCode, intent, Player.this);


                    File file = new File(GlobalSongList.GetInstance().GetCurSongDetails().getPath2());
                    boolean canwrite = false;
                    try {
                        canwrite = StorageAccessAPI.getDocumentFile(file, false).canWrite();
                    } catch (Exception e) {
                        canwrite = false;
                    }
                    if (canwrite) {
                        new EditTags().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void) null);
                    } else
                        ActivityUtil.showCrouton(Player.this, getString(R.string.tag_not_edited));

                } else
                    ActivityUtil.showCrouton(Player.this, getString(R.string.cancel));
        }
    }

    private void checkbuttonplaypause() {
        if (GlobalSongList.GetInstance().boolMusicPlaying1 == true) {
            buttonPlayStop.setBackgroundResource(R.drawable.pause2);
        } else {
            buttonPlayStop.setBackgroundResource(R.drawable.play);
        }
    }

    // private class FetchDetailsOfAds extends AsyncTask < URL, Void, String >
    // {
    //
    // @Override
    // protected String doInBackground( URL ... params )
    // {
    // try
    // {
    // URL url2 = new URL( IConstant.urlContainingImageAndApk );
    // URLConnection connection = url2.openConnection( );
    // connection.setConnectTimeout( 10000 );
    // connection.setReadTimeout( 30000 );
    //
    // DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance( );
    // DocumentBuilder db = dbf.newDocumentBuilder( );
    //
    // final Document document = db.parse( connection.getInputStream( ) );
    // document.getDocumentElement( ).normalize( );
    // XPathFactory xPathfactory = XPathFactory.newInstance( );
    // XPath xPathEvaluator = xPathfactory.newXPath( );
    // XPathExpression nameExpr = xPathEvaluator.compile( "//ankit/image" );
    // NodeList nl = ( NodeList ) nameExpr.evaluate( document, XPathConstants.NODESET );
    // Node currentItem;
    // for ( int zzz = 0; zzz < nl.getLength( ); zzz ++ )
    // {
    // currentItem = nl.item( zzz );
    // bitmap = currentItem.getTextContent( );
    // }
    // nameExpr = xPathEvaluator.compile( "//ankit/text" );
    // nl = ( NodeList ) nameExpr.evaluate( document, XPathConstants.NODESET );
    // for ( int zzz = 0; zzz < nl.getLength( ); zzz ++ )
    // {
    // currentItem = nl.item( zzz );
    // text = currentItem.getTextContent( );
    // }
    // nameExpr = xPathEvaluator.compile( "//ankit/apk" );
    // nl = ( NodeList ) nameExpr.evaluate( document, XPathConstants.NODESET );
    // for ( int zzz = 0; zzz < nl.getLength( ); zzz ++ )
    // {
    // currentItem = nl.item( zzz );
    // apk = currentItem.getTextContent( );
    // }
    // nameExpr = xPathEvaluator.compile( "//ankit/adnumber" );
    // nl = ( NodeList ) nameExpr.evaluate( document, XPathConstants.NODESET );
    // for ( int zzz = 0; zzz < nl.getLength( ); zzz ++ )
    // {
    // currentItem = nl.item( zzz );
    // adnumber = currentItem.getTextContent( );
    //
    // }
    // int x = 0;
    // try
    // {
    // x = Integer.parseInt( adnumber );
    // }
    // catch ( Exception e )
    // {
    // // break;
    // }
    // SharedPreferences pref = Player.this.getSharedPreferences( HowManyTimesAdAppeared.PREF_NAME, Context.MODE_PRIVATE );
    // Editor editor = pref.edit( );
    // int adNo = pref.getInt( KEY_AdNo, 0 );
    // if ( x > adNo )
    // {
    // editor.putInt( KEY_AdNo, x );
    // editor.commit( );
    // HowManyTimesAdAppeared.clearSharedPreferences( Player.this );
    // }
    // }
    // catch ( Exception e )
    // {
    // }
    // return null;
    // }
    //
    // @Override
    // protected void onPostExecute( String result )
    // {
    // LayoutInflater li = ( LayoutInflater ) getSystemService( Context.LAYOUT_INFLATER_SERVICE );
    // v = li.inflate( R.layout.crouton, null );
    // URL url = null;
    // try
    // {
    // url = new URL( bitmap );
    // }
    // catch ( MalformedURLException e )
    // {
    // e.printStackTrace( );
    // }
    // catch ( Exception e )
    // {
    // }
    // UrlImageView x;
    // TextView tv;
    // x = ( ( UrlImageView ) v.findViewById( R.id.thumbnail ) );
    // x.setOnImageLoadedInURLImageViewListener( Player.this );
    // tv = ( ( TextView ) v.findViewById( R.id.adText ) );
    // x.setImageURL( url );
    // tv.setText( text );
    // x.setOnClickListener( new OnClickListener( ) {
    //
    // @Override
    // public void onClick( View v )
    // {
    // if ( apk != null )
    // {
    // try
    // {
    // startActivity( new Intent( Intent.ACTION_VIEW, Uri.parse( apk ) ) );
    // }
    // catch ( android.content.ActivityNotFoundException anfe )
    // {
    // startActivity( new Intent( Intent.ACTION_VIEW, Uri.parse( apk ) ) );
    // }
    // catch ( Exception e )
    // {
    // }
    // }
    // }
    // } );
    // }
    // }
    //
    private void showCroutonForAd() {
        // new FetchDetailsOfAds( ).execute( );
    }

    private void setListeners() {
        NowPlaying.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent newintent = new Intent(getApplicationContext(), Now_Playing.class);
                startActivity(newintent);
            }
        });

        ringtone.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                String Artist = new String(GlobalSongList.GetInstance().GetCurSongDetails().getArtist().trim());
                Artist = Artist.replaceAll(" ", "+");
                String Song = new String(GlobalSongList.GetInstance().GetCurSongDetails().getSong().trim());
                Song = Song.replaceAll(" ", "+");
                String URL = "http://app.toneshub.com/?cid=2922&artist=" + Artist + "&song=" + Song;
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(URL));
                startActivity(browserIntent);
            }
        });

        repeat.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (GlobalSongList.GetInstance().GetRepeatMode() == GlobalSongList.REPEAT_NONE) {
                    GlobalSongList.GetInstance().SetRepeatMode(GlobalSongList.REPEAT_ONCE);
                    ActivityUtil.showCrouton(Player.this, getString(R.string.repeat_one_selected));
                } else if (GlobalSongList.GetInstance().GetRepeatMode() == GlobalSongList.REPEAT_ONCE) {
                    GlobalSongList.GetInstance().SetRepeatMode(GlobalSongList.REPEAT_ALL);
                    ActivityUtil.showCrouton(Player.this, getString(R.string.repeat_all_selected));
                } else if (GlobalSongList.GetInstance().GetRepeatMode() == GlobalSongList.REPEAT_ALL) {
                    GlobalSongList.GetInstance().SetRepeatMode(GlobalSongList.REPEAT_NONE);
                    ActivityUtil.showCrouton(Player.this, getString(R.string.no_repeat_selected));
                }
                checkButtonRandom();
            }
        });
        /*
         * layout.setPanelSlideListener(new PanelSlideListener() {
		 * @Override public void onPanelSlide(View panel, float slideOffset) { Equalizer.setVisibility(View.VISIBLE);
		 * seekbar.setVisibility(View.VISIBLE); buttonPlayStop.setVisibility(View.VISIBLE); // if (slideOffset < 0.2) { //if (Visualizervis == true) {
		 * //mVisualizerView.setVisibility(View.GONE); //Visualizervis = false; //} else { // } //} }
		 */
        /*
		 * @Override public void onPanelExpanded(View panel) { layout.setEnableDragViewTouchEvents(true); fpanel = true; //addLineRenderer();
		 * Equalizer.setVisibility(View.GONE); seekbar.setVisibility(View.GONE); buttonPlayStop.setVisibility(View.GONE);
		 * //mVisualizerView.setVisibility(View.VISIBLE); //Visualizervis = true; }
		 * @Override public void onPanelCollapsed(View panel) { layout.setEnableDragViewTouchEvents(false); fpanel = false;
		 * Equalizer.setVisibility(View.VISIBLE); seekbar.setVisibility(View.VISIBLE); buttonPlayStop.setVisibility(View.VISIBLE); //
		 * mVisualizerView.clearRenderers(); //mVisualizerView.setVisibility(View.GONE); //Visualizervis = false; }
		 * @Override public void onPanelAnchored(View panel) { fpanel = true; } });
		 */
        Equalizer.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent SecondScreen = new Intent( Player.this, Equalizer2.class );
                Intent SecondScreen = new Intent(Player.this, EqualizerActivity.class);
                startActivity(SecondScreen);
            }
        });
        buttonPlayStop.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (GlobalSongList.GetInstance().GetNowPlayingSize() > 0)
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
                if (GlobalSongList.GetInstance().GetNowPlayingSize() > 0)
                    next();
            }
        });

        previous.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (GlobalSongList.GetInstance().GetNowPlayingSize() > 0)
                    prev();
            }
        });

        bfake.setOnTouchListener(new OnSwipeTouchListener2() {
            @Override
            public void onSwipeRight() {
                if (GlobalSongList.GetInstance().GetNowPlayingSize() > 0)
                    prev();
            }

            @Override
            public void onSwipeLeft() {
                if (GlobalSongList.GetInstance().GetNowPlayingSize() > 0)
                    next();
            }
        });

        edit.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog = Utilities.designdialog(330, Player.this);

                dialog.setContentView(R.layout.dialoge_edit_tags);
                dialog.setTitle(GlobalSongList.GetInstance().GetCurSongDetails().getSong());
                EditText album = (EditText) dialog.findViewById(R.id.al);
                EditText artist = (EditText) dialog.findViewById(R.id.ar);
                EditText title = (EditText) dialog.findViewById(R.id.ti);
                artist.setText(GlobalSongList.GetInstance().GetCurSongDetails().getArtist());
                album.setText(GlobalSongList.GetInstance().GetCurSongDetails().getAlbum());
                title.setText(GlobalSongList.GetInstance().GetCurSongDetails().getSong());

                Button bDOK = (Button) dialog.findViewById(R.id.dBOK);
                Button bDCancle = (Button) dialog.findViewById(R.id.dBCancel);

                dialog.show();
                // metadata.getTrackNumber();
                // album,artist,title;Button bDOK//,bDCancle
                bDOK.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        EditText album = (EditText) dialog.findViewById(R.id.al);
                        EditText artist = (EditText) dialog.findViewById(R.id.ar);
                        EditText title = (EditText) dialog.findViewById(R.id.ti);

                        String artist2 = artist.getText().toString();
                        if (artist2 == null)
                            artist2 = GlobalSongList.GetInstance().GetCurSongDetails().getArtist();

                        String album2 = album.getText().toString();
                        if (album2 == null)
                            album2 = GlobalSongList.GetInstance().GetCurSongDetails().getAlbum();

                        String title2 = title.getText().toString();
                        if (title2 == null)
                            title2 = GlobalSongList.GetInstance().GetCurSongDetails().getSong();

                        meta = new MusicMetadata("name");
                        meta.setAlbum(album2);
                        meta.setArtist(artist2);
                        meta.setSongTitle(title2);

                        GlobalSongList.GetInstance().GetCurSongDetails().setSong(title2);
                        GlobalSongList.GetInstance().GetCurSongDetails().setArtist(artist2);
                        GlobalSongList.GetInstance().GetCurSongDetails().setAlbum(album2);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            File file = new File(GlobalSongList.GetInstance().GetCurSongDetails().getPath2());
                            boolean canwrite = false;
                            if (file.getAbsolutePath().toString().contains("emulated") || file.getAbsolutePath().toString().contains("storage0")) {
                                new EditTags().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void) null);
                            } else {
                                try {
                                    canwrite = StorageAccessAPI.getDocumentFile(file, false).canWrite();
                                } catch (Exception e) {
                                    canwrite = false;
                                }
                                if (canwrite) {
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
        if (!GlobalSongList.GetInstance().boolshuffled) {
            GlobalSongList.GetInstance().boolshuffled = true;
        } else
            GlobalSongList.GetInstance().boolshuffled = false;
    }

    private void showshuffleCrouton() {
        if (GlobalSongList.GetInstance().GetNowPlayingSize() > 1) {
            if (GlobalSongList.GetInstance().boolshuffled)
                ActivityUtil.showCrouton(Player.this, getString(R.string.shuffle_on));
            else
                ActivityUtil.showCrouton(Player.this, getString(R.string.shuffle_off));
        } else if (GlobalSongList.GetInstance().GetNowPlayingSize() == 1)

            ActivityUtil.showCrouton(Player.this, getString(R.string.only_one_song));

    }

    private void setShuffleState() {
        if (GlobalSongList.GetInstance().GetNowPlayingSize() > 1) {
            SharedPreferences.Editor editor = this.getSharedPreferences("shuffle_setting", MODE_PRIVATE).edit();
            if (GlobalSongList.GetInstance().boolshuffled) {
                editor.putBoolean("shuffle_setting", true);
                editor.commit();
                if (GlobalSongList.GetInstance().GetNowPlayingList().size() > 800) // to avoid crashes due to memory
                {
                    GlobalSongList.GetInstance().backup = GlobalSongList.GetInstance().GetNowPlayingList();
                } else
                    GlobalSongList.GetInstance().backup = new ArrayList<SongDetails>(GlobalSongList.GetInstance().GetNowPlayingList());
                SongDetails temp = new SongDetails();
                temp = GlobalSongList.GetInstance().GetCurSongDetails();
                GlobalSongList.GetInstance().RemoveCurSongDetails();
                Collections.shuffle(GlobalSongList.GetInstance().GetNowPlayingList());
                GlobalSongList.GetInstance().Add2CurSongDetails(temp);
                bShufflebg.setBackgroundColor(Color.parseColor("#30000000"));
                //bShuffle.getBackground().setColorFilter(Color.parseColor("#ff00bb"), PorterDuff.Mode.SRC_ATOP);
                temp = null;
            } else {
                editor.putBoolean("shuffle_setting", false);
                editor.commit();
                int x = 0;
                try {
                    x = GlobalSongList.GetInstance().backup.indexOf(GlobalSongList.GetInstance().GetCurSongDetails());
                } catch (Exception e) {
                }
                bShufflebg.setBackgroundColor(Color.parseColor("#00ffffff"));
                //bShuffle.getBackground().setColorFilter(Color.parseColor("#ffffff"), PorterDuff.Mode.SRC_ATOP);

                GlobalSongList.GetInstance().NP_List = GlobalSongList.GetInstance().backup;
                GlobalSongList.GetInstance().setPosition(x);

            }
        }
    }

    class EditTags extends AsyncTask<Void, String, String> {
        @Override
        protected void onPostExecute(String x) {
            if (x.equals("notenoughpermission")) {
                ActivityUtil.showCrouton(Player.this, getString(R.string.denied_permission));
                return;
            }
            // spinner.setVisibility( View.GONE );
            if (!GlobalSongList.GetInstance().GetCurSongDetails().getSong().equals(oldsong)) {
                songname.setText(GlobalSongList.GetInstance().GetCurSongDetails().getSong());
                oldsong = songname.getText().toString();

            }
            if (!GlobalSongList.GetInstance().GetCurSongDetails().getAlbum().equals(oldalbum)) {
                songalbum.setText(GlobalSongList.GetInstance().GetCurSongDetails().getAlbum());
                oldalbum = songalbum.getText().toString();
            }
            if (!GlobalSongList.GetInstance().GetCurSongDetails().getArtist().equals(oldArtist)) {
                songartist.setText(GlobalSongList.GetInstance().GetCurSongDetails().getArtist());
                oldArtist = songartist.getText().toString();
            }
            ActivityUtil.showCrouton(Player.this, getString(R.string.tags_edited));

            meta = null;
            new FetchFromInternet().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }

        @Override
        protected void onPreExecute() {
            // spinner.setVisibility( View.VISIBLE );

            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                File src = new File(GlobalSongList.GetInstance().GetCurSongDetails().getPath2());
                MusicMetadataSet src_set = null;


                src_set = new MyID3().read(src);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && !(src.getAbsolutePath().toString().contains("emulated") || src
                        .getAbsolutePath().toString().contains("storage0"))) {
                    new MyID3().write(src, src, src_set, meta);
                } else
                    new MyID3().update(src, src_set, meta);
            } catch (NullPointerException e) {
                // e = null;
            } catch (UnsupportedEncodingException e) {
                // e.printStackTrace();
                // e = null;
            } catch (ID3WriteException e) {
                // e.printStackTrace();
                // e = null;
            } catch (IOException e) {
                // e.printStackTrace();
                // e = null;
            } // write updated metadata
            catch (OutOfMemoryError e1) {
                e1.printStackTrace();
            } catch (Exception e) {
                // e.printStackTrace();
                // e = null;
            }
            try {
                MediaScannerConnection.scanFile(Player.this, new String[]{GlobalSongList.GetInstance().GetCurSongDetails().getPath2()}, null,
                        new MediaScannerConnection.OnScanCompletedListener() {
                            public void onScanCompleted(String path, Uri uri) {

                                // ab.notifyDataSetChanged();
                            }
                        });
            } catch (Exception e) {
                // e.printStackTrace();
                // e = null;
            }

            return "x";
        }
    }

    public void prev() {
        if (Music_service.mp.getCurrentPosition() > 5000) {
            Intent intentswap = new Intent(IConstant.BROADCAST_SWAP);
            intentswap.putExtra("swap", 0);
            sendBroadcast(intentswap);
            return;
        }
        if (GlobalSongList.GetInstance().getPosition() > 0) {
            GlobalSongList.GetInstance().decreasePosition();
            Intent intentswap = new Intent(IConstant.BROADCAST_SWAP);
            intentswap.putExtra("swap", -1);
            sendBroadcast(intentswap);
        } else if (GlobalSongList.GetInstance().GetNowPlayingSize() > 0) {
            if (GlobalSongList.GetInstance().GetRepeatMode() == GlobalSongList.REPEAT_ALL) {
                GlobalSongList.GetInstance().setPosition(GlobalSongList.GetInstance().GetNowPlayingSize() - 1);
                Intent intentswap = new Intent(IConstant.BROADCAST_SWAP);
                intentswap.putExtra("swap", 0);
                sendBroadcast(intentswap);
            } else if (GlobalSongList.GetInstance().GetRepeatMode() == GlobalSongList.REPEAT_NONE) {
            } else if (GlobalSongList.GetInstance().GetRepeatMode() == GlobalSongList.REPEAT_ONCE) {
            }
        }

        buttonPlayStop.setBackgroundResource(R.drawable.pause2);
        GlobalSongList.GetInstance().boolMusicPlaying1 = true;
    }

    public void next() {
        if (GlobalSongList.GetInstance().getPosition() < GlobalSongList.GetInstance().GetNowPlayingSize() - 1) {
            GlobalSongList.GetInstance().increasePosition();
            Intent intentswap = new Intent(IConstant.BROADCAST_SWAP);
            intentswap.putExtra("swap", 1);
            sendBroadcast(intentswap);
            buttonPlayStop.setBackgroundResource(R.drawable.pause2);
            GlobalSongList.GetInstance().boolMusicPlaying1 = true;
        } else if (GlobalSongList.GetInstance().getPosition() == GlobalSongList.GetInstance().GetNowPlayingSize() - 1
                && GlobalSongList.GetInstance().GetNowPlayingSize() > 0) {
            if (GlobalSongList.GetInstance().GetRepeatMode() == GlobalSongList.REPEAT_NONE) {
                GlobalSongList.GetInstance().boolMusicPlaying1 = false;
                return;
            } else if (GlobalSongList.GetInstance().GetRepeatMode() == GlobalSongList.REPEAT_ONCE) {
                GlobalSongList.GetInstance().boolMusicPlaying1 = false;
                return;
            } else if (GlobalSongList.GetInstance().GetRepeatMode() == GlobalSongList.REPEAT_ALL) {
                GlobalSongList.GetInstance().setPosition(0);
                Intent intentswap = new Intent(IConstant.BROADCAST_SWAP);
                intentswap.putExtra("swap", 1);
                sendBroadcast(intentswap);
                buttonPlayStop.setBackgroundResource(R.drawable.pause2);
                GlobalSongList.GetInstance().boolMusicPlaying1 = true;
            }
        }
    }

    private void buttonPlayStopClick() {
        if (GlobalSongList.GetInstance().boolMusicPlaying1) {
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

    private void playAudio(int position2) {
        // Logger.d("Music_service", "playAudio..................");
        buttonPlayStop.setBackgroundResource(R.drawable.pause2);
        GlobalSongList.GetInstance().boolMusicPlaying1 = true;
        Intent serviceIntent = new Intent(this, Music_service.class);
        // serviceIntent.putParcelableArrayListExtra("sentAudioLink", GlobalSongList.GetInstance().GetNowPlayingList());
        // serviceIntent.putExtra("postion_service", GlobalSongList.GetInstance().getPosition());
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
                if (GlobalSongList.GetInstance().GetCurSongDetails() == null) // why?
                {
                    // do nothing
                    return;
                }
                songname.setText(GlobalSongList.GetInstance().GetCurSongDetails().getSong());
                songalbum.setText(GlobalSongList.GetInstance().GetCurSongDetails().getAlbum());
                songartist.setText(GlobalSongList.GetInstance().GetCurSongDetails().getArtist());
                try {
                    songTotalDurationLabel.setText(GlobalSongList.GetInstance().GetCurSongDetails().getTime());
                } catch (IndexOutOfBoundsException e) {
                }

                if (!oldsong.equals(GlobalSongList.GetInstance().GetCurSongDetails().getSong())) {
                    imagecheck = 0;
                    songname.startAnimation(fadeIn);
                } else songname.setText(oldsong);
                if (!oldalbum.equals(GlobalSongList.GetInstance().GetCurSongDetails().getAlbum())) {
                    imagecheck = 0;
                    songalbum.startAnimation(fadeIn);
                } else songalbum.setText(oldalbum);
                if (!oldArtist.equals(GlobalSongList.GetInstance().GetCurSongDetails().getArtist())) {
                    imagecheck = 0;
                    songartist.startAnimation(fadeIn);
                } else songartist.setText(oldArtist);
                if (!oldTime.equals(GlobalSongList.GetInstance().GetCurSongDetails().getTime())) {
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

                if (GlobalSongList.GetInstance().GetCurSongDetails().getAlbum().equals(oldalbum)
                        && GlobalSongList.GetInstance().GetCurSongDetails().getArtist().equals(oldArtist)) {
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

        // Logger.d("Player", "startCoverOperation...................................");
        // if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB)
        // {

        // }
        // else
        // {
        // new StartCoverOperationTask().execute((Void) null);
        // }
        isStartCoverOperation = true;
    }

    private void settextetc() {
        fadein(00, 200);
        fadeout(00, 500);
        if (!GlobalSongList.GetInstance().GetCurSongDetails().getSong().equals(oldsong)) {
            songname.startAnimation(fadeOut);
        }
        if (!oldalbum.equals(GlobalSongList.GetInstance().GetCurSongDetails().getAlbum())) {
            songalbum.startAnimation(fadeOut);
        }
        if (!oldArtist.equals(GlobalSongList.GetInstance().GetCurSongDetails().getArtist())) {
            songartist.startAnimation(fadeOut);
        }

        if (!oldTime.equals(GlobalSongList.GetInstance().GetCurSongDetails().getTime())) {
            songTotalDurationLabel.startAnimation(fadeOut);
        }
    }

    @Override
    protected void onPause() {
        try {
            unregisterReceiver(broadcastReceiver);
        } catch (Exception e) {
        }
        ActionIsRegistered = false;
        try {
            unregisterReceiver(headsetReceiver);
        } catch (Exception e) {
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
        if (!IConstant.IS_PRO_VERSION) {
            if (GlobalSongList.secondtimeplayershown == 0)
                InterstitialAd.display(this);
        }

        if (!ActionIsRegistered) {
            registerReceiver(broadcastReceiver, new IntentFilter(IConstant.BROADCAST_ACTION));
            ActionIsRegistered = true;
        }

        if (!HeadsetIsRegistered) {
            registerReceiver(headsetReceiver, new IntentFilter(Intent.ACTION_HEADSET_PLUG));
            HeadsetIsRegistered = true;
        }

        if (GlobalSongList.GetInstance().GetNowPlayingSize() == 0) {
            songname.setText(getString(R.string.no_songs));
            songalbum.setText("");
            songartist.setText("");
            PrevBgImage.setBackgroundResource(R.drawable.grad);
            // imageBG.setImageResource(R.drawable.grad);
        } else {
            // Logger.d("Player", "startCoverOperation......................onResume");
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
        progress2 = null;

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }

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
                GlobalSongList.GetInstance().boolMusicPlaying1 =

                        false;
            } else if (headsetSwitch == 1) {
                buttonPlayStop.setBackgroundResource(R.drawable.pause2);
                GlobalSongList.GetInstance().boolMusicPlaying1 =

                        true;
            }

        }

    };

	/*
	 * private Bitmap adjustedContrast(Bitmap src, double value) { int width = src.getWidth(); int height = src.getHeight(); // create output bitmap
	 * // create a mutable empty bitmap Bitmap bmOut = Bitmap.createBitmap(width, height, src.getConfig()); // create a canvas so that we can draw the
	 * bmOut Bitmap from source // bitmap Canvas c = new Canvas(); c.setBitmap(bmOut); // draw bitmap to bmOut from src bitmap so we can modify it
	 * c.drawBitmap(src, 0, 0, new Paint(Color.BLACK)); // color information int A, R, G, B; int pixel; // get contrast value double contrast =
	 * Math.pow((100 + value) / 100, 2); // scan through all pixels for (int x = 0; x < width; ++x) { for (int y = 0; y < height; ++y) { // get pixel
	 * color pixel = src.getPixel(x, y); A = Color.alpha(pixel); // apply filter contrast for every channel R, G, B R = Color.red(pixel); R = (int)
	 * (((((R / 255.0) - 0.5) * contrast) + 0.5) * 255.0); if (R < 0) { R = 0; } else if (R > 255) { R = 255; } G = Color.green(pixel); G = (int)
	 * (((((G / 255.0) - 0.5) * contrast) + 0.5) * 255.0); if (G < 0) { G = 0; } else if (G > 255) { G = 255; } B = Color.blue(pixel); B = (int)
	 * (((((B / 255.0) - 0.5) * contrast) + 0.5) * 255.0); if (B < 0) { B = 0; } else if (B > 255) { B = 255; } bmOut.setPixel(x, y, Color.argb(A, R,
	 * G, B)); } } src.recycle(); c = null; return bmOut; }
	 */

	/*
	 * public Bitmap highlightImage(Bitmap src) { // create new bitmap, which will be painted and becomes result image // TODO Bitmap bmOut =
	 * Bitmap.createBitmap(src.getWidth(), src.getWidth(), Bitmap.Config.ARGB_8888); // setup canvas for painting Canvas canvas = new Canvas(bmOut);
	 * // setup default color canvas.drawColor(0, PorterDuff.Mode.CLEAR); // create a blur paint for capturing alpha Paint ptBlur = new Paint();
	 * ptBlur.setMaskFilter(new BlurMaskFilter(15, Blur.NORMAL)); int[] offsetXY = new int[2]; offsetXY[0] = src.getWidth(); offsetXY[1] =
	 * src.getWidth(); // capture alpha into a bitmap Bitmap bmAlpha = src.extractAlpha(ptBlur, offsetXY); // create a color paint Paint ptAlphaColor
	 * = new Paint(); ptAlphaColor.setColor(0x33000000); // paint color for captured alpha region (bitmap) canvas.drawBitmap(bmAlpha, offsetXY[0],
	 * offsetXY[1], ptAlphaColor); // free memory bmAlpha.recycle(); src.recycle(); ptBlur = null; canvas = null; // paint the image source //
	 * canvas.drawBitmap(src, 0, 0, null); // return out final image return bmOut; }
	 */

    @Override
    public void onDestroy() {
        if (GlobalSongList.secondtimeplayershown == 0)
            GlobalSongList.secondtimeplayershown = 1;

        else if (GlobalSongList.secondtimeplayershown == 1)
            GlobalSongList.secondtimeplayershown = 2;
        else if (GlobalSongList.secondtimeplayershown == 2)
            GlobalSongList.secondtimeplayershown = 0;

        try {
            unregisterReceiver(broadcastCoverReceiver);
        } catch (IllegalArgumentException e) {
        } catch (IllegalStateException e) {
        }
        try {
            unregisterReceiver(checkagain);
        } catch (IllegalArgumentException e) {
        } catch (IllegalStateException e) {
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

    protected void fadeinimage(int offset, int duration)

    {
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

    /*
	 * @Override public void onBackPressed() { if (fpanel == true) { layout.collapsePane(); fpanel = false; } else if (fpanel == false) {
	 * super.onBackPressed(); } }
	 */
    public void checkButtonRandom() {
        if (GlobalSongList.GetInstance().GetRepeatMode() == GlobalSongList.REPEAT_NONE) {
            brepeat.setBackgroundResource(R.drawable.repeat_off_tran);
        } else if (GlobalSongList.GetInstance().GetRepeatMode() == GlobalSongList.REPEAT_ONCE) {
            brepeat.setBackgroundResource(R.drawable.repeat_one_tran);
        } else if (GlobalSongList.GetInstance().GetRepeatMode() == GlobalSongList.REPEAT_ALL) {
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
        GlobalSongList.GetInstance().SaveLastPlaylist(isFromRecentAdded, click_no);
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
        } catch (Exception e) {
        }
        setResult(RESULT_OK);

        if (IConstant.IS_AMAZONVERSION) {

            // if ( mInterstitialAd.isLoaded( ) )
            // {
            // mInterstitialAd.show( );
            // }
            // mInterstitialAd.setAdListener( new AdListener( ) {
            // @Override
            // public void onAdClosed( )
            // {
            // requestNewInterstitial( );
            //
            // }
            // } );

        }
        finish();
    }

    private void requestNewInterstitial() {
        // AdRequest adRequest = new AdRequest.Builder( ).build( );
        //
        // mInterstitialAd.loadAd( adRequest );
    }

    class StartCoverOperationTask extends AsyncTask<Void, Void, Bitmap> {
        int wt_px, ht_px;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if (GlobalSongList.GetInstance().boolshuffled == true) {
                bShufflebg.setBackgroundColor(Color.parseColor("#30000000"));
                //bShuffle.getBackground().setColorFilter(Color.parseColor("#ff00bb"), PorterDuff.Mode.SRC_ATOP);
            } else if (GlobalSongList.GetInstance().boolshuffled == false) {
                bShufflebg.setBackgroundColor(Color.parseColor("#00ffffff"));
                //bShuffle.getBackground().setColorFilter(Color.parseColor("#ffffff"), PorterDuff.Mode.SRC_ATOP);
            }

            wt_px = (int) Player.this.getResources().getDimension(R.dimen.player_image_size);
            ht_px = (int) Player.this.getResources().getDimension(R.dimen.player_image_size);
        }

        @Override
        protected Bitmap doInBackground(Void... arg0) {
            if (GlobalSongList.GetInstance().GetCurSongDetails() == null) {
                return null;
            }
            int albumID = GlobalSongList.GetInstance().GetCurSongDetails().getAlbumID();
            Logger.d("Player", "doInBackground............. albumID = " + albumID);
            // if (playingBGResID == 0)
            // {
            // playingBGResID = IConstant.arr [ new Random().nextInt(IConstant.arr.length) ];
            // GlobalSongList.GetInstance().GetCurSongDetails().setPlayingBGResID(playingBGResID);
            // }
            // else
            // {
            // if (playingBGResID < R.drawable.album_art_1 || playingBGResID > R.drawable.album_art_9)
            // {
            // playingBGResID = IConstant.arr [ new Random().nextInt(IConstant.arr.length) ];
            // }
            // }
            Cursor cursor = null;
            try {
                cursor = Player.this.getContentResolver().query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                        new String[]{MediaStore.Audio.Albums._ID, MediaStore.Audio.Albums.ALBUM_ART}, MediaStore.Audio.Albums._ID + "=?",
                        new String[]{String.valueOf(albumID)}, null);

                if (cursor != null && cursor.moveToFirst()) {
                    GlobalSongList.GetInstance().path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART));
                    // do whatever you need to do
                }
            } catch (SQLiteException e) {
            } catch (Exception e) {
            } finally {
                if (cursor != null) {
                    if (!cursor.isClosed()) {
                        cursor.close();
                        cursor = null;
                    }
                }
            }
            album = imgLoader.fetchfilefromcahce(GlobalSongList.GetInstance().GetCurSongDetails().getAlbum(),
                    GlobalSongList.GetInstance().GetCurSongDetails().getArtist());
            if (album == null) {
                album = BitmapUtil.GetBitmapFromSongPath(Player.this.getResources(), albumID, wt_px, ht_px,
                        GlobalSongList.GetInstance().GetCurSongDetails().getPath2());

            }

            return album;
        }

        @Override
        protected void onPostExecute(final Bitmap result) {
            super.onPostExecute(result);

            // Logger.d("Player", "onPostExecute............. result = " + result);
            if (result != null) {
                album = result;
            } else {
                // album = imgLoader.fetchfilefromcahce(GlobalSongList.GetInstance().GetCurSongDetails().getAlbum(), GlobalSongList.GetInstance()
                // .GetCurSongDetails().getArtist());

                // Logger.d("Player", "fetchfilefromcahce............. album = " + album);
                album = BitmapUtil.GetRandomBitmap(Player.this.getResources(), GlobalSongList.GetInstance().GetCurSongDetails().getAlbumID(),
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
            // Logger.d("Player", "FetchFromInternet............. doInBackground");
            Bitmap bitmap = imgLoader.getBitmap2(GlobalSongList.GetInstance().GetCurSongDetails().getArtist(),
                    GlobalSongList.GetInstance().GetCurSongDetails().getAlbum());
            return bitmap;
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
                StackBlurManager _stackBlurManager = new StackBlurManager(params[0]);
                _stackBlurManager.process(65);
                Bitmap dstBmp = _stackBlurManager.returnBlurredImage();
                // dstBmp = adjustedContrast(dstBmp, 9);
                d = new BitmapDrawable(getResources(), dstBmp);
                ColorMatrix matrix = new ColorMatrix();
                matrix.setSaturation((float) 1.24);
                ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
                d.setColorFilter(filter);

                if (IConstant.OPTIMIZE_MEM_RECYCLE_BITMAP) {
                    dstBmp.recycle();
                    dstBmp = null;
                }
            } catch (OutOfMemoryError e) {
                outofmemoryerror = true;
            } catch (ArrayIndexOutOfBoundsException e) {
            } catch (Exception e) {
            }
            if (d == null && outofmemoryerror == false) {
                try {
                    StackBlurManager _stackBlurManager = new StackBlurManager(Bitmap.createScaledBitmap(params[0], 120, 120, false));
                    _stackBlurManager.process(65);
                    Bitmap dstBmp = _stackBlurManager.returnBlurredImage();
                    // dstBmp = adjustedContrast(dstBmp, 9);
                    d = new BitmapDrawable(getResources(), dstBmp);
                    ColorMatrix matrix = new ColorMatrix();
                    matrix.setSaturation((float) 0.84);
                    ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
                    d.setColorFilter(filter);

                    if (IConstant.OPTIMIZE_MEM_RECYCLE_BITMAP) {
                        dstBmp.recycle();
                        dstBmp = null;
                    }
                } catch (OutOfMemoryError e) {
                } catch (ArrayIndexOutOfBoundsException e) {
                } catch (Exception e) {
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

            // if (!GlobalSongList.GetInstance().GetCurSongDetails().getSong().equals(oldsong)
            // || !GlobalSongList.GetInstance().GetCurSongDetails().getAlbum().equals(oldalbum))
            if (imagecheck == 0) {
                i1.startAnimation(fadeOutImage);
                // PrevBgImage.startAnimation( fadeOutImage );

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
                    // TODO Auto-generated method stub

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    // TODO Auto-generated method stub
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                        try {
                            PrevBgImage.setBackgroundDrawable(result);
                        } catch (NullPointerException e) {
                        } catch (Exception e) {
                        }
                        NextBgImage.setAlpha(0f);
                    } else {
                        try {
                            PrevBgImage.setBackground(result);
                        } catch (NullPointerException e) {
                        } catch (Exception e) {
                        }
                        NextBgImage.setAlpha(0f);
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                    // TODO Auto-generated method stub

                }
            });

            // imageBG.setImageDrawable(result);
            // fadein(0, 1000);
            // if (!GlobalSongList.GetInstance().GetCurSongDetails().getSong().equals(oldsong)
            // || !GlobalSongList.GetInstance().GetCurSongDetails().getAlbum().equals(oldalbum))
            // {
            // imageBG.startAnimation(fadeIn);
            // }

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

    @Override
    protected String GetGAScreenName() {
        return "Player";
    }

    // @Override
    // public void onImageLoaded( )
    // {
    // if ( text != null || apk != null )
    // {
    // Crouton c = Crouton.make( this, v );
    // HowManyTimesAdAppeared.onStart( this );
    // if ( HowManyTimesAdAppeared.toShowCroutonIfNeeded( ) )
    // c.show( );
    // }

    // }

}
