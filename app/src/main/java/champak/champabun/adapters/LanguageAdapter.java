package champak.champabun.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import champak.champabun.R;
import champak.champabun.classes.ELanguage;
import champak.champabun.ui.TypefaceTextView;

public class LanguageAdapter extends BaseAdapter {
    private ELanguage[] languages;
    private Context context;

    public LanguageAdapter(Context context) {
        this.context = context;
        languages = ELanguage.values();
    }

    @Override
    public int getCount() {
        if (languages == null) {
            return 0;
        } else {
            return languages.length;
        }
    }

    @Override
    public Object getItem(int arg0) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.listview_item, parent, false);
            holder = new ViewHolder();
            holder.textview = (TypefaceTextView) convertView;
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.textview.setText(languages[position].getLanguageName());

        return convertView;
    }

    class ViewHolder {
        TypefaceTextView textview;
    }
}
