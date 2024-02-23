package com.coralblocks.coralproto.field;

import java.nio.ByteBuffer;

import com.coralblocks.coralproto.AbstractProto;
import com.coralblocks.coralproto.util.ByteBufferEncoder;

public class ByteField implements ProtoField {
	
	private final ByteBufferEncoder bbEncoder = new ByteBufferEncoder();
	private final boolean isOptional;
	private boolean isPresent;
	private byte value;
	
	public ByteField() {
		this(null);
	}
	
	public ByteField(AbstractProto proto) {
		this(proto, false);
	}
	
	public ByteField(boolean isOptional) {
		this(null, isOptional);
	}
	
	public ByteField(AbstractProto proto, boolean isOptional) {
		if (proto != null) proto.add(this);
		this.isOptional = isOptional;
		reset();
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof ByteField) {
			ByteField bf = (ByteField) o;
			return bf.value == this.value;
		}
		return false;
	}
	
	@Override
	public void reset() {
		this.value = 0;
	}
	
	@Override
	public ProtoField newInstance() {
		return new ByteField(null, this.isOptional);
	}

	@Override
	public final int size() {
		if (isOptional) {
			return isPresent ? 1 + 1 : 1;
		} else {
			return 1;
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
	public final void readFrom(ByteBuffer buf) {
		if (isOptional) this.isPresent = true;
		this.value = buf.get();
	}
	
	@Override
	public final void writeTo(ByteBuffer buf) {
		if (isOptional && !isPresent) throw new IllegalStateException("Cannot write a value that is not present!");
		buf.put(value);
	}
	
	@Override
	public final void writeAsciiTo(ByteBuffer buf) {
		if (isOptional && !isPresent) throw new IllegalStateException("Cannot write a value that is not present!");
		bbEncoder.append(buf, value);
	}
	
	public final byte get() {
		if (isOptional && !isPresent) throw new IllegalStateException("Cannot get an optional field that is not present!");
		return value;
	}
	
	public final void set(byte value) {
		if (isOptional) this.isPresent = true;
		this.value = value;
	}
	
	public final void set(int value) {
		if (value > Byte.MAX_VALUE || value < Byte.MIN_VALUE) throw new RuntimeException("Value is not a byte: " + value);
		set((byte) value);
	}
	
	@Override
	public String toString() {
		if (isOptional) {
			return isPresent ? String.valueOf(value) : "BLANK";
		} else {
			return String.valueOf(value);
		}
	}
}