package com.coralblocks.coralproto.field;

import java.nio.ByteBuffer;

import com.coralblocks.coralproto.AbstractProto;
import com.coralblocks.coralproto.util.ByteArrayCharSequence;

public class CharsField implements ProtoField {
	
	private final boolean isOptional;
	private boolean isPresent;
	private final ByteArrayCharSequence bacs;
	private final int size;
	
	public CharsField(int size) {
		this(null, size);
	}
	
	public CharsField(AbstractProto proto, int size) {
		this(proto, size, false);
	}
	
	public CharsField(int size, boolean isOptional) {
		this(null, size, isOptional);
	}
	
	public CharsField(AbstractProto proto, int size, boolean isOptional) {
		if (proto != null) proto.add(this);
		this.bacs = new ByteArrayCharSequence(size);
		byte[] byteArray = bacs.getByteArray();
		for(int i = 0; i < size; i++) byteArray[i] = (byte) ' ';
		this.isOptional = isOptional;
		this.size = size;
	}
	
	@Override
	public ProtoField newInstance() {
		return new CharsField(null, size, isOptional);
	}

	@Override
	public final int size() {
		if (isOptional) {
			return isPresent ? 1 + size : 1;
		} else {
			return size;
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
	
	private final ByteArrayCharSequence getAndMarkAsPresent() {
		if (isOptional) isPresent = true;
		return (ByteArrayCharSequence) get();
	}
	
	public final CharsField clear() {
		ByteArrayCharSequence bacs = getAndMarkAsPresent();
		byte[] byteArray = bacs.getByteArray();
		for(int i = 0; i < size; i++) byteArray[i] = (byte) ' ';
		return this;
	}
	
	public final void set(CharSequence cs) {
		int len = cs.length();
		if (len > size) {
			throw new IllegalArgumentException("CharSequence is larger than field length: " + cs.toString() + " (" + size + ")");
		}
		ByteArrayCharSequence bacs = getAndMarkAsPresent();
		byte[] byteArray = bacs.getByteArray();
		for(int i = 0; i < len; i++) byteArray[i] = (byte) cs.charAt(i);
		for(int i = len; i < size; i++) byteArray[i] = (byte) ' ';
	}
	
	public final void set(byte[] array) {
		int len = array.length;
		if (len > size) {
			throw new IllegalArgumentException("Array is larger than field length: " + len);
		}
		ByteArrayCharSequence bacs = getAndMarkAsPresent();
		byte[] byteArray = bacs.getByteArray();
		System.arraycopy(array, 0, byteArray, 0, len);
		for(int i = len; i < size; i++)  byteArray[i] = (byte) ' ';
	}
	
	public final void set(char[] array) {
		int len = array.length;
		if (len > size) {
			throw new IllegalArgumentException("Array is larger than field length: " + len);
		}
		ByteArrayCharSequence bacs = getAndMarkAsPresent();
		byte[] byteArray = bacs.getByteArray();
		System.arraycopy(array, 0, byteArray, 0, len);
		for(int i = len; i < size; i++)  byteArray[i] = (byte) ' ';
	}
	
	public final CharSequence get() {
		if (isOptional && !isPresent) throw new IllegalStateException("Cannot get an optional field that is not present!");
		return bacs;
	}
	
	public final byte[] getByteArray() {
		return ((ByteArrayCharSequence) get()).getByteArray();
	}
	
	@Override
	public final void readFrom(ByteBuffer src) {
		if (isOptional) this.isPresent = true;
		byte[] byteArray = bacs.getByteArray();
		src.get(byteArray);
	}
	
	@Override
	public final void writeTo(ByteBuffer buf) {
		if (isOptional && !isPresent) throw new IllegalStateException("Cannot write a value that is not present!");
		byte[] byteArray = bacs.getByteArray();
		buf.put(byteArray);
	}
	
	@Override
	public final void writeAsciiTo(ByteBuffer buf) {
		if (isOptional && !isPresent) throw new IllegalStateException("Cannot write a value that is not present!");
		byte[] byteArray = bacs.getByteArray();
		buf.put(byteArray);
	}
	
	@Override
	public String toString() {
		byte[] byteArray = bacs.getByteArray();
		if (isOptional) {
			if (isPresent) {
				StringBuilder sb = new StringBuilder(size + 2);
				sb.append('[');
				for(int i = 0; i < byteArray.length; i++) sb.append((char) byteArray[i]);
				sb.append(']');
				return sb.toString();
			} else {
				return "BLANK";
			}
		} else {
			StringBuilder sb = new StringBuilder(size + 2);
			sb.append('[');
			for(int i = 0; i < byteArray.length; i++) sb.append((char) byteArray[i]);
			sb.append(']');
			return sb.toString();
		}
	}
}