package com.coralblocks.coralproto.field;

import java.nio.ByteBuffer;

import com.coralblocks.coralproto.AbstractProto;
import com.coralblocks.coralproto.enums.IntEnum;
import com.coralblocks.coralproto.util.ByteBufferUtils;
import com.coralblocks.coralproto.util.IntMap;

public class IntEnumField<E extends IntEnum> implements ProtoField {
	
	private final boolean isOptional;
	private boolean isPresent;
	private E value;
	private IntMap<E> intMap;
	
	public IntEnumField(IntMap<E> intMap) {
		this(null, intMap);
	}
	
	public IntEnumField(AbstractProto proto, IntMap<E> intMap) {
		this(proto, intMap, false);
	}
	
	public IntEnumField(IntMap<E> intMap, boolean isOptional) {
		this(null, intMap, isOptional);
	}
	
	public IntEnumField(AbstractProto proto, IntMap<E> intMap, boolean isOptional) {
		if (proto != null) proto.add(this);
		this.isOptional = isOptional;
		this.intMap = intMap;
	}
	
	@Override
	public ProtoField newInstance() {
		return new IntEnumField<E>(null, intMap, this.isOptional);
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
		int i = buf.getInt();
		this.value = intMap.get(i);
	}
	
	@Override
	public final void writeTo(ByteBuffer buf) {
		if (isOptional && !isPresent) throw new IllegalStateException("Cannot write a value that is not present!");
		if (value == null) throw new IllegalStateException("Cannot write a null value!");
		buf.putInt(value.getInt());
	}
	
	@Override
	public final void writeAsciiTo(ByteBuffer buf) {
		if (isOptional && !isPresent) throw new IllegalStateException("Cannot write a value that is not present!");
		if (value == null) throw new IllegalStateException("Cannot write a null value!");
		String s = value.toString();
		ByteBufferUtils.appendCharSequence(buf, s);
	}
	
	public final E get() {
		if (isOptional && !isPresent) throw new IllegalStateException("Cannot get an optional field that is not present!");
		return value;
	}
	
	public final void set(E value) {
		if (isOptional) this.isPresent = true;
		if (value == null) throw new IllegalArgumentException("Cannot set a null value!");
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