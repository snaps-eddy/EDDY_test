package com.snaps.mobile.activity.book;

import android.content.Context;

import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.structure.SnapsTemplate;
import com.snaps.common.structure.control.SnapsClipartControl;
import com.snaps.common.structure.control.SnapsControl;
import com.snaps.common.structure.control.SnapsLayoutControl;
import com.snaps.common.structure.control.SnapsTextControl;
import com.snaps.common.structure.page.SnapsPage;
import com.snaps.common.utils.constant.Const_PRODUCT;
import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.constant.Const_VALUES;
import com.snaps.common.utils.constant.SnapsAPI;
import com.snaps.common.utils.imageloader.filters.ImageEffectBitmap;
import com.snaps.common.utils.imageloader.recoders.AdjustableCropInfo;
import com.snaps.common.utils.imageloader.recoders.CropInfo;
import com.snaps.common.utils.pref.Setting;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.mobile.activity.book.SNSBookRecorder.SNSBookInfo;
import com.snaps.mobile.activity.diary.SnapsDiaryConstants;
import com.snaps.mobile.activity.diary.SnapsDiaryDataManager;
import com.snaps.mobile.activity.diary.recoder.SnapsDiaryPublishItem;
import com.snaps.mobile.activity.diary.recoder.SnapsDiaryUserInfo;
import com.snaps.mobile.utils.ui.CalcViewRectUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Created by songhw on 2016. 3. 31..
 */
public class SnapsDiaryDrawManager {
    public static final int PROGRESS_GET_DATA = 0;
    public static final int PROGRESS_GET_TEMPLATE = 1;
    public static final int PROGRESS_GET_DRAW_COVER = 2;
    public static final int PROGRESS_GET_DRAW_A_PAGE = 3;
    public static final int PROGRESS_GET_DOWNLOAD_FONT = 4;
    public static final float[] PROGRESS_ARRAY = new float[]{10, 10, 3, 3, 3};

    private ArrayList<SnapsDiaryPublishItem> diaryList;

    private Context context;
    private SNSBookInfo popupInfo;
    private ProgressListener progressListener;
    private SnapsPage normalPage, endPage;

    private int[] feelingPos = new int[4];
    private int[] weatherPos = new int[4];
    private SnapsClipartControl happy, normal, joyjul, thankful, unhappy, sad, angry, tired;
    private SnapsClipartControl sunny, cloudy, windy, rainy, snowy, yellowdust, thunder, hazy;

    private int totalPage;

    private float maxProgress = 0f;
    private float curProgress = 0f;

    public interface ProgressListener {
        void updateProgress(int per);

        int getProgress();
    }

    public SnapsDiaryDrawManager(Context context) {
        this.context = context;
    }

    public void makePage(SnapsTemplate template) {
        if (template != null && template.getPages() != null) {
            for (int i = 0; i < template.getPages().size(); ++i) {
                if (i > PageType.END_PAGE.getIndex()) {
                    template.getPages().remove(i);
                    i--;
                } else if (i == PageType.PAGES.getIndex()) normalPage = template.getPages().get(i);
                else if (i == PageType.END_PAGE.getIndex()) endPage = template.getPages().get(i);
            }

            if (endPage != null) template.getPages().remove(PageType.END_PAGE.getIndex());
            if (normalPage != null) template.getPages().remove(PageType.PAGES.getIndex());
        }

        drawPage(template);
        drawCover(template);
        drawTitle(template);

        for (int i = 0; i < template.getPages().size(); ++i)
            template.getPages().get(i).setTextControlFont(Const_PRODUCT.AURATEXT_BASIC_RATION);
    }

    private void drawPage(SnapsTemplate template) {
        if (diaryList == null || diaryList.isEmpty()) return;

        SnapsPage tempPage = null;
        for (int i = 0; i < diaryList.size(); ++i) {
            if (SNSBookInfo.getPageCount(template.getPages().size()) > SNSBookFragmentActivity.LIMIT_MAX_PAGE_COUNT - 1) {
                getInfo().setMaxPageEdited(true);

                diaryList.remove(i);
                i--;
                continue;
            }

            if (tempPage == null) {
                if (template.getPages().size() * 2 > SNSBookFragmentActivity.LIMIT_MAX_PAGE_COUNT - 2)
                    break;

                tempPage = i == diaryList.size() - 1 ? endPage.copyPage(PageType.END_PAGE.getIndex()) : normalPage.copyPage(PageType.PAGES.getIndex());
                drawItem(tempPage, diaryList.get(i), true);
            } else {
                drawItem(tempPage, diaryList.get(i), false);
                template.getPages().add(tempPage);
                tempPage = null;
            }
            addProgress(SnapsDiaryDrawManager.PROGRESS_GET_DRAW_A_PAGE);
        }

        if (tempPage != null) template.getPages().add(tempPage);
    }

    /***
     * 책등 텍스트를 설정하는 함수..
     *
     * @param coverPage
     */
    private SnapsTextControl setCoverSpineText(SnapsPage coverPage) {
        // 텍스트..
        for (SnapsControl t : coverPage.getTextControlList()) {
            SnapsTextControl tControl = (SnapsTextControl) t;
            if (tControl != null && tControl.format.verticalView.equals("true")) {
                SnapsDiaryDataManager dataManager = SnapsDiaryDataManager.getInstance();
                tControl.text = StringUtil.getFormattedDateString(dataManager.getStartDate(), "yyyyMMdd", "yyyy. MM. dd") + " - " + StringUtil.getFormattedDateString(dataManager.getEndDate(), "yyyyMMdd", "yyyy. MM. dd");
                return tControl;
            }
        }
        return null;
    }

    private void drawCover(SnapsTemplate template) {
        SnapsDiaryDataManager dataManager = SnapsDiaryDataManager.getInstance();
        SnapsPage cover = template.getPages().get(PageType.COVER.getIndex());

        // 커버 이미지 편집 가능하도록
        for (SnapsControl c : cover.getLayerLayouts()) {
            if (c instanceof SnapsLayoutControl && c.getSnsproperty().equals("thumbnail")) {
                SnapsLayoutControl control = (SnapsLayoutControl) c;
                control.isSnsBookCover = true;
            }
        }

        template.setSNSBookStick(setCoverSpineText(cover));

        // 텍스트.
        SnapsTextControl temp;
        ArrayList<SnapsControl> textControlList = cover.getTextControlList();
        String tempDate, tempY, tempM, tempD;

        int count = 0;
        int tempLongDate;
        Set<Integer> dateSet = new HashSet<Integer>();
        for (int j = 0; j < diaryList.size(); ++j) {
            tempLongDate = Integer.parseInt(diaryList.get(j).getDate());
            if (!dateSet.contains(tempLongDate)) {
                dateSet.add(tempLongDate);
                count++;
            }
        }

        for (int i = 0; i < textControlList.size(); ++i) {
            temp = (SnapsTextControl) textControlList.get(i);
            if ("period".equals(temp.getSnsproperty())) {
                if ("YYYY_MM_DD - YYYY_MM_DD".equals(temp.getFormat()) || "yyyy_MM_dd - yyyy_MM_dd".equals(temp.getFormat()))
                    temp.setText(StringUtil.getFormattedDateString(dataManager.getStartDate(), "yyyyMMdd", "yyyy_MM_dd") + " - " + StringUtil.getFormattedDateString(dataManager.getEndDate(), "yyyyMMdd", "yyyy_MM_dd"));
                else if ("YYYY. MM. DD - YYYY. MM. DD".equals(temp.getFormat()) || "yyyy. MM. dd - yyyy. MM. dd".equals(temp.getFormat()))
                    temp.setText(StringUtil.getFormattedDateString(dataManager.getStartDate(), "yyyyMMdd", "yyyy. MM. dd") + " - " + StringUtil.getFormattedDateString(dataManager.getEndDate(), "yyyyMMdd", "yyyy. MM. dd"));
                else if ("dd Mmm yyyy - dd Mmm yyyy".equals(temp.getFormat()))
                    temp.setText(StringUtil.getFormattedDateString(dataManager.getStartDate(), "yyyyMMdd", "dd MMM yyyy", Locale.ENGLISH) + " - " + StringUtil.getFormattedDateString(dataManager.getEndDate(), "yyyyMMdd", "dd MMM yyyy", Locale.ENGLISH));
                else if ("dd MMM yyyy - dd MMM yyyy".equals(temp.getFormat()))
                    temp.setText(StringUtil.getFormattedDateString(dataManager.getStartDate(), "yyyyMMdd", "dd MMM yyyy", Locale.ENGLISH).toUpperCase() + " - " + StringUtil.getFormattedDateString(dataManager.getEndDate(), "yyyyMMdd", "dd MMM yyyy", Locale.ENGLISH).toUpperCase());
                else if ("d d  M M M  y y y y  -  d d  M M M  y y y y".equals(temp.getFormat()))
                    temp.setText(putSpace(StringUtil.getFormattedDateString(dataManager.getStartDate(), "yyyyMMdd", "dd MMM yyyy", Locale.ENGLISH).toUpperCase() + " - " + StringUtil.getFormattedDateString(dataManager.getEndDate(), "yyyyMMdd", "dd MMM yyyy", Locale.ENGLISH).toUpperCase()));
                else if ("yyyy. mm. dd - yyyy. mm. dd / dd days".equals(temp.getFormat()) || "yyyy. MM. dd - yyyy. MM. dd / dd days".equals(temp.getFormat()))
                    temp.setText(StringUtil.getFormattedDateString(dataManager.getStartDate(), "yyyyMMdd", "yyyy. MM. dd") + " - " + StringUtil.getFormattedDateString(dataManager.getEndDate(), "yyyyMMdd", "yyyy. MM. dd") + " / " + count + " days");
            } else if ("startdate".equals(temp.getSnsproperty())) {
                if ("YYYY/MM/DD".equals(temp.getFormat()) || "yyyy/MM/dd".equals(temp.getFormat()))
                    temp.setText(StringUtil.getFormattedDateString(dataManager.getStartDate(), "yyyyMMdd", "yyyy/MM/dd"));
                else if ("dd Mmm yyyy".equals(temp.getFormat()))
                    temp.setText(StringUtil.getFormattedDateString(dataManager.getStartDate(), "yyyyMMdd", "dd MMM yyyy", Locale.ENGLISH));
                else if ("d d  M M M  y y y y".equals(temp.getFormat()))
                    temp.setText(putSpace(StringUtil.getFormattedDateString(dataManager.getStartDate(), "yyyyMMdd", "dd MMM yyyy", Locale.ENGLISH).toUpperCase()));
                else if ("y y y y m m d d".equals(temp.getFormat()) || "y y y y M M d d".equals(temp.getFormat()))
                    temp.setText(putSpace(dataManager.getStartDate()));
                else if ("yyyy mm dd".equals(temp.getFormat()) || "yyyy MM dd".equals(temp.getFormat()))
                    temp.setText(StringUtil.getFormattedDateString(dataManager.getStartDate(), "yyyyMMdd", "yyyy MM dd"));
                else if ("yyyy. mm. dd".equals(temp.getFormat()) || "yyyy. MM. dd".equals(temp.getFormat()))
                    temp.setText(StringUtil.getFormattedDateString(dataManager.getStartDate(), "yyyyMMdd", "yyyy. MM. dd"));
                else if ("dd Month yyyy".equals(temp.getFormat()) || "d MMMM yyyy".equals(temp.getFormat()))
                    temp.setText(StringUtil.getFormattedDateString(dataManager.getStartDate(), "yyyyMMdd", "d MMMM yyyy", Locale.ENGLISH));

            } else if ("enddate".equals(temp.getSnsproperty())) {
                if ("YYYY/MM/DD".equals(temp.getFormat()) || "yyyy/MM/dd".equals(temp.getFormat()))
                    temp.setText(StringUtil.getFormattedDateString(dataManager.getEndDate(), "yyyyMMdd", "yyyy/MM/dd"));
                else if ("dd Mmm yyyy".equals(temp.getFormat()))
                    temp.setText(StringUtil.getFormattedDateString(dataManager.getEndDate(), "yyyyMMdd", "dd MMM yyyy", Locale.ENGLISH));
                else if ("d d  M M M  y y y y".equals(temp.getFormat()))
                    temp.setText(putSpace(StringUtil.getFormattedDateString(dataManager.getEndDate(), "yyyyMMdd", "dd MMM yyyy", Locale.ENGLISH).toUpperCase()));
                else if ("y y y y m m d d".equals(temp.getFormat()) || "y y y y M M d d".equals(temp.getFormat()))
                    temp.setText(putSpace(dataManager.getEndDate()));
                else if ("yyyy mm dd".equals(temp.getFormat()) || "yyyy MM dd".equals(temp.getFormat()))
                    temp.setText(StringUtil.getFormattedDateString(dataManager.getEndDate(), "yyyyMMdd", "yyyy MM dd"));
                else if ("yyyy. mm. dd".equals(temp.getFormat()) || "yyyy. MM. dd".equals(temp.getFormat()))
                    temp.setText(StringUtil.getFormattedDateString(dataManager.getEndDate(), "yyyyMMdd", "yyyy. MM. dd"));
                else if ("dd Month yyyy".equals(temp.getFormat()) || "d MMMM yyyy".equals(temp.getFormat()))
                    temp.setText(StringUtil.getFormattedDateString(dataManager.getEndDate(), "yyyyMMdd", "d MMMM yyyy", Locale.ENGLISH));
            } else if ("totalpost".equals(temp.getSnsproperty())) {
                String countStr = "" + count;
                if ("dd.".equals(temp.getFormat())) countStr += ".";
                else if ("dd days".equals(temp.getFormat())) countStr += " days";
                temp.setText(countStr);
            }
        }

        // 썸네일
        ArrayList<SnapsControl> imageList = cover.getLayoutListByProperty("thumbnail");
        Collections.sort(imageList, new Comparator<SnapsControl>() {
            @Override
            public int compare(SnapsControl lhs, SnapsControl rhs) {
                return Integer.parseInt(lhs.regValue) > Integer.parseInt(rhs.regValue) ? 1 : Integer.parseInt(lhs.regValue) < Integer.parseInt(rhs.regValue) ? -1 : 0;
            }
        });

        if (imageList != null && imageList.size() > 0) {
            SnapsLayoutControl tempImgControl;
            int index;
            for (int i = 0; i < imageList.size(); ++i) {
                index = diaryList.size() - 1 - i;
                if (index < 0) break;

                if (diaryList.get(index).getArrPhotoImageDatas().isEmpty()) continue; //TODO  이미지가 없으면 안되는데?

                tempImgControl = (SnapsLayoutControl) imageList.get(i);
                tempImgControl.imgData = new MyPhotoSelectImageData();
                tempImgControl.imgData.set(diaryList.get(index).getArrPhotoImageDatas().get(0));
                tempImgControl.imgData.cropRatio = tempImgControl.getRatio();
                tempImgControl.imagePath = tempImgControl.imgData.ORIGINAL_PATH;
                tempImgControl.thumPath = tempImgControl.imgData.THUMBNAIL_PATH;
                tempImgControl.imageLoadType = tempImgControl.imgData.KIND;
                initImageEditInfo(tempImgControl);
            }
        }
    }

    public static final String[][] defaultProfileUrl = {
            {"/Upload/Data1/Resource/sticker/Mprint/Pst23_jj.png", "/Upload/Data1/Resource/sticker/Mdp/Dst23_jj.jpg"},
            {"/Upload/Data1/Resource/sticker/Mprint/Pst24_jj.png", "/Upload/Data1/Resource/sticker/Mdp/Dst24_jj.jpg"},
            {"/Upload/Data1/Resource/sticker/Mprint/Pst25_jj.png", "/Upload/Data1/Resource/sticker/Mdp/Dst25_jj.jpg"},
            {"/Upload/Data1/Resource/sticker/Mprint/Pst26_jj.png", "/Upload/Data1/Resource/sticker/Mdp/Dst26_jj.jpg"},
            {"/Upload/Data1/Resource/sticker/Mprint/Pst27_jj.png", "/Upload/Data1/Resource/sticker/Mdp/Dst27_jj.jpg"},
            {"/Upload/Data1/Resource/sticker/Mprint/Pst28_jj.png", "/Upload/Data1/Resource/sticker/Mdp/Dst28_jj.jpg"}
    };

    private void drawTitle(SnapsTemplate template) {
        SnapsDiaryDataManager dataManager = SnapsDiaryDataManager.getInstance();
        SnapsPage title = template.getPages().get(PageType.TITLE.getIndex());

        // 텍스트.
        SnapsTextControl tempText;
        ArrayList<SnapsControl> textControlList = title.getTextControlList();
        for (int i = 0; i < textControlList.size(); ++i) {
            tempText = (SnapsTextControl) textControlList.get(i);
            if ("period".equals(tempText.getSnsproperty()) && ("YYYY. MM. DD - YYYY. MM. DD".equals(tempText.getFormat()) || "yyyy. MM. dd - yyyy. MM. dd".equals(tempText.getFormat())))
                tempText.setText(StringUtil.getFormattedDateString(dataManager.getStartDate(), "yyyyMMdd", "yyyy. MM. dd") + " - " + StringUtil.getFormattedDateString(dataManager.getEndDate(), "yyyyMMdd", "yyyy. MM. dd"));
            else if ("totalpost".equals(tempText.getSnsproperty())) {
                int count = 0;
                int tempLongDate;
                Set<Integer> dateSet = new HashSet<Integer>();
                for (int j = 0; j < diaryList.size(); ++j) {
                    tempLongDate = Integer.parseInt(diaryList.get(j).getDate());
                    if (!dateSet.contains(tempLongDate)) {
                        dateSet.add(tempLongDate);
                        count++;
                    }
                }

                String countStr = "" + count;
                if ("dd.".equals(tempText.getFormat())) countStr += ".";
                else if ("dd days".equals(tempText.getFormat())) countStr += " days";
                tempText.setText(countStr);
            }
        }


        // 썸네일
        ArrayList<SnapsControl> imageList = title.getLayoutListByProperty("thumbnail");
        Collections.sort(imageList, new Comparator<SnapsControl>() {
            @Override
            public int compare(SnapsControl lhs, SnapsControl rhs) {
                return Integer.parseInt(lhs.regValue) > Integer.parseInt(rhs.regValue) ? 1 : Integer.parseInt(lhs.regValue) < Integer.parseInt(rhs.regValue) ? -1 : 0;
            }
        });

        if (imageList != null && imageList.size() > 0) {
            SnapsLayoutControl tempImgControl;
            int index;
            for (int i = 0; i < imageList.size(); ++i) {
                index = diaryList.size() - 1 - i;
                if (index < 0) break;
                tempImgControl = (SnapsLayoutControl) imageList.get(i);

                if (diaryList.get(index).getArrPhotoImageDatas().isEmpty()) continue;

                tempImgControl.imgData = new MyPhotoSelectImageData();
                tempImgControl.imgData.set(diaryList.get(index).getArrPhotoImageDatas().get(0));
                tempImgControl.imagePath = tempImgControl.imgData.ORIGINAL_PATH;
                tempImgControl.thumPath = tempImgControl.imgData.THUMBNAIL_PATH;
                tempImgControl.imageLoadType = tempImgControl.imgData.KIND;
                initImageEditInfo(tempImgControl);

            }
        }

        String path = "", thumbPath = "";

        path = dataManager.getSnapsDiaryUserInfo() == null ? "" : dataManager.getSnapsDiaryUserInfo().getThumbnailPath();
        if (!StringUtil.isEmpty(path)) thumbPath = path;
        else {
            int selectedIndex = (int) (Math.random() * defaultProfileUrl.length);
            if (selectedIndex > defaultProfileUrl.length - 1 || selectedIndex < 0)
                selectedIndex = 0;
            path = defaultProfileUrl[selectedIndex][0];
            thumbPath = defaultProfileUrl[selectedIndex][1];

            popupInfo.setThumbUrl(SnapsAPI.DOMAIN() + path);
        }

        // 프로필
        SnapsControl temp = title.getLayoutByProperty("profile");
        if (temp != null) {
            SnapsLayoutControl profile = (SnapsLayoutControl) temp;

            profile.imgData = new MyPhotoSelectImageData();
            profile.imgData.KIND = Const_VALUES.SELECT_SNAPS;
            profile.imgData.PATH = SnapsAPI.DOMAIN() + path;
            profile.imgData.THUMBNAIL_PATH = SnapsAPI.DOMAIN() + thumbPath;
            profile.imagePath = profile.imgData.PATH;
            profile.thumPath = profile.imgData.THUMBNAIL_PATH;
            profile.imageLoadType = profile.imgData.KIND;
            initImageEditInfo(profile);
        }

        // best 날씨, 기분
        SnapsClipartControl tempClip;
        ArrayList<SnapsControl> clipList = title.getClipartControlList();
        for (int i = 0; i < clipList.size(); ++i) {
            temp = clipList.get(i);
            if (temp instanceof SnapsClipartControl && "bestfeeling".equals(temp.getSnsproperty())) {
                tempClip = getBestFeeling();
                if (tempClip == null) title.removeSticker((SnapsClipartControl) temp);
                else ((SnapsClipartControl) temp).resourceURL = tempClip.resourceURL;
            } else if (temp instanceof SnapsClipartControl && "bestweather".equals(temp.getSnsproperty())) {
                tempClip = getBestWeather();
                if (tempClip == null) title.removeSticker((SnapsClipartControl) temp);
                else ((SnapsClipartControl) temp).resourceURL = tempClip.resourceURL;
            }
        }
    }

    //사진 편집정보를 제거하는 함수
    private void initImageEditInfo(SnapsLayoutControl layoutControl) {
        //고객이 회전시킨건 초기화 exif angle은 유지.
        //layoutControl.imgData.ROTATE_ANGLE_THUMB == -1 경우 고객이
        layoutControl.imgData.ROTATE_ANGLE = layoutControl.imgData.ROTATE_ANGLE - ((layoutControl.imgData.ROTATE_ANGLE_THUMB == -1) ? 0 : layoutControl.imgData.ROTATE_ANGLE_THUMB);
        layoutControl.imgData.ROTATE_ANGLE_THUMB = 0;
        //이전 편집정보를 제거한다.
        layoutControl.imgData.CROP_INFO = new CropInfo();
        layoutControl.imgData.ADJ_CROP_INFO = new AdjustableCropInfo();
        layoutControl.imgData.isAdjustableCropMode = false;
        layoutControl.imgData.isApplyEffect = false;
        layoutControl.imgData.EFFECT_TYPE = ImageEffectBitmap.EffectType.ORIGIN.toString();

        layoutControl.angle = layoutControl.imgData.ROTATE_ANGLE + "";
    }


    private SnapsClipartControl getBestFeeling() {
        HashMap<Integer, Integer> countMap = new HashMap<Integer, Integer>();
        int index = -1, currentCount = 0, tempIndex;
        SnapsDiaryConstants.eFeeling feeling;
        for (int i = 0; i < diaryList.size(); ++i) {
            feeling = diaryList.get(i).getFeels();
            if (feeling == null || SnapsDiaryConstants.eFeeling.NONE.equals(feeling)) continue;
            index = Integer.parseInt(feeling.getCode());
            currentCount = countMap.containsKey(index) ? countMap.get(index) : 0;
            countMap.put(index, ++currentCount);
        }

        Set set = countMap.entrySet();
        Iterator iterator = set.iterator();
        currentCount = 0;
        String bestIndex = "";
        Map.Entry pair;
        while (iterator.hasNext()) {
            pair = (Map.Entry) iterator.next();
            if (currentCount < 1 || currentCount < (int) pair.getValue()) {
                currentCount = (int) pair.getValue();
                bestIndex = (int) pair.getKey() + "";
            }
        }

        if (bestIndex.equals(SnapsDiaryConstants.eFeeling.ANGRY.getCode()))
            return angry.copyClipartControl();
        else if (bestIndex.equals(SnapsDiaryConstants.eFeeling.FUNNY.getCode()))
            return joyjul.copyClipartControl();
        else if (bestIndex.equals(SnapsDiaryConstants.eFeeling.HAPPY.getCode()))
            return happy.copyClipartControl();
        else if (bestIndex.equals(SnapsDiaryConstants.eFeeling.MISFORTUNE.getCode()))
            return unhappy.copyClipartControl();
        else if (bestIndex.equals(SnapsDiaryConstants.eFeeling.NO_FEELING.getCode()))
            return normal.copyClipartControl();
        else if (bestIndex.equals(SnapsDiaryConstants.eFeeling.SAD.getCode()))
            return sad.copyClipartControl();
        else if (bestIndex.equals(SnapsDiaryConstants.eFeeling.THANKS.getCode()))
            return thankful.copyClipartControl();
        else if (bestIndex.equals(SnapsDiaryConstants.eFeeling.TIRED.getCode()))
            return tired.copyClipartControl();
        else return null;
    }

    private SnapsClipartControl getBestWeather() {
        HashMap<Integer, Integer> countMap = new HashMap<Integer, Integer>();
        int index = -1, currentCount = 0, tempIndex;
        SnapsDiaryConstants.eWeather weather;
        for (int i = 0; i < diaryList.size(); ++i) {
            weather = diaryList.get(i).getWeather();
            if (weather == null || SnapsDiaryConstants.eWeather.NONE.equals(weather)) continue;
            index = Integer.parseInt(weather.getCode());
            currentCount = countMap.containsKey(index) ? countMap.get(index) : 0;
            countMap.put(index, ++currentCount);
        }

        Set set = countMap.entrySet();
        Iterator iterator = set.iterator();
        currentCount = 0;
        String bestIndex = "";
        Map.Entry pair;
        while (iterator.hasNext()) {
            pair = (Map.Entry) iterator.next();
            if (currentCount < 1 || currentCount < (int) pair.getValue()) {
                currentCount = (int) pair.getValue();
                bestIndex = (int) pair.getKey() + "";
            }
        }

        if (bestIndex.equals(SnapsDiaryConstants.eWeather.CLOUDY.getCode()))
            return cloudy.copyClipartControl();
        else if (bestIndex.equals(SnapsDiaryConstants.eWeather.DUST_STORM.getCode()))
            return yellowdust.copyClipartControl();
        else if (bestIndex.equals(SnapsDiaryConstants.eWeather.FOG.getCode()))
            return hazy.copyClipartControl();
        else if (bestIndex.equals(SnapsDiaryConstants.eWeather.LIGHTNING.getCode()))
            return thunder.copyClipartControl();
        else if (bestIndex.equals(SnapsDiaryConstants.eWeather.RAINY.getCode()))
            return rainy.copyClipartControl();
        else if (bestIndex.equals(SnapsDiaryConstants.eWeather.SNOWY.getCode()))
            return snowy.copyClipartControl();
        else if (bestIndex.equals(SnapsDiaryConstants.eWeather.SUNSHINE.getCode()))
            return sunny.copyClipartControl();
        else if (bestIndex.equals(SnapsDiaryConstants.eWeather.WIND.getCode()))
            return windy.copyClipartControl();
        else return null;
    }

    private void drawItem(SnapsPage page, SnapsDiaryPublishItem item, boolean drawLeft) {
        if (item == null || item.getTemplate() == null || item.getTemplate().getPages() == null || item.getTemplate().getPages().size() < 1)
            return;

        // 기분, 날씨
        if (drawLeft) saveAndClearClips(page);
        SnapsClipartControl feeling = null, weather = null;

        if (SnapsDiaryConstants.eFeeling.ANGRY.equals(item.getFeels()))
            feeling = angry.copyClipartControl();
        else if (SnapsDiaryConstants.eFeeling.FUNNY.equals(item.getFeels()))
            feeling = joyjul.copyClipartControl();
        else if (SnapsDiaryConstants.eFeeling.HAPPY.equals(item.getFeels()))
            feeling = happy.copyClipartControl();
        else if (SnapsDiaryConstants.eFeeling.MISFORTUNE.equals(item.getFeels()))
            feeling = unhappy.copyClipartControl();
        else if (SnapsDiaryConstants.eFeeling.NO_FEELING.equals(item.getFeels()))
            feeling = normal.copyClipartControl();
        else if (SnapsDiaryConstants.eFeeling.SAD.equals(item.getFeels()))
            feeling = sad.copyClipartControl();
        else if (SnapsDiaryConstants.eFeeling.THANKS.equals(item.getFeels()))
            feeling = thankful.copyClipartControl();
        else if (SnapsDiaryConstants.eFeeling.TIRED.equals(item.getFeels()))
            feeling = tired.copyClipartControl();

        if (SnapsDiaryConstants.eWeather.CLOUDY.equals(item.getWeather()))
            weather = cloudy.copyClipartControl();
        else if (SnapsDiaryConstants.eWeather.DUST_STORM.equals(item.getWeather()))
            weather = yellowdust.copyClipartControl();
        else if (SnapsDiaryConstants.eWeather.FOG.equals(item.getWeather()))
            weather = hazy.copyClipartControl();
        else if (SnapsDiaryConstants.eWeather.LIGHTNING.equals(item.getWeather()))
            weather = thunder.copyClipartControl();
        else if (SnapsDiaryConstants.eWeather.RAINY.equals(item.getWeather()))
            weather = rainy.copyClipartControl();
        else if (SnapsDiaryConstants.eWeather.SNOWY.equals(item.getWeather()))
            weather = snowy.copyClipartControl();
        else if (SnapsDiaryConstants.eWeather.SUNSHINE.equals(item.getWeather()))
            weather = sunny.copyClipartControl();
        else if (SnapsDiaryConstants.eWeather.WIND.equals(item.getWeather()))
            weather = windy.copyClipartControl();

        // 타입 구분하기 위해 날씨와 기분 위치를 비교. 타입이 많아지면 템플릿 번호로 구분하도록 변경.
        boolean AType = Math.abs(happy.getIntY() - cloudy.getIntY()) < 10;

        if (feeling != null) {
            if (!AType || weather != null) { // A타입은 날씨가 없으면 기분을 날씨 위치로 이동 B타입은 원래 위치로.
                feeling.x = feelingPos[drawLeft ? 0 : 2] + "";
                feeling.y = feelingPos[drawLeft ? 1 : 3] + "";
            } else {
                feeling.x = weatherPos[drawLeft ? 0 : 2] + "";
                feeling.y = weatherPos[drawLeft ? 1 : 3] + "";
            }
            page.addControl(feeling);
        }

        if (weather != null) {
            weather.x = weatherPos[drawLeft ? 0 : 2] + "";
            weather.y = weatherPos[drawLeft ? 1 : 3] + "";
            page.addControl(weather);
        }

        // 사진
        SnapsLayoutControl guideImageControl = (SnapsLayoutControl) page.getLayoutByRegData("user_image", drawLeft ? "0" : "1");
        if (guideImageControl == null) return;
        ArrayList<SnapsLayoutControl> imgControlList = item.getTemplate().getPages().get(0).getLayoutListByRegData("user_image", null);

        int[] originArea = getImageArea(item.getTemplate().getPages().get(0));
        int[] targetArea = new int[]{guideImageControl.getIntX(), guideImageControl.getIntY(), guideImageControl.getIntWidth(), guideImageControl.getIntHeight()};
        ArrayList<SnapsLayoutControl> newControlList = convertControls(imgControlList, originArea, targetArea);
        for (int i = 0; i < newControlList.size(); ++i) page.addLayout(newControlList.get(i));

        // text
        SnapsTextControl temp;
        String content;
        ArrayList<SnapsControl> textControlList = page.getTextControlList();
        int divideX = page.getWidth() / 2;
        for (int i = 0; i < textControlList.size(); ++i) {
            temp = (SnapsTextControl) textControlList.get(i);
            if ((drawLeft && "0".equals(temp.regValue)) || (!drawLeft && "1".equals(temp.regValue))) {
                if ("postdate".equals(temp.getSnsproperty())) {
                    if ("요일".equals(temp.getFormat()))
                        temp.setText(StringUtil.getFormattedDateString(item.getDate(), "yyyyMMdd", "E") + "요일");
                    else if ("_요일".equals(temp.getFormat()))
                        temp.setText("_" + StringUtil.getFormattedDateString(item.getDate(), "yyyyMMdd", "E") + "요일");
                    else if ("YYYY. M. D".equals(temp.getFormat()) || "yyyy. M. d".equals(temp.getFormat()))
                        temp.setText(StringUtil.getFormattedDateString(item.getDate(), "yyyyMMdd", "yyyy. M. d"));
                    else if ("yyyy년 mm월 dd일".equals(temp.getFormat()) || "yyyy년 M월 d일".equals(temp.getFormat()))
                        temp.setText(StringUtil.getFormattedDateString(item.getDate(), "yyyyMMdd", "yyyy년 M월 d일"));
                    else if ("yyyy. mm. dd. DAY".equals(temp.getFormat()) || "yyyy. M. d. DAY".equals(temp.getFormat()))
                        temp.setText(StringUtil.getFormattedDateString(item.getDate(), "yyyyMMdd", "yyyy. M. d. EEE", Locale.ENGLISH).toUpperCase());
                } else if ("text".equals(temp.getSnsproperty())) {
                    content = item.getContents();
                    if (!StringUtil.isEmpty(content)) {
                        if( !content.contains("\n") ) content += "\n ";
                        CalcViewRectUtil.makeLineText(temp, new ArrayList<String>(Arrays.asList(content.split("\n"))), Float.parseFloat(temp.format.fontSize));
                        temp.setText(content);
                        // 두개중 첫번째 타입(선미선임 작업분) 구분하기 위함. 타입이 많아지면 템플릿 번호로 구분하도록 변경.
                        temp.lineSpcing = AType ? 2.5f : 2.3f;
                    }

                }
            }
        }
    }

    private void saveAndClearClips(SnapsPage page) {
        ArrayList<SnapsControl> clipControlList = page.getClipartControlList();
        SnapsClipartControl temp;
        int type;
        final int TYPE_NONE = 0;
        final int TYPE_FEELING = 1;
        final int TYPE_WEATHER = 2;
        for (int i = clipControlList.size() - 1; i > -1; --i) {
            temp = (SnapsClipartControl) clipControlList.get(i);
            type = TYPE_NONE;
            switch (temp.getSnsproperty()) {
                case "sticker_happy":
                    type = TYPE_FEELING;
                    happy = temp.copyClipartControl();
                    break;
                case "sticker_normal":
                    type = TYPE_FEELING;
                    normal = temp.copyClipartControl();
                    break;
                case "sticker_joyful":
                    type = TYPE_FEELING;
                    joyjul = temp.copyClipartControl();
                    break;
                case "sticker_thankful":
                    type = TYPE_FEELING;
                    thankful = temp.copyClipartControl();
                    break;
                case "sticker_unhappy":
                    type = TYPE_FEELING;
                    unhappy = temp.copyClipartControl();
                    break;
                case "sticker_sad":
                    type = TYPE_FEELING;
                    sad = temp.copyClipartControl();
                    break;
                case "sticker_angry":
                    type = TYPE_FEELING;
                    angry = temp.copyClipartControl();
                    break;
                case "sticker_tired":
                    type = TYPE_FEELING;
                    tired = temp.copyClipartControl();
                    break;

                case "sticker_sunny":
                    type = TYPE_WEATHER;
                    sunny = temp.copyClipartControl();
                    break;
                case "sticker_thunder":
                    type = TYPE_WEATHER;
                    thunder = temp.copyClipartControl();
                    break;
                case "sticker_snowy":
                    type = TYPE_WEATHER;
                    snowy = temp.copyClipartControl();
                    break;
                case "sticker_cloudy":
                    type = TYPE_WEATHER;
                    cloudy = temp.copyClipartControl();
                    break;
                case "sticker_windy":
                    type = TYPE_WEATHER;
                    windy = temp.copyClipartControl();
                    break;
                case "sticker_rainy":
                    type = TYPE_WEATHER;
                    rainy = temp.copyClipartControl();
                    break;
                case "sticker_yellowdust":
                    type = TYPE_WEATHER;
                    yellowdust = temp.copyClipartControl();
                    break;
                case "sticker_hazy":
                    type = TYPE_WEATHER;
                    hazy = temp.copyClipartControl();
                    break;
            }

            if (type == TYPE_FEELING) {
                feelingPos[temp.getIntX() < page.getWidth() / 2 ? 0 : 2] = temp.getIntX();
                feelingPos[temp.getIntX() < page.getWidth() / 2 ? 1 : 3] = temp.getIntY();
            } else if (type == TYPE_WEATHER) {
                weatherPos[temp.getIntX() < page.getWidth() / 2 ? 0 : 2] = temp.getIntX();
                weatherPos[temp.getIntX() < page.getWidth() / 2 ? 1 : 3] = temp.getIntY();
            }

            if (type != TYPE_NONE) page.removeSticker(temp);
        }
    }

    private class PositionFromTo {
        int start, end;

        public PositionFromTo(int start, int end) {
            this.start = start;
            this.end = end;
        }
    }

    private boolean isInArea(ArrayList<PositionFromTo> area, PositionFromTo pos) {
        for (int i = 0; i < area.size(); ++i)
            if (pos.start > area.get(i).start - 1 && pos.end < area.get(i).end + 1) return false;
        return true;
    }

    private ArrayList<PositionFromTo> rearrangeArea(ArrayList<PositionFromTo> origin) {
        ArrayList<PositionFromTo> newAry = (ArrayList<PositionFromTo>) origin.clone();
        boolean processDone = false;
        while (!processDone) {
            processDone = true;
            for (int i = 0; i < newAry.size(); ++i) {
                for (int j = 0; j < newAry.size(); ++j) {
                    if (i != j && ((newAry.get(i).start < newAry.get(j).start + 1 && newAry.get(i).end > newAry.get(j).start - 1) || (newAry.get(i).start + 1 > newAry.get(j).start && newAry.get(i).start < newAry.get(j).end + 1))) {
                        if (newAry.get(i).end != newAry.get(j).end || newAry.get(i).start != newAry.get(j).start)
                            newAry.add(new PositionFromTo(Math.min(newAry.get(i).start, newAry.get(j).start), Math.max(newAry.get(i).end, newAry.get(j).end)));
                        newAry.remove(i);
                        i--;
                        processDone = false;
                        break;
                    }
                }
            }
        }
        return newAry;
    }

    // 이미지들을 모두 체크하여 변환 전의 이미지 영역을 구한다.
    private int[] getImageArea(SnapsPage page) {
        int width = 0, height = 0, x = 1000, y = 1000;
        if (page != null) {
            ArrayList<SnapsLayoutControl> imgControlList = (ArrayList<SnapsLayoutControl>) page.getLayoutListByRegData("user_image", null);
            if (imgControlList != null) {
                for (int i = 0; i < imgControlList.size(); ++i) {
                    x = Math.min(imgControlList.get(i).getIntX(), x);
                    y = Math.min(imgControlList.get(i).getIntY(), y);
                }

                for (int i = 0; i < imgControlList.size(); ++i) {
                    width = Math.max(imgControlList.get(i).getIntX() + imgControlList.get(i).getIntWidth() - x, width);
                    height = Math.max(imgControlList.get(i).getIntY() + imgControlList.get(i).getIntHeight() - y, height);
                }
            }
        }
        return new int[]{x, y, width, height};
    }

    private boolean fillCheckArea(int[] area, int start, int end) {
        boolean isOutside = false;
        for (int i = start; i < end; ++i) {
            if (area[i] == 0) {
                isOutside = true;
                area[i] = 1;
            }
        }
        return isOutside;
    }

    private ArrayList<SnapsLayoutControl> convertControls(ArrayList<SnapsLayoutControl> origin, int[] originArea, int[] targetArea) {
        // 변환할때 인트로 변환하면서 픽셀단위로 어긋나는 경우가 생겨 변경
        // 6X6 36개의 영역의 시작 끝 좌표로 영역을 정의
        final int FIX_RANGE = 5;

        int[][] pos = new int[origin.size()][4]; // [start x, start y, end x, end y]
        int[][] count = new int[origin.size()][2]; // [count x, count y]
        boolean[] flag;

        ArrayList<SnapsLayoutControl> list = new ArrayList<SnapsLayoutControl>();
        int lastItemIndex;
        for (int i = 0; i < origin.size(); ++i) {
            list.add(origin.get(i).copyImageControl());

            flag = new boolean[4];
            for (int j = 0; j < 6; ++j) {
                if (!flag[0] && origin.get(i).getIntX() < originArea[2] / 6 * j + FIX_RANGE) {
                    pos[i][0] = j;
                    flag[0] = true;
                }

                if (!flag[1] && origin.get(i).getIntY() < originArea[3] / 6 * j + FIX_RANGE) {
                    pos[i][1] = j;
                    flag[1] = true;
                }

                if (!flag[2] && origin.get(i).getIntWidth() + origin.get(i).getIntX() < originArea[2] / 6 * (j + 1) + FIX_RANGE) {
                    pos[i][2] = j + 1;
                    flag[2] = true;
                }

                if (!flag[3] && origin.get(i).getIntHeight() + origin.get(i).getIntY() < originArea[3] / 6 * (j + 1) + FIX_RANGE) {
                    pos[i][3] = j + 1;
                    flag[3] = true;
                }

                if (flag[0] && flag[1] && flag[2] && flag[3]) break;
            }
        }

        int totalW, totalH, remainW, remainH, x, y, w, h;//, temp1, temp2;
        int[][] checked;
        for (int i = 0; i < list.size(); ++i) {
            checked = new int[2][6];
            for (int j = 0; j < list.size(); ++j) {
                if (pos[j][1] < pos[i][3] && pos[j][3] > pos[i][1] && fillCheckArea(checked[0], pos[j][0], pos[j][2]))
                    count[i][0]++;
                if (pos[j][0] < pos[i][2] && pos[j][2] > pos[i][0] && fillCheckArea(checked[1], pos[j][1], pos[j][3]))
                    count[i][1]++;
            }

            totalW = targetArea[2] - count[i][0] + 1;
            totalH = targetArea[3] - count[i][1] + 1;
            int divW = (count[i][0] == 2 && pos[i][2] - pos[i][0] != 3) ? 3 : count[i][0];
            int divH = (count[i][1] == 2 && pos[i][3] - pos[i][1] != 3) ? 3 : count[i][1]; // 행, 열의 갯수가 2개지만 크기는 다를때 카운트로 remain값을 계산하면 안맞는 경우가 있어 추가.
            remainW = totalW % divW;
            remainH = totalH % divH;

            x = targetArea[0];
            y = targetArea[1];
            w = totalW;
            h = totalH;

            if (count[i][0] == 2 && pos[i][2] - pos[i][0] != 3)
                w = w * ((pos[i][2] - pos[i][0]) / 2) / 3;
            else w /= count[i][0];
            if (count[i][1] == 2 && pos[i][3] - pos[i][1] != 3)
                h = h * ((pos[i][3] - pos[i][1]) / 2) / 3;
            else h /= count[i][1];

            if (pos[i][0] == 0) { // 첫번째 아이템일때
                if (count[i][0] == remainW) w++; // 갯수와 남은 픽셀수가 같으면 처음 아이템부터 추가
            } else { // 첫번째 아이템이 아닐때
                if (count[i][0] > 2) { // 3개일때
                    if (pos[i][0] > 3) { // 3번째일때
                        x += totalW / 3 * 2 + (remainW > 1 ? 3 : 2);
                        if (remainW > 0) w++;
                    } else {
                        x += totalW / 3 + 1;
                        if (remainW > 1) w++;
                    }
                } else { // 2개일때
                    if (pos[i][0] == 3) x += totalW / 2 + 1; // 1/2
                    else if (pos[i][0] < 3) x += totalW / 3 + (totalW % 3 == 0 ? 0 : 1); // 1/3
                    else x += totalW / 3 * 2 + (totalW % 3 == 0 ? 0 : 1); // 2/3

                    if (remainW > 0) w++;
                }

                if (pos[i][2] > 5 && x + w < targetArea[0] + targetArea[2]) { // 마지막 아이템
                    lastItemIndex = -1;
                    for (int j = 0; j < i; ++j) {
                        if (pos[j][1] < pos[i][1] + 1 && pos[j][3] > pos[i][3] - 1) { // 같은 행을 공유하는 마지막 놈을 찾음
                            lastItemIndex = j;
                        }
                    }

                    // 보정
                    if (lastItemIndex > -1) {
                        x = list.get(lastItemIndex).getIntX() + list.get(lastItemIndex).getIntWidth() + 1;
                    }
                    w = targetArea[0] + targetArea[2] - x;
                }
            }

            if (pos[i][1] == 0) { // 첫번째 아이템일때
                if (count[i][1] == remainH) h++; // 갯수와 남은 픽셀수가 같으면 처음 아이템부터 추가
            } else { // 첫번째 아이템이 아닐때
                if (count[i][1] > 2) { // 3개일때
                    if (pos[i][1] > 3) { // 3번째일때
                        y += totalH / 3 * 2 + (remainH > 1 ? 3 : 2);
                        if (remainH > 0) h++;
                    } else {
                        y += totalH / 3 + 1;
                        if (remainH > 1) h++;
                    }
                } else { // 2개일때
                    if (pos[i][1] == 3) y += totalH / 2 + 1; // 1/2
                    else if (pos[i][1] < 3) y += totalH / 3 + (totalH % 3 == 0 ? 0 : 1); // 1/3
                    else y += totalH / 3 * 2 + (totalH % 3 == 0 ? 0 : 1); // 2/3

                    if (remainH > 0) h++;
                }

                if (pos[i][3] > 5 && y + h < targetArea[1] + targetArea[3]) { // 마지막 아이템
                    lastItemIndex = -1;
                    for (int j = 0; j < i; ++j) {
                        if (pos[j][0] < pos[i][0] + 1 && pos[j][2] > pos[i][2] - 1) { // 같은 행을 공유하는 마지막 놈을 찾음
                            lastItemIndex = j;
                        }
                    }

                    // 보정
                    if (lastItemIndex > -1) {
                        y = list.get(lastItemIndex).getIntY() + list.get(lastItemIndex).getIntHeight() + 1;
                    }
                    h = targetArea[1] + targetArea[3] - y;
                }
            }

            list.get(i).x = x + "";
            list.get(i).y = y + "";
            list.get(i).width = w + "";
            list.get(i).height = h + "";
        }

        return list;
    }

    public void setDiaryList(ArrayList<SnapsDiaryPublishItem> list) {
        this.diaryList = list;
    }

    public void createInfo(Context context) {
        SnapsDiaryUserInfo userInfo = SnapsDiaryDataManager.getInstance().getSnapsDiaryUserInfo();
        popupInfo = new SNSBookInfo();
        popupInfo.setThumbUrl(userInfo == null ? "" : SnapsAPI.DOMAIN() + userInfo.getThumbnailPath());
        popupInfo.setPeriod(getFormattedStartDateString("yyyy.MM.dd") + " ~ " + getFormattedEndDateString("yyyy.MM.dd"));
        popupInfo.setPageCount(totalPage + "");

        String name = Setting.getString(context, Const_VALUE.KEY_USER_INFO_USER_NAME);
        if (name.length() > 4) name = name.substring(0, 4) + "...";
        popupInfo.setUserName(name);
    }

    public SNSBookInfo getInfo() {
        if (popupInfo != null) popupInfo.setPageCount(totalPage + "");
        return popupInfo;
    }

    public String putSpace(String origin) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < origin.length(); ++i) {
            if (i != 0) sb.append(" ");
            sb.append(origin.charAt(i));
        }
        return sb.toString();
    }

    public String getFormattedStartDateString(String format) {
        return StringUtil.getFormattedDateString(SnapsDiaryDataManager.getInstance().getStartDate(), "yyyyMMdd", format);
    }

    public String getFormattedEndDateString(String format) {
        return StringUtil.getFormattedDateString(SnapsDiaryDataManager.getInstance().getEndDate(), "yyyyMMdd", format);
    }

    public void setProgressListener(ProgressListener listener) {
        this.progressListener = listener;
    }

    public void initProgress(int pageCount) {
        maxProgress = PROGRESS_ARRAY[PROGRESS_GET_DATA] * (pageCount == 0 ? 1 : pageCount + 1) + PROGRESS_ARRAY[PROGRESS_GET_TEMPLATE] + PROGRESS_ARRAY[PROGRESS_GET_DRAW_COVER] + (PROGRESS_ARRAY[PROGRESS_GET_DRAW_A_PAGE] * pageCount / 2) + PROGRESS_ARRAY[PROGRESS_GET_DOWNLOAD_FONT];
        curProgress = 0f;
        updateProgress(0);
    }

    public void updateProgress(int per) {
        if (progressListener != null) progressListener.updateProgress(per);
    }

    public int getProgress() {
        return progressListener != null ? progressListener.getProgress() : 0;
    }

    public void addProgress(int type) {
        addProgress(type, 1);
    }

    public void addProgress(int type, int count) {
        float value = PROGRESS_ARRAY[type] * count;
        if (type == PROGRESS_GET_DRAW_A_PAGE) value = value / 2;
        curProgress += value;
        updateProgress(Math.min((int) (curProgress / maxProgress * 100), 100));
    }

    public void setTotalPage(int count) {
        this.totalPage = count;
    }

    public enum PageType {
        COVER(0), TITLE(1), PAGES(2), END_PAGE(3);

        private final int index;

        PageType(int index) {
            this.index = index;
        }

        // 객체 필드를 리턴함
        public int getIndex() {
            return index;
        }
    }
}
