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

public class LongField extends AbstractField {

	private final ByteBufferEncoder bbEncoder = new ByteBufferEncoder();
	private long value;

	public LongField() {
		this(null);
	}

	public LongField(AbstractProto proto) {
		this(proto, false);
	}

	public LongField(boolean isOptional) {
		this(null, isOptional);
	}

	public LongField(AbstractProto proto, boolean isOptional) {
		super(proto, isOptional);
		reset();
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof LongField) {
			LongField field = (LongField) o;
			boolean present = isPresent();
			if (field.isPresent() != present) return false;
			return !present || field.value == value;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return isPresent() ? Long.hashCode(value) : 0;
	}

	@Override
	public void reset() {
		resetPresence();
		value = 0;
	}

	@Override
	public ProtoField newInstance() {
		return new LongField(null, isOptional());
	}

	@Override
	public final int size() {
		return sizeWithPresence(Long.BYTES);
	}

	@Override
	public final void readFrom(ByteBuffer buf) {
		value = buf.getLong();
		markAsPresent();
	}

	@Override
	public final void writeTo(ByteBuffer buf) {
		requirePresentForWrite();
		buf.putLong(value);
	}

	@Override
	public final void writeAsciiTo(ByteBuffer buf) {
		requirePresentForWrite();
		bbEncoder.append(buf, value);
	}

	public final long get() {
		requirePresent();
		return value;
	}

	public final void set(long value) {
		this.value = value;
		markAsPresent();
	}

	@Override
	public String toString() {
		return isPresent() ? String.valueOf(value) : "BLANK";
	}
}
