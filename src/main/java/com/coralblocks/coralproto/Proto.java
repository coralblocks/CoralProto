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
package com.coralblocks.coralproto;

import java.nio.ByteBuffer;

public interface Proto {
	
	/**
	 * Returns the type of this proto message
	 * 
	 * @return the type of this proto message
	 */
	public char getType();
	
	/**
	 * Returns the subtype of this proto message
	 * 
	 * @return the subtype of this proto message
	 */
	public char getSubtype();
	
	/**
	 * Returns the version of this proto message
	 * 
	 * @return the version of this proto message
	 */
	public short getVersion();
	
	/**
	 * Reads the message fields, NOT including the four-byte header: type, subtype,
	 * and version.
	 * The buffer must be externally framed to contain only this message because an
	 * older schema may leave appended fields unread during schema evolution.
	 * 
	 * @param buf where to read this message from
	 */
	public void read(ByteBuffer buf);
	
	/**
	 * Writes the complete message, INCLUDING the four-byte header: type, subtype,
	 * and version.
	 * If the buffer is already large enough, the number of bytes written can be
	 * measured without calling {@link #getLength()} first:
	 * <pre>
	 * int start = buf.position();
	 * proto.write(buf);
	 * int length = buf.position() - start;
	 * </pre>
	 * 
	 * @param buf where to write this message to
	 */
	public void write(ByteBuffer buf);
	
	/**
	 * Writes the contents of this proto message INCLUDING the type and subtype in ASCII so it can be logged.
	 * 
	 * @param shortVersion should we write this message in a short format version?
	 * @param buf where to write this message to
	 */
	public void writeAscii(boolean shortVersion, ByteBuffer buf);
	
	/**
	 * Returns the total message length, INCLUDING the four-byte header: type,
	 * subtype, and version.
	 * Use this when the length must be known before writing, such as when allocating
	 * an exactly sized buffer. When writing to an existing buffer, measuring the
	 * buffer position before and after {@link #write(ByteBuffer)} avoids this
	 * separate pass over the message fields.
	 * 
	 * @return the total length of this message
	 */
	public int getLength();
}
