package com.coralblocks.coralproto.field;

import java.nio.ByteBuffer;

import com.coralblocks.coralproto.AbstractProto;
import com.coralblocks.coralproto.util.ByteArrayCharSequence;

public class VarCharsField implements ProtoField {
	
	private final boolean isOptional;
	private boolean isPresent;
	private final ByteArrayCharSequence bacs;
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
		this.bacs = new ByteArrayCharSequence(maxLength);
		this.bacs.setSize(0);
		this.isOptional = isOptional;
	}
	
	@Override
	public ProtoField newInstance() {
		return new VarCharsField(null, maxLength, isOptional);
	}
	
	public final int getMaxLength() {
		return maxLength;
	}
	
	private final void enforceMaxLength(int size) {
		if (size > maxLength) throw new RuntimeException("Size larger than maxLength: " + size + " (maxLength=" + maxLength + ")");
	}

	@Override
	public final int size() {
		int len = bacs.length();
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
		ByteArrayCharSequence bacs = getAndMarkAsPresent();
		bacs.setSize(0);
		return this;
	}
	
	public final void set(CharSequence cs) {
		int len = cs.length();
		enforceMaxLength(len);
		ByteArrayCharSequence bacs = getAndMarkAsPresent();
		byte[] byteArray = bacs.getByteArray();
		for(int i = 0; i < len; i++) byteArray[i] = (byte) cs.charAt(i);
		bacs.setSize(len);
	}
	
	public final void set(byte[] array) {
		int len = array.length;
		enforceMaxLength(array.length);
		ByteArrayCharSequence bacs = getAndMarkAsPresent();
		byte[] byteArray = bacs.getByteArray();
		System.arraycopy(array, 0, byteArray, 0, len);
		bacs.setSize(len);
	}
	
	public final void set(char[] array) {
		int len = array.length;
		enforceMaxLength(array.length);
		ByteArrayCharSequence bacs = getAndMarkAsPresent();
		byte[] byteArray = bacs.getByteArray();
		for(int i = 0; i < len; i++) byteArray[i] = (byte) array[i];
		bacs.setSize(len);
	}
	
	public final CharSequence get() {
		if (isOptional && !isPresent) throw new IllegalStateException("Cannot get an optional field that is not present!");
		return bacs;
	}
	
	private final ByteArrayCharSequence getAndMarkAsPresent() {
		if (isOptional) isPresent = true;
		return (ByteArrayCharSequence) get();
	}
	
	@Override
	public final void markAsPresent() {
		if (isOptional) this.isPresent = true;
	}
	
	@Override
	public final void readFrom(ByteBuffer src) {
		if (isOptional) this.isPresent = true;
		int len = src.getInt();
		enforceMaxLength(len);
		src.get(bacs.getByteArray(), 0, len);
		bacs.setSize(len);
	}
	
	@Override
	public final void writeTo(ByteBuffer buf) {
		if (isOptional && !isPresent) throw new IllegalStateException("Cannot write a value that is not present!");
		buf.putInt(bacs.length());
		buf.put(bacs.getByteArray(), 0, bacs.length());
	}
	
	@Override
	public final void writeAsciiTo(ByteBuffer buf) {
		if (isOptional && !isPresent) throw new IllegalStateException("Cannot write a value that is not present!");
		buf.putInt(bacs.length());
		buf.put(bacs.getByteArray(), 0, bacs.length());
	}
	
	@Override
	public String toString() {
		if (isOptional) {
			if (isPresent) {
				StringBuilder sb = new StringBuilder(bacs.length());
				sb.append(new String(bacs.getByteArray(), 0, bacs.length()));
				return sb.toString();
			} else {
				return "BLANK";
			}
		} else {
			StringBuilder sb = new StringBuilder(bacs.length());
			sb.append(new String(bacs.getByteArray(), 0, bacs.length()));
			return sb.toString();
		}
	}
}