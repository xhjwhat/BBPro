package com.bbpro.app.util;

import java.security.Key;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

import android.net.MailTo;
import android.text.TextUtils;
import android.util.Base64;

public class StringUtil {

	public static boolean isMobileNum(String mobiles) {
		String regex = "^((13[0-9])|(15[^4,\\D])|(18[0,5-9])|(17[0-9]))\\d{8}$";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(mobiles);
		return m.matches();
	}

	public static boolean isStandar(String str){
    	Pattern pattern = Pattern.compile("(^[a-zA-Z0-9~!@#$%&*_+-]{6,64}$)");
    	Matcher m = pattern.matcher(str);
    	if(m.matches()){
    		return true;
    	}else{
    		return false;
    	}
    }
	/**
	 * @param nickname
	 *            输入的是字母或者中文
	 * @return
	 */
	public static boolean checkNickName(String nickname) {
		String regex = "^[a-zA-Z\u4e00-\u9fa5]+$";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(nickname);
		return m.matches();
	}

	public static final String ALGORITHM_DES = "DES/CBC/PKCS5Padding";

	/**
	 * DES算法，加密
	 * 
	 * @param data
	 *            待加密字符串
	 * @param key
	 *            加密私钥，长度不能够小于8位
	 * @return 加密后的字节数组，一般结合Base64编码使用
	 * @throws CryptException
	 *             异常
	 */
	public static String encode(String key, String data) throws Exception {
		return encode(key, data.getBytes());
	}

	/**
	 * DES算法，加密
	 * 
	 * @param data
	 *            待加密字符串
	 * @param key
	 *            加密私钥，长度不能够小于8位
	 * @return 加密后的字节数组，一般结合Base64编码使用
	 * @throws CryptException
	 *             异常
	 */
	public static String encode(String key, byte[] data) throws Exception {
		try {
			DESKeySpec dks = new DESKeySpec(key.getBytes());

			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
			// key的长度不能够小于8位字节
			Key secretKey = keyFactory.generateSecret(dks);
			Cipher cipher = Cipher.getInstance(ALGORITHM_DES);
			IvParameterSpec iv = new IvParameterSpec("12345678".getBytes());
			AlgorithmParameterSpec paramSpec = iv;
			cipher.init(Cipher.ENCRYPT_MODE, secretKey, paramSpec);

			byte[] bytes = cipher.doFinal(data);

			return Base64.encodeToString(bytes,Base64.DEFAULT);
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	/**
	 * DES算法，解密
	 * 
	 * @param data
	 *            待解密字符串
	 * @param key
	 *            解密私钥，长度不能够小于8位
	 * @return 解密后的字节数组
	 * @throws Exception
	 *             异常
	 */
	public static byte[] decode(String key, byte[] data) throws Exception {
		try {
			SecureRandom sr = new SecureRandom();
			DESKeySpec dks = new DESKeySpec(key.getBytes());
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
			// key的长度不能够小于8位字节
			Key secretKey = keyFactory.generateSecret(dks);
			Cipher cipher = Cipher.getInstance(ALGORITHM_DES);
			IvParameterSpec iv = new IvParameterSpec("12345678".getBytes());
			AlgorithmParameterSpec paramSpec = iv;
			cipher.init(Cipher.DECRYPT_MODE, secretKey, paramSpec);
			return cipher.doFinal(data);
		} catch (Exception e) {
			throw new Exception(e);
		}
	}
	

}
