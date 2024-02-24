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
import com.coralblocks.coralproto.util.ByteBufferEncoder;
import com.coralblocks.coralproto.util.DoubleUtils;

public class DoubleField implements ProtoField {
	
	private final ByteBufferEncoder bbEncoder = new ByteBufferEncoder();
	private final boolean isOptional;
	private boolean isPresent;
	private double value;
	private final int precision;
	
	public DoubleField() {
		this(null, DoubleUtils.DEFAULT_PRECISION);
	}
	
	public DoubleField(int precision) {
		this(null, precision);
	}
	
	public DoubleField(AbstractProto proto) {
		this(proto, DoubleUtils.DEFAULT_PRECISION, false);
	}
	
	public DoubleField(AbstractProto proto, int precision) {
		this(proto, precision, false);
	}
	
	public DoubleField(boolean isOptional) {
		this(null, DoubleUtils.DEFAULT_PRECISION, isOptional);
	}
	
	public DoubleField(int precision, boolean isOptional) {
		this(null, precision, isOptional);
	}
	
	public DoubleField(AbstractProto proto, boolean isOptional) {
		this(proto, DoubleUtils.DEFAULT_PRECISION, isOptional);
	}
	
	public DoubleField(AbstractProto proto, int precision, boolean isOptional) {
		if (proto != null) proto.add(this);
		this.isOptional = isOptional;
		this.precision = precision;
		reset();
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof DoubleField) {
			DoubleField cf = (DoubleField) o;
			return cf.value == this.value;
		}
		return false;
	}
	
	@Override
	public void reset() {
		this.value = 0;
	}
	
	public int getPrecision() {
		return precision;
	}
	
	@Override
	public ProtoField newInstance() {
		return new DoubleField(null, this.precision, this.isOptional);
	}

	@Override
	public final int size() {
		if (isOptional) {
			return isPresent ? 1 + 8 : 1;
		} else {
			return 8;
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
	public final void readFrom(ByteBuffer buf) {
		if (isOptional) this.isPresent = true;
		long value = buf.getLong();
		this.value = DoubleUtils.toDouble(value, precision);
	}
	
	@Override
	public final void writeTo(ByteBuffer buf) {
		if (isOptional && !isPresent) throw new IllegalStateException("Cannot write a value that is not present!");
		long value = DoubleUtils.toLong(this.value, precision);
		buf.putLong(value);
	}
	
	@Override
	public final void writeAsciiTo(ByteBuffer buf) {
		if (isOptional && !isPresent) throw new IllegalStateException("Cannot write a value that is not present!");
		bbEncoder.append(buf, value);
	}
	
	public final double get() {
		if (isOptional && !isPresent) throw new IllegalStateException("Cannot get an optional field that is not present!");
		return value;
	}
	
	public final void set(double value) {
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