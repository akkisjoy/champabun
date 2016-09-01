package champak.champabun.business.utilities.utilMethod;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

public class MusicLibraryScanner {

    private static Cursor mediaCursor;
    private static Cursor genresCursor;

    private static String[] mediaProjection = {
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.TITLE
    };
    private static String[] genresProjection = {
            MediaStore.Audio.Genres.NAME,
            MediaStore.Audio.Genres._ID
    };

    public static void getMusicFromStorage(Context context,String musicid) {

        mediaCursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                mediaProjection, null, null, null);

        int artist_column_index = mediaCursor
                .getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST);
        int album_column_index = mediaCursor
                .getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM);
        int title_column_index = mediaCursor
                .getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE);
        int id_column_index = mediaCursor
                .getColumnIndexOrThrow(MediaStore.Audio.Media._ID);

        if (mediaCursor.moveToFirst()) {
             
                String info = "Song " + mediaCursor.getString(title_column_index) + " ";
                info += "from album " + mediaCursor.getString(album_column_index) + " ";
                info += "by " + mediaCursor.getString(artist_column_index) + ". ";

                int musicId = Integer.parseInt(musicid);

                Uri uri = MediaStore.Audio.Genres.getContentUriForAudioId("external", musicId);
                genresCursor = context.getContentResolver().query(uri,
                        genresProjection, null, null, null);
                int genre_column_index = genresCursor.getColumnIndexOrThrow(MediaStore.Audio.Genres.NAME);                

                if (genresCursor.moveToFirst()) {
                    info += "Genres: ";
                    do {
                        info += genresCursor.getString(genre_column_index) + " ";
                    } while (genresCursor.moveToNext());
                }

                Log.e("Audio scanner", "Song info: " + info);
             
        }
    }
}