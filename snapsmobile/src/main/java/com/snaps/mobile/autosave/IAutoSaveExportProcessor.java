package com.snaps.mobile.autosave;

import java.util.ArrayList;

import com.snaps.common.structure.SnapsTemplate;
import com.snaps.common.structure.page.SnapsPage;

public interface IAutoSaveExportProcessor {
	public void exportTemplate(SnapsTemplate template);
	
	public void exportLayoutControls(ArrayList<SnapsPage> pages, ArrayList<String> thumbnailPaths, int lastPageIdx);
	
	public void exportProjectInfo(AutoSaveProjectInfo info);
	
}
