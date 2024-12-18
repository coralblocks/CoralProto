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
	
	public char getType();
	
	public char getSubtype();
	
	/**
	 * Reads the contents of this proto message NOT including the type and subtype.
	 * 
	 * @param buf where to read this message from
	 */
	public void read(ByteBuffer buf);
	
	/**
	 * Writes the contents of this proto message INCLUDING the type and subtype.
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
	 * Returns the total length of this proto message INCLUDING the type and subtype.
	 * 
	 * @return the total length of this message
	 */
	public int getLength();
}
