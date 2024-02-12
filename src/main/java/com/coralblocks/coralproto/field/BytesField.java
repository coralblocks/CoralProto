package com.coralblocks.coralproto.field;

import java.nio.ByteBuffer;

import com.coralblocks.coralproto.AbstractProto;
import com.coralblocks.coralproto.util.ByteBufferUtils;
import com.coralblocks.coralproto.util.CharUtils;

public class BytesField implements ProtoField {
	
	private final boolean isOptional;
	private boolean isPresent;
	private final ByteBuffer byteBuffer;
	private boolean wasRead = false;
	private int savedLim = -1;
	
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
	
	public final ByteBuffer getAndMarkAsPresent() {
		if (isOptional) isPresent = true;
		return get();
	}
	
	public final ByteBuffer get() {
		if (isOptional && !isPresent) throw new IllegalStateException("Cannot get an optional field that is not present!");
		if (wasRead) byteBuffer.limit(savedLim).position(0);
		return byteBuffer;
	}
	
	public final void set(byte[] array, int offset, int len) {
		if (len > byteBuffer.capacity()) {
			throw new IllegalArgumentException("Array is larger than field length: " + new String(array, offset, len) + " (" + byteBuffer.capacity() + ")");
		}
		ByteBuffer bb = getAndMarkAsPresent();
		bb.clear();
		bb.put(array, offset, len);
		bb.flip();
	}
	
	public final void set(byte[] array) {
		if (array.length > byteBuffer.capacity()) {
			throw new IllegalArgumentException("Array is larger than field length: " + new String(array) + " (" + byteBuffer.capacity() + ")");
		}
		ByteBuffer bb = getAndMarkAsPresent();
		bb.clear();
		bb.put(array);
		bb.flip();
	}
	
	public final void set(ByteBuffer buf) {
		if (buf.remaining() > byteBuffer.capacity()) {
			throw new IllegalArgumentException("ByteBuffer is larger than field length: " + ByteBufferUtils.parseString(buf) + " (" + byteBuffer.capacity() + ")");
		}
		ByteBuffer bb = getAndMarkAsPresent();
		bb.clear();
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
		wasRead = true;
		savedLim = byteBuffer.limit();
		
	}
	
	@Override
	public final void writeTo(ByteBuffer buf) {
		if (isOptional && !isPresent) throw new IllegalStateException("Cannot write a value that is not present!");
		int pos = byteBuffer.position();
		int rem = byteBuffer.remaining();
		int paddle = byteBuffer.capacity() - rem;
		buf.put(byteBuffer);
		for(int i = 0; i < paddle; i++) buf.put((byte) ' ');
		byteBuffer.position(pos);
	}
	
	@Override
	public final void writeAsciiTo(ByteBuffer buf) {
		if (isOptional && !isPresent) throw new IllegalStateException("Cannot write a value that is not present!");
		if (wasRead) byteBuffer.limit(savedLim).position(0);
		int pos = byteBuffer.position();
		int len = byteBuffer.remaining();
		int paddle = byteBuffer.capacity() - len;
		for(int i = 0; i < len; i++) {
			byte b = byteBuffer.get();
			if (CharUtils.isPrintable((char) b)) {
				buf.put((byte) b);
			} else {
				buf.put((byte) '?');
			}
		}
		for(int i = 0; i < paddle; i++) buf.put((byte) ' ');
		byteBuffer.position(pos);
	}
	
	@Override
	public String toString() {
		if (isOptional) {
			if (isPresent) {
				ByteBuffer byteBuffer = get();
				int pos = byteBuffer.position();
				int rem = byteBuffer.remaining();
				int paddle = byteBuffer.capacity() - rem;
				StringBuilder sb = new StringBuilder(byteBuffer.capacity() + 2);
				sb.append('[');
				sb.append(ByteBufferUtils.parseString(byteBuffer));
				byteBuffer.position(pos);
				for(int i = 0; i < paddle; i++) sb.append(' ');
				sb.append(']');
				return sb.toString();
			} else {
				return "BLANK";
			}
		} else {
			ByteBuffer byteBuffer = get();
			int pos = byteBuffer.position();
			int rem = byteBuffer.remaining();
			int paddle = byteBuffer.capacity() - rem;
			StringBuilder sb = new StringBuilder(byteBuffer.capacity() + 2);
			sb.append('[');
			sb.append(ByteBufferUtils.parseString(byteBuffer));
			byteBuffer.position(pos);
			for(int i = 0; i < paddle; i++) sb.append(' ');
			sb.append(']');
			return sb.toString();
		}
	}
	
	/*
	
	// The code below is commented out because we want to force the client to 
	// get the ByteBuffer using the get() or getAndMarkAsPresent() methods.
	
	public final int capacity() {
		return byteBuffer.capacity();
	}

	public final int position() {
		return byteBuffer.position();
	}

	public final Buffer position(int newPosition) {
		return byteBuffer.position(newPosition);
	}

	public final int limit() {
		return byteBuffer.limit();
	}

	public final Buffer limit(int newLimit) {
		return byteBuffer.limit(newLimit);
	}

	public final Buffer mark() {
		return byteBuffer.mark();
	}

	public final Buffer reset() {
		return byteBuffer.reset();
	}

	public final Buffer clear() {
		return byteBuffer.clear();
	}

	public final Buffer flip() {
		return byteBuffer.flip();
	}

	public final Buffer rewind() {
		return byteBuffer.rewind();
	}

	public final int remaining() {
		return byteBuffer.remaining();
	}

	public final boolean hasRemaining() {
		return byteBuffer.hasRemaining();
	}

	public boolean isReadOnly() {
		return byteBuffer.isReadOnly();
	}

	public byte getByte() {
		return byteBuffer.get();
	}

	public ByteBuffer put(byte b) {
		return byteBuffer.put(b);
	}

	public byte get(int index) {
		return byteBuffer.get(index);
	}

	public ByteBuffer put(int index, byte b) {
		return byteBuffer.put(index, b);
	}

	public ByteBuffer get(byte[] dst, int offset, int length) {
		return byteBuffer.get(dst, offset, length);
	}

	public ByteBuffer get(byte[] dst) {
		return byteBuffer.get(dst);
	}

	public ByteBuffer put(ByteBuffer src) {
		return byteBuffer.put(src);
	}

	public ByteBuffer put(byte[] src, int offset, int length) {
		return byteBuffer.put(src, offset, length);
	}

	public final ByteBuffer put(byte[] src) {
		return byteBuffer.put(src);
	}

	public final boolean hasArray() {
		return byteBuffer.hasArray();
	}

	public final byte[] array() {
		return byteBuffer.array();
	}

	public final int arrayOffset() {
		return byteBuffer.arrayOffset();
	}

	public ByteBuffer compact() {
		return byteBuffer.compact();
	}

	public boolean isDirect() {
		return byteBuffer.isDirect();
	}
	
	public final ByteOrder order() {
		return byteBuffer.order();
	}

	public final ByteBuffer order(ByteOrder bo) {
		return byteBuffer.order(bo);
	}

	public short getShort() {
		return byteBuffer.getShort();
	}

	public ByteBuffer putShort(short value) {
		return byteBuffer.putShort(value);
	}

	public short getShort(int index) {
		return byteBuffer.getShort(index);
	}

	public ByteBuffer putShort(int index, short value) {
		return byteBuffer.putShort(index, value);
	}

	public int getInt() {
		return byteBuffer.getInt();
	}

	public ByteBuffer putInt(int value) {
		return byteBuffer.putInt(value);
	}

	public int getInt(int index) {
		return byteBuffer.getInt(index);
	}

	public ByteBuffer putInt(int index, int value) {
		return byteBuffer.putInt(index, value);
	}

	public long getLong() {
		return byteBuffer.getLong();
	}

	public ByteBuffer putLong(long value) {
		return byteBuffer.putLong(value);
	}

	public long getLong(int index) {
		return byteBuffer.getLong(index);
	}

	public ByteBuffer putLong(int index, long value) {
		return byteBuffer.putLong(index, value);
	}

	public float getFloat() {
		return byteBuffer.getFloat();
	}

	public ByteBuffer putFloat(float value) {
		return byteBuffer.putFloat(value);
	}

	public float getFloat(int index) {
		return byteBuffer.getFloat(index);
	}

	public ByteBuffer putFloat(int index, float value) {
		return byteBuffer.putFloat(index, value);
	}

	public double getDouble() {
		return byteBuffer.getDouble();
	}

	public ByteBuffer putDouble(double value) {
		return byteBuffer.putDouble(value);
	}

	public double getDouble(int index) {
		return byteBuffer.getDouble(index);
	}

	public ByteBuffer putDouble(int index, double value) {
		return byteBuffer.putDouble(index, value);
	}
	
	*/
}