package champak.champabun.framework.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;

import champak.champabun.business.definition.IConstant;

public class MediaButtonIntentReceiver extends BroadcastReceiver {
    private static long mLastClickTime = 0;
    private static boolean mDown = false;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_MEDIA_BUTTON.equals(intent.getAction())) {
            KeyEvent event = intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);

            if (event == null) {
                return;
            }

            int keycode = event.getKeyCode();
            int action = event.getAction();
            long eventtime = event.getEventTime();

            // single quick press: pause/resume.
            // double press: next track

            String command = null;
            switch (keycode) {
                case KeyEvent.KEYCODE_MEDIA_PLAY:
                    command = IConstant.CMDPLAY;
                    break;
                case KeyEvent.KEYCODE_MEDIA_PAUSE:
                    command = IConstant.CMDPAUSE;
                    break;
                case KeyEvent.KEYCODE_MEDIA_STOP:
                    command = IConstant.CMDSTOP;
                    break;
                case KeyEvent.KEYCODE_HEADSETHOOK:
                case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                    command = IConstant.CMDTOGGLEPAUSE;
                    break;
                case KeyEvent.KEYCODE_MEDIA_NEXT:
                    command = IConstant.CMDNEXT;
                    break;
                case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                    command = IConstant.CMDPREVIOUS;
                    break;
            }

            if (command != null) {
                if (action == KeyEvent.ACTION_DOWN) {
                    if (!mDown) {
                        // if this isn't a repeat event. The service may or may not be running, but we need to send it a command.
                        Intent i = new Intent(context, Music_service.class);
                        i.setAction(IConstant.SERVICECMD);
                        i.putExtra("callfrom", "MediaButtonIntentReceiver");
                        if (keycode == KeyEvent.KEYCODE_HEADSETHOOK && eventtime - mLastClickTime < 300) {
                            i.putExtra(IConstant.CMDNAME, IConstant.CMDNEXT);
                            context.startService(i);
                            mLastClickTime = 0;
                        } else {
                            i.putExtra(IConstant.CMDNAME, command);
                            context.startService(i);
                            mLastClickTime = eventtime;
                        }

                        mDown = true;
                    }
                } else {
                    mDown = false;
                }
                if (isOrderedBroadcast()) {
                    abortBroadcast();
                }
            }
        }
    }
}
