package com.coralblocks.coralproto.field;

import com.coralblocks.coralproto.AbstractProto;

public class TypeField {
	
	private final char type;
	
	public TypeField(AbstractProto proto, char type) {
		proto.setType(type);
		this.type = type;
	}
	
	public char getType() {
		return type;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof TypeField) {
			TypeField sf = (TypeField) o;
			return sf.type == this.type;
		}
		return false;
	}
}