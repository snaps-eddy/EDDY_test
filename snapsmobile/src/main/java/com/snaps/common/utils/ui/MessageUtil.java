package com.snaps.common.utils.ui;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Looper;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.util.Linkify;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager.BadTokenException;
import android.widget.TextView;
import android.widget.Toast;

import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.pref.Setting;
import com.snaps.mobile.R;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * AlertDialog 및 Toast 출력에 사용하는 Utility class
 */
public class MessageUtil {
    private static final String TAG = MessageUtil.class.getSimpleName();

    static Toast toast = null;

    public static void alertnoTitle(Activity activity, String message, ICustomDialogListener onClick) {
        if (activity == null)
            return;
        try {
            CustomizeDialog confirmDialog = new CustomizeDialog(activity, message, onClick);
            confirmDialog.show();
        } catch (BadTokenException e) {
            Dlog.e(TAG, e);
        }
    }

    public static void alertnoTitle(Activity activity, String message, ICustomDialogListener onClick, DialogInterface.OnCancelListener cancel) {
        if (activity == null)
            return;
        try {
            CustomizeDialog confirmDialog = new CustomizeDialog(activity, message, onClick, cancel);
            confirmDialog.show();
        } catch (BadTokenException e) {
            Dlog.e(TAG, e);
        }
    }

    public static CustomizeDialog alertnoTitleOneBtn(Activity activity, String message, ICustomDialogListener onClick) {
        if (activity == null || activity.isFinishing())
            return null;
        try {
            CustomizeDialog confirmDialog = new CustomizeDialog(activity, message, onClick);
            confirmDialog.setOneBtnStyle(activity, R.string.confirm);
            confirmDialog.show();
            return confirmDialog;
        } catch (BadTokenException e) {
            Dlog.e(TAG, e);
        }
        return null;
    }

    public static CMDDialog alertForCMDShow(Activity activity, String message, ICustomDialogListener onClick) {
        if (activity == null || activity.isFinishing())
            return null;
        try {
            CMDDialog confirmDialog = new CMDDialog(activity, message, onClick);
            confirmDialog.show();
            return confirmDialog;
        } catch (BadTokenException e) {
            Dlog.e(TAG, e);
        }
        return null;
    }

    public static CustomizeDialog alertnoTitleTwoBtn(Activity activity, String message, ICustomDialogListener onClick) {
        if (activity == null)
            return null;
        try {
            CustomizeDialog confirmDialog = new CustomizeDialog(activity, message, onClick);
            confirmDialog.show();
            return confirmDialog;
        } catch (BadTokenException e) {
            Dlog.e(TAG, e);
        }
        return null;
    }

    public static void kakaoAlert(Activity activity, String title, String message, ICustomDialogListener onClick) {
        if (activity == null)
            return;
        try {
            CustomizeDialog confirmDialog = new CustomizeDialog(activity, message, R.string.confirm_ok_for_kakao, R.string.confirm_cancel_for_kakao, onClick);
            confirmDialog.show();
        } catch (BadTokenException e) {
            Dlog.e(TAG, e);
        }
    }

    public static void alert(Activity activity, String title, String message, ICustomDialogListener onClick) {
        if (activity == null)
            return;
        try {
            CustomizeDialog confirmDialog = new CustomizeDialog(activity, title, message, onClick);
            confirmDialog.show();
        } catch (BadTokenException e) {
            Dlog.e(TAG, e);
        }
    }

    public static void alert(Context context, String title, String message) {
        if (context == null)
            return;
        try {
            CustomizeDialog confirmDialog = new CustomizeDialog(context, title, message, null);
            confirmDialog.setOneBtnStyle();
            confirmDialog.show();
        } catch (BadTokenException e) {
            Dlog.e(TAG, e);
        }
    }

    public static void alert(Activity activity, String title, String message) {
        if (activity == null)
            return;
        try {
            CustomizeDialog confirmDialog = new CustomizeDialog(activity, title, message, null);
            confirmDialog.setOneBtnStyle();
            confirmDialog.show();
        } catch (BadTokenException e) {
            Dlog.e(TAG, e);
        }
    }

    public static void alert(Activity activity, int resId) {
        if (activity == null)
            return;
        try {
            CustomizeDialog confirmDialog = new CustomizeDialog(activity, activity.getString(resId), null);
            confirmDialog.setOneBtnStyle();
            confirmDialog.show();
        } catch (BadTokenException e) {
            Dlog.e(TAG, e);
        }
    }

    public static void alert(Context context, int resId) {
        if (context == null)
            return;
        try {
            CustomizeDialog confirmDialog = new CustomizeDialog(context, context.getString(resId), null);
            confirmDialog.setOneBtnStyle();
            confirmDialog.show();
        } catch (BadTokenException e) {
            Dlog.e(TAG, e);
        }
    }

    public static void alert(Activity activity, Spannable sp) {
        if (activity == null)
            return;
        try {
            CustomizeDialog confirmDialog = new CustomizeDialog(activity, sp.toString(), null);
            confirmDialog.setOneBtnStyle();
            confirmDialog.show();
        } catch (BadTokenException e) {
            Dlog.e(TAG, e);
        }
    }

    public static void alert(Context context, int resId, ICustomDialogListener onClick) {
        if (context == null)
            return;
        try {
            CustomizeDialog confirmDialog = new CustomizeDialog(context, context.getString(resId), onClick);
            confirmDialog.setOneBtnStyle();
            confirmDialog.show();
        } catch (BadTokenException e) {
            Dlog.e(TAG, e);
        }
    }

    public static void alert(Context context, String msg, boolean isCancel, ICustomDialogListener onClick) {
        try {
            CustomizeDialog confirmDialog = new CustomizeDialog(context, msg, onClick);
            confirmDialog.setOneBtnStyle();
            confirmDialog.setCancelable(isCancel);
            confirmDialog.show();
        } catch (BadTokenException e) {
            Dlog.e(TAG, e);
        }
    }

    public static void alert(Context context, int msgId, boolean isCancel, ICustomDialogListener onClick) {
        try {
            CustomizeDialog confirmDialog = new CustomizeDialog(context, context.getString(msgId), onClick);
            confirmDialog.setOneBtnStyle();
            confirmDialog.setCancelable(isCancel);
            confirmDialog.show();
        } catch (BadTokenException e) {
            Dlog.e(TAG, e);
        }
    }

    public static void alert(Context context, String title, String msg, int posBtnStrId, int negBtnStrId, boolean isCancel, ICustomDialogListener onClick) {
        try {
            CustomizeDialog confirmDialog = new CustomizeDialog(context, title, msg, negBtnStrId, posBtnStrId, onClick);
            confirmDialog.setCancelable(isCancel);
            confirmDialog.show();
        } catch (BadTokenException e) {
            Dlog.e(TAG, e);
        }
    }

    public static void alert2(Context context, int msgId, boolean isCancel, ICustomDialogListener onClick) {
        try {
            CustomizeDialog confirmDialog = new CustomizeDialog(context, context.getString(msgId), onClick);
            confirmDialog.setCancelable(isCancel);
            confirmDialog.show();
        } catch (BadTokenException e) {
            Dlog.e(TAG, e);
        }
    }

    public static void alert2(Context context, String msg, boolean isCancel, ICustomDialogListener onClick) {
        try {
            CustomizeDialog confirmDialog = new CustomizeDialog(context, msg, onClick);
            confirmDialog.setCancelable(isCancel);
            confirmDialog.show();
        } catch (BadTokenException e) {
            Dlog.e(TAG, e);
        }
    }

    public static void alert(Context context, int titleId, int msgId, boolean isCancel, ICustomDialogListener onClick) {
        try {
            CustomizeDialog confirmDialog = new CustomizeDialog(context, context.getString(titleId), context.getString(msgId), onClick);
            confirmDialog.setOneBtnStyle();
            confirmDialog.setCancelable(isCancel);
            confirmDialog.show();
        } catch (BadTokenException e) {
            Dlog.e(TAG, e);
        }
    }

    public static void alert(Context context, int titleId, String msg, boolean isCancel, ICustomDialogListener onClick) {
        try {
            CustomizeDialog confirmDialog = new CustomizeDialog(context, context.getString(titleId), msg, onClick);
            confirmDialog.setOneBtnStyle();
            confirmDialog.setCancelable(isCancel);
            confirmDialog.show();
        } catch (BadTokenException e) {
            Dlog.e(TAG, e);
        }
    }

    public static void alert(Context context, String title, String msg, boolean isCancel, ICustomDialogListener onClick) {
        if (context == null)
            return;
        try {
            CustomizeDialog confirmDialog = new CustomizeDialog(context, title, msg, onClick);
            confirmDialog.setOneBtnStyle();
            confirmDialog.setCancelable(isCancel);
            confirmDialog.show();
        } catch (BadTokenException e) {
            Dlog.e(TAG, e);
        }
    }

    public static void alertTwoButton(Context context, String title, String msg, boolean isCancel, ICustomDialogListener onClick) {
        if (context == null)
            return;
        try {
            CustomizeDialog confirmDialog = new CustomizeDialog(context, title, msg, onClick);
            confirmDialog.setCancelable(isCancel);
            confirmDialog.show();
        } catch (BadTokenException e) {
            Dlog.e(TAG, e);
        }
    }

    public static void alert(Context context, int titleId, int resId, ICustomDialogListener onClick) {
        if (context == null)
            return;
        try {
            CustomizeDialog confirmDialog = new CustomizeDialog(context, context.getString(titleId), context.getString(resId), onClick);
            confirmDialog.setOneBtnStyle();
            confirmDialog.show();
        } catch (BadTokenException e) {
            Dlog.e(TAG, e);
        }
    }

    public static void toast(Context context, String message) {
        try {
            if (Build.VERSION.SDK_INT < 16)
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            else {
                if (toast != null)
                    toast.cancel();
                toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
                toast.show();
            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    public static void longToast(Context context, String message) {
        try {
            if (Build.VERSION.SDK_INT < 16)
                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
            else {
                if (toast != null)
                    toast.cancel();
                toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
                toast.show();
            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    public static void simpletoast(Context context, String message) {
        try {
            if (Build.VERSION.SDK_INT < 16)
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            else {
                if (toast != null)
                    toast.cancel();
                toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    public static void toast(Context context, int resId) {
        try {
            if (Build.VERSION.SDK_INT < 16)
                Toast.makeText(context, resId, Toast.LENGTH_SHORT).show();
            else {
                if (toast != null) {
                    toast.cancel();
                    toast = null;
                }
                toast = Toast.makeText(context, resId, Toast.LENGTH_SHORT);
                toast.show();
            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    public static Toast makeToast(Context context, int resId) {
        try {
            if (Build.VERSION.SDK_INT < 16) {
                return Toast.makeText(context, resId, Toast.LENGTH_SHORT);
            }
            return Toast.makeText(context, resId, Toast.LENGTH_SHORT);
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
        return null;
    }

    public static void noPrintToast(Context context, int offSetX, int offSetY) {
        noPrintToast(context, offSetX, offSetY, false);
    }

    public static void noPrintToast(Context context, int offSetX, int offSetY, boolean photoPrint) {
        try {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view;
            if (photoPrint) {
                view = inflater.inflate(R.layout.toast_no_photo_print_custom, null);
            } else {
                view = inflater.inflate(R.layout.toast_no_print_custom, null);
            }


            if (Build.VERSION.SDK_INT < 16)
                Toast.makeText(context, R.string.phootoprint_warnning_message, Toast.LENGTH_SHORT).show();
            else {
                if (toast != null) {
                    toast.cancel();
                    toast = null;
                }
                toast = new Toast(context);
                toast.setGravity(Gravity.BOTTOM, UIUtil.convertDPtoPX(context, offSetX), UIUtil.convertDPtoPX(context, offSetY));
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.setView(view);
                toast.show();
            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    public static void toastForError(Context context, int httpStatus, int kakaoStatus, JSONObject result) {
        Toast.makeText(context, "onError() - httpStatus: " + httpStatus + ", kakaoStatus: " + kakaoStatus + ", result: " + result, Toast.LENGTH_SHORT).show();
    }

    public static void toastForComplete(Context context, int status, int statusCode, JSONObject result) {
        Toast.makeText(context, "onComplete() - httpStatus: " + status + ", kakaoStatus: " + statusCode + ", result: " + result, Toast.LENGTH_SHORT).show();
    }

    public static void toast(Context context, int resId, int gravity) {
        try {
            if (Build.VERSION.SDK_INT < 16)
                Toast.makeText(context, resId, Toast.LENGTH_SHORT).show();
            else {
                if (toast != null) {
                    toast.cancel();
                    toast = null;
                }
                toast = Toast.makeText(context, resId, Toast.LENGTH_SHORT);
                ((TextView) toast.getView().findViewById(android.R.id.message)).setGravity(gravity);
                toast.show();

            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    public static void showPushAgreeInfo(Context context, boolean isAgree, ICustomDialogListener listener) {
        if (context == null)
            return;
        try {
            SimpleDateFormat toFormat = null;

            if (Config.useKorean()) {
                toFormat = new SimpleDateFormat("yyyy년 M월 d일");
            } else {
                toFormat = new SimpleDateFormat("yyyy.M.d");
            }

            String msg = String.format(context.getString(Config.isSnapsBitween() ? R.string.push_agree_popup_info_desc_for_between : R.string.push_agree_popup_info_desc), toFormat.format(new Date()),
                    context.getString((isAgree ? R.string.push_agree_popup_info_msg_confirm : R.string.push_agree_popup_info_msg_blocked)));

            CustomizeDialog confirmDialog = new CustomizeDialog(context, context.getString(Config.isSnapsBitween() ? R.string.push_agree_popup_info_title_for_between
                    : R.string.snaps_push_alarm_service_info), msg, R.string.confirm, R.string.push_agree_popup_btn_no, listener);

            confirmDialog.setCancelable(false);
            confirmDialog.setOneBtnStyle();
            confirmDialog.show();

            Setting.set(context, (Config.isSnapsBitween() ? Const_VALUE.KEY_SAW_BETWEEN_PUSH_AGREE_POPUP : Const_VALUE.KEY_SAW_PUSH_AGREE_POPUP), true);
            Setting.set(context, Const_VALUE.KEY_GCM_PUSH_RECEIVE, isAgree);

        } catch (BadTokenException e) {
            Dlog.e(TAG, e);
        }
    }

    public static void showSnsAlert(Context context) {
        new CustomizeDialog(context).showSnsAlert();
    }

//    public static void showLanguageNotServicedPopup(Activity activity, ICustomDialogListener onClick) {
//
//        String message = "We apologize for the inconvenience.\n" +
//                "Snaps will put English/Chinese services on hold for a while to improve the service.\n" +
//                "If you have any questions, please leave a message below email.\n" +
//                "contact: CS@snaps.com\n" +
//                "Thank you";
//
//        SpannableString spannableString = new SpannableString(message);
//
//        try {
//            Linkify.addLinks(spannableString, Linkify.EMAIL_ADDRESSES);
//            CustomizeDialog confirmDialog = new CustomizeDialog(activity, message, onClick);
//            TextView tv_message = confirmDialog.findViewById(R.id.custom_dialog_title);
//            tv_message.setText("We apologize for the inconvenience.\n");
//            tv_message.append("Snaps will put English/Chinese services on hold for a while to improve the service.\n");
//            tv_message.append("If you have any questions, please leave a message below email.\n");
//            tv_message.append("contact: ");
//            tv_message.append(makeLinkSpan(activity));
//            tv_message.append("\nThank you");
//            tv_message.setMovementMethod(LinkMovementMethod.getInstance());
//
//            confirmDialog.setOneBtnStyle();
//            confirmDialog.setCancelable(false);
//            confirmDialog.show();
//
//        } catch (BadTokenException e) {
//            Dlog.e(TAG, e);
//        }
//
//    }
//
//    private static SpannableString makeLinkSpan(Context context) {
//        CharSequence text = "CS@snaps.com";
//        SpannableString link = new SpannableString(text);
//        link.setSpan(new ClickableSpan() {
//            @Override
//            public void onClick(View widget) {
//                IntentUtil.sendToGMail(context, text.toString());
//            }
//
//            @Override
//            public void updateDrawState(TextPaint ds) {
//                super.updateDrawState(ds);
//                ds.setColor(Color.BLUE);
//                ds.setUnderlineText(true);
//            }
//
//        }, 0, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        return link;
//    }

    public static class PriceToast {
        public static final long DEFAULT_DURATION_MILLISECONDS = 3500;
        public interface Listener {
            void onClose();
        }

        private Activity mActivity;
        private String mMessage;
        private long mDurationInMilliSeconds;
        private volatile Toast mToast;
        //private volatile CountDownTimer mCountDownTimer;
        private Listener mListener;

        public PriceToast() {
            mActivity = null;
            mToast = null;
            //mCountDownTimer = null;
            mListener = null;
        }

        public void show(Activity activity, String msg, long durationInMilliSeconds) {
            show(activity, msg, durationInMilliSeconds, null);
        }

        public void show(Activity activity, String msg, long durationInMilliSeconds, Listener listener) {
            mActivity = activity;
            mMessage = msg;
            mDurationInMilliSeconds = durationInMilliSeconds;
            mListener = listener;

            showToast();

            /*
            mCountDownTimer = new CountDownTimer(mDurationInMilliSeconds - 2000, 1000) {
                public void onTick(long millisUntilFinished) {
                    //cancelToast();
                    showToast();
                }
                public void onFinish() {
                    mToast.cancel();
                    if (mListener != null) {
                        mListener.onClose();
                    }
                }
            };
            mCountDownTimer.start();
            */
        }

        public void cancel() {
            /*
            try {
                if (mCountDownTimer != null) {
                    mCountDownTimer.cancel();
                }
            }catch (Exception e) {
            }
            finally {
                mCountDownTimer = null;
            }
            */

            try {
                cancelToast();
            }catch (Exception e) {
            }
        }

        private void showToast() {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                mToast = makeToast();
                mToast.show();
            }
            else {

                mActivity.runOnUiThread(new Runnable() {
                    public void run() {
                        mToast = makeToast();
                        mToast.show();
                    }
                });
            }
        }

        private void cancelToast() {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                try {
                    if (mToast != null) {
                        mToast.cancel();
                    }
                }catch (Exception e) {
                }
                finally {
                    mToast = null;
                }
            }
            else {
                mActivity.runOnUiThread(new Runnable() {
                    public void run() {
                        try {
                            if (mToast != null) {
                                mToast.cancel();
                            }
                        }catch (Exception e) {
                        }
                        finally {
                            mToast = null;
                        }
                    }
                });
            }
        }

        private Toast makeToast() {
            Toast toast = Toast.makeText(mActivity, mMessage, Toast.LENGTH_LONG);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                View toastView = toast.getView();
                TextView textViewMessage = toastView.findViewById(android.R.id.message);
                if (textViewMessage == null) return toast;

                GradientDrawable gd = new GradientDrawable();
                gd.setColor(Color.BLACK);
                gd.setCornerRadius(10);
                toastView.setBackground(gd);

                textViewMessage.setPadding(10,0,10,0);
                textViewMessage.setTextColor(Color.WHITE);
                textViewMessage.setGravity(Gravity.CENTER);
            }
            return toast;
        }
    }
}
