package com.coralblocks.coralproto.field;

import java.nio.ByteBuffer;

import com.coralblocks.coralproto.AbstractProto;
import com.coralblocks.coralproto.enums.CharEnum;
import com.coralblocks.coralproto.util.ByteBufferUtils;
import com.coralblocks.coralproto.util.CharMap;

public class CharEnumField<E extends CharEnum> implements ProtoField {
	
	private final boolean isOptional;
	private boolean isPresent;
	private E value;
	private CharMap<E> charMap;
	
	public CharEnumField(CharMap<E> charMap) {
		this(null, charMap);
	}
	
	public CharEnumField(AbstractProto proto, CharMap<E> charMap) {
		this(proto, charMap, false);
	}
	
	public CharEnumField(CharMap<E> charMap, boolean isOptional) {
		this(null, charMap, isOptional);
	}
	
	public CharEnumField(AbstractProto proto, CharMap<E> charMap, boolean isOptional) {
		if (proto != null) proto.add(this);
		this.isOptional = isOptional;
		this.charMap = charMap;
		reset();
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof CharEnumField) {
			CharEnumField<?> bf = (CharEnumField<?>) o;
			return bf.value == this.value;
		}
		return false;
	}
	
	@Override
	public void reset() {
		this.value = null;
	}
	
	@Override
	public ProtoField newInstance() {
		return new CharEnumField<E>(null, charMap, this.isOptional);
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
		char c = (char) buf.get();
		this.value = charMap.get(c);
	}
	
	@Override
	public final void writeTo(ByteBuffer buf) {
		if (isOptional && !isPresent) throw new IllegalStateException("Cannot write a value that is not present!");
		if (value == null) throw new IllegalStateException("Cannot write a null value!");
		buf.put((byte) value.getChar());
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
		if (value == null) throw new IllegalArgumentException("Cannot set a null value!");
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