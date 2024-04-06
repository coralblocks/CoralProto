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

import com.coralblocks.coralproto.util.ByteBufferCharSequence;

public class Chars {
	
	private final ByteBufferCharSequence bbcs;
	private final int size;
	
	public Chars(int size) {
		this.bbcs = new ByteBufferCharSequence(size);
		this.size = size;
		clear();
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof Chars) {
			Chars cf = (Chars) o;
			return cf.bbcs.equals(this.bbcs);
		}
		return false;
	}
	
	public void clear() {
		ByteBuffer byteBuffer = this.bbcs.getByteBuffer();
		for(int i = 0; i < byteBuffer.capacity(); i++) byteBuffer.put((byte) ' ');
	}
	
	public final int size() {
		return size;
	}

	public final void set(CharSequence cs) {
		int len = cs.length();
		if (len > size) {
			throw new IllegalArgumentException("CharSequence is larger than field length: " + cs.toString() + " (" + size + ")");
		}
		ByteBuffer byteBuffer = bbcs.getByteBuffer();
		for(int i = 0; i < len; i++) byteBuffer.put((byte) cs.charAt(i));
		for(int i = len; i < size; i++) byteBuffer.put((byte) ' ');
	}
	
	public final void set(byte[] array) {
		int len = array.length;
		if (len > size) {
			throw new IllegalArgumentException("Array is larger than field length: " + len);
		}
		ByteBuffer byteBuffer = bbcs.getByteBuffer();
		byteBuffer.put(array);
		for(int i = len; i < size; i++) byteBuffer.put((byte) ' ');
	}
	
	public final void set(char[] array) {
		int len = array.length;
		if (len > size) {
			throw new IllegalArgumentException("Array is larger than field length: " + len);
		}
		ByteBuffer byteBuffer = bbcs.getByteBuffer();
		for(int i = 0; i < len; i++) byteBuffer.put((byte) array[i]);
		for(int i = len; i < size; i++)  byteBuffer.put((byte) ' ');
	}
	
	public final ByteBuffer getByteBuffer() {
		return bbcs.getByteBuffer();
	}
	
	public final void readFrom(ByteBuffer src) {
		ByteBuffer byteBuffer = bbcs.getByteBuffer();
		int savedLim = src.limit();
		src.limit(src.position() + byteBuffer.capacity());
		byteBuffer.put(src);
		src.limit(savedLim);
	}
	
	public final void writeTo(ByteBuffer buf) {
		ByteBuffer byteBuffer = bbcs.getByteBuffer();
		buf.put(byteBuffer);
	}
	
	public final void writeAsciiTo(ByteBuffer buf) {
		ByteBuffer byteBuffer = bbcs.getByteBuffer();
		buf.put(byteBuffer);
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(size + 2);
		sb.append('[');
		sb.append(bbcs);
		sb.append(']');
		return sb.toString();
	}
}