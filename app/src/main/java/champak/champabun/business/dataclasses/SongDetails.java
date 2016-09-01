package champak.champabun.business.dataclasses;

import android.os.Parcel;
import android.os.Parcelable;

public class SongDetails implements Parcelable
{
	public static final Creator<SongDetails> CREATOR = new Creator<SongDetails>() {
		public SongDetails createFromParcel(Parcel in) {
			return new SongDetails(in);
		}

		public SongDetails[] newArray(int size) {
			return new SongDetails[size];
		}
	};
	// Bitmap icon;
	private int _ID;
	private String click_no;
	private String song;
	private String Artist;
	private String Album;
	private String Path;
	private String Time;
	private String sortBy;
	private int albumID;
	private String audioID;

	public SongDetails(int _ID, String click_no, String Artist, String song)
	{
		this(_ID, click_no, Artist, song, null);
	}

	public SongDetails(int _ID, String click_no, String Artist, String song, String Path)
	{
		this(_ID, click_no, null, Artist, Path, null, song, null, -1, Path);
	}

	public SongDetails(int _ID, String click_no, String Album, String Artist, String Path, String Time, String song, String sortBy, int albumID)
	{
		this(_ID, click_no, Album, Artist, Path, Time, song, sortBy, albumID, null);
	}

	public SongDetails(int _ID, String click_no, String Album, String Artist, String Path, String Time, String song, String sortBy, int albumID,
			String audioID)
	{
		this._ID = _ID;
		this.click_no = click_no;
		this.Album = Album;
		this.Artist = Artist;
		this.Path = Path;
		this.Time = Time;
		this.song = song;
		this.sortBy = sortBy;
		this.albumID = albumID;
		this.audioID = audioID;
	}

	public SongDetails()
	{
	}

	public SongDetails(Parcel in)
	{
		this._ID = in.readInt();
		this.Path = in.readString();
		this.song = in.readString();
		this.Album = in.readString();
		this.Artist = in.readString();
		this.Time = in.readString();
		this.sortBy = in.readString();
		this.click_no = in.readString();
		this.albumID = Integer.parseInt(in.readString());
		this.audioID = in.readString();
	}

	public int getID()
	{
		return _ID;
	}

	public void setID(int _ID)
	{
		this._ID = _ID;
	}

	public String getClick_no()
	{
		return click_no;
	}

	public void setClick_no(String click_no)
	{
		this.click_no = click_no;
	}

	public String getsortBy()
	{
		return sortBy;
	}

	public void setsortBy(String sortBy)
	{
		this.sortBy = sortBy;
	}

	public String getSong()
	{
		return song;
	}

	public void setSong(String song)
	{
		this.song = song;
	}

	public String getTime()
	{
		return Time;
	}

	public void setTime(String Time)
	{
		this.Time = Time;
	}

	public String getArtist()
	{
		return Artist;
	}

	public void setArtist(String Artist)
	{
		this.Artist = Artist;
	}

	public String getPath2()
	{
		return Path;
	}

	public void setPath2(String Path)
	{
		this.Path = Path;
	}

	public String getAlbum()
	{
		return Album;
	}

	public void setAlbum(String Album)
	{
		this.Album = Album;
	}

	public int getAlbumID()
	{
		return albumID;
	}

	public void setAlbumID(int albumID)
	{
		this.albumID = albumID;
	}

	public String getAudioID()
	{
		return audioID;
	}

	public void setAudioID(String audioID)
	{
		this.audioID = audioID;
	}

	// public void setIcon(int icLauncher) {
	// this.icLauncher = icLauncher;
	// }
	@Override
	public int describeContents()
	{
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags)
	{
		dest.writeInt(_ID);
		dest.writeString(Path);
		dest.writeString(song);
		dest.writeString(Album);
		dest.writeString(Artist);
		dest.writeString(Time);
		dest.writeString(sortBy);
		dest.writeString(click_no);
		dest.writeString(String.valueOf(albumID));
		dest.writeString(audioID);
	}
}