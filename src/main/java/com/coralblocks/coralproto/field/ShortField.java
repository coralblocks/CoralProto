package com.coralblocks.coralproto.field;

import java.nio.ByteBuffer;

import com.coralblocks.coralproto.AbstractProto;
import com.coralblocks.coralproto.util.ByteBufferEncoder;

public class ShortField implements ProtoField {
	
	private final ByteBufferEncoder bbEncoder = new ByteBufferEncoder();
	private final boolean isOptional;
	private boolean isPresent;
	private short value;
	
	public ShortField() {
		this(null);
	}
	
	public ShortField(AbstractProto proto) {
		this(proto, false);
	}
	
	public ShortField(boolean isOptional) {
		this(null, isOptional);
	}
	
	public ShortField(AbstractProto proto, boolean isOptional) {
		if (proto != null) proto.add(this);
		this.isOptional = isOptional;
		reset();
	}
	
	@Override
	public void reset() {
		this.value = 0;
	}
	
	@Override
	public ProtoField newInstance() {
		return new ShortField(null, this.isOptional);
	}

	@Override
	public final int size() {
		if (isOptional) {
			return isPresent ? 1 + 2 : 1;
		} else {
			return 2;
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
		this.value = buf.getShort();
	}
	
	@Override
	public final void writeTo(ByteBuffer buf) {
		if (isOptional && !isPresent) throw new IllegalStateException("Cannot write a value that is not present!");
		buf.putShort(value);
	}
	
	@Override
	public final void writeAsciiTo(ByteBuffer buf) {
		if (isOptional && !isPresent) throw new IllegalStateException("Cannot write a value that is not present!");
		bbEncoder.append(buf, value);
	}
	
	public final short get() {
		if (isOptional && !isPresent) throw new IllegalStateException("Cannot get an optional field that is not present!");
		return value;
	}
	
	public final void set(short value) {
		if (isOptional) this.isPresent = true;
		this.value = value;
	}
	
	public final void set(int value) {
		if (value < Short.MIN_VALUE || value > Short.MAX_VALUE) throw new RuntimeException("Value is not a short: " + value);
		set((short) value);
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