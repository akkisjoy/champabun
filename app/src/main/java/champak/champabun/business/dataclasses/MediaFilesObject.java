package champak.champabun.business.dataclasses;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class MediaFilesObject implements Parcelable {
    public static final Creator<MediaFilesObject> CREATOR = new Creator<MediaFilesObject>() {
        public MediaFilesObject createFromParcel(Parcel in) {
            return new MediaFilesObject(in);
        }

        public MediaFilesObject[] newArray(int size) {
            return new MediaFilesObject[size];
        }
    };

    private String folderName;
    private String path;
    private String parentPath;
    private ArrayList<MediaFilesObject> subMediaFiles = new ArrayList<>();
    private SongDetails songdetails;// is it is last child, it will be songdetails
    private int totalSongs;

    public MediaFilesObject(String folderName, String path, String parentPath, SongDetails songdetails) {
        this.folderName = folderName;
        this.path = path;
        this.setParentPath(parentPath);
        this.songdetails = songdetails;
    }

    public MediaFilesObject(Parcel in) {
        folderName = in.readString();
        path = in.readString();
        parentPath = in.readString();
        int subMediaFilesSize = in.readInt();
        if (subMediaFilesSize > 0) {
            subMediaFiles = new ArrayList<>();
            in.readTypedList(subMediaFiles, MediaFilesObject.CREATOR);
        }
        songdetails = in.readParcelable(SongDetails.class.getClassLoader());
        totalSongs = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(folderName);
        dest.writeString(path);
        dest.writeString(parentPath);
        if (subMediaFiles != null && subMediaFiles.size() > 0) {
            dest.writeInt(subMediaFiles.size());
            dest.writeTypedList(subMediaFiles);
        } else {
            dest.writeInt(0);
        }
        dest.writeParcelable(songdetails, flags);
        dest.writeInt(totalSongs);
    }

    public String getFolderName() {
        return folderName;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getParentPath() {
        return parentPath;
    }

    public void setParentPath(String parentPath) {
        this.parentPath = parentPath;
    }

    public ArrayList<MediaFilesObject> getSubMediaFiles() {
        return subMediaFiles;
    }

    public void addSubMediaFiles(MediaFilesObject subMediaFiles) {
        if (this.subMediaFiles == null) {
            this.subMediaFiles = new ArrayList<>();
        }
        this.subMediaFiles.add(subMediaFiles);
    }

    public SongDetails getSongdetails() {
        return songdetails;
    }

    public boolean isSongdetails() {
        return this.songdetails != null;
    }

    public void setSongdetails(SongDetails songdetails) {
        this.songdetails = songdetails;
    }

    public int getTotalSongs() {
        return totalSongs;
    }

    public void setTotalSongs(int totalSongs) {
        this.totalSongs = totalSongs;
    }

}
