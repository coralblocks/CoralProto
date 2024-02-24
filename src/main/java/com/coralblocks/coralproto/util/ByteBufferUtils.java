/* 
 * Copyright 2024 (c) CoralBlocks - http://www.coralblocks.com
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

public class ByteBufferUtils {
	
	private ByteBufferUtils() {
		
	}
	
	public final static void appendCharSequence(ByteBuffer buf, CharSequence s) {
		int len = s.length();
		for (int i = 0; i < len; i++) {
			buf.put((byte) s.charAt(i));
		}
	}
	
	public final static void parseString(ByteBuffer src, StringBuilder sb) {
		int len = src.remaining();
		for (int i = 0; i < len; i++) {
			char c = (char) src.get();
			if (!CharUtils.isPrintable(c)) {
				sb.append('?');
			} else {
				sb.append(c);
			}
		}
	}
	
	public final static String parseString(ByteBuffer src) {
		StringBuilder sb = new StringBuilder(src.remaining());
		parseString(src, sb);
		return sb.toString();
	}
	
}