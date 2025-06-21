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
import com.coralblocks.coralproto.enums.TwoCharEnum;
import com.coralblocks.coralproto.util.ByteBufferUtils;
import com.coralblocks.coralproto.util.ByteUtils;

public class TwoCharEnumField<E extends TwoCharEnum> implements ProtoField {
	
	private final boolean isOptional;
	private boolean isPresent;
	private E value;
	private IntMap<E> intMap;
	
	public TwoCharEnumField(IntMap<E> intMap) {
		this(null, intMap);
	}
	
	public TwoCharEnumField(AbstractProto proto, IntMap<E> intMap) {
		this(proto, intMap, false);
	}
	
	public TwoCharEnumField(IntMap<E> intMap, boolean isOptional) {
		this(null, intMap, isOptional);
	}
	
	public TwoCharEnumField(AbstractProto proto, IntMap<E> intMap, boolean isOptional) {
		if (proto != null) proto.add(this);
		this.isOptional = isOptional;
		this.intMap = intMap;
		reset();
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof TwoCharEnumField) {
			TwoCharEnumField<?> bf = (TwoCharEnumField<?>) o;
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
		return new TwoCharEnumField<E>(null, intMap, this.isOptional);
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
		byte b1 = buf.get();
		byte b2 = buf.get();
		int i =  ByteUtils.toShort(b1, b2);
		this.value = intMap.get(i);
	}
	
	@Override
	public final void writeTo(ByteBuffer buf) {
		if (isOptional && !isPresent) throw new IllegalStateException("Cannot write a value that is not present!");
		if (value == null) throw new IllegalStateException("Cannot write a null value!");
		String s = value.getString();
		if (s.length() != 2) throw new IllegalStateException("TwoChar value can only have two chars: size=" + s.length() + " [" + s + "]");
		buf.put((byte) s.charAt(0));
		buf.put((byte) s.charAt(1));
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