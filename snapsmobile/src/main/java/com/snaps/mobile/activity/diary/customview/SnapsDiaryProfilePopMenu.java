package com.snaps.mobile.activity.diary.customview;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.diary.SnapsDiaryDataManager;

/**
 * Created by ysjeong on 16. 4. 5..
 */
public class SnapsDiaryProfilePopMenu extends RelativeLayout {

    public interface ISnapsDiaryProfilePopMenuListener {
        void onProfilePopMenuClick(int position);
    }

    private ISnapsDiaryProfilePopMenuListener listener = null;

    private Context context = null;
    private Rect popoverLayoutRect = null;
    private Rect targetViewRect = null;

    private LinearLayout popMenuLy = null;
    private RelativeLayout rootView = null;

    private TextView tvSelPhoto = null;
    private TextView tvDefPhoto = null;
    private View lineView = null;

    private boolean isShowing = false;

    public SnapsDiaryProfilePopMenu(Context context) {
        super(context);
        init(context);
    }

    public SnapsDiaryProfilePopMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SnapsDiaryProfilePopMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void setPopMenuListener(ISnapsDiaryProfilePopMenuListener listener) {
        this.listener = listener;
    }

    private void init(Context context) {
        this.context = context;
        popoverLayoutRect = new Rect();
        targetViewRect = new Rect();

        LayoutInflater inflater =
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.snaps_diary_profile_pop_menu, null, false);

        popMenuLy = (LinearLayout) view.findViewById(R.id.snaps_diary_mission_state_bar_thumbnail_popmenu_ly);

        this.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dissmiss();
            }
        });

        tvSelPhoto = (TextView) view.findViewById(R.id.snaps_diary_mission_state_bar_thumbnail_select_photo);
        tvDefPhoto = (TextView) view.findViewById(R.id.snaps_diary_mission_state_bar_thumbnail_default);

        lineView = view.findViewById(R.id.snaps_diary_mission_state_bar_thumbnail_default_line);

        SnapsDiaryDataManager dataManager = SnapsDiaryDataManager.getInstance();
        if (dataManager.isExistUserTumbnail()) {
            tvSelPhoto.setBackgroundResource(R.drawable.pop_menu_bg_red01);
            tvDefPhoto.setVisibility(View.VISIBLE);
            lineView.setVisibility(View.VISIBLE);
        } else {
            tvSelPhoto.setBackgroundResource(R.drawable.pop_menu_bg_red_rounded);
            tvDefPhoto.setVisibility(View.GONE);
            lineView.setVisibility(View.GONE);
        }

        tvSelPhoto.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dissmiss();
                if(listener != null)
                    listener.onProfilePopMenuClick(0);
            }
        });

        tvDefPhoto.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dissmiss();
                if(listener != null)
                    listener.onProfilePopMenuClick(1);
            }
        });

        addView(view);
    }

    private void checkThumbnailState() {
        SnapsDiaryDataManager dataManager = SnapsDiaryDataManager.getInstance();
        if (dataManager.isExistUserTumbnail()) {
            tvSelPhoto.setBackgroundResource(R.drawable.pop_menu_bg_red01);
            tvDefPhoto.setVisibility(View.VISIBLE);
            lineView.setVisibility(View.VISIBLE);
        } else {
            tvSelPhoto.setBackgroundResource(R.drawable.pop_menu_bg_red_rounded);
            tvDefPhoto.setVisibility(View.GONE);
            lineView.setVisibility(View.GONE);
        }
    }

    public void showPopMenu(RelativeLayout rootView, View targetView) {
        dissmiss();
        setIsShowing(true);

        checkThumbnailState();

        targetView.getGlobalVisibleRect(targetViewRect);

        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) popMenuLy.getLayoutParams();
        lp.leftMargin = targetViewRect.left;
        lp.topMargin = targetViewRect.bottom - UIUtil.convertDPtoPX(context, 18);
        popMenuLy.setLayoutParams(lp);

        this.rootView = rootView;

        rootView.addView(this);
        rootView.invalidate();
    }

    public void dissmiss() {
        if (!isShowing()) return;
        setIsShowing(false);
        if(rootView != null)
            rootView.removeView(this);
    }

    public boolean isShowing() {
        return isShowing;
    }

    public void setIsShowing(boolean isShowing) {
        this.isShowing = isShowing;
    }
}
