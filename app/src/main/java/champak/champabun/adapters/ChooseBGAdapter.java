package champak.champabun.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import champak.champabun.R;
import champak.champabun.util.ActivityUtil;

public class ChooseBGAdapter extends BaseAdapter {
    private Context mContext;
    private int[] imageBG;
    private int width = 0;
    private int height = 0;

    public ChooseBGAdapter(Context context, int[] imageBG) {
        this.mContext = context;
        this.imageBG = imageBG;

        int sWidth = ActivityUtil.GetScreenWidth(context);
        int padding = context.getResources().getDimensionPixelSize(R.dimen.activity_horizontal_margin);
        int horizontalSpacing = context.getResources().getDimensionPixelSize(R.dimen.change_background_horizontal_spacing);
        int cols = context.getResources().getInteger(R.integer.change_background_gridview_columns);
        width = (sWidth - (padding << 1) - ((cols - 1) * horizontalSpacing)) / cols;
        height = width;
    }

    @Override
    public int getCount() {
        if (imageBG == null) {
            return 0;
        } else {
            return imageBG.length;
        }
    }

    @Override
    public Object getItem(int position) {
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
            holder = new ViewHolder();

            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.choose_bg_item, parent, false);
            holder.imageView = (ImageView) convertView;

            AbsListView.LayoutParams params = new AbsListView.LayoutParams(width, height);
            convertView.setLayoutParams(params);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.imageView.setImageResource(imageBG[position]);

        return convertView;
    }

    private class ViewHolder {
        ImageView imageView;
    }
}
