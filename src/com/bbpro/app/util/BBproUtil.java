package com.bbpro.app.util;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

public class BBproUtil {

	public static int dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	public static int getScreenWidth(Context context) {
		WindowManager wmManager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		return wmManager.getDefaultDisplay().getWidth();
	}

	public static int getScreenHeight(Context context) {
		WindowManager wmManager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		return wmManager.getDefaultDisplay().getHeight();
	}

	/**
	 * 检测是否有网络连接
	 * 
	 * @return
	 */
	public static boolean isOnNet(Context mCon) {
		ConnectivityManager cm = (ConnectivityManager) mCon
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cm.getActiveNetworkInfo();
		if (info != null && info.isConnected() && info.isAvailable()) {

			return true;
		}
		return false;
	}

	public static void hideSoftKeyboard(Context context, View v) {
		InputMethodManager imm = (InputMethodManager) context
				.getSystemService(Activity.INPUT_METHOD_SERVICE);
		if (v != null) {
			if (imm.isActive()) {
				imm.hideSoftInputFromWindow(v.getApplicationWindowToken(),
						InputMethodManager.HIDE_NOT_ALWAYS);
			}
		}
	}

	public static void getLocation(Context context, double[] mylocation) {

		LocationManager locationManager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			Location location = locationManager
					.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			if (location != null) {
				mylocation[0] = location.getLatitude();
				mylocation[1] = location.getLongitude();
			}
		} else {
			LocationListener locationListener = new LocationListener() {

				// Provider的状态在可用、暂时不可用和无服务三个状态直接切换时触发此函数

				// Provider被enable时触发此函数，比如GPS被打开
				@Override
				public void onProviderEnabled(String provider) {

				}

				// Provider被disable时触发此函数，比如GPS被关闭
				@Override
				public void onProviderDisabled(String provider) {

				}

				// 当坐标改变时触发此函数，如果Provider传进相同的坐标，它就不会被触发
				@Override
				public void onLocationChanged(Location location) {
					if (location != null) {
						Log.e("Map",
								"Location changed : Lat: "
										+ location.getLatitude() + " Lng: "
										+ location.getLongitude());
					}
				}

				@Override
				public void onStatusChanged(String provider, int status,
						Bundle extras) {
					// TODO Auto-generated method stub

				}
			};
			locationManager
					.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
							1000, 0, locationListener);
			Location location = locationManager
					.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			if (location != null) {
				mylocation[0] = location.getLatitude(); // 经度
				mylocation[1] = location.getLongitude(); // 纬度
			}
		}
	}
}
