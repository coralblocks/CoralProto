package com.coralblocks.coralproto.field;

import java.nio.ByteBuffer;

import com.coralblocks.coralproto.AbstractProto;
import com.coralblocks.coralproto.enums.TwoCharEnum;
import com.coralblocks.coralproto.util.ByteBufferUtils;
import com.coralblocks.coralproto.util.IntMap;

public class TwoCharEnumField<E extends TwoCharEnum> implements ProtoField {
	
	private final boolean isOptional;
	private boolean isPresent;
	private E value;
	private IntMap<E> intMap;
	
	public TwoCharEnumField(IntMap<E> intMap) {
		this(null, intMap);
	}
	
	public TwoCharEnumField(AbstractProto proto, IntMap<E> intMap) {
		this(proto, intMap, false);
	}
	
	public TwoCharEnumField(IntMap<E> intMap, boolean isOptional) {
		this(null, intMap, isOptional);
	}
	
	public TwoCharEnumField(AbstractProto proto, IntMap<E> intMap, boolean isOptional) {
		if (proto != null) proto.add(this);
		this.isOptional = isOptional;
		this.intMap = intMap;
	}
	
	@Override
	public ProtoField newInstance() {
		return new TwoCharEnumField<E>(null, intMap, this.isOptional);
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
	public final void markAsPresent() {
		if (isOptional) this.isPresent = true;
	}
	
	@Override
	public final void readFrom(ByteBuffer buf) {
		if (isOptional) this.isPresent = true;
		byte b1 = buf.get();
		byte b2 = buf.get();
		int i =  (short) ( ( (b1 & 0xFF) << 0L ) +  ( (b2 & 0xFF) << 8L ) );
		this.value = intMap.get(i);
	}
	
	@Override
	public final void writeTo(ByteBuffer buf) {
		if (isOptional && !isPresent) throw new IllegalStateException("Cannot write a value that is not present!");
		String s = value.getString();
		if (s.length() != 2) throw new IllegalStateException("TwoChar value can only have two chars: size=" + s.length() + " [" + s + "]");
		buf.put((byte) s.charAt(0));
		buf.put((byte) s.charAt(1));
	}
	
	@Override
	public final void writeAsciiTo(ByteBuffer buf) {
		if (isOptional && !isPresent) throw new IllegalStateException("Cannot write a value that is not present!");
		String s = value.toString();
		ByteBufferUtils.appendCharSequence(buf, s);
	}
	
	public final E get() {
		if (isOptional && !isPresent) throw new IllegalStateException("Cannot get an optional field that is not present!");
		return value;
	}
	
	public final void set(E value) {
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