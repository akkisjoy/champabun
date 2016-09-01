package champak.champabun.business.utilities.utilClass;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import java.util.Calendar;

import champak.champabun.business.utilities.utilMethod.Utilities;

public class MyTimePickerDialog extends TimePickerDialog
{
	private OnButtonClickListener listener;
	private String okString;
	private String cancelString;

	public MyTimePickerDialog( Calendar timeToShow, Context context, String okString, String cancelString, OnTimeSetListener callBack )
	{
		super( context, null, timeToShow.get( Calendar.HOUR_OF_DAY ), timeToShow.get( Calendar.MINUTE ), DateFormat.is24HourFormat( context ) );
		this.okString = okString;
		this.cancelString = cancelString;
		initializePicker( callBack );
	}

	public MyTimePickerDialog( Calendar timeToShow, Context context, int theme, OnTimeSetListener callBack )
	{
		super( context, theme, null, timeToShow.get( Calendar.HOUR_OF_DAY ), timeToShow.get( Calendar.MINUTE ), DateFormat.is24HourFormat( context ) );
		initializePicker( callBack );
	}

	private void initializePicker( final OnTimeSetListener callback )
	{
		try
		{
			// if you&#39;re only using Honeycomb+ then you can just call getTimePicker() instead of using reflection
			java.lang.reflect.Field pickerField = TimePickerDialog.class.getDeclaredField( "mTimePicker" );
			pickerField.setAccessible( true );
			final TimePicker picker = ( TimePicker ) pickerField.get( this );
			this.setCancelable( true );
			cancelString = Utilities.isEmpty( cancelString ) ? getContext( ).getText( android.R.string.cancel ).toString( ) : cancelString;
			this.setButton( DialogInterface.BUTTON_NEGATIVE, cancelString, new OnClickListener( ) {

				@Override
				public void onClick( DialogInterface dialog, int which )
				{
					if ( listener != null )
					{
						listener.OnCancel( );
					}
				}
			} );
			okString = Utilities.isEmpty( okString ) ? getContext( ).getText( android.R.string.ok ).toString( ) : okString;
			this.setButton( DialogInterface.BUTTON_POSITIVE, okString, new OnClickListener( ) {
				@Override
				public void onClick( DialogInterface dialog, int which )
				{
					if ( listener != null )
					{
						listener.OnSet( );
					}
					picker.clearFocus( ); // focus must be cleared so the value change listener is called
					callback.onTimeSet( picker, picker.getCurrentHour( ), picker.getCurrentMinute( ) );
				}
			} );
		}
		catch ( Exception e )
		{
			// reflection probably failed
		}
	}

	public void SetOnButtonClickListener( OnButtonClickListener listener )
	{
		this.listener = listener;
	}

	public interface OnButtonClickListener
	{
		void OnCancel();

		void OnSet();
	}
}
