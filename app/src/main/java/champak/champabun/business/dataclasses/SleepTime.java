package champak.champabun.business.dataclasses;

public class SleepTime
{
	private int hour;
	private int minute;

	public SleepTime( int hour, int minute )
	{
		this.hour = hour;
		this.minute = minute;
	}

	public int getHour( )
	{
		return hour;
	}

	public void setHour( int hour )
	{
		this.hour = hour;
	}

	public int getMinute( )
	{
		return minute;
	}

	public void setMinute( int minute )
	{
		this.minute = minute;
	}
}