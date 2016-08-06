package champak.champabun.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewParent;
import android.widget.SeekBar;

public class VerticalSeekBar extends SeekBar
{
	private OnSeekBarChangeListener changeListener;
	private int x, y, z, w;

	public VerticalSeekBar(Context context)
	{
		super(context);
	}

	public VerticalSeekBar(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	}

	public VerticalSeekBar(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	protected void onSizeChanged(int w, int h, int oldw, int oldh)
	{
		super.onSizeChanged(h, w, oldh, oldw);

		this.x = w;
		this.y = h;
		this.z = oldw;
		this.w = oldh;
	}

	@Override
	protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		super.onMeasure(heightMeasureSpec, widthMeasureSpec);
		setMeasuredDimension(getMeasuredHeight(), getMeasuredWidth());
	}

	protected void onDraw(Canvas c)
	{
		c.rotate(-90);
		c.translate(-getHeight(), 0);

		super.onDraw(c);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		if (!isEnabled())
		{
			return false;
		}

		switch (event.getAction())
		{
			case MotionEvent.ACTION_DOWN:
				setSelected(true);
				setPressed(true);
				if (changeListener != null)
					changeListener.onStartTrackingTouch(this);
				break;
			case MotionEvent.ACTION_UP:
				setSelected(false);
				setPressed(false);
				if (changeListener != null)
					changeListener.onStopTrackingTouch(this);
				break;
			case MotionEvent.ACTION_MOVE:
			{
				ViewParent viewParent = getParent();
				if (viewParent != null)
				{
					viewParent.requestDisallowInterceptTouchEvent(true);
				}
				int progress = getMax() - (int) (getMax() * event.getY() / getHeight());
				setProgress(progress);
				onSizeChanged(getWidth(), getHeight(), 0, 0);
				if (changeListener != null)
					changeListener.onProgressChanged(this, progress, true);
				break;
			}
			case MotionEvent.ACTION_CANCEL:
				break;
		}
		return true;
	}

	@Override
	public synchronized void setOnSeekBarChangeListener(OnSeekBarChangeListener listener)
	{
		changeListener = listener;
	}

	@Override
	public synchronized void setProgress(int progress)
	{
		if (progress >= 0)
			super.setProgress(progress);
		else
			super.setProgress(0);

		onSizeChanged(x, y, z, w);

		if (changeListener != null)
			changeListener.onProgressChanged(this, progress, false);
	}
}