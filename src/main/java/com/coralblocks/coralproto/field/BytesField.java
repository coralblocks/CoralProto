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

import com.coralblocks.coralproto.AbstractProto;
import com.coralblocks.coralproto.util.ByteBufferUtils;
import com.coralblocks.coralproto.util.CharUtils;

public class BytesField implements ProtoField {
	
	private final boolean isOptional;
	private boolean isPresent;
	private final ByteBuffer byteBuffer;
	
	public BytesField(int size) {
		this(null, size);
	}
	
	public BytesField(AbstractProto proto, int size) {
		this(proto, size, false);
	}
	
	public BytesField(int size, boolean isOptional) {
		this(null, size, isOptional);
	}
	
	public BytesField(AbstractProto proto, int size, boolean isOptional) {
		if (proto != null) proto.add(this);
		this.byteBuffer = ByteBuffer.allocateDirect(size);
		this.isOptional = isOptional;
		reset();
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof BytesField) {
			BytesField bf = (BytesField) o;
			bf.byteBuffer.limit(bf.byteBuffer.capacity()).limit(0);
			this.byteBuffer.limit(this.byteBuffer.capacity()).limit(0);
			return this.byteBuffer.equals(bf.byteBuffer);
		}
		return false;
	}
	
	@Override
	public void reset() {
		this.byteBuffer.clear();
		for(int i = 0; i < byteBuffer.capacity(); i++) this.byteBuffer.put((byte) 0);
		this.byteBuffer.flip();
	}
	
	@Override
	public ProtoField newInstance() {
		return new BytesField(null, byteBuffer.capacity(), isOptional);
	}

	@Override
	public final int size() {
		if (isOptional) {
			return isPresent ? 1 + byteBuffer.capacity() : 1;
		} else {
			return byteBuffer.capacity();
		}
	}

	@Override
	public final boolean isPresent() {
		if (!isOptional) return true;
		return isPresent;
	}

	@Override
	public final boolean isOptional() {
		return isOptional;
	}
	
	@Override
	public final void markAsNotPresent() {
		if (!isOptional) throw new IllegalStateException("Cannot mark a required field as not present!");
		this.isPresent = false;
	}
	
	private final ByteBuffer getAndMarkAsPresent() {
		if (isOptional) isPresent = true;
		return get();
	}
	
	public final ByteBuffer get() {
		if (isOptional && !isPresent) throw new IllegalStateException("Cannot get an optional field that is not present!");
		byteBuffer.limit(byteBuffer.capacity()).position(0);
		return byteBuffer;
	}
	
	public final void set(byte[] array) {
		if (array.length != byteBuffer.capacity()) {
			throw new IllegalArgumentException("Invalid array length: " + array.length);
		}
		ByteBuffer bb = getAndMarkAsPresent();
		bb.put(array);
		bb.flip();
	}
	
	public final void set(ByteBuffer buf) {
		if (buf.remaining() != byteBuffer.capacity()) {
			throw new IllegalArgumentException("Invalid ByteBuffer size: " + buf.remaining());
		}
		ByteBuffer bb = getAndMarkAsPresent();
		bb.put(buf);
		bb.flip();
	}
	
	@Override
	public final void readFrom(ByteBuffer src) {
		if (src.remaining() < byteBuffer.capacity()) {
			throw new IllegalArgumentException("ByteBuffer is too small: " + src.remaining());
		}
		if (isOptional) this.isPresent = true;
		byteBuffer.clear();
		int savedLim = src.limit();
		src.limit(src.position() + byteBuffer.capacity());
		byteBuffer.put(src);
		byteBuffer.flip();
		src.limit(savedLim);
	}
	
	@Override
	public final void writeTo(ByteBuffer buf) {
		if (isOptional && !isPresent) throw new IllegalStateException("Cannot write a value that is not present!");
		if (buf.remaining() < byteBuffer.capacity()) throw new IllegalArgumentException("Give ByteBuffer does not have space available: " + buf.remaining());
		byteBuffer.limit(byteBuffer.capacity()).position(0);
		buf.put(byteBuffer);
	}
	
	@Override
	public final void writeAsciiTo(ByteBuffer buf) {
		if (isOptional && !isPresent) throw new IllegalStateException("Cannot write a value that is not present!");
		if (buf.remaining() < byteBuffer.capacity()) throw new IllegalArgumentException("Give ByteBuffer does not have space available: " + buf.remaining());
		byteBuffer.limit(byteBuffer.capacity()).position(0);
		for(int i = 0; i < byteBuffer.capacity(); i++) {
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
		if (isOptional) {
			if (isPresent) {
				ByteBuffer byteBuffer = get();
				StringBuilder sb = new StringBuilder(byteBuffer.capacity() + 2);
				sb.append('[');
				sb.append(ByteBufferUtils.parseString(byteBuffer));
				sb.append(']');
				return sb.toString();
			} else {
				return "BLANK";
			}
		} else {
			ByteBuffer byteBuffer = get();
			StringBuilder sb = new StringBuilder(byteBuffer.capacity() + 2);
			sb.append('[');
			sb.append(ByteBufferUtils.parseString(byteBuffer));
			sb.append(']');
			return sb.toString();
		}
	}
	
}