package com.bbpro.app.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.List;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;


public class FileUtil {
	private static final String PATH_DOCUMENT = "document";
	public static final String PROVIDER_INTERFACE = "android.content.action.DOCUMENTS_PROVIDER";
	public final static String FILE_EXTENSION_SEPARATOR = ".";

	public static boolean isFileExist(String filePath) {
		if (TextUtils.isEmpty(filePath)) {
			return false;
		}
		File file = new File(filePath);
		return (file.exists() && file.isFile());
	}

	public static boolean makeDirs(String filePath) {
		String folderName = getFolderName(filePath);
		if (TextUtils.isEmpty(folderName)) {
			return false;
		}
		File folder = new File(folderName);
		return (folder.exists() && folder.isDirectory()) ? true : folder.mkdirs();
	}

	/*
	 * @param filePath
	 * 
	 * @return
	 */
	public static String getFolderName(String filePath) {

		if (TextUtils.isEmpty(filePath)) {
			return filePath;
		}

		int filePosi = filePath.lastIndexOf(File.separator);
		return (filePosi == -1) ? "" : filePath.substring(0, filePosi);
	}

	public static boolean isFolderExist(String directoryPath) {
		if (TextUtils.isEmpty(directoryPath)) {
			return false;
		}

		File dire = new File(directoryPath);
		return (dire.exists() && dire.isDirectory());
	}

	

	public static String getPath(final Context context, final Uri uri) {
		if ("content".equalsIgnoreCase(uri.getScheme())) {
			if (isGooglePhotosUri(uri))
				return uri.getLastPathSegment();
			return getDataColumn(context, uri, null, null);
		} else if ("file".equalsIgnoreCase(uri.getScheme())) {
			return uri.getPath();
		}
		return null;
	}

	public static String getPathWithApi19(Context context, Uri uri) {
		if (isExternalStorageDocument(uri)) {
			final String docId = getDocumentId(uri);
			final String[] split = docId.split(":");
			final String type = split[0];

			if ("primary".equalsIgnoreCase(type)) {
				return Environment.getExternalStorageDirectory() + "/" + split[1];
			}
		} else if (isDownloadsDocument(uri)) {

			final String id = getDocumentId(uri);
			final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

			return getDataColumn(context, contentUri, null, null);
		} else if (isMediaDocument(uri)) {
			final String docId = getDocumentId(uri);
			final String[] split = docId.split(":");
			final String type = split[0];

			Uri contentUri = null;
			if ("image".equals(type)) {
				contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
			} else if ("video".equals(type)) {
				contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
			} else if ("audio".equals(type)) {
				contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
			}

			final String selection = "_id=?";
			final String[] selectionArgs = new String[] { split[1] };

			return getDataColumn(context, contentUri, selection, selectionArgs);
		}
		return null;
	}

	public static String getDocumentId(Uri documentUri) {
		final List<String> paths = documentUri.getPathSegments();
		if (paths.size() < 2) {
			throw new IllegalArgumentException("Not a document: " + documentUri);
		}
		if (!PATH_DOCUMENT.equals(paths.get(0))) {
			throw new IllegalArgumentException("Not a document: " + documentUri);
		}
		return paths.get(1);
	}

	public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {

		Cursor cursor = null;
		final String column = "_data";
		final String[] projection = { column };

		try {
			cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
			if (cursor != null && cursor.moveToFirst()) {
				final int index = cursor.getColumnIndexOrThrow(column);
				return cursor.getString(index);
			}
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return null;
	}

	public static boolean isExternalStorageDocument(Uri uri) {
		return "com.android.externalstorage.documents".equals(uri.getAuthority());
	}

	public static boolean isDownloadsDocument(Uri uri) {
		return "com.android.providers.downloads.documents".equals(uri.getAuthority());
	}

	public static boolean isMediaDocument(Uri uri) {
		return "com.android.providers.media.documents".equals(uri.getAuthority());
	}

	public static boolean isGooglePhotosUri(Uri uri) {
		return "com.google.android.apps.photos.content".equals(uri.getAuthority());
	}

	public static int getFileSize(File file) {
		FileInputStream fis;
		int len = 0;
		try {
			fis = new FileInputStream(file);
			len = fis.available();
			fis.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return len;
	}

	public static void copyFile(File oldFile, File newFile) {
		try {
			int byteread = 0;
			if (oldFile.exists()) { // 文件存在时
				InputStream inStream = new FileInputStream(oldFile); // 读入原文件
				FileOutputStream fs = new FileOutputStream(newFile);
				byte[] buffer = new byte[1024];
				while ((byteread = inStream.read(buffer)) != -1) {
					fs.write(buffer, 0, byteread);
				}
				inStream.close();
				fs.flush();
				fs.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static byte[] toByteArray(File f) throws IOException {

		if (!f.exists()) {
			throw new FileNotFoundException("");
		}

		ByteArrayOutputStream bos = new ByteArrayOutputStream((int) f.length());
		BufferedInputStream in = null;
		try {
			in = new BufferedInputStream(new FileInputStream(f));
			int buf_size = 1024;
			byte[] buffer = new byte[buf_size];
			int len = 0;
			while (-1 != (len = in.read(buffer, 0, buf_size))) {
				bos.write(buffer, 0, len);
			}
			return bos.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		} finally {
			try {
				if(null != in){//clear null pointer
					in.close();
				}				
			} catch (IOException e) {
				e.printStackTrace();
			}
			bos.close();
		}
	}

	public static byte[] getBitmapByte(Bitmap bitmap) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
		try {
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return out.toByteArray();
	}

	public static String getFileExtension(String filePath) {
		if (TextUtils.isEmpty(filePath)) {
			return filePath;
		}

		int extenPosi = filePath.lastIndexOf(FILE_EXTENSION_SEPARATOR);
		int filePosi = filePath.lastIndexOf(File.separator);
		if (extenPosi == -1) {
			return "";
		}
		return (filePosi >= extenPosi) ? "" : filePath.substring(extenPosi + 1);
	}

	// 将字符串写入到文本文件中
	public static void WriteTxtFile(String strcontent, String strFilePath) {
		// 每次写入时，都换行写
		String strContent = strcontent + "\n";
		try {
			File file = new File(strFilePath);
			if (!file.exists()) {
				file.createNewFile();
			}
			RandomAccessFile raf = new RandomAccessFile(file, "rw");
			raf.seek(file.length());
			raf.write(strContent.getBytes());
			raf.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

   public static boolean deleteFile(String path) {
       if (TextUtils.isEmpty(path)) {
           return true;
       }

       File file = new File(path);
       if (!file.exists()) {
           return true;
       }
       if (file.isFile()) {
           return file.delete();
       }
       if (!file.isDirectory()) {
           return false;
       }

       try{//clear null pointer
		   for (File f : file.listFiles()) {
               if (f.isFile()) {
                   f.delete();
               } else if (f.isDirectory()) {
                   deleteFile(f.getAbsolutePath());
               }
           } 
	   }catch (NullPointerException e){
		   //null
	   }       	   

       return file.delete();
   }

	
}
