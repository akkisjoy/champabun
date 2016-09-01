package champak.champabun.business.dataclasses;

import android.os.Parcel;
import android.os.Parcelable;

public class PlayMeeConfig implements Parcelable {
    public static final Creator<PlayMeeConfig> CREATOR = new Creator<PlayMeeConfig>() {
        public PlayMeeConfig createFromParcel(Parcel in) {
            return new PlayMeeConfig(in);
        }

        public PlayMeeConfig[] newArray(int size) {
            return new PlayMeeConfig[size];
        }
    };

    private long version;
    private AppConfig appConfig;
    private AdsConfig adsConfig;

    public PlayMeeConfig(long version, AppConfig appConfig, AdsConfig adsConfig) {
        this.version = version;
        this.appConfig = appConfig;
        this.adsConfig = adsConfig;
    }

    public PlayMeeConfig(Parcel in) {
        this.version = in.readLong();
        this.appConfig = in.readParcelable(AppConfig.class.getClassLoader());
        this.adsConfig = in.readParcelable(AdsConfig.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flag) {
        dest.writeLong(version);
        dest.writeParcelable(appConfig, flag);
        dest.writeParcelable(adsConfig, flag);
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public AdsConfig getAdsConfig() {
        return adsConfig;
    }

}
