package com.coralblocks.coralproto.field;

import java.nio.ByteBuffer;

import com.coralblocks.coralproto.AbstractProto;
import com.coralblocks.coralproto.util.ByteBufferUtils;

public class GroupField implements ProtoField {
	
	private final ProtoField[] protoFields;
	private final boolean isOptional;
	private boolean isPresent;
	
	public GroupField(ProtoField ... fields) {
		this(null, fields);
	}
	
	public GroupField(AbstractProto proto, ProtoField ... fields) {
		this(proto, false, fields);
	}
	
	public GroupField(boolean isOptional, ProtoField ... fields) {
		this(null, isOptional, fields);
	}
	
	public GroupField(AbstractProto proto, boolean isOptional, ProtoField ... fields) {
		if (proto != null) proto.add(this);
		this.isOptional = isOptional;
		if (fields == null || fields.length < 2) throw new IllegalArgumentException(GroupField.class.getSimpleName() + " must have at least 2 fields!");
		this.protoFields = fields;
		reset();
	}
	
	@Override
	public void reset() {
		for(int i = 0; i < protoFields.length; i++) protoFields[i].reset();
	}
	
	@Override
	public ProtoField newInstance() {
		ProtoField[] fieldsCopy = new ProtoField[protoFields.length];
		for(int i = 0; i < protoFields.length; i++) {
			fieldsCopy[i] = protoFields[i].newInstance();
		}
		return new GroupField(null, isOptional, fieldsCopy);
	}

	@Override
	public final int size() {
		
		int size = 0;
		for(ProtoField f : protoFields) {
			size += f.size();
		}
		
		if (isOptional) {
			if (!isPresent) {
				return 1;
			} else {
				return 1 + size;
			}
		} else {
			return size;
		}
	}

	@Override
	public boolean isPresent() {
		if (!isOptional) return true;
		return isPresent;
	}

	@Override
	public boolean isOptional() {
		return isOptional;
	}
	
	@Override
	public final void markAsNotPresent() {
		if (!isOptional) throw new IllegalStateException("Cannot mark a required field as not present!");
		this.isPresent = false;
	}
	
	public final ProtoField[] internalArray() {
		return protoFields;
	}
	
	public final ProtoField get(int index) {
		return get()[index];
	}
	
	public final ProtoField[] get() {
		if (isOptional && !isPresent) throw new IllegalStateException("Cannot get an optional field that is not present!");
		return protoFields;
	}
	
	public final ProtoField getAndMarkAsPresent(int index) {
		return getAndMarkAsPresent()[index];
	}
	
	public final ProtoField[] getAndMarkAsPresent() {
		if (isOptional) isPresent = true;
		return get();
	}

	@Override
	public void readFrom(ByteBuffer buf) {
		if (isOptional) isPresent = true;
		for(ProtoField protoField : protoFields) {
			if (protoField.isOptional()) {
				boolean isPresent = buf.get() == 'Y';
				if (isPresent) {
					protoField.readFrom(buf);
				} else {
					protoField.markAsNotPresent();
				}
			} else {
				protoField.readFrom(buf);
			}
		}
	}

	@Override
	public void writeTo(ByteBuffer buf) {
		for(ProtoField protoField : protoFields) {
			if (protoField.isOptional()) {
				if (protoField.isPresent()) {
					buf.put((byte) 'Y');
					protoField.writeTo(buf);
				} else {
					buf.put((byte) 'N');
				}
			} else {
				protoField.writeTo(buf);
			}
		}
	}

	@Override
	public void writeAsciiTo(ByteBuffer buf) {
		if (isOptional && !isPresent) {
			ByteBufferUtils.appendCharSequence(buf, "BLANK");
			return;
		}
		for(int i = 0; i < protoFields.length; i++) {
			ProtoField protoField = protoFields[i];
			if (i > 0) ByteBufferUtils.appendCharSequence(buf, ",");
			if (protoField.isOptional()) {
				if (protoField.isPresent()) {
					protoField.writeAsciiTo(buf);
				} else {
					ByteBufferUtils.appendCharSequence(buf, "BLANK");
				}
			} else {
				protoField.writeAsciiTo(buf);
			}
		}
	}
	
	@Override
	public String toString() {
		if (isOptional && !isPresent) {
			return "BLANK";
		}
		StringBuilder sb = new StringBuilder(protoFields.length * 64);
		for(int i = 0; i < protoFields.length; i++) {
			ProtoField protoField = protoFields[i];
			if (i > 0) sb.append(',');
			sb.append(protoField.toString());
		}
		return sb.toString();
	}
}