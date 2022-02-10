package com.snaps.common.utils.file;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import android.webkit.MimeTypeMap;

import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.net.xml.GetMultiPartMethod;
import com.snaps.common.utils.thread.ATask;
import com.snaps.common.utils.thread.ATask.OnTask;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.edit.view.custom_progress.SnapsTimerProgressView;
import com.snaps.mobile.activity.edit.view.custom_progress.SnapsTimerProgressViewFactory;
import com.snaps.mobile.activity.webview.WebviewActivity;
import com.snaps.mobile.interfaces.OnFileUploadListener;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.StreamCorruptedException;

import errorhandle.logger.SnapsInterfaceLogDefaultHandler;

public class FileUtil {
	private static final String TAG = FileUtil.class.getSimpleName();

	public static String getMimeType(String url) throws Exception {
		String type = null;
		String extension = MimeTypeMap.getFileExtensionFromUrl(url);
		if (extension != null) {
			type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
		}
		return type;
	}

	/**
	 * 폴더까지 모두 삭제
	 *
	 * @param targetFolder
	 * @return
	 */
	public static boolean deleteFolder(String targetFolder) {
		return deleteFolder(new File(targetFolder));
	}

	/**
	 * 폴더까지 모두 삭제
	 *
	 * @param targetFolder
	 * @return
	 */
	public static boolean deleteFolder(File targetFolder) {
		if (!targetFolder.exists()) {
			return true;
		}
		try {
			File[] childFile = targetFolder.listFiles();
			if (childFile != null) {
				int size = childFile.length;

				if (size > 0) {
					for (int i = 0; i < size; i++) {
						if (childFile[i].isFile()) {
							childFile[i].delete();
						} else {
							deleteFolder(childFile[i]);
						}
					}
				}
			}
			targetFolder.delete();
		} catch (Exception e) {
			Dlog.e(TAG, e);
		}
		return (!targetFolder.exists());
	}

	public static void deleteFile(String path) {
		try {
			File file = new File(path);
			if (file.exists()) {
				file.delete();
			}
		} catch (Exception e) {
			Dlog.e(TAG, e);
		}
	}

	/**
	 * 폴더 내 파일만 삭제
	 *
	 * @param targetFolder
	 * @return
	 */
	public static boolean deleteFolderInFiles(File targetFolder) {
		if (targetFolder == null || !targetFolder.exists()) {
			return true;
		}
		try {
			File[] childFile = targetFolder.listFiles();
			if (childFile != null) {
				int size = childFile.length;

				if (size > 0) {
					for (int i = 0; i < size; i++) {
						if (childFile[i].isFile()) {
							childFile[i].delete();
						} else {
							deleteFolder(childFile[i]);// 폴더 내 폴더는 폴더까지 삭제함.
						}
					}
				}
			}
			return true;
		} catch (Exception e) {
			Dlog.e(TAG, e);
		}
		return false;
	}

	public static boolean copyAssets(Context context, String srcAssets, String tgtDir, int fileCount) {
		File targetDir = new File(tgtDir);
		if (!targetDir.exists()) {
			targetDir.mkdirs();
		} else {
			if (targetDir.list().length >= fileCount)// 이미파일이 있으면 리턴
			{
				return false;
			}
		}

		try {
			AssetManager assetManager = context.getAssets();
			String[] files = assetManager.list(srcAssets);
			for (int i = 0; i < files.length; i++) {
				InputStream in = assetManager.open(srcAssets + "/" + files[i]);
				OutputStream out = new FileOutputStream(tgtDir + files[i]);

				copyFile(in, out);

				in.close();
				in = null;
				out.flush();
				out.close();
				out = null;
			}
			return true;
		} catch (Exception e) {
			Dlog.e(TAG, e);
		}
		return false;
	}

	public static boolean copyAssetFile(Context context, String assetSrcFullPath, String targetFullPath) {
		try {
			AssetManager assetManager = context.getAssets();
			InputStream in = assetManager.open(assetSrcFullPath);
			OutputStream out = new FileOutputStream(targetFullPath);

			copyFile(in, out);

			in.close();
			in = null;
			out.flush();
			out.close();
			out = null;
			return true;
		} catch (Exception e) {
			Dlog.e(TAG, e);
		}
		return false;
	}

	static void copyFile(InputStream in, OutputStream out) throws IOException {
		Dlog.d("copyFile() input is available:" + in.available());
		byte[] buffer = new byte[in.available()];
		int read;
		while ((read = in.read(buffer)) != -1) {
			out.write(buffer, 0, read);
		}
	}

	/***
	 * 시리얼 오브젝트를 내부 메모리에 저장을 한다.
	 *
	 * @param context
	 * @param object
	 * @param fileName
	 * @return
	 */
	static public boolean saveInnerFile(Context context, Serializable object, String fileName) {
		synchronized (object) {
			FileOutputStream outputStream;

			try {
				outputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
				ObjectOutputStream os = new ObjectOutputStream(outputStream);
				os.writeObject(object);
				os.close();
			} catch (FileNotFoundException e) {
				Dlog.e(TAG, e);
				return false;
			} catch (IOException e) {
				Dlog.e(TAG, e);
				return false;
			}

			return true;
		}
	}

	/**
	 * object to file
	 */
	public static boolean saveToFile(Object object, File file) {
		if (object == null || file == null) {
			return false;
		}

		FileOutputStream fos = null;
		ObjectOutputStream oos = null;
		try {
			if (!file.exists()) {
				file.createNewFile();
				file.setWritable(true);
				file.setReadable(true);
			}

			fos = new FileOutputStream(file);
			oos = new ObjectOutputStream(fos);

			oos.writeObject(object);
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
		return true;
	}

	public static boolean writeFileWithStr(@NonNull File file, String contents) {
		BufferedWriter writer = null;
		try {
			if (contents == null) {
				contents = "";
			}

			File targetDir = file.getParentFile();
			if (!targetDir.exists()) {
				targetDir.mkdirs();
			}

			if (file.exists()) {
				file.delete();
			}

			writer = new BufferedWriter(new FileWriter(file.getAbsolutePath()));
			writer.write(contents);
			return true;
		} catch (IOException e) {
			Dlog.e(TAG, e);
		} finally {
			try {
				if (writer != null) {
					writer.flush();
					writer.close();
				}
			} catch (IOException e) {
				Dlog.e(TAG, e);
			}
		}
		return false;
	}

	/**
	 * file to object
	 */
	public static Object loadfromFile(File file) {
		if (file == null) {
			return null;
		}

		FileInputStream inputStream = null;
		Object object;
		try {
			inputStream = new FileInputStream(file);
			ObjectInputStream is = new ObjectInputStream(inputStream);
			object = is.readObject();
			is.close();
		} catch (FileNotFoundException e) {
			Dlog.e(TAG, e);
			return null;
		} catch (StreamCorruptedException e) {
			Dlog.e(TAG, e);
			return null;
		} catch (IOException e) {
			Dlog.e(TAG, e);
			return null;
		} catch (ClassNotFoundException e) {
			Dlog.e(TAG, e);
			return null;
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					Dlog.e(TAG, e);
				}
			}
		}

		return object;
	}

	/***
	 * 시리얼 오브젝트를 내부 메모리에 로드를 한다.
	 *
	 * @param context
	 * @param fileName
	 * @return
	 */
	static public Serializable readInnerFile(Context context, String fileName) {

		FileInputStream inputStream = null;
		Serializable object = null;
		try {
			inputStream = context.openFileInput(fileName);
			ObjectInputStream is = new ObjectInputStream(inputStream);
			object = (Serializable) is.readObject();
			is.close();
		} catch (FileNotFoundException e) {
			Dlog.e(TAG, e);
			return null;
		} catch (StreamCorruptedException e) {
			Dlog.e(TAG, e);
			return null;
		} catch (IOException e) {
			Dlog.e(TAG, e);
			return null;
		} catch (ClassNotFoundException e) {
			Dlog.e(TAG, e);
			return null;
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					Dlog.e(TAG, e);
				}
			}
		}

		return object;
	}

	/**
	 * 내부메모리에 저장이 된 파일을 삭제하는 함수.
	 *
	 * @param context
	 * @param fileName
	 * @return
	 */
	static public boolean deleteInnerFile(Context context, String fileName) {
		boolean isSuccess;
		isSuccess = context.deleteFile(fileName);
		return isSuccess;

	}

	/**
	 * 갤러리를 호출한다.
	 */
	public static void callGallery(Activity act) {
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_PICK);
		intent.setType("image/*");

		act.startActivityForResult(Intent.createChooser(intent, "File Chooser"), WebviewActivity.REQUEST_CODE_OPEN_GALLERY);
	}

	/**
	 * 첨부 파일 서버의 임시 경로로 전송
	 */
	public static void sendAttachFile(final Context context, Uri uri, final OnFileUploadListener listener) {
		final File file = FileUtil.uriToFile(context, uri);
		ATask.executeVoid(new OnTask() {

			String result = null;

			@Override
			public void onPre() {
				SnapsTimerProgressView.showProgress((Activity) context,
						SnapsTimerProgressViewFactory.eTimerProgressType.PROGRESS_TYPE_LOADING,
						context.getString(R.string.file_uploading_msg));
			}

			@Override
			public void onPost() {
				SnapsTimerProgressView.destroyProgressView();
				if (result != null) {
					try {
						JSONObject json = new JSONObject(result);
						String result = (String) json.get("status");
						String fileName = (String) json.get("fileName");
						if (result != null && result.equalsIgnoreCase("success")) {
							if (fileName != null && fileName.length() > 0) {
								if (listener != null) {
									listener.onFinished(fileName);
									return;
								}
							}
						}
					} catch (Exception e) {
						Dlog.e(TAG, e);
					}
				}

				if (listener != null) {
					listener.onFinished(null);
				}
			}

			@Override
			public void onBG() {
				try {
					result = GetMultiPartMethod.getAttachFileUpload(file, null, SnapsInterfaceLogDefaultHandler.createDefaultHandler());
				} catch (ClientProtocolException e) {
					Dlog.e(TAG, e);
				} catch (IOException e) {
					Dlog.e(TAG, e);
				}
			}
		});
	}

	public static File uriToFile(Context context, Uri contentUri) {
		Cursor cursor = null;
		String filePath = null;

		if (context != null && context.getContentResolver() != null) {
			try {
				String[] proj = {MediaStore.Images.Media.DATA};
				cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
				if (cursor != null) {
					int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
					cursor.moveToFirst();
					filePath = cursor.getString(column_index);
				}
			} catch (Exception e) {
				Dlog.e(TAG, e);
			} finally {
				if (cursor != null) {
					cursor.close();
				}
			}
		}

		if (filePath != null && filePath.length() > 0) {
			return new File(filePath);
		}

		return null;
	}

	public static boolean copyFile(File srcFile, File tarFile) {
		if (srcFile == null || tarFile == null || !srcFile.exists() || !tarFile.exists() || srcFile == tarFile) {
			return false;
		}

		FileInputStream instream = null;
		FileOutputStream outstream = null;
		int length = 0;
		try {
			instream = new FileInputStream(srcFile);
			length = instream.available();

			if (length <= 0) {
				instream.close();
				return false;
			}

			outstream = new FileOutputStream(tarFile);

			byte[] buffer = new byte[length];
			instream.read(buffer);
			outstream.write(buffer);
		} catch (IOException e) {
			Dlog.e(TAG, e);
			return false;
		} catch (OutOfMemoryError e) {
			Dlog.e(TAG, e);
			return false;
		} catch (Exception e) {
			Dlog.e(TAG, e);
			return false;
		} finally {
			if (instream != null) {
				try {
					instream.close();
				} catch (IOException e) {
					Dlog.e(TAG, e);
				}
			}

			if (outstream != null) {
				try {
					outstream.flush();
					if (outstream.getFD() != null) {
						outstream.getFD().sync();
					}
				}catch (Exception e) {
					Dlog.e(TAG, e);
				}finally {
					try {
						outstream.close();
					} catch (IOException e) {
						Dlog.e(TAG, e);
					}
				}
			}
		}

		return waitFlush(tarFile, length);
	}

	public static boolean waitFlush(File file, long bytes) {
		if (file == null || !file.exists() || bytes <= 0) {
			return false;
		}

		int itr = 0;
		while (file.length() != bytes) {
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				Dlog.e(TAG, e);
			}

			if (++itr >= 2000 / 200) {
				return false;
			}
		}

		return true;
	}

	public static boolean isExistFile(String fileName) {
		File file = new File(fileName);
		return file.exists() && file.length() > 0;
	}

	/**
	 * file을 확인하고 없으면 새로 만든다.
	 *
	 * @param path
	 * @return
	 */
	public static boolean createFile(String path) {
		File file = new File(path);
		File parent = file.getParentFile();

		if (!parent.exists() || !parent.isDirectory()) {
			parent.mkdirs();
		}

		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				Dlog.e(TAG, e);
				return false;
			}
		}

		return true;
	}

	public static String readFile(File file) {
		if (file == null || !file.exists()) {
			return null;
		}
		StringBuilder result = new StringBuilder();
		BufferedReader br = null;
		try {
			String buffer = "";
			br = new BufferedReader(new FileReader(file));
			while ((buffer = br.readLine()) != null) {
				result.append(buffer);
			}
		} catch (IOException e) {
			Dlog.e(TAG, e);
		} finally {
			try {
				if (br != null) {
					br.close();
				}
			} catch (IOException ex) {
				Dlog.e(TAG, ex);
			}
		}
		return result.toString();
	}

	public static String getStringFromAsset(Context context, String fileName) {
		if (context == null) {
			return null;
		}

		AssetManager assetManager = context.getAssets();
		BufferedReader in = null;
		InputStream ims = null;
		try {
			ims = assetManager.open(fileName);
			in = new BufferedReader(new InputStreamReader(ims, "UTF-8"));
			String str;
			StringBuilder buffer = new StringBuilder();

			while ((str = in.readLine()) != null) {
				buffer.append(str);
			}

			return buffer.toString();
		} catch (IOException e) {
			Dlog.e(TAG, e);
		} finally {
			try {
				if (ims != null) {
					ims.close();
				}

				if (in != null) {
					in.close();
				}
			} catch (IOException e) {
				Dlog.e(TAG, e);
			}
		}
		return null;
	}

	public static void initProjectFileSaveStorage() {
		try {
			Config.checkThumbnailFileDir();
			File thumbPath = Config.getThumbnailFileDir();
			FileUtil.deleteFolderInFiles(thumbPath);// 폴더 내 파일 삭제
		} catch (Exception e) {
			Dlog.e(TAG, e);
		}

		try {
			Config.checkProjectFileDir();
			File projectPath = Config.getProjectFileDir();
			FileUtil.deleteFolderInFiles(projectPath);// 폴더 내 파일 삭제
		} catch (Exception e) {
			Dlog.e(TAG, e);
		}
	}

	public static String convertStreamToString(InputStream is) {
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader reader = new BufferedReader(isr);
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append((line));
			}
		} catch (IOException e) {
			Dlog.e(TAG, e);
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					Dlog.e(TAG, e);
				}
			}

			if (isr != null) {
				try {
					isr.close();
				} catch (IOException e) {
					Dlog.e(TAG, e);
				}
			}

			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					Dlog.e(TAG, e);
				}
			}
		}
		return sb.toString();
	}
}
