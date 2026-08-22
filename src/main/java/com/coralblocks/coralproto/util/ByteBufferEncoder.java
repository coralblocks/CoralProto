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

	public final void append(ByteBuffer buf, float number, int precision) {
		if (precision < 1 || precision > 5) throw new IllegalArgumentException("Float precision must be between 1 and 5: " + precision);
		appendScaled(buf, FloatUtils.toInt(number, precision), precision);
	}

	public final void append(ByteBuffer buf, double number, int precision) {
		if (precision < 1 || precision > 10) throw new IllegalArgumentException("Double precision must be between 1 and 10: " + precision);
		appendScaled(buf, DoubleUtils.toLong(number, precision), precision);
	}

	/**
	 * Appends an integer mantissa with the supplied decimal precision. The integer
	 * part is not padded. The fractional part is padded with leading zeroes and
	 * insignificant trailing zeroes are omitted. The conversion uses only integer
	 * arithmetic and does not allocate.
	 */
	private void appendScaled(ByteBuffer buf, long number, int precision) {

		long multiplier = 1;
		for(int i = 0; i < precision; i++) multiplier *= 10;

		long integerPart = number / multiplier;
		long fractionalPart = number % multiplier;
		if (number < 0) {
			buf.put((byte) '-');
			integerPart = -integerPart;
			fractionalPart = -fractionalPart;
		}

		append(buf, integerPart);
		buf.put((byte) '.');

		int fractionalDigits = precision;
		while(fractionalDigits > 1 && fractionalPart % 10 == 0) {
			fractionalPart /= 10;
			fractionalDigits--;
		}

		long divisor = 1;
		for(int i = 1; i < fractionalDigits; i++) divisor *= 10;
		while(divisor > 0) {
			buf.put((byte) ('0' + fractionalPart / divisor));
			fractionalPart %= divisor;
			divisor /= 10;
		}
	}
	
}
