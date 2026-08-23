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

public class BytesField extends AbstractField {

	private final Bytes bytes;

	public BytesField(int size) {
		this(null, size);
	}

	public BytesField(AbstractProto proto, int size) {
		this(proto, size, false);
	}

	public BytesField(int size, boolean isOptional) {
		this(null, size, isOptional);
	}

	public BytesField(AbstractProto proto, int size, boolean isOptional) {
		super(proto, isOptional);
		this.bytes = new Bytes(size);
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof BytesField) {
			BytesField field = (BytesField) o;
			boolean present = isPresent();
			if (field.isPresent() != present) return false;
			return !present || bytes.equals(field.bytes);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return isPresent() ? bytes.hashCode() : 0;
	}

	@Override
	public void reset() {
		resetPresence();
		bytes.clear();
	}

	@Override
	public ProtoField newInstance() {
		return new BytesField(null, bytes.size(), isOptional());
	}

	@Override
	public final int size() {
		return sizeWithPresence(bytes.size());
	}

	public final ByteBuffer get() {
		requirePresent();
		return bytes.get();
	}

	public final void set(byte[] array) {
		bytes.set(array);
		markAsPresent();
	}

	public final void set(ByteBuffer buf) {
		bytes.set(buf);
		markAsPresent();
	}

	@Override
	public final void readFrom(ByteBuffer src) {
		bytes.readFrom(src);
		markAsPresent();
	}

	@Override
	public final void writeTo(ByteBuffer buf) {
		requirePresentForWrite();
		bytes.writeTo(buf);
	}

	@Override
	public final void writeAsciiTo(ByteBuffer buf) {
		requirePresentForWrite();
		bytes.writeAsciiTo(buf);
	}

	@Override
	public String toString() {
		return isPresent() ? bytes.toString() : "BLANK";
	}
}
