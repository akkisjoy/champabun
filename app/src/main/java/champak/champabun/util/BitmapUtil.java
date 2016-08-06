package champak.champabun.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaMetadataRetriever;

import com.enrique.stackblur.StackBlurManager;

import java.io.File;
import java.util.Random;

import champak.champabun.GlobalSongList;
import champak.champabun.IConstant;
import champak.champabun.R;
import champak.champabun.albums.lazylist.FileCache;

public class BitmapUtil {
    public static Bitmap SetAlbumArtBG(Context c, String image_path, String name, String artist) {
        Bitmap b = BitmapFactory.decodeFile(image_path);
        int wt_px = (int) c.getResources().getDimension(R.dimen.player_image_width);
        if (b != null) {

            b = Bitmap.createScaledBitmap(b, (int) wt_px, (int) wt_px, true);
        } else {
            b = BitmapUtil.fetchfilefromcahce(name, artist, c.getApplicationContext());
            if (b == null) {
                b = BitmapFactory.decodeResource(c.getResources(), IConstant.arr[new Random().nextInt(IConstant.arr.length - 1)]);
            }
            b = Bitmap.createScaledBitmap(b, (int) wt_px, (int) wt_px, true);
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

    // albumname and artistname are not required,you can remove it.
    public static Bitmap GetBitmapFromSongPath(Resources res, int albumID, int desireWidth, int desireHeight, String image_path) {
        Bitmap bitmap = null;
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        try {
            mmr.setDataSource(GlobalSongList.GetInstance().GetCurSongDetails().getPath2());
            byte[] rawArt = mmr.getEmbeddedPicture();
            if (rawArt != null) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_4444;
                options.inSampleSize = 2;
                bitmap = BitmapFactory.decodeByteArray(rawArt, 0, rawArt.length, options);
                bitmap = Bitmap.createScaledBitmap(bitmap, desireWidth, desireHeight, true);
            } else {
                bitmap = BitmapFactory.decodeFile(image_path);
                if (bitmap != null) {
                    bitmap = Bitmap.createScaledBitmap(bitmap, desireWidth, desireHeight, true);
                }
            }
            // dstBmp = Bitmap.createBitmap(bitmap2, 0, 0,
            // 4 * bitmap2.getHeight() / 5,
            // 4 * bitmap2.getHeight() / 5);
        } catch (Exception e) {
        }
        mmr.release();
        return bitmap;
    }

    public static BitmapDrawable SetBG(Bitmap b, Context context) {
        StackBlurManager _stackBlurManager = new StackBlurManager(b);
        _stackBlurManager.process(68);
        b = _stackBlurManager.returnBlurredImage();
        // dstBmp = adjustedContrast(dstBmp, 9);
        BitmapDrawable d = new BitmapDrawable(context.getResources(), b);
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
        Bitmap bitmap = null;

        {
            int x = R.drawable.album_art_1 + albumID % IConstant.arr.length;

            bitmap = BitmapFactory.decodeResource(res, x);
            bitmap = Bitmap.createScaledBitmap(bitmap, desireWidth, desireHeight, true);
        }
        return bitmap;

    }
}
