package com.alexsen.chronometer.utils;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

public class ToastUtils {

	public static void showToast(Context cntx, String msg) {
		Toast toastClear = Toast.makeText(cntx, msg, Toast.LENGTH_SHORT);
		toastClear.setGravity(Gravity.TOP, 0, 70);
		toastClear.show();		
	}

	public static void showToast(Context cntx, int msgId) {
		Toast toastClear = Toast.makeText(cntx, msgId, Toast.LENGTH_SHORT);
		toastClear.setGravity(Gravity.TOP, 0, 70);
		toastClear.show();		
	}		
	
}
