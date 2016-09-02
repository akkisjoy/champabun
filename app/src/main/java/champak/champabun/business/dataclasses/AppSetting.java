package champak.champabun.business.dataclasses;

public class AppSetting {
    private SleepTime sleepTimer;
    private int durationFilterTime;
    private String songSortKey;
    private String albumSortKey;
    private String artistSortKey;
    private String genreSortKey;
    private boolean autoDownloadAlbumArt;
    private boolean isAppFullscreen;

    public AppSetting() {
    }

    public SleepTime getSleepTimer() {
        return sleepTimer;
    }

    public void setSleepTimer(SleepTime sleepTimer) {
        this.sleepTimer = sleepTimer;
    }

    public int getDurationFilterTime() {
        return durationFilterTime;
    }

    public void setDurationFilterTime(int durationFilterTime) {
        this.durationFilterTime = durationFilterTime;
    }

    public String getAlbumSortKey() {
        return albumSortKey;
    }

    public void setAlbumSortKey(String albumSortKey) {
        this.albumSortKey = albumSortKey;
    }

    public String getArtistSortKey() {
        return artistSortKey;
    }

    public void setArtistSortKey(String artistSortKey) {
        this.artistSortKey = artistSortKey;
    }

    public String getSongSortKey() {
        return songSortKey;
    }

    public void setSongSortKey(String songSortKey) {
        this.songSortKey = songSortKey;
    }

    public String getGenreSortKey() {
        return genreSortKey;
    }

    public void setGenreSortKey(String genreSortKey) {
        this.genreSortKey = genreSortKey;
    }

    public boolean isAutoDownloadAlbumArt() {
        return autoDownloadAlbumArt;
    }

    public void setAutoDownloadAlbumArt(boolean autoDownloadAlbumArt) {
        this.autoDownloadAlbumArt = autoDownloadAlbumArt;
    }

    public boolean isAppFullscreen() {
        return isAppFullscreen;
    }

    public void setAppFullscreen(boolean isAppFullscreen) {
        this.isAppFullscreen = isAppFullscreen;
    }
}
