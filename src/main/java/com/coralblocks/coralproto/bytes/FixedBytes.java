package com.coralblocks.coralproto.bytes;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class FixedBytes {
	
	private final ByteBuffer bb;

	public FixedBytes(int size) {
		this.bb = ByteBuffer.allocate(size);
	}
	
	public ByteBuffer getByteBuffer() {
		return bb;
	}
	
	public int getTotalLength() {
		return bb.capacity();
	}
	
	public final int capacity() {
		return bb.capacity();
	}

	public final int position() {
		return bb.position();
	}

	public final Buffer position(int newPosition) {
		return bb.position(newPosition);
	}

	public final int limit() {
		return bb.limit();
	}

	public final Buffer limit(int newLimit) {
		return bb.limit(newLimit);
	}

	public final Buffer mark() {
		return bb.mark();
	}

	public final Buffer reset() {
		return bb.reset();
	}

	public final Buffer clear() {
		return bb.clear();
	}

	public final Buffer flip() {
		return bb.flip();
	}

	public final Buffer rewind() {
		return bb.rewind();
	}

	public final int remaining() {
		return bb.remaining();
	}

	public final boolean hasRemaining() {
		return bb.hasRemaining();
	}

	public boolean isReadOnly() {
		return bb.isReadOnly();
	}

	public byte get() {
		return bb.get();
	}

	public ByteBuffer put(byte b) {
		return bb.put(b);
	}

	public byte get(int index) {
		return bb.get(index);
	}

	public ByteBuffer put(int index, byte b) {
		return bb.put(index, b);
	}

	public ByteBuffer get(byte[] dst, int offset, int length) {
		return bb.get(dst, offset, length);
	}

	public ByteBuffer get(byte[] dst) {
		return bb.get(dst);
	}

	public ByteBuffer put(ByteBuffer src) {
		return bb.put(src);
	}

	public ByteBuffer put(byte[] src, int offset, int length) {
		return bb.put(src, offset, length);
	}

	public final ByteBuffer put(byte[] src) {
		return bb.put(src);
	}

	public final boolean hasArray() {
		return bb.hasArray();
	}

	public final byte[] array() {
		return bb.array();
	}

	public final int arrayOffset() {
		return bb.arrayOffset();
	}

	public ByteBuffer compact() {
		return bb.compact();
	}

	public boolean isDirect() {
		return bb.isDirect();
	}

	@Override
	public String toString() {
		return bb.toString();
	}

	@Override
	public int hashCode() {
		return bb.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof FixedBytes) {
			FixedBytes fb = (FixedBytes) obj;
			return fb.bb.equals(this.bb);
		}
		return false;
	}

	public int compareTo(ByteBuffer that) {
		return bb.compareTo(that);
	}

	public final ByteOrder order() {
		return bb.order();
	}

	public final ByteBuffer order(ByteOrder bo) {
		return bb.order(bo);
	}

	public short getShort() {
		return bb.getShort();
	}

	public ByteBuffer putShort(short value) {
		return bb.putShort(value);
	}

	public short getShort(int index) {
		return bb.getShort(index);
	}

	public ByteBuffer putShort(int index, short value) {
		return bb.putShort(index, value);
	}

	public int getInt() {
		return bb.getInt();
	}

	public ByteBuffer putInt(int value) {
		return bb.putInt(value);
	}

	public int getInt(int index) {
		return bb.getInt(index);
	}

	public ByteBuffer putInt(int index, int value) {
		return bb.putInt(index, value);
	}

	public long getLong() {
		return bb.getLong();
	}

	public ByteBuffer putLong(long value) {
		return bb.putLong(value);
	}

	public long getLong(int index) {
		return bb.getLong(index);
	}

	public ByteBuffer putLong(int index, long value) {
		return bb.putLong(index, value);
	}

	public float getFloat() {
		return bb.getFloat();
	}

	public ByteBuffer putFloat(float value) {
		return bb.putFloat(value);
	}

	public float getFloat(int index) {
		return bb.getFloat(index);
	}

	public ByteBuffer putFloat(int index, float value) {
		return bb.putFloat(index, value);
	}

	public double getDouble() {
		return bb.getDouble();
	}

	public ByteBuffer putDouble(double value) {
		return bb.putDouble(value);
	}

	public double getDouble(int index) {
		return bb.getDouble(index);
	}

	public ByteBuffer putDouble(int index, double value) {
		return bb.putDouble(index, value);
	}
}