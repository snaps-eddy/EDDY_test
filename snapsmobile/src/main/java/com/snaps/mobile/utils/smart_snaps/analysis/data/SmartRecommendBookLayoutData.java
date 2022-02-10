package com.snaps.mobile.utils.smart_snaps.analysis.data;

import com.snaps.common.structure.SnapsTemplate;
import com.snaps.common.utils.net.xml.bean.Xml_ThemeCover;
import com.snaps.common.utils.net.xml.bean.Xml_ThemePage;

import java.util.Set;

/**
 * Created by ysjeong on 2018. 4. 27..
 */

public class SmartRecommendBookLayoutData {
	private SnapsTemplate coverTemplate;

	private Xml_ThemeCover coverDesignList;

	private Set<String> coverPhotoKeySet = null;

	private Xml_ThemePage indexDesignList;
	private Xml_ThemePage pageDesignList;
	private Xml_ThemePage pageBGResList;

	public void releaseInstance() {
		clearTemplateInfo();

		if (coverPhotoKeySet != null) {
			coverPhotoKeySet.clear();
			coverPhotoKeySet = null;
		}
	}

	public void clearTemplateInfo() {
		coverTemplate = null;
		coverDesignList = null;
		indexDesignList = null;
		pageDesignList = null;
		pageBGResList = null;
	}

	public SnapsTemplate getCoverTemplate() {
		return coverTemplate;
	}

	public void setCoverTemplate(SnapsTemplate coverTemplate) {
		this.coverTemplate = coverTemplate;
	}

	public Set<String> getCoverPhotoKeySet() {
		return coverPhotoKeySet;
	}

	public void setCoverPhotoKeySet(Set<String> coverPhotoKeySet) {
		this.coverPhotoKeySet = coverPhotoKeySet;
	}

	public Xml_ThemeCover getCoverDesignList() {
		return coverDesignList;
	}

	public void setCoverDesignList(Xml_ThemeCover coverDesignList) {
		this.coverDesignList = coverDesignList;
	}

	public Xml_ThemePage getIndexDesignList() {
		return indexDesignList;
	}

	public void setIndexDesignList(Xml_ThemePage indexDesignList) {
		this.indexDesignList = indexDesignList;
	}

	public Xml_ThemePage getPageDesignList() {
		return pageDesignList;
	}

	public void setPageDesignList(Xml_ThemePage pageDesignList) {
		this.pageDesignList = pageDesignList;
	}

	public Xml_ThemePage getPageBGResList() {
		return pageBGResList;
	}

	public void setPageBGResList(Xml_ThemePage pageBGResList) {
		this.pageBGResList = pageBGResList;
	}
}
