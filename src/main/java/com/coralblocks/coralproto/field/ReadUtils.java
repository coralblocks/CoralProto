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
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.coralblocks.coralproto.field;

import java.nio.ByteBuffer;

/**
 * Validates field lengths before a reader temporarily changes the source
 * buffer's limit. This prevents a truncated message from exposing stale bytes
 * between the current limit and the buffer's capacity.
 */
final class ReadUtils {

	private ReadUtils() {
	}

	/**
	 * Ensures the requested field length is valid and entirely contained in the
	 * source buffer's remaining data.
	 */
	static void ensureRemaining(ByteBuffer src, int required) {
		if (required < 0) {
			throw new IllegalArgumentException("Negative field length: " + required);
		}
		if (src.remaining() < required) {
			throw new IllegalArgumentException("ByteBuffer is too small: remaining=" + src.remaining() + ", required=" + required);
		}
	}

	/**
	 * Reads and validates a variable-length field prefix, then verifies that its
	 * complete payload is available in the source buffer.
	 */
	static int readLength(ByteBuffer src, int maxLength) {
		ensureRemaining(src, Integer.BYTES);
		int len = src.getInt();
		if (len > maxLength) {
			throw new IllegalArgumentException("Field length " + len + " exceeds maximum " + maxLength);
		}
		ensureRemaining(src, len);
		return len;
	}
}
