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
package com.coralblocks.coralproto;

import java.nio.ByteBuffer;

import org.junit.Assert;
import org.junit.Test;

import com.coralblocks.coralproto.field.Chars;
import com.coralblocks.coralproto.field.CharsField;
import com.coralblocks.coralproto.field.VarBytes;
import com.coralblocks.coralproto.field.VarBytesField;
import com.coralblocks.coralproto.field.VarChars;
import com.coralblocks.coralproto.field.VarCharsField;

public class TruncatedInputTest {

	private static ByteBuffer fixedInput(int available) {
		ByteBuffer bb = ByteBuffer.allocate(32);
		for(int i = 0; i < available; i++) bb.put((byte) ('A' + i));
		bb.flip();
		return bb;
	}

	private static ByteBuffer variableInput(int declaredLength, int available) {
		ByteBuffer bb = ByteBuffer.allocate(32);
		bb.putInt(declaredLength);
		for(int i = 0; i < available; i++) bb.put((byte) ('A' + i));
		bb.flip();
		return bb;
	}

	@Test
	public void testFixedCharsRejectTruncatedInputWithSpareCapacity() {
		ByteBuffer charsInput = fixedInput(2);
		int charsLimit = charsInput.limit();
		Assert.assertThrows(IllegalArgumentException.class, () -> new Chars(4).readFrom(charsInput));
		Assert.assertEquals(charsLimit, charsInput.limit());

		ByteBuffer fieldInput = fixedInput(2);
		int fieldLimit = fieldInput.limit();
		CharsField field = new CharsField(4, true);
		Assert.assertThrows(IllegalArgumentException.class, () -> field.readFrom(fieldInput));
		Assert.assertEquals(fieldLimit, fieldInput.limit());
		Assert.assertFalse(field.isPresent());
	}

	@Test
	public void testVariableFieldsRejectTruncatedInputWithSpareCapacity() {
		Assert.assertThrows(IllegalArgumentException.class, () -> new VarChars(8).readFrom(variableInput(4, 2)));
		Assert.assertThrows(IllegalArgumentException.class, () -> new VarBytes(8).readFrom(variableInput(4, 2)));

		VarCharsField charsField = new VarCharsField(8, true);
		Assert.assertThrows(IllegalArgumentException.class, () -> charsField.readFrom(variableInput(4, 2)));
		Assert.assertFalse(charsField.isPresent());

		VarBytesField bytesField = new VarBytesField(8, true);
		Assert.assertThrows(IllegalArgumentException.class, () -> bytesField.readFrom(variableInput(4, 2)));
		Assert.assertFalse(bytesField.isPresent());
	}

	@Test
	public void testVariableFieldsRejectInvalidLengths() {
		Assert.assertThrows(IllegalArgumentException.class, () -> new VarChars(4).readFrom(variableInput(-1, 0)));
		Assert.assertThrows(IllegalArgumentException.class, () -> new VarBytes(4).readFrom(variableInput(-1, 0)));
		Assert.assertThrows(IllegalArgumentException.class, () -> new VarCharsField(4).readFrom(variableInput(-1, 0)));
		Assert.assertThrows(IllegalArgumentException.class, () -> new VarBytesField(4).readFrom(variableInput(-1, 0)));

		Assert.assertThrows(IllegalArgumentException.class, () -> new VarChars(4).readFrom(variableInput(5, 5)));
		Assert.assertThrows(IllegalArgumentException.class, () -> new VarBytes(4).readFrom(variableInput(5, 5)));
		Assert.assertThrows(IllegalArgumentException.class, () -> new VarCharsField(4).readFrom(variableInput(5, 5)));
		Assert.assertThrows(IllegalArgumentException.class, () -> new VarBytesField(4).readFrom(variableInput(5, 5)));
	}
}
