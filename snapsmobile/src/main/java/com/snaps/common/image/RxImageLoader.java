package com.snaps.common.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.PictureDrawable;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.snaps.common.utils.constant.SnapsAPI;
import com.snaps.common.utils.imageloader.ImageLoader;
import com.snaps.mobile.activity.edit.skin.SnapsSkinRequestAttribute;
import com.snaps.mobile.activity.edit.skin.SnapsSkinUtil;
import com.snaps.mobile.activity.ui.menu.renewal.GlideApp;
import com.snaps.mobile.activity.ui.menu.renewal.SvgSoftwareLayerSetter;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

public class RxImageLoader {

    private static final String TAG = RxImageLoader.class.getSimpleName();

    public Single<Bitmap> loadImageRx(Context context, String requestURL, int requestWidth, int requestHeight) {
        return Single.fromCallable(() ->
                GlideApp.with(context)
                        .asBitmap()
                        .load(requestURL)
                        .skipMemoryCache(false)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .override(requestWidth, requestHeight)
                        .submit()
                        .get())
                .subscribeOn(Schedulers.io());
    }

    public Single<Bitmap> loadSVGImage(Context context, String requestURL, int requestWidth, int requestHeight) {
        return Single.fromCallable(() -> GlideApp.with(context)
                .as(PictureDrawable.class)
                .listener(new SvgSoftwareLayerSetter())
                .skipMemoryCache(false)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .load(requestURL)
                .override(requestWidth, requestHeight)
                .submit().get())
                .subscribeOn(Schedulers.io())
                .map(pictureDrawable -> {
                    Bitmap bitmap = Bitmap.createBitmap(pictureDrawable.getIntrinsicWidth(), pictureDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(bitmap);
                    canvas.drawPicture(pictureDrawable.getPicture());
                    return bitmap;
                });
    }

    /**
     * 버그 수정 (Ben)
     * 이유는 모르겠으나 SVG파일의 가로/세로 비율과 템플릿에서 사진 영역의 가로/세로 비율이 현재 다름 (다른게 정상인지 비정상인지 모르겠음...)
     *
     * 예) 클리어 하이브리드 (iPhone12 - PHOTO FULL)
     * http://m.snaps.kr/Upload/Data1/Resource/scene_mask/phone_case/iphone-12.svg <- SVG ( width="234" height="506" )
     * requestWidth : 440
     * requestHeight : 984
     * SVG 가로/세로 비율 = 234 / 506 = 0.4624505928853755
     * requestWidth / requestHeight = 440 / 984 = 0.4471544715447154
     *
     * 테플릿을 보면 : http://m.snaps.kr/servlet/Command.do?part=mall.smartphotolite.SmartPhotoLiteInterface&cmd=getCalendarTmplMulti&prmProdCode=00802900010015&prmTmplCode=045021023086
     * <sceneMask x="22" y="24" width="220" height="492" angle="0" alpha="1" id="sm_iphone_12" resourceURL="/Upload/Data1/Resource/scene_mask/phone_case/iphone-12.svg"/>
     * width, height 값이 SVG랑 다른데....
     *
     *
     * 아무튼 그래서 SVG를 bitmap으로 로드하면 이미지 위아래에 공백 부분이 발생!! (정확히 말해면 svg 가로 세로 비율을 유지한채로 확대되므로 위아래에 투명 영역이 생김)
     * Glide에서 옵션 바꿔서 위아래 공백없이 로드하려고 했는데 안돼!!! (정확히 말하면 내가 몰라)
     * 그래서 단순한 방법 사용 : SVG 원본 크기로 로딩해서 비트맵 만들고 요청한 크기로 이미지 잡아 늘림. 끝~ ^^;;;
     *
     * 마빈과 함께 원인 찾아보니 템플릿이 잘못 된 것이 맞음
     */
    public Single<Bitmap> loadSVGImage2(Context context, String requestURL, int requestWidth, int requestHeight) {
        return Single.fromCallable(() -> GlideApp.with(context)
                .as(PictureDrawable.class)
                .skipMemoryCache(false)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .load(requestURL)
                .submit().get())
                .subscribeOn(Schedulers.io())
                .map(pictureDrawable -> {
                    Bitmap bitmap = Bitmap.createBitmap(pictureDrawable.getIntrinsicWidth(), pictureDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(bitmap);
                    canvas.drawPicture(pictureDrawable.getPicture());

                    Bitmap resizeBitmap = Bitmap.createScaledBitmap(bitmap, requestWidth, requestHeight, false);
                    return resizeBitmap;
                });
    }

    public Single<Bitmap> getBitmapSkin(Context context, int maskSize, String gradientFilePath) {
        SnapsSkinRequestAttribute requestSkinBuilder = new SnapsSkinRequestAttribute.Builder()
                .setContext(context)
                .setRequestWidth(maskSize)
                .setRequestHeight(maskSize)
                .setResourceFileName(gradientFilePath)
                .create();

        return SnapsSkinUtil.getSkinImageRx(requestSkinBuilder);
    }

}
