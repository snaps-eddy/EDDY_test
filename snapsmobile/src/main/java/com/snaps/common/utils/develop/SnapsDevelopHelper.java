package com.snaps.common.utils.develop;

import android.app.Activity;
import android.content.Context;
import androidx.annotation.NonNull;

import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.net.CNetStatus;
import com.snaps.common.utils.thread.ATask;
import com.snaps.common.utils.ui.ContextUtil;
import com.snaps.common.utils.ui.MessageUtil;
import com.snaps.mobile.utils.pref.StringCrypto;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;

public class SnapsDevelopHelper {
	private static final String TAG = SnapsDevelopHelper.class.getSimpleName();

	public static boolean IS_USE_CMD_CATCH = false; //이거 켜면 CMD를 계속 보여 준다.
	public static final boolean IS_USE_IMAGE_UPLOAD_HISTORY = true; //이거 켜면 한번 업로드 한 사진은 기록 해 놓고 다시 안 올림(개발할 때만 사용..)
	public static boolean IS_USE_UNIQUE_XML_FILE_NAME = false; //이거 켜면 한번 업로드 할 때, 디버깅용 XML 파일을 유니크 하게 생성한다.

	private static final String THUMBNAIL_UPLOAD_HISTORY_FILE_PATH = "snaps/develop/t_upload_history.dat";
	private static final String ORG_UPLOAD_HISTORY_FILE_PATH = "snaps/develop/o_upload_history.dat";
	private static final String FILE_UPLOAD_HISTORY_FILE_SEPARATOR = "[S]";
	private static final String FILE_UPLOAD_HISTORY_FILE_END_POINT = "[E]";

	private static final String CUSTOM_LOG_FILE_PATH = "/snaps/customLog";
	private static final String CMD_LOG_FILE_NAME = "/cmdLog.dat";

	public static boolean isCMDCatchMode() {
		return Config.isDevelopVersion() && SnapsDevelopHelper.IS_USE_CMD_CATCH;
	}

	public static void uploadCMDLogToFTPServer(final Activity act, final String log) {
		if (!Config.isDevelopVersion()) {
			return;
		}

		if (!CNetStatus.getInstance().isWifiConnected(ContextUtil.getContext())) {
			return;
		}

		ATask.executeVoidWithThreadPool(new ATask.OnTask() {
			@Override
			public void onPre() {
			}

			@Override
			public void onBG() {
				FTPClient fileClient = new FTPClient();
				boolean successLogin = false;
				try {
					fileClient.connect(StringCrypto.decAES128("VTdXjo83etAJUWMvVAIB2g=="));
					if (!FTPReply.isPositiveCompletion(fileClient.getReplyCode())) {
						return;
					}

					successLogin = fileClient.login(StringCrypto.decAES128("0L+sW2aei/Gu56l+fniQ5w=="), StringCrypto.decAES128("JUsYYCtBBEQLCWUtUKhOCQ=="));
					if (successLogin) {
						fileClient.enterLocalPassiveMode();
						fileClient.setBufferSize(1024 * 1024);
						fileClient.setFileType(FTP.BINARY_FILE_TYPE);

						final String SERVER_PATH = StringCrypto.decAES128("jrL1qH8OuFhgYmNNn00Ad0VpFAKM4jt1zU9MgaS+b8KBUg4k7heeJZVgsa9FPPSKECe1Uf2eZxTcL5iogP+p/A==");
						final String FTP_FULL_PATH = StringCrypto.decAES128("gznynIgMjJI+YtAlyfVyh29lQvVP15l0EP/HJYMdhip+Ss0iBFxdBieCFuo1wIaE");

						File cmdFile = writeCMDLogToCMDLogFile(act, log);
						FileInputStream cmdFileInputStream = new FileInputStream(cmdFile);
						String cmdFileName = getXMLFileFTPPath("cmd");
						fileClient.storeFile(FTP_FULL_PATH + cmdFileName, cmdFileInputStream);
						cmdFileInputStream.close();
					}
				} catch (IOException e) {
					Dlog.e(TAG, e);
				} catch (Exception e) {
					Dlog.e(TAG, e);
				} finally {
					try {
						if (successLogin) {
							fileClient.logout();
						}
					} catch (IOException e) {
						Dlog.e(TAG, e);
					}

					try {
						fileClient.disconnect();
					} catch (IOException e) {
						Dlog.e(TAG, e);
					}
				}
			}

			private String getXMLFileFTPPath(String name) {
				return name + ".txt";
			}

			@Override
			public void onPost() {
				MessageUtil.toast(act, "전송 완료 : http://m.snaps.kr/Upload/Data1/mobile/develop/xml/cmd.txt");
			}
		});
	}

	private static File writeCMDLogToCMDLogFile(Context context, String log) {
		File customLogFile = null;
		try {
			customLogFile = new File(getCMDLogFileFullPath(context));

			if (customLogFile.exists()) {
				deletePrevCMDLogFile();
			}

			createCMDLogFile(context);

			BufferedWriter bfw = new BufferedWriter(new FileWriter(customLogFile, true));
			bfw.write(log);
			bfw.write("\n");
			bfw.flush();
			bfw.close();
		} catch (Exception e) {
			Dlog.e(TAG, e);
		}
		return customLogFile;
	}

	private static File createCMDLogFile(@NonNull Context context) {
		File customLogFile = null;
		try {
			customLogFile = new File(getCustomLogFilePath(context));
			if (!customLogFile.exists()) {
				customLogFile.mkdirs();
			}

			customLogFile = new File(getCMDLogFileFullPath(context));
			if (!customLogFile.exists()) {
				customLogFile.createNewFile();
				customLogFile.setWritable(true);
				customLogFile.setReadable(true);
			}
		} catch (Exception e) {
			Dlog.e(TAG, e);
		}
		return customLogFile;
	}

	private static String getCustomLogFilePath(Context context) {
		return Config.getExternalCacheDir(context) + CUSTOM_LOG_FILE_PATH;
	}

	private static String getCMDLogFileFullPath(Context context) {
		return Config.getExternalCacheDir(context) + CUSTOM_LOG_FILE_PATH + CMD_LOG_FILE_NAME;
	}

	private static void deletePrevCMDLogFile() throws Exception {
		File prevCustomInfoFile = new File(getCMDLogFileFullPath(ContextUtil.getContext()));
		if (prevCustomInfoFile.exists()) {
			prevCustomInfoFile.delete();
		}
	}
}
