package champak.champabun.driver.parser;

import org.json.JSONException;
import org.json.JSONObject;

import champak.champabun.business.dataclasses.AdsConfig;
import champak.champabun.business.dataclasses.AppConfig;
import champak.champabun.business.dataclasses.PlayMeeConfig;

public class JsonParser
{
	public JsonParser( )
	{
	}

	public long GetPlayMeeConfigConfigVersion( String json ) throws JSONException
	{
		long version = -1L;
		JSONObject jsonObj = new JSONObject( json );
		if ( jsonObj.has( "version" ) )
		{
			version = jsonObj.getLong( "version" );
		}
		return version;
	}

	public PlayMeeConfig GetPlayMeeConfigConfig( String json ) throws JSONException
	{
		long version = -1L;
		AppConfig appConfig = null;
		AdsConfig adsConfig = null;

		JSONObject jsonObj = new JSONObject( json );
		if ( jsonObj.has( "version" ) )
		{
			version = jsonObj.getLong( "version" );
		}
		if ( jsonObj.has( "app_config" ) )
		{
			JSONObject app_configObj = jsonObj.getJSONObject( "app_config" );
			boolean show_fb_ads_first = true;
			if ( app_configObj.has( "show_fb_ads_first" ) )
			{
				show_fb_ads_first = app_configObj.getInt( "show_fb_ads_first" ) == 1;
			}
			appConfig = new AppConfig( show_fb_ads_first );
		}
		if ( jsonObj.has( "ads_config" ) )
		{
			JSONObject ads_configObj = jsonObj.getJSONObject( "ads_config" );
			adsConfig = new AdsConfig( ads_configObj.getInt( "isShowFullScreenAds" ) == 1, ads_configObj.getInt( "isShowBanner" ) == 1,
					ads_configObj.getInt( "isShowFullScreenAdsAfterSplash" ) == 1 );
		}
		return new PlayMeeConfig( version, appConfig, adsConfig );
	}
}
