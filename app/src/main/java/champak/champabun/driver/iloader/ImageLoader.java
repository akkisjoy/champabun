package champak.champabun.driver.iloader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.TypedValue;
import android.widget.ImageView;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import champak.champabun.AmuzicgApp;
import champak.champabun.R;

public class ImageLoader {
    final private static int MAX_THREAD = 3;
    static MemoryCache memoryCache = new MemoryCache();
    private static FileCache fileCache;
    Context c;
    float ht_px;
    float wt_px;
    Bitmap draw;
    ExecutorService executorService;
    Handler handler = new Handler();// handler to display images in UI thread
    private Map<ImageView, String> imageViews = Collections.synchronizedMap(new HashMap<ImageView, String>());

    public ImageLoader(Context context) {
        setFileCache(new FileCache(context));
        c = context;// added some context thing
        executorService = Executors.newFixedThreadPool(MAX_THREAD);
        draw = BitmapFactory.decodeResource(c.getResources(), R.drawable.default_art);
        ht_px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 130, c.getResources().getDisplayMetrics());
        wt_px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 130, c.getResources().getDisplayMetrics());
        draw = Bitmap.createScaledBitmap(draw, (int) ht_px, (int) wt_px, true);
        // the reason why i have used a "draw" is because suppose the uri doesn't exist for some cases,then because of
        // view reusability random pictures were getting displayed
        // fadein(0,400);
    }

    public void DisplayImage(String url, ImageView imageView, String album, String artist) {
        imageViews.put(imageView, url);
        Bitmap bitmap = memoryCache.get(url);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        } else {
            queuePhoto(url, imageView, album, artist);
            imageView.setImageBitmap(draw);
        }
    }

    private void queuePhoto(String url, ImageView imageView, String album, String artist) {
        PhotoToLoad p = new PhotoToLoad(url, imageView, album, artist);
        executorService.submit(new PhotosLoader(p));
    }

    public Bitmap getBitmap(String url, String album, String artist) {
        artist = artist.trim();

        album = album.trim();
        File f = getFileCache().getFile(artist + "_" + album);
        Bitmap b = null;
        if (f.exists()) {
            b = BitmapFactory.decodeFile(f.getAbsolutePath());
        }
        if (b != null) {
            return b;
        } else {
            f = new File(url);
            b = decodeFile(f);

            if (b == null) {
                b = getBitmap2(artist, album);
            }
            if (b != null) {
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                f = getFileCache().getFile(artist + "_" + album);
                try {
                    b.compress(Bitmap.CompressFormat.JPEG, 80, bytes);
                    f.createNewFile();
                    FileOutputStream fo = new FileOutputStream(f);
                    fo.write(bytes.toByteArray());
                    fo.close();
                } catch (IOException ignored) {
                }
            }
            return b;
        }
    }

    public Bitmap fetchfilefromcahce(String album, String artist) {
        artist = artist.trim();

        album = album.trim();
        File f = getFileCache().getFile(artist + "_" + album);
        if (!f.exists()) {
            return null;
        }
        Bitmap b = null;
        try {
            b = BitmapFactory.decodeFile(f.getAbsolutePath());
        } catch (OutOfMemoryError e) {
        }
        return b;
    }

    public Bitmap getBitmap2(String artistname, String albumname) {
        String key = null;
        artistname = artistname.trim();

        albumname = albumname.trim();
        File f = getFileCache().getFile(artistname + "_" + albumname);
        artistname = artistname.replace(" ", "%20");
        artistname = artistname.replace("/", "");

        albumname = albumname.replace(" ", "%20");
        albumname = albumname.replace("/", "");

        if (albumname.equals("") || albumname.equals("unknown") || artistname.equals("") || artistname.equals("unknown"))
            return null;

        String url = "http://ws.audioscrobbler.com/2.0/?method=album.getinfo&api_key=ee51c315e1feda25a36d5523150ccdd4&artist=" + artistname
                + "&album=" + albumname;
        try {
            URL url2 = new URL(url);
            URLConnection connection = url2.openConnection();
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(30000);

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();

            final Document document = db.parse(connection.getInputStream());
            document.getDocumentElement().normalize();
            XPathFactory xPathfactory = XPathFactory.newInstance();
            XPath xPathEvaluator = xPathfactory.newXPath();
            XPathExpression nameExpr = xPathEvaluator.compile("//lfm/album/image");
            NodeList nl = (NodeList) nameExpr.evaluate(document, XPathConstants.NODESET);
            Node currentItem;

            for (int zzz = 0; zzz < ((nl.getLength() > 0) ? nl.getLength() - 2 : 0); zzz++) {
                currentItem = nl.item(zzz);
                key = currentItem.getTextContent();

            }
            Bitmap bitmap;

            URL imageUrl = new URL(key);
            HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(30000);
            conn.setInstanceFollowRedirects(true);
            InputStream is = conn.getInputStream();

            OutputStream os = new FileOutputStream(f);
            Utils.CopyStream(is, os);
            os.close();
            conn.disconnect();
            bitmap = decodeFile(f);
            if (bitmap != null) {
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, bytes);
                f.createNewFile();
                FileOutputStream fo = new FileOutputStream(f);
                fo.write(bytes.toByteArray());
                // remember close de FileOutput
                fo.close();
            }
            return bitmap;
        } catch (Throwable ex) {
            if (ex instanceof OutOfMemoryError)
                memoryCache.clear();
            return null;
        }
    }

    // decodes image and scales it to reduce memory consumption
    private Bitmap decodeFile(File f) {
        try {
            // decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            FileInputStream stream1 = new FileInputStream(f);
            BitmapFactory.decodeStream(stream1, null, o);
            stream1.close();

            // Find the correct scale value. It should be the power of 2.

            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;
            while (true) {
                if (width_tmp / 2 < AmuzicgApp.GetInstance().sizeofimage || height_tmp / 2 < AmuzicgApp.GetInstance().sizeofimage)
                    break;
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }

            // decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            FileInputStream stream2 = new FileInputStream(f);
            Bitmap bitmap = BitmapFactory.decodeStream(stream2, null, o2);
            stream2.close();
            return bitmap;
        } catch (IOException | OutOfMemoryError ignored) {
        }
        return null;
    }

    boolean imageViewReused(PhotoToLoad photoToLoad) {
        String tag = imageViews.get(photoToLoad.imageView);
        return tag == null || !tag.equals(photoToLoad.url);
    }

    public void clearMCache() {
        memoryCache.clear();
        draw = null;
    }

    public FileCache getFileCache() {
        return fileCache;
    }

    public static void setFileCache(FileCache fileCache) {
        ImageLoader.fileCache = fileCache;
    }

    // Task for the queue
    private class PhotoToLoad {
        public String url;
        public ImageView imageView;
        public String album;
        public String artist;

        public PhotoToLoad(String u, ImageView i, String a, String art) {
            url = u;
            imageView = i;
            album = a;
            artist = art;
        }
    }

    class PhotosLoader implements Runnable {
        PhotoToLoad photoToLoad;

        PhotosLoader(PhotoToLoad photoToLoad) {
            this.photoToLoad = photoToLoad;
        }

        @Override
        public void run() {
            try {
                Bitmap bmp;
                if (imageViewReused(photoToLoad))
                    return;
                bmp = getBitmap(photoToLoad.url, photoToLoad.album, photoToLoad.artist);
                memoryCache.put(photoToLoad.url, bmp);
                if (imageViewReused(photoToLoad))
                    return;
                BitmapDisplayer bd = new BitmapDisplayer(bmp, photoToLoad);
                handler.post(bd);
            } catch (Throwable th) {
                th.printStackTrace();
            }
        }
    }

    // Used to display bitmap in the UI thread
    class BitmapDisplayer implements Runnable {
        Bitmap bitmap;
        PhotoToLoad photoToLoad;

        public BitmapDisplayer(Bitmap bb, PhotoToLoad p) {
            bitmap = bb;
            photoToLoad = p;
        }

        public void run() {
            if (imageViewReused(photoToLoad))

                return;
            if (bitmap != null)
            // TODO Animation
            {
                photoToLoad.imageView.setImageBitmap(bitmap);
            } else {
                photoToLoad.imageView.setImageBitmap(draw);
            }
        }
    }
}
