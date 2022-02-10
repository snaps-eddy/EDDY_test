package com.snaps.common.structure.photoprint;

import com.snaps.common.structure.SnapsDelImage;
import com.snaps.mobile.activity.photoprint.model.PhotoPrintData;

import java.util.ArrayList;

public interface ImpUploadProject {

	/***
	 * orderCode를 구하는 함수..(사용하지 않음.. 무조건 14600으로 반환)
	 * 
	 * @return
	 */
	String getOrderCode();

	/***
	 * 프로젝트 코드를 설정하는 함수
	 * 
	 * @param prjCode
	 */
	void setProjectCode(String prjCode);

	/***
	 * 프로젝트 코드를 받아오는 함수
	 * 
	 * @return
	 */
	String getProjectCode();

	/***
	 * 오리지널 이미지를 서버에 저장을 하고 시퀀스를 저장하는 함수..
	 * 
	 * @param idx
	 * @param data
	 */
	void setItemImgSeqWithImageId(int idx, SnapsDelImage data);

	/***
	 * 어플리케이션 버젼을 설정하는 함수.
	 * 
	 * @param version
	 */
	void setApplicationVersion(String version);

	/***
	 * 장바구니에 표시될 작품썸네일 업로드 사진인화는 사용하지 않음
	 * 
	 */
	String getCartThumbnail();

	/***
	 * 작품페이지 이미지 업로드 데이타..사진인화는 사용하지 않음
	 * 
	 * @return
	 */
	ArrayList<String> getWorkThumbnails();

	/***
	 * 원본이미지 Path
	 * 
	 * @param index
	 * @return
	 */
	String getOriginalPathWithIndex(int index);

	/***
	 * 정해진 파일패스로 saveXML를 만드는 함수
	 * 
	 * @param filePath
	 */
	SnapsXmlMakeResult makeSaveXML(String filePath);

	SnapsXmlMakeResult makeAuraOrderXML(String filePath);

	SnapsXmlMakeResult makeOptionXML(String filePath);

	/***
	 * 아이텝 갯수를 반환하는 함수..
	 * 
	 * @return
	 */
	int getItemCount();

	/*****
	 * 프로젝트 업로드 상태...
	 */

	/***
	 * 프로젝트 진행 단계을 설정하는 함수..
	 * 
	 * @param step
	 * @param subStep
	 */
	void setProcessStep(int step, int subStep);

	/***
	 * 현재진행완료된 단계
	 * 
	 * @return
	 */
	int getProcessStep();

	/***
	 * 현재진행된 서브단계..
	 * 
	 * @return
	 */
	int getProcessSubStep();

	/***
	 * 작업취소 여부
	 * 
	 * @return
	 */
	int getCancel();

	/***
	 * 리트라이를 한 횟수를 가져오는 함
	 * 
	 * @return
	 */
	int getRetryCount();

	/***
	 * 리트라이 카운트 증가시키는 함수 -1대입시 초기화 보통 1을 넣는다.
	 * 
	 * @param count
	 */
	void setRetryCount(int count);

	/***
	 * facebook 이미지 인지 로컬이미지인치 판단하는 함수..
	 * 
	 * @param index
	 */
	boolean isFacebookImage(int index);

	/***
	 * 업로드가 완료가 된 사진갯수..
	 * 
	 * @return
	 */
	int getUploadComleteCount();

	/***
	 * 이미지 종류를 가져오는 함수..
	 * 
	 * @return
	 */
	int getImageKindWithIndex(int index);

	/***
	 * 이미지 사이즈를 변경해주는 함수..
	 * 
	 * @param width
	 * @param height
	 * @return
	 */
	boolean chagneImageSize(int index, int width, int height);


	/**
	 * 업로드 대상에서 삭제한다.
	 */
	boolean removeImageDataWithImageId(int imageId) throws Exception;

	PhotoPrintData getPhotoPrintDataWithImageId(int imageId);

	PhotoPrintData getPhotoPrintDataWithIndex(int index);
}
