package com.snaps.mobile.autosave;

import android.content.Context;

import com.snaps.common.structure.SnapsTemplate;
import com.snaps.common.structure.control.SnapsControl;
import com.snaps.common.structure.control.SnapsLayoutControl;
import com.snaps.common.structure.page.SnapsCalendarRecoverPage;
import com.snaps.common.structure.page.SnapsPage;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.file.FileUtil;
import com.snaps.common.utils.log.Dlog;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class AutoSaveFileProcess implements IAutoSaveFileInfo, IAutoSaveExportProcessor, IAutoSaveConstants {
	private static final String TAG = AutoSaveFileProcess.class.getSimpleName();
	private final byte[] FILES = {FILE_TYPE_INFO, FILE_TYPE_TEMPLATE};
	private Context context;

	private AutoSaveThread saveTask = null;

	private SnapsCalendarRecoverPage objOnlyLayoutControl;

	public AutoSaveFileProcess(Context context) {
		this.context = context;
	}

	public String getFilePath(byte TYPE) {
		return getFilePath(TYPE, true);
	}

	@Override
	public String getFilePath(byte TYPE, boolean realFile) {
		if (context == null) {
			return null;
		}
		StringBuffer sbPath = new StringBuffer();
		sbPath.append("snaps/save/");
		if (!realFile) {
			sbPath.append(FILE_NAME_TEMP_PREFIX);
		}
		switch (TYPE) {
			case FILE_TYPE_INFO:
				sbPath.append(FILE_NAME_INFO);
				break;
			case FILE_TYPE_TEMPLATE:
				sbPath.append(FILE_NAME_TEMPLATE);
				break;
			case FILE_TYPE_LAYOUT_CONTROLS:
				sbPath.append(FILE_NAME_LAYOUT_CONTROLS);
				break;
			default:
				break;
		}

		File file = new File(Config.getExternalCacheDir(context), sbPath.toString());
		return file.getAbsolutePath();
	}

	@Override
	public boolean checkAutoSavedFilesExists() {
		for (byte file : FILES) {
			String path = getFilePath(file);
			File f = new File(path);

			//파일이 1개라도 없다면, 다 지워 버린다.
			if (!f.exists()) {
				deleteAllFiles();
				return false;
			}

			//만약, 1달 이상 지난 파일이라면 지워 버린다.
			if (isExceededValidPeriodSaveFile(f)) {
				deleteAllFiles();
				return false;
			}
		}

		return true;
	}

	@Override
	public void deleteAllFiles() {
		String root = getFilePath(FILE_TYPE_DIRECTORY);
		if (root == null) {
			return;
		}
		File rootDirectory = new File(root);
		if (rootDirectory.isDirectory()) {
			try {
				FileUtil.deleteFolderInFiles(rootDirectory);
			} catch (Exception e) {
				Dlog.e(TAG, e);
			}
		}
	}

	@Override
	public Object getObjectFromFile(File file) {
		if (file == null || !file.exists()) {
			return null;
		}

		FileInputStream fis = null;
		ObjectInputStream ois = null;

		try {
			fis = new FileInputStream(file);
			ois = new ObjectInputStream(fis);
			return ois.readObject();
		} catch (Exception e) {
			Dlog.e(TAG, e);
		} finally {
			// 스트림을 닫아준다.
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					Dlog.e(TAG, e);
				}
			}
			if (ois != null) {
				try {
					ois.close();
				} catch (IOException e) {
					Dlog.e(TAG, e);
				}
			}
		}
		return null;
	}

	@Override
	public void exportTemplate(SnapsTemplate template) {
		if (template == null) {
			return;
		}
		waitPrevTask(FILE_TYPE_TEMPLATE);
		saveTask = new AutoSaveThread(this, FILE_TYPE_TEMPLATE, template);
		saveTask.start();
	}

	@Override
	public void exportLayoutControls(ArrayList<SnapsPage> pages,
			ArrayList<String> thumbnailPaths,
			int lastPageIdx) {
		objOnlyLayoutControl = new SnapsCalendarRecoverPage();
		objOnlyLayoutControl.setLastPageIdx(lastPageIdx);
		objOnlyLayoutControl.setThumbnailPaths(thumbnailPaths);

		ArrayList<SnapsControl> arrControls = objOnlyLayoutControl.getLayouts();

		for (SnapsPage page : pages) {

			if (page == null) {
				continue;
			}

			ArrayList<SnapsControl> arrLayoutControls = page.getLayoutList();

			if (arrLayoutControls == null) {
				continue;
			}

			for (SnapsControl c : arrLayoutControls) {
				if (c != null && c instanceof SnapsLayoutControl) {
					SnapsLayoutControl layoutControl = (SnapsLayoutControl) c;
					arrControls.add(layoutControl);
				}
			}
		}

		waitPrevTask(FILE_TYPE_LAYOUT_CONTROLS);
		saveTask = new AutoSaveThread(this, FILE_TYPE_LAYOUT_CONTROLS, objOnlyLayoutControl);
		saveTask.start();
	}

	@Override
	public void exportProjectInfo(AutoSaveProjectInfo info) {
		if (info == null) {
			return;
		}
		waitPrevTask(FILE_TYPE_INFO);
		saveTask = new AutoSaveThread(this, FILE_TYPE_INFO, info);
		saveTask.start();
	}

	private void waitPrevTask(byte taskType) {
		if (saveTask == null) {
			return;
		}

		//같은 작업을 진행 중이라면 중단 시켜 버리고 새로운 작업으로 바로 돌입한다.
		if (saveTask.getState() == Thread.State.RUNNABLE
				&& saveTask.getTaskType() == taskType) {
			saveTask.suspendThread();
			try {
				saveTask.join();
			} catch (InterruptedException e) {
				Dlog.e(TAG, e);
			}
		}
	}

	/**
	 * 자동 저장 파일이 생성 된 시점에서 한달 이상 지나면, 삭제 해 버린다.\
	 */
	private boolean isExceededValidPeriodSaveFile(File file) {
		if (file == null || !file.exists()) {
			return false;
		}

		//2주 전
		Calendar validDate = Calendar.getInstance();
		validDate.add(Calendar.DAY_OF_MONTH, -14);

		//파일의 최종 수정 일
		Date lastModDate = new Date(file.lastModified());

		return lastModDate.before(validDate.getTime());
	}
}
