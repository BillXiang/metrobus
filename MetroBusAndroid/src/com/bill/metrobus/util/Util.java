package com.bill.metrobus.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.UUID;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;

/**
 * 
 * @Description: 一些通用的工具
 * 
 */
public class Util {
	/**
	 * 
	 * @Description: 用于字符串md5加密的方法
	 * @param str
	 * @return md5加密后的字符串，或者null if str==null
	 * @throws NoSuchAlgorithmException
	 * @throws UnsupportedEncodingException
	 */
	public static final String EncoderPwdByMd5(String str) {
		if (str == null) {
			return null;
		}
		MessageDigest md5;
		String newstr = null;// 加密后的字符串
		try {
			// 确定计算方法
			md5 = MessageDigest.getInstance("MD5");
			// BASE64Encoder base64en = new BASE64Encoder();
			// 加密后的字符串
			// newstr = base64en.encode(md5.digest(str.getBytes("utf-8")));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		// catch (UnsupportedEncodingException e) {
		// e.printStackTrace();
		// }
		return newstr;
	}

	/**
	 * 根据参数生成KEY
	 */
	public static SecretKey getKey(String strKey) {
		try {
			KeyGenerator generator = KeyGenerator.getInstance("DES");
			generator.init(new SecureRandom(strKey.getBytes()));
			return generator.generateKey();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 文件file进行DES加密并保存目标文件destFile中
	 * 
	 * @param file
	 *            要加密的文件 如c:/test/srcFile.txt
	 * @param destFile
	 *            加密后存放的文件名 如c:/加密后文件.txt
	 */
	public static void encrypt(String file, String destFile) throws Exception {
		Cipher cipher = Cipher.getInstance("DES");
		cipher.init(Cipher.ENCRYPT_MODE, getKey("his"));
		InputStream is = new FileInputStream(file);
		OutputStream out = new FileOutputStream(destFile);
		CipherInputStream cis = new CipherInputStream(is, cipher);
		byte[] buffer = new byte[1024];
		int r;
		while ((r = cis.read(buffer)) > 0) {
			out.write(buffer, 0, r);
		}
		cis.close();
		is.close();
		out.close();
	}

	/**
	 * 文件采用DES算法解密文件
	 * 
	 * @param file
	 *            已加密的文件 如c:/加密后文件.txt * @param destFile 解密后存放的文件名 如c:/
	 *            test/解密后文件.txt
	 * @param charset
	 *            文件编码
	 */
	public static String decrypt(String file, String charset) throws Exception {
		Cipher cipher = Cipher.getInstance("DES");
		cipher.init(Cipher.DECRYPT_MODE, getKey("his"));
		InputStream is = new FileInputStream(file);
		CipherInputStream cis = new CipherInputStream(is, cipher);
		InputStreamReader inReader = new InputStreamReader(cis, charset);
		char[] cbuf = new char[1024];
		StringBuffer sbBuffer = new StringBuffer();
		int count;
		while ((count = inReader.read(cbuf)) >= 0) {
			sbBuffer.append(new String(cbuf, 0, count));
		}
		cis.close();
		return sbBuffer.toString();
	}

	/**
	 * 
	 * @Description: 将int数字用符号fill在前面填充到length位
	 * @param data
	 * @param length
	 * @param fill
	 * @return
	 */
	public static String fillInt(int data, int length, char fill) {
		String result = null;
		result = String.format("%" + fill + length + "d", data);
		return result;
	}

	public static void main(String[] argvs) {
		// System.out.print(EncoderPwdByMd5(""));
		System.out.print(getCheckCode());
	}

	public static String getCheckCode() {
		java.util.Date date = new java.util.Date();
		return EncoderPwdByMd5(date.getYear() + "" + date.getMonth() + ""
				+ date.getDate() + "" + date.getHours() + "")
				+ "jgk";
	}

	/**
	 * deviceID的组成为：渠道标志+识别符来源标志+hash后的终端识别符
	 * 
	 * 渠道标志为： 1，andriod（a）
	 * 
	 * 识别符来源标志： 1， wifi mac地址（wifi）； 2， IMEI（imei）； 3， 序列号（sn）； 4，
	 * id：随机码。若前面的都取不到时，则随机生成一个随机码，需要缓存。
	 * 
	 * @param context
	 * @return
	 */
	public static String getDeviceId(Context context) {

		StringBuilder deviceId = new StringBuilder();
		// 渠道标志
		deviceId.append("a");

		try {

			// wifi mac地址
			WifiManager wifi = (WifiManager) context
					.getSystemService(Context.WIFI_SERVICE);
			WifiInfo info = wifi.getConnectionInfo();
			String wifiMac = info.getMacAddress();
			if (!isEmpty(wifiMac)) {
				deviceId.append("wifi");
				deviceId.append(wifiMac);
				return deviceId.toString();
			}

			// IMEI（imei）
			TelephonyManager tm = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			String imei = tm.getDeviceId();
			if (!isEmpty(imei)) {
				deviceId.append("imei");
				deviceId.append(imei);
				return deviceId.toString();
			}

			// 序列号（sn）
			String sn = tm.getSimSerialNumber();
			if (!isEmpty(sn)) {
				deviceId.append("sn");
				deviceId.append(sn);
				return deviceId.toString();
			}

			// 如果上面都没有， 则生成一个id：随机码
			String uuid = getUUID(context);
			if (!isEmpty(uuid)) {
				deviceId.append("id");
				deviceId.append(uuid);
				return deviceId.toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
			deviceId.append("id").append(getUUID(context));
		}

		return deviceId.toString();

	}

	/**
	 * 得到全局唯一UUID
	 */
	public static String getUUID(Context context) {
		String uuid = null;
		SharedPreferences mShare = context.getSharedPreferences("MetroBus", Context.MODE_PRIVATE);
		// 获得修改器
		Editor editor = mShare.edit();
		if (mShare != null) {
			uuid = mShare.getString("uuid", "");
		}

		if (isEmpty(uuid)) {
			uuid = UUID.randomUUID().toString();
			editor.putString("uuid", uuid);
			editor.commit();
		}
		return uuid;
	}
	
	public static boolean isEmpty(String string){
		if(string!=null && !string.equals("")){
			return true;
		}
		return false;
	}
}
