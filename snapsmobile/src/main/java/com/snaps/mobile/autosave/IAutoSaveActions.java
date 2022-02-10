package com.snaps.mobile.autosave;


public interface IAutoSaveActions {
	
	public void startAutoSave(int productType);
	
	public void finishAutoSaveMode();
	
	public void continueAutoSave();
	
	public void delete();
	
	public void recovery() throws Exception;
}
