package com.snaps.common.utils.log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import android.os.AsyncTask;
import android.os.Environment;

public class FileLogAsynTask extends AsyncTask<String, String, Long> {
	private static final String TAG = FileLogAsynTask.class.getSimpleName();
	@Override
	protected Long doInBackground(String... arg0) {
		
		String logString = "";
		if(arg0.length == 1){
			logString = arg0[0];
		}else if(arg0.length >= 2){
			logString = arg0[0] + arg0[1];
		}
		
		writeToFile(logString);
		return null;
	}

	void writeToFile(String log) {
		String str_Path_Full = Environment.getExternalStorageDirectory().getAbsolutePath() + "/download/snpaslog.txt";
		File file = new File(str_Path_Full);
		if (file.exists() == false) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				Dlog.e(TAG, e);
			}
		} else {
			try {
				BufferedWriter bfw = new BufferedWriter(new FileWriter(str_Path_Full, true));
				bfw.write(log);
				bfw.write("\n");
				bfw.flush();
				bfw.close();
			} catch (FileNotFoundException e) {
				Dlog.e(TAG, e);
			} catch (IOException e) {
				Dlog.e(TAG, e);
			}
		}
	}

}
