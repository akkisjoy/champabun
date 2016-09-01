package champak.champabun.business.dataclasses;

import android.os.Parcel;
import android.os.Parcelable;

public class AppConfig implements Parcelable {
    public static final Creator<AppConfig> CREATOR = new Creator<AppConfig>() {
        public AppConfig createFromParcel(Parcel in) {
            return new AppConfig(in);
        }

        public AppConfig[] newArray(int size) {
            return new AppConfig[size];
        }
    };

    private boolean show_fb_ads_first;

    public AppConfig(boolean show_fb_ads_first) {
        this.show_fb_ads_first = show_fb_ads_first;
    }

    public AppConfig(Parcel in) {
        this.show_fb_ads_first = in.readInt() == 1;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(show_fb_ads_first ? 1 : 0);
    }
}
