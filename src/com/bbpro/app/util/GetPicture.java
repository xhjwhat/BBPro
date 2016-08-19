package com.bbpro.app.util;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import com.bbpro.app.net.Constants;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;


public class GetPicture {
	/* 请求码 */
	public static final int IMAGE_REQUEST_CODE = 0;
	public static final int CAMERA_REQUEST_CODE = 1;
	public static final int RESULT_REQUEST_CODE = 2;
	public static final int SELECT_PIC_KITKAT = 4;

    public Activity mcontext;
	public  static String picPath = "";
	public static String picName = "";
    public static String fileFulPath = "";
	public GetPicture(Activity context) {
		super();
		mcontext = context;
		//String picpath = Constants.BBPRO_FILE;
		//createPicPath(picname,picpath);
	}
	
//	public void createPicPath(String picname, String picpath) {
//		if(null == picname || picname.equals("")){//clear find bugs null pointer
//			picName = String.valueOf(System.currentTimeMillis()) + "temp.jpg";
//		}else{
//			picName = picname;
//		}
//		if(null == picpath || picpath.equals("")){//clear find bugs null pointer
//			picPath = Environment.getExternalStorageDirectory() + File.separator +"bbpro"+File.separator;
//		}else{
//			picPath= picpath;
//		}
//		fileFulPath = picPath+ picName;
//		if (!FileUtil.isFileExist(picPath)) {
//			File	localfile = new File(picPath + picName);
//			File pfile = localfile.getParentFile();
//			if (pfile != null && !pfile.exists()) {//clear find bugs null pointer
//				pfile.mkdirs();
//			}
//			try {
//				localfile.createNewFile();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//	}
	
	/**
	 * 从本地获取图片
	 */
	public void choosePicFromLocal() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		intent.setType("image/*");
		if (android.os.Build.VERSION.SDK_INT >= 19) {
			mcontext.startActivityForResult(intent, SELECT_PIC_KITKAT);
		} else {
			mcontext.startActivityForResult(intent, IMAGE_REQUEST_CODE);
		}
	}
	
	/**
	 * 通过拍照获取
	 */
	public void choosePicFromCarma(String file) {
		if (!FileUtil.isFileExist(file)) {
			File localfile = new File(file);
			File pfile = localfile.getParentFile();
			if (pfile != null && !pfile.exists()) {//clear find bugs null pointer
				pfile.mkdirs();
			}
			try {
				localfile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		Intent intentFromCapture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		// 判断存储卡是否可以用，可用进行存储
			File	localfile = new File(file);
			intentFromCapture.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(localfile));
			intentFromCapture.putExtra(MediaStore.Images.Media.ORIENTATION, 1);
			intentFromCapture.setFlags(PendingIntent.FLAG_UPDATE_CURRENT);
			intentFromCapture.putExtra("return-data", false);
		mcontext.startActivityForResult(intentFromCapture, CAMERA_REQUEST_CODE);
//		Intent intent = new Intent(
//				android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//		File	localfile = new File(fileFulPath);
//		Uri u = Uri.fromFile(localfile);
//		//intent.putExtra(MediaStore.Images.Media.ORIENTATION, 1);
//		intent.putExtra(MediaStore.EXTRA_OUTPUT, u);
//		intent.setFlags(PendingIntent.FLAG_UPDATE_CURRENT);
//		mcontext.startActivityForResult(intent, CAMERA_REQUEST_CODE);
	}

	/**
	 * 裁剪图片方法实现
	 * @param uri
	 *   spectX aspectY 是宽高的比例 outputX outputY 是裁剪图片宽高
	 */
	public void startCropPhoto(Uri uri) {
		if (uri == null) {
			return;
		}
		Intent intent = new Intent("com.android.camera.action.CROP");
		if (android.os.Build.VERSION.SDK_INT >= 19) {
			String url = FileUtil.getPathWithApi19(mcontext, uri);
			if (url == null) {
				intent.setDataAndType(uri, "image/*");
			} else {
				intent.setDataAndType(Uri.fromFile(new File(url)), "image/*");
			}
		} else {
			intent.setDataAndType(uri, "image/*");
		}
		// 设置裁剪
		intent.putExtra("crop", "true");
		intent.putExtra("scale", true);
		intent.setFlags(PendingIntent.FLAG_UPDATE_CURRENT);
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX outputY 是裁剪图片宽高
		intent.putExtra("outputX", 300);
		intent.putExtra("outputY", 300);
		intent.putExtra("return-data", true);
		mcontext.startActivityForResult(intent, RESULT_REQUEST_CODE);
	}
	
	/**
	 * 压缩图片
	 * @param uri
	 */
	public boolean startPhotoZoom(Uri uri, String fileName) {	

		 if(FileUtil.getFileSize(new File(fileName)) > 400 * 1000){	
				Bitmap tempBitmap = BitMapUtil.getCompressBitmap(fileName, 800);
				return BitMapUtil.saveBitmap2fileForSetting(tempBitmap, fileName,90);
		}else{
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			Bitmap	photo=null;
			try {
			 BitmapFactory.decodeStream(getInputStreamFromUriString(uri.toString()), null, options);
			int desw = mcontext.getWindowManager().getDefaultDisplay().getWidth();
			int desh = mcontext.getWindowManager().getDefaultDisplay().getHeight();
			options.inSampleSize = calculateSampleSize(options.outWidth, options.outHeight, desw, desh);
			options.inJustDecodeBounds = false;
			photo = BitmapFactory.decodeStream(getInputStreamFromUriString(uri.toString()), null, options);
			
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			if(null == photo){//clear find bugs null pointer
				return false;
			}
			
		 return BitMapUtil.saveBitmap2fileForSetting(photo, fileName);
		}
	 
	}
	
	
	private InputStream getInputStreamFromUriString(String uriString) throws IOException {
		if (uriString.startsWith("content")) {
			Uri uri = Uri.parse(uriString);
			return mcontext.getContentResolver().openInputStream(uri);
		} else if (uriString.startsWith("file://")) {
			int question = uriString.indexOf("?");
			if (question > -1) {
				uriString = uriString.substring(0, question);
			}
			if (uriString.startsWith("file:///android_asset/")) {
				Uri uri = Uri.parse(uriString);
				String relativePath = uri.getPath().substring(15);
				return mcontext.getAssets().open(relativePath);
			} else {
				return new FileInputStream(getRealPath(uriString));
			}
		} else {
			return new FileInputStream(getRealPath(uriString));
		}
	}

	private static final String _DATA = "_data";

	private String getRealPath(String uriString) {
		String realPath = null;

		if (uriString.startsWith("content://")) {
			String[] proj = { _DATA };
			Cursor cursor = mcontext.managedQuery(Uri.parse(uriString), proj, null, null, null);
			int column_index = cursor.getColumnIndexOrThrow(_DATA);
			cursor.moveToFirst();
			realPath = cursor.getString(column_index);
			if (realPath == null) {
			}
		} else if (uriString.startsWith("file://")) {
			realPath = uriString.substring(7);
			if (realPath.startsWith("/android_asset/")) {
				realPath = null;
			}
		} else {
			realPath = uriString;
		}

		return realPath;
	}

	/**
	 * Figure out what ratio we can load our image into memory at while still
	 * being bigger than our desired width and height
	 * 
	 * @param srcWidth
	 * @param srcHeight
	 * @param dstWidth
	 * @param dstHeight
	 * @return
	 */
	public static int calculateSampleSize(int srcWidth, int srcHeight, int dstWidth, int dstHeight) {
		// final float wAspect = (float) srcWidth / (float) dstWidth;
		// final float hAspect = (float) srcHeight / (float) dstHeight;
		// if (srcWidth > srcHeight) {
		// return (int) wAspect;
		// } else {
		// return (int) hAspect;
		// }
		final float srcAspect = (float) srcWidth / (float) srcHeight;
		final float dstAspect = (float) dstWidth / (float) dstHeight;

		if (srcAspect > dstAspect) {
			return (int) Math.ceil((float) srcWidth / (float) dstWidth);
			// return srcWidth / dstWidth;
		} else {
			return (int) Math.ceil((float) srcHeight / (float) dstHeight);
			// return srcHeight / dstHeight;
		}
	}

	public void toEditPhotoFromMedia(Uri uri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setFlags(PendingIntent.FLAG_UPDATE_CURRENT);
		intent.setDataAndType(uri, "image/*");
		//intent.putExtra("scale", true);
		intent.putExtra("noFaceDetection", true);
		intent.putExtra("return-data", true);
		intent.putExtra("aspectX", 1);// 这两项为裁剪框的比例.
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", 200);
		intent.putExtra("outputY", 200);
		intent.putExtra("outputFormat", "JPEG");
		intent.putExtra("output", Uri.fromFile(new File(picPath+"temp.jpg")));
		mcontext.startActivityForResult(intent, RESULT_REQUEST_CODE);
	}
}
