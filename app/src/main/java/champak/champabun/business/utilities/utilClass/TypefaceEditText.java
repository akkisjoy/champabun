/**
 * TypefaceEditText.java
 * <p/>
 * <br>
 * <b>Purpose</b> :
 * <p/>
 * <br>
 * <b>Optional info</b> :
 * <p/>
 * <br>
 * <b>author</b> : chquay@gmail.com
 * <p/>
 * <br>
 * <b>date</b> : Aug 31, 2014
 * <p/>
 * <br>
 * <b>lastChangedRevision</b> :
 * <p/>
 * <br>
 * <b>lastChangedDate</b> : Aug 31, 2014
 */
package champak.champabun.business.utilities.utilClass;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;

import java.util.HashMap;

import champak.champabun.R;

public class TypefaceEditText extends EditText {
    public TypefaceEditText(final Context context) {
        this(context, null, 0);
    }

    public TypefaceEditText(final Context context, final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TypefaceEditText(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);

        if (TypefaceTextView.mTypefaces == null) {
            TypefaceTextView.mTypefaces = new HashMap<String, Typeface>();
        }

        final TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.TypefaceTextView);
        if (array != null) {
            final String typefaceAssetPath = array.getString(R.styleable.TypefaceTextView_customTypeface);

            if (typefaceAssetPath != null) {
                Typeface typeface = null;

                if (TypefaceTextView.mTypefaces.containsKey(typefaceAssetPath)) {
                    typeface = TypefaceTextView.mTypefaces.get(typefaceAssetPath);
                } else {
                    AssetManager assets = context.getAssets();
                    typeface = Typeface.createFromAsset(assets, typefaceAssetPath);
                    TypefaceTextView.mTypefaces.put(typefaceAssetPath, typeface);
                }

                setTypeface(typeface);
            }
            array.recycle();
        }
    }

    @Override
    public void setError(CharSequence error) {
        if (error != null && error.length() > 0) {
            Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.shake);
            this.startAnimation(animation);

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    setError(null);
                }
            }, 4 * 1000);
        }
        super.setError(error);
    }
}
