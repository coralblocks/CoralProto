/*
* Copyright (c) CoralBlocks LLC (c) 2017
 */
package com.coralblocks.coralproto.example;

import java.nio.ByteBuffer;

import com.coralblocks.coralproto.AbstractProto;
import com.coralblocks.coralproto.field.Bytes;
import com.coralblocks.coralproto.field.Chars;
import com.coralblocks.coralproto.field.VarBytes;
import com.coralblocks.coralproto.field.VarChars;

public final class SampleProtoMessage extends AbstractProto {
	
	public static final char TYPE = 'P';
	public static final char SUBTYPE = 'M';
	
	public		byte					aByte;
	public		short					aShort;
	public		int						aInt;
	public		long					aLong;
	public		Bytes					bytes = new Bytes(8);
	public		VarBytes				varBytes = new VarBytes(1024);
	public		Chars					chars = new Chars(8);
	public		VarChars				varChars = new VarChars(1024);
	public		char					aChar;
	public		boolean					aBoolean;

	
	@Override
	public final char getType() {
		return TYPE;
	}
	
	@Override
	public final char getSubtype() {
		return SUBTYPE;
	}
	
	@Override
	public final int getLength() {
		
		return 2 /* Type (1 byte) + Subtype (1 byte) */
			 + 1 /* byte (1 byte) */
			 + 2 /* short (2 bytes) */
			 + 4 /* int (4 bytes) */
			 + 8 /* long (8 bytes) */
			 + bytes.size()
			 + varBytes.size()
			 + chars.size()
			 + varChars.size()
			 + 1 /* char (1 byte) */
			 + 1 /* boolean (1 byte) */;
	}

	@Override
    public final void read(ByteBuffer buf) {

		aByte		=	read(buf, aByte);
		aShort		=	read(buf, aShort);
		aInt		=	read(buf, aInt);
		aLong		=	read(buf, aLong);
		bytes		=	read(buf, bytes);
		varBytes	=	read(buf, varBytes);
		chars		=	read(buf, chars);
		varChars	=	read(buf, varChars);
		aChar		=	read(buf, aChar);
		aBoolean	=	read(buf, aBoolean);
    }

	@Override
    public final void write(ByteBuffer buf) {

		write(buf, TYPE);
		write(buf, SUBTYPE);
		write(buf, aByte);
		write(buf, aShort);
		write(buf, aInt);
		write(buf, aLong);
		write(buf, bytes);
		write(buf, varBytes);
		write(buf, chars);
		write(buf, varChars);
		write(buf, aChar);
		write(buf, aBoolean);
    }
	
	@Override
	public final void writeAscii(boolean shortVersion, ByteBuffer buf) {
		
		writeAsciiTypeSubtypeName(shortVersion, buf);
		
		writeAsciiSeparator(buf);
		writeAscii(buf, aByte);
		
		writeAsciiSeparator(buf);
		writeAscii(buf, aShort);
		
		writeAsciiSeparator(buf);
		writeAscii(buf, aInt);
		
		writeAsciiSeparator(buf);
		writeAscii(buf, aLong);
		
		writeAsciiSeparator(buf);
		writeAscii(buf, bytes);
		
		writeAsciiSeparator(buf);
		writeAscii(buf, varBytes);
		
		writeAsciiSeparator(buf);
		writeAscii(buf, chars);
		
		writeAsciiSeparator(buf);
		writeAscii(buf, varChars);

		writeAsciiSeparator(buf);
		writeAscii(buf, aChar);
		
		writeAsciiSeparator(buf);
		writeAscii(buf, aBoolean);
	}
}
