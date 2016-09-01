package champak.champabun.business.utilities.utilMethod;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.util.ArrayList;

import champak.champabun.AmuzicgApp;
import champak.champabun.business.dataclasses.SongDetails;

public class SongListUtil {
    private Context context;

    public SongListUtil(Context context) {
        this.context = context;
    }

    public ArrayList<SongDetails> GetRecentAdded() {
        ArrayList<SongDetails> play = null;
        Cursor songCursor = null;
        int duration = AmuzicgApp.GetInstance().getAppSettings().getDurationFilterTime();
        try {
            String sortOrder = MediaStore.Audio.Media.DATE_ADDED + " COLLATE NOCASE " + " DESC";
            String[] TRACK_COLUMNS = new String[]{MediaStore.Audio.AudioColumns.ALBUM, MediaStore.Audio.AudioColumns.ARTIST,
                    MediaStore.MediaColumns.DATA, MediaStore.Audio.AudioColumns.DURATION, MediaStore.MediaColumns.TITLE, MediaStore.Audio.Media._ID,
                    MediaStore.Audio.Media.DATE_ADDED, MediaStore.MediaColumns._ID};
            songCursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, TRACK_COLUMNS,
                    MediaStore.Audio.AudioColumns.DURATION + ">=" + duration, null, sortOrder);
            if (songCursor != null && songCursor.moveToFirst()) {
                play = new ArrayList<>();

                int i = 0;
                int _ID_index = songCursor.getColumnIndex(MediaStore.MediaColumns._ID);
                int ALBUM_index = songCursor.getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM);
                int ARTIST_index = songCursor.getColumnIndex(MediaStore.Audio.AudioColumns.ARTIST);
                int DATA_index = songCursor.getColumnIndex(MediaStore.MediaColumns.DATA);
                int DURATION_index = songCursor.getColumnIndex(MediaStore.Audio.AudioColumns.DURATION);
                int TITLE_index = songCursor.getColumnIndex(MediaStore.MediaColumns.TITLE);
                do {
                    play.add(new SongDetails(songCursor.getInt(_ID_index), null, songCursor.getString(ALBUM_index), songCursor
                            .getString(ARTIST_index), songCursor.getString(DATA_index),
                            Utilities.getTime(songCursor.getString(DURATION_index)), songCursor.getString(TITLE_index), songCursor
                            .getString(ARTIST_index), 0));
                    i = i + 1;
                    if (i == 301) {
                        break;
                    }
                }
                while (songCursor.moveToNext());

            }
        } catch (IllegalStateException e) {
        } finally {
            if (songCursor != null) {
                songCursor.close();
            }
        }

        return play;
    }

    public ArrayList<SongDetails> FetchLastPlayed() {
        ArrayList<SongDetails> play = null;
        Cursor songCursor = null;
        PlayMeePreferences prefs = new PlayMeePreferences(context);
        String[] in = prefs.GetLastPlaylist();
        if (in == null) {
            return null;
        }

        StringBuilder sb = new StringBuilder(in.length * 2 - 1);
        sb.append("?");
        for (int i = 1; i < in.length; i++) {
            sb.append(",?");
        }
        String strIn = sb.toString();

        StringBuilder inStr = new StringBuilder(MediaStore.MediaColumns._ID)
                .append(" IN (?");

        StringBuilder orderStr = new StringBuilder("CASE ")
                .append(MediaStore.MediaColumns._ID)
                .append(" WHEN ")
                .append(in[0])
                .append(" THEN 0 ");

        for (int i = 1; i < in.length; i++) {
            inStr.append(",?");

            orderStr.append("WHEN ")
                    .append(in[i])
                    .append(" THEN ")
                    .append(i)
                    .append(" ");
        }

        inStr.append(")");

        orderStr.append("END; ")
                .append(MediaStore.MediaColumns._ID)
                .append(" ASC");

        int duration = AmuzicgApp.GetInstance().getAppSettings().getDurationFilterTime();

        try {
            String[] TRACK_COLUMNS = new String[]{MediaStore.Audio.AudioColumns.ALBUM, MediaStore.Audio.AudioColumns.ARTIST,
                    MediaStore.MediaColumns.DATA, MediaStore.Audio.AudioColumns.DURATION, MediaStore.MediaColumns.TITLE, MediaStore.MediaColumns._ID};

            songCursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    TRACK_COLUMNS,
                    inStr.toString() + " AND ( " + MediaStore.Audio.AudioColumns.DURATION + ">=" + duration + ")",
                    in,
                    orderStr.toString());


            if (songCursor != null && songCursor.moveToFirst()) {
                play = new ArrayList<>();

                int _ID_index = songCursor.getColumnIndex(MediaStore.MediaColumns._ID);
                int ALBUM_index = songCursor.getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM);
                int ARTIST_index = songCursor.getColumnIndex(MediaStore.Audio.AudioColumns.ARTIST);
                int DATA_index = songCursor.getColumnIndex(MediaStore.MediaColumns.DATA);
                int DURATION_index = songCursor.getColumnIndex(MediaStore.Audio.AudioColumns.DURATION);
                int TITLE_index = songCursor.getColumnIndex(MediaStore.MediaColumns.TITLE);
                do {
                    play.add(new SongDetails(songCursor.getInt(_ID_index), null, songCursor.getString(ALBUM_index), songCursor
                            .getString(ARTIST_index), songCursor.getString(DATA_index),
                            Utilities.getTime(songCursor.getString(DURATION_index)), songCursor.getString(TITLE_index), songCursor
                            .getString(ALBUM_index), 0));
                }
                while (songCursor.moveToNext());

            }
        } catch (IllegalStateException e) {
        } finally {
            if (songCursor != null) {
                songCursor.close();
            }
        }
        return play;
    }

    public ArrayList<SongDetails> GetSonglistByClickNo(String click_no) {
        int s;
        try {
            s = Integer.parseInt(click_no);
        } catch (NumberFormatException e) {
            return null;
        }
        ArrayList<SongDetails> _play = null;
        int duration = AmuzicgApp.GetInstance().getAppSettings().getDurationFilterTime();
        Uri uri2 = MediaStore.Audio.Playlists.Members.getContentUri("external", s);

        String[] projection1 = {MediaStore.Audio.Playlists.Members.AUDIO_ID, MediaStore.Audio.Playlists.Members.ARTIST,
                MediaStore.Audio.Playlists.Members.TITLE, MediaStore.Audio.Playlists.Members.DATA, MediaStore.Audio.Playlists.Members.ALBUM,
                MediaStore.Audio.Playlists.Members.DURATION, MediaStore.MediaColumns._ID};
        Cursor cursor = null;

        try {
            cursor = context.getContentResolver().query(uri2, projection1, MediaStore.Audio.Playlists.Members.DURATION + ">=" + duration, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                _play = new ArrayList<>();

                int _ID_index = cursor.getColumnIndex(MediaStore.MediaColumns._ID);
                int ALBUM_index = cursor.getColumnIndex(MediaStore.Audio.Playlists.Members.ALBUM);
                int ARTIST_index = cursor.getColumnIndex(MediaStore.Audio.Playlists.Members.ARTIST);
                int DATA_index = cursor.getColumnIndex(MediaStore.Audio.Playlists.Members.DATA);
                int DURATION_index = cursor.getColumnIndex(MediaStore.Audio.Playlists.Members.DURATION);
                int TITLE_index = cursor.getColumnIndex(MediaStore.Audio.Playlists.Members.TITLE);
                int AUDIO_ID_index = cursor.getColumnIndex(MediaStore.Audio.Playlists.Members.AUDIO_ID);

                do {
                    SongDetails pl = new SongDetails();

                    pl.setID(cursor.getInt(_ID_index));
                    pl.setSong(cursor.getString(TITLE_index));
                    pl.setPath2(cursor.getString(DATA_index));
                    pl.setArtist(cursor.getString(ARTIST_index));
                    pl.setAlbum(cursor.getString(ALBUM_index));
                    pl.setTime(cursor.getString(DURATION_index));
                    pl.setAudioID(cursor.getString(AUDIO_ID_index));
                    try {
                        int intTime = Integer.parseInt(cursor.getString(DURATION_index));
                        int newTime = intTime / 1000;
                        int newTimeMinutes = newTime / 60;
                        int newTimeSeconds = newTime % 60;
                        String max2;
                        if (newTimeSeconds < 10) {
                            max2 = newTimeMinutes + ":0" + newTimeSeconds;
                        } else {
                            max2 = newTimeMinutes + ":" + newTimeSeconds;
                        }
                        pl.setTime(max2);
                    } catch (Exception ignored) {
                    }

                    _play.add(pl);
                }
                while (cursor.moveToNext());
            }
        } catch (IllegalStateException ignored) {
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return _play;
    }
}
