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

public class CharUtils {
	
	private CharUtils() {
		
	}
	
	public final static boolean isPrintable(char c) {
		byte b = (byte) c;
		if (b >= 32 && b <= 126) return true;
		return false;
	}
	
	public final static short toShort(CharSequence s) {
		
		int len = s.length();
		
		return  (short) (
				((( (len > 0 ? ((byte) s.charAt(0)) : (byte) ' ') & 0xFF)) << 0L)
				+  ((( (len > 1 ? ((byte) s.charAt(1)) : (byte) ' ') & 0xFF)) << 8L)
				);
	}
	
	public final static short toShort(byte b1, byte b2) {
		
		return  (short) ( ( (b1 & 0xFF) << 0L ) +  ( (b2 & 0xFF) << 8L ) );
	}
	
}