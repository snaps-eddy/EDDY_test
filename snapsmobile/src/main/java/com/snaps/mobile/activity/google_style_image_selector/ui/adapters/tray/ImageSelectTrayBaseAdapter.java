package com.snaps.mobile.activity.google_style_image_selector.ui.adapters.tray;

import android.content.Context;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.data.smart_snaps.interfacies.SmartSnapsConstants;
import com.snaps.common.structure.page.SnapsPage;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_PRODUCT;
import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.constant.Const_VALUES;
import com.snaps.common.utils.imageloader.ImageLoader;
import com.snaps.common.utils.log.Dlog;
import com.snaps.mobile.activity.diary.customview.SnapsRecyclerView;
import com.snaps.mobile.activity.google_style_image_selector.datas.ImageSelectIntentData;
import com.snaps.mobile.activity.google_style_image_selector.datas.ImageSelectTrayPageCountInfo;
import com.snaps.mobile.activity.google_style_image_selector.datas.ImageSelectUITrayControl;
import com.snaps.mobile.activity.google_style_image_selector.datas.TrayAdapterInsertParam;
import com.snaps.mobile.activity.google_style_image_selector.interfaces.IImageSelectPublicMethods;
import com.snaps.mobile.activity.google_style_image_selector.interfaces.IImageSelectStateChangedListener;
import com.snaps.mobile.activity.google_style_image_selector.interfaces.IImageSelectTrayAllView;
import com.snaps.mobile.activity.google_style_image_selector.interfaces.IImageSelectTrayAllViewListener;
import com.snaps.mobile.activity.google_style_image_selector.interfaces.ISnapsImageSelectConstants;
import com.snaps.mobile.activity.google_style_image_selector.ui.adapters.ImageSelectAdapterHolders;
import com.snaps.mobile.activity.google_style_image_selector.ui.adapters.items.ImageSelectTrayCellItem;
import com.snaps.mobile.activity.google_style_image_selector.ui.custom.TrayLinearLayoutManager;
import com.snaps.mobile.activity.google_style_image_selector.utils.ImageSelectManager;
import com.snaps.mobile.order.order_v2.SnapsOrderManager;
import com.snaps.mobile.utils.smart_snaps.SmartSnapsManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.snaps.mobile.activity.google_style_image_selector.interfaces.ISnapsImageSelectConstants.TRANSPARENCY_PHOTO_CARD_MAX;
import static com.snaps.mobile.activity.google_style_image_selector.interfaces.ISnapsImageSelectConstants.eTRAY_CELL_STATE.PHOTO_THUMBNAIL;

/**
 * Created by ysjeong on 16. 3. 9..
 */
public abstract class ImageSelectTrayBaseAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements IImageSelectTrayAllView {

    private static final String TAG = ImageSelectTrayBaseAdapter.class.getSimpleName();

    /**
     * @Makro Image~~AllViewAdapter ??? ??????, ImageSelectActivity -> ImageSelectActivityTrayAllView ????????? ?????? ??? ?????? ???????????????
     * ?????? ?????? ??? ImageSelectActivity Context ??? ???????????? ????????? ??????.
     * ????????? Context ??? ImageSelectActivity bridge ???????????? ?????? ???????????? ???.
     */
    protected Context context;
    protected IImageSelectPublicMethods imageSelectPublicMethods;

    protected ArrayList<ImageSelectTrayCellItem> trayCellItemList;
    protected ImageSelectUITrayControl trayControl;
    protected ImageSelectTrayPageCountInfo pageCountInfo;
    protected ImageSelectIntentData intentData; //FIXME ?????????....????????? ?????? ??????...
    protected IImageSelectTrayAllViewListener trayAllViewListener;

    protected boolean pageSyncLock = false;
    protected int imageCount = 0;

    //???????????? ???????????? ?????? ????????? ?????? (?????? ???????????? ???????????? ??????. UI?????????...)
    public abstract void removeSelectedImage(String key);

    //???????????? ???????????? ?????? ????????? ?????? (?????? ???????????? ???????????? ??????. UI?????????...)
    public abstract void removeSelectedImageArray(String key, boolean first, boolean last);

    //?????? ?????? ????????? ??? ????????? ??????
    public abstract void refreshCounterInfo();

    //Fragment?????? ????????? ???????????? ????????? ??? ???????????? ????????????.
    public abstract boolean insertPhotoThumbnailOnTrayItem(TrayAdapterInsertParam trayAdapterInsertParam);

//    //????????? ????????? ????????????.
//    public abstract void insertPhotoThumbnailOnTrayItemArray(String key, final MyPhotoSelectImageData imageData, boolean last);

    //????????? ????????? ???????????? ????????? ??????
    public abstract ImageSelectTrayCellItem findNextEmptyCellItem();

    //???????????? ???????????? ?????? ??????
    public abstract int getEmptyCellCount();

    //????????? ????????? ?????? ??????
    protected abstract void onClickedTrayItem(ImageSelectTrayCellItem cellItem);

    //????????? Plus ?????? ?????? ??????
    public abstract void performClickTrayAddBtn();

    //?????? ????????? ??? ?????? ?????????
    public abstract boolean checkExcessMaxPage();

    //?????? ????????? ??? ?????? ?????? ???
    public abstract boolean checkExcessMaxPhoto();

    public abstract boolean checkExcessMaxPhotoForDragging();

    //???????????? ????????? Item ???????????? ?????????.
    protected abstract void initTrayCells(ArrayList<SnapsPage> pageList);

    //????????? ?????? ?????? ?????? ?????? ?????? ?????????
    protected abstract ISnapsImageSelectConstants.eTRAY_CELL_STATE getDefaultTrayState();


    public boolean isExistPhotoOnCover() {
        return false;
    }

    public boolean isExistDummyView() {
        return false;
    }

    //????????? ????????? ??????
    public abstract void selectTrayItem(int id, boolean isScrollToCenter);

    public ImageSelectTrayBaseAdapter(Context context, IImageSelectPublicMethods imageSelectPublicMethods) {
        this.context = context;
        this.imageSelectPublicMethods = imageSelectPublicMethods;
        this.intentData = imageSelectPublicMethods.getIntentData();

        ImageSelectManager imageSelectManager = ImageSelectManager.getInstance();
        if (imageSelectManager != null)
            pageCountInfo = imageSelectManager.getPageCountInfo();
    }

    public void releaseInstance() {
        if (trayCellItemList != null) {
            trayCellItemList.clear();
            trayCellItemList = null;
        }

        if (trayControl != null) {
            trayControl.releaseInstance();
        }
    }

    //?????? ?????? ?????? cell??? ?????????
    public int getSelectedCellItemPosition() {
        if (getTrayCellItemList() == null) return 0;
        for (int ii = 0; ii < getTrayCellItemList().size(); ii++) {
            ImageSelectTrayCellItem item = getTrayCellItemList().get(ii);
            if (item != null && item.isSelected()) return ii;
        }
        return 0;
    }

    public int getDefaultLimitImageCount() {
        String templateCode = Config.getTMPL_CODE();
        String prodCode = Config.getPROD_CODE();

        if (Config.isSimpleMakingBook())
            return Const_VALUE.MAX_IMAGE_SIMPLE_MAKING_BOOK;
        else if (Const_PRODUCT.isTransparencyPhotoCardProduct())
            return TRANSPARENCY_PHOTO_CARD_MAX;
        else if (templateCode != null && templateCode.equals(Config.TEMPLATE_STICKER_6))
            return Const_VALUE.MAX_IMAGE_STICKER_6;
        else if (templateCode != null && templateCode.equals(Config.TEMPLATE_STICKER_2))
            return Const_VALUE.MAX_IMAGE_STICKER_2;
        else if (templateCode != null && templateCode.equals(Config.TEMPLATE_STICKER_1))
            return Const_VALUE.MAX_IMAGE_STICKER_1;
        else if (prodCode != null && Config.isSimplePhotoBook(prodCode))
            return getItemCount() - 1;
        else if (Const_PRODUCT.isSNSBook() || Config.isSnapsPhotoPrint())
            return -1;
        else {
            return getItemCount();
        }
    }

    public void setData(ArrayList<SnapsPage> newList) {
        if (newList == null) return;

        initTrayItemList();

        initTrayCells(newList);

        notifyDataSetChanged();
    }

    public boolean isSmartChoiceType() {
        return intentData != null && intentData.getSmartSnapsImageSelectType() == SmartSnapsConstants.eSmartSnapsImageSelectType.SMART_CHOICE;
    }

    public boolean isPageSyncLock() {
        return pageSyncLock;
    }

    public void setPageSyncLock(boolean pageSyncLock) {
        this.pageSyncLock = pageSyncLock;
    }

    public RecyclerView.ViewHolder getItemViewHolder(ViewGroup parent) {
        return null;
    }

    //????????? ?????? ?????????..
    @Override
    public void setTrayAllViewList(ArrayList<ImageSelectTrayCellItem> allViewList, int defaultSelectedId) {
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return getItemViewHolder(parent);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
    }


    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);

        if (holder == null || !(holder instanceof ImageSelectAdapterHolders.TrayThumbnailItemHolder)) return;

        ImageSelectAdapterHolders.TrayThumbnailItemHolder photoHolder = (ImageSelectAdapterHolders.TrayThumbnailItemHolder) holder;

        if (photoHolder.getPhotoThumbnail() != null) {
            ImageLoader.clear(context, photoHolder.getPhotoThumbnail());
        }
    }

    protected ImageSelectTrayCellItem getItem(int pos) {
        if (trayCellItemList == null || trayCellItemList.size() <= pos) return null;
        return trayCellItemList.get(pos);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return trayCellItemList != null ? trayCellItemList.size() : 0;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void add(ImageSelectTrayCellItem contents) {
        insert(contents, trayCellItemList.size());
    }

    public void insert(ImageSelectTrayCellItem contents, int position) {
        trayCellItemList.add(position, contents);
        notifyItemInserted(position);
    }

    public void remove(int position) {
        if (trayCellItemList == null || trayCellItemList.size() <= position || position < 0) return;

        trayCellItemList.remove(position);
        notifyItemRemoved(position);
    }

    public void removeByCellId(int cellId) {
        int position = findCellPositionByCellId(cellId);
        remove(position);
    }

    public void clear() {
        int size = trayCellItemList.size();
        trayCellItemList.clear();
        notifyItemRangeRemoved(0, size);
    }

    public void addAll(ImageSelectTrayCellItem[] contentses) {
        int startIndex = trayCellItemList.size();
        trayCellItemList.addAll(startIndex, Arrays.asList(contentses));
        notifyItemRangeInserted(startIndex, contentses.length);
    }

    public void addAll(List<ImageSelectTrayCellItem> contentses) {
        int startIndex = trayCellItemList.size();
        trayCellItemList.addAll(startIndex, contentses);
        notifyItemRangeInserted(startIndex, contentses.size());
    }

    public IImageSelectTrayAllViewListener getTrayAllViewListener() {
        return trayAllViewListener;
    }

    public void setTrayAllViewListener(IImageSelectTrayAllViewListener trayAllViewListener) {
        this.trayAllViewListener = trayAllViewListener;
    }

    public ImageSelectUITrayControl getTrayControl() {
        return trayControl;
    }

    public void setTrayControl(ImageSelectUITrayControl trayControl) {
        this.trayControl = trayControl;
    }

    public ArrayList<ImageSelectTrayCellItem> getTrayCellItemList() {
        return this.trayCellItemList;
    }

    public void cloneTrayCellItemList(ArrayList<ImageSelectTrayCellItem> trayCellItemList) {
        if (trayCellItemList == null) return;
        this.trayCellItemList = (ArrayList<ImageSelectTrayCellItem>) trayCellItemList.clone();
    }

    public boolean isTwiceClickedCell(int id) {
        ImageSelectTrayCellItem selectedCell = getTrayCellItemById(id);
        if (selectedCell == null || selectedCell.getCellState() != PHOTO_THUMBNAIL) return false;

        return selectedCell.isSelected(); //?????? ???????????? 2?????? ?????? ??? ?????????
    }

    public ImageSelectTrayPageCountInfo getPageCountInfo() {
        return pageCountInfo;
    }

    protected ImageSelectTrayCellItem getTrayCellItem(int pos) {
        if (trayCellItemList == null || trayCellItemList.size() <= pos) return null;
        return trayCellItemList.get(pos);
    }

    protected ImageSelectTrayCellItem getTrayCellItemById(int id) {
        if (trayCellItemList == null) return null;
        for (ImageSelectTrayCellItem cellItem : trayCellItemList) {
            if (cellItem != null && cellItem.getCellId() == id)
                return cellItem;
        }
        return null;
    }

    protected void notifyItemChangedByCellId(int id) {
        if (trayCellItemList == null) return;
        int position = findCellPositionByCellId(id);
        if (position >= 0 && position < getItemCount()) {
            notifyItemChanged(position);
        }
    }

    //cell ID??? position??? ?????????.
    protected int findCellPositionByCellId(int id) {
        if (trayCellItemList == null) return -1;
        for (int ii = 0; ii < trayCellItemList.size(); ii++) {
            ImageSelectTrayCellItem cellItem = trayCellItemList.get(ii);
            if (cellItem != null && cellItem.getCellId() == id)
                return ii;
        }
        return -1;
    }

    protected void initTrayItemList() {
        if (trayCellItemList != null)
            trayCellItemList.clear();
        else
            trayCellItemList = new ArrayList<>();
    }

    //????????? ????????? ?????? ????????? ??????
    protected void sendTrayItemSelectEvent(ImageSelectTrayCellItem item) {
        ImageSelectManager imageSelectManager = ImageSelectManager.getInstance();
        if (imageSelectManager != null) {
            IImageSelectStateChangedListener listener = imageSelectManager.getSelectStateChangedListener();
            if (listener != null)
                listener.onTrayItemSelected(item);
        }
    }

    //??????????????? ????????? ?????? ??????.
    public void scrollToCenterTrayView(int position) {
        if (!isValidScrollToCenterTrayView(position)) return;

        if (trayControl != null) {
            SnapsRecyclerView recyclerView = trayControl.getTrayThumbRecyclerView();
            if (recyclerView != null) {
                TrayLinearLayoutManager layoutManager = (TrayLinearLayoutManager) recyclerView.getLayoutManager();
                layoutManager.smoothScrollToPosition(recyclerView, null, position);
            }
        }
    }

    protected boolean isValidScrollToCenterTrayView(int position) {
        return position >= 0 && position < getItemCount() && !isTrayAllViewMode();
    }

    //??????????????? ??????????????? ??????
    protected void scrollToLastItem() {
        if (trayControl != null) {
            SnapsRecyclerView recyclerView = trayControl.getTrayThumbRecyclerView();
            if (recyclerView != null) {
                TrayLinearLayoutManager layoutManager = (TrayLinearLayoutManager) recyclerView.getLayoutManager();
                layoutManager.scrollToPosition(getItemCount() - 1);
            }
        }
    }

    //?????? ?????? ?????? ??????
//    protected void showNoPrintAlert() {
//        if (imageSelectActivityV2 == null) return;
//        MessageUtil.alertnoTitleOneBtn((Activity) imageSelectActivityV2, imageSelectActivityV2.getString(R.string.print_resolution_change_msg), null);
//    }

    //????????? ??????
    protected void addTrayItem(ImageSelectTrayCellItem item) {
        if (trayCellItemList == null)
            initTrayItemList();

        insert(item, trayCellItemList.size());
    }

    protected ImageSelectTrayCellItem findCellByImageKey(String key) {
        if (trayCellItemList == null || key == null || key.length() < 1) return null;
        for (ImageSelectTrayCellItem cellItem : trayCellItemList) { //????????? ?????????
            if (cellItem == null) continue;

            if (key.equalsIgnoreCase(cellItem.getImageKey())) {
                return cellItem;
            }
        }
        return null;
    }

    //??????????????? ????????????...
    protected int getOnlyThumbnailCellCount() {
        if (trayCellItemList == null) return 0;
        int result = 0;
        for (ImageSelectTrayCellItem cellItem : trayCellItemList) {
            if (cellItem == null) continue;

            if (cellItem.getCellState() == ISnapsImageSelectConstants.eTRAY_CELL_STATE.PHOTO_THUMBNAIL
                    || cellItem.getCellState() == ISnapsImageSelectConstants.eTRAY_CELL_STATE.TEMPLATE)
                result++;
        }
        return result;
    }

    void createThumbnailCacheWithImageData(MyPhotoSelectImageData imageData) {
        if (!SmartSnapsManager.isSupportSmartSnapsProduct() || imageData == null || imageData.KIND != Const_VALUES.SELECT_PHONE) return;
        try {
            SnapsOrderManager orderManager = SnapsOrderManager.getInstance();
            orderManager.createThumbnailCacheWithImageData(context, imageData);
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    boolean isTrayAllViewMode() {
        ImageSelectManager imageSelectManager = ImageSelectManager.getInstance();
        return imageSelectManager.isTrayAllViewMode();
    }

    public boolean checkSelectImage(String mapKey) {
        for (ImageSelectTrayCellItem item : trayCellItemList) {
            if (item.getImageKey().equals(mapKey)) {
                return true;
            }
        }
        return false;
    }

    public void setImageCount(int count) {
        this.imageCount = count;
    }
}
