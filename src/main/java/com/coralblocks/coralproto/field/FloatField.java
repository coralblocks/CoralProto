package com.coralblocks.coralproto.field;

import java.nio.ByteBuffer;

import com.coralblocks.coralproto.AbstractProto;
import com.coralblocks.coralproto.util.ByteBufferEncoder;
import com.coralblocks.coralproto.util.FloatUtils;

public class FloatField implements ProtoField {
	
	private final ByteBufferEncoder bbEncoder = new ByteBufferEncoder();
	private final boolean isOptional;
	private boolean isPresent;
	private float value;
	private final int precision;
	
	public FloatField() {
		this(null, FloatUtils.DEFAULT_PRECISION);
	}
	
	public FloatField(int precision) {
		this(null, precision);
	}
	
	public FloatField(AbstractProto proto) {
		this(proto, false, FloatUtils.DEFAULT_PRECISION);
	}
	
	public FloatField(AbstractProto proto, int precision) {
		this(proto, false, precision);
	}
	
	public FloatField(boolean isOptional) {
		this(null, isOptional, FloatUtils.DEFAULT_PRECISION);
	}
	
	public FloatField(boolean isOptional, int precision) {
		this(null, isOptional, precision);
	}
	
	public FloatField(AbstractProto proto, boolean isOptional) {
		this(proto, isOptional, FloatUtils.DEFAULT_PRECISION);
	}
	
	public FloatField(AbstractProto proto, boolean isOptional, int precision) {
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
		return new FloatField(null, this.isOptional, this.precision);
	}

	@Override
	public final int size() {
		if (isOptional) {
			return isPresent ? 1 + 4 : 1;
		} else {
			return 4;
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
		int value = buf.getInt();
		this.value = FloatUtils.toFloat(value, precision);
	}
	
	@Override
	public final void writeTo(ByteBuffer buf) {
		if (isOptional && !isPresent) throw new IllegalStateException("Cannot write a value that is not present!");
		int value = FloatUtils.toInt(this.value, precision);
		buf.putInt(value);
	}
	
	@Override
	public final void writeAsciiTo(ByteBuffer buf) {
		if (isOptional && !isPresent) throw new IllegalStateException("Cannot write a value that is not present!");
		bbEncoder.append(buf, value);
	}
	
	public final float get() {
		if (isOptional && !isPresent) throw new IllegalStateException("Cannot get an optional field that is not present!");
		return value;
	}
	
	public final void set(float value) {
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