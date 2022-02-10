package com.snaps.common.utils.file;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;

import com.snaps.common.structure.control.Observer;
import com.snaps.common.structure.control.Subject;
import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.thread.ATask;
import com.snaps.common.utils.ui.ContextUtil;
import com.snaps.common.utils.ui.FontUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.edit.view.custom_progress.SnapsTimerProgressView;
import com.snaps.mobile.activity.edit.view.custom_progress.SnapsTimerProgressViewFactory;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import errorhandle.logger.Logg;

public class DownloadFileAsync implements Subject {
    private static final String TAG = DownloadFileAsync.class.getSimpleName();

    private ProgressDialog mDlg;
    private Context mContext;

    private String filename;
    int progress_value;

    Activity activity;

//	ProgressView progress;

    private static ArrayList<Observer> list;
    private String _url;

    public void addObserver(Observer observer) {
        list.add(observer);
    }

    public void notifyObserver() {
        int _size = list.size();
        for (int i = 0; i < _size; i++) {
            Observer o_observer = list.get(i);
            o_observer.updateUI();

        }
    }

    public void removeObserver(Observer observer) {
        list.remove(observer);
    }

    public DownloadFileAsync(Context context, String filename, String url) {
        super();
        mContext = context;

        this.filename = filename;
        this.activity = (Activity) context;
        this._url = url;
        list = new ArrayList<Observer>();

        getFontStorageDir();

        /*
        SnapsTimerProgressView.showProgress((Activity) mContext,
                SnapsTimerProgressViewFactory.eTimerProgressType.PROGRESS_TYPE_TASKS,
                ContextUtil.getString(R.string.font_downloading_msg, "폰트를 다운로드 중 입니다.") + " ");
         */
        SnapsTimerProgressView.showProgress((Activity) mContext,
                SnapsTimerProgressViewFactory.eTimerProgressType.PROGRESS_TYPE_TASKS,
                context.getString(R.string.font_downloading_msg) + " ");
    }

    public DownloadFileAsync(Context context, String filename) {
        mContext = context;

        this.filename = filename;
        this.activity = (Activity) context;
        // getFontStorageDir();

        SnapsTimerProgressView.showProgress((Activity) mContext,
                SnapsTimerProgressViewFactory.eTimerProgressType.PROGRESS_TYPE_TASKS,
                ContextUtil.getString(R.string.font_downloading_msg, "폰트를 다운로드 중 입니다.") + " ");
    }

    public boolean syncProcess() {
        int count = 0;

        HttpURLConnection conexion = null;
        try {
            URL url = new URL(_url);
            conexion = (HttpURLConnection) url.openConnection();
            conexion.connect();

            InputStream input = new BufferedInputStream(url.openStream());
            String sdPath = FontUtil.FONT_FILE_PATH(activity);
            File path = new File(sdPath);
            if (!path.exists()) path.mkdirs();
            File fontFile = new File(sdPath, filename);
            if (!fontFile.exists()) fontFile.createNewFile();

            OutputStream output = new FileOutputStream(fontFile);

            byte data[] = new byte[1024];

            while ((count = input.read(data)) != -1) {

                output.write(data, 0, count);
            }

            output.flush();
            output.close();
            input.close();

            return true;

        } catch (FileNotFoundException fnfe) {
            Dlog.e(TAG, Dlog.PRE_FIX_FONT + "폰트 파일을 찾을 수 없습니다. 경로 : " + _url, fnfe);

        } catch (IOException e) {
            Dlog.e(TAG, e);

        } finally {
            if (conexion != null)
                conexion.disconnect();
        }

        return false;

    }

    public void process(final Context context) {
        ATask.executeVoid(new ATask.OnTask() {
            @Override
            public void onPre() {
            }

            @Override
            public void onBG() {

                int count = 0;

                HttpURLConnection conexion = null;
                try {
                    URL url = new URL(_url);
                    conexion = (HttpURLConnection) url.openConnection();
                    conexion.connect();

                    int lenghtOfFile = conexion.getContentLength();

                    InputStream input = new BufferedInputStream(url.openStream());

                    String sdPath = Const_VALUE.PATH_PACKAGE(activity, false) + "/font";

                    OutputStream output = new FileOutputStream(new File(sdPath, filename));

                    byte data[] = new byte[1024];

                    long total = 0;

                    while ((count = input.read(data)) != -1) {
                        total += count;

                        int percent = (int) ((total * 100) / lenghtOfFile);
                        output.write(data, 0, count);
                        if (percent > progress_value) {
                            progress_value = percent;
                            SnapsTimerProgressView.updateTasksProgressValue(percent);
                        }
                    }

                    SnapsTimerProgressView.destroyProgressView();

                    output.flush();
                    output.close();
                    input.close();
                    notifyObserver();

                } catch (IOException e) {
                    Dlog.e(TAG, e);
                    SnapsTimerProgressView.destroyProgressView();
                } finally {
                    if (conexion != null)
                        conexion.disconnect();
                }
            }

            @Override
            public void onPost() {

            }
        });
    }

    public DownloadFileAsync(Context context, String filename, Activity activity) {
        mContext = context;
        this.filename = filename;
        this.activity = activity;

        getFontStorageDir();

        SnapsTimerProgressView.showProgress((Activity) mContext,
                SnapsTimerProgressViewFactory.eTimerProgressType.PROGRESS_TYPE_TASKS,
                ContextUtil.getString(R.string.font_downloading_msg, "폰트를 다운로드 중 입니다.") + " ");
    }

    private File getFontStorageDir() {

        String sdPath = FontUtil.FONT_FILE_PATH(activity); // 다국어 적용 위해 폰트 저장 경로 변경. renewal 디자인에서도 사용해야 하므로 cache에서 file디렉토리로 옮긴다.

        File file = new File(sdPath);
        if (!file.mkdirs()) {
        }
        return file;
    }

}
