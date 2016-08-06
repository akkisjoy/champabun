package champak.champabun.parser;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.HttpsURLConnection;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import android.annotation.SuppressLint;
import android.util.Log;
import champak.champabun.util.Utilities;

@SuppressLint( "DefaultLocale" )
public class ServiceHelper
{
	final private static String CONTENT_TYPE = "text/xml; charset=utf-8";
	final private static String USER_AGENT = "Android/MusicPlayer";
	final private static String CHARSET = "UTF-8";
	final private static String ACCEPT_CHARSET = CHARSET;
	final private static String ACCEPT_ENCODING = "gzip";
	final private static int CONNECTION_TIMEOUT = 15 * 1000;

	public static String DoRequest( String URL, byte [ ] data, String method ) throws IOException, ParserConfigurationException, SAXException
	{
		String str = null;
		HttpURLConnection mHttpUrlConnection = null;
		try
		{
			URL u = new URL( URL );
			if ( u.getProtocol( ).toLowerCase( ).equals( "https" ) )
			{
				HttpsURLConnection https = ( HttpsURLConnection ) u.openConnection( );
				mHttpUrlConnection = https;
			}
			else
			{
				mHttpUrlConnection = ( HttpURLConnection ) u.openConnection( );
			}

			mHttpUrlConnection.setRequestProperty( "Content-Type", CONTENT_TYPE );
			mHttpUrlConnection.setRequestProperty( "User-Agent", USER_AGENT + "(Android-" + android.os.Build.MODEL + "-"
					+ android.os.Build.MANUFACTURER + "-" + android.os.Build.PRODUCT + ")" );
			mHttpUrlConnection.setRequestProperty( "Accept-Charset", ACCEPT_CHARSET );
			mHttpUrlConnection.setRequestProperty( "Accept-Encoding", ACCEPT_ENCODING );
			mHttpUrlConnection.setConnectTimeout( CONNECTION_TIMEOUT );

			if ( data != null )
			{
				mHttpUrlConnection.setRequestMethod( "POST" );
				mHttpUrlConnection.setUseCaches( false );
				mHttpUrlConnection.setDoInput( true );
				mHttpUrlConnection.setDoOutput( true );

				OutputStream out = mHttpUrlConnection.getOutputStream( );
				out.write( data );
				out.flush( );
				out.close( );
			}
			else
			{
				mHttpUrlConnection.setRequestMethod( "GET" );
				mHttpUrlConnection.setDoInput( true );
				mHttpUrlConnection.setDoOutput( false );
			}

			mHttpUrlConnection.connect( );

			// int httpCode = mHttpUrlConnection.getResponseCode( );
			InputStream in = new BufferedInputStream( mHttpUrlConnection.getInputStream( ) );
			 Log.d( "ServiceHelper", "in = " + in );
			if ( in != null )
			{
				if ( "gzip".equals( mHttpUrlConnection.getContentEncoding( ) ) )
				{
					in = new GZIPInputStream( in );
				}
				if ( Utilities.IsEmpty( method ) )
				{
					str = Utilities.GetStringFromInputStream( in );
				}
			}
		}
		finally
		{
			if ( mHttpUrlConnection != null )
			{
				mHttpUrlConnection.disconnect( );
			}
		}
		return str;
	}
}
