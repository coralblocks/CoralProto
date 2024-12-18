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
package com.coralblocks.coralproto.util;

import java.nio.ByteBuffer;

public class ByteBufferEncoder {

	private final StringBuilder sb;
	
	public ByteBufferEncoder() {
		int maxLength = String.valueOf(Long.MIN_VALUE).length();
		this.sb = new StringBuilder(maxLength);
	}
	
	public final void append(ByteBuffer buf, char c) {
		buf.put((byte) c);
	}
	
	public final void append(ByteBuffer buf, byte number) {
		append(buf, (int) number);
	}
	
	public final void append(ByteBuffer buf, int number) {
		sb.setLength(0);
		sb.append(number);
		ByteBufferUtils.appendCharSequence(buf, sb);
	}
	
	public final void append(ByteBuffer buf, long number) {
		sb.setLength(0);
		sb.append(number);
		ByteBufferUtils.appendCharSequence(buf, sb);
	}
	
	public final void append(ByteBuffer buf, float number) {
		sb.setLength(0);
		sb.append(number);
		ByteBufferUtils.appendCharSequence(buf, sb);
	}
	
	public final void append(ByteBuffer buf, double number) {
		sb.setLength(0);
		sb.append(number);
		ByteBufferUtils.appendCharSequence(buf, sb);
	}
	
}