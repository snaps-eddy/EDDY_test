/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.snaps.common.utils.image;

import android.graphics.Color;
import android.os.Build;
import android.view.View;

import com.snaps.common.utils.log.Dlog;

import java.lang.reflect.Field;
import java.util.Random;

/**
 * Class containing some static utility methods.
 */
public class Utils {
    private static final String TAG = Utils.class.getSimpleName();
	private Utils() {
	};

    /**
     * Returns the current View.OnClickListener for the given View
     * @param view the View whose click listener to retrieve
     * @return the View.OnClickListener attached to the view; null if it could not be retrieved
     */
    public static View.OnLongClickListener getOnLongClickListener(View view) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            return getOnLongClickListenerV14(view);
        } else {
            return getOnLongClickListenerV(view);
        }
    }

    //Used for APIs lower than ICS (API 14)
    private static View.OnLongClickListener getOnLongClickListenerV(View view) {
        View.OnLongClickListener retrievedListener = null;
        String viewStr = "android.view.View";
        Field field;

        try {
            field = Class.forName(viewStr).getDeclaredField("mOnLongClickListener");
            retrievedListener = (View.OnLongClickListener) field.get(view);
        } catch (NoSuchFieldException ex) {
            Dlog.e(TAG, ex);
        } catch (IllegalAccessException ex) {
            Dlog.e(TAG, ex);
        } catch (ClassNotFoundException ex) {
            Dlog.e(TAG, ex);
        }

        return retrievedListener;
    }

    //Used for new ListenerInfo class structure used beginning with API 14 (ICS)
    private static View.OnLongClickListener getOnLongClickListenerV14(View view) {
        View.OnLongClickListener retrievedListener = null;
        String viewStr = "android.view.View";
        String lInfoStr = "android.view.View$ListenerInfo";

        try {
            Field listenerField = Class.forName(viewStr).getDeclaredField("mListenerInfo");
            Object listenerInfo = null;

            if (listenerField != null) {
                listenerField.setAccessible(true);
                listenerInfo = listenerField.get(view);
            }

            Field clickListenerField = Class.forName(lInfoStr).getDeclaredField("mOnClickListener");

            if (clickListenerField != null && listenerInfo != null) {
                retrievedListener = (View.OnLongClickListener) clickListenerField.get(listenerInfo);
            }
        } catch (NoSuchFieldException ex) {
            Dlog.e(TAG, ex);
        } catch (IllegalAccessException ex) {
            Dlog.e(TAG, ex);
        } catch (ClassNotFoundException ex) {
            Dlog.e(TAG, ex);
        }

        return retrievedListener;
    }

    public static int getRandomColor() {
        return getRandomColor( 255 );
    }

    public static int getRandomColor( int alpha ) {
        Random rnd = new Random();
        return Color.argb( alpha, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256) );
    }

	public static boolean hasFroyo() {
		// Can use static final constants like FROYO, declared in later versions
		// of the OS since they are inlined at compile time. This is guaranteed behavior.
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO;
	}

	public static boolean hasGingerbread() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD;
	}

	public static boolean hasHoneycomb() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
	}

	public static boolean hasHoneycombMR1() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1;
	}

	public static boolean hasIceCreamSandWich() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;
	}

	public static boolean hasJellyBean() {
		return false;
	}
}
