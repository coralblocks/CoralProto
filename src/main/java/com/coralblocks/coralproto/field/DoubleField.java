package com.coralblocks.coralproto.field;

import java.nio.ByteBuffer;

import com.coralblocks.coralproto.AbstractProto;
import com.coralblocks.coralproto.util.ByteBufferEncoder;
import com.coralblocks.coralproto.util.DoubleUtils;

public class DoubleField implements ProtoField {
	
	private final ByteBufferEncoder bbEncoder = new ByteBufferEncoder();
	private final boolean isOptional;
	private boolean isPresent;
	private double value;
	private final int precision;
	
	public DoubleField() {
		this(null, DoubleUtils.DEFAULT_PRECISION);
	}
	
	public DoubleField(int precision) {
		this(null, precision);
	}
	
	public DoubleField(AbstractProto proto) {
		this(proto, false, DoubleUtils.DEFAULT_PRECISION);
	}
	
	public DoubleField(AbstractProto proto, int precision) {
		this(proto, false, precision);
	}
	
	public DoubleField(boolean isOptional) {
		this(null, isOptional, DoubleUtils.DEFAULT_PRECISION);
	}
	
	public DoubleField(boolean isOptional, int precision) {
		this(null, isOptional, precision);
	}
	
	public DoubleField(AbstractProto proto, boolean isOptional) {
		this(proto, isOptional, DoubleUtils.DEFAULT_PRECISION);
	}
	
	public DoubleField(AbstractProto proto, boolean isOptional, int precision) {
		if (proto != null) proto.add(this);
		this.isOptional = isOptional;
		this.precision = precision;
		reset();
	}
	
	@Override
	public void reset() {
		this.value = 0;
	}
	
	@Override
	public ProtoField newInstance() {
		return new DoubleField(null, this.isOptional, this.precision);
	}

	@Override
	public final int size() {
		if (isOptional) {
			return isPresent ? 1 + 8 : 1;
		} else {
			return 8;
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
		long value = buf.getLong();
		this.value = DoubleUtils.toDouble(value, precision);
	}
	
	@Override
	public final void writeTo(ByteBuffer buf) {
		if (isOptional && !isPresent) throw new IllegalStateException("Cannot write a value that is not present!");
		long value = DoubleUtils.toLong(this.value, precision);
		buf.putLong(value);
	}
	
	@Override
	public final void writeAsciiTo(ByteBuffer buf) {
		if (isOptional && !isPresent) throw new IllegalStateException("Cannot write a value that is not present!");
		bbEncoder.append(buf, value);
	}
	
	public final double get() {
		if (isOptional && !isPresent) throw new IllegalStateException("Cannot get an optional field that is not present!");
		return value;
	}
	
	public final void set(double value) {
		if (isOptional) this.isPresent = true;
		this.value = value;
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