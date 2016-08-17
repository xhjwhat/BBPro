package com.bbpro.app.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.json.JSONException;
import org.json.JSONObject;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.SystemClock;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

public class BitMapUtil {

	/**
	 * @param bmp
	 * @param filename
	 * @return 存储图片到本地
	 */
	public static boolean saveBitmap2fileForSetting(Bitmap bmp, String filename) {

		CompressFormat format = Bitmap.CompressFormat.JPEG;
		OutputStream stream = null;
		int quality = 100;
		try {

			if (!new File(filename).exists()) {
				File file = new File(filename);
				File pfile = file.getParentFile();
				if (pfile != null && !pfile.exists()) {//clear null pointer
					pfile.mkdirs();
				}
				if (!file.exists()) {
					file.createNewFile();
				}
			}
			stream = new FileOutputStream(filename);
		} catch (FileNotFoundException e) { 
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bmp.compress(format, quality, stream);
	}

	/**
	 * @param bmp
	 * @param filename
	 * @param quality
	 * @return 存储图片到本地
	 */
	public static boolean saveBitmap2fileForSetting(Bitmap bmp, String filename,int quality) {

		CompressFormat format = Bitmap.CompressFormat.JPEG;
		OutputStream stream = null;
		try {

			if (!new File(filename).exists()) {
				File file = new File(filename);
				File pfile = file.getParentFile();
				if (pfile != null && !pfile.exists()) {//clear null pointer
					pfile.mkdirs();
				}
				if (!file.exists()) {
					file.createNewFile();
				}
			}
			stream = new FileOutputStream(filename);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bmp.compress(format, quality, stream);
	}
	/**
	 * @param filepath
	 * @return 获取本地图片,不缩放
	 */
	public static Bitmap getBitmap2file(String filepath) {

		File file = new File(filepath);
		Bitmap bm = null;
		if (file.exists()) {
			bm = BitmapFactory.decodeFile(filepath);
		}
		return bm;
	}
	
	public static Bitmap compressImage(Bitmap image,String path,BitmapFactory.Options option) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while ( baos.toByteArray().length / 1024 > 200) {    //循环判断如果压缩后图片是否大于100kb,大于继续压缩        
            baos.reset();//重置baos即清空baos
            options -= 3;//每次都减少10
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
        }
        try {
			FileOutputStream fos = new FileOutputStream(path);
			fos.write(baos.toByteArray());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, option);//把ByteArrayInputStream数据生成图片
        return bitmap;
    }
	public static Bitmap getCompressBitmap(String filePath,int compress){
		
		
		BitmapFactory.Options option = new BitmapFactory.Options();
		option.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath,option);
		if(option.outWidth >= option.outHeight && option.outWidth > compress){
			option.inSampleSize = option.outWidth/compress;
			option.outHeight = option.outHeight*compress/option.outWidth;
			option.outWidth = compress;
		}else if(option.outHeight > option.outWidth && option.outHeight > compress){
			option.inSampleSize = option.outHeight/compress;
			option.outWidth = option.outWidth*compress/option.outHeight;
			option.outHeight = compress;
		}
		option.inJustDecodeBounds = false;
		Bitmap tempBitmap = BitmapFactory.decodeFile(filePath,option);
		int degree = readPictureDegree(filePath);
		if(degree == 90){
			return rotaingImageView(90, tempBitmap);
		}
		return tempBitmap;
		//return compressImage(tempBitmap, filePath,option);
	}
	
	/**
	 * @param bmp
	 * @param filename
	 * @return 存储图片到本地
	 */
	public static boolean saveBitmap2file(Bitmap bmp, String filename) {
		CompressFormat format = Bitmap.CompressFormat.JPEG;
		int quality = 85;
		OutputStream stream = null;
		try {

			if (!new File(filename).exists()) {
				File file = new File(filename);
				File pfile = file.getParentFile();
				if (pfile != null && !pfile.exists()) {//clear null pointer
					pfile.mkdirs();
				}
				file.createNewFile();
			}
			stream = new FileOutputStream(filename);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return bmp.compress(format, quality, stream);
	}

	
	/**
	 * @param img
	 * @param filepath
	 *            保存图片到imgview中
	 */
	public static void saveBitmaptoViewAndZoom(ImageView img, String filepath) {
		File file = new File(filepath);
		if (file.exists()) {
			img.setImageBitmap(getImageChatThumbnail(filepath));
		}
	}

	/**
	 * @param imagePath
	 * @return
	 */
	public static Bitmap getImageChatThumbnail(String imagePath) {

		Bitmap bitmap = null;
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = false;
		options.inSampleSize = 4;
		bitmap = BitmapFactory.decodeFile(imagePath, options);

		// 计算缩放比
		int w = options.outWidth;
		int h = options.outHeight;
		// 计算缩放比例
		float scaleWidth = 4 / 3f; // ((float) newWidth) / options.outWidth;
		float scaleHeight = 4 / 3f;// ((float) newHeight) / options.outHeight;

		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		// 得到新的图片
		Bitmap newbm = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		bitmap.recycle();
		return newbm;
	}
	
	
	/**
     * 读取图片属性：旋转的角度
     * @param path 图片绝对路径
     * @return degree旋转的角度
     */
    public static int readPictureDegree(String path) {
        int degree  = 0;
        try {
                ExifInterface exifInterface = new ExifInterface(path);
                int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                        degree = 90;
                        break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                        degree = 180;
                        break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                        degree = 270;
                        break;
                }
        } catch (IOException e) {
                e.printStackTrace();
        }
        return degree;
    }
   /**
    * 旋转图片 
    * @param angle 
    * @param bitmap 
    * @return Bitmap 
    */ 
   public static Bitmap rotaingImageView(int angle , Bitmap bitmap) {  
       //旋转图片 动作   
       Matrix matrix = new Matrix();;  
       matrix.postRotate(angle);  
       // 创建新的图片   
       Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,  
               bitmap.getWidth(), bitmap.getHeight(), matrix, true);  
       return resizedBitmap;  
   }
   
}
