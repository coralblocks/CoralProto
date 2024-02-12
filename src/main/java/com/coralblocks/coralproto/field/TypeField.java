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
}