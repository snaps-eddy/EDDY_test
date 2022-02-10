package com.snaps.common.utils.thread;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.snaps.common.utils.log.Dlog;
import com.snaps.mobile.R;

import font.FProgressDialog;

/**
 * AsyncTask를 편하게 씀.
 *
 * @author crjung
 */
public class ATask {
    private static final String TAG = ATask.class.getSimpleName();

    /**
     * 외부 클래스에서 정의해야 할 Thread 작업 시의 처리들
     *
     * @author crjung
     */
    public interface OnTask {
        public void onPre();

        public void onBG();

        public void onPost();
    }

    public interface OnTaskResult {
        public void onPre();

        public boolean onBG();

        public void onPost(boolean result);
    }

    public interface OnTaskObject {
        public void onPre();

        public Object onBG();

        public void onPost(Object result);
    }

    public interface OnTaskBitmap {
        public void onPre();

        public Bitmap onBG();

        public void onPost(Bitmap bitmap);
    }


    /**
     * 단순 쓰레드작업 실행
     *
     * @param onTask
     * @return
     */
    public static AsyncTask<Void, Void, Void> executeVoid(final OnTask onTask) {
        AsyncTask<Void, Void, Void> aTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                onTask.onPre();
            }

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    onTask.onBG();
                } catch (Exception e) {
                    Dlog.e(TAG, e);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                try {
                    onTask.onPost();
                } catch (Exception e) {
                    Dlog.e(TAG, e);
                }
            }

            ;
        }.execute();
        return aTask;
    }

    public static AsyncTask<Void, Void, Void> executeVoidWithThreadPool(final OnTask onTask) {
        AsyncTask<Void, Void, Void> aTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                onTask.onPre();
            }

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    onTask.onBG();
                } catch (Exception e) {
                    Dlog.e(TAG, e);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                try {
                    onTask.onPost();
                } catch (Exception e) {
                    Dlog.e(TAG, e);
                }
            }

            ;
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        return aTask;
    }

    public static AsyncTask<Void, Void, Boolean> executeVoidWithThreadPoolBooleanDefProgress(Context context, final OnTaskResult onTask) {
        final FProgressDialog pd = new FProgressDialog(context);
        pd.setMessage(context.getString(R.string.please_wait));
        pd.setCancelable(false);
        AsyncTask<Void, Void, Boolean> aTask = new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected void onPreExecute() {
                pd.show();
                onTask.onPre();
            }

            @Override
            protected Boolean doInBackground(Void... params) {
                try {
                    return onTask.onBG();
                } catch (Exception e) {
                    Dlog.e(TAG, e);
                }
                return false;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                try {
                    pd.dismiss();
                    onTask.onPost(result);
                } catch (Exception e) {
                    Dlog.e(TAG, e);
                }
            }

            ;
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        return aTask;
    }

    public static AsyncTask<Void, Void, Boolean> executeVoidWithThreadPoolBoolean(final OnTaskResult onTask) {
        AsyncTask<Void, Void, Boolean> aTask = new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected void onPreExecute() {
                onTask.onPre();
            }

            @Override
            protected Boolean doInBackground(Void... params) {
                try {
                    return onTask.onBG();
                } catch (Exception e) {
                    Dlog.e(TAG, e);
                }
                return false;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                try {
                    onTask.onPost(result);
                } catch (Exception e) {
                    Dlog.e(TAG, e);
                }
            }

            ;
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        return aTask;
    }


    public static AsyncTask<Void, Void, Bitmap> executeVoidWithThreadPool(final OnTaskBitmap onTask) {
        AsyncTask<Void, Void, Bitmap> aTask = new AsyncTask<Void, Void, Bitmap>() {
            @Override
            protected void onPreExecute() {
                onTask.onPre();
            }

            @Override
            protected Bitmap doInBackground(Void... params) {
                try {
                    return onTask.onBG();
                } catch (Exception e) {
                    Dlog.e(TAG, e);;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Bitmap result) {
                try {
                    onTask.onPost(result);
                } catch (Exception e) {
                    Dlog.e(TAG, e);
                }
            }

            ;
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        return aTask;
    }

    public static AsyncTask<Void, Void, Object> executeVoid(final OnTaskObject onTask) {
        AsyncTask<Void, Void, Object> aTask = new AsyncTask<Void, Void, Object>() {
            @Override
            protected void onPreExecute() {
                onTask.onPre();
            }

            @Override
            protected Object doInBackground(Void... params) {
                try {
                    return onTask.onBG();
                } catch (Exception e) {
                    Dlog.e(TAG, e);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Object result) {
                try {
                    onTask.onPost(result);
                } catch (Exception e) {
                    Dlog.e(TAG, e);
                }
            }

            ;
        }.execute();
        return aTask;
    }

    public static AsyncTask<Void, Void, Bitmap> executeVoid(final OnTaskBitmap onTask) {
        AsyncTask<Void, Void, Bitmap> aTask = new AsyncTask<Void, Void, Bitmap>() {
            @Override
            protected void onPreExecute() {
                onTask.onPre();
            }

            @Override
            protected Bitmap doInBackground(Void... params) {
                try {
                    return onTask.onBG();
                } catch (Exception e) {
                    Dlog.e(TAG, e);
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Bitmap result) {
                try {
                    onTask.onPost(result);
                } catch (Exception e) {
                    Dlog.e(TAG, e);
                }
            }

            ;
        }.execute();
        return aTask;
    }

    /**
     * 기본 프로그래스 메시지를 보여주는 단순 쓰레드작업 실행
     *
     * @param context
     * @param onTask
     * @return
     */
    public static AsyncTask<Void, Void, Void> executeVoidDefProgress(Context context, final OnTask onTask) {
        return executeVoidDefProgress(context, R.string.please_wait, onTask);
    }

    public static AsyncTask<Void, Void, Void> executeVoidDefProgress(Context context, int progressMsg, final OnTask onTask) {
        if (context == null) return null;

        final FProgressDialog pd = new FProgressDialog(context);
        pd.setMessage(context.getString(progressMsg));
        pd.setCancelable(false);
        AsyncTask<Void, Void, Void> aTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                pd.show();
                onTask.onPre();
            }

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    onTask.onBG();
                } catch (Exception e) {
                    Dlog.e(TAG, e);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                try {
                    pd.dismiss();
                    onTask.onPost();
                } catch (Exception e) {
                    Dlog.e(TAG, e);
                }
            }

            ;
        }.execute();
        return aTask;
    }

    public static AsyncTask<Void, Void, Boolean> executeBoolean(final OnTaskResult onTask) {
        AsyncTask<Void, Void, Boolean> aTask = new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected void onPreExecute() {
                onTask.onPre();
            }

            @Override
            protected Boolean doInBackground(Void... params) {
                try {
                    return onTask.onBG();
                } catch (Exception e) {
                    Dlog.e(TAG, e);
                }
                return false;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                try {
                    onTask.onPost(result);
                } catch (Exception e) {
                    Dlog.e(TAG, e);
                }
            }

            ;
        }.execute();
        return aTask;
    }

    /**
     * 기본 프로그래스 메시지를 보여주고 background의 결과를 받는 단순 쓰레드작업 실행
     *
     * @param context
     * @param onTask
     * @return
     */
    public static AsyncTask<Void, Void, Boolean> executeBooleanDefProgress(Context context, final OnTaskResult onTask) {
        final FProgressDialog pd = new FProgressDialog(context);
        pd.setMessage(context.getString(R.string.please_wait));
        pd.setCancelable(false);
        AsyncTask<Void, Void, Boolean> aTask = new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected void onPreExecute() {
                pd.show();
                onTask.onPre();
            }

            @Override
            protected Boolean doInBackground(Void... params) {
                try {
                    return onTask.onBG();
                } catch (Exception e) {
                    Dlog.e(TAG, e);
                }
                return false;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                try {
                    pd.dismiss();
                    onTask.onPost(result);
                } catch (Exception e) {
                    Dlog.e(TAG, e);
                }
            }

            ;
        }.execute();
        return aTask;
    }

}
