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
import java.util.Iterator;

import com.coralblocks.coralpool.LinkedObjectPool;
import com.coralblocks.coralpool.ObjectPool;
import com.coralblocks.coralpool.util.Builder;
import com.coralblocks.coralproto.AbstractProto;
import com.coralblocks.coralproto.util.ByteBufferEncoder;
import com.coralblocks.coralproto.util.LinkedObjectList;

public class RepeatingGroupField implements ProtoField {
	
	private final ByteBufferEncoder bbEncoder = new ByteBufferEncoder();
	private final ObjectPool<GroupField> groupFieldPool;
	private final LinkedObjectList<GroupField> groupFields = new LinkedObjectList<GroupField>(3);
	private final ProtoField[] protoFields;
	private Iterator<GroupField> iterator;
	
	public RepeatingGroupField(ProtoField ... protoFields) {
		this(null, protoFields);
	}
	
	public RepeatingGroupField(AbstractProto proto, ProtoField ... protoFields) {
		if (proto != null) proto.add(this);
		this.protoFields = protoFields;
		final GroupField groupField = new GroupField(protoFields);
		Builder<GroupField> builder = new Builder<GroupField>() {
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
		if (o instanceof RepeatingGroupField) {
			RepeatingGroupField rgf = (RepeatingGroupField) o;
			if (rgf.groupFields.size() == this.groupFields.size()) {
				Iterator<GroupField> iter1 = this.groupFields.iterator();
				Iterator<GroupField> iter2 = rgf.groupFields.iterator();
				while(iter1.hasNext() && iter2.hasNext()) {
					if (!iter1.next().equals(iter2.next())) return false;
				}
				return true;
			}
		}
		return false;
	}
	
	@Override
	public void reset() {
		Iterator<GroupField> iter = groupFields.iterator();
		while(iter.hasNext()) {
			groupFieldPool.release(iter.next());
		}
		groupFields.clear();
	}
	
	public int getNumberOfElements() {
		return groupFields.size();
	}
	
	public GroupField nextElement() {
		groupFields.addLast(groupFieldPool.get());
		return groupFields.last();
	}
	
	public void beginIteration() {
		if (groupFields.isEmpty()) {
			this.iterator = null;
		} else {
			this.iterator = iterator();
		}
	}
	
	public boolean iterHasNext() {
		if (iterator == null) return false;
		return iterator.hasNext();
	}
	
	public GroupField iterNext() {
		if (iterator == null) return null;
		return iterator.next();
	}
	
	public void clear() {
		Iterator<GroupField> iter = groupFields.iterator();
		while(iter.hasNext()) {
			groupFieldPool.release(iter.next());
		}
		groupFields.clear();
	}
	
	private Iterator<GroupField> iterator() {
		return groupFields.iterator();
	}

	@Override
	public int size() {
		
		int size = 2;
		Iterator<GroupField> iter = groupFields.iterator();
		while(iter.hasNext()) {
			size += iter.next().size();
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
		Iterator<GroupField> iter = iterator();
		while(iter.hasNext()) {
			iter.next().writeTo(buf);
		}
	}

	@Override
	public void writeAsciiTo(ByteBuffer buf) {
		int n = getNumberOfElements();
		bbEncoder.append(buf, n);
		if (n > 0) {
			buf.put((byte) '=');
			buf.put((byte) '[');
			int count = 0;
			Iterator<GroupField> iter = iterator();
			while(iter.hasNext()) {
				if (count > 0) buf.put((byte) ';');
				iter.next().writeAsciiTo(buf);
				count++;
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
			int count = 0;
			Iterator<GroupField> iter = iterator();
			while(iter.hasNext()) {
				if (count > 0) sb.append(';');
				sb.append(iter.next().toString());
				count++;
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