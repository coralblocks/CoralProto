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

public class Bytes {
	
	private final ByteBuffer byteBuffer;
	
	public Bytes(int size) {
		this.byteBuffer = ByteBuffer.allocateDirect(size);
		clear();
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof Bytes) {
			Bytes bf = (Bytes) o;
			bf.byteBuffer.limit(bf.byteBuffer.capacity()).limit(0);
			this.byteBuffer.limit(this.byteBuffer.capacity()).limit(0);
			return this.byteBuffer.equals(bf.byteBuffer);
		}
		return false;
	}
	
	public void clear() {
		this.byteBuffer.clear();
		for(int i = 0; i < byteBuffer.capacity(); i++) this.byteBuffer.put((byte) 0);
		this.byteBuffer.flip();
	}
	
	public final int size() {
		return byteBuffer.capacity();
	}

	private final ByteBuffer get() {
		byteBuffer.limit(byteBuffer.capacity()).position(0);
		return byteBuffer;
	}
	
	public final void set(byte[] array) {
		if (array.length != byteBuffer.capacity()) {
			throw new IllegalArgumentException("Invalid array length: " + array.length);
		}
		ByteBuffer bb = get();
		bb.put(array);
		bb.flip();
	}
	
	public final void set(ByteBuffer buf) {
		if (buf.remaining() != byteBuffer.capacity()) {
			throw new IllegalArgumentException("Invalid ByteBuffer size: " + buf.remaining());
		}
		ByteBuffer bb = get();
		bb.put(buf);
		bb.flip();
	}
	
	public final void readFrom(ByteBuffer src) {
		if (src.remaining() < byteBuffer.capacity()) {
			throw new IllegalArgumentException("ByteBuffer is too small: " + src.remaining());
		}
		byteBuffer.clear();
		int savedLim = src.limit();
		src.limit(src.position() + byteBuffer.capacity());
		byteBuffer.put(src);
		byteBuffer.flip();
		src.limit(savedLim);
	}
	
	public final void writeTo(ByteBuffer buf) {
		if (buf.remaining() < byteBuffer.capacity()) throw new IllegalArgumentException("Give ByteBuffer does not have space available: " + buf.remaining());
		ByteBuffer bb = get();
		buf.put(bb);
	}
	
	public final void writeAsciiTo(ByteBuffer buf) {
		if (buf.remaining() < byteBuffer.capacity()) throw new IllegalArgumentException("Give ByteBuffer does not have space available: " + buf.remaining());
		ByteBuffer bb = get();
		for(int i = 0; i < bb.capacity(); i++) {
			byte b = bb.get();
			if (CharUtils.isPrintable((char) b)) {
				buf.put((byte) b);
			} else {
				buf.put((byte) '?');
			}
		}
	}
	
	@Override
	public String toString() {
		ByteBuffer bb = get();
		StringBuilder sb = new StringBuilder(bb.capacity() + 2);
		sb.append('[');
		sb.append(ByteBufferUtils.parseString(bb));
		sb.append(']');
		return sb.toString();
	}
	
}