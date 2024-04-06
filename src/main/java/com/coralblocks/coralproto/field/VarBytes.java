/* 
 * Copyright 2024 (c) CoralBlocks - http://www.coralblocks.com
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

import com.coralblocks.coralproto.util.ByteBufferUtils;
import com.coralblocks.coralproto.util.CharUtils;

public class VarBytes {
	
	private final ByteBuffer byteBuffer;
	private int size;
	
	public VarBytes(int maxLength) {
		this.byteBuffer = ByteBuffer.allocate(maxLength);
		clear();
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof VarBytes) {
			VarBytes vbf = (VarBytes) o;
			vbf.byteBuffer.limit(vbf.size).position(0);
			this.byteBuffer.limit(this.size).position(0);
			return vbf.byteBuffer.equals(this.byteBuffer);
		}
		return false;
	}
	
	public void clear() {
		this.size = 0;
	}
	
	public final int getMaxLength() {
		return byteBuffer.capacity();
	}
	
	private final void enforceMaxLength(int size) {
		if (size > byteBuffer.capacity()) throw new RuntimeException("Size larger than maxLength: " + size + " (maxLength=" + byteBuffer.capacity() + ")");
	}
	
	public final int size() {
		return 4 + size;
	}

	public final void set(byte[] array, int offset, int len) {
		enforceMaxLength(len);
		byteBuffer.clear();
		byteBuffer.put(array, offset, len);
		byteBuffer.flip();
		this.size = len;
	}
	
	public final void set(byte[] array) {
		enforceMaxLength(array.length);
		byteBuffer.clear();
		byteBuffer.put(array);
		byteBuffer.flip();
		this.size = array.length;
	}
	
	public final void set(ByteBuffer buf) {
		enforceMaxLength(buf.remaining());
		byteBuffer.clear();
		byteBuffer.put(buf);
		byteBuffer.flip();
		this.size = byteBuffer.remaining();
	}
	
	public final void readFrom(ByteBuffer src) {
		byteBuffer.clear();
		int len = src.getInt();
		enforceMaxLength(len);
		int savedLim = src.limit();
		src.limit(src.position() + len);
		byteBuffer.put(src);
		byteBuffer.flip();
		src.limit(savedLim);
		this.size = len;
	}
	
	public final void writeTo(ByteBuffer buf) {
		buf.putInt(size);
		byteBuffer.limit(size).position(0);
		buf.put(byteBuffer);
	}
	
	public final void writeAsciiTo(ByteBuffer buf) {
		byteBuffer.limit(size).position(0);
		int len = byteBuffer.remaining();
		for(int i = 0; i < len; i++) {
			byte b = byteBuffer.get();
			if (CharUtils.isPrintable((char) b)) {
				buf.put((byte) b);
			} else {
				buf.put((byte) '?');
			}
		}
	}
	
	@Override
	public String toString() {
		byteBuffer.limit(size).position(0);
		StringBuilder sb = new StringBuilder(byteBuffer.remaining());
		sb.append(ByteBufferUtils.parseString(byteBuffer));
		return sb.toString();
	}
}