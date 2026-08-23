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

public class VarBytesField extends AbstractField {

	private final VarBytes varBytes;

	public VarBytesField(int maxLength) {
		this(null, maxLength);
	}

	public VarBytesField(AbstractProto proto, int maxLength) {
		this(proto, maxLength, false);
	}

	public VarBytesField(int maxLength, boolean isOptional) {
		this(null, maxLength, isOptional);
	}

	public VarBytesField(AbstractProto proto, int maxLength, boolean isOptional) {
		super(proto, isOptional);
		this.varBytes = new VarBytes(maxLength);
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof VarBytesField) {
			VarBytesField field = (VarBytesField) o;
			boolean present = isPresent();
			if (field.isPresent() != present) return false;
			return !present || varBytes.equals(field.varBytes);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return isPresent() ? varBytes.hashCode() : 0;
	}

	@Override
	public void reset() {
		resetPresence();
		varBytes.clear();
	}

	@Override
	public ProtoField newInstance() {
		return new VarBytesField(null, varBytes.getMaxLength(), isOptional());
	}

	public final int getMaxLength() {
		return varBytes.getMaxLength();
	}

	@Override
	public final int size() {
		return sizeWithPresence(varBytes.size());
	}

	public final ByteBuffer get() {
		requirePresent();
		return varBytes.get();
	}

	public final void set(byte[] array, int offset, int len) {
		varBytes.set(array, offset, len);
		markAsPresent();
	}

	public final void set(byte[] array) {
		varBytes.set(array);
		markAsPresent();
	}

	public final void set(ByteBuffer buf) {
		varBytes.set(buf);
		markAsPresent();
	}

	@Override
	public final void readFrom(ByteBuffer src) {
		varBytes.readFrom(src);
		markAsPresent();
	}

	@Override
	public final void writeTo(ByteBuffer buf) {
		requirePresentForWrite();
		varBytes.writeTo(buf);
	}

	@Override
	public final void writeAsciiTo(ByteBuffer buf) {
		requirePresentForWrite();
		varBytes.writeAsciiTo(buf);
	}

	@Override
	public String toString() {
		return isPresent() ? varBytes.toString() : "BLANK";
	}
}
