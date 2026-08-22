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

/**
 * This utility class convert doubles with N-decimal precision to longs and vice-versa. 
 */
public class DoubleUtils {
	
	/**
	 * The default precision we choose to use.
	 */
	public static final int DEFAULT_PRECISION = 8;
	
	private static final long[] MULTIPLIERS = new long[10];
	
	static {
		for(int i = 1; i <= MULTIPLIERS.length; i++) {
			MULTIPLIERS[i - 1] = (long) Math.pow(10, i);
		}
	}
	
	private static final long DEFAULT_MULTIPLIER = MULTIPLIERS[DEFAULT_PRECISION - 1];
	private static final double MIN_ROUNDABLE_VALUE = -0x1.0p63;
	private static final double MAX_ROUNDABLE_VALUE = 0x1.0p63;

	/**
	 * Scales the value and verifies that the result fits the long transport
	 * before rounding. Without this check, Math.round silently converts NaN to
	 * zero and values beyond the long range to Long.MIN_VALUE or Long.MAX_VALUE.
	 */
	private static long scaleToLong(double value, long multiplier) {
		double scaled = value * multiplier;
		// Keep +2^63: Math.round maps it to Long.MAX_VALUE, preserving a decoded maximum long.
		if (!Double.isFinite(scaled) || scaled < MIN_ROUNDABLE_VALUE || scaled > MAX_ROUNDABLE_VALUE) {
			throw new IllegalArgumentException("Double value cannot be represented as a scaled long: " + value);
		}
		return Math.round(scaled);
	}
	
	public static long toLong(double value) {
		return scaleToLong(value, DEFAULT_MULTIPLIER);
	}
	
	public static double toDouble(long value) {
		return ((double) value) / ((double) DEFAULT_MULTIPLIER);
	}
	
	public static long toLong(double value, int precision) {
		return scaleToLong(value, MULTIPLIERS[precision - 1]);
	}
	
	public static double toDouble(long value, int precision) {
		return ((double) value) / ((double) MULTIPLIERS[precision - 1]);
	}
}
