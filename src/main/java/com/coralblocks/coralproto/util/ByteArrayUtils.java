package com.coralblocks.coralproto.util;

public class ByteArrayUtils {
	
	private ByteArrayUtils() {
		
	}
	
	public static String parseString(byte[] src) {
		int len = src.length;
		char[] chars = new char[len];
		for (int i = 0; i < len; i++) {
			char c = (char) src[i];
			if (CharUtils.isPrintable(c)) {
				chars[i] = c;	
			} else {
				chars[i] = '?';
			}
		}
		return new String(chars);
	}
}