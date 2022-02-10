package com.snaps.common.thumb;

import android.graphics.Bitmap;
import android.util.Log;

import com.snaps.common.utils.log.Dlog;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

public class MemoryCache {

	private static final String TAG = MemoryCache.class.getSimpleName();
	private Map<String, Bitmap> cache = Collections.synchronizedMap(new LinkedHashMap<String, Bitmap>(10, 1.5f, true));// Last argument true for LRU ordering

	private long size = 0;// current allocated size
	private long limit = 1024 * 1024 * 5;// max memory in bytes

	public MemoryCache() {
		// use 25% of available heap size
		setLimit(Runtime.getRuntime().maxMemory() / 4);
	}

	public void setLimit(long new_limit) {
		limit = new_limit;
		Dlog.i(TAG, "setLimit() MemoryCache will use up to " + limit / 1024. / 1024. + "MB");
	}

	public Bitmap get(String id) {
		try {
			if (!cache.containsKey(id)) {
				return null;
			}
			// NullPointerException sometimes happen here http://code.google.com/p/osmdroid/issues/detail?id=78
			return cache.get(id);
		} catch (NullPointerException ex) {
			Dlog.e(TAG, ex);
			return null;
		}
	}

	public void put(String id, Bitmap bitmap) {
		try {
			if (cache.containsKey(id)) {
				size -= getSizeInBytes(cache.get(id));
			}

			cache.put(id, bitmap);

			size += getSizeInBytes(bitmap);
			checkSize();
		} catch (Throwable th) {
			Dlog.e(TAG, th);
		}
	}

	private void checkSize() {
		Dlog.i(TAG, "checkSize() cache size:" + size + ", length:" + cache.size());
		while (true) {
			if (size > limit) {
				Iterator<Entry<String, Bitmap>> iter = cache.entrySet().iterator();// least recently accessed item will be the first one iterated
				if (iter.hasNext()) {
					Entry<String, Bitmap> entry = iter.next();
					size -= getSizeInBytes(entry.getValue());
					cache.remove(entry.getKey());
				}
				Dlog.i(TAG, "checkSize() Clean cache. New size:" + cache.size());
			}

			if (size <= limit) {
				break;
			}
		}
	}

	public void clear() {
		try {
			// NullPointerException sometimes happen here http://code.google.com/p/osmdroid/issues/detail?id=78
			cache.clear();
			size = 0;
		} catch (NullPointerException ex) {
			Dlog.e(TAG, ex);
		}
	}

	long getSizeInBytes(Bitmap bitmap) {
		if (bitmap == null) {
			return 0;
		}
		return bitmap.getRowBytes() * bitmap.getHeight();
	}
}