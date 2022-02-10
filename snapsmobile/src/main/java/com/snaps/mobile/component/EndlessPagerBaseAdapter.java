package com.snaps.mobile.component;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public abstract class EndlessPagerBaseAdapter extends FragmentStatePagerAdapter {

	public static final int DUMMY_COUNT = 2000;

	public EndlessPagerBaseAdapter(FragmentManager fm) {
		super(fm);
	}

	public abstract int getDataCount();
	public abstract boolean isBadgeExist( int position );
}
