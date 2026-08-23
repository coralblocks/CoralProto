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

public class CharsField extends AbstractField {

	private final Chars chars;

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
		super(proto, isOptional);
		this.chars = new Chars(size);
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof CharsField) {
			CharsField field = (CharsField) o;
			boolean present = isPresent();
			if (field.isPresent() != present) return false;
			return !present || chars.equals(field.chars);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return isPresent() ? chars.hashCode() : 0;
	}

	@Override
	public void reset() {
		resetPresence();
		chars.clear();
	}

	@Override
	public ProtoField newInstance() {
		return new CharsField(null, chars.size(), isOptional());
	}

	@Override
	public final int size() {
		return sizeWithPresence(chars.size());
	}

	public final CharsField clear() {
		chars.clear();
		markAsPresent();
		return this;
	}

	public final void set(CharSequence cs) {
		chars.set(cs);
		markAsPresent();
	}

	public final void set(byte[] array) {
		chars.set(array);
		markAsPresent();
	}

	public final void set(char[] array) {
		chars.set(array);
		markAsPresent();
	}

	public final CharSequence get() {
		requirePresent();
		return chars.get();
	}

	public final ByteBuffer getByteBuffer() {
		requirePresent();
		return chars.getByteBuffer();
	}

	@Override
	public final void readFrom(ByteBuffer src) {
		chars.readFrom(src);
		markAsPresent();
	}

	@Override
	public final void writeTo(ByteBuffer buf) {
		requirePresentForWrite();
		chars.writeTo(buf);
	}

	@Override
	public final void writeAsciiTo(ByteBuffer buf) {
		requirePresentForWrite();
		chars.writeAsciiTo(buf);
	}

	@Override
	public String toString() {
		return isPresent() ? chars.toString() : "BLANK";
	}
}
