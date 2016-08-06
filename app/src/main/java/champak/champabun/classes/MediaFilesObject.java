package champak.champabun.classes;

import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;

public class MediaFilesObject implements Parcelable
{
	public static final Creator < MediaFilesObject > CREATOR = new Creator < MediaFilesObject >() {
		public MediaFilesObject createFromParcel(Parcel in)
		{
			return new MediaFilesObject(in);
		}

		public MediaFilesObject [] newArray(int size)
		{
			return new MediaFilesObject [ size ];
		}
	};

	private String folderName;
	private String path;
	private String parentPath;
	private ArrayList < MediaFilesObject > subMediaFiles = new ArrayList < MediaFilesObject >();
	private SongDetails songdetails;// is it is last child, it will be songdetails
	private int totalSongs;

	public MediaFilesObject(String folderName, String path, String parentPath, SongDetails songdetails)
	{
		this.folderName = folderName;
		this.path = path;
		this.setParentPath(parentPath);
		this.songdetails = songdetails;
	}

	public MediaFilesObject(String folderName, String path, String parentPath, ArrayList < MediaFilesObject > subMediaFiles, int totalSongs)
	{
		this.folderName = folderName;
		this.path = path;
		this.setParentPath(parentPath);
		this.subMediaFiles = subMediaFiles;
		this.totalSongs = totalSongs;
	}

	public MediaFilesObject(Parcel in)
	{
		folderName = in.readString();
		path = in.readString();
		parentPath = in.readString();
		int subMediaFilesSize = in.readInt();
		if (subMediaFilesSize > 0)
		{
			subMediaFiles = new ArrayList < MediaFilesObject >();
			in.readTypedList(subMediaFiles, MediaFilesObject.CREATOR);
		}
		songdetails = in.readParcelable(SongDetails.class.getClassLoader());
		totalSongs = in.readInt();
	}

	@Override
	public int describeContents()
	{
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags)
	{
		dest.writeString(folderName);
		dest.writeString(path);
		dest.writeString(parentPath);
		if (subMediaFiles != null && subMediaFiles.size() > 0)
		{
			dest.writeInt(subMediaFiles.size());
			dest.writeTypedList(subMediaFiles);
		}
		else
		{
			dest.writeInt(0);
		}
		dest.writeParcelable(songdetails, flags);
		dest.writeInt(totalSongs);
	}

	public String getFolderName()
	{
		return folderName;
	}

	public void setFolderName(String folderName)
	{
		this.folderName = folderName;
	}

	public String getPath()
	{
		return path;
	}

	public String getParentPath()
	{
		return parentPath;
	}

	public void setParentPath(String parentPath)
	{
		this.parentPath = parentPath;
	}

	public void setPath(String path)
	{
		this.path = path;
	}

	public ArrayList < MediaFilesObject > getSubMediaFiles()
	{
		return subMediaFiles;
	}

	public void addSubMediaFiles(MediaFilesObject subMediaFiles)
	{
		if (this.subMediaFiles == null)
		{
			this.subMediaFiles = new ArrayList < MediaFilesObject >();
		}
		this.subMediaFiles.add(subMediaFiles);
	}

	public void setSubMediaFiles(ArrayList < MediaFilesObject > subMediaFiles)
	{
		this.subMediaFiles = subMediaFiles;
	}

	public SongDetails getSongdetails()
	{
		return songdetails;
	}

	public void setSongdetails(SongDetails songdetails)
	{
		this.songdetails = songdetails;
	}

	public boolean isSongdetails()
	{
		return this.songdetails != null;
	}

	public int getTotalSongs()
	{
		return totalSongs;
	}

	public void setTotalSongs(int totalSongs)
	{
		this.totalSongs = totalSongs;
	}

	public void encreaseTotalSongs()
	{
		this.totalSongs ++;
	}
}
