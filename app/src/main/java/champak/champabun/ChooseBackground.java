package champak.champabun;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import champak.champabun.adapters.ChooseBGAdapter;
import champak.champabun.classes.AppDatabase;

public class ChooseBackground extends BaseActivity {
    private GridView mGridView;
    private ChooseBGAdapter adapter;
    private int[] imageBG;
    private boolean needRefresh = false;

    @Override
    public String GetActivityID() {
        return "ChooseBackground";
    }

    @Override
    public void OnBackPressed() {
        setResult(needRefresh ? RESULT_OK : RESULT_CANCELED, getIntent());
        finish();
    }

    @Override
    protected String GetGAScreenName() {
        return "ChooseBackground";
    }

    @Override
    public int GetLayoutResID() {
        return R.layout.choose_background;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            needRefresh = savedInstanceState.getBoolean("needRefresh");
        }
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);

        mGridView = (GridView) findViewById(R.id.gridview);
        mGridView.setOnItemClickListener(itemClick);
        //imageBG = new int [ ] { R.drawable.a1, R.drawable.a2, R.drawable.a3, R.drawable.a4, R.drawable.a5, R.drawable.a6, R.drawable.a7,
        //	R.drawable.a8 };
        imageBG = IConstant.arr;
        adapter = new ChooseBGAdapter(ChooseBackground.this, imageBG);
        mGridView.setAdapter(adapter);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("needRefresh", needRefresh);
        super.onSaveInstanceState(outState);
    }

    private OnItemClickListener itemClick = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            int oldResID = appSettings.getAppBGResID();
            if (oldResID != imageBG[position]) {
                appSettings.setAppBGResID(imageBG[position]);
                AppDatabase.SaveAppBGResID(ChooseBackground.this, imageBG[position]);
                Intent intent = getIntent();
                intent.putExtra("bgResID", imageBG[position]);
                needRefresh = true;
            }
            OnBackPressed();
        }
    };
}
