package champak.champabun.business.utilities.utilMethod;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.provider.DocumentFile;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import champak.champabun.AmuzicgApp;

public class StorageAccessAPI {

    public static final int Code = 42;
    public static final int codeToDelete = 43;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void openDocumentTree(Activity c, int deleteoredit, Fragment fragment) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        if (fragment == null) {
            if (deleteoredit == 1)
                c.startActivityForResult(intent, Code);
            else
                c.startActivityForResult(intent, codeToDelete);
        } else {
            if (deleteoredit == 1)
                fragment.startActivityForResult(intent, Code);
            else
                fragment.startActivityForResult(intent, codeToDelete);
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static Uri onActivityResult(final int requestCode, final int resultCode, final Intent resultData, Activity c) {
        Uri treeUri = null;
        if (requestCode == Code || requestCode == codeToDelete) {

            if (resultCode == Activity.RESULT_OK) {
                // Get Uri from Storage Access Framework.
                treeUri = resultData.getData();

                // Persist URI in shared preference so that you can use it later.
                PlayMeePreferences prefs = new PlayMeePreferences(c);
                prefs.SaveSharedPreferenceUri(treeUri);
                System.out.println("from Storage acces APi" + treeUri.toString());
                // Persist access permissions.
                final int takeFlags = resultData.getFlags()

                        & (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                prefs.SaveIntFlag(takeFlags);
                c.getContentResolver().takePersistableUriPermission(treeUri, takeFlags);
            }
        }
        return treeUri;
    }

    public static DocumentFile getDocumentFile(final File file, final boolean isDirectory) {
        String baseFolder = getExtSdCardFolder(file);

        if (baseFolder == null) {
            return null;
        }

        String relativePath;
        try {
            String fullPath = file.getCanonicalPath();
            relativePath = fullPath.substring(baseFolder.length() + 1);
        } catch (IOException e) {
            return null;
        }
        PlayMeePreferences prefs = new PlayMeePreferences(AmuzicgApp.GetInstance().getApplicationContext());
        Uri treeUri = null;
        try {
            treeUri = Uri.parse(prefs.GetSharedPreferenceUri());
        } catch (Exception ignored) {
        }

        if (treeUri == null) {
            return null;
        }

        // start with root of SD card and then parse through document tree.
        DocumentFile document = DocumentFile.fromTreeUri(AmuzicgApp.GetInstance().getApplicationContext(), treeUri);

        String[] parts = relativePath.split("\\/");
        for (int i = 0; i < parts.length; i++) {
            DocumentFile nextDocument = document.findFile(parts[i]);

            if (nextDocument == null) {
                if ((i < parts.length - 1) || isDirectory) {
                    nextDocument = document.createDirectory(parts[i]);
                } else {
                    nextDocument = document.createFile("image", parts[i]);
                }
            }
            document = nextDocument;
        }

        return document;
    }

    public static String getExtSdCardFolder(final File file) {
        String[] extSdPaths = getExtSdCardPaths();
        try {
            for (int i = 0; i < extSdPaths.length; i++) {
                if (file.getCanonicalPath().startsWith(extSdPaths[i])) {
                    return extSdPaths[i];
                }
            }
        } catch (IOException e) {
            return null;
        }
        return null;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private static String[] getExtSdCardPaths() {
        List<String> paths = new ArrayList<>();

        for (File file : AmuzicgApp.GetInstance().getApplicationContext().getExternalFilesDirs("emulated")) {

            if (file != null && !file.equals(AmuzicgApp.GetInstance().getApplicationContext().getExternalFilesDir("emulated"))) {
                int index = file.getAbsolutePath().lastIndexOf("/Android/data");
                if (index < 0) {
                    Log.w("StorageAccessAPI", "Unexpected external file dir: " + file.getAbsolutePath());
                } else {
                    String path = file.getAbsolutePath().substring(0, index);
                    try {
                        path = new File(path).getCanonicalPath();
                    } catch (IOException e) {
                        // Keep non-canonical path.
                    }
                    paths.add(path);
                }
            }
        }
        return paths.toArray(new String[paths.size()]);
    }

}
