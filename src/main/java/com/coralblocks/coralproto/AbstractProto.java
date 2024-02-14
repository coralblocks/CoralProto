/*
* Copyright (c) CoralBlocks LLC (c) 2017
 */
package com.coralblocks.coralproto;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.coralblocks.coralproto.enums.CharEnum;
import com.coralblocks.coralproto.field.ProtoField;
import com.coralblocks.coralproto.util.ByteBufferUtils;
import com.coralblocks.coralproto.util.CharMap;
import com.coralblocks.coralproto.util.CharUtils;

public abstract class AbstractProto implements Proto {
	
	protected final byte SEP = '|';
	
	private final StringBuilder stringBuilder = new StringBuilder(String.valueOf(Long.MAX_VALUE).length() + 1);
	
	private final List<ProtoField> protoFields = new ArrayList<ProtoField>(16);
	private char typeField = 0;
	private char subtypeField = 0;
	
	public final void setType(char type) {
		this.typeField = type;
	}
	
	public final void setSubtype(char subtype) {
		this.subtypeField = subtype;
	}
	
	public final void add(ProtoField protoField) {
		if (!protoFields.contains(protoField)) protoFields.add(protoField);
	}
	
	@Override
	public char getType() {
		if (typeField == 0) throw new IllegalStateException("Type field was not defined!");
		return typeField;
	}
	
	@Override
	public char getSubtype() {
		if (subtypeField == 0) throw new IllegalStateException("Subtype field was not defined!");
		return subtypeField;
	}
	
	@Override
	public int getLength() {
		int len = 2; // type + subtype
		int size = protoFields.size();
		for(int i = 0; i < size; i++) {
			len += protoFields.get(i).size();
		}
		return len;
	}
	
	@Override
    public void read(ByteBuffer buf) throws ProtoException {
		int size = protoFields.size();
		for(int i = 0; i < size; i++) {
			read(buf, protoFields.get(i));
		}
	}

	@Override
    public void write(ByteBuffer buf) {

		write(buf, getType());
		write(buf, getSubtype());
		
		int size = protoFields.size();
		for(int i = 0; i < size; i++) {
			write(buf, protoFields.get(i));
		}
	}
	
	@Override
    public void writeAscii(boolean shortVersion, ByteBuffer buf) {
		
		writeAscii(buf, getType());
		writeAscii(buf, getSubtype());
		if (!shortVersion) {
			writeAscii(buf, " (");
			writeAscii(buf, getClass().getSimpleName());
			writeAscii(buf, ")");

		}
		int size = protoFields.size();
		for(int i = 0; i < size; i++) {
			writeAsciiSeparator(buf);
			writeAscii(buf, protoFields.get(i));
		}
	}
	
	private final void read(ByteBuffer buf, ProtoField protoField) {
		if (protoField.isOptional()) {
			boolean isPresent = buf.hasRemaining() && buf.get() == 'Y';
			if (isPresent) {
				protoField.readFrom(buf);
			} else {
				protoField.markAsNotPresent();
			}
		} else {
			protoField.readFrom(buf);
		}
	}
	
	private final void write(ByteBuffer buf, ProtoField protoField) {
		if (protoField.isOptional()) {
			if (protoField.isPresent()) {
				buf.put((byte) 'Y');
				protoField.writeTo(buf);
			} else {
				buf.put((byte) 'N');
			}
		} else {
			protoField.writeTo(buf);
		}
	}
	
	private final void writeAscii(ByteBuffer buf, ProtoField protoField) {
		if (protoField.isOptional()) {
			if (protoField.isPresent()) {
				protoField.writeAsciiTo(buf);
			} else {
				ByteBufferUtils.appendCharSequence(buf, "BLANK");
			}
		} else {
			protoField.writeAsciiTo(buf);
		}
	}
	
	protected final long read(ByteBuffer buf, long value) {
		return buf.getLong();
	}
	
	protected final int read(ByteBuffer buf, int value) {
		return buf.getInt();
	}
	
	protected final short read(ByteBuffer buf, short value) {
		return buf.getShort();
	}
	
	protected final char read(ByteBuffer buf, char value) {
		return (char) buf.get();
	}
	
	protected final byte read(ByteBuffer buf, byte value) {
		return buf.get();
	}
	
	protected final boolean read(ByteBuffer buf, boolean value) {
		byte b = buf.get();
		return b == 'Y';
	}
	
	protected final ByteBuffer read(ByteBuffer buf, ByteBuffer dst) {
		dst.clear();
		dst.put(buf);
		dst.flip();
		return dst;
	}
	
	protected final byte[] read(ByteBuffer buf, byte[] dst) {
		buf.get(dst);
		return dst;
	}
	
	protected final byte[] read(ByteBuffer buf, byte[] dst, int len) {
		buf.get(dst, 0, len);
		return dst;
	}
	
	protected final <E extends Enum<?> & CharEnum> E read(ByteBuffer buf, CharMap<E> all) {
		char c = (char) buf.get();
		return all.get(c);
	}
	
	protected final void write(ByteBuffer buf, boolean value) {
		buf.put(value ? (byte) 'Y' : (byte) 'N');
	}
	
	protected final void write(ByteBuffer buf, long value) {
		buf.putLong(value);
	}
	
	protected final void writeAscii(ByteBuffer buf, long value) {
		stringBuilder.setLength(0);
		stringBuilder.append(value);
		ByteBufferUtils.appendCharSequence(buf, stringBuilder);
	}
	
	protected final void write(ByteBuffer buf, int value) {
		buf.putInt(value);
	}
	
	protected final void writeAscii(ByteBuffer buf, int value) {
		stringBuilder.setLength(0);
		stringBuilder.append(value);
		ByteBufferUtils.appendCharSequence(buf, stringBuilder);
	}
	
	protected final void writeAscii(ByteBuffer buf, String s) {
		ByteBufferUtils.appendCharSequence(buf, s);
	}
	
	protected final void writeAscii(ByteBuffer buf, CharSequence s) {
		ByteBufferUtils.appendCharSequence(buf, s);
	}
	
	protected final void write(ByteBuffer buf, short value) {
		buf.putShort(value);
	}
	
	protected final void writeAscii(ByteBuffer buf, short value) {
		writeAscii(buf, (int) value);
	}
	
	protected final void writeAscii(ByteBuffer buf, double value) {
		stringBuilder.setLength(0);
		stringBuilder.append(value);
		ByteBufferUtils.appendCharSequence(buf, stringBuilder);
	}
	
	protected final void write(ByteBuffer buf, byte value) {
		buf.put(value);
	}
	
	protected final void write(ByteBuffer buf, char value) {
		buf.put((byte) value);
	}
	
	protected final void writeAscii(ByteBuffer buf, boolean value) {
		buf.put(value ? (byte) 'Y' : (byte) 'N');
	}
	
	protected final void write(ByteBuffer buf, StringBuilder sb) {
		int len = sb.length();
		buf.putInt(len);
		for(int i = 0; i < len; i++) {
			buf.put((byte) sb.charAt(i));
		}
	}
	
	protected final void write(ByteBuffer buf, ByteBuffer bb) {
		int pos = bb.position();
		int lim = bb.limit();
		buf.put(bb);
		bb.limit(lim).position(pos);
	}
	
	protected final void write(ByteBuffer buf, byte[] bytes) {
		buf.put(bytes);
	}
	
	protected final void write(ByteBuffer buf, byte[] bytes, int len) {
		buf.put(bytes, 0, len);
	}
	
	protected final void writeAsciiTypeSubtypeName(boolean shortVersion, ByteBuffer buf) {
		writeAscii(buf, getType());
		writeAscii(buf, getSubtype());
		if (!shortVersion) {
			writeAscii(buf, " (");
			writeAscii(buf, this.getClass().getSimpleName());
			writeAscii(buf, ")");
		}
	}
	
	protected final void writeAscii(ByteBuffer buf, char value) {
		if (CharUtils.isPrintable(value)) {
			buf.put((byte) value);
		} else {
			buf.put((byte) '?');
		}
	}
	
	protected final void writeAsciiByteAsChar(ByteBuffer buf, byte value) {
		if (CharUtils.isPrintable((char) value)) {
			buf.put(value);
		} else {
			buf.put((byte) '?');
		}
	}
	
	protected final void writeAscii(ByteBuffer buf, byte value) {
		writeAscii(buf, (int) value);
	}
	
	protected final void writeAsciiSpace(ByteBuffer buf) {
		buf.put((byte) ' ');
	}
	
	protected final void writeAscii(ByteBuffer buf, byte[] array) {
		for(int i = 0; i < array.length; i++) {
			byte b = array[i];
			if (CharUtils.isPrintable((char) b)) {
				buf.put(b);
			} else {
				buf.put((byte) '?');
			}
		}
	}
	
	protected final void writeAscii(ByteBuffer buf, ByteBuffer bb) {
		int pos = bb.position();
		int lim = bb.limit();
		while(bb.hasRemaining()) {
			byte b = bb.get();
			if (CharUtils.isPrintable((char) b)) {
				buf.put(b);
			} else {
				buf.put((byte) '?');
			}
		}
		bb.limit(lim).position(pos);
	}
	
	protected final <E extends Enum<?>> void writeAscii(ByteBuffer buf, E e) {
		ByteBufferUtils.appendCharSequence(buf, e.toString());
	}
	
	protected final void writeAsciiSeparator(ByteBuffer buf) {
		buf.put(SEP);
	}
	
	protected final <E extends Enum<?> & CharEnum> void write(ByteBuffer buf, E e) {
		buf.put((byte) e.getChar());
	}
	
	private StringBuilder sb;
	
	@Override
	public String toString() {
		if (sb == null) sb = new StringBuilder(1024);
		sb.setLength(0);
		toString(sb);
		return sb.toString();
	}
	
	public void toString(StringBuilder sb) {
		sb.append("Proto:{Type=").append(getType()).append(" Subtype=").append(getSubtype());
		if (protoFields.isEmpty()) {
			sb.append("}");
		} else {
			sb.append(" | ");
			for(int i = 0; i < protoFields.size(); i++) {
				if (i > 0) sb.append(" ");
				ProtoField f = protoFields.get(i);
				sb.append(f.toString());
			}
			sb.append("}");
		}
	}
}
