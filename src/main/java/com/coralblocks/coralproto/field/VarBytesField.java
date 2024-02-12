package com.coralblocks.coralproto.field;

import java.nio.ByteBuffer;

import com.coralblocks.coralproto.AbstractProto;
import com.coralblocks.coralproto.util.ByteBufferUtils;
import com.coralblocks.coralproto.util.CharUtils;

public class VarBytesField implements ProtoField {
	
	private final boolean isOptional;
	private boolean isPresent;
	private final ByteBuffer byteBuffer;
	private boolean wasRead = false;
	private int savedLim = -1;
	
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
			return isPresent ? 1 + 4 + byteBuffer.remaining() : 1;
		} else {
			return 4 + byteBuffer.remaining();
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
		if (wasRead) byteBuffer.limit(savedLim).position(0);
		return byteBuffer;
	}
	
	public final ByteBuffer getAndMarkAsPresent() {
		if (isOptional) isPresent = true;
		return get();
	}
	
	public final void set(byte[] array, int offset, int len) {
		ByteBuffer bb = getAndMarkAsPresent();
		bb.clear();
		bb.put(array, offset, len);
		bb.flip();
	}
	
	public final void set(byte[] array) {
		ByteBuffer bb = getAndMarkAsPresent();
		bb.clear();
		bb.put(array);
		bb.flip();
	}
	
	public final void set(ByteBuffer buf) {
		ByteBuffer bb = getAndMarkAsPresent();
		bb.clear();
		bb.put(buf);
		bb.flip();
	}
	
	@Override
	public final void markAsPresent() {
		if (isOptional) this.isPresent = true;
	}
	
	@Override
	public final void readFrom(ByteBuffer src) {
		if (isOptional) this.isPresent = true;
		byteBuffer.clear();
		int len = src.getInt();
		for(int i = 0; i < len; i++) {
			byteBuffer.put(src.get());
		}
		byteBuffer.flip();
		wasRead = true;
		savedLim = byteBuffer.limit();
	}
	
	@Override
	public final void writeTo(ByteBuffer buf) {
		if (isOptional && !isPresent) throw new IllegalStateException("Cannot write a value that is not present!");
		int pos = byteBuffer.position();
		int rem = byteBuffer.remaining();
		buf.putInt(rem);
		buf.put(byteBuffer);
		byteBuffer.position(pos);
	}
	
	@Override
	public final void writeAsciiTo(ByteBuffer buf) {
		if (isOptional && !isPresent) throw new IllegalStateException("Cannot write a value that is not present!");
		if (wasRead) byteBuffer.limit(savedLim).position(0);
		int pos = byteBuffer.position();
		int len = byteBuffer.remaining();
		for(int i = 0; i < len; i++) {
			byte b = byteBuffer.get();
			if (CharUtils.isPrintable((char) b)) {
				buf.put((byte) b);
			} else {
				buf.put((byte) '?');
			}
		}
		byteBuffer.position(pos);
	}
	
	@Override
	public String toString() {
		if (isOptional) {
			if (isPresent) {
				ByteBuffer byteBuffer = get();
				int pos = byteBuffer.position();
				StringBuilder sb = new StringBuilder(byteBuffer.remaining());
				sb.append(ByteBufferUtils.parseString(byteBuffer));
				byteBuffer.position(pos);
				return sb.toString();
			} else {
				return "BLANK";
			}
		} else {
			ByteBuffer byteBuffer = get();
			int pos = byteBuffer.position();
			StringBuilder sb = new StringBuilder(byteBuffer.remaining());
			sb.append(ByteBufferUtils.parseString(byteBuffer));
			byteBuffer.position(pos);
			return sb.toString();
		}
	}
}