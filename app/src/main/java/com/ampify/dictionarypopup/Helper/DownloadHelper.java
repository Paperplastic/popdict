package com.ampify.dictionarypopup.Helper;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.webkit.MimeTypeMap;
import android.widget.ProgressBar;

import com.ampify.dictionarypopup.MainActivity;

import java.io.File;

public class DownloadHelper {

    private Context mContext;
    private DownloadManager mDownloadManager;
    private long downloadId;
    private boolean downloading;
    private ProgressBar mProgressBar;

    //Extract on download complete
    private BroadcastReceiver onDownloadComplete = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            if (downloadId == id) {
                new ExtractWordnetHelper(mContext, new ExtractWordnetHelper.ExtractInterface(){
                    @Override
                    public void extractFinish() {
                        deleteAdditionalFiles();
                        Intent intentToMain = new Intent(mContext , MainActivity.class);
                        mContext.startActivity(intentToMain);
                        mContext.unregisterReceiver(onDownloadComplete);
                    }
                }).execute();
            }
        }
    };

    public DownloadHelper(Context context, ProgressBar progressBar) {
        mContext = context;
        mProgressBar = progressBar;
        mContext.registerReceiver(onDownloadComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    //TODO: set progress bar while downloading
    public void downloadWordNet() {
        String u = "http://wordnetcode.princeton.edu/3.0/WNdb-3.0.tar.gz";
        Uri uri = Uri.parse(u);
        File file = new File(mContext.getExternalFilesDir(null), uri.getLastPathSegment());
        DownloadManager.Request request = new DownloadManager.Request(uri)
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setMimeType(getMimeType(u))
                .setDestinationUri(Uri.fromFile(file));
        mDownloadManager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
        downloadId = mDownloadManager.enqueue(request);
        showProgress();
    }

    public static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }

    public void showProgress() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                downloading = true;
                while (downloading) {
                    DownloadManager.Query query = new DownloadManager.Query();
                    query.setFilterById(downloadId);
                    Cursor cursor = mDownloadManager.query(query);
                    cursor.moveToFirst();
                    final int totalBytes = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                    final int bytesDownloaded = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                    if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL) {
                        mProgressBar.setProgress(100);
                        downloading = false;
                    } else {
                        ((Activity)mContext).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mProgressBar.setProgress((bytesDownloaded * 100) / totalBytes);
                            }
                        });
                        cursor.close();
                    }

                }
            }
        }).start();
    }

    public void deleteAdditionalFiles() {
        File dir = mContext.getExternalFilesDir(null);
        String[] files = dir.list();
        for (int i = 0; i < files.length; i++) {
            if (!files[i].equals("Wordnet")) {
                new File(dir, files[i]).delete();
            }
        }
    }

}
