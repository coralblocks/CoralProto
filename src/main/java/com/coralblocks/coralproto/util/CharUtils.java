package com.coralblocks.coralproto.util;

public class CharUtils {
	
	private CharUtils() {
		
	}
	
	public final static boolean isPrintable(char c) {
		byte b = (byte) c;
		if (b >= 32 && b <= 126) return true;
		return false;
	}
	
	public final static short toShort(CharSequence s) {
		
		int len = s.length();
		
		return  (short) (
				((( (len > 0 ? ((byte) s.charAt(0)) : (byte) ' ') & 0xFF)) << 0L)
				+  ((( (len > 1 ? ((byte) s.charAt(1)) : (byte) ' ') & 0xFF)) << 8L)
				);
	}
	
	public final static short toShort(byte b1, byte b2) {
		
		return  (short) ( ( (b1 & 0xFF) << 0L ) +  ( (b2 & 0xFF) << 8L ) );
	}
	
}