package champak.champabun.business.utilities.utilClass;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import com.romainpiel.shimmer.ShimmerTextView;

/**
 * Created by axay on 09-03-2016.
 */
public class TextViewLobster extends ShimmerTextView {

    private Context c;

    public TextViewLobster(Context c) {
        super(c);
        this.c = c;
        setTypeFace(c);
    }

    public TextViewLobster(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.c = context;
        setTypeFace(c);
    }

    public TextViewLobster(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.c = context;
        setTypeFace(c);

    }

    private void setTypeFace(Context c) {
        Typeface tfs = Typeface.createFromAsset(c.getAssets(),
                "fonts/lobster.otf");
        setTypeface(tfs);
    }
}
