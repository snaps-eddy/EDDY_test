package com.snaps.mobile.tutorial.new_tooltip_tutorial;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import androidx.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.snaps.common.structure.SnapsHandler;
import com.snaps.common.utils.ISnapsHandler;
import com.snaps.common.utils.animation.SnapsAnimationHandler;
import com.snaps.common.utils.animation.SnapsFrameAnimation;
import com.snaps.common.utils.animation.frame_animation.SnapsFrameAnimationResFactory;
import com.snaps.common.utils.constant.Const_PRODUCT;
import com.snaps.common.utils.constant.Const_VALUES;
import com.snaps.common.utils.log.Dlog;
import com.snaps.mobile.R;
import com.snaps.mobile.tutorial.SnapsTutorialAttribute;

import font.FTextView;

import static com.snaps.mobile.tutorial.SnapsTutorialAttribute.GIF_TYPE.PINCH_ZOOM_AND_DRAG;

/**
 * Created by kimduckwon on 2017. 9. 22..
 */

public class GifTutorialView extends Dialog implements ISnapsHandler {
    private static final String TAG = GifTutorialView.class.getSimpleName();

    public static final int DEFAULT_TUTORIAL_CLOSE_TIME = 8000;
    private static final int CHANGE_TIME = 4000;
    private SnapsFrameAnimation frameAnimation = null;
    private SnapsTutorialAttribute attribute = null;
    private CloseListener closeLintener = null;
    private SnapsHandler snapsHandler = null;
    private boolean isStopped = false;

    public GifTutorialView(@NonNull Context context, SnapsTutorialAttribute attribute) {
        super(context,android.R.style.Theme_Translucent_NoTitleBar);
        this.attribute = attribute;
    }

    public GifTutorialView(@NonNull Context context, SnapsTutorialAttribute attribute,CloseListener closeLintener) {
        super(context,android.R.style.Theme_Translucent_NoTitleBar);
        this.attribute = attribute;
        this.closeLintener = closeLintener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gif_tutorial_view);
        setInit();
    }

    private void setInit() {
        this.snapsHandler = new SnapsHandler(this);
        this.isStopped = false;

        String msg = null;
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.relativeLayoutTopMost);
        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                endCloseAndOpenAnimation();
                stopTimer();
                cancel();

                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(closeLintener != null){
                            closeLintener.close();
                        }
                    }
                });
            }
        });

        long totalDurations = DEFAULT_TUTORIAL_CLOSE_TIME;

        switch (attribute.getGifType()) {
            case PINCH_ZOOM_AND_DRAG:
                msg = getContext().getString(R.string.img_sel_phone_pic_tutorial_pinch);
                final ImageView imageViewDrag = (ImageView) findViewById(R.id.imageViewDragAnimationView);
                if (imageViewDrag != null) {
                    imageViewDrag.setVisibility(View.VISIBLE);
                    startNewPinchAnimation(imageViewDrag);
                    snapsHandler.sendEmptyMessageDelayed(HANDLE_MSG_PINCH_ZOOM_AND_DRAG_TUTORIAL_CHECK, CHANGE_TIME);
                }
                break;
            case PINCH_ZOOM: {
                msg = getContext().getString(R.string.img_sel_phone_pic_tutorial_pinch);
                ImageView imageView = (ImageView) findViewById(R.id.imageViewHandAnimationView);
                if (imageView != null) {
                    imageView.setVisibility(View.VISIBLE);
                    startImageSelectPinchTutorialAnimation(imageView);
                    totalDurations = frameAnimation.getTotalDurations();
                }
                break;
            }
            case MOVE_HAND: {
                msg = getContext().getString(R.string.tutorial_print_area_setting);
                final HandAnimationView handAnimationView = findViewById(R.id.handAnimationView);
                if (handAnimationView != null) {
                    handAnimationView.setVisibility(View.VISIBLE);
                    handAnimationView.startAnimation(getContext());
                    totalDurations = frameAnimation.getTotalDurations();
                }
                break;
            }
            case RECOMMEND_BOOK_MAIN_LIST_PINCH_ZOOM: {
                msg = getContext().getString(R.string.recommend_book_main_act_pinch_zoom_tutorial_msg);
                ImageView imageView = (ImageView) findViewById(R.id.imageViewDragAnimationView);
                if (imageView != null) {
                    imageView.setVisibility(View.VISIBLE);
                    startRecommendBookPinchTutorialAnimation(imageView);
                    totalDurations = frameAnimation.getTotalDurations();
                }
                break;
            }
            case KT_BOOK_EDITOR: {
                //KT 북
                msg = Const_VALUES.KT_BOOK_EDITOR_TUTORIAL_TEXT;
                ImageView imageView = (ImageView) findViewById(R.id.imageViewDragAnimationView);
                if (imageView != null) {
                    imageView.setVisibility(View.VISIBLE);
                    startKTBookEditorTutorialAnimation(imageView);
                    totalDurations = frameAnimation.getTotalDurations();
                }
                break;
            }
            case ACRYLIC_KEYING_EDITOR: {
                msg = getContext().getString(R.string.tutorial_acrylicKeyring_editor_usage_guide);
                ImageView imageView = (ImageView) findViewById(R.id.imageViewDragAnimationView);
                if (imageView != null) {
                    imageView.setVisibility(View.VISIBLE);
                    starAcrylicKeyringEditorTutorialAnimation(imageView);
                    totalDurations = frameAnimation.getTotalDurations();
                }
                break;
            }
            case ACRYLIC_STAND_EDITOR: {
                msg = getContext().getString(R.string.tutorial_acrylicStand_editor_usage_guide);
                ImageView imageView = (ImageView) findViewById(R.id.imageViewDragAnimationView);
                if (imageView != null) {
                    imageView.setVisibility(View.VISIBLE);
                    starAcrylicStandEditorTutorialAnimation(imageView);
                    totalDurations = frameAnimation.getTotalDurations();
                }
                break;
            }
        }

        FTextView textViewMsg = (FTextView) findViewById(R.id.textViewMsg);
        textViewMsg.setText(msg);
//        startTimer();

        snapsHandler.sendEmptyMessageDelayed(HANDLE_MSG_CLOSE_TUTORIAL_CHECK, totalDurations);

    }

    private void stopTimer() {
        isStopped = true;
        endCloseAndOpenAnimation();
    }

    //KT 북
    private void startKTBookEditorTutorialAnimation(ImageView imageView) {
        frameAnimation = SnapsAnimationHandler.startFrameAnimation(getContext(), imageView, SnapsFrameAnimationResFactory.eSnapsFrameAnimation.KT_BOOK_EDITOR);
    }

    private void startImageSelectPinchTutorialAnimation(ImageView imageView) {
        frameAnimation = SnapsAnimationHandler.startFrameAnimation(getContext(), imageView, SnapsFrameAnimationResFactory.eSnapsFrameAnimation.IMAGE_SELECT_PINCH);
    }

    private void startRecommendBookPinchTutorialAnimation(ImageView imageView) {
        frameAnimation = SnapsAnimationHandler.startFrameAnimation(getContext(), imageView, SnapsFrameAnimationResFactory.eSnapsFrameAnimation.RECOMMEND_BOOK_MAIN_ACT_PINCH);
    }

    private void starAcrylicKeyringEditorTutorialAnimation(ImageView imageView) {
        frameAnimation = SnapsAnimationHandler.startFrameAnimation(getContext(), imageView, SnapsFrameAnimationResFactory.eSnapsFrameAnimation.ACRYLIC_KEYING_EDITOR);
    }

    private void starAcrylicStandEditorTutorialAnimation(ImageView imageView) {
        frameAnimation = SnapsAnimationHandler.startFrameAnimation(getContext(), imageView, SnapsFrameAnimationResFactory.eSnapsFrameAnimation.ACRYLIC_STAND_EDITOR);
    }

    private void startDragAnimation() {
       Handler handler = new Handler(Looper.getMainLooper());
       handler.post(new Runnable() {
           @Override
           public void run() {
               FTextView textViewMsg = (FTextView) findViewById(R.id.textViewMsg);
               if (textViewMsg != null) {
                   String msg = getContext().getString(R.string.tutorial_drag_msg);
                   textViewMsg.setText(msg);
               }
               final ImageView imageView = (ImageView) findViewById(R.id.imageViewDragAnimationView);
               if (imageView != null) {
                   frameAnimation = SnapsAnimationHandler.startFrameAnimation(getContext(), imageView, SnapsFrameAnimationResFactory.eSnapsFrameAnimation.IMAGE_SELECT_TUTORIAL_DRAG);
               }
           }
       });
    }

    private void startNewPinchAnimation(ImageView imageView) {
        frameAnimation = SnapsAnimationHandler.startFrameAnimation(getContext(), imageView, SnapsFrameAnimationResFactory.eSnapsFrameAnimation.IMAGE_SELECT_NEW_PINCH);
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
                 if (closeLintener != null)
                    closeLintener.close();
             }
         });

        super.cancel();
    }

    public interface CloseListener {
        void close();
    }
    
    public static GifTutorialView createGifView(Context context, SnapsTutorialAttribute attribute){
        return new GifTutorialView(context,attribute);
    }

    public static GifTutorialView createGifView(Context context, SnapsTutorialAttribute attribute, CloseListener closeListener){
        return new GifTutorialView(context,attribute,closeListener);
    }

    private static final int HANDLE_MSG_PINCH_ZOOM_AND_DRAG_TUTORIAL_CHECK = 0;
    private static final int HANDLE_MSG_CLOSE_TUTORIAL_CHECK = 1;

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case HANDLE_MSG_PINCH_ZOOM_AND_DRAG_TUTORIAL_CHECK:
                checkZoomAndDragTutorialChange();
                break;
            case HANDLE_MSG_CLOSE_TUTORIAL_CHECK:
                forceCancelGifTutorialView();
                break;
        }
    }

    private void checkZoomAndDragTutorialChange() {
        if(!isStopped && attribute != null && attribute.getGifType() == PINCH_ZOOM_AND_DRAG && Const_PRODUCT.isMultiImageSelectProduct()) {
            startDragAnimation();
        }
    }

    private void forceCancelGifTutorialView() {
        if (isStopped) return;
        try {
            stopTimer();
            GifTutorialView.this.cancel();

            sendCloseMsg();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    private void sendCloseMsg() {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                if(closeLintener != null){
                    closeLintener.close();
                }
            }
        });
    }
}
