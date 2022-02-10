package com.snaps.common.utils.imageloader;

import android.graphics.Bitmap;
import androidx.collection.LruCache;

public class CLruCache<K, V> extends LruCache<K, V> {

	public CLruCache(int maxSize) {
		super(maxSize);
	}

	@Override
	protected int sizeOf(K key, V value) {
		return ((Bitmap) value).getRowBytes() * ((Bitmap) value).getHeight();
	}

}