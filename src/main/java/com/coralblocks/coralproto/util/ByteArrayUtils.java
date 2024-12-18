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

public class ByteArrayUtils {
	
	private ByteArrayUtils() {
		
	}
	
	public static String parseString(byte[] src) {
		int len = src.length;
		char[] chars = new char[len];
		for (int i = 0; i < len; i++) {
			char c = (char) src[i];
			if (CharUtils.isPrintable(c)) {
				chars[i] = c;	
			} else {
				chars[i] = '?';
			}
		}
		return new String(chars);
	}
}