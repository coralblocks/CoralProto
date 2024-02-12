package com.coralblocks.coralproto.util;

public class CharUtils {
	
	private CharUtils() {
		
	}
	
	public final static boolean isPrintable(char c) {
		byte b = (byte) c;
		if (b >= 32 && b <= 126) return true;
		return false;
	}
	
}