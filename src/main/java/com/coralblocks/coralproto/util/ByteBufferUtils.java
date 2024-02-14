package com.coralblocks.coralproto.util;

import java.nio.ByteBuffer;

public class ByteBufferUtils {
	
	private ByteBufferUtils() {
		
	}
	
	public final static void appendCharSequence(ByteBuffer buf, CharSequence s) {
		int len = s.length();
		for (int i = 0; i < len; i++) {
			buf.put((byte) s.charAt(i));
		}
	}
	
	public final static void parseString(ByteBuffer src, StringBuilder sb) {
		int len = src.remaining();
		for (int i = 0; i < len; i++) {
			char c = (char) src.get();
			if (!CharUtils.isPrintable(c)) {
				sb.append('?');
			} else {
				sb.append(c);
			}
		}
	}
	
	public final static String parseString(ByteBuffer src) {
		StringBuilder sb = new StringBuilder(src.remaining());
		parseString(src, sb);
		return sb.toString();
	}
	
}