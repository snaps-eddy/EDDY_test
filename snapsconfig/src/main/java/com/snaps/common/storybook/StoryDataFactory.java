package com.snaps.common.storybook;

import com.snaps.common.utils.log.Dlog;

public class StoryDataFactory {
	private static final String TAG = StoryDataFactory.class.getSimpleName();
	static public IStoryDataStrategy createFactory(StoryDataType type) {
		IStoryDataStrategy instance = null;
		switch (type) {
			case KAKAO_STORY :
				instance = createInstance("com.snaps.kakao.utils.kakao.KakaoStoryDataManager");
				break;
			case BETWEEN :

				break;

			default :
				break;
		}

		return instance;
	}

	static IStoryDataStrategy createInstance(String fullClassName) {
		@SuppressWarnings("rawtypes")
		Class klass;
		IStoryDataStrategy IStory = null;
		try {
			klass = Class.forName(fullClassName);
			IStory = (IStoryDataStrategy) klass.newInstance();
			return IStory;
		} catch (ClassNotFoundException e) {
			Dlog.e(TAG, e);
		} catch (InstantiationException e) {
			Dlog.e(TAG, e);
		} catch (IllegalAccessException e) {
			Dlog.e(TAG, e);
		}

		return null;
	}
}
