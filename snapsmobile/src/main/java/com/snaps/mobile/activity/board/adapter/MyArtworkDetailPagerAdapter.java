package com.snaps.mobile.activity.board.adapter;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.image.ImageDirectLoader;
import com.snaps.common.utils.imageloader.ImageLoader;
import com.snaps.common.utils.net.xml.bean.Xml_MyArtworkDetail.MyArtworkDetail;
import com.snaps.common.utils.system.ViewUnbindHelper;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.board.MyArtworkDetailActivity;

import java.io.File;
import java.util.List;

public class MyArtworkDetailPagerAdapter extends PagerAdapter {
    MyArtworkDetailActivity detailAct;
    List<MyArtworkDetail> myartworkDetailList;

    int srcImgThumbSize = -1;
    int type = 0; // 0:default 1:sample 2:preview

    public MyArtworkDetailPagerAdapter(MyArtworkDetailActivity detailAct, int type) {
        this.detailAct = detailAct;
        if (detailAct.myartworkDetail != null)
            myartworkDetailList = detailAct.myartworkDetail.myartworkDetail;
        srcImgThumbSize = UIUtil.getScreenHeight(detailAct);// UIUtil.getCalcMyartworkWidth(detailAct);
        this.type = type;
    }

    @Override
    public Object instantiateItem(View pager, final int position) {// 뷰페이저에서 사용할 뷰객체 생성/등록
        // 페이징화면 생성
        LayoutInflater inflater = LayoutInflater.from(detailAct);
        View view = inflater.inflate(R.layout.activity_myartworkdetail_item, null);

        ImageView imgMyartworkDetail = (ImageView) view.findViewById(R.id.imgMyartworkDetail);
        ProgressBar progressImg = (ProgressBar) view.findViewById(R.id.progressImg);

        if (type == 0) {
            String imgUrl = myartworkDetailList.get(position).source;

            String prefix = Const_VALUE.PATH_MYARTWORK_DETAIL(detailAct, detailAct.projCode);
            ImageDirectLoader.loadImage(imgUrl, imgMyartworkDetail, srcImgThumbSize, prefix, progressImg);
        }// 샘플뷰 인경우
        else if (type == 1) {
            String imgUrl = detailAct.themeBookPageThumbnailPaths.get(position);
            String prefix = Const_VALUE.PATH_MYARTWORK_DETAIL(detailAct, detailAct.projCode);
            ImageDirectLoader.loadImage(imgUrl, imgMyartworkDetail, srcImgThumbSize, prefix, progressImg);
        }// 미리보기 인경우...
        else if (type == 2) {
            String filePath = detailAct.themeBookPageThumbnailPaths.get(position);
            ImageLoader.with(detailAct).load(new File(filePath)).into(imgMyartworkDetail);
        }

        ((ViewPager) pager).addView(view, 0); // 뷰 페이저에 추가
        return view;
    }

    @Override
    public int getCount() {
        if (type == 1 || type == 2)
            return detailAct.themeBookPageThumbnailPaths.size();
        else
            return myartworkDetailList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(View pager, int position, Object view) {// 뷰 객체 삭제.
        ((ViewPager) pager).removeView((View) view);
        ViewUnbindHelper.unbindReferences((View) view);
        view = null;
    }
}
