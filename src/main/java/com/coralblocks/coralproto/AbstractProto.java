/* 
 * Copyright 2015-2024 (c) CoralBlocks LLC - http://www.coralblocks.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package com.coralblocks.coralproto;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.coralblocks.coralproto.field.Bytes;
import com.coralblocks.coralproto.field.Chars;
import com.coralblocks.coralproto.field.ProtoField;
import com.coralblocks.coralproto.field.VarBytes;
import com.coralblocks.coralproto.field.VarChars;
import com.coralblocks.coralproto.util.ByteBufferEncoder;
import com.coralblocks.coralproto.util.ByteBufferUtils;
import com.coralblocks.coralproto.util.CharUtils;

public abstract class AbstractProto implements Proto {
	
	public static final byte SEPARATOR = '|';
	
	private final List<ProtoField> protoFields = new ArrayList<ProtoField>(16);
	private char typeField = 0;
	private char subtypeField = 0;
	private short versionField = 0;
	private final ByteBufferEncoder bbEncoder = new ByteBufferEncoder();
	
	public final void setType(char type) {
		this.typeField = type;
	}
	
	public final void setSubtype(char subtype) {
		this.subtypeField = subtype;
	}
	
	public final void setVersion(short version) {
		this.versionField = version;
	}
	
	public final void add(ProtoField protoField) {
		protoFields.add(protoField);
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
	public short getVersion() {
		return versionField; // can return zero, no problem (default backwards compatible case)
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof AbstractProto) {
			AbstractProto ap = (AbstractProto) o;
			if (ap.getType() != this.getType()) return false;
			if (ap.getSubtype() != this.getSubtype()) return false;
			if (ap.getVersion() != this.getVersion()) return false;
			if (ap.protoFields.size() != this.protoFields.size()) return false;
			for(int i = 0; i < this.protoFields.size(); i++) {
				if (!ap.protoFields.get(i).equals(this.protoFields.get(i))) return false;
			}
			return true;
		}
		return false;
	}
	
	@Override
	public int getLength() {
		int len = 4; // type + subtype + version
		int size = protoFields.size();
		for(int i = 0; i < size; i++) {
			len += protoFields.get(i).size();
		}
		return len;
	}
	
	@Override
    public void read(ByteBuffer buf) {
		int size = protoFields.size();
		for(int i = 0; i < size; i++) {
			read(buf, protoFields.get(i));
		}
	}

	@Override
    public void write(ByteBuffer buf) {

		buf.put((byte) getType());
		buf.put((byte) getSubtype());
		buf.putShort(getVersion());
		
		int size = protoFields.size();
		for(int i = 0; i < size; i++) {
			write(buf, protoFields.get(i));
		}
	}
	
	@Override
    public void writeAscii(boolean shortVersion, ByteBuffer buf) {
		
		if (CharUtils.isPrintable(getType())) {
			buf.put((byte) getType());
		} else {
			buf.put((byte) '?');
		}
		
		if (CharUtils.isPrintable(getSubtype())) {
			buf.put((byte) getSubtype());
		} else {
			buf.put((byte) '?');
		}
		
		if (getVersion() > 0) {
			writeAscii(buf, '-');
			writeAscii(buf, getVersion());
		}
		
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
			if (buf.hasRemaining()) {
				protoField.readFrom(buf);
			} else {
				protoField.reset();
			}
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
	
	private final void writeAscii(ByteBuffer buf, String s) {
		ByteBufferUtils.appendCharSequence(buf, s);
	}
	
	protected final void writeAsciiSeparator(ByteBuffer buf) {
		buf.put(SEPARATOR);
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
		sb.append("Proto:{Type=").append(getType()).append(" Subtype=").append(getSubtype()).append(" Version=").append(getVersion());
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
	
	// DIRECT APPROACH: (a bit faster)
	
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
	
	protected final Bytes read(ByteBuffer buf, Bytes bytes) {
		bytes.readFrom(buf);
		return bytes;
	}
	
	protected final Chars read(ByteBuffer buf, Chars chars) {
		chars.readFrom(buf);
		return chars;
	}
	
	protected final VarBytes read(ByteBuffer buf, VarBytes varBytes) {
		varBytes.readFrom(buf);
		return varBytes;
	}
	
	protected final VarChars read(ByteBuffer buf, VarChars varChars) {
		varChars.readFrom(buf);
		return varChars;
	}
	
	protected final void write(ByteBuffer buf, boolean value) {
		buf.put(value ? (byte) 'Y' : (byte) 'N');
	}
	
	protected final void write(ByteBuffer buf, long value) {
		buf.putLong(value);
	}
	
	protected final void write(ByteBuffer buf, int value) {
		buf.putInt(value);
	}
	
	protected final void write(ByteBuffer buf, short value) {
		buf.putShort(value);
	}
	
	protected final void write(ByteBuffer buf, byte value) {
		buf.put(value);
	}
	
	protected final void write(ByteBuffer buf, char value) {
		buf.put((byte) value);
	}
	
	protected final void write(ByteBuffer buf, Bytes bytes) {
		bytes.writeTo(buf);
	}
	
	protected final void write(ByteBuffer buf, Chars chars) {
		chars.writeTo(buf);
	}
	
	protected final void write(ByteBuffer buf, VarBytes varBytes) {
		varBytes.writeTo(buf);
	}
	
	protected final void write(ByteBuffer buf, VarChars varChars) {
		varChars.writeTo(buf);
	}

	protected final void writeAscii(ByteBuffer buf, boolean value) {
		buf.put(value ? (byte) 'Y' : (byte) 'N');
	}
	
	protected final void writeAscii(ByteBuffer buf, long value) {
		bbEncoder.append(buf, value);
	}
	
	protected final void writeAscii(ByteBuffer buf, int value) {
		bbEncoder.append(buf, value);
	}
	
	protected final void writeAscii(ByteBuffer buf, short value) {
		bbEncoder.append(buf, value);
	}
	
	protected final void writeAscii(ByteBuffer buf, byte value) {
		bbEncoder.append(buf, value);
	}
	
	protected final void writeAscii(ByteBuffer buf, char value) {
		bbEncoder.append(buf, value);
	}
	
	protected final void writeAscii(ByteBuffer buf, Bytes bytes) {
		bytes.writeAsciiTo(buf);
	}
	
	protected final void writeAscii(ByteBuffer buf, Chars chars) {
		chars.writeAsciiTo(buf);
	}
	
	protected final void writeAscii(ByteBuffer buf, VarBytes varBytes) {
		varBytes.writeAsciiTo(buf);
	}
	
	protected final void writeAscii(ByteBuffer buf, VarChars varChars) {
		varChars.writeAsciiTo(buf);
	}
	
	protected final void writeAsciiTypeSubtypeVersionName(boolean shortVersion, ByteBuffer buf) {
		writeAscii(buf, getType());
		writeAscii(buf, getSubtype());
		
		if (getVersion() > 0) {
			writeAscii(buf, '-');
			writeAscii(buf, getVersion());
		}
		
		if (!shortVersion) {
			writeAscii(buf, " (");
			writeAscii(buf, this.getClass().getSimpleName());
			writeAscii(buf, ")");
		}
	}
}
