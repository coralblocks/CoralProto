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

public class CharField extends AbstractField {

	private char value;

	public CharField() {
		this(null);
	}

	public CharField(AbstractProto proto) {
		this(proto, false);
	}

	public CharField(boolean isOptional) {
		this(null, isOptional);
	}

	public CharField(AbstractProto proto, boolean isOptional) {
		super(proto, isOptional);
		reset();
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof CharField) {
			CharField field = (CharField) o;
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
		value = ' ';
	}

	@Override
	public ProtoField newInstance() {
		return new CharField(null, isOptional());
	}

	@Override
	public final int size() {
		return sizeWithPresence(Byte.BYTES);
	}

	@Override
	public final void readFrom(ByteBuffer buf) {
		value = (char) buf.get();
		markAsPresent();
	}

	@Override
	public final void writeTo(ByteBuffer buf) {
		requirePresentForWrite();
		buf.put((byte) value);
	}

	@Override
	public final void writeAsciiTo(ByteBuffer buf) {
		writeTo(buf);
	}

	public final char get() {
		requirePresent();
		return value;
	}

	public final void set(char value) {
		this.value = value;
		markAsPresent();
	}

	@Override
	public String toString() {
		return isPresent() ? String.valueOf(value) : "BLANK";
	}
}
