package com.snaps.mobile.tutorial.new_tooltip_tutorial;

import android.content.Context;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.snaps.mobile.R;

/**
 * Created by kimduckwon on 2017. 9. 22..
 */

public class HandAnimationView extends LinearLayout {
    private View view = null;
    private ImageView imageViewHand = null;
    private ImageView imageViewArrowLeft = null;
    private ImageView imageViewArrowRight = null;
    private ImageView imageViewArrowUp = null;
    private ImageView imageViewArrowDown = null;
    private Animation handAnimation = null;
    private Animation arrowAnimation =null;
    private PLAY_TYPE playType;

    enum PLAY_TYPE {
        START,LEFT,RIGHT,UP,DOWN,END
    }

    public HandAnimationView(Context context) {
        super(context);

    }

    public HandAnimationView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    private void initImageViewHand(Context context) {
        if (context == null || imageViewHand != null) return;
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.hand_animation_view,null);
        imageViewHand = (ImageView) view.findViewById(R.id.imageViewHand);
        addView(view);
    }

    public void startAnimation(Context context){
        initImageViewHand(context);

        imageViewArrowLeft =  (ImageView) view.findViewById(R.id.imageViewLeft);
        imageViewArrowRight = (ImageView) view.findViewById(R.id.imageViewRight);
        imageViewArrowUp =  (ImageView) view.findViewById(R.id.imageViewUp);
        imageViewArrowDown = (ImageView) view.findViewById(R.id.imageViewDown);

        handAnimation =  AnimationUtils.loadAnimation(getContext(),R.anim.move_hand_start_animation);
        arrowAnimation =  AnimationUtils.loadAnimation(getContext(),R.anim.move_arrow_left_animation);
        handAnimation.setAnimationListener(animationListener);

        imageViewHand.startAnimation(handAnimation);
        imageViewArrowLeft.setVisibility(View.VISIBLE);
        imageViewArrowLeft.startAnimation(arrowAnimation);
        playType = PLAY_TYPE.START;

    }
    public void startAnimationUpAndDown(){

    }

    Animation.AnimationListener animationListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {

            switch (playType){
                case START:
                    handAnimation =  AnimationUtils.loadAnimation(getContext(),R.anim.move_hand_right_animation);
                    arrowAnimation = AnimationUtils.loadAnimation(getContext(),R.anim.move_arrow_right_animation);
                    if(!imageViewArrowRight.isShown()){
                        imageViewArrowRight.setVisibility(View.VISIBLE);
                    }
                    imageViewArrowRight.startAnimation(arrowAnimation);
                    playType =PLAY_TYPE.RIGHT;
                    break;
                case RIGHT:
                    handAnimation =  AnimationUtils.loadAnimation(getContext(),R.anim.move_hand_left_animation);
                    playType =PLAY_TYPE.LEFT;
                    break;
                case LEFT:
                    handAnimation =  AnimationUtils.loadAnimation(getContext(),R.anim.move_hand_up_animation);
                    arrowAnimation = AnimationUtils.loadAnimation(getContext(),R.anim.move_arrow_up_animation);
                    if(!imageViewArrowUp.isShown()){
                        imageViewArrowUp.setVisibility(View.VISIBLE);
                    }
                    imageViewArrowUp.startAnimation(arrowAnimation);
                    playType =PLAY_TYPE.UP;
                    break;
                case UP:
                    handAnimation =  AnimationUtils.loadAnimation(getContext(),R.anim.move_hand_down_animation);
                    arrowAnimation = AnimationUtils.loadAnimation(getContext(),R.anim.move_arrow_down_animation);
                    if(!imageViewArrowDown.isShown()){
                        imageViewArrowDown.setVisibility(View.VISIBLE);
                    }
                    imageViewArrowDown.startAnimation(arrowAnimation);
                    playType =PLAY_TYPE.DOWN;
                    break;
                case DOWN:
                    handAnimation =  AnimationUtils.loadAnimation(getContext(),R.anim.move_hand_end_animation);
                    playType =PLAY_TYPE.END;
                    break;
                case END:
                    handAnimation =  AnimationUtils.loadAnimation(getContext(),R.anim.move_hand_start_animation);
                    arrowAnimation =  AnimationUtils.loadAnimation(getContext(),R.anim.move_arrow_left_animation);
                    if(!imageViewArrowLeft.isShown()){
                        imageViewArrowLeft.setVisibility(View.VISIBLE);
                    }
                    imageViewArrowLeft.startAnimation(arrowAnimation);
                    playType =PLAY_TYPE.START;
                    break;
            }
            handAnimation.setAnimationListener(animationListener);
            imageViewHand.startAnimation(handAnimation);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    };

}
