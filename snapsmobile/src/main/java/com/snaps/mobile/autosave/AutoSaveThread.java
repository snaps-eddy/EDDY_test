package com.snaps.mobile.autosave;

import com.snaps.common.structure.SnapsTemplate;
import com.snaps.common.structure.page.SnapsCalendarRecoverPage;
import com.snaps.common.utils.file.FileUtil;
import com.snaps.common.utils.log.Dlog;
import com.snaps.mobile.utils.smart_snaps.SmartSnapsManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import static com.snaps.mobile.autosave.IAutoSaveConstants.FILE_TYPE_TEMPLATE;

public class AutoSaveThread extends Thread {
	private static final String TAG = AutoSaveThread.class.getSimpleName();

	private byte mType = 0;

	private AutoSaveFileProcess fileProcess = null;

	private SnapsTemplate snapsTemplate = null;

	private AutoSaveProjectInfo projectInfo = null;

	private SnapsCalendarRecoverPage layoutControls = null;

	private boolean isSuspended = false;

	public AutoSaveThread(AutoSaveFileProcess fileProcess, byte type, SnapsTemplate template) {
		mType = type;
		snapsTemplate = template;
		this.fileProcess = fileProcess;

		isSuspended = false;
		setDaemon(true);
	}

	public AutoSaveThread(AutoSaveFileProcess fileProcess, byte type, SnapsCalendarRecoverPage layoutControls) {
		mType = type;
		this.layoutControls = layoutControls;
		this.fileProcess = fileProcess;

		isSuspended = false;
		setDaemon(true);
	}

	public AutoSaveThread(AutoSaveFileProcess fileProcess, byte type, AutoSaveProjectInfo info) {
		mType = type;
		this.projectInfo = info;
		this.fileProcess = fileProcess;

		isSuspended = false;
		setDaemon(true);
	}

	@Override
	public void run() {
		super.run();

		if (isSuspended()) {
			return;
		}

		int waitCount = 0;
		while (FILE_TYPE_TEMPLATE == mType && SmartSnapsManager.isSmartAreaSearching() && !isSuspended()) { //얼굴 서칭 중에는 편집 정보가 또 바뀔 수 있으니 기다려준다.
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				Dlog.e(TAG, e);
			}
			if (++waitCount > 60) {
				break;
			}
		}

		if (isSuspended()) {
			return;
		}

		try {

			checkDiectory();

			//우선 임시 파일을 만들고 유효 할 때만 실제 파일을 생성한다.
			File fileTemporary = getFile(false);
			boolean isValidFile = createTempFile(fileTemporary);

			if (isValidFile) {
				File fileReal = getFile(true);
				copyRealFile(fileTemporary, fileReal);
			}

		} catch (Exception e) {
			Dlog.e(TAG, e);
		}
	}

	public void suspendThread() {
		isSuspended = true;
		try {
			interrupt();
		} catch (Exception e) {
			Dlog.e(TAG, e);
		}
	}

	public boolean isSuspended() {
		return isSuspended || isInterrupted();
	}

	public byte getTaskType() {
		return mType;
	}

	private File getFile(boolean isTempFile) {
		AutoSaveManager saveMan = AutoSaveManager.getInstance();
		if (saveMan == null) {
			return null;
		}

		String filePath = saveMan.getFilePath(mType, isTempFile);
		if (filePath != null && filePath.length() > 0) {
			return new File(filePath);
		}
		return null;
	}

	private File getDirectory() {
		AutoSaveManager saveMan = AutoSaveManager.getInstance();
		if (saveMan == null) {
			return null;
		}

		String filePath = saveMan.getFilePath(IAutoSaveConstants.FILE_TYPE_DIRECTORY);
		if (filePath != null && filePath.length() > 0) {
			return new File(filePath);
		}
		return null;
	}

	private void saveObject(ObjectOutputStream stream) throws IOException {
		if (stream == null || isSuspended()) {
			return;
		}
		switch (mType) {
			case IAutoSaveConstants.FILE_TYPE_INFO:
				stream.writeObject(projectInfo);
				break;
			case FILE_TYPE_TEMPLATE:
				stream.writeObject(snapsTemplate);
				break;
			case IAutoSaveConstants.FILE_TYPE_LAYOUT_CONTROLS:
				stream.writeObject(layoutControls);
				break;
			default:
				break;
		}
	}

	private void checkDiectory() {
		if (isSuspended()) {
			return;
		}
		File directory = getDirectory();
		// thumb path 폴더가 없으면 만든다.
		if (!directory.exists()) {
			directory.mkdirs();
		}
	}

	/**
	 * 정상적으로 임시파일을 생성했을 때만 true를 반환한다.
	 */
	private boolean createTempFile(File file) throws IOException {
		if (isSuspended() || file == null) {
			return false;
		}

		if (!file.exists()) {
			file.createNewFile();
			file.setWritable(true);
			file.setReadable(true);
		}

		FileOutputStream fos = null;
		ObjectOutputStream oos = null;
		try {
			fos = new FileOutputStream(file);
			oos = new ObjectOutputStream(fos);

			//object 저장
			saveObject(oos);

			return checkValidObjectFile(file);

		} catch (FileNotFoundException e) {
			Dlog.e(TAG, e);
		} catch (IOException e) {
			Dlog.e(TAG, e);
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					Dlog.e(TAG, e);
				}
			}
			if (oos != null) {
				try {
					oos.close();
				} catch (IOException e) {
					Dlog.e(TAG, e);
				}
			}
		}

		return false;
	}

	private boolean checkValidObjectFile(File file) {
		if (isSuspended()
				|| file == null
				|| !file.exists()
				|| file.length() < 1
				|| fileProcess == null) {
			return false;
		}

		Object obj = fileProcess.getObjectFromFile(file);
		if (obj == null) {
			return false;
		}

		return true;
	}

	private void copyRealFile(File tempFile, File realFile) {
		if (isSuspended() || tempFile == null || realFile == null) {
			return;
		}

		synchronized (tempFile) {
			if (realFile.exists()) {
				realFile.delete();
			}

			if (!realFile.exists()) {
				try {
					realFile.createNewFile();
					realFile.setWritable(true);
					realFile.setReadable(true);
				} catch (IOException e) {
					Dlog.e(TAG, e);
				}
			}

			FileUtil.copyFile(tempFile, realFile);

			tempFile.delete();
		}
	}
}
