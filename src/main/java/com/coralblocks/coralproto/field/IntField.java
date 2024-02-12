package com.coralblocks.coralproto.field;

import java.nio.ByteBuffer;

import com.coralblocks.coralproto.AbstractProto;
import com.coralblocks.coralproto.util.ByteBufferEncoder;

public class IntField implements ProtoField {
	
	private final ByteBufferEncoder bbEncoder = new ByteBufferEncoder();
	private final boolean isOptional;
	private boolean isPresent;
	private int value;
	
	public IntField() {
		this(null);
	}
	
	public IntField(AbstractProto proto) {
		this(proto, false);
	}
	
	public IntField(boolean isOptional) {
		this(null, isOptional);
	}
	
	public IntField(AbstractProto proto, boolean isOptional) {
		if (proto != null) proto.add(this);
		this.isOptional = isOptional;
	}
	
	@Override
	public ProtoField newInstance() {
		return new IntField(null, this.isOptional);
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
	public final void markAsPresent() {
		if (isOptional) this.isPresent = true;
	}
	
	@Override
	public final void readFrom(ByteBuffer buf) {
		if (isOptional) this.isPresent = true;
		this.value = buf.getInt();
	}
	
	@Override
	public final void writeTo(ByteBuffer buf) {
		if (isOptional && !isPresent) throw new IllegalStateException("Cannot write a value that is not present!");
		buf.putInt(value);
	}
	
	@Override
	public final void writeAsciiTo(ByteBuffer buf) {
		if (isOptional && !isPresent) throw new IllegalStateException("Cannot write a value that is not present!");
		bbEncoder.append(buf, value);
	}
	
	public final int get() {
		if (isOptional && !isPresent) throw new IllegalStateException("Cannot get an optional field that is not present!");
		return value;
	}
	
	public final void set(int value) {
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