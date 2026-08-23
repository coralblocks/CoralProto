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

public class VarCharsField extends AbstractField {

	private final VarChars varChars;

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
		super(proto, isOptional);
		this.varChars = new VarChars(maxLength);
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof VarCharsField) {
			VarCharsField field = (VarCharsField) o;
			boolean present = isPresent();
			if (field.isPresent() != present) return false;
			return !present || varChars.equals(field.varChars);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return isPresent() ? varChars.hashCode() : 0;
	}

	@Override
	public void reset() {
		resetPresence();
		varChars.clear();
	}

	@Override
	public ProtoField newInstance() {
		return new VarCharsField(null, varChars.getMaxLength(), isOptional());
	}

	public final int getMaxLength() {
		return varChars.getMaxLength();
	}

	@Override
	public final int size() {
		return sizeWithPresence(varChars.size());
	}

	public final VarCharsField clear() {
		varChars.clear();
		markAsPresent();
		return this;
	}

	public final void set(CharSequence cs) {
		varChars.set(cs);
		markAsPresent();
	}

	public final void set(byte[] array) {
		varChars.set(array);
		markAsPresent();
	}

	public final void set(char[] array) {
		varChars.set(array);
		markAsPresent();
	}

	public final CharSequence get() {
		requirePresent();
		return varChars.get();
	}

	@Override
	public final void readFrom(ByteBuffer src) {
		varChars.readFrom(src);
		markAsPresent();
	}

	@Override
	public final void writeTo(ByteBuffer buf) {
		requirePresentForWrite();
		varChars.writeTo(buf);
	}

	@Override
	public final void writeAsciiTo(ByteBuffer buf) {
		requirePresentForWrite();
		varChars.writeAsciiTo(buf);
	}

	@Override
	public String toString() {
		return isPresent() ? varChars.toString() : "BLANK";
	}
}
