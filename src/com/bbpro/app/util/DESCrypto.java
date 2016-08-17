package com.bbpro.app.util;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Security;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;


/**
 * DES加密和解密类
 */
public class DESCrypto {
  
	private static String defaultDesKey = "12345678";

	  private Cipher encryptCipher = null;

	  private Cipher decryptCipher = null;
	  
	  private String charsetName = "UTF-8";
	  
	  
	  private static byte[] iv = { 1, 2, 3, 4, 5, 6, 7, 8 };
	  //十六进制下数字到字符的映射数组  
	    private final static String[] hexDigits = {"0", "1", "2", "3", "4",  
	        "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};  

	  /**
	   * 将byte数组转换为表示16进制值的字符串， 如：byte[]{8,18}转换为：0813， 和public static byte[]
	   * hexStr2ByteArr(String strIn) 互为可逆的转换过程
	   *
	   * @param arrB
	   *            需要转换的byte数组
	   * @return 转换后的字符串
	   * @throws Exception
	   *             本方法不处理任何异常，所有异常全部抛出
	   */
	  public static String byteArr2HexStr(byte[] arrB) throws Exception {
	    int iLen = arrB.length;
	    // 每个byte用两个字符才能表示，所以字符串的长度是数组长度的两倍
	    StringBuffer sb = new StringBuffer(iLen * 2);
	    for (int i = 0; i < iLen; i++) {
	      int intTmp = arrB[i];
	      // 把负数转换为正数
	      while (intTmp < 0) {
	        intTmp = intTmp + 256;
	      }
	      // 小于0F的数需要在前面补0
	      if (intTmp < 16) {
	        sb.append("0");
	      }
	      sb.append(Integer.toString(intTmp, 16));
	    }
	    return sb.toString();
	  }

	  /**
	   * 将表示16进制值的字符串转换为byte数组， 和public static String byteArr2HexStr(byte[] arrB)
	   * 互为可逆的转换过程
	   *
	   * @param strIn
	   *            需要转换的字符串
	   * @return 转换后的byte数组
	   * @throws Exception
	   *             本方法不处理任何异常，所有异常全部抛出
	   * @author <a href="mailto:leo841001@163.com">LiGuoQing</a>
	   */
	  public static byte[] hexStr2ByteArr(String strIn) throws Exception {
	    byte[] arrB = strIn.getBytes();
	    int iLen = arrB.length;

	    // 两个字符表示一个字节，所以字节数组长度是字符串长度除以2
	    byte[] arrOut = new byte[iLen / 2];
	    for (int i = 0; i < iLen; i = i + 2) {
	      String strTmp = new String(arrB, i, 2);
	      arrOut[i / 2] = (byte) Integer.parseInt(strTmp, 16);
	    }
	    return arrOut;
	  }

	  /**
	   * 默认构造方法，使用默认密钥
	   *
	   * @throws Exception
	   */
	  public DESCrypto() throws Exception {
	    this(defaultDesKey);
	  }

	  /**
	   * 指定密钥构造方法
	   *
	   * @param strKey
	   *            指定的密钥
	   * @throws Exception
	   */
	  public DESCrypto(String strKey) {
	    //Security.addProvider(new com.sun.crypto.provider.SunJCE());
	    try {
	      Key key = getKey(strKey.getBytes());
	      IvParameterSpec ivSpec = new IvParameterSpec(iv);
	      encryptCipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
	      encryptCipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);

	      decryptCipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
	      decryptCipher.init(Cipher.DECRYPT_MODE, key, ivSpec);
	    } catch (InvalidKeyException e) {
	      e.printStackTrace();
	    } catch (NoSuchAlgorithmException e) {
	      e.printStackTrace();
	    } catch (NoSuchPaddingException e) {
	      e.printStackTrace();
	    } catch (Exception e) {
	      e.printStackTrace();
	    }
	  }

	  /**
	   * 加密字节数组
	   *
	   * @param arrB
	   *            需加密的字节数组
	   * @return 加密后的字节数组
	   * @throws Exception
	   */
	  public byte[] encrypt(byte[] arrB) throws Exception {
	    return encryptCipher.doFinal(arrB);
	  }

	  /**
	   * 加密字符串
	   *
	   * @param strIn
	   *            需加密的字符串
	   * @return 加密后的字符串
	   * @throws Exception
	   */
	  public String encrypt(String strIn) {
		  String result = "";
		    try {
		      result = byteArr2HexStr(encrypt(strIn.getBytes(charsetName)));
		      return result;
		    } catch (UnsupportedEncodingException e) {
		      e.printStackTrace();
		    } catch (Exception e) {
		      e.printStackTrace();
		    }
		    return result;
	  }

	  /**
	   * 解密字节数组
	   *
	   * @param arrB
	   *            需解密的字节数组
	   * @return 解密后的字节数组
	   * @throws Exception
	   */
	  public byte[] decrypt(byte[] arrB) throws Exception {
	    return decryptCipher.doFinal(arrB);
	  }

	  /**
	   * 解密字符串
	   *
	   * @param strIn
	   *            需解密的字符串
	   * @return 解密后的字符串
	   * @throws Exception
	   */
	  public String decrypt(String strIn) throws Exception {
	    return new String(decrypt(hexStr2ByteArr(strIn)), charsetName);
	  }

	  /**
	   * 从指定字符串生成密钥，密钥所需的字节数组长度为8位 不足8位时后面补0，超出8位只取前8位
	   *
	   * @param arrBTmp
	   *            构成该字符串的字节数组
	   * @return 生成的密钥
	   * @throws java.lang.Exception
	   */
	  private Key getKey(byte[] arrBTmp) throws Exception {
	    // 创建一个空的8位字节数组（默认值为0）
	    byte[] arrB = new byte[8];

	    // 将原始字节数组转换为8位
	    for (int i = 0; i < arrBTmp.length && i < arrB.length; i++) {
	      arrB[i] = arrBTmp[i];
	    }

	    // 生成密钥
	    Key key = new javax.crypto.spec.SecretKeySpec(arrB, "DES");
	    return key;
	  }
	  
	  public static String GetMD5Code(String strObj) {
	        String resultString = null;
	        try {
	            resultString = new String(strObj);
	            MessageDigest md = MessageDigest.getInstance("MD5");
	            // md.digest() 该函数返回值为存放哈希值结果的byte数组
	            resultString = byteToString(md.digest(strObj.getBytes()));
	        } catch (NoSuchAlgorithmException ex) {
	            ex.printStackTrace();
	        }
	        return resultString.toLowerCase();
	    }
	  
	  
	  /**  
	     * 转换字节数组为十六进制字符串 
	     * @param     字节数组 
	     * @return    十六进制字符串 
	     */  
	    private static String byteToString(byte[] b){  
	        StringBuffer resultSb = new StringBuffer();  
	        for (int i = 0; i < b.length; i++){  
	            resultSb.append(byteToHexString(b[i]));  
	        }  
	        return resultSb.toString();  
	    }  
	      
	    /** 将一个字节转化成十六进制形式的字符串     */  
	    private static String byteToHexString(byte b){  
	        int n = b;  
	        if (n < 0)  
	            n = 256 + n;  
	        int d1 = n / 16;  
	        int d2 = n % 16;  
	        return hexDigits[d1] + hexDigits[d2];  
	    }  
	  
	  public static void main(String[] args) {	
		try {
			
//			String aa="AE79B33B63BA28A0443B62D1EC1032FF";
//			System.out.println(aa.toLowerCase());
			String strKey = "12345678";
			// 需要加密的字串mm FHYX-001
			String data = "si=7&cd=0003&ap=18565699305,ae79b33b63ba28a0443b62d1ec1032ff&pc=1&ps=5&pg=1";
			// 加密
			long lStart = System.currentTimeMillis();
			DESCrypto des = new DESCrypto(strKey);
			String encriptStr = des.encrypt(data);
			System.out.println("after encrypt : >>  "+encriptStr);
//			long lUseTime = System.currentTimeMillis() - lStart;
//			// 解密
			String decriptStr = des.decrypt("d138e1153f15c339d11a3ebeba8e5ea1ba61a90481690ff93f5f1b9cab50b66b012f2613d2bb652a7266e74d130e19148cb038c542a02ea180e36e58189d414dc602f5f014064cd4a2fbcef4e8eaab9a");
			System.out.println("after decrypt:  >>>  " + decriptStr);
		} catch (Exception e) {
			e.printStackTrace();
		}
	  }
	}


