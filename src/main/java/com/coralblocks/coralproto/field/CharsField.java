package com.coralblocks.coralproto.field;

import java.nio.ByteBuffer;

import com.coralblocks.coralproto.AbstractProto;
import com.coralblocks.coralproto.util.ByteArrayUtils;
import com.coralblocks.coralproto.util.ByteBufferUtils;

public class CharsField implements ProtoField {
	
	private final boolean isOptional;
	private boolean isPresent;
	private final StringBuilder stringBuilder;
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
		this.stringBuilder = new StringBuilder(size);
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
	
	public final StringBuilder getAndMarkAsPresent() {
		if (isOptional) isPresent = true;
		return get();
	}
	
	public final CharsField clear() {
		StringBuilder stringBuilder = getAndMarkAsPresent();
		stringBuilder.setLength(0);
		return this;
	}
	
	public final void set(CharSequence cs) {
		if (cs.length() > size) {
			throw new IllegalArgumentException("CharSequence is larger than field length: " + cs.toString() + " (" + size + ")");
		}
		StringBuilder stringBuilder = getAndMarkAsPresent();
		stringBuilder.setLength(0);
		stringBuilder.append(cs);
	}
	
	public final void set(byte[] array) {
		if (array.length > size) {
			throw new IllegalArgumentException("Array is larger than field length: " + ByteArrayUtils.parseString(array) + " (" + size + ")");
		}
		StringBuilder stringBuilder = getAndMarkAsPresent();
		stringBuilder.setLength(0);
		for(byte b : array) {
			stringBuilder.append((char) b);
		}
	}
	
	public final void set(char[] array) {
		if (array.length > size) {
			throw new IllegalArgumentException("Array is larger than field length: " + new String(array) + " (" + size + ")");
		}
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
	
	@Override
	public final void readFrom(ByteBuffer src) {
		if (isOptional) this.isPresent = true;
		stringBuilder.setLength(0);
		for(int i = 0; i < size; i++) {
			stringBuilder.append((char) src.get());
		}
	}
	
	@Override
	public final void writeTo(ByteBuffer buf) {
		if (stringBuilder.length() > size) {
			throw new IllegalArgumentException("StringBuilder is larger than field length: " + stringBuilder.toString() + " (" + size + ")");
		}
		if (isOptional && !isPresent) throw new IllegalStateException("Cannot write a value that is not present!");
		int rem = stringBuilder.length();
		int paddle = size - rem;
		ByteBufferUtils.appendCharSequence(buf, stringBuilder);
		for(int i = 0; i < paddle; i++) buf.put((byte) ' ');
	}
	
	@Override
	public final void writeAsciiTo(ByteBuffer buf) {
		if (stringBuilder.length() > size) {
			throw new IllegalArgumentException("StringBuilder is larger than field length: " + stringBuilder.toString() + " (" + size + ")");
		}
		if (isOptional && !isPresent) throw new IllegalStateException("Cannot write a value that is not present!");
		int len = stringBuilder.length();
		int paddle = size - len;
		ByteBufferUtils.appendCharSequence(buf, stringBuilder);
		for(int i = 0; i < paddle; i++) buf.put((byte) ' ');
	}
	
	@Override
	public String toString() {
		if (stringBuilder.length() > size) {
			throw new IllegalArgumentException("StringBuilder is larger than max length: " + stringBuilder.toString() + " (" + size + ")");
		}
		int rem = stringBuilder.length();
		int paddle = size - rem;
		if (isOptional) {
			if (isPresent) {
				StringBuilder sb = new StringBuilder(size + 2);
				sb.append('[');
				sb.append(stringBuilder);
				for(int i = 0; i < paddle; i++) sb.append(' ');
				sb.append(']');
				return sb.toString();
			} else {
				return "BLANK";
			}
		} else {
			StringBuilder sb = new StringBuilder(size + 2);
			sb.append('[');
			sb.append(stringBuilder);
			for(int i = 0; i < paddle; i++) sb.append(' ');
			sb.append(']');
			return sb.toString();
		}
	}
}