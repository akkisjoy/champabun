package champak.champabun.view.activity;

import android.content.Intent;
import android.os.Handler;

import champak.champabun.AmuzicgApp;
import champak.champabun.R;

public class Splash extends BaseActivity {
    final private static long TIME_DELAY = 3 * 1000;


    @Override
    public String GetActivityID() {
        return "Splash";
    }

    @Override
    public void OnBackPressed() {
        finish();
    }

    @Override
    protected String GetGAScreenName() {
        return "Splash";
    }

    @Override
    public int GetLayoutResID() {
        return R.layout.splash;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!(AmuzicgApp.secondtimeplayershown == 0))
            AmuzicgApp.secondtimeplayershown = 0;

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                startActivity(new Intent(Splash.this, MainActivity.class));
                finish();
            }
        }, TIME_DELAY);
    }

}
