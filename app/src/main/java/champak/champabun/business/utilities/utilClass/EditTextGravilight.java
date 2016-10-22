package champak.champabun.business.utilities.utilClass;

import android.content.Context;
import android.graphics.Typeface;
import android.support.design.widget.TextInputEditText;
import android.util.AttributeSet;

/**
 * Created by axay on 09-03-2016.
 */
public class EditTextGravilight extends TextInputEditText {

    private Context c;

    public EditTextGravilight(Context c) {
        super(c);
        this.c = c;
        setTypeFace(c);
    }

    public EditTextGravilight(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.c = context;
        setTypeFace(c);
    }

    public EditTextGravilight(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.c = context;
        setTypeFace(c);
    }

    private void setTypeFace(Context c) {
        Typeface tfs = Typeface.createFromAsset(c.getAssets(),
                "fonts/graviolight.otf");
        setTypeface(tfs);
    }
}
