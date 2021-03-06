package android.scanner.common;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Build;

/**
 * 
 * @author hugo
 * 
 */
public class Runnable {

	@SuppressLint("NewApi")
	@SuppressWarnings("unchecked")
	public static void execAsync(AsyncTask<?, ?, ?> task) {
		if (Build.VERSION.SDK_INT >= 11) {
			task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		}
		else {
			task.execute();
		}

	}

}
