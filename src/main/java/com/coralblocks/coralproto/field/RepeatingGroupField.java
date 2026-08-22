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
import java.util.NoSuchElementException;

import com.coralblocks.coralds.list.ArrayList;
import com.coralblocks.coralpool.LinkedObjectPool;
import com.coralblocks.coralpool.ObjectBuilder;
import com.coralblocks.coralpool.ObjectPool;
import com.coralblocks.coralproto.AbstractProto;
import com.coralblocks.coralproto.util.ByteBufferEncoder;

public class RepeatingGroupField implements ProtoField {
	
	private final ByteBufferEncoder bbEncoder = new ByteBufferEncoder();
	private final ObjectPool<GroupField> groupFieldPool;
	private final ArrayList<GroupField> groupFields = new ArrayList<GroupField>(3);
	private final ProtoField[] protoFields;
	private int cursor = -1;
	
	public RepeatingGroupField(ProtoField ... protoFields) {
		this(null, protoFields);
	}
	
	public RepeatingGroupField(AbstractProto proto, ProtoField ... protoFields) {
		if (proto != null) proto.add(this);
		this.protoFields = protoFields;
		final GroupField groupField = new GroupField(protoFields);
		ObjectBuilder<GroupField> builder = new ObjectBuilder<GroupField>() {
			@Override
			public GroupField newInstance() {
				return (GroupField) groupField.newInstance();
			}
		};
		this.groupFieldPool = new LinkedObjectPool<GroupField>(2, builder);
		this.groupFieldPool.release(groupField);
		reset();
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == this) return true;
		if (o instanceof RepeatingGroupField) {
			RepeatingGroupField rgf = (RepeatingGroupField) o;
			if (rgf.groupFields.size() == this.groupFields.size()) {
				for(int i = 0; i < groupFields.size(); i++) {
					if (!this.groupFields.get(i).equals(rgf.groupFields.get(i))) return false;
				}
				return true;
			}
		}
		return false;
	}
	
	@Override
	public void reset() {
		for(int i = 0; i < groupFields.size(); i++) {
			groupFieldPool.release(groupFields.get(i));
		}
		groupFields.clear();
		cursor = -1;
	}
	
	public int getNumberOfElements() {
		return groupFields.size();
	}
	
	public GroupField nextElement() {
		GroupField groupField = groupFieldPool.get();
		groupField.reset();
		groupFields.addLast(groupField);
		return groupField;
	}
	
	public void beginIteration() {
		cursor = groupFields.isEmpty() ? -1 : 0;
	}
	
	public boolean iterHasNext() {
		return cursor >= 0 && cursor < groupFields.size();
	}
	
	public GroupField iterNext() {
		if (cursor < 0) return null;
		if (cursor >= groupFields.size()) throw new NoSuchElementException();
		return groupFields.get(cursor++);
	}
	
	public void clear() {
		for(int i = 0; i < groupFields.size(); i++) {
			groupFieldPool.release(groupFields.get(i));
		}
		groupFields.clear();
		cursor = -1;
	}

	@Override
	public int size() {
		
		int size = 2;
		for(int i = 0; i < groupFields.size(); i++) {
			size += groupFields.get(i).size();
		}
		
		return size;
	}
	
	@Override
	public boolean isPresent() {
		return true;
	}

	@Override
	public boolean isOptional() {
		return false;
	}
	
	@Override
	public final void markAsNotPresent() {
		throw new IllegalStateException("Cannot mark a required field as not present!");
	}
	
	@Override
	public void readFrom(ByteBuffer buf) {
		clear();
		short n = buf.getShort();
		for(int i = 0; i < n; i++) {
			GroupField groupField = nextElement();
			groupField.readFrom(buf);
		}
	}

	@Override
	public void writeTo(ByteBuffer buf) {
		buf.putShort((short) getNumberOfElements());
		for(int i = 0; i < groupFields.size(); i++) {
			groupFields.get(i).writeTo(buf);
		}
	}

	@Override
	public void writeAsciiTo(ByteBuffer buf) {
		int n = getNumberOfElements();
		bbEncoder.append(buf, n);
		if (n > 0) {
			buf.put((byte) '=');
			buf.put((byte) '[');
			for(int i = 0; i < n; i++) {
				if (i > 0) buf.put((byte) ';');
				groupFields.get(i).writeAsciiTo(buf);
			}
			buf.put((byte) ']');
		}
	}
	
	@Override
	public String toString() {
		int n = getNumberOfElements();
		StringBuilder sb = new StringBuilder(n * 64);
		sb.append(n);
		if (n > 0) {
			sb.append("=[");
			for(int i = 0; i < n; i++) {
				if (i > 0) sb.append(';');
				sb.append(groupFields.get(i).toString());
			}
			sb.append(']');
		}
		
		return sb.toString();
	}

	@Override
	public ProtoField newInstance() {
		ProtoField[] copyFields = new ProtoField[protoFields.length];
		for(int i = 0; i < protoFields.length; i++) {
			copyFields[i] = protoFields[i].newInstance();
		}
		return newInstance(copyFields);
	}
	
	protected RepeatingGroupField newInstance(ProtoField[] protoFields) {
		return new RepeatingGroupField(null, protoFields);
	}
}
