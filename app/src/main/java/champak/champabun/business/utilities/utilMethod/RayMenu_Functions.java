package champak.champabun.business.utilities.utilMethod;

import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.provider.MediaStore;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.RadioButton;

import java.util.ArrayList;

import champak.champabun.AmuzicgApp;
import champak.champabun.R;
import champak.champabun.business.dataclasses.AppDatabase;
import champak.champabun.business.dataclasses.SongDetails;
import champak.champabun.view.activity.Album;
import champak.champabun.view.activity.Artist;
import champak.champabun.view.activity.Genre;
import champak.champabun.view.adapters.Activity_Fragments;
import champak.champabun.view.adapters.Adapter_SongView;

public class RayMenu_Functions {
    static String sexy2;
    static String pos2;

    // click_no=-10 when this operation is running from f_songs
    public static void sort(final String click_no, final Context mActivity, final BaseAdapter adapter, final ArrayList<SongDetails> songdetails,
                            final ListView mListView, final int whichactivity, String pos, String sexy) {
        sexy2 = sexy;
        pos2 = pos;
        final Dialog dialog2;
        dialog2 = Utilities.designdialog(400, mActivity);
        dialog2.setContentView(R.layout.dialoug_sort);
        final RadioButton r0, r1, r2, r3, r4, r5;
        dialog2.show();
        Typeface font = Typeface.createFromAsset(mActivity.getAssets(), mActivity.getString(R.string.Typeface_JosefinSans_Regular));
        CheckBox chk1 = (CheckBox) dialog2.findViewById(R.id.checkBox1);
        CheckBox chk2 = (CheckBox) dialog2.findViewById(R.id.checkBox2);
        r0 = (RadioButton) dialog2.findViewById(R.id.radio0);
        r1 = (RadioButton) dialog2.findViewById(R.id.radio1);
        r2 = (RadioButton) dialog2.findViewById(R.id.radio2);
        r3 = (RadioButton) dialog2.findViewById(R.id.radio3);
        r4 = (RadioButton) dialog2.findViewById(R.id.radio4);
        r5 = (RadioButton) dialog2.findViewById(R.id.radio5);
        r0.setTypeface(font);
        r1.setTypeface(font);
        r2.setTypeface(font);
        r3.setTypeface(font);
        r4.setTypeface(font);
        r5.setTypeface(font);
        chk1.setTypeface(font);
        chk2.setTypeface(font);
        Button ok = (Button) dialog2.findViewById(R.id.dBSOK);
        Button cancel = (Button) dialog2.findViewById(R.id.dBSCancel);
        if (click_no.equals("-10")) {
            r1.setVisibility(View.VISIBLE);
            r2.setVisibility(View.VISIBLE);
        } else {
            r1.setVisibility(View.GONE);
            r2.setVisibility(View.GONE);
        }
        ok.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                String Asc = checkstateASC_DESC(dialog2);
                String The = checkstateThe(dialog2);
                String The2 = checkstateThe2(dialog2);
                String sortOrder = null;

                ArrayList<SongDetails> songdetails2 = null;
                if (r0.isChecked()) {
                    sortOrder = The2 + MediaStore.Audio.Media.TITLE + The + " COLLATE NOCASE " + Asc;
                    songdetails2 = sort(sortOrder, 1, mActivity, songdetails, click_no);
                } else if (r1.isChecked()) {
                    sortOrder = The2 + MediaStore.Audio.Media.ARTIST + The + " COLLATE NOCASE " + Asc;
                    songdetails2 = sort(sortOrder, 1, mActivity, songdetails, click_no);
                } else if (r2.isChecked()) {
                    sortOrder = The2 + MediaStore.Audio.Media.ALBUM + The + " COLLATE NOCASE " + Asc;
                    songdetails2 = sort(sortOrder, 2, mActivity, songdetails, click_no);
                } else if (r3.isChecked()) {
                    sortOrder = The2 + MediaStore.Audio.Media.DATE_ADDED + The + " COLLATE NOCASE " + Asc;
                    songdetails2 = sort(sortOrder, 1, mActivity, songdetails, click_no);
                } else if (r4.isChecked()) {
                    sortOrder = The2 + MediaStore.Audio.Media.DURATION + The + " COLLATE NOCASE " + Asc;
                    songdetails2 = sort(sortOrder, 1, mActivity, songdetails, click_no);
                } else if (r5.isChecked()) {
                    sortOrder = The2 + MediaStore.Audio.Media.YEAR + The + " COLLATE NOCASE " + Asc;
                    songdetails2 = sort(sortOrder, 1, mActivity, songdetails, click_no);
                }

                if (mActivity instanceof Genre) {
                    AmuzicgApp.GetInstance().getAppSettings().setGenreSortKey(sortOrder);
                    AppDatabase.SaveGenreSortKey(mActivity, sortOrder);
                } else if (mActivity instanceof Artist) {
                    AmuzicgApp.GetInstance().getAppSettings().setArtistSortKey(sortOrder);
                    AppDatabase.SaveArtistSortKey(mActivity, sortOrder);
                } else if (mActivity instanceof Album) {
                    AmuzicgApp.GetInstance().getAppSettings().setAlbumSortKey(sortOrder);
                    AppDatabase.SaveAlbumSortKey(mActivity, sortOrder);
                } else if (mActivity instanceof Activity_Fragments) {
                    AmuzicgApp.GetInstance().getAppSettings().setSongSortKey(sortOrder);
                    AppDatabase.SaveSongSortKey(mActivity, sortOrder);
                }

                setAdapterAndDismissDialog(dialog2, adapter, mActivity, songdetails2, mListView, whichactivity);
                dialog2.dismiss();
            }
        });

        cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialog2.dismiss();
            }
        });
    }

    public static String checkstateASC_DESC(Dialog dialog2) {
        CheckBox asc = (CheckBox) dialog2.findViewById(R.id.checkBox2);

        if (asc.isChecked()) {
            return " DESC";
        } else {
            return " ASC";
        }
    }

    public static String checkstateThe(Dialog dialog2) {
        CheckBox the = (CheckBox) dialog2.findViewById(R.id.checkBox1);
        if (the.isChecked()) {
            return ", '<BEGIN>The ', '<BEGIN>')";
        } else {
            return " ";
        }
    }

    public static String checkstateThe2(Dialog dialog2) {
        CheckBox the = (CheckBox) dialog2.findViewById(R.id.checkBox1);
        if (the.isChecked()) {
            return "REPLACE ('<BEGIN>' || ";
        } else {
            return " ";
        }
    }

    public static ArrayList<SongDetails> sort(String sortOrder, int albumornot, Context mActivity, ArrayList<SongDetails> songdetails,
                                              String click_no) {
        Cursor songCursor = null;
        String where = null;
        String whereVal[] = {click_no};
        if ("-10".equals(click_no)) {
            int duration = AmuzicgApp.GetInstance().getAppSettings().getDurationFilterTime();
            where = MediaStore.Audio.Media.DURATION + ">=?";
            whereVal = new String[]{String.valueOf(duration)};
        } else if ("-2".equals(click_no)) {
            if (pos2 == null) {
                where = MediaStore.Audio.Media.ARTIST + "=?";// +cursor2.getString(0);
                whereVal = new String[]{sexy2};
            } else {
                where = MediaStore.Audio.Media.ALBUM + "=?" + "AND " + MediaStore.Audio.Media.ARTIST + "=?";// +cursor2.getString(0);
                whereVal = new String[]{pos2, sexy2};
            }
        } else {
            where = MediaStore.Audio.Media.ALBUM_ID + "=?";
        }
        try {
            String[] TRACK_COLUMNS = new String[]{MediaStore.Audio.AudioColumns.ALBUM, // 0
                    MediaStore.Audio.AudioColumns.ARTIST, // 1
                    MediaStore.MediaColumns.DATA, // 2
                    MediaStore.MediaColumns.DISPLAY_NAME,// 3
                    MediaStore.Audio.AudioColumns.DURATION,// 4
                    MediaStore.Audio.AudioColumns.TRACK,// 5
                    MediaStore.Audio.AudioColumns.YEAR,// 6
                    MediaStore.MediaColumns.SIZE,// 7
                    MediaStore.MediaColumns.TITLE,// 8
                    MediaStore.Audio.Media._ID,// 9
                    MediaStore.Audio.Media.DATE_ADDED,// 10
                    MediaStore.Audio.AudioColumns.ALBUM_ID,// 11
                    MediaStore.MediaColumns._ID // 12
            };
            songCursor = mActivity.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, TRACK_COLUMNS, where, whereVal,
                    sortOrder);
            if (songCursor != null && songCursor.moveToFirst()) {
                if (songdetails != null) {
                    songdetails.clear();
                } else {
                    songdetails = new ArrayList<>();
                }
                do {
                    int x = Integer.parseInt(songCursor.getString(11));

                    songdetails.add(new SongDetails(songCursor.getInt(songCursor.getColumnIndex(MediaStore.MediaColumns._ID)), null, songCursor
                            .getString(0), songCursor.getString(1), songCursor.getString(2), Utilities.getTime(songCursor.getString(4)),
                            songCursor.getString(8), (albumornot == 2) ? songCursor.getString(0) : songCursor.getString(1), x));
                }
                while (songCursor.moveToNext());

            }
        } finally {
            if (songCursor != null) {
                songCursor.close();
            }
        }

        return songdetails;
    }


    public static void setAdapterAndDismissDialog(Dialog dialog2, BaseAdapter adapter, Context mActivity, ArrayList<SongDetails> songdetails,
                                                  ListView mListView, int whichactivity) {
        if (adapter == null) {
            adapter = new Adapter_SongView(songdetails, mActivity, whichactivity);
            ((Adapter_SongView) adapter).OnUpdate(songdetails);
            mListView.setAdapter(adapter);
        } else {
            ((Adapter_SongView) adapter).OnUpdate(songdetails);
        }
        dialog2.dismiss();
    }

    public static ArrayList<SongDetails> getCheckedList(int highlight_zero, SparseBooleanArray checked, ArrayList<SongDetails> songdetails,
                                                        Adapter_SongView adapter) {

        ArrayList<SongDetails> checkedList = new ArrayList<>();

        if (highlight_zero % 2 == 1) {
            if (songdetails != null && songdetails.size() > 0) {
                checkedList.add(songdetails.get(0));
            }
        }

        if (checked == null || checked.size() == 0) {
            return checkedList;
        }

        if (adapter != null) {
            for (int i = 1; i < checked.size(); i++) {
                if (checked.valueAt(i)) {
                    checkedList.add(adapter.getItem(checked.keyAt(i)));
                }
            }
        }
        return checkedList;
    }
}
