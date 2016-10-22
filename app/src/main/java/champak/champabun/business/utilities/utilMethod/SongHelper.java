package champak.champabun.business.utilities.utilMethod;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.media.MediaScannerConnection;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
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
import champak.champabun.business.utilities.popupMenu.ActionItem;
import champak.champabun.business.utilities.popupMenu.QuickAction;
import champak.champabun.view.activity.NowPlaying;
import champak.champabun.view.activity.Player;
import champak.champabun.view.adapters.Adapter_playlist_Dialog;

public class SongHelper {
    final public static int DEFAULT = -1;
    final public static int F_SONG = 0;
    final public static int F_PLAYLIST = 2;
    final public static int PLAYLIST = 5;
    final public static int ARTIST = 6;
    final public static int ALBUM = 7;
    private static Activity mActivity;
    public OnEditTagsListener listeneredit;
    private QuickAction mQuickAction;
    private OnQuickActionItemSelectListener mListener;

    private Dialog dialog;
    private boolean isBusy;

    public SongHelper() {
    }

    protected static void Permission(final int deleteoredit, final Activity mActivity, final Fragment fragment) {
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
        } catch (SQLiteException e) {
        }
        ActivityUtil.showCrouton(mActivity,
                String.format(mActivity.getResources().getString(R.string.f_delete_successful), songdetails.get(position2).getSong()));
        songdetails.remove(position2);
        if (listener != null) {
            listener.OnSongDeleted();
        }

    }

    private void SetupQuickAction(int fragmentID) {
        mQuickAction = new QuickAction(mActivity);

        ActionItem Play = new ActionItem(1, mActivity.getResources().getString(R.string.play), mActivity.getResources().getDrawable(
                R.drawable.menu_play));
        mQuickAction.addActionItem(Play);

        if (fragmentID != F_PLAYLIST) {
            ActionItem Queue_Song = new ActionItem(2, mActivity.getResources().getString(R.string.add_to_playlist), mActivity.getResources()
                    .getDrawable(R.drawable.menu_queue));
            mQuickAction.addActionItem(Queue_Song);
        }

        String Edit_Tags_Name = mActivity.getResources().getString(R.string.edit_tags);
        if (fragmentID == F_PLAYLIST) {
            Edit_Tags_Name = mActivity.getResources().getString(R.string.rename);
        }
        ActionItem Edit_Tags = new ActionItem(3, Edit_Tags_Name, mActivity.getResources().getDrawable(R.drawable.menu_tags));
        mQuickAction.addActionItem(Edit_Tags);

        if (fragmentID != F_PLAYLIST) {
            ActionItem Set_As_Ringtone = new ActionItem(4, mActivity.getResources().getString(R.string.set_as_ringtone), mActivity.getResources()
                    .getDrawable(R.drawable.menu_ring));
            mQuickAction.addActionItem(Set_As_Ringtone);
        }

        // ActionItem View_Details = new ActionItem(5, "View Details", getResources().getDrawable(R.drawable.menu_details));
        // mQuickAction.addActionItem(View_Details);

        ActionItem Delete = new ActionItem(6, mActivity.getResources().getString(R.string.delete), mActivity.getResources().getDrawable(
                R.drawable.menu_delete));
        mQuickAction.addActionItem(Delete);

        if (fragmentID == F_SONG) {
            ActionItem send = new ActionItem(7, mActivity.getResources().getString(R.string.send), mActivity.getResources().getDrawable(
                    R.drawable.share));
            mQuickAction.addActionItem(send);
        }

        mQuickAction.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {
            @Override
            public void onItemClick(QuickAction quickAction, int position, int actionId) {
                if (mListener != null) {
                    switch (actionId) {
                        case 1:
                            mListener.QuickAction_OnPlaySong();
                            break;
                        case 2:
                            mListener.QuickAction_OnAdd2Playlist();
                            break;
                        case 3:
                            mListener.QuickAction_OnEditTags();
                            break;
                        case 4:
                            mListener.QuickAction_OnSetAsRingtone();
                            break;
                        case 5:
                            mListener.QuickAction_OnViewDetails();
                            break;
                        case 6:
                            mListener.QuickAction_OnDeleteSong();
                            break;
                        case 7:
                            mListener.QuickAction_OnRemoveSong();
                        case 8:
                            mListener.QuickAction_OnSendSong();
                            break;
                    }
                }
            }
        });
    }

    public void CheckQuickAction(int fragmentID) {
        if (mQuickAction == null) {
            SetupQuickAction(fragmentID);
        }
        if (mQuickAction != null) {
            if (fragmentID == PLAYLIST) {
                ActionItem Remove = new ActionItem(7, mActivity.getResources().getString(R.string.remove_from_playlist), mActivity.getResources()
                        .getDrawable(R.drawable.menu_remove));
                mQuickAction.addActionItem(Remove);
            }
        }
    }

    public void SetOnQuickActionItemSelectListener(OnQuickActionItemSelectListener listener) {
        this.mListener = listener;
    }

    public void Show(Activity activity, View anchor, OnQuickActionItemSelectListener listener, int fragmentID) {
        mActivity = activity;
        CheckQuickAction(fragmentID);
        SetOnQuickActionItemSelectListener(listener);
        mQuickAction.show(anchor);
    }

    public void ShowF_Playlist_QA(Activity activity, View anchor, OnQuickActionItemSelectListener listener) {
        mActivity = activity;
        if (mQuickAction == null) {
            SetupQuickAction(F_PLAYLIST);
        }
        SetOnQuickActionItemSelectListener(listener);
        mQuickAction.show(anchor);
    }

    //	private void ShowSaveAsDialog( final SongDetails
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
        ActivityUtil.showCrouton(mActivity,
                String.format(mActivity.getResources().getString(R.string.remove_from_playlist)));
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
                         final OnEditTagsListener listener, final Fragment fragment) {
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

    public void addToNowPlaying(SongDetails songdetails) {
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

    public interface OnQuickActionItemSelectListener {
        void QuickAction_OnPlaySong();

        void QuickAction_OnAdd2Playlist();

        void QuickAction_OnEditTags();

        void QuickAction_OnSetAsRingtone();

        void QuickAction_OnViewDetails();

        void QuickAction_OnDeleteSong();

        void QuickAction_OnRemoveSong();

        void QuickAction_OnSendSong();
    }

    public interface OnEditTagsListener {
        void OnEditTagsSuccessful();
    }

    public interface OnRemoveSongListener {
        void OnRemoveSong();
    }

    public interface OnDeleteSongListener {
        void OnSongDeleted();
    }

    class EditTagsTask extends AsyncTask<Void, String, Boolean> {
        File src;
        MusicMetadataSet src_set;
        MusicMetadata meta;

        public EditTagsTask(File _src, MusicMetadataSet _src_set, MusicMetadata _meta) {
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
                    listeneredit.OnEditTagsSuccessful();
                }

                ActivityUtil.showCrouton(mActivity, mActivity.getResources().getString(R.string.tags_edited));
            }
        }
    }
}
