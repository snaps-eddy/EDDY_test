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
     * @Makro Image~~AllViewAdapter 의 경우, ImageSelectActivity -> ImageSelectActivityTrayAllView 순으로 호출 된 후에 생성되는데
     * 뷰를 만들 때 ImageSelectActivity Context 로 만들어서 문제가 발생.
     * 그래서 Context 와 ImageSelectActivity bridge 메소드를 따로 가져가야 함.
     */
    protected Context context;
    protected IImageSelectPublicMethods imageSelectPublicMethods;

    protected ArrayList<ImageSelectTrayCellItem> trayCellItemList;
    protected ImageSelectUITrayControl trayControl;
    protected ImageSelectTrayPageCountInfo pageCountInfo;
    protected ImageSelectIntentData intentData; //FIXME 이것도....트레이 전체 보기...
    protected IImageSelectTrayAllViewListener trayAllViewListener;

    protected boolean pageSyncLock = false;
    protected int imageCount = 0;

    //외부에서 트레이에 있는 이미지 제거 (이미 데이터는 삭제되어 있다. UI처리만...)
    public abstract void removeSelectedImage(String key);

    //외부에서 트레이에 있는 이미지 제거 (이미 데이터는 삭제되어 있다. UI처리만...)
    public abstract void removeSelectedImageArray(String key, boolean first, boolean last);

    //왼쪽 하단 카운터 뷰 그리는 로직
    public abstract void refreshCounterInfo();

    //Fragment에서 선택한 이미지를 트레이 빈 공간에서 보여준다.
    public abstract boolean insertPhotoThumbnailOnTrayItem(TrayAdapterInsertParam trayAdapterInsertParam);

//    //여러장 한번에 보여준다.
//    public abstract void insertPhotoThumbnailOnTrayItemArray(String key, final MyPhotoSelectImageData imageData, boolean last);

    //다음에 들어갈 트레이의 아이템 반환
    public abstract ImageSelectTrayCellItem findNextEmptyCellItem();

    //비어있는 트레이의 갯수 반환
    public abstract int getEmptyCellCount();

    //트레이 아이템 클릭 처리
    protected abstract void onClickedTrayItem(ImageSelectTrayCellItem cellItem);

    //트레이 Plus 버튼 클릭 처리
    public abstract void performClickTrayAddBtn();

    //최대 추가할 수 있는 페이지
    public abstract boolean checkExcessMaxPage();

    //최대 추가할 수 있는 사진 수
    public abstract boolean checkExcessMaxPhoto();

    public abstract boolean checkExcessMaxPhotoForDragging();

    //트레이에 표시할 Item 리스트를 만든다.
    protected abstract void initTrayCells(ArrayList<SnapsPage> pageList);

    //사진이 들어 있지 않은 경우 셀의 상태값
    protected abstract ISnapsImageSelectConstants.eTRAY_CELL_STATE getDefaultTrayState();


    public boolean isExistPhotoOnCover() {
        return false;
    }

    public boolean isExistDummyView() {
        return false;
    }

    //트레이 아이템 클릭
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

    //선택 되어 있는 cell의 포지션
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

    //트레이 전체 보기용..
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

        return selectedCell.isSelected(); //같은 트레이를 2번째 클릭 한 것인지
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

    //cell ID로 position을 찾는다.
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

    //트레이 아이템 클릭 이벤트 전달
    protected void sendTrayItemSelectEvent(ImageSelectTrayCellItem item) {
        ImageSelectManager imageSelectManager = ImageSelectManager.getInstance();
        if (imageSelectManager != null) {
            IImageSelectStateChangedListener listener = imageSelectManager.getSelectStateChangedListener();
            if (listener != null)
                listener.onTrayItemSelected(item);
        }
    }

    //트레이뷰를 센터로 맞춰 준다.
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

    //트레이뷰를 마지막으로 이동
    protected void scrollToLastItem() {
        if (trayControl != null) {
            SnapsRecyclerView recyclerView = trayControl.getTrayThumbRecyclerView();
            if (recyclerView != null) {
                TrayLinearLayoutManager layoutManager = (TrayLinearLayoutManager) recyclerView.getLayoutManager();
                layoutManager.scrollToPosition(getItemCount() - 1);
            }
        }
    }

    //인화 불가 안내 알럿
//    protected void showNoPrintAlert() {
//        if (imageSelectActivityV2 == null) return;
//        MessageUtil.alertnoTitleOneBtn((Activity) imageSelectActivityV2, imageSelectActivityV2.getString(R.string.print_resolution_change_msg), null);
//    }

    //아이템 추가
    protected void addTrayItem(ImageSelectTrayCellItem item) {
        if (trayCellItemList == null)
            initTrayItemList();

        insert(item, trayCellItemList.size());
    }

    protected ImageSelectTrayCellItem findCellByImageKey(String key) {
        if (trayCellItemList == null || key == null || key.length() < 1) return null;
        for (ImageSelectTrayCellItem cellItem : trayCellItemList) { //삭제할 이미지
            if (cellItem == null) continue;

            if (key.equalsIgnoreCase(cellItem.getImageKey())) {
                return cellItem;
            }
        }
        return null;
    }

    //더미같은거 제외하고...
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
