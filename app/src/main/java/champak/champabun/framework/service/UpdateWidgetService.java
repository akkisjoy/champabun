package champak.champabun.framework.service;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.RemoteViews;

import champak.champabun.AmuzicgApp;
import champak.champabun.R;
import champak.champabun.business.definition.IConstant;
import champak.champabun.business.definition.Logger;

public class UpdateWidgetService extends Service {
    final public static int ACTION_NORMAL = 0;
    final public static int ACTION_STOP = 1;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int action = ACTION_NORMAL;
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            action = bundle.getInt("action", ACTION_NORMAL);
        }
        if (action == ACTION_STOP) {
            AmuzicgApp.GetInstance().CLearNowPlaying();
        }
        Logger.d("MyWidget", "onStartCommand......................... action = " + action);

        RemoteViews controlButtons = new RemoteViews(getPackageName(), R.layout.widget);

        Intent playIntent = new Intent(IConstant.BROADCAST_PLAYPAUSE);

        PendingIntent playPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), IConstant.REQUEST_CODE, playIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        controlButtons.setOnClickPendingIntent(R.id.bPlay, playPendingIntent);

        Intent previousIntent = new Intent(IConstant.BROADCAST_PREV);
        PendingIntent previousPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), IConstant.REQUEST_CODE, previousIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        controlButtons.setOnClickPendingIntent(R.id.bprev, previousPendingIntent);

        Logger.d("MyWidget", "............ size = " + AmuzicgApp.GetInstance().GetNowPlayingSize());
        if (AmuzicgApp.GetInstance().GetNowPlayingSize() > 0) {
            String songTitle = AmuzicgApp.GetInstance().GetCurSongDetails().getSong();
            String artistTitle = AmuzicgApp.GetInstance().GetCurSongDetails().getArtist();
            controlButtons.setTextViewText(R.id.song, songTitle + " - " + artistTitle);
            controlButtons.setTextViewText(R.id.artist, artistTitle);

            Intent open_activityIntent = new Intent(IConstant.BROADCAST_OPEN_ACTIVITY);
            PendingIntent openactivityPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), IConstant.REQUEST_CODE,
                    open_activityIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            controlButtons.setOnClickPendingIntent(R.id.parent, openactivityPendingIntent);

            if (AmuzicgApp.GetInstance().boolMusicPlaying1 == true) {
                controlButtons.setImageViewResource(R.id.bPlay, R.drawable.pause2);
            } else {
                controlButtons.setImageViewResource(R.id.bPlay, R.drawable.play);
            }
        } else {
            //controlButtons.setTextViewText(R.id.song, "");
            controlButtons.setTextViewText(R.id.artist, "");
            controlButtons.setImageViewResource(R.id.bPlay, R.drawable.play);
        }

        Intent nextIntent = new Intent(IConstant.BROADCAST_SWAP);
        nextIntent.putExtra("nextprev", 1);

        PendingIntent nextPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        controlButtons.setOnClickPendingIntent(R.id.bNext, nextPendingIntent);

        ComponentName myWidget = new ComponentName(getApplicationContext(), MyWidget.class);
        AppWidgetManager manager = AppWidgetManager.getInstance(getApplicationContext());
        int[] appWidgetIds = manager.getAppWidgetIds(myWidget);
        if (appWidgetIds != null && appWidgetIds.length > 0) {
            manager.updateAppWidget(appWidgetIds, controlButtons);
        }

        stopSelf();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
