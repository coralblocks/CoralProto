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
import com.coralblocks.coralproto.AbstractProto;
import com.coralblocks.coralproto.util.ByteBufferEncoder;

public class RepeatingGroupField implements ProtoField {

	// Number of reusable group elements created during construction.
	private static final int INITIAL_CAPACITY = 3;

	private final ByteBufferEncoder bbEncoder = new ByteBufferEncoder();
	// This list stores both the elements in the current message and extra elements ready for reuse.
	private final ArrayList<GroupField> groupFields = new ArrayList<GroupField>(INITIAL_CAPACITY);
	private final ProtoField[] protoFields;
	// This is both the current element count and the list position used by nextElement().
	// Entries before it belong to the current message; entries from it onward are available for reuse.
	private int numberOfElements = 0;
	private int cursor = -1;
	
	public RepeatingGroupField(ProtoField ... protoFields) {
		this(null, protoFields);
	}
	
	public RepeatingGroupField(AbstractProto proto, ProtoField ... protoFields) {
		if (proto != null) proto.add(this);
		this.protoFields = protoFields;

		// Create the first three elements now so small groups do not allocate while being used.
		GroupField firstGroupField = new GroupField(protoFields);
		groupFields.addLast(firstGroupField);
		for(int i = 1; i < INITIAL_CAPACITY; i++) {
			groupFields.addLast((GroupField) firstGroupField.newInstance());
		}

		reset();
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == this) return true;
		if (o instanceof RepeatingGroupField) {
			RepeatingGroupField rgf = (RepeatingGroupField) o;
			if (rgf.numberOfElements == this.numberOfElements) {
				for(int i = 0; i < numberOfElements; i++) {
					if (!this.groupFields.get(i).equals(rgf.groupFields.get(i))) return false;
				}
				return true;
			}
		}
		return false;
	}

	@Override
	public int hashCode() {
		int result = 1;
		for(int i = 0; i < numberOfElements; i++) {
			result = 31 * result + groupFields.get(i).hashCode();
		}
		return result;
	}
	
	@Override
	public void reset() {
		clear();
	}
	
	public int getNumberOfElements() {
		return numberOfElements;
	}
	
	public GroupField nextElement() {
		// The element count is written as a signed short on the wire.
		if (numberOfElements >= Short.MAX_VALUE) {
			throw new IllegalStateException("Repeating group cannot contain more than " + Short.MAX_VALUE + " elements");
		}

		// Grow if needed...
		if (numberOfElements == groupFields.size()) {
			groupFields.addLast((GroupField) groupFields.get(0).newInstance());
		}

		// Reuse the element at that position, clear its old values, and move to the next position.
		GroupField groupField = groupFields.get(numberOfElements);
		groupField.reset();
		numberOfElements++;
		return groupField;
	}
	
	public void beginIteration() {
		cursor = numberOfElements == 0 ? -1 : 0;
	}
	
	public boolean iterHasNext() {
		return cursor >= 0 && cursor < numberOfElements;
	}
	
	public GroupField iterNext() {
		if (cursor < 0) return null;
		if (cursor >= numberOfElements) throw new NoSuchElementException();
		return groupFields.get(cursor++);
	}
	
	public void clear() {
		numberOfElements = 0;
		cursor = -1;
	}

	@Override
	public int size() {
		
		int size = 2;
		
		for(int i = 0; i < numberOfElements; i++) {
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
		if (n < 0) {
			throw new IllegalArgumentException("Negative repeating group element count: " + n);
		}
		for(int i = 0; i < n; i++) {
			GroupField groupField = nextElement();
			groupField.readFrom(buf);
		}
	}

	@Override
	public void writeTo(ByteBuffer buf) {
		buf.putShort((short) getNumberOfElements());
		for(int i = 0; i < numberOfElements; i++) {
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
