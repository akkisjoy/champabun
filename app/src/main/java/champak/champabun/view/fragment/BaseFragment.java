package champak.champabun.view.fragment;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;

import champak.champabun.business.definition.IConstant;
import champak.champabun.business.definition.Logger;

public abstract class BaseFragment extends Fragment {
    protected abstract String GetGAScreenName();

    @Override
    public void onDestroy() {
        super.onDestroy();
        MyGarbageCollection();
    }

    private void MyGarbageCollection() {
        try {
            SetNullForCustomVariable();
            UnbindReferences(getView());
        } catch (Exception e) {
            Logger.e("ERROR", "Unbind References" + e.toString());
        }
    }

    private void UnbindReferences(View view) {
        try {
            if (view != null) {
                if (view instanceof ViewGroup) {
                    UnbindViewGroupReferences((ViewGroup) view);
                }
            }
            if (IConstant.USE_SYSTEM_GC) {
                System.gc();
            }
        } catch (Throwable e) {
            // whatever exception is thrown just ignore it because a crash is always worse than this method not doing what it's supposed to do
        }
    }

    protected void UnbindViewGroupReferences(ViewGroup viewGroup) {
        if (viewGroup == null)
            return;

        int nrOfChildren = viewGroup.getChildCount();
        for (int i = 0; i < nrOfChildren; i++) {
            View view = viewGroup.getChildAt(i);
            UnbindViewReferences(view);
            if (view instanceof ViewGroup)
                UnbindViewGroupReferences((ViewGroup) view);
        }
        try {
            viewGroup.removeAllViews();
        } catch (Throwable mayHappen) {
            // AdapterViews, ListViews and potentially other ViewGroups don't support the removeAllViews operation
        }
    }

    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    private void UnbindViewReferences(View view) {
        if (view == null)
            return;
        // set all listeners to null (not every view and not every API level supports the methods)
        try {
            view.setOnClickListener(null);
        } catch (Throwable ignored) {
        }
        try {
            view.setOnCreateContextMenuListener(null);
        } catch (Throwable ignored) {
        }
        try {
            view.setOnFocusChangeListener(null);
        } catch (Throwable ignored) {
        }
        try {
            view.setOnKeyListener(null);
        } catch (Throwable ignored) {
        }
        try {
            view.setOnLongClickListener(null);
        } catch (Throwable ignored) {
        }
        try {
            view.setOnClickListener(null);
        } catch (Throwable ignored) {
        }

        // set background to null
        Drawable d = view.getBackground();
        if (d != null)
            d.setCallback(null);
        if (view instanceof ImageView) {
            ImageView imageView = (ImageView) view;
            d = imageView.getDrawable();
            if (d != null)
                d.setCallback(null);
            imageView.setImageDrawable(null);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                imageView.setBackgroundDrawable(null);
            } else {
                imageView.setBackground(null);
            }
        }

        // destroy webview
        if (view instanceof WebView) {
            view.destroyDrawingCache();
            ((WebView) view).destroy();
        }
    }

    public void SetNullForCustomVariable() {
    }

    public abstract void Update();
}
