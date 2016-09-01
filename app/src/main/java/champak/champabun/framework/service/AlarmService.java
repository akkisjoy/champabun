package champak.champabun.framework.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import champak.champabun.business.definition.IConstant;
import champak.champabun.business.utilities.utilMethod.ActivityUtil;

public class AlarmService extends BroadcastReceiver
{
	@Override
	public void onReceive( Context context, Intent intent )
	{
		if ( IConstant.ACTION_SLEEP_TIMER.equals( intent.getAction( ) ) )
		{
			if ( ActivityUtil.IsMyServiceRunning( context, Music_service.class ) )
			{
				context.sendBroadcast( new Intent( IConstant.BROADCAST_PLAYPAUSE ) );
			}
		}
	}
}
