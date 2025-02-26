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
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package com.coralblocks.coralproto.field;

import java.nio.ByteBuffer;

import com.coralblocks.coralds.map.IntMap;
import com.coralblocks.coralproto.AbstractProto;
import com.coralblocks.coralproto.enums.ShortEnum;
import com.coralblocks.coralproto.util.ByteBufferUtils;

public class ShortEnumField<E extends ShortEnum> implements ProtoField {
	
	private final boolean isOptional;
	private boolean isPresent;
	private E value;
	private IntMap<E> intMap;
	
	public ShortEnumField(IntMap<E> intMap) {
		this(null, intMap);
	}
	
	public ShortEnumField(AbstractProto proto, IntMap<E> intMap) {
		this(proto, intMap, false);
	}
	
	public ShortEnumField(IntMap<E> intMap, boolean isOptional) {
		this(null, intMap, isOptional);
	}
	
	public ShortEnumField(AbstractProto proto, IntMap<E> intMap, boolean isOptional) {
		if (proto != null) proto.add(this);
		this.isOptional = isOptional;
		this.intMap = intMap;
		reset();
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof ShortEnumField) {
			ShortEnumField<?> bf = (ShortEnumField<?>) o;
			return bf.value == this.value;
		}
		return false;
	}
	
	@Override
	public void reset() {
		this.value = null;
	}
	
	@Override
	public ProtoField newInstance() {
		return new ShortEnumField<E>(null, intMap, this.isOptional);
	}

	@Override
	public final int size() {
		if (isOptional) {
			return isPresent ? 1 + 2 : 1;
		} else {
			return 2;
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
		short s = buf.getShort();
		this.value = intMap.get(s);
	}
	
	@Override
	public final void writeTo(ByteBuffer buf) {
		if (isOptional && !isPresent) throw new IllegalStateException("Cannot write a value that is not present!");
		if (value == null) throw new IllegalStateException("Cannot write a null value!");
		buf.putShort(value.getShort());
	}
	
	@Override
	public final void writeAsciiTo(ByteBuffer buf) {
		if (isOptional && !isPresent) throw new IllegalStateException("Cannot write a value that is not present!");
		if (value == null) throw new IllegalStateException("Cannot write a null value!");
		String s = value.toString();
		ByteBufferUtils.appendCharSequence(buf, s);
	}
	
	public final E get() {
		if (isOptional && !isPresent) throw new IllegalStateException("Cannot get an optional field that is not present!");
		return value;
	}
	
	public final void set(E value) {
		if (isOptional) this.isPresent = true;
		if (value == null) throw new IllegalArgumentException("Cannot set a null value!");
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