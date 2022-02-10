package com.snaps.common.structure.control;

public interface Subject {

	public abstract void addObserver(Observer observer);
	public abstract void notifyObserver();
	//public abstract void removeObserver();
	
}
