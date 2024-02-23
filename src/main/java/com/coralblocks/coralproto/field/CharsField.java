package com.coralblocks.coralproto.field;

import java.nio.ByteBuffer;

import com.coralblocks.coralproto.AbstractProto;
import com.coralblocks.coralproto.util.ByteBufferCharSequence;

public class CharsField implements ProtoField {
	
	private final boolean isOptional;
	private boolean isPresent;
	private final ByteBufferCharSequence bbcs;
	private final int size;
	
	public CharsField(int size) {
		this(null, size);
	}
	
	public CharsField(AbstractProto proto, int size) {
		this(proto, size, false);
	}
	
	public CharsField(int size, boolean isOptional) {
		this(null, size, isOptional);
	}
	
	public CharsField(AbstractProto proto, int size, boolean isOptional) {
		if (proto != null) proto.add(this);
		this.bbcs = new ByteBufferCharSequence(size);
		this.isOptional = isOptional;
		this.size = size;
		reset();
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof CharsField) {
			CharsField cf = (CharsField) o;
			return cf.bbcs.equals(this.bbcs);
		}
		return false;
	}
	
	@Override
	public void reset() {
		ByteBuffer byteBuffer = this.bbcs.getByteBuffer();
		for(int i = 0; i < byteBuffer.capacity(); i++) byteBuffer.put((byte) ' ');
	}
	
	@Override
	public ProtoField newInstance() {
		return new CharsField(null, size, isOptional);
	}

	@Override
	public final int size() {
		if (isOptional) {
			return isPresent ? 1 + size : 1;
		} else {
			return size;
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
	
	private final ByteBufferCharSequence getAndMarkAsPresent() {
		if (isOptional) isPresent = true;
		return (ByteBufferCharSequence) get();
	}
	
	public final CharsField clear() {
		ByteBufferCharSequence bacs = getAndMarkAsPresent();
		ByteBuffer byteBuffer = bacs.getByteBuffer();
		for(int i = 0; i < size; i++) byteBuffer.put((byte) ' ');
		return this;
	}
	
	public final void set(CharSequence cs) {
		int len = cs.length();
		if (len > size) {
			throw new IllegalArgumentException("CharSequence is larger than field length: " + cs.toString() + " (" + size + ")");
		}
		ByteBufferCharSequence bacs = getAndMarkAsPresent();
		ByteBuffer byteBuffer = bacs.getByteBuffer();
		for(int i = 0; i < len; i++) byteBuffer.put((byte) cs.charAt(i));
		for(int i = len; i < size; i++) byteBuffer.put((byte) ' ');
	}
	
	public final void set(byte[] array) {
		int len = array.length;
		if (len > size) {
			throw new IllegalArgumentException("Array is larger than field length: " + len);
		}
		ByteBufferCharSequence bacs = getAndMarkAsPresent();
		ByteBuffer byteBuffer = bacs.getByteBuffer();
		byteBuffer.put(array);
		for(int i = len; i < size; i++) byteBuffer.put((byte) ' ');
	}
	
	public final void set(char[] array) {
		int len = array.length;
		if (len > size) {
			throw new IllegalArgumentException("Array is larger than field length: " + len);
		}
		ByteBufferCharSequence bacs = getAndMarkAsPresent();
		ByteBuffer byteBuffer = bacs.getByteBuffer();
		for(int i = 0; i < len; i++) byteBuffer.put((byte) array[i]);
		for(int i = len; i < size; i++)  byteBuffer.put((byte) ' ');
	}
	
	public final CharSequence get() {
		if (isOptional && !isPresent) throw new IllegalStateException("Cannot get an optional field that is not present!");
		return bbcs;
	}
	
	public final ByteBuffer getByteBuffer() {
		return ((ByteBufferCharSequence) get()).getByteBuffer();
	}
	
	@Override
	public final void readFrom(ByteBuffer src) {
		if (isOptional) this.isPresent = true;
		ByteBuffer byteBuffer = bbcs.getByteBuffer();
		int savedLim = src.limit();
		src.limit(src.position() + byteBuffer.capacity());
		byteBuffer.put(src);
		src.limit(savedLim);
	}
	
	@Override
	public final void writeTo(ByteBuffer buf) {
		if (isOptional && !isPresent) throw new IllegalStateException("Cannot write a value that is not present!");
		ByteBuffer byteBuffer = bbcs.getByteBuffer();
		buf.put(byteBuffer);
	}
	
	@Override
	public final void writeAsciiTo(ByteBuffer buf) {
		if (isOptional && !isPresent) throw new IllegalStateException("Cannot write a value that is not present!");
		ByteBuffer byteBuffer = bbcs.getByteBuffer();
		buf.put(byteBuffer);
	}
	
	@Override
	public String toString() {
		if (isOptional) {
			if (isPresent) {
				StringBuilder sb = new StringBuilder(size + 2);
				sb.append('[');
				sb.append(bbcs);
				sb.append(']');
				return sb.toString();
			} else {
				return "BLANK";
			}
		} else {
			StringBuilder sb = new StringBuilder(size + 2);
			sb.append('[');
			sb.append(bbcs);
			sb.append(']');
			return sb.toString();
		}
	}
}