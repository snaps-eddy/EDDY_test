package com.snaps.mobile.tutorial.custom_tutorial;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import androidx.annotation.NonNull;

import com.snaps.common.data.model.SnapsCommonResultListener;
import com.snaps.common.structure.SnapsHandler;
import com.snaps.common.utils.ISnapsHandler;
import com.snaps.common.utils.animation.SnapsFrameAnimation;
import com.snaps.common.utils.log.Dlog;
import com.snaps.mobile.tutorial.SnapsTutorialAttribute;

import static com.snaps.mobile.tutorial.SnapsTutorialAttribute.eCustomTutorialType.RECOMMEND_BOOK_IMAGE_SELECT;

public class CustomTutorialView extends Dialog implements ISnapsHandler {
    private static final String TAG = CustomTutorialView.class.getSimpleName();

    private SnapsTutorialAttribute attribute = null;

    private SnapsFrameAnimation frameAnimation = null;
    private CloseListener closeListener = null;
    private SnapsHandler snapsHandler = null;
    private boolean isStopped = false;

    private CustomTutorialInterface tutorialInterface = null;

    public CustomTutorialView(@NonNull Context context, SnapsTutorialAttribute attribute) {
        super(context,android.R.style.Theme_Translucent_NoTitleBar);
        this.attribute = attribute;
    }

    public CustomTutorialView(@NonNull Context context, SnapsTutorialAttribute attribute, CloseListener closeLintener) {
        super(context,android.R.style.Theme_Translucent_NoTitleBar);
        this.attribute = attribute;
        this.closeListener = closeLintener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initialize();

        if (tutorialInterface == null) {
            cancel();
            return;
        }

        setContentView(tutorialInterface.getContentViewLayoutId());

        tutorialInterface.initTutorialView();

        tutorialInterface.showTutorialView();

        tutorialInterface.setCloseTutorialListener(new SnapsCommonResultListener<Void>() {
            @Override
            public void onResult(Void aVoid) {
                performCloseTutorial();
            }
        });

        startTutorialCloseTimer();
    }

    private void initialize() {
        tutorialInterface = CustomTutorialViewStrategyFactory.createTutorialStrategy(this, RECOMMEND_BOOK_IMAGE_SELECT);
        snapsHandler = new SnapsHandler(this);
        isStopped = false;
    }

    private void startTutorialCloseTimer() {
        if (tutorialInterface == null || snapsHandler == null) return;
        snapsHandler.sendEmptyMessageDelayed(HANDLE_MSG_CLOSE_TUTORIAL_CHECK, tutorialInterface.getTutorialAutoCloseTime());
    }

    private void performCloseTutorial() {
        endCloseAndOpenAnimation();
        stopTimer();
        cancel();

        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                if(closeListener != null){
                    closeListener.close();
                }
            }
        });
    }

    private void setInit() {
//        this.snapsHandler = new SnapsHandler(this);
//        this.isStopped = false;
//
//        String msg = null;
//        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.layout);
//        final ImageView imageView = (ImageView) findViewById(R.id.imageViewHandAnimationView);
//        final HandAnimationView handAnimationView = (HandAnimationView) findViewById(R.id.handAnimationView);
//        relativeLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                endCloseAndOpenAnimation();
//                stopTimer();
//                cancel();
//
//                Handler handler = new Handler(Looper.getMainLooper());
//                handler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        if(closeListener != null){
//                            closeListener.close();
//                        }
//                    }
//                });
//            }
//        });
//
//        switch (attribute.getGifType()) {
//            case PINCH_ZOOM_AND_DRAG:
//                msg = getContext().getString(R.string.img_sel_phone_pic_tutorial_pinch);
//                handAnimationView.setVisibility(View.INVISIBLE);
//                final ImageView imageViewDrag = (ImageView) findViewById(R.id.imageViewDragAnimationView);
//                startNewPinchAnimation(imageViewDrag);
//                snapsHandler.sendEmptyMessageDelayed(HANDLE_MSG_PINCH_ZOOM_AND_DRAG_TUTORIAL_CHECK, CHANGE_TIME);
//                break;
//            case PINCH_ZOOM: {
//                msg = getContext().getString(R.string.img_sel_phone_pic_tutorial_pinch);
//                handAnimationView.setVisibility(View.INVISIBLE);
//                startCloseAndOpenAnimation(imageView);
//                break;
//            }
//            case MOVE_HAND: {
//                msg = getContext().getString(R.string.tutorial_print_area_setting);
//                imageView.setVisibility(View.INVISIBLE);
//                handAnimationView.startAnimation(getContext());
//                break;
//            }
//        }
//
//        FTextView textViewMsg = (FTextView) findViewById(R.id.textViewMsg);
//        textViewMsg.setText(msg);
//        snapsHandler.sendEmptyMessageDelayed(HANDLE_MSG_CLOSE_TUTORIAL_CHECK, DEFAULT_TUTORIAL_CLOSE_TIME);
    }

    private void stopTimer() {
        isStopped = true;
        endCloseAndOpenAnimation();
    }

    private void endCloseAndOpenAnimation() {
        if(frameAnimation != null) {
            frameAnimation.release();
        }
    }

    @Override
    public void cancel() {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
             @Override
             public void run() {
                 if (closeListener != null)
                    closeListener.close();
             }
         });

        super.cancel();
    }

    public interface CloseListener {
        void close();
    }
    
    public static CustomTutorialView createTutorialView(Context context, SnapsTutorialAttribute attribute){
        return new CustomTutorialView(context,attribute);
    }

    public static CustomTutorialView createTutorialView(Context context, SnapsTutorialAttribute attribute, CloseListener closeListener){
        return new CustomTutorialView(context,attribute,closeListener);
    }

    private static final int HANDLE_MSG_CLOSE_TUTORIAL_CHECK = 0;

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case HANDLE_MSG_CLOSE_TUTORIAL_CHECK:
                forceCancelGifTutorialView();
                break;
        }
    }

    private void forceCancelGifTutorialView() {
        if (isStopped) return;
        try {
            stopTimer();
            CustomTutorialView.this.cancel();

            sendCloseMsg();
        } catch (Exception e) { Dlog.e(TAG, e); }
    }

    private void sendCloseMsg() {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                if(closeListener != null){
                    closeListener.close();
                }
            }
        });
    }
}
