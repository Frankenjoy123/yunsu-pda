package com.yunsoo.util;

import com.yunsoo.manager.SessionManager;

import java.security.MessageDigest;


public class KeyHelper {
	public static String getWrappedKey(String sOriginKey) {

//		String sKey = SessionManager.getInstance().getId()
//				+ sOriginKey;
//		return sKey;
        return sOriginKey;
	}

	public static String getMD5String(String value) {
		try {
			MessageDigest digest = MessageDigest.getInstance("MD5");
			byte[] data = value.getBytes("UTF-8");
			byte[] hashData = digest.digest(data);
			StringBuffer sb=new StringBuffer();
			for (int i = 0; i < hashData.length; i++) {
			     String hex=Integer.toHexString(0xff & hashData[i]);			    
			     sb.append(hex);
			}
			return sb.toString();


		} catch (Exception e) {
			return String.valueOf(value.hashCode());
		}

	}
}
