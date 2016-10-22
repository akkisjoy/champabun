package champak.champabun.business.utilities.utilClass;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by axay on 09-03-2016.
 */
public class TextViewOstrich extends TextView {

    private Context c;

    public TextViewOstrich(Context c) {
        super(c);
        this.c = c;
        setTypeFace(c);
    }

    public TextViewOstrich(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.c = context;
        setTypeFace(c);
    }

    public TextViewOstrich(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.c = context;
        setTypeFace(c);

    }

    private void setTypeFace(Context c) {
        Typeface tfs = Typeface.createFromAsset(c.getAssets(),
                "fonts/blackostrich.otf");
        setTypeface(tfs);
    }
}
