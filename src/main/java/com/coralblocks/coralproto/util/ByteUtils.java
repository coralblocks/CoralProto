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

public class ByteUtils {
	
	private ByteUtils() {
		
	}
	
	public static final short toShort(byte b1, byte b2) {
		
		return (short) (    (((b1 	& 0xFF)) << 0)
						+  	(((b2 	& 0xFF)) << 8)
					   );
	}
	
	public static final int toInt(byte b1, byte b2, short s1) {
		
		byte high = (byte) ((s1 >> 8) & 0xFF);
		byte low  = (byte) ((s1 >> 0) & 0xFF);
		
		return     (((b1 	& 0xFF)) << 0)
				+  (((b2 	& 0xFF)) << 8)
				+  (((high 	& 0xFF)) << 16)
				+  (((low 	& 0xFF)) << 24);
	}
}