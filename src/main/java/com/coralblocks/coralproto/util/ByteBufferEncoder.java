package com.coralblocks.coralproto.util;

import java.nio.ByteBuffer;

public class ByteBufferEncoder {

	private final StringBuilder sb;
	
	public ByteBufferEncoder() {
		int maxLength = 1 + String.valueOf(Long.MIN_VALUE).length();
		this.sb = new StringBuilder(maxLength);
	}
	
	public final void append(ByteBuffer buf, int number) {
		sb.setLength(0);
		sb.append(number);
		ByteBufferUtils.appendCharSequence(buf, sb);
	}
	
	public final void append(ByteBuffer buf, long number) {
		sb.setLength(0);
		sb.append(number);
		ByteBufferUtils.appendCharSequence(buf, sb);
	}
	
}