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
		this.byteBuffer = ByteBuffer.allocate(size);
		this.isOptional = isOptional;
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
	
	@Override
	public final void markAsPresent() {
		if (isOptional) this.isPresent = true;
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
		if (isOptional) this.isPresent = true;
		byteBuffer.clear();
		int len = byteBuffer.capacity();
		for(int i = 0; i < len; i++) {
			byteBuffer.put(src.get());
		}
		byteBuffer.flip();
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