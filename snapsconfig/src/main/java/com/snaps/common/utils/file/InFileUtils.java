package com.snaps.common.utils.file;

import android.content.Context;

import com.snaps.common.utils.log.Dlog;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StreamCorruptedException;

/**
 * Created by ifunbae on 16. 2. 25..
 */
public class InFileUtils {
    private static final String TAG = InFileUtils.class.getSimpleName();
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

    /***
     * 시리얼 오브젝트를 내부 메모리에 로드를 한다.
     *
     * @param context
     * @param fileName
     * @return
     */
    static public Serializable readInnerFile(Context context, String fileName) {

        FileInputStream inputStream;
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


}
