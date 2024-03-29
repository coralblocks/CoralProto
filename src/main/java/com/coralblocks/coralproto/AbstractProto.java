/* 
 * Copyright 2024 (c) CoralBlocks - http://www.coralblocks.com
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

import com.coralblocks.coralproto.field.ProtoField;
import com.coralblocks.coralproto.util.ByteBufferUtils;
import com.coralblocks.coralproto.util.CharUtils;

public abstract class AbstractProto implements Proto {
	
	public static final byte SEPARATOR = '|';
	
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
	public boolean equals(Object o) {
		if (o instanceof AbstractProto) {
			AbstractProto ap = (AbstractProto) o;
			if (ap.getType() != this.getType()) return false;
			if (ap.getSubtype() != this.getSubtype()) return false;
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
		int len = 2; // type + subtype
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
	
	private final void write(ByteBuffer buf, char value) {
		buf.put((byte) value);
	}
	
	private final void writeAscii(ByteBuffer buf, char value) {
		if (CharUtils.isPrintable(value)) {
			buf.put((byte) value);
		} else {
			buf.put((byte) '?');
		}
	}
	
	private final void writeAsciiSeparator(ByteBuffer buf) {
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
