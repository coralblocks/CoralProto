/*
* Copyright (c) CoralBlocks LLC (c) 2017
 */
package com.coralblocks.coralproto;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.coralblocks.coralproto.bytes.FixedBytes;
import com.coralblocks.coralproto.bytes.VarBytes;
import com.coralblocks.coralproto.chars.FixedChars;
import com.coralblocks.coralproto.chars.VarChars;
import com.coralblocks.coralproto.enums.CharEnum;
import com.coralblocks.coralproto.field.ProtoField;
import com.coralblocks.coralproto.util.ByteBufferUtils;
import com.coralblocks.coralproto.util.CharMap;
import com.coralblocks.coralproto.util.CharUtils;
import com.coralblocks.coralproto.util.GrowableByteBuffer;
import com.coralblocks.coralproto.util.MaxLengthStringBuilder;

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
	
	protected final long readUnsignedIntAsLong(ByteBuffer buf) {
		return ((long) buf.getInt()) & 0xffffffffL;
	}
	
	protected final int readUnsignedShortAsInt(ByteBuffer buf) {
		return ((int) buf.getShort()) & 0xffff;
	}
	
	protected final short readUnsignedByteAsShort(ByteBuffer buf) {
		return (short) (((short) buf.get()) & ((short) 0xff));
	}
	
	protected final ByteBuffer read(ByteBuffer buf, ByteBuffer dst) {
		dst.clear();
		dst.put(buf);
		dst.flip();
		return dst;
	}
	
	protected final FixedChars read(ByteBuffer buf, FixedChars fixedChars) {
		read(buf, fixedChars.getMaxLengthStringBuilder());
		return fixedChars;
	}
	
	protected final FixedBytes read(ByteBuffer buf, FixedBytes fixedBytes) {
		ByteBuffer bb = fixedBytes.getByteBuffer();
		bb.clear();
		int saveLim = buf.limit();
		buf.limit(buf.position() + bb.capacity());
		bb.put(buf);
		bb.flip();
		buf.limit(saveLim);
		return fixedBytes;
	}
	
	protected final VarBytes read(ByteBuffer buf, VarBytes varBytes) {
		ByteBuffer bb = varBytes.getByteBuffer();
		int size = buf.getInt();
		bb.clear();
		int saveLim = buf.limit();
		buf.limit(buf.position() + size);
		bb.put(buf);
		bb.flip();
		buf.limit(saveLim);
		return varBytes;
	}
	
	protected final MaxLengthStringBuilder read(ByteBuffer buf, MaxLengthStringBuilder dst) {
		dst.setLength(0);
		int len = dst.getMaxLength();
		for(int i = 0; i < len; i++) {
			byte b = buf.get();
			dst.append((char) b);
		}
		return dst;
	}
	
	protected final VarChars read(ByteBuffer buf, VarChars varChars) {
		StringBuilder dst = varChars.getStringBuilder();
		dst.setLength(0);
		int len = buf.getInt();
		for(int i = 0; i < len; i++) {
			byte b = buf.get();
			dst.append((char) b);
		}
		return varChars;
	}
	
	protected final StringBuilder read(ByteBuffer buf, StringBuilder dst) {
		dst.setLength(0);
		int len = buf.getInt();
		for(int i = 0; i < len; i++) {
			byte b = buf.get();
			dst.append((char) b);
		}
		return dst;
	}
	
	protected final byte[] read(ByteBuffer buf, byte[] dst) {
		buf.get(dst);
		return dst;
	}
	
	protected GrowableByteBuffer read(ByteBuffer buf, GrowableByteBuffer dst) {
		int len = buf.getInt();
		for(int i = 0; i < len; i++) {
			dst.put(buf.get());
		}
		dst.flip();
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
	
	protected final void writeAscii(ByteBuffer buf, VarChars varChars) {
		ByteBufferUtils.appendCharSequence(buf, varChars.getStringBuilder());
	}

	protected final void writeAscii(ByteBuffer buf, FixedChars fixedChars) {
		MaxLengthStringBuilder sb = fixedChars.getMaxLengthStringBuilder();
		int size = sb.getMaxLength();
		int padding = size - sb.length();
		ByteBufferUtils.appendCharSequence(buf, sb);
		for(int i = 0; i < padding; i++) buf.put((byte) ' ');
	}
	
	protected final void writeAscii(ByteBuffer buf, FixedBytes fixedBytes) {
		ByteBuffer bb = fixedBytes.getByteBuffer();
		int pos = bb.position();
		int lim = bb.limit();
		int padding = bb.capacity() - bb.remaining();
		writeAsciiByteBufferAsCharacters(buf, bb);
		for(int i = 0; i < padding; i++) buf.put((byte) ' ');
		bb.limit(lim).position(pos);
	}
	
	protected final void writeAscii(ByteBuffer buf, VarBytes varBytes) {
		ByteBuffer bb = varBytes.getByteBuffer();
		int pos = bb.position();
		int lim = bb.limit();
		writeAsciiByteBufferAsCharacters(buf, bb);
		bb.limit(lim).position(pos);
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
	
	protected final void write(ByteBuffer buf, FixedBytes fixedBytes) {
		ByteBuffer bb = fixedBytes.getByteBuffer();
		int pos = bb.position();
		int lim = bb.limit();
		int padding = bb.capacity() - bb.limit();
		buf.put(bb);
		for(int i = 0; i < padding; i++) buf.put((byte) ' ');
		bb.limit(lim).position(pos);
	}
	
	protected final void write(ByteBuffer buf, VarBytes varBytes) {
		ByteBuffer bb = varBytes.getByteBuffer();
		int pos = bb.position();
		int lim = bb.limit();
		buf.putInt(bb.remaining());
		buf.put(bb);
		bb.limit(lim).position(pos);
	}
	
	protected final void writeChars(ByteBuffer buf, MaxLengthStringBuilder sb) {
		int maxLength = sb.getMaxLength();
		int len = sb.length();
		for(int i = 0; i < maxLength; i++) {
			if (i < len) {
				buf.put((byte) sb.charAt(i));
			} else {
				buf.put((byte) ' ');
			}
		}
	}
	
	protected final void write(ByteBuffer buf, FixedChars fixedChars) {
		MaxLengthStringBuilder sb = fixedChars.getMaxLengthStringBuilder();
		int maxLength = sb.getMaxLength();
		int len = sb.length();
		for(int i = 0; i < maxLength; i++) {
			if (i < len) {
				buf.put((byte) sb.charAt(i));
			} else {
				buf.put((byte) ' ');
			}
		}
	}
	
	protected final void write(ByteBuffer buf, VarChars varChars) {
		StringBuilder sb = varChars.getStringBuilder();
		int len = sb.length();
		buf.putInt(len);
		for(int i = 0; i < len; i++) {
			buf.put((byte) sb.charAt(i));
		}
	}
	
	protected final void write(ByteBuffer buf, StringBuilder sb) {
		int len = sb.length();
		buf.putInt(len);
		for(int i = 0; i < len; i++) {
			buf.put((byte) sb.charAt(i));
		}
	}
	
	protected final void write(ByteBuffer buf, MaxLengthStringBuilder sb) {
		int maxLength = sb.getMaxLength();
		int len = sb.length();
		for(int i = 0; i < maxLength; i++) {
			if (i < len) {
				buf.put((byte) sb.charAt(i));
			} else {
				buf.put((byte) ' ');
			}
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
	
	protected final void write(ByteBuffer buf, GrowableByteBuffer bytes) {
		int pos = bytes.position();
		int lim = bytes.limit();
		int len = bytes.remaining();
		buf.putInt(len);
		for(int i = 0; i < len; i++) {
			buf.put(bytes.get());
		}
		bytes.limit(lim).position(pos);
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
	
	protected final void writeAsciiByteAsCharacter(ByteBuffer buf, byte value) {
		buf.put(value);
	}
	
	protected final void writeAsciiByteArrayAsCharacters(ByteBuffer buf, byte[] array) {
		writeAsciiByteArrayAsCharacters(buf, array, 0, array.length);
	}
	
	protected final void writeAsciiByteArrayAsCharacters(ByteBuffer buf, byte[] array, int len) {
		writeAsciiByteArrayAsCharacters(buf, array, 0, len);
	}
	
	protected final void writeAsciiBytes(ByteBuffer buf, byte[] array) {
		writeAsciiByteArrayAsCharacters(buf, array, 0, array.length);
	}
	
	protected final void writeAscii(ByteBuffer buf, byte[] array) {
		writeAsciiBytes(buf, array);
	}
	
	protected final void writeAsciiBytes(ByteBuffer buf, byte[] array, int len) {
		writeAsciiByteArrayAsCharacters(buf, array, 0, len);
	}
	
	protected final void writeAscii(ByteBuffer buf, byte[] array, int len) {
		writeAsciiBytes(buf, array, len);
	}
	
	protected final void writeAsciiTrimmed(ByteBuffer buf, byte[] array) {
		writeAsciiTrimmedBytes(buf, array);
	}
	
	protected final void writeAsciiTrimmedBytes(ByteBuffer buf, byte[] array) {
		
		int start = 0;
		int end = array.length;
		while (start < end && array[start] <= ' ') {
			start++;
		}
		while (end > start && array[end - 1] <= ' ') {
			end--;
		}
		
		if (start >= end) return;
		
		writeAsciiByteArrayAsCharacters(buf, array, start, end - start);
	}
	
	protected final void writeAsciiByteArrayAsCharacters(ByteBuffer buf, byte[] array, int offset, int len) {
		for(int i = offset; i < offset + len; i++) {
			byte b = array[i];
			if (CharUtils.isPrintable((char) b)) {
				buf.put(b);
			} else {
				buf.put((byte) '?');
			}
		}
	}
	
	protected final void writeAsciiGrowableByteBufferAsCharacters(ByteBuffer buf, GrowableByteBuffer bytes) {
		int pos = bytes.position();
		int lim = bytes.limit();
		int len = bytes.remaining();
		for(int i = 0; i < len; i++) {
			byte b = bytes.get();
			if (CharUtils.isPrintable((char) b)) {
				buf.put(b);
			} else {
				buf.put((byte) '?');
			}
		}
		bytes.limit(lim).position(pos);
	}
	
	protected final void writeAscii(ByteBuffer buf, GrowableByteBuffer bytes) {
		writeAsciiGrowableByteBufferAsCharacters(buf, bytes);
	}
	
	protected final void writeAsciiBytes(ByteBuffer buf, ByteBuffer bb) {
		writeAsciiByteBufferAsCharacters(buf, bb);
	}
	
	protected final void writeAscii(ByteBuffer buf, ByteBuffer bb) {
		writeAsciiBytes(buf, bb);
	}
	
	protected final void writeAsciiByteBufferAsCharacters(ByteBuffer buf, ByteBuffer bb) {
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
	
	protected final void writeLongAsUnsignedInt(ByteBuffer buf, long value) {
		buf.putInt((int) value);
	}
	
	protected final void writeIntAsUnsignedShort(ByteBuffer buf, int value) {
		buf.putShort((short) value);
	}
	
	protected final void writeShortAsUnsignedByte(ByteBuffer buf, short value) {
		buf.put((byte) value);
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