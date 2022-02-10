package com.snaps.mobile.edit_activity_tools.customview;

/**
 * Created by ysjeong on 16. 4. 19..
 */

import android.content.Context;
import android.graphics.Rect;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import com.snaps.common.utils.constant.Const_PRODUCT;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.edit_activity_tools.utils.EditActivityThumbnailUtils;

public class EditActivityThumbnailSpacingDecoration extends RecyclerView.ItemDecoration {
    private Context context;
    private int spaceLeft;
    private int spaceTop;
    private int spaceRight;
    private int spaceBottom;

    private boolean isLandscapeMode = false;

    private Rect mThumbnailDimensRect = null;

    public EditActivityThumbnailSpacingDecoration(Context context) {
        this.context = context;
    }

    public void setLandscapeMode(boolean isLandscapeMode) {
        this.isLandscapeMode = isLandscapeMode;
    }

    public void setItemDimensRect(Rect rect) {
        mThumbnailDimensRect = rect;
    }

    public void setSpaceOffset() {
        int commonMargin = UIUtil.convertDPtoPX(context, 7);
        if (EditActivityThumbnailUtils.isCalendarVerticalProduct()) {
            if (isLandscapeMode) {
                setSpace(commonMargin, commonMargin, 0, commonMargin);
            } else {
                //달력 세로형과 미니는 5개 보이게 한다.
                int itemW = mThumbnailDimensRect != null ? mThumbnailDimensRect.width() : UIUtil.convertDPtoPX(context, 39);
                int screenWidth = UIUtil.getScreenWidth(context);
                int spcing = (screenWidth - itemW * 5) / 6;
                setSpace(commonMargin, commonMargin, spcing, commonMargin);
            }
        } else if (Const_PRODUCT.isPackageProduct()) {
            if (isLandscapeMode) {
                setSpace(commonMargin, commonMargin, commonMargin, commonMargin);
            } else {
                setSpace(commonMargin, commonMargin, commonMargin, commonMargin);
            }
        } else {
            if (isLandscapeMode) {
               // setSpace(commonMargin, commonMargin, commonMargin, UIUtil.convertDPtoPX(context, 25));
                if(Const_PRODUCT.isNewYearsCardProduct()|| Const_PRODUCT.isStikerGroupProduct() && !Const_PRODUCT.isNameStickerProduct()) {
                    setSpace(UIUtil.convertDPtoPX(context, 0), UIUtil.convertDPtoPX(context, 0),UIUtil.convertDPtoPX(context, 13), UIUtil.convertDPtoPX(context, 0));
                } else if(Const_PRODUCT.isNameStickerProduct() ) {
                    setSpace(UIUtil.convertDPtoPX(context, 0), UIUtil.convertDPtoPX(context, 0),UIUtil.convertDPtoPX(context, 0), UIUtil.convertDPtoPX(context, 0));
                } else if(Const_PRODUCT.isCardProduct() || Const_PRODUCT.isSloganProduct()) {
                    setSpace(UIUtil.convertDPtoPX(context, 0), UIUtil.convertDPtoPX(context, 0),UIUtil.convertDPtoPX(context, 0), UIUtil.convertDPtoPX(context, 0));
                } else if(Const_PRODUCT.isAccordionCardProduct()) {
                    setSpace(UIUtil.convertDPtoPX(context, 0), UIUtil.convertDPtoPX(context, 7),UIUtil.convertDPtoPX(context, 0), UIUtil.convertDPtoPX(context, 0));
                }else if(Const_PRODUCT.isPosterGroupProduct()) {
                    setSpace(UIUtil.convertDPtoPX(context, 0), UIUtil.convertDPtoPX(context, 7),UIUtil.convertDPtoPX(context, 13), UIUtil.convertDPtoPX(context, 16));
                }else if(Const_PRODUCT.isBabyNameStikerGroupProduct()){
                    int height = UIUtil.getCurrentScreenHeight(context) - UIUtil.convertDPtoPX(context,48);
                    int itemHeight = UIUtil.convertDPtoPX(context,202);
                    int emptyHeight = height - itemHeight;
                    emptyHeight = emptyHeight/2;
                    setSpace(UIUtil.convertDPtoPX(context, 0), emptyHeight,UIUtil.convertDPtoPX(context, 0), UIUtil.convertDPtoPX(context, 0));
                } else {
                    setSpace(UIUtil.convertDPtoPX(context, 13), UIUtil.convertDPtoPX(context, 11),UIUtil.convertDPtoPX(context, 13), UIUtil.convertDPtoPX(context, 16));
                }

            } else {
//                setSpace(commonMargin, commonMargin, commonMargin, commonMargin);
                if(Const_PRODUCT.isNewYearsCardProduct() || Const_PRODUCT.isStikerGroupProduct() ) {
                    setSpace(UIUtil.convertDPtoPX(context, 0), UIUtil.convertDPtoPX(context, 11),UIUtil.convertDPtoPX(context, 16), UIUtil.convertDPtoPX(context, 11));
                } else if(Const_PRODUCT.isCardProduct() || Const_PRODUCT.isSloganProduct()) {
                    setSpace(UIUtil.convertDPtoPX(context, 0), UIUtil.convertDPtoPX(context, 0),UIUtil.convertDPtoPX(context, 0), UIUtil.convertDPtoPX(context, 0));
                } else if(Const_PRODUCT.isAccordionCardProduct()) {
                    setSpace(UIUtil.convertDPtoPX(context, 6), UIUtil.convertDPtoPX(context, 0),UIUtil.convertDPtoPX(context, 0), UIUtil.convertDPtoPX(context, 0));
                } else if(Const_PRODUCT.isPosterGroupProduct() ) {
                    setSpace(UIUtil.convertDPtoPX(context, 6), UIUtil.convertDPtoPX(context, 11),UIUtil.convertDPtoPX(context, 16), UIUtil.convertDPtoPX(context, 10));
                } else if(Const_PRODUCT.isBabyNameStikerGroupProduct()){
                    int wdith = UIUtil.getCurrentScreenWidth(context);
                    int itemWdith = UIUtil.convertDPtoPX(context,224);
                    int emptyWdith = wdith - itemWdith;
                    emptyWdith = emptyWdith/2;
                    setSpace( emptyWdith, UIUtil.convertDPtoPX(context, 0),UIUtil.convertDPtoPX(context, 0), UIUtil.convertDPtoPX(context, 0));
                } else{
                    setSpace(UIUtil.convertDPtoPX(context, 20), UIUtil.convertDPtoPX(context, 11),UIUtil.convertDPtoPX(context, 16), UIUtil.convertDPtoPX(context, 11));
                }


            }
        }
    }

    private void setSpace(int l, int t, int r, int b) {
        this.spaceLeft = l;
        this.spaceTop = t;
        this.spaceRight = r;
        this.spaceBottom = b;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view,
                               RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view); // item position
        if (isLandscapeMode) {
            if (position == 0) {
                outRect.top = spaceTop;
            }

            outRect.left = spaceLeft;
            outRect.bottom = spaceBottom;
        }
        else {
            if (position == 0) {
                outRect.left = spaceLeft;
            }
            outRect.top = spaceTop;
            outRect.right = spaceRight;
        }
    }
}
