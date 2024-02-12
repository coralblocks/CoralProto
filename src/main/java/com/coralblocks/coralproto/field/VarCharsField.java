package com.coralblocks.coralproto.field;

import java.nio.ByteBuffer;

import com.coralblocks.coralproto.AbstractProto;
import com.coralblocks.coralproto.util.ByteBufferUtils;

public class VarCharsField implements ProtoField {
	
	private final boolean isOptional;
	private boolean isPresent;
	private final StringBuilder stringBuilder;
	private final int maxLength;
	
	public VarCharsField(int maxLength) {	
		this(null, maxLength);
	}
	
	public VarCharsField(AbstractProto proto, int maxLength) {
		this(proto, maxLength, false);
	}
	
	public VarCharsField(int maxLength, boolean isOptional) {
		this(null, maxLength, isOptional);
	}
	
	public VarCharsField(AbstractProto proto, int maxLength, boolean isOptional) {
		if (proto != null) proto.add(this);
		this.maxLength = maxLength;
		this.stringBuilder = new StringBuilder(maxLength);
		this.isOptional = isOptional;
	}
	
	@Override
	public ProtoField newInstance() {
		return new VarCharsField(null, stringBuilder.capacity(), isOptional);
	}
	
	public final int getMaxLength() {
		return maxLength;
	}
	
	private final void enforceMaxLength(int size) {
		if (size > maxLength) throw new RuntimeException("Size larger than maxLength: " + size + " (maxLength=" + maxLength + ")");
	}

	@Override
	public final int size() {
		int len = stringBuilder.length();
		enforceMaxLength(len);
		if (isOptional) {
			return isPresent ? 1 + 4 + len : 1;
		} else {
			return 4 + len;
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
	
	public final VarCharsField clear() {
		StringBuilder stringBuilder = getAndMarkAsPresent();
		stringBuilder.setLength(0);
		return this;
	}
	
	public final void set(CharSequence cs) {
		enforceMaxLength(cs.length());
		StringBuilder stringBuilder = getAndMarkAsPresent();
		stringBuilder.setLength(0);
		stringBuilder.append(cs);
	}
	
	public final void set(byte[] array) {
		enforceMaxLength(array.length);
		StringBuilder stringBuilder = getAndMarkAsPresent();
		stringBuilder.setLength(0);
		for(byte b : array) {
			stringBuilder.append((char) b);
		}
	}
	
	public final void set(char[] array) {
		enforceMaxLength(array.length);
		StringBuilder stringBuilder = getAndMarkAsPresent();
		stringBuilder.setLength(0);
		for(char c : array) {
			stringBuilder.append(c);
		}
	}
	
	public final StringBuilder get() {
		if (isOptional && !isPresent) throw new IllegalStateException("Cannot get an optional field that is not present!");
		return stringBuilder;
	}
	
	public final StringBuilder getAndMarkAsPresent() {
		if (isOptional) isPresent = true;
		return get();
	}
	
	@Override
	public final void markAsPresent() {
		if (isOptional) this.isPresent = true;
	}
	
	@Override
	public final void readFrom(ByteBuffer src) {
		if (isOptional) this.isPresent = true;
		stringBuilder.setLength(0);
		int len = src.getInt();
		enforceMaxLength(len);
		for(int i = 0; i < len; i++) {
			stringBuilder.append((char) src.get());
		}
	}
	
	@Override
	public final void writeTo(ByteBuffer buf) {
		if (isOptional && !isPresent) throw new IllegalStateException("Cannot write a value that is not present!");
		int rem = stringBuilder.length();
		enforceMaxLength(rem);
		buf.putInt(rem);
		ByteBufferUtils.appendCharSequence(buf, stringBuilder);
	}
	
	@Override
	public final void writeAsciiTo(ByteBuffer buf) {
		if (isOptional && !isPresent) throw new IllegalStateException("Cannot write a value that is not present!");
		enforceMaxLength(stringBuilder.length());
		ByteBufferUtils.appendCharSequence(buf, stringBuilder);
	}
	
	@Override
	public String toString() {
		enforceMaxLength(stringBuilder.length());
		if (isOptional) {
			if (isPresent) {
				StringBuilder sb = new StringBuilder(stringBuilder.length());
				sb.append(stringBuilder);
				return sb.toString();
			} else {
				return "BLANK";
			}
		} else {
			StringBuilder sb = new StringBuilder(stringBuilder.length());
			sb.append(stringBuilder);
			return sb.toString();
		}
	}
}