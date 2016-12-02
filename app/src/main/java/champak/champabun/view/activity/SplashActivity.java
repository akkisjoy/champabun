package champak.champabun.view.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import champak.champabun.AmuzicgApp;
import champak.champabun.R;

public class SplashActivity extends BaseActivity implements ActivityCompat.OnRequestPermissionsResultCallback {
    final private static long TIME_DELAY = 3 * 1000;

    private ImageView blurred;

    @Override
    public String GetActivityID() {
        return "SplashActivity";
    }

    @Override
    public void OnBackPressed() {
        finish();
    }

    @Override
    protected String GetGAScreenName() {
        return "SplashActivity";
    }

    @Override
    public int GetLayoutResID() {
        return R.layout.splash;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        blurred = (ImageView) findViewById(R.id.blurred);
        blurred.setColorFilter(ContextCompat.getColor(SplashActivity.this, R.color.pinkPager), PorterDuff.Mode.MULTIPLY);

        if (checkPermissionForExternalStorage()) {
            callActivity();
        }
    }

    private void callActivity() {
        if (!(AmuzicgApp.secondtimeplayershown == 0))
            AmuzicgApp.secondtimeplayershown = 0;

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                Animation animZoomIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom_in);
                ImageView logo = (ImageView) findViewById(R.id.logo);
                logo.startAnimation(animZoomIn);
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        startActivity(new Intent(SplashActivity.this, MainActivity.class));
                        overridePendingTransition(0, 0);
                        finish();
                    }
                }, 2000);
            }
        }, 3000);


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.v("permission", "Permission: " + permissions[0] + "was " + grantResults[0]);
            //resume tasks needing this permission
            callActivity();
        } else {
            Toast.makeText(SplashActivity.this, "You must allow permission to use this application", Toast.LENGTH_LONG).show();
        }
    }

    public boolean checkPermissionForExternalStorage() {
        if (Build.VERSION.SDK_INT >= 23) {
            int result = ContextCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (result == PackageManager.PERMISSION_GRANTED) {
                Log.v("permission", "Permission is granted");
                return true;
            } else {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(SplashActivity.this);

                dialogBuilder.setTitle("Alert");
                dialogBuilder.setMessage("Please allow storage permission on next screen to use Amuzicg player.");
                dialogBuilder.setPositiveButton("Got it", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.v("permission", "Permission is revoked");
                        ActivityCompat.requestPermissions(SplashActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                    }
                });
                AlertDialog alertDialog = dialogBuilder.create();
                alertDialog.show();

                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v("permission", "Permission is granted");
            return true;
        }
    }

    public void requestPermissionForCamera() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(SplashActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(SplashActivity.this, "SD Card permission needed. Please allow in App Settings for using Amuzicg player.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(SplashActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }
    }
}
