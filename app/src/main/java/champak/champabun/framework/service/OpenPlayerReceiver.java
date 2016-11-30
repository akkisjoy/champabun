package champak.champabun.framework.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;

import champak.champabun.AmuzicgApp;
import champak.champabun.business.dataclasses.SongDetails;
import champak.champabun.business.definition.IConstant;
import champak.champabun.business.definition.Logger;
import champak.champabun.business.utilities.utilMethod.ActivityUtil;
import champak.champabun.business.utilities.utilMethod.AmuzePreferences;
import champak.champabun.business.utilities.utilMethod.SongListUtil;
import champak.champabun.business.utilities.utilMethod.Utilities;
import champak.champabun.view.activity.Player;

public class OpenPlayerReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Logger.d("OpenPlayerReceiver", "onReceive...................action = " + action);
        if (IConstant.BROADCAST_OPEN_ACTIVITY.equals(action) || IConstant.BROADCAST_PLAYPAUSE.equals(action)
                || IConstant.BROADCAST_PREV.equals(action) || IConstant.BROADCAST_SWAP.equals(action)) {
            Intent intent2 = new Intent(context, Player.class);
            ArrayList<SongDetails> play = null;
            int index = 0;
            AmuzePreferences prefs = new AmuzePreferences(context);

            if (AmuzicgApp.GetInstance().GetNowPlayingSize() <= 0) {
                SongListUtil util = new SongListUtil(context);
                String click_no = prefs.GetLastplayClickNo();
                if (!Utilities.isEmpty(click_no)) {
                    play = util.GetSonglistByClickNo(click_no);
                    intent2.putExtra("fromRecentAdded", false);
                } else {
                    if (prefs.HasLastPlayed()) {
                        boolean isFromRecentAdded = prefs.IsFromRecentAdded();
                        if (isFromRecentAdded) {
                            play = util.GetRecentAdded();
                        } else {
                            play = util.FetchLastPlayed();
                        }
                        intent2.putExtra("fromRecentAdded", isFromRecentAdded);
                    }
                }
                if (play != null && play.size() > 0) {
                    index = prefs.GetLastPlaylistIndex();
                    intent2.putExtra("Data1", play);
                    intent2.putExtra("click_no_playlist", click_no);
                }
                AmuzicgApp.GetInstance().setCheck(0);
            } else {
                AmuzicgApp.GetInstance().setCheck(1);
            }

            if (IConstant.BROADCAST_OPEN_ACTIVITY.equals(action)) {
                intent2.putExtra("Data2", index);
                intent2.putExtra("comefrom", "Music_service");
                intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent2);
            } else {
                if (ActivityUtil.IsMyServiceRunning(context, Music_service.class)) {
                    return;
                }
                if (IConstant.BROADCAST_PREV.equals(intent.getAction())) {
                    if (play != null && play.size() > 0) {
                        if (index > 0) {
                            index--;
                        } else if (prefs.GetRepeatMode() == AmuzicgApp.REPEAT_ALL) {
                            index = play.size() - 1;
                        }
                    }
                } else if (IConstant.BROADCAST_SWAP.equals(intent.getAction())) {
                    if (play != null && play.size() > 0) {
                        if (index < play.size() - 1) {
                            index++;
                        } else if (prefs.GetRepeatMode() == AmuzicgApp.REPEAT_ALL) {
                            index = 0;
                        }
                    }
                } else if (IConstant.BROADCAST_PLAYPAUSE.equals(intent.getAction())) {
                    //Nothing to do yet
                }

                if (AmuzicgApp.GetInstance().getCheck() == 0) {
                    AmuzicgApp.GetInstance().SetNowPlayingList(play);
                    AmuzicgApp.GetInstance().setPosition(index);
                }
                AmuzicgApp.GetInstance().boolMusicPlaying1 = true;
                AmuzicgApp.GetInstance().setCheck(1);
                Intent serviceIntent = new Intent(context, Music_service.class);
                context.startService(serviceIntent);
            }
        }
    }
}
