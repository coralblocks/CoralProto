package com.coralblocks.coralproto.field;

import java.nio.ByteBuffer;

public interface ProtoField {
	
	public int size();
	
	public boolean isPresent();
	
	public boolean isOptional();
	
	public void markAsNotPresent();
	
	public void markAsPresent();
	
	public void readFrom(ByteBuffer buf);
	
	public void writeTo(ByteBuffer buf);
	
	public void writeAsciiTo(ByteBuffer buf);
	
	public ProtoField newInstance();
}