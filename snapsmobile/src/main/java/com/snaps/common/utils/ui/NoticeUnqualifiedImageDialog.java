package com.snaps.common.utils.ui;

import android.app.Dialog;
import android.content.Context;
import android.view.ViewGroup;
import android.view.Window;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.jakewharton.rxbinding3.view.RxView;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.edit.fragment.dialog.DialogInputNameFragment;

import io.reactivex.disposables.CompositeDisposable;

public class NoticeUnqualifiedImageDialog extends Dialog {

    private static final String TAG = NoticeUnqualifiedImageDialog.class.getSimpleName();

    private DialogInputNameFragment.IDialogInputNameClickListener dialogInputNameClickListener;
    private SubsamplingScaleImageView iv_photo;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    public NoticeUnqualifiedImageDialog(Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        init(context, null);
    }

    public DialogInputNameFragment.IDialogInputNameClickListener getDialogInputNameClickListener() {
        return dialogInputNameClickListener;
    }

    public void setDialogInputNameClickListener(DialogInputNameFragment.IDialogInputNameClickListener dialogInputNameClickListener) {
        this.dialogInputNameClickListener = dialogInputNameClickListener;
    }

    private void init(Context context, DialogInputNameFragment.IDialogInputNameClickListener listener) {
        if (context == null) {
            return;
        }

        setContentView(R.layout.dialog_photo);

        if (getWindow() != null) {
            getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }

        iv_photo = findViewById(R.id.iv_photo);
        iv_photo.setMaxScale(3F);

        compositeDisposable.add(RxView.clicks(findViewById(R.id.ib_close))
                .subscribe(unit -> NoticeUnqualifiedImageDialog.this.dismiss()));

        compositeDisposable.add(RxView.clicks(findViewById(R.id.making_keyring_img_popup_confirm))
                .subscribe(unit -> NoticeUnqualifiedImageDialog.this.dismiss()));

        if (listener != null) {
            setDialogInputNameClickListener(listener);
        }

        setCanceledOnTouchOutside(true);
    }

    public void setNoticeImage(ImageEdgeValidation imageEdgeValidation) {
//        iv_photo.setImageBitmap();
        iv_photo.setImage(ImageSource.bitmap(imageEdgeValidation.getBitmap()));
    }

    @Override
    public void dismiss() {
        super.dismiss();
//        Drawable d = iv_photo.get();
//
//        if (d instanceof BitmapDrawable) {
//            Bitmap bitmap = ((BitmapDrawable) d).getBitmap();
//            bitmap.recycle();
//        }
        iv_photo.recycle();

        compositeDisposable.clear();
    }
}
