package com.snaps.mobile.autosave;

import java.io.File;

public interface IAutoSaveFileInfo {
	public String getFilePath(byte TYPE, boolean realFile);

	public boolean checkAutoSavedFilesExists();

	public void deleteAllFiles();

	public Object getObjectFromFile(File file);
}
