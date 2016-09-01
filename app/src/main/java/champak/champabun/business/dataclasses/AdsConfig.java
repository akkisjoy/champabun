package champak.champabun.business.dataclasses;

import android.os.Parcel;
import android.os.Parcelable;

public class AdsConfig implements Parcelable
{
	public static final Creator < AdsConfig > CREATOR = new Creator < AdsConfig >( ) {
		public AdsConfig createFromParcel( Parcel in )
		{
			return new AdsConfig( in );
		}

		public AdsConfig [ ] newArray( int size )
		{
			return new AdsConfig [ size ];
		}
	};

	private boolean isShowFullScreenAds = true;
	private boolean isShowBanner = true;
	private boolean isShowFullScreenAdsAfterSplash = isShowFullScreenAds && true;

	public AdsConfig( )
	{
	}

	public AdsConfig( boolean isShowFullScreenAds, boolean isShowBanner, boolean isShowFullScreenAdsAfterSplash )
	{
		this.isShowFullScreenAds = isShowFullScreenAds;
		this.isShowBanner = isShowBanner;
		this.isShowFullScreenAdsAfterSplash = isShowFullScreenAdsAfterSplash;
	}

	public AdsConfig( Parcel in )
	{
		this.isShowFullScreenAds = in.readInt( ) == 1;
		this.isShowBanner = in.readInt( ) == 1;
		this.isShowFullScreenAdsAfterSplash = in.readInt( ) == 1;
	}

	public boolean isShowFullScreenAds( )
	{
		return isShowFullScreenAds;
	}

	public void setShowFullScreenAds( boolean isShowFullScreenAds )
	{
		this.isShowFullScreenAds = isShowFullScreenAds;
	}

	public boolean isShowBanner( )
	{
		return isShowBanner;
	}

	public void setShowBanner( boolean isShowBanner )
	{
		this.isShowBanner = isShowBanner;
	}

	public boolean isShowFullScreenAdsAfterSplash( )
	{
		return isShowFullScreenAds && isShowFullScreenAdsAfterSplash;
	}

	public void setShowFullScreenAdsAfterSplash( boolean isShowFullScreenAdsAfterSplash )
	{
		this.isShowFullScreenAdsAfterSplash = isShowFullScreenAdsAfterSplash;
	}
	
	public boolean isAdsConfigOffer( )
	{
		return isShowBanner( ) || isShowFullScreenAds( );
	}

	@Override
	public int describeContents( )
	{
		return 0;
	}

	@Override
	public void writeToParcel( Parcel dest, int flag )
	{
		dest.writeInt( isShowFullScreenAds ? 1 : 0 );
		dest.writeInt( isShowBanner ? 1 : 0 );
		dest.writeInt( isShowFullScreenAdsAfterSplash ? 1 : 0 );
	}

}
