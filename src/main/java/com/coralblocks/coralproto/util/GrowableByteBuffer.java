package com.coralblocks.coralproto.util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class GrowableByteBuffer implements Comparable<GrowableByteBuffer> {
	
	private final static int DEFAULT_INITIAL_CAPACITY = 16;
	private final static float DEFAULT_EXPAND_FACTOR = .2f;
	private final static boolean DEFAULT_DIRECT = false;
	
	public static GrowableByteBuffer allocate(int initialCapacity) {
		return new GrowableByteBuffer(initialCapacity);
	}
	
	public static GrowableByteBuffer allocate() {
		return new GrowableByteBuffer();
	}
	
	public static GrowableByteBuffer allocate(int initialCapacity, float expandFactor) {
		return new GrowableByteBuffer(initialCapacity, expandFactor);
	}
	
	public static GrowableByteBuffer allocateDirect(int initialCapacity) {
		return new GrowableByteBuffer(initialCapacity, true);
	}
	
	public static GrowableByteBuffer allocateDirect() {
		return new GrowableByteBuffer(true);
	}
	
	public static GrowableByteBuffer allocateDirect(int initialCapacity, float expandFactor) {
		return new GrowableByteBuffer(initialCapacity, expandFactor, true);
	}

	private ByteBuffer byteBuffer;
	private final float expandFactor;
	private final boolean direct;
	
	private GrowableByteBuffer(ByteBuffer bb, float expandFactor, boolean direct) {
		this.byteBuffer = bb;
		this.expandFactor = expandFactor;
		this.direct = direct;
	}
	
	private GrowableByteBuffer() {
		this(DEFAULT_INITIAL_CAPACITY, DEFAULT_EXPAND_FACTOR, DEFAULT_DIRECT);
	}
	
	private GrowableByteBuffer(boolean direct) {
		this(DEFAULT_INITIAL_CAPACITY, DEFAULT_EXPAND_FACTOR, direct);
	}

	private GrowableByteBuffer(int initialCapacity, float expandFactor, boolean direct) {
		if (expandFactor > 1 || expandFactor <= 0) {
			throw new IllegalArgumentException("The expand factor must be a porcentage between 0 (exclusive) and 1 (inclusive)!");
		}
		this.byteBuffer = direct ? ByteBuffer.allocateDirect(initialCapacity) : ByteBuffer.allocate(initialCapacity);
		this.expandFactor = expandFactor;
		this.direct = direct;
	}
	
	private GrowableByteBuffer(int initialCapacity, float expandFactor) {
		this(initialCapacity, expandFactor, DEFAULT_DIRECT);
	}

	private GrowableByteBuffer(int initialCapacity) {
		this(initialCapacity, DEFAULT_EXPAND_FACTOR, DEFAULT_DIRECT);
	}
	
	private GrowableByteBuffer(int initialCapacity, boolean direct) {
		this(initialCapacity, DEFAULT_EXPAND_FACTOR, direct);
	}

	public int capacity() {
		return byteBuffer.capacity();
	}

	public void clear() {
		byteBuffer.clear();
	}
	
	public GrowableByteBuffer flip() {
		byteBuffer.flip();
		return this;
	}

	public boolean hasRemaining() {
		return byteBuffer.hasRemaining();
	}

	public boolean isReadOnly() {
		return byteBuffer.isReadOnly();
	}

	public int limit() {
		return byteBuffer.limit();
	}

	public GrowableByteBuffer limit(int newLimit) {
		byteBuffer.limit(newLimit);
		return this;
	}

	public GrowableByteBuffer mark() {
		byteBuffer.mark();
		return this;
	}

	public int position() {
		return byteBuffer.position();
	}

	public GrowableByteBuffer position(int newPosition) {
		byteBuffer.position(newPosition);
		return this;
	}

	public int remaining() {
		return byteBuffer.remaining();
	}

	public GrowableByteBuffer reset() {
		byteBuffer.reset();
		return this;
	}

	public GrowableByteBuffer rewind() {
		byteBuffer.rewind();
		return this;
	}

	public byte[] array() {
		return byteBuffer.array();
	}

	public int arrayOffset() {
		return byteBuffer.arrayOffset();
	}

	public GrowableByteBuffer compact() {
		byteBuffer.compact();
		return this;
	}

	@Override
	public int compareTo(GrowableByteBuffer that) {
		return byteBuffer.compareTo(that.byteBuffer);
	}

	public GrowableByteBuffer duplicate() {
		ByteBuffer bb = byteBuffer.duplicate();
		return new GrowableByteBuffer(bb, expandFactor, direct);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof GrowableByteBuffer) {
			GrowableByteBuffer gbb = (GrowableByteBuffer) obj;
			return gbb.byteBuffer.equals(this.byteBuffer);
		}
		return false;
	}

	public byte get() {
		return byteBuffer.get();
	}

	public GrowableByteBuffer get(byte[] dst) {
		byteBuffer.get(dst);
		return this;
	}

	public GrowableByteBuffer get(byte[] dst, int offset, int length) {
		byteBuffer.get(dst, offset, length);
		return this;
	}

	public byte get(int index) {
		return byteBuffer.get(index);
	}

	public char getChar() {
		return byteBuffer.getChar();
	}

	public char getChar(int index) {
		return byteBuffer.getChar(index);
	}

	public double getDouble() {
		return byteBuffer.getDouble();
	}

	public double getDouble(int index) {
		return byteBuffer.getDouble(index);
	}

	public float getFloat() {
		return byteBuffer.getFloat();
	}

	public float getFloat(int index) {
		return byteBuffer.getFloat(index);
	}

	public int getInt() {
		return byteBuffer.getInt();
	}

	public int getInt(int index) {
		return byteBuffer.getInt(index);
	}

	public long getLong() {
		return byteBuffer.getLong();
	}

	public long getLong(int index) {
		return byteBuffer.getLong(index);
	}

	public short getShort() {
		return byteBuffer.getShort();
	}

	public short getShort(int index) {
		return byteBuffer.getShort(index);
	}

	public boolean hasArray() {
		return byteBuffer.hasArray();
	}

	public boolean isDirect() {
		return direct;
	}

	public ByteOrder order() {
		return byteBuffer.order();
	}

	public GrowableByteBuffer order(ByteOrder bo) {
		byteBuffer.order(bo);
		return this;
	}

	public GrowableByteBuffer put(byte b) {
		ensureSpace(1);
		byteBuffer.put(b);
		return this;
	}

	public GrowableByteBuffer put(byte[] src) {
		ensureSpace(src.length);
		byteBuffer.put(src);
		return this;
	}

	public GrowableByteBuffer put(byte[] src, int offset, int length) {
		ensureSpace(length);
		byteBuffer.put(src, offset, length);
		return this;
	}

	public GrowableByteBuffer put(ByteBuffer src) {
		ensureSpace(src.remaining());
		byteBuffer.put(src);
		return this;
	}

	public GrowableByteBuffer put(int index, byte b) {
		ensureSpace(1);
		byteBuffer.put(index, b);
		return this;
	}

	public GrowableByteBuffer putChar(char value) {
		ensureSpace(2);
		byteBuffer.putChar(value);
		return this;
	}

	public GrowableByteBuffer putChar(int index, char value) {
		ensureSpace(2);
		byteBuffer.putChar(index, value);
		return this;
	}

	public GrowableByteBuffer putDouble(double value) {
		ensureSpace(8);
		byteBuffer.putDouble(value);
		return this;
	}

	public GrowableByteBuffer putDouble(int index, double value) {
		ensureSpace(8);
		byteBuffer.putDouble(index, value);
		return this;
	}

	public GrowableByteBuffer putFloat(float value) {
		ensureSpace(4);
		byteBuffer.putFloat(value);
		return this;
	}

	public GrowableByteBuffer putFloat(int index, float value) {
		ensureSpace(4);
		byteBuffer.putFloat(index, value);
		return this;
	}

	public GrowableByteBuffer putInt(int value) {
		ensureSpace(4);
		byteBuffer.putInt(value);
		return this;
	}

	public GrowableByteBuffer putInt(int index, int value) {
		ensureSpace(4);
		byteBuffer.putInt(index, value);
		return this;
	}

	public GrowableByteBuffer putLong(int index, long value) {
		ensureSpace(8);
		byteBuffer.putLong(index, value);
		return this;
	}

	public GrowableByteBuffer putLong(long value) {
		ensureSpace(8);
		byteBuffer.putLong(value);
		return this;
	}

	public GrowableByteBuffer putShort(int index, short value) {
		ensureSpace(2);
		byteBuffer.putShort(index, value);
		return this;
	}

	public GrowableByteBuffer putShort(short value) {
		ensureSpace(2);
		byteBuffer.putShort(value);
		return this;
	}

	public GrowableByteBuffer slice() {
		ByteBuffer bb = byteBuffer.slice();
		return new GrowableByteBuffer(bb, expandFactor, direct);
	}

	@Override
	public int hashCode() {
		return byteBuffer.hashCode();
	}

	@Override
	public String toString() {
		return "GrowableByteBuffer [byteBuffer=" + byteBuffer + ", expandFactor=" + expandFactor + ", direct=" + direct + "]";
	}

	public float getExpandFactor() {
		return expandFactor;
	}

	private final void ensureSpace(int needed) {
		
		if (remaining() >= needed) {
			return;
		}
		
		needed = needed - remaining();
		
		int oldCapacity = byteBuffer.capacity();
		float factor = 1 + expandFactor;
		int newCapacity = (int) (oldCapacity * factor);
		if (newCapacity == oldCapacity) newCapacity++;
		while(newCapacity < oldCapacity + needed) {
			int save = newCapacity;
			newCapacity = (int) (newCapacity * factor);
			if (save == newCapacity) newCapacity++;
		}
		
		ByteBuffer expanded = direct ? ByteBuffer.allocateDirect(newCapacity) : ByteBuffer.allocate(newCapacity);
		byteBuffer.flip();
		expanded.put(byteBuffer);
		expanded.order(byteBuffer.order()); // this is important!
		this.byteBuffer = expanded; // previous one will be released for the GC!
	}
}