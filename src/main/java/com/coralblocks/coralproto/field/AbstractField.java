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

import com.coralblocks.coralproto.AbstractProto;

/**
 * Holds the optional/present state shared by simple fields. Concrete fields
 * keep their own value and wire-format logic.
 */
abstract class AbstractField implements ProtoField {

	private final boolean optional;
	private boolean present;

	protected AbstractField(AbstractProto proto, boolean optional) {
		if (proto != null) proto.add(this);
		this.optional = optional;
	}

	@Override
	public final boolean isPresent() {
		return !optional || present;
	}

	@Override
	public final boolean isOptional() {
		return optional;
	}

	@Override
	public final void markAsNotPresent() {
		if (!optional) throw new IllegalStateException("Cannot mark a required field as not present!");
		present = false;
	}

	protected final void markAsPresent() {
		if (optional) present = true;
	}

	protected final void resetPresence() {
		present = false;
	}

	protected final void requirePresent() {
		if (optional && !present) throw new IllegalStateException("Cannot get an optional field that is not present!");
	}

	protected final void requirePresentForWrite() {
		if (optional && !present) throw new IllegalStateException("Cannot write a value that is not present!");
	}

	protected final int sizeWithPresence(int valueSize) {
		if (!optional) return valueSize;
		return present ? 1 + valueSize : 1;
	}
}
