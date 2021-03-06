package champak.champabun.business.utilities.utilClass;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import com.romainpiel.shimmer.ShimmerTextView;

/**
 * Created by axay on 09-03-2016.
 */
public class TextViewGravilar extends ShimmerTextView {

    private Context c;

    public TextViewGravilar(Context c) {
        super(c);
        this.c = c;
        setTypeFace(c);
    }

    public TextViewGravilar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.c = context;
        setTypeFace(c);
    }

    public TextViewGravilar(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.c = context;
        setTypeFace(c);

    }

    private void setTypeFace(Context c) {
        Typeface tfs = Typeface.createFromAsset(c.getAssets(),
                "fonts/graviolar.otf");
        setTypeface(tfs);
    }
}
