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
 * This utility class convert floats with N-decimal precision to ints and vice-versa. 
 */
public class FloatUtils {
	
	/**
	 * The default precision we choose to use.
	 */
	public static final int DEFAULT_PRECISION = 4;
	
	private static final int[] MULTIPLIERS = new int[5];
	
	static {
		for(int i = 1; i <= MULTIPLIERS.length; i++) {
			MULTIPLIERS[i - 1] = (int) Math.pow(10, i);
		}
	}
	
	private static final int DEFAULT_MULTIPLIER = MULTIPLIERS[DEFAULT_PRECISION - 1];
	private static final double MIN_ROUNDABLE_VALUE = Integer.MIN_VALUE - 0.5d;
	private static final double MAX_ROUNDABLE_VALUE = Integer.MAX_VALUE + 0.5d;

	/**
	 * Scales in double precision so the multiplication does not introduce an
	 * additional float-rounding step. The range check rejects non-finite and
	 * overflowing values before rounding and narrowing the result to an int.
	 */
	private static int scaleToInt(float value, int multiplier) {
		double scaled = (double) value * multiplier;
		// Math.round rounds as floor(scaled + 0.5), making the upper bound exclusive.
		if (!Double.isFinite(scaled) || scaled < MIN_ROUNDABLE_VALUE || scaled >= MAX_ROUNDABLE_VALUE) {
			throw new IllegalArgumentException("Float value cannot be represented as a scaled int: " + value);
		}
		return (int) Math.round(scaled);
	}
	
	public static int toInt(float value) {
		return scaleToInt(value, DEFAULT_MULTIPLIER);
	}
	
	public static float toFloat(int value) {
		return ((float) value) / ((float) DEFAULT_MULTIPLIER);
	}
	
	public static int toInt(float value, int precision) {
		return scaleToInt(value, MULTIPLIERS[precision - 1]);
	}
	
	public static float toFloat(int value, int precision) {
		return ((float) value) / ((float) MULTIPLIERS[precision - 1]);
	}
}
