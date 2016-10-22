package champak.champabun.view.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.romainpiel.shimmer.Shimmer;
import com.romainpiel.shimmer.ShimmerTextView;

import java.util.ArrayList;

import champak.champabun.AmuzicgApp;
import champak.champabun.R;
import champak.champabun.business.dataclasses.MediaFilesObject;
import champak.champabun.business.dataclasses.SongDetails;
import champak.champabun.business.definition.IConstant;
import champak.champabun.business.utilities.utilMethod.Utilities;
import champak.champabun.view.activity.MainActivity;
import champak.champabun.view.activity.Player;
import champak.champabun.view.adapters.Adapter_Folder;

public class F_Folder extends BaseFragment {
    ImageView backPager;
    private String rootPath = "/";
    private TextView mPathTextView;
    private ListView mListView;
    private Adapter_Folder mAdapter;
    private MainActivity mActivity;
    private MediaFilesObject mMediaFilesObject;
    private MediaFilesObject mCurMediaFilesObject;
    private ArrayList<SongDetails> songDetails;
    private boolean isRegistered;
    private ShimmerTextView titleHeader;
    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            songDetails = intent.getExtras().getParcelableArrayList("songdetails");
            if (songDetails != null && songDetails.size() > 0) {
                FetchFolders();
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            rootPath = savedInstanceState.getString("rootPath");
            isRegistered = savedInstanceState.getBoolean("isRegistered");
            songDetails = savedInstanceState.getParcelableArrayList("F_Folder.songDetails");
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (isVisibleToUser) {
            new Shimmer().setRepeatCount(0)
                    .setDuration(2000)
                    .setStartDelay(100)
                    .start(titleHeader);

        }
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.l_folder, container, false);

        mActivity = (MainActivity) getActivity();
        mAdapter = null;
        backPager = (ImageView) view.findViewById(R.id.backPager);
        backPager.setColorFilter(ContextCompat.getColor(getActivity(), R.color.purplePager), PorterDuff.Mode.MULTIPLY);
        titleHeader = (ShimmerTextView) view.findViewById(R.id.titleHeader);
        mPathTextView = (TextView) view.findViewById(R.id.path);
        mListView = (ListView) view.findViewById(R.id.list);
        mListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                if (mCurMediaFilesObject.getSubMediaFiles().get(position).isSongdetails()) {
                    ArrayList<SongDetails> sd = GetCurSongDetails();
                    int findPos = position;
                    if (sd.size() < mCurMediaFilesObject.getTotalSongs()) {
                        SongDetails _sd = mCurMediaFilesObject.getSubMediaFiles().get(position).getSongdetails();
                        for (int i = 0; i < sd.size(); i++) {
                            if (sd.get(i).getPath2().equals(_sd.getPath2())) {
                                findPos = i;
                                break;
                            }
                        }
                    }
                    GoToPlayer(sd, findPos);
                } else {
                    mCurMediaFilesObject = mCurMediaFilesObject.getSubMediaFiles().get(position);
                    OnRefreshListview();
                }
            }
        });

        if (!isRegistered) {
            mActivity.registerReceiver(receiver, new IntentFilter(IConstant.BROADCAST_ON_SONGDETAILS_UPDATED));
            isRegistered = true;
        }

        if (mMediaFilesObject == null) {
            if (songDetails != null && songDetails.size() > 0) {
                FetchFolders();
            }
        } else {
            OnRefreshListview();
        }

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("rootPath", rootPath);
        outState.putBoolean("isRegistered", isRegistered);
        outState.putParcelableArrayList("F_Folder.songDetails", songDetails);
        super.onSaveInstanceState(outState);
    }

    private void FetchFolders() {
        if (songDetails != null && songDetails.size() > 0) {
            new GetMediaFiles(songDetails).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void) null);
        }
    }

    private void OnRefreshListview() {
        String path = mCurMediaFilesObject.getPath();
        mPathTextView.setText(path);
        if (mAdapter == null) {
            mAdapter = new Adapter_Folder(getActivity(), mCurMediaFilesObject.getSubMediaFiles());
            mListView.setAdapter(mAdapter);
        } else {
            mAdapter.OnUpdate(mCurMediaFilesObject.getSubMediaFiles());
        }
    }

    public boolean canGoBack() {
        if (mCurMediaFilesObject == null || mCurMediaFilesObject.getPath().equals(rootPath)) {
            return false;
        } else {
            // Logger.d("F_Folder", "canGoBack.................... mMediaFilesObject.getFolderName() = " + mMediaFilesObject.getFolderName()
            // + " mCurMediaFilesObject.getParentPath() = " + mCurMediaFilesObject.getParentPath());
            mCurMediaFilesObject = GetCurMediaFilesObject(mMediaFilesObject, mCurMediaFilesObject.getParentPath());
            OnRefreshListview();
            return true;
        }
    }

    private MediaFilesObject GetCurMediaFilesObject(MediaFilesObject mediaFilesObject, String parentPath) {
        if (mediaFilesObject.getPath().equals(parentPath)) {
            return mediaFilesObject;
        }
        ArrayList<MediaFilesObject> mFiles = mediaFilesObject.getSubMediaFiles();
        for (int i = 0; i < mFiles.size(); i++) {
            if (mFiles.get(i).getPath().equals(parentPath)) {
                return mFiles.get(i);
            } else {
                if (!mFiles.get(i).isSongdetails()) {
                    MediaFilesObject mf = GetCurMediaFilesObject(mFiles.get(i), parentPath);
                    if (mf != null) {
                        return mf;
                    }
                }
            }
        }
        return null;
    }

    private ArrayList<SongDetails> GetCurSongDetails() {
        ArrayList<SongDetails> songdetails = new ArrayList<>();
        ArrayList<MediaFilesObject> mFiles = mCurMediaFilesObject.getSubMediaFiles();
        for (int i = 0; i < mFiles.size(); i++) {
            if (mFiles.get(i).isSongdetails()) {
                songdetails.add(mFiles.get(i).getSongdetails());
            }
        }
        return songdetails;
    }

    private void GoToPlayer(ArrayList<SongDetails> songdetails, int position) {
        Intent intent = new Intent(mActivity, Player.class);

        if (songdetails.size() > 800) {
            AmuzicgApp.GetInstance().SetNowPlayingList(songdetails);
        } else {
            AmuzicgApp.GetInstance().SetNowPlayingList(new ArrayList<>(songdetails));
        }
        AmuzicgApp.GetInstance().setPosition(position);
        AmuzicgApp.GetInstance().setCheck(0);
        mActivity.startActivity(intent);
    }

    @Override
    public void onDestroy() {
        try {
            mActivity.unregisterReceiver(receiver);
            isRegistered = false;
        } catch (Exception ignored) {
        }
        super.onDestroy();
    }

    @Override
    public void Update() {
    }

    @Override
    protected String GetGAScreenName() {
        return "F_Folder";
    }

    class GetMediaFiles extends AsyncTask<Void, Void, MediaFilesObject> {
        int start;
        ArrayList<SongDetails> songDetails;

        public GetMediaFiles(ArrayList<SongDetails> _songDetails) {
            songDetails = _songDetails;
            if (songDetails != null && songDetails.size() > 0) {
                CheckRootPath(songDetails.get(0).getPath2());
            }
            start = rootPath.length();
        }

        private void CheckRootPath(String songPath) {
            if (Utilities.isEmpty(songPath)) {
                rootPath = Environment.getExternalStorageDirectory().getPath() + "/";
                return;
            }
            if (songPath.startsWith("/storage/")) {
                rootPath = "/storage/";
            } else if (songPath.startsWith("/sdcard/")) {
                rootPath = "/sdcard/";
            } else if (songPath.startsWith("/mnt/sdcard/")) {
                rootPath = "/mnt/sdcard/";
            } else if (songPath.startsWith("/storage/sdcard0/")) {
                rootPath = "/storage/sdcard0/";
            } else if (songPath.startsWith("/storage/sdcard1/")) {
                rootPath = "/storage/sdcard1/";
            } else if (songPath.startsWith("/sdcard/external_sd/")) {
                rootPath = "/sdcard/external_sd/";
            } else {
                rootPath = Environment.getExternalStorageDirectory().getPath() + "/";
            }
        }

        @Override
        protected MediaFilesObject doInBackground(Void... arg0) {
            MediaFilesObject root = null;
            if (songDetails != null && songDetails.size() > 0) {
                ArrayList<SongDetails> temp = new ArrayList<>();
                temp.addAll(songDetails);
                root = new MediaFilesObject("/", rootPath, null, null);
                GetMediaFilesObject(root, temp);
            }
            return root;
        }

        private void GetMediaFilesObject(MediaFilesObject root, ArrayList<SongDetails> songdetails) {
            if (songdetails != null && songdetails.size() > 0) {
                int totalSong = 0;
                String prePath = root.getPath();
                int index = 0;
                while (index < songdetails.size()) {
                    String songPath = songdetails.get(index).getPath2();
                    if (Utilities.isEmpty(songPath)) {
                        index++;
                        continue;
                    }
                    String folder = null;
                    String lastPath = songPath.substring(prePath.length());
                    int slashIndex = lastPath.indexOf("/");
                    if (slashIndex != -1)// still folder
                    {
                        folder = lastPath.substring(0, slashIndex);
                        MediaFilesObject mf = new MediaFilesObject(folder, prePath + folder + "/", prePath, null);
                        root.addSubMediaFiles(mf);
                        ArrayList<SongDetails> sd = GetSongDetails(prePath + folder + "/", songdetails);
                        if (sd != null) {
                            totalSong += sd.size();
                        }
                        GetMediaFilesObject(mf, sd);
                    }// file
                    else {
                        root.addSubMediaFiles(new MediaFilesObject(folder, prePath + folder + "/", prePath, songdetails.get(index)));
                        totalSong++;
                        index++;
                    }
                    // sort SubMediaFiles, folder first
                    ArrayList<MediaFilesObject> subMediaFiles = root.getSubMediaFiles();
                    if (subMediaFiles != null && subMediaFiles.size() > 0) {
                        int songIndex = -1;
                        int j = 0;
                        do {
                            if (!subMediaFiles.get(j).isSongdetails()) {
                                if (songIndex >= 0) {
                                    MediaFilesObject obj = subMediaFiles.get(j);
                                    subMediaFiles.remove(j);
                                    subMediaFiles.add(songIndex, obj);
                                    songIndex++;
                                } else {
                                    j++;
                                }
                            } else {
                                if (songIndex == -1) {
                                    songIndex = j;
                                }
                                j++;
                            }
                        }
                        while (j < subMediaFiles.size());
                    }
                }
                root.setTotalSongs(totalSong);
            }
        }

        private ArrayList<SongDetails> GetSongDetails(String folderPath, ArrayList<SongDetails> temp) {
            ArrayList<SongDetails> newList = new ArrayList<>();
            int index = 0;
            while (index < temp.size()) {
                if (temp.get(index).getPath2().contains(folderPath)) {
                    newList.add(temp.get(index));
                    temp.remove(index);
                } else {
                    index++;
                }
            }
            return newList;
        }

        @Override
        protected void onPostExecute(MediaFilesObject result) {
            super.onPostExecute(result);

            mMediaFilesObject = result;
            mCurMediaFilesObject = mMediaFilesObject;
            OnRefreshListview();
        }
    }
}
