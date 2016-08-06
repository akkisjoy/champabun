package champak.champabun.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import champak.champabun.R;

public class EqualizerSpinnerAdapter extends ArrayAdapter<String> {

    public EqualizerSpinnerAdapter(Context context, int view, List<String> objects) {
        super(context, view, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return initView(position, convertView);
    }

    @Override
    public View getDropDownView(int position, View convertView,
                                ViewGroup parent) {
        return initView(position, convertView);
    }

    private View initView(int position, View convertView) {
        if (convertView == null)
            convertView = View.inflate(getContext(),
                    R.layout.simplerow,
                    null);
        TextView tvText1 = (TextView) convertView.findViewById(R.id.rowTextView);
        tvText1.setText(getItem(position));

        return convertView;
    }
}

   

