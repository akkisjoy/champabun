package champak.champabun.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.media.RemoteControlClient;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.support.v7.app.NotificationCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.widget.RemoteViews;

import champak.champabun.GlobalSongList;
import champak.champabun.IConstant;
import champak.champabun.Player;
import champak.champabun.R;
import champak.champabun.albums.lazylist.ImageLoader;
import champak.champabun.equalizer.Singleton;
import champak.champabun.util.BitmapUtil;
import champak.champabun.util.PlayMeePreferences;

@SuppressWarnings("deprecation")
public class Music_service extends Service
        implements OnCompletionListener, OnPreparedListener, OnErrorListener, OnSeekCompleteListener, OnInfoListener {
    private static final String TAG = "Music_service";

    public static MediaPlayer mp;// = new MediaPlayer();

    Intent bufferIntent, nextsongcoverIntent;
    // int position;
    private static final int NOTIFICATION_ID = 1;
    private boolean isPausedInCall = false;
    private PhoneStateListener phoneStateListener;
    private TelephonyManager telephonyManager;
    private int headsetSwitch = 1;
    int playpause;
    String sntSeekPos;
    int intSeekPos;
    int mediaPosition;

    int mediaMax;
    // Intent intentcoveradapter;
    private final Handler handler = new Handler();
    private static int songEnded;
    Intent seekIntent;
    Intent updateWidgetService;

    private boolean bIsReceiverRegistered, bIsHeadsetReceiverRegistered;

    private WakeLock mWakeLock;
    private RemoteControlClient mRemoteControlClient;
    private AudioManager mAudioManager;

    private boolean mPausedByTransientLossOfFocus = false;
    private int mServiceStartId = -1;
    private boolean mIsInitialized = false;
    Singleton m_Inst = Singleton.getInstance();

    // private final MyWidget mAppWidgetProvider = MyWidget.getInstance();
    @Override
    public void onCreate() {
        init();
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, this.getClass().getName());
        mWakeLock.setReferenceCounted(false);

        Intent mediaButtonIntent = new Intent(Intent.ACTION_MEDIA_BUTTON);
        mediaButtonIntent.setComponent(new ComponentName(Music_service.this.getPackageName(), MediaButtonIntentReceiver.class.getName()));
        mRemoteControlClient = new RemoteControlClient(PendingIntent.getBroadcast(getApplicationContext(), 0, mediaButtonIntent, 0));
        mRemoteControlClient.setTransportControlFlags(RemoteControlClient.FLAG_KEY_MEDIA_PLAY_PAUSE | RemoteControlClient.FLAG_KEY_MEDIA_NEXT
                | RemoteControlClient.FLAG_KEY_MEDIA_PREVIOUS);

        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mAudioManager.registerMediaButtonEventReceiver(
                new ComponentName(Music_service.this.getPackageName(), MediaButtonIntentReceiver.class.getName()));

        // initializeEqualisers();
    }

    private void initMediaPlayer() {
        if (mp != null) {
            try {
                if (mp.isPlaying()) {
                    mp.stop();
                    mp.release();
                    mp = null;
                }
            } catch (IllegalStateException e) {
            }
        }
        mp = new MediaPlayer();
        mp.setOnCompletionListener(this);
        mp.setOnPreparedListener(this);
        mp.setOnErrorListener(this);
        mp.setOnSeekCompleteListener(this);
        mp.setOnInfoListener(this);
        mp.setWakeMode(Music_service.this, PowerManager.PARTIAL_WAKE_LOCK);

        mIsInitialized = false;
        GlobalSongList.GetInstance().boolMusicPlaying1 = false;
    }

    private void init() {
        initMediaPlayer();
        nextsongcoverIntent = new Intent(IConstant.BROADCAST_COVER);
        // intentcoveradapter=new Intent(BROADCAST_COVERPARALLAX);
        seekIntent = new Intent(IConstant.BROADCAST_ACTION);

        if (!bIsHeadsetReceiverRegistered) {
            registerReceiver(headsetReceiver, new IntentFilter(Intent.ACTION_HEADSET_PLUG));
            registerReceiver(buttonplaypauseReceiver, new IntentFilter(IConstant.BROADCAST_PLAYPAUSE));
            // registerReceiver(widgetopenactivityReceiver, new IntentFilter(IConstant.BROADCAST_OPEN_ACTIVITY));
            registerReceiver(stopthisshit, new IntentFilter(IConstant.BROADCAST_STOP_NOW));
            registerReceiver(stop, new IntentFilter(IConstant.BROADCAST_STOP));
            bIsHeadsetReceiverRegistered = true;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startid) {
        // Logger.d("Music_service", "onStartCommand..................");
        mServiceStartId = startid;

        String callfrom = null;
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            callfrom = bundle.getString("callfrom");
        }
        if ("MediaButtonIntentReceiver".equals(callfrom)) {
            return CallFromMediaButtonIntentReceiver(intent);
        }

        init();

        // GlobalSongList.GetInstance().boolshuffled = false;
        if (!bIsReceiverRegistered) {
            registerReceiver(broadcastReceiver, new IntentFilter(IConstant.BROADCAST_SEEKBAR));
            registerReceiver(swapReceiverwidget, new IntentFilter(IConstant.BROADCAST_PREV));
            registerReceiver(swapReceiver, new IntentFilter(IConstant.BROADCAST_SWAP));
            bIsReceiverRegistered = true;
        }

        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        phoneStateListener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                // Logger.d( TAG, "Starting CallStateChange state = " + state );
                switch (state) {
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                    case TelephonyManager.CALL_STATE_RINGING:
                        if (mp != null) {
                            if (GlobalSongList.GetInstance().boolMusicPlaying1) {
                                isPausedInCall = true;
                            }
                            pauseMedia();
                        }
                        break;
                    case TelephonyManager.CALL_STATE_IDLE:
                        if (mp != null) {
                            if (isPausedInCall && !GlobalSongList.GetInstance().boolMusicPlaying1) {
                                playMedia();
                                isPausedInCall = false;
                            }
                        }
                        break;
                }
            }
        };
        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);

        // try
        // {// songdetails =
        // // intent.getParcelableArrayListExtra("sentAudioLink");
        // // position = intent.getIntExtra("postion_service", 0);
        // }
        // catch (Exception e)
        // {
        // }
        try {
            if (mp != null && mp.isPlaying()) {
                mp.stop();
                mp.release();
            }
            mp.reset();
        } catch (Exception e) {
            e.printStackTrace();
        }

        setupHandler();
        // Logger.d("Music_service", "onStartCommand.................. GlobalSongList.GetInstance().GetNowPlayingSize() = "
        // + GlobalSongList.GetInstance().GetNowPlayingSize());
        if (GlobalSongList.GetInstance().GetNowPlayingSize() > 0)
            settingsource();

        return START_NOT_STICKY;
    }

    private int CallFromMediaButtonIntentReceiver(Intent intent) {
        String action = intent.getAction();
        String cmd = intent.getStringExtra(IConstant.CMDNAME);

        if (IConstant.CMDNEXT.equals(cmd) || IConstant.NEXT_ACTION.equals(action)) {
            next();
        } else if (IConstant.CMDPREVIOUS.equals(cmd) || IConstant.PREVIOUS_ACTION.equals(action)) {
            prev();
        } else if (IConstant.CMDTOGGLEPAUSE.equals(cmd) || IConstant.TOGGLEPAUSE_ACTION.equals(action)) {
            if (isPlaying()) {
                pauseMedia();
                mPausedByTransientLossOfFocus = false;
            } else {
                playMedia();
            }
        } else if (IConstant.CMDPLAY.equals(cmd) || IConstant.PLAY_ACTION.equals(action)) {
            playMedia();
        } else if (IConstant.CMDPAUSE.equals(cmd) || IConstant.PAUSE_ACTION.equals(action)) {
            pauseMedia();
            mPausedByTransientLossOfFocus = false;
        } else if (IConstant.CMDSTOP.equals(cmd)) {
            pauseMedia();
            mPausedByTransientLossOfFocus = false;
            seek(0);
        }

        return START_NOT_STICKY;
    }

    private void setupHandler() {
        handler.removeCallbacks(sendUpdatesToUI);
        handler.postDelayed(sendUpdatesToUI, 1000); // 1 second
    }

    private Runnable sendUpdatesToUI = new Runnable() {
        public void run() {
            LogMediaPosition();
            handler.postDelayed(this, 1000); // 2 seconds
        }
    };

    private void LogMediaPosition() {
        if (mp.isPlaying()) {
            mediaPosition = mp.getCurrentPosition();
            mediaMax = mp.getDuration();
            seekIntent.putExtra("counter", String.valueOf(mediaPosition));
            seekIntent.putExtra("mediamax", String.valueOf(mediaMax));
            seekIntent.putExtra("song_ended", String.valueOf(songEnded));
            sendBroadcast(seekIntent);
        }
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateSeekPos(intent);
        }
    };

    public void updateSeekPos(Intent intent) {
        int seekPos = intent.getIntExtra("seekpos", 0);
        if (mp.isPlaying()) {
            handler.removeCallbacks(sendUpdatesToUI);
        }
        if (mp.isPlaying()) {
            mp.seekTo(seekPos);
        } else {
            mp.seekTo(seekPos);
            mp.pause();
        }
        setupHandler();

    }

    BroadcastReceiver headsetReceiver = new BroadcastReceiver() {
        private boolean headsetConnected = false;

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.hasExtra("state")) {
                if (headsetConnected && intent.getIntExtra("state", 0) == 0) {
                    headsetConnected = false;
                    headsetSwitch = 0;
                } else if (!headsetConnected && intent.getIntExtra("state", 0) == 1) {
                    headsetConnected = true;
                    headsetSwitch = 1;
                }
            }
            switch (headsetSwitch) {
                case (0): {
                    if (!isPausedInCall)
                        pauseMedia();
                    break;
                }
                case (1): {
                    if (!isPausedInCall)
                        playMedia();
                    break;
                }
            }
        }
    };
    BroadcastReceiver swapReceiverwidget = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (GlobalSongList.GetInstance().getPosition() != 0) {
                GlobalSongList.GetInstance().decreasePosition();
            } else {
                if (GlobalSongList.GetInstance().GetRepeatMode() == GlobalSongList.REPEAT_NONE) {
                } else if (GlobalSongList.GetInstance().GetRepeatMode() == GlobalSongList.REPEAT_ONCE) {
                } else if (GlobalSongList.GetInstance().GetRepeatMode() == GlobalSongList.REPEAT_ALL) {
                    GlobalSongList.GetInstance().decreasePosition();
                }
            }

            stopMedia();
            if (GlobalSongList.GetInstance().GetNowPlayingSize() > 0)
                settingsource();
        }
    };

    BroadcastReceiver swapReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int zzz = intent.getIntExtra("nextprev", 0);// this line is for the widget
            GlobalSongList.GetInstance().boolMusicPlaying1 = true;

            if (zzz == 1) {
                if (GlobalSongList.GetInstance().GetNowPlayingSize() - 1 != GlobalSongList.GetInstance().getPosition()) {
                    GlobalSongList.GetInstance().increasePosition();
                } else if (GlobalSongList.GetInstance().GetNowPlayingSize() - 1 == GlobalSongList.GetInstance().getPosition()
                        && GlobalSongList.GetInstance().GetNowPlayingSize() > 0) {
                    if (GlobalSongList.GetInstance().GetRepeatMode() == GlobalSongList.REPEAT_NONE) {
                        GlobalSongList.GetInstance().boolMusicPlaying1 = false;
                        return;
                    } else if (GlobalSongList.GetInstance().GetRepeatMode() == GlobalSongList.REPEAT_ONCE) {
                        GlobalSongList.GetInstance().boolMusicPlaying1 = false;
                        return;
                    } else if (GlobalSongList.GetInstance().GetRepeatMode() == GlobalSongList.REPEAT_ALL) {
                        GlobalSongList.GetInstance().setPosition(0);
                        GlobalSongList.GetInstance().boolMusicPlaying1 = true;
                    }
                }
            }

            stopMedia();
            updateWidgetService = new Intent(getApplicationContext(), UpdateWidgetService.class);
            context.startService(updateWidgetService);
            notifyChange();
            if (GlobalSongList.GetInstance().GetNowPlayingSize() > 0) {
                settingsource();
                Intent checkagain = new Intent(IConstant.BROADCAST_CHECK_AGAIN);
                sendBroadcast(checkagain);
            }
        }
    };

    BroadcastReceiver buttonplaypauseReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            playpause = intent.getIntExtra("playpause", 2);
            if (mp.isPlaying()) {
                try {
                    mp.pause();
                    isPausedInCall = false;
                    GlobalSongList.GetInstance().boolMusicPlaying1 = false;
                    initNotification();
                } catch (Exception e) {
                }
            } else {
                try {
                    playMedia();
                } catch (Exception e) {
                }
            }
            Intent checkagain = new Intent(IConstant.BROADCAST_CHECK_AGAIN);
            sendBroadcast(checkagain);
            updateWidgetService = new Intent(getApplicationContext(), UpdateWidgetService.class);
            context.startService(updateWidgetService);
            notifyChange();
        }
    };

    // private BroadcastReceiver widgetopenactivityReceiver = new OpenPlayerReceiver();

    BroadcastReceiver stopthisshit = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            stopMedia();
        }
    };

    // handle this receiver in case, activity has been removed from recent list
    BroadcastReceiver stop = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            stopSelf();
        }
    };

    @Override
    public boolean onUnbind(Intent intent) {
        // No active playlist, OK to stop the service right now
        stopSelf(mServiceStartId);
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releaseEqualizers();

        //Logger.d( TAG, "unregisterRemoteControlClient" );
        mAudioManager.unregisterMediaButtonEventReceiver(
                new ComponentName(Music_service.this.getPackageName(), MediaButtonIntentReceiver.class.getName()));
        mAudioManager.unregisterRemoteControlClient(mRemoteControlClient);

        mWakeLock.release();
        if (mp != null) {
            try {
                if (mp.isPlaying()) // throw IllegalStateException, why???
                {
                    GlobalSongList.GetInstance().boolMusicPlaying1 = false;
                    mp.stop();
                }
            } catch (IllegalStateException e) {
            }
            mp.release();
            mp = null;
        }
        mAudioManager.abandonAudioFocus(mAudioFocusListener);
        if (phoneStateListener != null) {
            telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
        }
        cancelNotification();

        updateWidgetService = new Intent(getApplicationContext(), UpdateWidgetService.class);
        stopService(updateWidgetService);

        try {
            unregisterReceiver(headsetReceiver);
        } catch (Exception e) {
        }
        try {
            unregisterReceiver(buttonplaypauseReceiver);
        } catch (Exception e) {
        }
        // try
        // {
        // unregisterReceiver(widgetopenactivityReceiver);
        // }
        // catch (Exception e)
        // {
        // }
        try {
            unregisterReceiver(stopthisshit);
        } catch (Exception e) {
        }
        try {
            unregisterReceiver(broadcastReceiver);
        } catch (Exception e) {
        }
        try {
            unregisterReceiver(swapReceiverwidget);
        } catch (Exception e) {
        }
        try {
            unregisterReceiver(swapReceiver);
        } catch (Exception e) {
        }
        try {
            unregisterReceiver(stop);
        } catch (Exception e) {
        }
        bIsReceiverRegistered = false;
        bIsHeadsetReceiverRegistered = false;

        handler.removeCallbacks(sendUpdatesToUI);
    }

    private void initNotification() {
        ImageLoader imgLoader = new ImageLoader(Music_service.this.getApplicationContext());
        if (GlobalSongList.GetInstance().GetCurSongDetails() == null) {
            cancelNotification();
            return;
        }
        if (android.os.Build.VERSION.SDK_INT < 16) {
            int icon = R.drawable.ic_launcher;
            CharSequence tickerText = getString(R.string.app_name);
            long when = System.currentTimeMillis();

            Context context = getApplicationContext();
            CharSequence contentTitle = (CharSequence) GlobalSongList.GetInstance().GetCurSongDetails().getSong();
            CharSequence contentText = (CharSequence) GlobalSongList.GetInstance().GetCurSongDetails().getArtist();

            Notification notification = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setOngoing(true)
                    .setAutoCancel(false)
                    .setContentTitle(contentTitle)
                    .setContentText(contentText)
                    .build();

            Intent notificationIntent = new Intent(this, Player.class);
            notificationIntent.putExtra("comefrom", "Music_service");
            PendingIntent contentIntent = PendingIntent.getActivity(context, IConstant.REQUEST_CODE, notificationIntent, 0);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(NOTIFICATION_ID, notification);
//            notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
//            startForeground(NOTIFICATION_ID, notification);
        } else {
            Intent intent = new Intent(this, Player.class);
            intent.putExtra("comefrom", "Music_service");
            PendingIntent pIntent = PendingIntent.getActivity(this, IConstant.REQUEST_CODE, intent, 0);

            Intent playIntent = new Intent(IConstant.BROADCAST_PLAYPAUSE);
            PendingIntent playPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), IConstant.REQUEST_CODE, playIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            Intent stopIntent = new Intent(IConstant.BROADCAST_STOP);
            PendingIntent stopPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), IConstant.REQUEST_CODE, stopIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            Intent previousIntent = new Intent(IConstant.BROADCAST_PREV);
            PendingIntent previousPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), IConstant.REQUEST_CODE, previousIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            Intent nextIntent = new Intent(IConstant.BROADCAST_SWAP);
            nextIntent.putExtra("nextprev", 1);

            PendingIntent nextPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), IConstant.REQUEST_CODE, nextIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.notification);

            int wt_px = (int) getResources().getDimension(R.dimen.notification_height);


            Bitmap bitmap = imgLoader.fetchfilefromcahce(GlobalSongList.GetInstance().GetCurSongDetails().getAlbum(),
                    GlobalSongList.GetInstance().GetCurSongDetails().getArtist());
            if (bitmap != null)
                bitmap = Bitmap.createScaledBitmap(bitmap, wt_px, wt_px, false);
            if (bitmap == null)
                bitmap = BitmapUtil.GetBitmapFromSongPath(getResources(), GlobalSongList.GetInstance().GetCurSongDetails().getAlbumID(),
                        wt_px, wt_px, GlobalSongList.GetInstance().GetCurSongDetails().getPath2());
            if (bitmap == null)
                bitmap = BitmapUtil.GetRandomBitmap(Music_service.this.getResources(), GlobalSongList.GetInstance().GetCurSongDetails().getAlbumID(),
                        wt_px, wt_px);

            // Logger.d("Music_service", "GlobalSongList.GetInstance().GetCurSongDetails().getPlayingBGResID() = "
            // + GlobalSongList.GetInstance().GetCurSongDetails().getPlayingBGResID());
            if (bitmap != null) {
                contentView.setImageViewBitmap(R.id.image, bitmap);
            } else {
                contentView.setImageViewResource(R.id.image, R.drawable.ic_launcher);
            }

            contentView.setImageViewResource(R.id.bPlayPause,
                    GlobalSongList.GetInstance().boolMusicPlaying1 == true ? R.drawable.pause2 : R.drawable.play);

            contentView.setTextViewText(R.id.title, GlobalSongList.GetInstance().GetCurSongDetails().getSong());
            contentView.setTextViewText(R.id.text, GlobalSongList.GetInstance().GetCurSongDetails().getArtist());
            contentView.setOnClickPendingIntent(R.id.stop, stopPendingIntent);
            contentView.setOnClickPendingIntent(R.id.bPrevious, previousPendingIntent);
            contentView.setOnClickPendingIntent(R.id.bPlayPause, playPendingIntent);
            contentView.setOnClickPendingIntent(R.id.bNext, nextPendingIntent);

            // RemoteViews customNotifView = new RemoteViews(getPackageName(), R.layout.notification_big);
            // if (bitmap != null)
            // {
            // contentView.setImageViewBitmap(R.id.image, bitmap);
            // }
            // else
            // {
            // customNotifView.setImageViewResource(R.id.image, R.drawable.ic_launcher);
            // }
            // customNotifView.setTextViewText(R.id.title, GlobalSongList.GetInstance().GetCurSongDetails().getSong());
            // customNotifView.setTextViewText(R.id.text, GlobalSongList.GetInstance().GetCurSongDetails().getArtist());
            // customNotifView.setOnClickPendingIntent(R.id.bPrevious, previousPendingIntent);
            // customNotifView.setOnClickPendingIntent(R.id.bPlayPause, playPendingIntent);
            // customNotifView.setOnClickPendingIntent(R.id.bNext, nextPendingIntent);
            // customNotifView.setOnClickPendingIntent(R.id.stop, stopPendingIntent);
            //
            // customNotifView.setImageViewResource(R.id.bPlayPause,
            // GlobalSongList.GetInstance().boolMusicPlaying1 == true ? R.drawable.notification_pause : R.drawable.notification_play);

            Notification n = new Notification.Builder(this).setContent(contentView)
                    .setContentTitle(GlobalSongList.GetInstance().GetCurSongDetails().getSong())
                    // .setContentText(ma.NP_List.get(ma.position).Artist)
                    .setSmallIcon(R.drawable.ic_launcher).setContentIntent(pIntent)
                    // .setAutoCancel(true)
                    // .addAction(R.drawable.notification_prev, "",
                    // previousPendingIntent)
                    // .addAction(
                    // ma.boolMusicPlaying1 == true ? R.drawable.notification_pause
                    // : R.drawable.notification_play, "",
                    // playPendingIntent)
                    // .addAction(R.drawable.notification_next, "", nextPendingIntent)
                    .build();
            // n.bigContentView = customNotifView;
            n.contentView = contentView;

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(NOTIFICATION_ID, n);
//            startForeground(NOTIFICATION_ID, n);
        }
        initWidgetView();
    }

    private void initWidgetView() {
        RemoteViews widgetView = new RemoteViews(getPackageName(), R.layout.widget);
        ImageLoader imgLoader = new ImageLoader(Music_service.this.getApplicationContext());
        Bitmap bitmap = imgLoader.fetchfilefromcahce(GlobalSongList.GetInstance().GetCurSongDetails().getAlbum(),
                GlobalSongList.GetInstance().GetCurSongDetails().getArtist());

        try {
            bitmap = Bitmap.createScaledBitmap(bitmap, 300, 300, false);
        } catch (NullPointerException e) {
        }

        if (bitmap == null) {

            bitmap = BitmapUtil.GetBitmapFromSongPath(getResources(), GlobalSongList.GetInstance().GetCurSongDetails().getAlbumID(), 300, 300,
                    GlobalSongList.GetInstance().GetCurSongDetails().getPath2());
        }
        if (bitmap == null) {
            bitmap = BitmapUtil.GetRandomBitmap(getResources(), GlobalSongList.GetInstance().GetCurSongDetails().getAlbumID(), 300, 300);
        }

        widgetView.setImageViewBitmap(R.id.album_image, bitmap);

        widgetView.setImageViewResource(R.id.bPlay, GlobalSongList.GetInstance().boolMusicPlaying1 == true ? R.drawable.pause2 : R.drawable.play);

        ComponentName myWidget = new ComponentName(getApplicationContext(), MyWidget.class);
        AppWidgetManager manager = AppWidgetManager.getInstance(getApplicationContext());
        int[] appWidgetIds = manager.getAppWidgetIds(myWidget);
        if (appWidgetIds != null && appWidgetIds.length > 0) {
            manager.updateAppWidget(appWidgetIds, widgetView);
        }
    }

    private void cancelNotification() {
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);
        mNotificationManager.cancel(NOTIFICATION_ID);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCompletion(MediaPlayer mp2) {
        stopMedia();
        if (GlobalSongList.GetInstance().GetNowPlayingSize() - 1 != GlobalSongList.GetInstance().getPosition()
                && GlobalSongList.GetInstance().GetNowPlayingSize() > 0) {
            if (GlobalSongList.GetInstance().GetRepeatMode() == GlobalSongList.REPEAT_NONE
                    || GlobalSongList.GetInstance().GetRepeatMode() == GlobalSongList.REPEAT_ALL) {
                GlobalSongList.GetInstance().increasePosition();
            }
        } else if (GlobalSongList.GetInstance().GetNowPlayingSize() - 1 == GlobalSongList.GetInstance().getPosition()
                && GlobalSongList.GetInstance().GetNowPlayingSize() > 0) {
            if (GlobalSongList.GetInstance().GetRepeatMode() == GlobalSongList.REPEAT_NONE) {
                GlobalSongList.GetInstance().boolMusicPlaying1 = false;
                return;
            } else if (GlobalSongList.GetInstance().GetRepeatMode() == GlobalSongList.REPEAT_ONCE) {
            } else if (GlobalSongList.GetInstance().GetRepeatMode() == GlobalSongList.REPEAT_ALL) {
                GlobalSongList.GetInstance().setPosition(0);
            }
        }

        GlobalSongList.GetInstance().boolMusicPlaying1 = true;
        // nextsongcoverIntent.putExtra("newcover", 1);
        // sendBroadcast(nextsongcoverIntent);
        settingsource();
        initNotification();
        mWakeLock.acquire(30000);
        // mMediaplayerHandler.sendEmptyMessage(TRACK_ENDED);
        mMediaplayerHandler.sendEmptyMessage(RELEASE_WAKELOCK);
    }

    public void settingsource() {
        if (GlobalSongList.GetInstance().GetNowPlayingSize() > 0) {
            try {
                mp.setDataSource(this, Uri.parse(GlobalSongList.GetInstance().GetCurSongDetails().getPath2()));

                // TODO
                Intent BROADCAST_EQ = new Intent(IConstant.BROADCAST_EQ);
                sendBroadcast(BROADCAST_EQ);
                nextsongcoverIntent.putExtra("setDataSourceFailed", false);
                mp.prepare();
                mp.start();
                mIsInitialized = true;
                GlobalSongList.GetInstance().boolMusicPlaying1 = true;
                sendBroadcast(nextsongcoverIntent);
                updateWidgetService = new Intent(getApplicationContext(), UpdateWidgetService.class);
                startService(updateWidgetService);
                notifyChange();
                initializeEqualisers();
            } catch (IndexOutOfBoundsException e) {
            } catch (Exception e) {
                e.printStackTrace();
                initMediaPlayer();
                nextsongcoverIntent.putExtra("setDataSourceFailed", true);
                sendBroadcast(nextsongcoverIntent);
                GlobalSongList.GetInstance().RemoveCurSongDetails();
                settingsource();
            }
        } else {
            stopSelf();
        }
    }

    private void SetVolume(float vol, float vol2) {
        try {
            mp.setVolume(vol, vol2);
        } catch (IllegalStateException e) {
        }
    }

    public void stopMedia() {
        try {
            if (mp.isPlaying()) {
                mp.stop();
                GlobalSongList.GetInstance().boolMusicPlaying1 = false;
            }
            mp.reset();
        } catch (Exception e) {
        }
    }

    private void playMedia() {
        playMedia(true);
    }

    private void playMedia(boolean notifyChange) {
        int result = mAudioManager.requestAudioFocus(mAudioFocusListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
//		Logger.d( TAG, "playMedia..........................notifyChange = " + notifyChange + " result = " + result);
        // rem by quayca. What is it here? It makes audio lost focus on incomming calls, so it could not resume the audio on call end
        // if ( result != AudioManager.AUDIOFOCUS_REQUEST_GRANTED )
        // {
        // return;
        // }
        try {
            mp.start();
            GlobalSongList.GetInstance().boolMusicPlaying1 = true;
            mAudioManager.registerRemoteControlClient(mRemoteControlClient);
            initNotification();
            updateWidgetService = new Intent(getApplicationContext(), UpdateWidgetService.class);
            startService(updateWidgetService);
        } catch (Exception e) {
//			 Logger.d( TAG, "Starting 3333333333333 " + e.toString( ) );
        }
        if (notifyChange)
            notifyChange();
    }

    private void pauseMedia() {
        pauseMedia(true);
    }

    private void pauseMedia(boolean notifyChange) {
        try {
            if (mp != null && mp.isPlaying()) {
                mp.pause();
                GlobalSongList.GetInstance().boolMusicPlaying1 = false;
                initNotification();
                updateWidgetService = new Intent(getApplicationContext(), UpdateWidgetService.class);
                startService(updateWidgetService);
            }
        } catch (Exception e) {
        }
        if (notifyChange)
            notifyChange();
    }

    private void next() {
        if (GlobalSongList.GetInstance().getPosition() < GlobalSongList.GetInstance().GetNowPlayingSize() - 1) {
            GlobalSongList.GetInstance().boolMusicPlaying1 = true;
            GlobalSongList.GetInstance().increasePosition();
        } else if (GlobalSongList.GetInstance().getPosition() == GlobalSongList.GetInstance().GetNowPlayingSize() - 1
                && GlobalSongList.GetInstance().GetNowPlayingSize() > 0) {
            if (GlobalSongList.GetInstance().GetRepeatMode() == GlobalSongList.REPEAT_NONE) {
                GlobalSongList.GetInstance().boolMusicPlaying1 = false;
                return;
            } else if (GlobalSongList.GetInstance().GetRepeatMode() == GlobalSongList.REPEAT_ONCE) {
                GlobalSongList.GetInstance().boolMusicPlaying1 = false;
                return;
            } else if (GlobalSongList.GetInstance().GetRepeatMode() == GlobalSongList.REPEAT_ALL) {
                GlobalSongList.GetInstance().boolMusicPlaying1 = true;
                GlobalSongList.GetInstance().setPosition(0);
            }
        }

        stopMedia();
        if (GlobalSongList.GetInstance().GetNowPlayingSize() > 0) {
            settingsource();
            // Intent checkagain = new Intent(IConstant.BROADCAST_CHECK_AGAIN);
            // sendBroadcast(checkagain);
        }
    }

    private void prev() {
        if (GlobalSongList.GetInstance().getPosition() != 0) {
            GlobalSongList.GetInstance().decreasePosition();
        } else {
            if (GlobalSongList.GetInstance().GetRepeatMode() == GlobalSongList.REPEAT_NONE) {
            } else if (GlobalSongList.GetInstance().GetRepeatMode() == GlobalSongList.REPEAT_ONCE) {
            } else if (GlobalSongList.GetInstance().GetRepeatMode() == GlobalSongList.REPEAT_ALL
                    && GlobalSongList.GetInstance().GetNowPlayingSize() > 0) {
                GlobalSongList.GetInstance().setPosition(GlobalSongList.GetInstance().GetNowPlayingSize() - 1);
            }
        }

        stopMedia();
        if (GlobalSongList.GetInstance().GetNowPlayingSize() > 0) {
            settingsource();
        }
    }

    private boolean isPlaying() {
        return GlobalSongList.GetInstance().boolMusicPlaying1;
    }

    /**
     * Returns the current playback position in milliseconds
     */
    public long position() {
        if (mp != null && mIsInitialized) {
            return mp.getCurrentPosition();
        }
        return -1;
    }

    /**
     * Seeks to the position specified.
     *
     * @param pos The position to seek to, in milliseconds
     */
    public void seek(int pos) {
        if (mp != null && mIsInitialized) {
            if (pos < 0)
                pos = 0;
            if (pos > mp.getDuration())
                pos = mp.getDuration();
            mp.seekTo(pos);
        }
    }

    @Override
    public void onPrepared(MediaPlayer arg0) {
        playMedia();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        mIsInitialized = false;
        return false;
    }

    @Override
    public void onSeekComplete(MediaPlayer mp2) {
        if (!mp.isPlaying()) {
            playMedia();
        }
    }

    @Override
    public boolean onInfo(MediaPlayer arg0, int arg1, int arg2) {
        return false;
    }

    private void notifyChange() {
        // // Share this notification directly with our widgets
        // mAppWidgetProvider.notifyChange(Music_service.this);

        mRemoteControlClient.setPlaybackState(isPlaying() ? RemoteControlClient.PLAYSTATE_PLAYING : RemoteControlClient.PLAYSTATE_PAUSED);

        String TITLE = "";
        String ARTIST = "";
        String GENRE = "";
        if (GlobalSongList.GetInstance().GetNowPlayingSize() > 0) {
            TITLE = GlobalSongList.GetInstance().GetCurSongDetails().getSong();
            ARTIST = GlobalSongList.GetInstance().GetCurSongDetails().getArtist();
        } else {
            TITLE = getResources().getString(R.string.app_name);
        }

        RemoteControlClient.MetadataEditor metadataEditor = mRemoteControlClient.editMetadata(true);
        metadataEditor.putString(MediaMetadataRetriever.METADATA_KEY_TITLE, TITLE);
        metadataEditor.putString(MediaMetadataRetriever.METADATA_KEY_ARTIST, ARTIST);
        metadataEditor.putString(MediaMetadataRetriever.METADATA_KEY_ALBUMARTIST, ARTIST);
        metadataEditor.putString(MediaMetadataRetriever.METADATA_KEY_GENRE, GENRE);

        metadataEditor.apply();

        sendBroadcast(new Intent(IConstant.BROADCAST_CHECK_AGAIN));
    }

    private static final int TRACK_ENDED = 1;
    private static final int RELEASE_WAKELOCK = 2;
    private static final int SERVER_DIED = 3;
    private static final int FOCUSCHANGE = 4;
    private static final int FADEDOWN = 5;
    private static final int FADEUP = 6;

    private final Handler mMediaplayerHandler = new Handler() {
        float mCurrentVolume = 1.0f;

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case FADEDOWN:
                    mCurrentVolume -= .05f;
                    if (mCurrentVolume > .2f) {
                        mMediaplayerHandler.sendEmptyMessageDelayed(FADEDOWN, 10);
                    } else {
                        mCurrentVolume = .2f;
                    }
                    SetVolume(mCurrentVolume, mCurrentVolume);
                    break;
                case FADEUP:
                    mCurrentVolume += .01f;
                    if (mCurrentVolume < 1.0f) {
                        mMediaplayerHandler.sendEmptyMessageDelayed(FADEUP, 10);
                    } else {
                        mCurrentVolume = 1.0f;
                    }
                    SetVolume(mCurrentVolume, mCurrentVolume);
                    break;
                // case SERVER_DIED:
                // if (mIsSupposedToBePlaying)
                // {
                // next(true);
                // }
                // else
                // {
                // // the server died when we were idle, so just
                // // reopen the same song (it will start again
                // // from the beginning though when the user
                // // restarts)
                // openCurrent();
                // }
                // break;

                // case TRACK_ENDED:
                // stopMedia();
                // if (GlobalSongList.GetInstance().GetNowPlayingSize() - 1 != GlobalSongList.GetInstance().getPosition()
                // && GlobalSongList.GetInstance().GetNowPlayingSize() > 0)
                // {
                // if (GlobalSongList.GetInstance().repeatmode == GlobalSongList.REPEAT_NONE
                // || GlobalSongList.GetInstance().repeatmode == GlobalSongList.REPEAT_ALL)
                // {
                // GlobalSongList.GetInstance().increasePosition();
                // }
                // }
                // else if (GlobalSongList.GetInstance().GetNowPlayingSize() - 1 == GlobalSongList.GetInstance().getPosition()
                // && GlobalSongList.GetInstance().GetNowPlayingSize() > 0)
                // {
                // if (GlobalSongList.GetInstance().repeatmode == GlobalSongList.REPEAT_NONE)
                // {
                // GlobalSongList.GetInstance().boolMusicPlaying1 = false;
                // }
                // else if (GlobalSongList.GetInstance().repeatmode == GlobalSongList.REPEAT_ONCE)
                // {
                // }
                // else if (GlobalSongList.GetInstance().repeatmode == GlobalSongList.REPEAT_ALL)
                // {
                // GlobalSongList.GetInstance().setPosition(0);
                // }
                // }
                //
                // GlobalSongList.GetInstance().boolMusicPlaying1 = true;
                // // nextsongcoverIntent.putExtra("newcover", 1);
                // // sendBroadcast(nextsongcoverIntent);
                // settingsource();
                // initNotification();
                // break;

                case RELEASE_WAKELOCK:
                    mWakeLock.release();
                    break;

                case FOCUSCHANGE:
                    // This code is here so we can better synchronize it with the code that handles fade-in
                    switch (msg.arg1) {
                        case AudioManager.AUDIOFOCUS_LOSS:
                            //Logger.d( TAG, "AudioManager.AUDIOFOCUS_LOSS" );
                            mAudioManager.unregisterMediaButtonEventReceiver(
                                    new ComponentName(Music_service.this.getPackageName(), MediaButtonIntentReceiver.class.getName()));
                            mAudioManager.unregisterRemoteControlClient(mRemoteControlClient);
                            mAudioManager.abandonAudioFocus(mAudioFocusListener);
                            if (isPlaying()) {
                                mPausedByTransientLossOfFocus = false;
                            }
                            pauseMedia();
                            break;

                        case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                            //Logger.d( TAG, "AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK" );
                            mMediaplayerHandler.removeMessages(FADEUP);
                            mMediaplayerHandler.sendEmptyMessage(FADEDOWN);
                            break;

                        case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                            //Logger.d( TAG, "AudioManager.AUDIOFOCUS_LOSS_TRANSIENT" );
                            if (isPlaying()) {
                                mPausedByTransientLossOfFocus = true;
                            }
                            pauseMedia();
                            break;

                        case AudioManager.AUDIOFOCUS_GAIN:
                            //Logger.d( TAG, "AudioManager.AUDIOFOCUS_GAIN" );
                            mAudioManager.registerMediaButtonEventReceiver(
                                    new ComponentName(Music_service.this.getPackageName(), MediaButtonIntentReceiver.class.getName()));
                            mAudioManager.registerRemoteControlClient(mRemoteControlClient);

                            if (!isPlaying() && mPausedByTransientLossOfFocus) {
                                mPausedByTransientLossOfFocus = false;
                                SetVolume(mCurrentVolume, mCurrentVolume);
                                playMedia(); // also queues a fade-in
                            } else {
                                mMediaplayerHandler.removeMessages(FADEDOWN);
                                mMediaplayerHandler.sendEmptyMessage(FADEUP);
                            }
                            break;
                    }
                    break;
            }
        }
    };

    private final OnAudioFocusChangeListener mAudioFocusListener = new OnAudioFocusChangeListener() {
        public void onAudioFocusChange(int focusChange) {
            mMediaplayerHandler.obtainMessage(FOCUSCHANGE, focusChange, 0).sendToTarget();
        }
    };

    /**
     * Initialiser equaliser, bassbooster and Virtualiser
     * <p/>
     * This will be called only once or otherwise multiple instances will get created
     */
    public PlayMeePreferences prefs;
    public AudioManager maudiomanager;

    private void initializeEqualisers() {//prefs=new PlayMeePreferences(Music_service.this);

        try {
            Singleton.theEqualizer = new android.media.audiofx.Equalizer(1, mp.getAudioSessionId());
            Singleton.theEqualizer.setEnabled(prefs.IsEqualizerOn());
            Singleton.theEqualizer.usePreset((short) Singleton.getInstance().getCurPresetIndex(getApplicationContext()));

        } catch (Throwable ex) {
            ex.printStackTrace();
        }
        try {
            Singleton.theBooster = new android.media.audiofx.BassBoost(1, mp.getAudioSessionId());

        } catch (Throwable ex) {
            ex.printStackTrace();
        }
        try {

            Singleton.theVirtualizer = new android.media.audiofx.Virtualizer(1, mp.getAudioSessionId());

        } catch (Throwable ex) {
            ex.printStackTrace();
        }
        try {
            maudiomanager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * Releases the Equaliser, Bass Booster and Virtualisers
     * <p/>
     * Only called once ate destroy so that equaliser may persist
     */
    private void releaseEqualizers() {

        if (Singleton.theVirtualizer != null) {
            try {
                Singleton.theVirtualizer.release();
            } catch (Throwable ex) {
                ex.printStackTrace();
            }
            Singleton.theVirtualizer = null;
        }
        if (Singleton.theBooster != null) {
            try {
                Singleton.theBooster.release();
            } catch (Throwable ex) {
                ex.printStackTrace();
            }
            Singleton.theBooster = null;
        }
        if (Singleton.theEqualizer != null) {
            try {
                Singleton.theEqualizer.release();
            } catch (Throwable ex) {
                ex.printStackTrace();
            }
            Singleton.theEqualizer = null;
        }

    }

}
