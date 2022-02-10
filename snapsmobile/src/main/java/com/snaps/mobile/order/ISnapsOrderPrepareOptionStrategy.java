package com.snaps.mobile.order;

import androidx.fragment.app.Fragment;

import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.structure.SnapsTemplate;
import com.snaps.common.structure.page.SnapsPage;
import com.snaps.mobile.activity.edit.fragment.dialog.DialogInputNameFragment;
import com.snaps.mobile.activity.edit.pager.BaseSnapsPagerController;

import java.util.ArrayList;

public interface ISnapsOrderPrepareOptionStrategy {

	public boolean checkOption(); //주문을 진행하기 전 요소들을 점검한다.
	
	public void showProjectNameInputPopup(ISnapsOrderActionListener lis); //프로젝트 명 확인을 위한 컨펌창 생성
	
	public void setSaveMode(boolean saveMode); //단순 저장하기를 누른 건지.
	
	public boolean isSaveMode();
	
	public ArrayList<SnapsPage> getPageList();
	
	public ArrayList<SnapsPage> getBackPageList();
	
	public ArrayList<SnapsPage> getHiddenPageList();
	
	public SnapsTemplate getTemplate();
	
	public ArrayList<MyPhotoSelectImageData> getImageList();
	
	public BaseSnapsPagerController getPagerController();
	
	public ArrayList<Fragment> getCanvasList();
	
	public DialogInputNameFragment getInputNameDialog();
}
