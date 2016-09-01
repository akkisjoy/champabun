package champak.champabun.framework.service;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import champak.champabun.AmuzicgApp;
import champak.champabun.R;
import champak.champabun.business.definition.IConstant;
import champak.champabun.business.definition.Logger;

public class MyWidget extends AppWidgetProvider {
    public static final String CMDAPPWIDGETUPDATE = "appwidgetupdate";
    private static MyWidget sInstance;

    static synchronized MyWidget getInstance() {
        if (sInstance == null) {
            sInstance = new MyWidget();
        }
        return sInstance;
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Logger.d("MyWidget", "onUpdate.........................");
        defaultAppWidget(context, appWidgetIds);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        Logger.d("MyWidget", "onReceive.........................");
    }

    /**
     * Initialize given widgets to default state, where we launch Music on default click and hide actions if service not running.
     */
    private void defaultAppWidget(Context context, int[] appWidgetIds) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget);

        //remoteViews.setTextViewText(R.id.song, "");
        remoteViews.setTextViewText(R.id.artist, "");
        remoteViews.setImageViewResource(R.id.bPlay, R.drawable.play);

        linkButtons(context, remoteViews);
        pushUpdate(context, appWidgetIds, remoteViews);
    }

    private void pushUpdate(Context context, int[] appWidgetIds, RemoteViews views) {
        // Update specific list of appWidgetIds if given, otherwise default to all
        final AppWidgetManager gm = AppWidgetManager.getInstance(context);
        if (appWidgetIds != null) {
            gm.updateAppWidget(appWidgetIds, views);
        } else {
            gm.updateAppWidget(new ComponentName(context, this.getClass()), views);
        }
    }

    /**
     * Check against {@link AppWidgetManager} if there are any instances of this widget.
     */
    private boolean hasInstances(Context context) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, this.getClass()));
        return (appWidgetIds.length > 0);
    }

    /**
     * Handle a change notification coming over from {@link MediaPlaybackService}
     */
    void notifyChange(Music_service service) {
        Logger.d("MyWidget", "notifyChange.............");
        if (hasInstances(service)) {
            Logger.d("MyWidget", "notifyChange hasInstances.............");
            performUpdate(service, null);
        }
    }

    /**
     * Update all active widget instances by pushing changes
     */
    void performUpdate(Music_service service, int[] appWidgetIds) {
        RemoteViews remoteViews = new RemoteViews(service.getPackageName(), R.layout.widget);
        if (AmuzicgApp.GetInstance().GetNowPlayingSize() > 0) {
            String songTitle = AmuzicgApp.GetInstance().GetCurSongDetails().getSong();
            String artistTitle = AmuzicgApp.GetInstance().GetCurSongDetails().getArtist();
            remoteViews.setTextViewText(R.id.song, songTitle + " - " + artistTitle);
            remoteViews.setTextViewText(R.id.artist, artistTitle);

            if (AmuzicgApp.GetInstance().boolMusicPlaying1) {
                remoteViews.setImageViewResource(R.id.bPlay, R.drawable.pause2);
            } else {
                remoteViews.setImageViewResource(R.id.bPlay, R.drawable.play);
            }
        } else {
            //remoteViews.setTextViewText(R.id.song, "");
            remoteViews.setTextViewText(R.id.artist, "");
            remoteViews.setImageViewResource(R.id.bPlay, R.drawable.play);
        }

        // Link actions buttons to intents
        linkButtons(service, remoteViews);

        pushUpdate(service, appWidgetIds, remoteViews);
    }

    /**
     * Link up various button actions using pending intents.
     *
     * @param playerActive {@code true} if player is active in background
     */
    private void linkButtons(Context context, RemoteViews remoteViews) {
        Logger.d("MyWidget", "............ linkButton");
        Intent playIntent = new Intent(IConstant.BROADCAST_PLAYPAUSE);
        PendingIntent playPendingIntent = PendingIntent.getBroadcast(context, IConstant.REQUEST_CODE, playIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.bPlay, playPendingIntent);

        Intent previousIntent = new Intent(IConstant.BROADCAST_PREV);
        PendingIntent previousPendingIntent = PendingIntent.getBroadcast(context, IConstant.REQUEST_CODE, previousIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.bprev, previousPendingIntent);

        Intent nextIntent = new Intent(IConstant.BROADCAST_SWAP);
        nextIntent.putExtra("nextprev", 1);

        PendingIntent nextPendingIntent = PendingIntent.getBroadcast(context, 0, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.bNext, nextPendingIntent);

        Intent open_activityIntent = new Intent(IConstant.BROADCAST_OPEN_ACTIVITY);
        PendingIntent openactivityPendingIntent = PendingIntent.getBroadcast(context, IConstant.REQUEST_CODE, open_activityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.parent, openactivityPendingIntent);
    }

    // public BroadcastReceiver broadcastCoverReceiver = new BroadcastReceiver() {
    // @Override
    // public void onReceive(Context context, Intent serviceIntent)
    // {
    // AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context.getApplicationContext());
    // ComponentName thisWidget = new ComponentName(context.getApplicationContext(), MyWidget.class);
    // int [] appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
    // if (appWidgetIds != null && appWidgetIds.length > 0)
    // {
    // onUpdate(context, appWidgetManager, appWidgetIds);
    // }
    // }
    // };

    // public BroadcastReceiver broadcastPlayPause = new BroadcastReceiver() {
    // @Override
    // public void onReceive(Context context, Intent serviceIntent)
    // {
    // AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context.getApplicationContext());
    // ComponentName thisWidget = new ComponentName(context.getApplicationContext(), MyWidget.class);
    // int [] appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
    // if (appWidgetIds != null && appWidgetIds.length > 0)
    // {
    // onUpdate(context, appWidgetManager, appWidgetIds);
    // }
    // }
    //
    // };
    // public BroadcastReceiver checkagain = new BroadcastReceiver() {
    // @Override
    // public void onReceive(Context context, Intent serviceIntent)
    // {
    // AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context.getApplicationContext());
    // ComponentName thisWidget = new ComponentName(context.getApplicationContext(), MyWidget.class);
    // int [] appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
    // if (appWidgetIds != null && appWidgetIds.length > 0)
    // {
    // onUpdate(context, appWidgetManager, appWidgetIds);
    // }
    // }
    // };

    // public BroadcastReceiver stop = new BroadcastReceiver() {
    // @Override
    // public void onReceive(Context context, Intent serviceIntent)
    // {
    // Logger.d("MyWidget", "onReceive........................." + serviceIntent.getAction());
    // AmuzicgApp.GetInstance().CLearNowPlaying();
    // AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context.getApplicationContext());
    // ComponentName thisWidget = new ComponentName(context.getApplicationContext(), MyWidget.class);
    // int [] appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
    // if (appWidgetIds != null && appWidgetIds.length > 0)
    // {
    // onUpdate(context, appWidgetManager, appWidgetIds);
    // }
    // }
    // };
}