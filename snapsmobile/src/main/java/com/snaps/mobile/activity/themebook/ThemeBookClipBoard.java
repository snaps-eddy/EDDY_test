package com.snaps.mobile.activity.themebook;

import android.content.Context;

import com.snaps.common.structure.page.SnapsPage;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.ui.MessageUtil;
import com.snaps.mobile.R;

public class ThemeBookClipBoard implements IThemeBookClipBoard {

	private Context mContext = null;

	private SnapsPage mPage = null;
	private boolean m_isExistCopiedPage = false;
	private int m_iSelectedPostion = -1;

	public ThemeBookClipBoard(Context con) {
		mContext = con;
	}

	@Override
	public void copy(SnapsPage p, boolean isPaste) {
		mPage = createCopiedPage(p); //p

		if (isPaste)
			setExistCopiedPage(true);

		if( !Config.isSimpleMakingBook() ) MessageUtil.toast(mContext, mContext.getString(R.string.page_copy_complete_msg));
	}

	@Override
	public SnapsPage getCopiedPage() {
		if(mPage == null) return null;
				
		return createCopiedPage(mPage);
	}

	@Override
	public void setExistCopiedPage(boolean isCopied) {
		m_isExistCopiedPage = isCopied;
	}

	@Override
	public boolean isExistCopiedPage() {
		return m_isExistCopiedPage;
	}

	@Override
	public int getSelectedPageIndex() {
		return m_iSelectedPostion;
	}

	@Override
	public void setSelectedPageIndex(int index) {
		m_iSelectedPostion = index;
	}

	private SnapsPage createCopiedPage(SnapsPage originPage) {
		if (originPage == null)
			return null;
		
		SnapsPage page = null;
		
		if(Config.isThemeBook()) {
			page = new SnapsPage(-1, originPage.info);
			page.setWidth(originPage.getWidth() + "");
			page.height = originPage.height;
			page.layout = originPage.layout;
			page.type = originPage.type;
			page.isSelected = false;
			page.border = originPage.border;
			page.background = originPage.background;
		} else {
			page = originPage.copyPage(-1);
			page.info = originPage.info;
		}
		
		return page;
	}

	@Override
	public void deleteClipBoardPage() {
		setExistCopiedPage(false);	
		mPage = null;
	}
}
