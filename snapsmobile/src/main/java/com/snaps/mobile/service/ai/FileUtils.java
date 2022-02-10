package com.snaps.mobile.service.ai;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

class FileUtils {
    private static final String TAG = FileUtils.class.getSimpleName();

    /**
     * 디렉토리 생성
     * @param path
     * @return
     */
    public static boolean mkdirs(String path) {
        if (path == null || path.length() == 0) return false;

        File dir = new File(path);
        if (dir.isDirectory()) return true;

        return dir.mkdirs();
    }

    /**
     * 디렉토리 안의 모든 파일 및 디렉토리를 삭제한다. (단 루트 디렉토리는 삭제하지 않는다.)
     * @param path
     * @return
     */
    public static boolean deleteAllInDirectory(String path) {
        if (path == null || path.length() == 0) return false;

        File dir = new File(path);
        if (dir.isDirectory() == false) return false;

        return deleteAllInDirectory(dir);
    }

    /**
     * 재귀 호출로 모든 파일 및 디렉토리를 삭제한다.
     * @param dir
     * @return
     */
    private static boolean deleteAllInDirectory(File dir) {
        File[] files = dir.listFiles();
        if (files == null) return true;

        for(File file: files) {
            if (file.isDirectory()) {
                if (deleteAllInDirectory(file) == false) return false;
            }
            if (file.delete() == false) return false;
        }

        return true;
    }

    /**
     * 디렉토리 안의 파일중 해당 확장자를 가진 파일의 경로 리스트를 구한다.
     * @param dirPath
     * @param fileExt
     * @return
     */
    public static List<String> getFilePathList(String dirPath, String fileExt) {
        List<String> list = new ArrayList<String>();
        if (dirPath == null || dirPath.length() == 0) return list;
        if (fileExt == null || fileExt.length() == 0) return list;

        File dir = new File(dirPath);
        if (dir.isDirectory() == false) return list;

        File[] files = dir.listFiles();
        if (files == null) return list;

        String tmpFileExt = (fileExt.startsWith(".") ? fileExt : "." + fileExt);
        for(File file : files) {
            String path = file.getAbsolutePath();
            if (path.endsWith(tmpFileExt)) {
                list.add(path);
            }
        }

        return list;
    }

    /**
     * 파일 생성하고 데이터 write
     * @param filePath
     * @param date
     * @return
     */
    public static boolean write(String filePath, String date) {
        if (filePath == null || filePath.length() == 0) return false;
        if (date == null || date.length() == 0) return false;

        File file = new File(filePath);
        if (file.isDirectory()) return false;
        if (file.isFile()) {
            if (file.delete() == false) return false;
        }

        boolean isSuccess = false;
        FileWriter fw = null;
        try {
            fw = new FileWriter(file);
            fw.write(date);
            fw.flush();
            isSuccess = true;
        } catch (IOException e) {
            Loggg.e(TAG, e);
        } finally {
            if (fw != null) {
                try {
                    fw.close();
                } catch (IOException e) {
                    Loggg.e(TAG, e);
                }
            }
        }
        return isSuccess;
    }

    /**
     * zip으로 압축
     * @param inFilePath
     * @param outFilePath
     * @return
     */
    public static boolean compress(String inFilePath, String outFilePath) {
        if (inFilePath == null || inFilePath.length() == 0) return false;
        if (outFilePath == null || outFilePath.length() == 0) return false;

        File inFile = new File(inFilePath);
        if (inFile.isDirectory()) return false;
        if (inFile.isFile() == false) return false;
        if (inFile.canRead() == false) return false;

        File outFile = new File(outFilePath);
        if (outFile.isDirectory()) return false;
        if (outFile.isFile()) {
            if (outFile.delete() == false) return false;
        }

        boolean isSuccess = false;

        FileInputStream fis = null;
        ZipOutputStream zipOut = null;
        try {
            fis = new FileInputStream(inFile);
            FileOutputStream fos = new FileOutputStream(outFilePath);
            zipOut = new ZipOutputStream(fos);

            ZipEntry zipEntry = new ZipEntry(inFile.getName());
            zipOut.putNextEntry(zipEntry);

            byte[] bytes = new byte[4096];
            int length;
            while ((length = fis.read(bytes)) > 0) {
                zipOut.write(bytes, 0, length);
            }
            zipOut.flush();
            zipOut.closeEntry();
            isSuccess = true;
        }catch(Exception e) {
            Loggg.e(TAG, e);
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    Loggg.e(TAG, e);
                }
            }

            if (zipOut != null) {
                try {
                    zipOut.close();
                } catch (IOException e) {
                    Loggg.e(TAG, e);
                }
            }
        }

        return isSuccess;
    }
}
