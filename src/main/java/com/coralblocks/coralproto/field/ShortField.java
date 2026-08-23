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
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.coralblocks.coralproto.field;

import java.nio.ByteBuffer;

import com.coralblocks.coralproto.AbstractProto;
import com.coralblocks.coralproto.util.ByteBufferEncoder;

public class ShortField extends AbstractField {

	private final ByteBufferEncoder bbEncoder = new ByteBufferEncoder();
	private short value;

	public ShortField() {
		this(null);
	}

	public ShortField(AbstractProto proto) {
		this(proto, false);
	}

	public ShortField(boolean isOptional) {
		this(null, isOptional);
	}

	public ShortField(AbstractProto proto, boolean isOptional) {
		super(proto, isOptional);
		reset();
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof ShortField) {
			ShortField field = (ShortField) o;
			boolean present = isPresent();
			if (field.isPresent() != present) return false;
			return !present || field.value == value;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return isPresent() ? value : 0;
	}

	@Override
	public void reset() {
		resetPresence();
		value = 0;
	}

	@Override
	public ProtoField newInstance() {
		return new ShortField(null, isOptional());
	}

	@Override
	public final int size() {
		return sizeWithPresence(Short.BYTES);
	}

	@Override
	public final void readFrom(ByteBuffer buf) {
		value = buf.getShort();
		markAsPresent();
	}

	@Override
	public final void writeTo(ByteBuffer buf) {
		requirePresentForWrite();
		buf.putShort(value);
	}

	@Override
	public final void writeAsciiTo(ByteBuffer buf) {
		requirePresentForWrite();
		bbEncoder.append(buf, value);
	}

	public final short get() {
		requirePresent();
		return value;
	}

	public final void set(short value) {
		this.value = value;
		markAsPresent();
	}

	public final void set(int value) {
		if (value < Short.MIN_VALUE || value > Short.MAX_VALUE) throw new RuntimeException("Value is not a short: " + value);
		set((short) value);
	}

	@Override
	public String toString() {
		return isPresent() ? String.valueOf(value) : "BLANK";
	}
}
