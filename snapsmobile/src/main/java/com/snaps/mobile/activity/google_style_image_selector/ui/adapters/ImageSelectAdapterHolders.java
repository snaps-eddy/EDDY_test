package com.snaps.mobile.activity.google_style_image_selector.ui.adapters;

import android.app.Activity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.system.ViewUnbindHelper;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.google_style_image_selector.interfaces.ISnapsImageSelectConstants;
import com.snaps.mobile.activity.google_style_image_selector.ui.adapters.phone_strategies.GooglePhotoStyleAdapterStrategyBase;
import com.snaps.mobile.activity.google_style_image_selector.ui.custom.SquareRelativeLayout;
import com.snaps.mobile.activity.selectimage.adapter.GalleryCursorRecord;
import com.snaps.mobile.component.SnapsTrayLayoutView;

import font.FTextView;

import static com.snaps.mobile.R.id.imgSelectTrayLayoutView;

/**
 * Created by ysjeong on 2016. 12. 12..
 */

public abstract class ImageSelectAdapterHolders {
    private static final String TAG = ImageSelectAdapterHolders.class.getSimpleName();
    public static class TrayThumbnailItemHolder extends RecyclerView.ViewHolder {

        private RelativeLayout parentView;
        //        private View border;
        private TextView label;
        private SnapsTrayLayoutView trayThumbnail;
        private ImageView photoThumbnail;
        private ImageView selector;
        private ImageView deleteIcon;
        private ImageView noPrintIcon;
        private ImageView imgSelectTrayPlusBtn;
        private FTextView imgLabel;
        private FTextView selectorTextView;
        private View imageAreaOutline;

        public TrayThumbnailItemHolder(View itemView) {
            super(itemView);
            parentView = (RelativeLayout) itemView.findViewById(R.id.simple_grid_back);
            label = (TextView) itemView.findViewById(R.id.imgSelect_page_txt);
            trayThumbnail = (SnapsTrayLayoutView) itemView.findViewById(imgSelectTrayLayoutView);
            photoThumbnail = (ImageView) itemView.findViewById(R.id.imgSelect);
            selector = (ImageView) itemView.findViewById(R.id.imgSelector);
            deleteIcon = (ImageView) itemView.findViewById(R.id.imgDelete);
            noPrintIcon = (ImageView) itemView.findViewById(R.id.imgNoprint);
            imgSelectTrayPlusBtn = (ImageView) itemView.findViewById(R.id.imgSelectTrayPlusBtn);
            imgLabel = (FTextView) itemView.findViewById(R.id.imgLabel);
            selectorTextView = (FTextView) itemView.findViewById(R.id.imgSelectTextView);
            imageAreaOutline = itemView.findViewById(R.id.image_select_tray_thumbnail_single_item_image_area_outline);
        }

        public void removeDeleteIcon() {
            if (deleteIcon != null) {
                deleteIcon.setVisibility(View.GONE);
            }

            if (selector != null) {
                selector.setVisibility(View.GONE);
            }
        }

        public void showOutLine() {
            if (imageAreaOutline != null) {
                imageAreaOutline.setVisibility(View.VISIBLE);
            }
        }

        public void setHolderStateToDummyItem() {
            try {
                if (parentView == null) return;

                if (selectorTextView != null) {
                    selectorTextView.setBackgroundResource(R.drawable.img_tray_cover_empty_holder);
                    selectorTextView.setText(R.string.cover);
                    selectorTextView.setVisibility(View.VISIBLE);
                }

                if (imageAreaOutline != null) {
                    imageAreaOutline.setVisibility(View.GONE);
                }

                if (photoThumbnail != null)
                    photoThumbnail.setVisibility(View.GONE);

                if (parentView != null) {
                    parentView.setOnClickListener(null);
                }

                if (selector != null) {
                    selector.setVisibility(View.GONE);
                }

                if (noPrintIcon != null) {
                    noPrintIcon.setVisibility(View.GONE);
                }

                if (deleteIcon != null) {
                    deleteIcon.setVisibility(View.GONE);
                }

                if (imgLabel != null) {
                    imgLabel.setVisibility(View.GONE);
                }

                if (label != null) {
                    label.setVisibility(View.GONE);
                }

                ViewUnbindHelper.unbindReferences(photoThumbnail, null, false);
            } catch (Exception e) {
                Dlog.e(TAG, e);
            }
        }

        public FTextView getImgLabel() {
            return imgLabel;
        }

        public RelativeLayout getParentView() {
            return parentView;
        }

        public TextView getLabel() {
            return label;
        }

        public SnapsTrayLayoutView getTrayThumbnail() {
            return trayThumbnail;
        }

        public ImageView getSelector() {
            return selector;
        }

        public ImageView getDeleteIcon() {
            return deleteIcon;
        }

        public ImageView getNoPrintIcon() {
            return noPrintIcon;
        }

        public ImageView getImgSelectTrayPlusBtn() {
            return imgSelectTrayPlusBtn;
        }

        public FTextView getSelectorTextView() {
            return selectorTextView;
        }

        public ImageView getPhotoThumbnail() {
            return photoThumbnail;
        }
    }

    public static class TraySectionTitleItemHolder extends RecyclerView.ViewHolder {
        private TextView label;

        public TraySectionTitleItemHolder(View itemView) {
            super(itemView);
            label = (TextView) itemView.findViewById(R.id.img_select_tray_section_title_item_title_tv);
        }

        public TextView getLabel() {
            return label;
        }
    }

    public static class GooglePhotoStyleDepthYearsYearSectionHolder extends ImageSelectAdapterHolders.GooglePhotoStyleSectionHolder {
        public GooglePhotoStyleDepthYearsYearSectionHolder(View itemView) {
            super(itemView);
        }
    }

    public static class GooglePhotoStyleDepthYearsMonthSectionHolder extends ImageSelectAdapterHolders.GooglePhotoStyleSectionHolder {
        public GooglePhotoStyleDepthYearsMonthSectionHolder(View itemView) {
            super(itemView);
        }
    }

    public static class GooglePhotoStyleHeaderHolder extends RecyclerView.ViewHolder {
        public GooglePhotoStyleHeaderHolder(View itemView) {
            super(itemView);
        }
    }

    public static class GooglePhotoStyleSectionHolder extends PhotoFragmentItemHolder {
        private LinearLayout parentLayout;
        private CheckBox checkBox;
        private ImageButton imageSelect;
        private LinearLayout linearLayoutSelect;
        private TextView tvSectionTitle;
        private TextView tvSectionSub;
        private String groupId;

        public GooglePhotoStyleSectionHolder(View itemView) {
            super(itemView);
            parentLayout = (LinearLayout) itemView.findViewById(R.id.section_parent_layout);
            checkBox = (CheckBox) itemView.findViewById(R.id.chkbox_push_agree);
            imageSelect = (ImageButton) itemView.findViewById(R.id.imageButtonSelect);
            linearLayoutSelect = (LinearLayout) itemView.findViewById(R.id.linearLayoutSelect);
            tvSectionTitle = (TextView) itemView.findViewById(R.id.img_select_tray_section_title_item_title_tv);
            tvSectionSub = (TextView) itemView.findViewById(R.id.img_select_tray_section_title_item_sub_title_tv);
        }

        public TextView getTvSectionSub() {
            return tvSectionSub;
        }

        public void setTvSectionSub(TextView tvSectionSub) {
            this.tvSectionSub = tvSectionSub;
        }

        public LinearLayout getParentLayout() {
            return parentLayout;
        }

        public String getGroupId() {
            return groupId;
        }

        public void setGroupId(String groupId) {
            this.groupId = groupId;
        }

        public CheckBox getCheckBox() {
            return checkBox;
        }

        public ImageButton getImageSelect() {
            return imageSelect;
        }

        public LinearLayout getLinearLayoutSelect() {
            return linearLayoutSelect;
        }

        public TextView getTvSectionTitle() {
            return tvSectionTitle;
        }

        public void setTvSectionTitle(TextView tvSectionTitle) {
            this.tvSectionTitle = tvSectionTitle;
        }
    }

    public static class TraySectionLineItemHolder extends RecyclerView.ViewHolder {
        public TraySectionLineItemHolder(View itemView) {
            super(itemView);
        }
    }

    public static class AlbumListSelectorItemHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private TextView counter;
        private ImageView thumbnail;
        private View parentView;

        public AlbumListSelectorItemHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.img_select_album_list_selector_item_title_tv);
            counter = (TextView) itemView.findViewById(R.id.img_select_album_list_selector_item_count_tv);
            thumbnail = (ImageView) itemView.findViewById(R.id.img_select_album_list_selector_item_thumbnail_iv);
            parentView = itemView.findViewById(R.id.img_select_album_list_selector_item_parent_ly);
        }

        public TextView getTitle() {
            return title;
        }

        public TextView getCounter() {
            return counter;
        }

        public ImageView getThumbnail() {
            return thumbnail;
        }

        public View getParentView() {
            return parentView;
        }
    }

    public static class PhotoFragmentItemHolder extends RecyclerView.ViewHolder {

        private View rootView = null;
        private SquareRelativeLayout parentView;
        private ImageView thumbnail;
        private ImageView selector;
        private ImageView checkIcon;
        private ImageView noPrintIcon;

        private TextView content;
        private FTextView imgLabel;

        private boolean isDisableClick = false;

        private String mapKey;
        private MyPhotoSelectImageData imgData;
        private GalleryCursorRecord.PhonePhotoFragmentItem phonePhotoItem;

        private String osType;

        public PhotoFragmentItemHolder(View itemView) {
            super(itemView);

            rootView = itemView;
            parentView = (SquareRelativeLayout) itemView.findViewById(R.id.imgParent);
            thumbnail = (ImageView) itemView.findViewById(R.id.imgDetail);
            selector = (ImageView) itemView.findViewById(R.id.imgChoiceBgWhite);
            checkIcon = (ImageView) itemView.findViewById(R.id.imgChoiceBg);
            noPrintIcon = (ImageView) itemView.findViewById(R.id.imgFilterBg);
            content = (TextView) itemView.findViewById(R.id.txtDetail);
            imgLabel = (FTextView) itemView.findViewById(R.id.imgLabel);
        }

        /**
         * Twothumb 08.06
         * 뷰홀더 생성 전에 사이즈를 지정한 다음 뷰홀더 객체를 넘겨줘서
         * RecyclerView에서 ItemDecoration관련 처리를 못한거 같다.
         * - 사이즈가 고정된 뷰를 넘겨줘서
         */
        public PhotoFragmentItemHolder(View itemView, Activity activityV2, GooglePhotoStyleAdapterStrategyBase.AdapterAttribute attribute) {
            super(itemView);
            rootView = itemView;

            parentView = (SquareRelativeLayout) itemView.findViewById(R.id.imgParent);
            thumbnail = (ImageView) itemView.findViewById(R.id.imgDetail);
            selector = (ImageView) itemView.findViewById(R.id.imgChoiceBgWhite);
            checkIcon = (ImageView) itemView.findViewById(R.id.imgChoiceBg);
            noPrintIcon = (ImageView) itemView.findViewById(R.id.imgFilterBg);
            content = (TextView) itemView.findViewById(R.id.txtDetail);
            imgLabel = (FTextView) itemView.findViewById(R.id.imgLabel);

            /**
             * Twothumb 08.06
             * 핸드폰 화면에따라 column수 만큼 뷰홀더의 높이값 설정
             */
            boolean isLandscapeMode = attribute.isLandscapeMode();
            int columnCount = attribute.getColumnCount();
            int holderDimens = isLandscapeMode ? UIUtil.getScreenHeight(activityV2) : UIUtil.getScreenWidth(activityV2);
            holderDimens /= columnCount;
            rootView.getLayoutParams().height = holderDimens;
        }


        public FTextView getImgLabel() {
            return imgLabel;
        }

        public ISnapsImageSelectConstants.eGOOGLE_STYLE_HOLDER_TYPE getHolderType() {
            if (phonePhotoItem == null) return null;
            return phonePhotoItem.getHolderType();
        }

        public GalleryCursorRecord.PhonePhotoFragmentItem getPhonePhotoItem() {
            return phonePhotoItem;
        }

        public void setPhonePhotoItem(GalleryCursorRecord.PhonePhotoFragmentItem phonePhotoItem) {
            this.phonePhotoItem = phonePhotoItem;
        }

        public View getRootView() {
            return rootView;
        }

        public TextView getContent() {
            return content;
        }

        public boolean isDisableClick() {
            return isDisableClick;
        }

        public void setDisableClick(boolean disableClick) {
            isDisableClick = disableClick;
        }

        public SquareRelativeLayout getParentView() {
            return parentView;
        }

        public ImageView getThumbnail() {
            return thumbnail;
        }

        public ImageView getSelector() {
            return selector;
        }

        public ImageView getCheckIcon() {
            return checkIcon;
        }

        public ImageView getNoPrintIcon() {
            return noPrintIcon;
        }

        public String getOsType() {
            return osType;
        }

        public void setOsType(String osType) {
            this.osType = osType;
        }

        public MyPhotoSelectImageData getImgData() {
            return imgData;
        }

        public void setImgData(MyPhotoSelectImageData imgData) {
            this.imgData = imgData;
        }

        public String getMapKey() {
            return mapKey;
        }

        public void setMapKey(String mapKey) {
            this.mapKey = mapKey;
        }

        public boolean isEnableMimeType() {
            //판단 불가인 경우 그냥 사용할수 있다고 처리
            if (imgData == null) return true;
            if (imgData.mineType == null) return true;

            String mimeType = imgData.mineType.toLowerCase();
            if (mimeType == null || mimeType.length() == 0) return true;  //카카오 스토리는 ""

            if (mimeType.equals("image/jpg")) return true;
            if (mimeType.equals("image/jpeg")) return true;
            if (mimeType.equals("image/png")) return true;

            return false;
        }

        public void setImgData(String mapKey, int photoKind, long imgId, String displayName, String path, String thumbnailPath) {
            if (path == null) path = "";
            if (thumbnailPath == null) thumbnailPath = "";
            this.mapKey = mapKey;
            imgData = new MyPhotoSelectImageData();
            imgData.KIND = photoKind;
            imgData.IMAGE_ID = imgId;
            imgData.F_IMG_NAME = displayName;
            imgData.PATH = path;
            imgData.THUMBNAIL_PATH = thumbnailPath;
            imgData.LOCAL_THUMBNAIL_PATH = thumbnailPath;
            imgData.IMAGE_ID = path.hashCode();
        }

        public void setImgData(String mapKey, int photoKind, long imgId, String displayName, String path, String thumbnailPath, String width, String height) {
            this.mapKey = mapKey;
            imgData = new MyPhotoSelectImageData();
            imgData.KIND = photoKind;
            imgData.IMAGE_ID = imgId;
            imgData.F_IMG_NAME = displayName;
            imgData.PATH = path;
            imgData.THUMBNAIL_PATH = thumbnailPath;
            imgData.LOCAL_THUMBNAIL_PATH = thumbnailPath;
            // 원본 이미지 w, h 추
            imgData.F_IMG_WIDTH = width;
            imgData.F_IMG_HEIGHT = height;
            imgData.IMAGE_ID = path.hashCode();
        }

        public void setImgData(String mapKey, int photoKind, String fbObjId, String displayName, String path, String thumbnailPath, long photoTakenTime, String width, String height) {
            this.mapKey = mapKey;
            imgData = new MyPhotoSelectImageData();
            imgData.KIND = photoKind;
            imgData.FB_OBJECT_ID = fbObjId;
            imgData.F_IMG_NAME = displayName;
            imgData.PATH = path;
            imgData.THUMBNAIL_PATH = thumbnailPath;
            imgData.LOCAL_THUMBNAIL_PATH = thumbnailPath;
            imgData.photoTakenDateTime = photoTakenTime;
            // 원본 이미지 w, h 추가
            imgData.F_IMG_WIDTH = width;
            imgData.F_IMG_HEIGHT = height;
            imgData.IMAGE_ID = path.hashCode();

        }

        public void setImgData(String mapKey, int photoKind, String fbObjId, String displayName, String path, String thumbnailPath, long photoTakenTime, String width, String height, String mineType) {
            this.mapKey = mapKey;
            imgData = new MyPhotoSelectImageData();
            imgData.KIND = photoKind;
            imgData.FB_OBJECT_ID = fbObjId;
            imgData.F_IMG_NAME = displayName;
            imgData.PATH = path;
            imgData.THUMBNAIL_PATH = thumbnailPath;
            imgData.LOCAL_THUMBNAIL_PATH = thumbnailPath;
            imgData.photoTakenDateTime = photoTakenTime;
            // 원본 이미지 w, h 추가
            imgData.F_IMG_WIDTH = width;
            imgData.F_IMG_HEIGHT = height;
            imgData.IMAGE_ID = path.hashCode();
            imgData.mineType = mineType;

        }

        public void setImgData(String mapKey, int photoKind, long imgId, String displayName, String path, String thumbnailPath, int rotate, String width, String height) {
            this.mapKey = mapKey;
            imgData = new MyPhotoSelectImageData();
            imgData.KIND = photoKind;
            imgData.IMAGE_ID = imgId;
            imgData.F_IMG_NAME = displayName;
            imgData.PATH = path;
            imgData.THUMBNAIL_PATH = thumbnailPath;
            imgData.LOCAL_THUMBNAIL_PATH = thumbnailPath;
            imgData.ROTATE_ANGLE = rotate;
            // 원본 이미지 w, h 추
            imgData.F_IMG_WIDTH = width;
            imgData.F_IMG_HEIGHT = height;
        }
    }
}
