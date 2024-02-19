package com.coralblocks.coralproto.field;

import java.nio.ByteBuffer;

import com.coralblocks.coralproto.AbstractProto;
import com.coralblocks.coralproto.util.ByteBufferUtils;
import com.coralblocks.coralproto.util.CharUtils;

public class VarBytesField implements ProtoField {
	
	private final boolean isOptional;
	private boolean isPresent;
	private final ByteBuffer byteBuffer;
	private int size;
	
	public VarBytesField(int maxLength) {	
		this(null, maxLength);
	}
	
	public VarBytesField(AbstractProto proto, int maxLength) {
		this(proto, maxLength, false);
	}
	
	public VarBytesField(int maxLength, boolean isOptional) {
		this(null, maxLength, isOptional);
	}
	
	public VarBytesField(AbstractProto proto, int maxLength, boolean isOptional) {
		if (proto != null) proto.add(this);
		this.byteBuffer = ByteBuffer.allocate(maxLength);
		this.isOptional = isOptional;
		this.size = 0;
	}
	
	public final int getMaxLength() {
		return byteBuffer.capacity();
	}
	
	@Override
	public ProtoField newInstance() {
		return new VarBytesField(null, byteBuffer.capacity(), isOptional);
	}

	@Override
	public final int size() {
		if (isOptional) {
			return isPresent ? 1 + 4 + size : 1;
		} else {
			return 4 + size;
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
	
	public final ByteBuffer get() {
		if (isOptional && !isPresent) throw new IllegalStateException("Cannot get an optional field that is not present!");
		byteBuffer.limit(size).position(0);
		return byteBuffer;
	}
	
	private final ByteBuffer getAndMarkAsPresent() {
		if (isOptional) isPresent = true;
		return get();
	}
	
	public final void set(byte[] array, int offset, int len) {
		ByteBuffer bb = getAndMarkAsPresent();
		bb.clear();
		bb.put(array, offset, len);
		bb.flip();
		this.size = len;
	}
	
	public final void set(byte[] array) {
		ByteBuffer bb = getAndMarkAsPresent();
		bb.clear();
		bb.put(array);
		bb.flip();
		this.size = array.length;
	}
	
	public final void set(ByteBuffer buf) {
		ByteBuffer bb = getAndMarkAsPresent();
		bb.clear();
		bb.put(buf);
		bb.flip();
		this.size = bb.remaining();
	}
	
	@Override
	public final void readFrom(ByteBuffer src) {
		if (isOptional) this.isPresent = true;
		byteBuffer.clear();
		int len = src.getInt();
		int savedLim = src.limit();
		src.limit(src.position() + len);
		byteBuffer.put(src);
		byteBuffer.flip();
		src.limit(savedLim);
		this.size = len;
	}
	
	@Override
	public final void writeTo(ByteBuffer buf) {
		if (isOptional && !isPresent) throw new IllegalStateException("Cannot write a value that is not present!");
		buf.putInt(size);
		buf.put(get());
	}
	
	@Override
	public final void writeAsciiTo(ByteBuffer buf) {
		if (isOptional && !isPresent) throw new IllegalStateException("Cannot write a value that is not present!");
		ByteBuffer byteBuffer = get();
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
		if (isOptional) {
			if (isPresent) {
				ByteBuffer byteBuffer = get();
				StringBuilder sb = new StringBuilder(byteBuffer.remaining());
				sb.append(ByteBufferUtils.parseString(byteBuffer));
				return sb.toString();
			} else {
				return "BLANK";
			}
		} else {
			ByteBuffer byteBuffer = get();
			StringBuilder sb = new StringBuilder(byteBuffer.remaining());
			sb.append(ByteBufferUtils.parseString(byteBuffer));
			return sb.toString();
		}
	}
}