package champak.champabun.business.utilities.utilMethod;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.graphics.PorterDuff;
import android.media.MediaScannerConnection;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.cmc.music.metadata.MusicMetadata;
import org.cmc.music.metadata.MusicMetadataSet;
import org.cmc.music.myid3.MyID3;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import champak.champabun.AmuzicgApp;
import champak.champabun.R;
import champak.champabun.business.dataclasses.SongDetails;
import champak.champabun.business.utilities.popupMenu.QuickAction;
import champak.champabun.framework.listener.EditTagsListener;
import champak.champabun.framework.listener.OptionItemSelectListener;
import champak.champabun.framework.listener.ResponseListener;
import champak.champabun.view.activity.NowPlaying;
import champak.champabun.view.activity.Player;
import champak.champabun.view.adapters.Adapter_playlist_Dialog;

public class SongHelper {
    final public static int DEFAULT = -1;
    public final static int F_SONG = 0;
    public final static int F_PLAYLIST = 2;
    public final static int PLAYLIST = 5;
    final public static int ARTIST = 6;
    final public static int ALBUM = 7;
    private static Activity mActivity;
    private EditTagsListener listeneredit;
    private QuickAction mQuickAction;
    private OptionItemSelectListener mListener;

    private Dialog dialog;
    private boolean isBusy;

    public SongHelper() {
    }

    private static void Permission(final int deleteoredit, final Activity mActivity, final Fragment fragment) {
        // 0 for delete 1 for edit tags
        PlayMeePreferences prefs = new PlayMeePreferences(mActivity);
        prefs.DeleteSharedPreferenceUri();
        final Dialog dg = Utilities.designdialog(200, mActivity);
        dg.setContentView(R.layout.dialoge_delete);
        dg.show();
        TextView headingdg = (TextView) dg.findViewById(R.id.heading);
        headingdg.setText(mActivity.getString(R.string.request_permission));
        TextView dgtext = (TextView) dg.findViewById(R.id.title);
        dgtext.setText(mActivity.getString(R.string.select_sdcard));
        Button ok = (Button) dg.findViewById(R.id.dBOK);
        Button cancel = (Button) dg.findViewById(R.id.dBCancel);

        cancel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                dg.dismiss();
                ActivityUtil.showCrouton(mActivity, mActivity.getString(R.string.denied_permission));
            }
        });

        ok.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                StorageAccessAPI.openDocumentTree(mActivity, deleteoredit, fragment);
                dg.dismiss();
            }
        });

    }

    public static void DeleteSongMultiple(Activity mActivity, final ArrayList<SongDetails> songdetails, final int position2, final OnDeleteSongListener listener, final Fragment fragment) {

        File file = new File(songdetails.get(position2).getPath2());
        boolean canwrite = false;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP
                && !(file.getAbsolutePath().contains("emulated") || file.getAbsolutePath().contains("storage0"))
                ) {

            try {
                canwrite = StorageAccessAPI.getDocumentFile(file, false).canWrite();
            } catch (Exception e) {
                canwrite = false;
            }

            if (!canwrite) {
                int deleteoredit = 0;
                Permission(deleteoredit, mActivity, fragment);
            } else {
                try {
                    StorageAccessAPI.getDocumentFile(file, false).delete();
                } catch (Exception ignored) {
                }
            }

        } else {
            if (file.exists()) {
                file.delete();
            }
        }
    }

    public static void removefromMediastore(ArrayList<SongDetails> songdetails, int position2, OnDeleteSongListener listener, Activity mActivity) {
        try {
            mActivity.getContentResolver().delete(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, MediaStore.MediaColumns.DATA + "=?",
                    new String[]{songdetails.get(position2).getPath2()});
        } catch (SQLiteException ignored) {
        }
        ActivityUtil.showCrouton(mActivity,
                String.format(mActivity.getResources().getString(R.string.f_delete_successful), songdetails.get(position2).getSong()));
        songdetails.remove(position2);
        if (listener != null) {
            listener.OnSongDeleted();
        }
    }

    private static void zoomEffect(View view, final ResponseListener responseListner) {
        ViewCompat.animate(view)
                .setDuration(100)
                .scaleX(0.9f)
                .scaleY(0.9f)
                .setInterpolator(new CycleInterpolator())
                .setListener(new ViewPropertyAnimatorListener() {
                    @Override
                    public void onAnimationStart(View view) {

                    }

                    @Override
                    public void onAnimationEnd(View view) {
                        responseListner.onSuccess("");
                    }

                    @Override
                    public void onAnimationCancel(View view) {

                    }
                })
                .withLayer()
                .start();
    }

    private void SetOnQuickActionItemSelectListener(OptionItemSelectListener listener) {
        this.mListener = listener;
    }

    public void Show(Activity activity, String titleSong, String subTitileSong, OptionItemSelectListener listener, int fragmentID) {
        mActivity = activity;
        SetOnQuickActionItemSelectListener(listener);
        showMoreOption(fragmentID, titleSong, subTitileSong);
    }

    private void showMoreOption(int fragmentID, String titleSong, String subTitileSong) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mActivity, R.style.bottomPopStyle);
        LayoutInflater inflater = LayoutInflater.from(mActivity);
        View dialogView = inflater.inflate(R.layout.dialog_song_more, null);
        dialogBuilder.setView(dialogView);
        final AlertDialog alertDialog = dialogBuilder.create();

        ImageView imageDialog = (ImageView) dialogView.findViewById(R.id.dialogBack);
        TextView titleDialog = (TextView) dialogView.findViewById(R.id.titleD);
        TextView subTitleDialog = (TextView) dialogView.findViewById(R.id.subTitleD);
//        ImageView ringtoneD = (ImageView) dialogView.findViewById(R.id.ringtoneD);
//        ImageView tagsD = (ImageView) dialogView.findViewById(R.id.tagsD);
//        ImageView shareD = (ImageView) dialogView.findViewById(R.id.shareD);
//        ImageView playD = (ImageView) dialogView.findViewById(R.id.playD);
//        ImageView addPlayD = (ImageView) dialogView.findViewById(R.id.addPlayD);
//        ImageView removeD = (ImageView) dialogView.findViewById(R.id.removeD);

        LinearLayout linRingtone = (LinearLayout) dialogView.findViewById(R.id.linRingtone);
        LinearLayout linTagsEdit = (LinearLayout) dialogView.findViewById(R.id.linTagsEdit);
        LinearLayout linShare = (LinearLayout) dialogView.findViewById(R.id.linShare);
        LinearLayout linPLay = (LinearLayout) dialogView.findViewById(R.id.linPLay);
        LinearLayout linAdd2PLay = (LinearLayout) dialogView.findViewById(R.id.linAdd2PLay);
        LinearLayout linRemove = (LinearLayout) dialogView.findViewById(R.id.linRemove);

        titleDialog.setText(titleSong);
        subTitleDialog.setText(subTitileSong);

        if (fragmentID == F_SONG) {
            linShare.setVisibility(View.VISIBLE);
            imageDialog.setColorFilter(ContextCompat.getColor(mActivity, R.color.pinkPager), PorterDuff.Mode.MULTIPLY);
        }

        if (fragmentID == PLAYLIST) {
            linRemove.setVisibility(View.VISIBLE);
        }

        if (fragmentID != F_PLAYLIST) {
            linPLay.setVisibility(View.VISIBLE);
            linRingtone.setVisibility(View.VISIBLE);
        }


        linRingtone.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                zoomEffect(view, new ResponseListener() {
                    @Override
                    public void onSuccess(Object response) {
                        alertDialog.dismiss();
                        mListener.action_OnPlaySong();
                    }
                });
            }
        });

        linTagsEdit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                zoomEffect(view, new ResponseListener() {
                    @Override
                    public void onSuccess(Object response) {
                        alertDialog.dismiss();
                        mListener.action_OnEditTags();
                    }
                });
            }
        });

        linShare.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                zoomEffect(view, new ResponseListener() {
                    @Override
                    public void onSuccess(Object response) {
                        alertDialog.dismiss();
                        mListener.action_OnSendSong();
                    }
                });
            }
        });

        linPLay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                zoomEffect(view, new ResponseListener() {
                    @Override
                    public void onSuccess(Object response) {
                        alertDialog.dismiss();
                        mListener.action_OnPlaySong();
                    }
                });
            }
        });

        linAdd2PLay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                zoomEffect(view, new ResponseListener() {
                    @Override
                    public void onSuccess(Object response) {
                        alertDialog.dismiss();
                        mListener.action_OnAdd2Playlist();
                    }
                });
            }
        });

        linRemove.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                zoomEffect(view, new ResponseListener() {
                    @Override
                    public void onSuccess(Object response) {
                        alertDialog.dismiss();
                        mListener.action_OnDeleteSong();
                    }
                });
            }
        });

        WindowManager.LayoutParams wmlp = alertDialog.getWindow().getAttributes();
//        wmlp.gravity = Gravity.BOTTOM;
        wmlp.windowAnimations = R.style.bottomPopStyle;
        alertDialog.show();
    }

    public void PlaySong(ArrayList<SongDetails> songdetails, int position) {
        AmuzicgApp.GetInstance().setCheck(0);
        Intent intent = new Intent(mActivity, Player.class);
        intent.putParcelableArrayListExtra("Data1", songdetails);
        intent.putExtra("Data2", position);
        mActivity.startActivity(intent);
    }

    public void RemoveSong(ContentResolver cr, String AudioId, int YOUR_PLAYLIST_ID) {
        String[] cols = new String[]{"count(*)"};
        Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", YOUR_PLAYLIST_ID);
        Cursor cur = cr.query(uri, cols, null, null, null);
        if (cur != null) {
            cur.moveToFirst();
            cur.close();
        }

        cr.delete(uri, MediaStore.Audio.Playlists.Members.AUDIO_ID + " = " + AudioId, null);
        ActivityUtil.showCrouton(mActivity, String.format(mActivity.getResources().getString(R.string.remove_from_playlist)));
    }

    public void Add2Playlist(final SongDetails songdetails) {
        Adapter_playlist_Dialog ab;
        dialog = Utilities.designdialog(400, mActivity);

        dialog.setContentView(R.layout.dialog_queue);
        dialog.setTitle(mActivity.getResources().getString(R.string.select_playlist));

        final ArrayList<SongDetails> pl = Utilities.generatePlaylists(mActivity.getApplicationContext());
        SongDetails np = new SongDetails();
        np.setSong(mActivity.getResources().getString(R.string.now_playing));
        pl.add(0, np);
        ab = new Adapter_playlist_Dialog(pl);
        ListView dlgLV = (ListView) dialog.findViewById(R.id.listView1);
        dlgLV.setAdapter(ab);
        dlgLV.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    addToNowPlaying(songdetails);
                } else {
                    String plId = pl.get(position).getArtist();
                    int x = Integer.parseInt(plId);
                    NowPlaying.addToPlaylist(mActivity.getApplicationContext(), songdetails.getPath2(), x);
                    ActivityUtil.showCrouton(mActivity, String.format(
                            mActivity.getResources().getString(R.string.f_song_added_to_the_playlist_named), pl.get(position).getSong()));
                }
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void EditTags(final SongDetails songdetails,
                         final EditTagsListener listener, final Fragment fragment) {
        dialog = Utilities.designdialog(330, mActivity);
        if (listener != null)
            listeneredit = listener;
        dialog.setContentView(R.layout.dialoge_edit_tags);

        dialog.setTitle(songdetails.getSong());
        EditText album = (EditText) dialog.findViewById(R.id.al);
        EditText artist = (EditText) dialog.findViewById(R.id.ar);
        EditText title = (EditText) dialog.findViewById(R.id.ti);
        Button bDOK = (Button) dialog.findViewById(R.id.dBOK);
        Button bDCancle = (Button) dialog.findViewById(R.id.dBCancel);
        artist.setText(songdetails.getArtist());
        album.setText(songdetails.getAlbum());
        String song_ = songdetails.getSong();
        title.setText(song_);
        title.requestFocus(song_.length());

        bDOK.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                EditText album = (EditText) dialog.findViewById(R.id.al);
                EditText artist = (EditText) dialog.findViewById(R.id.ar);
                EditText title = (EditText) dialog.findViewById(R.id.ti);

                String artist2 = artist.getText().toString();
                String album2 = album.getText().toString();
                String title2 = title.getText().toString();
                File src = new File(songdetails.getPath2());
                MusicMetadataSet src_set = null;
                try {
                    src_set = new MyID3().read(src);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (src_set == null)// perhaps no metadata
                {
                    return;
                }
                MusicMetadata meta = new MusicMetadata("name");
                meta.setAlbum(album2);
                meta.setArtist(artist2);
                meta.setSongTitle(title2);
                songdetails.setSong(title2);
                songdetails.setArtist(artist2);
                songdetails.setAlbum(album2);

                try {
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Audio.AudioColumns.ALBUM, album2);
                    values.put(MediaStore.Audio.AudioColumns.ARTIST, artist2);
                    values.put(MediaStore.MediaColumns.TITLE, title2);
                    mActivity.getContentResolver().update(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values, MediaStore.MediaColumns.DATA + "=?",
                            new String[]{songdetails.getPath2()});
                } catch (SQLiteException ignored) {
                }

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {

                    boolean canwrite = false;
                    if (src.getAbsolutePath().contains("emulated") || src.getAbsolutePath().contains("storage0")) {
                        new EditTagsTask(src, src_set, meta).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void) null);
                    } else {
                        try {
                            canwrite = StorageAccessAPI.getDocumentFile(src, false).canWrite();
                        } catch (Exception e) {
                            canwrite = false;
                        }
                        if (canwrite) {
                            new EditTagsTask(src, src_set, meta).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void) null);
                        } else {
                            int deleteoredit = 1;
                            Permission(deleteoredit, mActivity, fragment);
                        }
                    }
                } else {
                    new EditTagsTask(src, src_set, meta).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void) null);
                }
                dialog.dismiss();
            }
        });

        bDCancle.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public Dialog GetDialog(int x) {
        dialog = Utilities.designdialog(x, mActivity);
        return dialog;
    }

    public void SetAsRingtone(SongDetails songdetails) {
        File ringtoneFile = new File(songdetails.getPath2());
        ContentValues content = new ContentValues();
        content.put(MediaStore.MediaColumns.DATA, ringtoneFile.getAbsolutePath());
        content.put(MediaStore.MediaColumns.TITLE, songdetails.getSong());
        content.put(MediaStore.Audio.Media.ALBUM, songdetails.getAlbum());
        content.put(MediaStore.MediaColumns.MIME_TYPE, "audio/*");
        content.put(MediaStore.Audio.Media.ARTIST, songdetails.getArtist());
        content.put(MediaStore.Audio.Media.DURATION, songdetails.getTime());
        content.put(MediaStore.Audio.Media.IS_RINGTONE, true);
        content.put(MediaStore.Audio.Media.IS_NOTIFICATION, false);
        content.put(MediaStore.Audio.Media.IS_ALARM, false);
        content.put(MediaStore.Audio.Media.IS_MUSIC, false);
        try {
            Uri uri = MediaStore.Audio.Media.getContentUriForPath(ringtoneFile.getAbsolutePath());
            mActivity.getContentResolver().delete(uri, MediaStore.MediaColumns.DATA + "=\"" + ringtoneFile.getAbsolutePath() + "\"", null);
            Uri newUri = mActivity.getContentResolver().insert(uri, content);
            RingtoneManager.setActualDefaultRingtoneUri(mActivity, RingtoneManager.TYPE_RINGTONE, newUri);
        } catch (SQLiteException ignored) {
        }
        ActivityUtil.showCrouton(mActivity,
                String.format(mActivity.getResources().getString(R.string.set_as_ringtone), songdetails.getSong()));
    }

    public void DeleteSong(final ArrayList<SongDetails> songdetails, final int position2, final OnDeleteSongListener listener, final Fragment fragment) {
        dialog = Utilities.designdialog(210, mActivity);
        dialog.setContentView(R.layout.dialoge_delete);
        dialog.setTitle(mActivity.getResources().getString(R.string.delete));
        TextView title = (TextView) dialog.findViewById(R.id.title);
        title.setText(String.format(mActivity.getResources().getString(R.string.f_confirm_delete), songdetails.get(position2).getSong()));
        Button ok = (Button) dialog.findViewById(R.id.dBOK);
        Button cancel = (Button) dialog.findViewById(R.id.dBCancel);

        cancel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        ok.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                File file = new File(songdetails.get(position2).getPath2());
                boolean canwrite;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP
                        && !(file.getAbsolutePath().contains("emulated") || file.getAbsolutePath().contains("storage0"))
                        ) {

                    try {
                        canwrite = StorageAccessAPI.getDocumentFile(file, false).canWrite();
                    } catch (Exception e) {
                        canwrite = false;
                    }

                    if (canwrite) {
                        try {
                            StorageAccessAPI.getDocumentFile(file, false).delete();
                            removefromMediastore(songdetails, position2, listener, mActivity);
                        } catch (Exception ignored) {
                        }
                    } else {
                        int deleteoredit = 0;
                        Permission(deleteoredit, mActivity, fragment);
                    }

                } else {
                    if (file.exists()) {
                        file.delete();
                        removefromMediastore(songdetails, position2, listener, mActivity);
                    }
                }


                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void addToNowPlaying(SongDetails songdetails) {
        ActivityUtil.showCrouton(mActivity,
                String.format(mActivity.getResources().getString(R.string.f_was_added_to_queue), songdetails.getSong()));
        AmuzicgApp.GetInstance().Add2NowPlaying(songdetails);
    }

    public void ShouldDismiss() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
            dialog = null;
        }
    }

    public boolean isBusy() {
        return isBusy;
    }

    public void setBusy(boolean isBusy) {
        this.isBusy = isBusy;
    }

    public interface OnRemoveSongListener {
        void OnRemoveSong();
    }

    public interface OnDeleteSongListener {
        void OnSongDeleted();
    }

    private static class CycleInterpolator implements android.view.animation.Interpolator {

        private final float mCycles = 0.5f;

        @Override
        public float getInterpolation(final float input) {
            return (float) Math.sin(2.0f * mCycles * Math.PI * input);
        }
    }

    private class EditTagsTask extends AsyncTask<Void, String, Boolean> {
        File src;
        MusicMetadataSet src_set;
        MusicMetadata meta;

        EditTagsTask(File _src, MusicMetadataSet _src_set, MusicMetadata _meta) {
            src = _src;
            src_set = _src_set;
            meta = _meta;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            boolean ref = false;
            try {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP && !(src.getAbsolutePath().contains("emulated") || src
                        .getAbsolutePath().contains("storage0"))) {
                    new MyID3().write(src, src, src_set, meta);
                } else
                    new MyID3().update(src, src_set, meta);

                MediaScannerConnection.scanFile(mActivity, new String[]{src.getPath()}, null,
                        new MediaScannerConnection.OnScanCompletedListener() {
                            public void onScanCompleted(String path, Uri uri) {
                            }
                        });
                ref = true;
            } catch (Exception ignored) {
            }

            return ref;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (result) {
                if (listeneredit != null) {
                    listeneredit.onEditTagsSuccessful();
                }

                ActivityUtil.showCrouton(mActivity, mActivity.getResources().getString(R.string.tags_edited));
            }
        }
    }
}
