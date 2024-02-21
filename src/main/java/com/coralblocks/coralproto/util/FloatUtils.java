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
	
	public static int toInt(float value) {
		return Math.round(value * DEFAULT_MULTIPLIER);
	}
	
	public static float toFloat(int value) {
		return ((float) value) / ((float) DEFAULT_MULTIPLIER);
	}
	
	public static int toInt(float value, int precision) {
		return Math.round(value * MULTIPLIERS[precision - 1]);
	}
	
	public static float toFloat(int value, int precision) {
		return ((float) value) / ((float) MULTIPLIERS[precision - 1]);
	}
}