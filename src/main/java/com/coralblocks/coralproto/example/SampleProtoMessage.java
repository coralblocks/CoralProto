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
package com.coralblocks.coralproto.example;

import java.nio.ByteBuffer;

import com.coralblocks.coralproto.AbstractProto;
import com.coralblocks.coralproto.field.Bytes;
import com.coralblocks.coralproto.field.Chars;
import com.coralblocks.coralproto.field.IntField;
import com.coralblocks.coralproto.field.VarBytes;
import com.coralblocks.coralproto.field.VarChars;

/**
 * <p>This is a Proto message where the fields are added directly and there is no parser because the fields are read explicitly.</p>
 * <p>That way reading is faster as you don't need to iterate over all the fields in a loop to parse the message.</p>
 * <p>The primitive types are also used <i>as-is</i>, in other words, an <code>int</code> is an <code>int</code> and not a {@link IntField}.</p>
 * <p>
 * To receive a message from a <code>ByteBuffer</code>, the fields are just read one by one. 
 * See {@link #read(ByteBuffer)}, which you maintain and write yourself.
 * Same thing happens when sending out the message to a <code>ByteBuffer</code>, the fields are written one by one.
 * See {@link #write(ByteBuffer)}, which you maintain and write yourself.
 * </p>
 * <p>
 * This is actually just a little bit faster but it can add up when you are parsing thousands of messages per second, 
 * and throughput is more important than latency per message.
 * It gets faster as the number of fields in the message increases. For a small number of fields it is not that much faster.
 * </p> 
 * <p>
 * As you can see, this approach is a bit faster but requires much more explicit coding from your part, and it offers no IDL for message definition.
 * </p>
 */
public final class SampleProtoMessage extends AbstractProto {
	
	public static final char TYPE = 'P';
	public static final char SUBTYPE = 'M';
	public static final short VERSION = 0; // default (no version)
	
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
	public final short getVersion() {
		return VERSION;
	}
	
	@Override
	public final int getLength() {
		
		return 4 /* Type (1 byte) + Subtype (1 byte) + Version (2 bytes) */
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

	/**
	 * Read all the fields, one by one.
	 * 
	 * @param buf the <code>ByteBuffer</code> from where to read the fields
	 */
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

	/**
	 * Write all the fields, one by one.
	 * 
	 * @param buf the <code>ByteBuffer</code> to where to write the fields
	 */
	@Override
    public final void write(ByteBuffer buf) {

		write(buf, TYPE);
		write(buf, SUBTYPE);
		write(buf, VERSION);
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
	
	/**
	 * Write all fields in ascii, one by one.
	 * 
	 * @param shortVersion true to write the ascii short version
	 * @param buf the <code>ByteBuffer</code> to where to write the ascii fields
	 */
	@Override
	public final void writeAscii(boolean shortVersion, ByteBuffer buf) {
		
		writeAsciiTypeSubtypeVersionName(shortVersion, buf);
		
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
