package champak.champabun.business.utilities.utilMethod;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import org.json.JSONException;
import org.xml.sax.SAXException;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import champak.champabun.business.dataclasses.PlayMeeConfig;
import champak.champabun.driver.parser.JsonParser;
import champak.champabun.driver.parser.ServiceHelper;

public class AppConfigHelper {
    private static final String URL_PLAYMEE_CONFIG = "http://playmee.org/goodstufflife/app_config.txt";

    private static final String APP_DB_KEY = "appcfg";
    private static final String KEY_CFG_JSON = "playmee_cfg_json";

    private static AppConfigHelper instance;
    private static PlayMeeConfig playMeeConfig;

    public static AppConfigHelper getInstance() {
        if (instance == null) {
            instance = new AppConfigHelper();
        }
        return instance;
    }

    public void SavePlayMeeConfigJson(Context context, String adsConfig) {
        SharedPreferences.Editor editor = context.getSharedPreferences(APP_DB_KEY, Context.MODE_PRIVATE).edit();
        editor.putString(KEY_CFG_JSON, adsConfig);
        editor.apply();
    }

    private PlayMeeConfig GetLocalPlayMeeConfig(Context context) {
        PlayMeeConfig playMeeConfig = null;
        String json = GetPlayMeeConfigFromPref(context);
        if (Utilities.IsEmpty(json)) {
            // get from asset
            json = Utilities.GetStringFromAssets(context, "cfg/app_config.txt");
        }
        JsonParser parser = new JsonParser();
        try {
            playMeeConfig = parser.GetPlayMeeConfigConfig(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return playMeeConfig;
    }

    private String GetPlayMeeConfigFromPref(Context context) {
        SharedPreferences pref = context.getSharedPreferences(APP_DB_KEY, Context.MODE_PRIVATE);
        return pref.getString(KEY_CFG_JSON, "");
    }

    public PlayMeeConfig GetPlayMeeConfig(Context context) {
        if (playMeeConfig == null) {
            playMeeConfig = GetLocalPlayMeeConfig(context);
        }
        return playMeeConfig;
    }

    public void CheckPlayMeeConfigUpdate(Context context, long version, OnGotPlayMeeConfigListener listener) {
        if (ActivityUtil.IsNetworkAvailable(context)) {
            new MyTask(context, version, listener).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void) null);
        }
    }

    public interface OnGotPlayMeeConfigListener {
        void OnGotPlayMeeConfig(PlayMeeConfig playMeeConfig);
    }

    class MyTask extends AsyncTask<Void, Void, PlayMeeConfig> {
        Context context;
        long version;
        OnGotPlayMeeConfigListener listener;

        public MyTask(Context ctx, long _version, OnGotPlayMeeConfigListener lis) {
            context = ctx;
            version = _version;
            listener = lis;
        }

        @Override
        protected PlayMeeConfig doInBackground(Void... arg0) {
            PlayMeeConfig config = null;
            try {
                String cfgJson = ServiceHelper.DoRequest(URL_PLAYMEE_CONFIG, null, null);
                JsonParser parser = new JsonParser();
                long new_version = parser.GetPlayMeeConfigConfigVersion(cfgJson);
                if (version < new_version) {
                    config = parser.GetPlayMeeConfigConfig(cfgJson);
                    SavePlayMeeConfigJson(context, cfgJson);
                }
            } catch (IOException | ParserConfigurationException | SAXException | JSONException e) {
                e.printStackTrace();
            }
            return config;
        }

        @Override
        protected void onPostExecute(PlayMeeConfig result) {
            super.onPostExecute(result);
            if (result != null) {
                if (listener != null) {
                    listener.OnGotPlayMeeConfig(result);
                }
            }
        }
    }
}
