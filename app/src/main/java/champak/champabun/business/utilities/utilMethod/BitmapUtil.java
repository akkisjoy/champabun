package champak.champabun.business.utilities.utilMethod;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaMetadataRetriever;

import com.commit451.nativestackblur.NativeStackBlur;

import java.io.File;

import champak.champabun.AmuzicgApp;
import champak.champabun.R;
import champak.champabun.business.definition.IConstant;
import champak.champabun.iloader.FileCache;

public class BitmapUtil {
    public static Bitmap SetAlbumArtBG(Context c, String image_path, String name, String artist) {
        Bitmap b = BitmapFactory.decodeFile(image_path);
        int wt_px = (int) c.getResources().getDimension(R.dimen.player_image_width);
        if (b != null) {

            b = Bitmap.createScaledBitmap(b, wt_px, wt_px, true);
        } else {
            b = BitmapUtil.fetchfilefromcahce(name, artist, c.getApplicationContext());
            if (b == null) {
                b = BitmapFactory.decodeResource(c.getResources(), R.drawable.default_art);
            }
            b = Bitmap.createScaledBitmap(b, wt_px, wt_px, true);
        }

        return b;
    }

    public static Bitmap fetchfilefromcahce(String album, String artist, Context c) {
        FileCache fileCache = new FileCache(c);
        artist = artist.trim();

        album = album.trim();
        File f = fileCache.getFile(artist + "_" + album);
        if (!f.exists()) {
            return null;
        }
        Bitmap b = BitmapFactory.decodeFile(f.getAbsolutePath());
        return b;
    }

    public static Bitmap GetBitmapFromSongPath(int desireWidth, int desireHeight, String image_path) {
        Bitmap bitmap = null;
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        try {
            mmr.setDataSource(AmuzicgApp.GetInstance().GetCurSongDetails().getPath2());
            byte[] rawArt = mmr.getEmbeddedPicture();
            if (rawArt != null) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                options.inSampleSize = 2;
                bitmap = BitmapFactory.decodeByteArray(rawArt, 0, rawArt.length, options);
                bitmap = Bitmap.createScaledBitmap(bitmap, desireWidth, desireHeight, true);
            } else {
                bitmap = BitmapFactory.decodeFile(image_path);
                if (bitmap != null) {
                    bitmap = Bitmap.createScaledBitmap(bitmap, desireWidth, desireHeight, true);
                }
            }
        } catch (Exception ignored) {
        }
        mmr.release();
        return bitmap;
    }

    public static BitmapDrawable SetBG(Bitmap b, Context context) {
        Bitmap bm = NativeStackBlur.process(b, 20);
//        b.recycle();
        BitmapDrawable d = new BitmapDrawable(context.getResources(), bm);
        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation((float) 0.84);
        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
        d.setColorFilter(filter);
        if (IConstant.OPTIMIZE_MEM_RECYCLE_BITMAP) {
            b.recycle();
        }
        return d;

    }

    public static Bitmap GetRandomBitmap(Resources res, int albumID, int desireWidth, int desireHeight) {
        int x = R.drawable.default_art;
        Bitmap bitmap = BitmapFactory.decodeResource(res, x);

        return Bitmap.createScaledBitmap(bitmap, desireWidth, desireHeight, true);
    }
}
