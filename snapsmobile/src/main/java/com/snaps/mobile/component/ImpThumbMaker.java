package com.snaps.mobile.component;

public interface ImpThumbMaker {
	
	public void onThumbMakeStart();
	public void onThumbMakeing(float per);
	public void onThumbMakeEnd();
	
	public void onParserXmlStart();
	public void onParserXmlMaking(float per);
	public void onParserXmlEnd();

}
