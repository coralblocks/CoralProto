package com.coralblocks.coralproto.field;

import com.coralblocks.coralproto.AbstractProto;

public class SubtypeField {
	
	private final char subtype;
	
	public SubtypeField(AbstractProto proto, char subtype) {
		proto.setSubtype(subtype);
		this.subtype = subtype;
	}
	
	public char getSubtype() {
		return subtype;
	}
}