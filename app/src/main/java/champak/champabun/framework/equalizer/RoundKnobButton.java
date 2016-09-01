

package champak.champabun.framework.equalizer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import champak.champabun.R;

public class RoundKnobButton extends RelativeLayout implements GestureDetector.OnGestureListener {
    public int eventValue;
    public int percent;
    public float posDegrees;
    public int stator;
    RectF oval;
    int width;
    private Bitmap bmpRotorOff;
    private Bitmap bmpRotorOn;
    private GestureDetector gestureDetector;
    private ImageView ivRotor;
    private float mAngleDown;
    private float mAngleUp;
    private RoundKnobButtonListener m_listener;
    private int m_nHeight;
    private int m_nWidth;
    private Bitmap srcoff;
    private Bitmap srcon;

    public RoundKnobButton(Context context, int i, int j, int k, int l, int i1) {
        super(context);
        eventValue = 10;
        m_nWidth = 0;
        m_nHeight = 0;
        width = l;
        oval = new RectF();
        m_nWidth = l;
        m_nHeight = i1;
        srcon = BitmapFactory.decodeResource(context.getResources(), j);
        srcoff = BitmapFactory.decodeResource(context.getResources(), k);
        float f = (float) l / (float) srcon.getWidth();
        float f1 = (float) i1 / (float) srcon.getHeight();
        Matrix matrix = new Matrix();
        matrix.postScale(f, f1);
        bmpRotorOn = Bitmap.createBitmap(srcon, 0, 0, srcon.getWidth(), srcon.getHeight(), matrix, true);
        bmpRotorOff = Bitmap.createBitmap(srcoff, 0, 0, srcoff.getWidth(), srcoff.getHeight(), matrix, true);
        ivRotor = new ImageView(context);
        ivRotor.setImageBitmap(bmpRotorOff);
        LayoutParams lp_ivKnob = new LayoutParams(l, i1);
        addView(ivRotor, lp_ivKnob);
        gestureDetector = new GestureDetector(getContext(), this);
    }

    private float cartesianToPolar(float f, float f1) {
        return (float) (-Math.toDegrees(Math.atan2(f - 0.5F, f1 - 0.5F)));
    }

    public void SetListener(RoundKnobButtonListener roundknobbuttonlistener) {
        m_listener = roundknobbuttonlistener;
    }

    public boolean onDown(MotionEvent motionevent) {
        mAngleDown = cartesianToPolar(1.0F - motionevent.getX() / (float) getWidth(), 1.0F - motionevent.getY() / (float) getHeight());
        return true;
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    public boolean onFling(MotionEvent motionevent, MotionEvent motionevent1, float f, float f1) {
        return false;
    }

    public void onLongPress(MotionEvent motionevent) {
        if (motionevent.getAction() == 0) {
            ivRotor.setImageBitmap(bmpRotorOff);
        } else {
            ivRotor.setImageBitmap(bmpRotorOn);
        }
    }

    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
                            float distanceY) {
        if (e1.getAction() == (MotionEvent.ACTION_SCROLL)) {
            ivRotor.setImageBitmap(bmpRotorOn);
        } else {
            ivRotor.setImageBitmap(bmpRotorOff);
        }
        float x = e2.getX() / ((float) getWidth());
        float y = e2.getY() / ((float) getHeight());
        float rotDegrees = cartesianToPolar(1 - x, 1 - y);
        if (!Float.isNaN(rotDegrees)) {
            posDegrees = rotDegrees;
            if (rotDegrees < 0)
                posDegrees = 360 + rotDegrees;
            if (posDegrees > 210 || posDegrees < 150) {
                setRotorPosAngle(posDegrees);
                float scaleDegrees = rotDegrees + 150;
                percent = (int) (scaleDegrees / 3);
                if (percent >= 0 && percent < 5) {
                    stator = R.drawable.stator1;
                } else if (percent >= 5 && percent < 10) {
                    stator = R.drawable.stator2;
                } else if (percent >= 10 && percent < 15) {
                    stator = R.drawable.stator3;
                } else if (percent >= 15 && percent < 20) {
                    stator = R.drawable.stator4;
                } else if (percent >= 20 && percent < 24) {
                    stator = R.drawable.stator5;
                } else if (percent >= 24 && percent < 29) {
                    stator = R.drawable.stator6;
                } else if (percent >= 29 && percent < 34) {
                    stator = R.drawable.stator7;
                } else if (percent >= 34 && percent < 39) {
                    stator = R.drawable.stator8;
                } else if (percent >= 39 && percent < 44) {
                    stator = R.drawable.stator9;
                } else if (percent >= 44 && percent < 50) {
                    stator = R.drawable.stator10;
                } else if (percent >= 50 && percent < 54) {
                    stator = R.drawable.stator11;
                } else if (percent >= 54 && percent < 58) {
                    stator = R.drawable.stator12;
                } else if (percent >= 58 && percent < 64) {
                    stator = R.drawable.stator13;
                } else if (percent >= 64 && percent < 69) {
                    stator = R.drawable.stator14;
                } else if (percent >= 69 && percent < 74) {
                    stator = R.drawable.stator15;
                } else if (percent >= 74 && percent < 80) {
                    stator = R.drawable.stator16;
                } else if (percent >= 80 && percent < 84) {
                    stator = R.drawable.stator17;
                } else if (percent >= 84 && percent < 90) {
                    stator = R.drawable.stator18;
                } else if (percent >= 90 && percent < 95) {
                    stator = R.drawable.stator19;
                } else if (percent >= 95 && percent < 100) {
                    stator = R.drawable.stator20;
                }
                if (m_listener != null)
                    m_listener.onRotate(percent, stator);
                return true;
            } else
                return false;
        } else
            return false;
    }

    public void onShowPress(MotionEvent motionevent) {
    }

    public boolean onSingleTapUp(MotionEvent motionevent) {
        mAngleUp = cartesianToPolar(1.0F - motionevent.getX() / (float) getWidth(), 1.0F - motionevent.getY() / (float) getHeight());
        if (!Float.isNaN(mAngleDown) && !Float.isNaN(mAngleUp)) {
            Math.abs(mAngleUp - mAngleDown);
        }
        return true;
    }

    public boolean onTouchEvent(MotionEvent motionevent) {
        if (gestureDetector.onTouchEvent(motionevent)) {
            if (motionevent.getAction() == 0) {
                ivRotor.setImageBitmap(bmpRotorOn);
            } else {
                ivRotor.setImageBitmap(bmpRotorOff);
            }
            return true;
        } else {
            return super.onTouchEvent(motionevent);
        }
    }

    public void setRotorPercentage(int i) {
        int j = i * 3 - 150;
        i = j;
        if (j < 0) {
            i = j + 360;
        }
        setRotorPosAngle(i);
    }

    public void setRotorPosAngle(float f) {
        if (f >= 210F || f <= 150F) {
            float f1 = f;
            if (f > 180F) {
                f1 = f - 360F;
            }
            Matrix matrix = new Matrix();
            ivRotor.setScaleType(ImageView.ScaleType.MATRIX);
            matrix.postRotate(f1, m_nWidth / 2, m_nHeight / 2);
            ivRotor.setImageMatrix(matrix);
        }
    }

    public interface RoundKnobButtonListener {
        void onRotate(int i, int j);
    }
}
