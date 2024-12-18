/* 
 * Copyright 2015-2024 (c) CoralBlocks LLC - http://www.coralblocks.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package com.coralblocks.coralproto.field;

import java.nio.ByteBuffer;

import com.coralblocks.coralproto.util.ByteBufferCharSequence;
import com.coralblocks.coralproto.util.ByteBufferUtils;

public class VarChars {
	
	private final ByteBufferCharSequence bbcs;
	private final int maxLength;
	
	public VarChars(int maxLength) {
		this.bbcs = new ByteBufferCharSequence(maxLength);
		this.maxLength = maxLength;
		clear();
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof VarChars) {
			VarChars vcf = (VarChars) o;
			return vcf.bbcs.equals(this.bbcs);
		}
		return false;
	}
	
	public void clear() {
		this.bbcs.setSize(0);		
	}
	
	public final int getMaxLength() {
		return maxLength;
	}
	
	private final void enforceMaxLength(int size) {
		if (size > maxLength) throw new RuntimeException("Size larger than maxLength: " + size + " (maxLength=" + maxLength + ")");
	}

	public final int size() {
		int len = bbcs.length();
		return 4 + len;
	}

	public final void set(CharSequence cs) {
		int len = cs.length();
		enforceMaxLength(len);
		bbcs.setSize(len);
		ByteBuffer byteBuffer = bbcs.getByteBuffer();
		for(int i = 0; i < len; i++) byteBuffer.put((byte) cs.charAt(i));
	}
	
	public final void set(byte[] array) {
		int len = array.length;
		enforceMaxLength(array.length);
		bbcs.setSize(len);
		ByteBuffer byteBuffer = bbcs.getByteBuffer();
		byteBuffer.put(array);
	}
	
	public final void set(char[] array) {
		int len = array.length;
		enforceMaxLength(array.length);
		bbcs.setSize(len);
		ByteBuffer byteBuffer = bbcs.getByteBuffer();
		for(int i = 0; i < len; i++) byteBuffer.put((byte) array[i]);
	}
	
	public final void readFrom(ByteBuffer src) {
		int len = src.getInt();
		enforceMaxLength(len);
		int savedLim = src.limit();
		src.limit(src.position() + len);
		bbcs.setSize(len);
		bbcs.getByteBuffer().put(src);
		src.limit(savedLim);
	}
	
	public final void writeTo(ByteBuffer buf) {
		buf.putInt(bbcs.length());
		ByteBuffer byteBuffer = bbcs.getByteBuffer();
		buf.put(byteBuffer);
	}
	
	public final void writeAsciiTo(ByteBuffer buf) {
		ByteBufferUtils.appendCharSequence(buf, bbcs);
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(bbcs.length());
		sb.append(bbcs);
		return sb.toString();
	}
}