package champak.champabun.view.activity;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import champak.champabun.AmuzicgApp;
import champak.champabun.R;
import champak.champabun.business.dataclasses.SongDetails;
import champak.champabun.business.definition.IConstant;
import champak.champabun.business.utilities.dragSortListView.DragSortController;
import champak.champabun.business.utilities.dragSortListView.DragSortListView;
import champak.champabun.business.utilities.utilMethod.ActivityUtil;

public class NowPlaying extends BaseActivity {
    Context context;
    private Adapter_DragSort adapter;
    public DragSortListView.RemoveListener onRemove = new DragSortListView.RemoveListener() {
        @Override
        public void remove(int which) {
            if (AmuzicgApp.GetInstance().getPosition() > which && AmuzicgApp.GetInstance().GetNowPlayingSize() > 1) {
                AmuzicgApp.GetInstance().decreasePosition();
            } else if (AmuzicgApp.GetInstance().getPosition() == which && AmuzicgApp.GetInstance().GetNowPlayingSize() > 1) {
                AmuzicgApp.GetInstance().decreasePosition();
                AmuzicgApp.GetInstance().boolMusicPlaying1 = true;

                Intent intentswap = new Intent(IConstant.BROADCAST_SWAP);
                sendBroadcast(intentswap);
            } else if (AmuzicgApp.GetInstance().getPosition() == 0 && AmuzicgApp.GetInstance().GetNowPlayingSize() == 1) {
                AmuzicgApp.GetInstance().boolMusicPlaying1 = false;
                Intent intent = new Intent(IConstant.BROADCAST_STOP_NOW);
                sendBroadcast(intent);
            }
            AmuzicgApp.GetInstance().RemoveFromNowPlaying(which);
            adapter.notifyDataSetChanged();
        }
    };
    private ProgressBar spinner;
    private Button save_as;
    private DragSortController mController;
    private DragSortListView SngList;
    private ImageView backPager;
    private DragSortListView.DropListener onDrop = new DragSortListView.DropListener() {
        @Override
        public void drop(int from, int to) {
            SongDetails item = adapter.getItem(from);
            if (AmuzicgApp.GetInstance().getPosition() == from) {
                AmuzicgApp.GetInstance().setPosition(to);
            } else if ((from < AmuzicgApp.GetInstance().getPosition() && to < AmuzicgApp.GetInstance().getPosition())
                    || (from > AmuzicgApp.GetInstance().getPosition() && to > AmuzicgApp.GetInstance().getPosition())) {
            } else if (from < AmuzicgApp.GetInstance().getPosition() && to >= AmuzicgApp.GetInstance().getPosition()) {
                AmuzicgApp.GetInstance().decreasePosition();
            } else if (from > AmuzicgApp.GetInstance().getPosition() && to <= AmuzicgApp.GetInstance().getPosition()) {
                AmuzicgApp.GetInstance().increasePosition();
            }
            AmuzicgApp.GetInstance().RemoveFromNowPlaying(from);
            SngList.removeItem(from);
            adapter.insert(item, to);
            adapter.notifyDataSetChanged();
        }
    };

    public static void addToPlaylist(Context context, String path, int YOUR_PLAYLIST_ID) { // TODO just a marker
        ContentResolver resolver = context.getContentResolver();
        int audioId = getIdfromPath(context, path);

        String[] cols = new String[]{"count(*)"};
        Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", YOUR_PLAYLIST_ID);
        Cursor cur = null;
        try {
            cur = resolver.query(uri, cols, null, null, null);
            if (cur != null && cur.moveToFirst()) {
                final int base = cur.getInt(0);
                ContentValues values = new ContentValues();
                values.put(MediaStore.Audio.Playlists.Members.PLAY_ORDER, Integer.valueOf(base + audioId));
                values.put(MediaStore.Audio.Playlists.Members.AUDIO_ID, audioId);
                resolver.insert(uri, values);
            }
        } finally {
            if (cur != null) {
                cur.close();
            }
        }
    }

    public static int getIdfromPath(Context context, String path) {
        String[] PROJECTION_FINDID = new String[]{MediaStore.MediaColumns.DATA, MediaStore.Audio.AudioColumns.YEAR, MediaStore.Audio.Media._ID,};

        int songId = 0;
        Cursor c = null;
        try {
            c = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, PROJECTION_FINDID,
                    MediaStore.MediaColumns.DATA + "=\"" + path + "\"", null, null);
            if (c != null) {
                c.moveToLast();
                songId = c.getInt(c.getColumnIndex(MediaStore.Audio.Media._ID));
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return songId;
    }

    public static int createPlaylist(String name, Context context) {
        String[] PROJECTION_PLAYLIST = new String[]{MediaStore.Audio.Playlists._ID, MediaStore.Audio.Playlists.NAME,
                MediaStore.Audio.Playlists.DATA};

        int mPlaylistId = -1;
        ContentValues mInserts = new ContentValues();
        mInserts.put(MediaStore.Audio.Playlists.NAME, name);
        mInserts.put(MediaStore.Audio.Playlists.DATE_ADDED, System.currentTimeMillis());
        mInserts.put(MediaStore.Audio.Playlists.DATE_MODIFIED, System.currentTimeMillis());
        ContentResolver mCR = context.getContentResolver();
        Uri mUri = mCR.insert(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, mInserts);
        if (mUri != null) {
            Cursor c = mCR.query(mUri, PROJECTION_PLAYLIST, null, null, MediaStore.Audio.Playlists.DATE_ADDED + " ASC");
            if (c != null) {
                c.moveToLast();
                mPlaylistId = c.getInt(c.getColumnIndex(MediaStore.Audio.Playlists._ID));
                c.close();
            }
        }
        return mPlaylistId;
    }

    public DragSortController buildController(DragSortListView dslv) {
        DragSortController controller = new DragSortController(dslv);
        controller.setDragHandleId(R.id.drag_handle);
        controller.setClickRemoveId(R.id.click_remove);
        controller.setRemoveEnabled(true);
        controller.setSortEnabled(true);
        controller.setDragInitMode(DragSortController.ON_DOWN);
        controller.setRemoveMode(DragSortController.FLING_REMOVE);

        return controller;
    }

    @Override
    public int GetLayoutResID() {
        return R.layout.now_playing;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;

        backPager = (ImageView) findViewById(R.id.backPager);
        backPager.setColorFilter(getResources().getColor(R.color.maroon), PorterDuff.Mode.MULTIPLY);

        spinner = (ProgressBar) findViewById(R.id.progressBar1);
        spinner.setVisibility(View.GONE);

        SngList = (DragSortListView) findViewById(R.id.listview);
        save_as = (Button) findViewById(R.id.save_as);
        mController = buildController(SngList);
        SngList.setDropListener(onDrop);
        SngList.setRemoveListener(onRemove);
        SngList.setFloatViewManager(mController);
        SngList.setOnTouchListener(mController);
        SngList.setDragEnabled(true);
        adapter = new Adapter_DragSort(AmuzicgApp.GetInstance().GetNowPlayingList());

        SngList.setAdapter(adapter);

        SngList.setSelection(AmuzicgApp.GetInstance().getPosition());
        SngList.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                AmuzicgApp.GetInstance().setPosition(position);
                AmuzicgApp.GetInstance().boolMusicPlaying1 = true;
                Intent intentswap = new Intent(IConstant.BROADCAST_SWAP);
                sendBroadcast(intentswap);
            }
        });
    }

    public void OnSaveAsButtonClick(View view) {
        final Dialog dialog;
        dialog = new Dialog(this, R.style.AmuzeTheme);
        final Window window = dialog.getWindow();
        float pixels = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 210, this.getResources().getDisplayMetrics());
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, (int) pixels);
        window.setBackgroundDrawableResource(R.drawable.dialogbg);
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.dimAmount = 0.8f; // Dim level. 0.0 - no dim, 1.0 - completely opaque
        dialog.getWindow().setAttributes(lp);
        window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        dialog.setContentView(R.layout.dialog_create_new_playlist);
        Button bDOK = (Button) dialog.findViewById(R.id.dBOK);
        Button bDCancle = (Button) dialog.findViewById(R.id.dBCancel);

        bDOK.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                EditText save = (EditText) dialog.findViewById(R.id.save_as);
                if (save.getText().toString() != null) {
                    int YOUR_PLAYLIST_ID = createPlaylist(save.getText().toString(), context);
                    if (YOUR_PLAYLIST_ID == -1)
                        return;
                    new AddToPl().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, YOUR_PLAYLIST_ID);
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

    @Override
    public String GetActivityID() {
        return "NowPlaying";
    }

    @Override
    public void SetNullForCustomVariable() {
    }

    @Override
    public int GetRootViewID() {
        return R.id.bg;
    }

    @Override
    public void OnBackPressed() {
        finish();
    }

    @Override
    protected String GetGAScreenName() {
        return "NowPlaying";
    }

    class AddToPl extends AsyncTask<Integer, String, String> {
        @Override
        protected void onPostExecute(String x) {
            save_as.setVisibility(View.VISIBLE);
            spinner.setVisibility(View.GONE);
            ActivityUtil.showCrouton(NowPlaying.this, getString(R.string.playlist_created));
        }

        @Override
        protected void onPreExecute() {
            save_as.setVisibility(View.GONE);
            spinner.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Integer... params) {
            for (int index = 0; index < AmuzicgApp.GetInstance().GetNowPlayingList().size(); index++) {
                try {
                    addToPlaylist(getApplicationContext(), AmuzicgApp.GetInstance().GetNowPlayingList().get(index).getPath2(),
                            params[0]);
                } catch (IllegalStateException e) {
                    ActivityUtil.showCrouton(NowPlaying.this, getString(R.string.playlist_name_already_exists));
                    return null;
                } catch (Exception e) {
                    ActivityUtil.showCrouton(NowPlaying.this, getString(R.string.an_error_occurred));
                    return null;
                }
            }

            return null;
        }
    }

    public class Adapter_DragSort extends ArrayAdapter<SongDetails> {
        private List<SongDetails> songs;

        public Adapter_DragSort(List<SongDetails> songs) {
            super(NowPlaying.this, R.layout.item_now_playing, R.id.Songs, songs);
            this.songs = songs;
        }

        @Override
        public int getCount() {
            if (songs == null) {
                return 0;
            } else {
                return songs.size();
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = super.getView(position, convertView, parent);
            if (v != convertView && v != null) {
                ViewHolder holder = new ViewHolder();

                holder.albumsView = (TextView) v.findViewById(R.id.Songs);
                v.setTag(holder);
            }
            ViewHolder holder = (ViewHolder) (v != null ? v.getTag() : null);
            String albums = songs.get(position).getSong();
            if (holder != null) {
                holder.albumsView.setText(albums);
            }
            return v;
        }
    }

    class ViewHolder {
        public TextView albumsView;
    }
}
