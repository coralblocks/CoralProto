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
package com.coralblocks.coralproto.field;

import java.nio.ByteBuffer;

import com.coralblocks.coralproto.AbstractProto;
import com.coralblocks.coralproto.util.ByteBufferCharSequence;
import com.coralblocks.coralproto.util.ByteBufferUtils;

public class VarCharsField implements ProtoField {
	
	private final boolean isOptional;
	private boolean isPresent;
	private final ByteBufferCharSequence bbcs;
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
		this.bbcs = new ByteBufferCharSequence(maxLength);
		this.isOptional = isOptional;
		reset();
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof VarCharsField) {
			VarCharsField vcf = (VarCharsField) o;
			return vcf.bbcs.equals(this.bbcs);
		}
		return false;
	}
	
	@Override
	public void reset() {
		this.bbcs.setSize(0);		
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
		int len = bbcs.length();
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
		ByteBufferCharSequence bacs = getAndMarkAsPresent();
		bacs.setSize(0);
		return this;
	}
	
	public final void set(CharSequence cs) {
		int len = cs.length();
		enforceMaxLength(len);
		ByteBufferCharSequence bacs = getAndMarkAsPresent();
		bacs.setSize(len);
		ByteBuffer byteBuffer = bacs.getByteBuffer();
		for(int i = 0; i < len; i++) byteBuffer.put((byte) cs.charAt(i));
	}
	
	public final void set(byte[] array) {
		int len = array.length;
		enforceMaxLength(array.length);
		ByteBufferCharSequence bacs = getAndMarkAsPresent();
		bacs.setSize(len);
		ByteBuffer byteBuffer = bacs.getByteBuffer();
		byteBuffer.put(array);
	}
	
	public final void set(char[] array) {
		int len = array.length;
		enforceMaxLength(array.length);
		ByteBufferCharSequence bacs = getAndMarkAsPresent();
		bacs.setSize(len);
		ByteBuffer byteBuffer = bacs.getByteBuffer();
		for(int i = 0; i < len; i++) byteBuffer.put((byte) array[i]);
	}
	
	public final CharSequence get() {
		if (isOptional && !isPresent) throw new IllegalStateException("Cannot get an optional field that is not present!");
		return bbcs;
	}
	
	private final ByteBufferCharSequence getAndMarkAsPresent() {
		if (isOptional) isPresent = true;
		return (ByteBufferCharSequence) get();
	}
	
	@Override
	public final void readFrom(ByteBuffer src) {
		if (isOptional) this.isPresent = true;
		int len = src.getInt();
		enforceMaxLength(len);
		int savedLim = src.limit();
		src.limit(src.position() + len);
		bbcs.setSize(len);
		bbcs.getByteBuffer().put(src);
		src.limit(savedLim);
	}
	
	@Override
	public final void writeTo(ByteBuffer buf) {
		if (isOptional && !isPresent) throw new IllegalStateException("Cannot write a value that is not present!");
		buf.putInt(bbcs.length());
		ByteBuffer byteBuffer = bbcs.getByteBuffer();
		buf.put(byteBuffer);
	}
	
	@Override
	public final void writeAsciiTo(ByteBuffer buf) {
		if (isOptional && !isPresent) throw new IllegalStateException("Cannot write a value that is not present!");
		ByteBufferUtils.appendCharSequence(buf, bbcs);
	}
	
	@Override
	public String toString() {
		if (isOptional) {
			if (isPresent) {
				StringBuilder sb = new StringBuilder(bbcs.length());
				sb.append(bbcs);
				return sb.toString();
			} else {
				return "BLANK";
			}
		} else {
			StringBuilder sb = new StringBuilder(bbcs.length());
			sb.append(bbcs);
			return sb.toString();
		}
	}
}