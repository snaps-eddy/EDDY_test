package com.snaps.mobile.service.ai;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * 로그 파일 생성 클래스
 * 배포 버전인 경우도 필요에 따라 로그 파일 생성 목적
 */
class LogFile {
    private static final int LOG_FILE_SIZE_LIMIT = 1024 * 1024 * 10;
    private static final int LOG_FILE_MAX_COUNT = 1;
    private Logger mLogger = null;
    private String mLogFileDir;

    public LogFile() {
        mLogger = null;
        mLogFileDir = null;
    }

    public boolean deleteLockFiles() {
        boolean isSuccess = false;
        List<String> lockFilePathList = FileUtils.getFilePathList(mLogFileDir, "lck");
        for(String lockFilePath : lockFilePathList) {
            try {
                File file = new File(lockFilePath);
                isSuccess = file.delete();
            }catch (Exception e) {
            }
        }
        return isSuccess;
    }

    public void create(String logFileDir) {
        mLogFileDir = logFileDir;
        SimpleDateFormat simpleDateFormatDate = new SimpleDateFormat("MM_dd-HH_mm_ss_SSS");
        String fileName = simpleDateFormatDate.format(new Date()) + ".txt";

        try {
            String fileFullPath = logFileDir + File.separator + fileName;
            FileHandler fileHandler = new FileHandler(fileFullPath, LOG_FILE_SIZE_LIMIT, LOG_FILE_MAX_COUNT, false);
            fileHandler.setFormatter(new Formatter() {
                @Override
                public String format(LogRecord r) {
                    return r.getMessage();
                }
            });
            mLogger = Logger.getLogger(LogFile.class.getName());
            mLogger.addHandler(fileHandler);
            mLogger.setLevel(Level.ALL);
            mLogger.setUseParentHandlers(false);
        }catch (IOException e) {
        }
    }

    public void write(String msg) {
        if (mLogger == null) return;
        mLogger.log(Level.INFO, msg + "\n");
    }
}
