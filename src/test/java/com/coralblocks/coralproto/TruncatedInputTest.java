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

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

import org.junit.Assert;
import org.junit.Test;

import com.coralblocks.coralproto.field.BooleanField;
import com.coralblocks.coralproto.field.ByteField;
import com.coralblocks.coralproto.field.Bytes;
import com.coralblocks.coralproto.field.BytesField;
import com.coralblocks.coralproto.field.CharEnumField;
import com.coralblocks.coralproto.field.CharField;
import com.coralblocks.coralproto.field.Chars;
import com.coralblocks.coralproto.field.CharsField;
import com.coralblocks.coralproto.field.DoubleField;
import com.coralblocks.coralproto.field.FloatField;
import com.coralblocks.coralproto.field.GroupField;
import com.coralblocks.coralproto.field.IntEnumField;
import com.coralblocks.coralproto.field.IntField;
import com.coralblocks.coralproto.field.LongField;
import com.coralblocks.coralproto.field.ProtoField;
import com.coralblocks.coralproto.field.RepeatingGroupField;
import com.coralblocks.coralproto.field.ShortEnumField;
import com.coralblocks.coralproto.field.ShortField;
import com.coralblocks.coralproto.field.TwoCharEnumField;
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

	private static void assertFixedWidthFieldRejectsEveryTruncation(ProtoField field, int width) {
		for(int available = 0; available < width; available++) {
			ByteBuffer input = fixedInput(available);
			Assert.assertThrows(BufferUnderflowException.class, () -> field.readFrom(input));
		}
	}

	private static void assertTruncatedOptionalFieldRemainsAbsent(ProtoField field, int width) {
		ByteBuffer input = fixedInput(width - 1);
		Assert.assertThrows(BufferUnderflowException.class, () -> field.readFrom(input));
		Assert.assertFalse(field.isPresent());
	}

	@Test
	public void testFixedWidthFieldsRejectEveryTruncation() {
		assertFixedWidthFieldRejectsEveryTruncation(new BooleanField(), Byte.BYTES);
		assertFixedWidthFieldRejectsEveryTruncation(new ByteField(), Byte.BYTES);
		assertFixedWidthFieldRejectsEveryTruncation(new CharField(), Byte.BYTES);
		assertFixedWidthFieldRejectsEveryTruncation(new ShortField(), Short.BYTES);
		assertFixedWidthFieldRejectsEveryTruncation(new IntField(), Integer.BYTES);
		assertFixedWidthFieldRejectsEveryTruncation(new LongField(), Long.BYTES);
		assertFixedWidthFieldRejectsEveryTruncation(new FloatField(), Integer.BYTES);
		assertFixedWidthFieldRejectsEveryTruncation(new DoubleField(), Long.BYTES);
		assertFixedWidthFieldRejectsEveryTruncation(new CharEnumField<EnumTest.Side>(EnumTest.Side.ALL), Byte.BYTES);
		assertFixedWidthFieldRejectsEveryTruncation(new ShortEnumField<EnumTest.RejectReason>(EnumTest.RejectReason.ALL), Short.BYTES);
		assertFixedWidthFieldRejectsEveryTruncation(new IntEnumField<EnumTest.ReduceRejectReason>(EnumTest.ReduceRejectReason.ALL), Integer.BYTES);
		assertFixedWidthFieldRejectsEveryTruncation(new TwoCharEnumField<EnumTest.CancelReason>(EnumTest.CancelReason.ALL), Short.BYTES);
	}

	@Test
	public void testTruncatedOptionalPrimitiveFieldsRemainAbsent() {
		assertTruncatedOptionalFieldRemainsAbsent(new BooleanField(true), Byte.BYTES);
		assertTruncatedOptionalFieldRemainsAbsent(new ByteField(true), Byte.BYTES);
		assertTruncatedOptionalFieldRemainsAbsent(new CharField(true), Byte.BYTES);
		assertTruncatedOptionalFieldRemainsAbsent(new ShortField(true), Short.BYTES);
		assertTruncatedOptionalFieldRemainsAbsent(new IntField(true), Integer.BYTES);
		assertTruncatedOptionalFieldRemainsAbsent(new LongField(true), Long.BYTES);
	}

	@Test
	public void testFixedCharsRejectTruncatedInputWithSpareCapacity() {
		for(int available = 0; available < 4; available++) {
			ByteBuffer charsInput = fixedInput(available);
			int charsLimit = charsInput.limit();
			Assert.assertThrows(IllegalArgumentException.class, () -> new Chars(4).readFrom(charsInput));
			Assert.assertEquals(charsLimit, charsInput.limit());

			ByteBuffer charsFieldInput = fixedInput(available);
			int charsFieldLimit = charsFieldInput.limit();
			CharsField charsField = new CharsField(4, true);
			Assert.assertThrows(IllegalArgumentException.class, () -> charsField.readFrom(charsFieldInput));
			Assert.assertEquals(charsFieldLimit, charsFieldInput.limit());
			Assert.assertFalse(charsField.isPresent());

			ByteBuffer bytesInput = fixedInput(available);
			int bytesLimit = bytesInput.limit();
			Assert.assertThrows(IllegalArgumentException.class, () -> new Bytes(4).readFrom(bytesInput));
			Assert.assertEquals(bytesLimit, bytesInput.limit());

			ByteBuffer bytesFieldInput = fixedInput(available);
			int bytesFieldLimit = bytesFieldInput.limit();
			BytesField bytesField = new BytesField(4, true);
			Assert.assertThrows(IllegalArgumentException.class, () -> bytesField.readFrom(bytesFieldInput));
			Assert.assertEquals(bytesFieldLimit, bytesFieldInput.limit());
			Assert.assertFalse(bytesField.isPresent());
		}
	}

	@Test
	public void testVariableFieldsRejectTruncatedInputWithSpareCapacity() {
		for(int available = 0; available < 4; available++) {
			int availableBytes = available;
			Assert.assertThrows(IllegalArgumentException.class, () -> new VarChars(8).readFrom(variableInput(4, availableBytes)));
			Assert.assertThrows(IllegalArgumentException.class, () -> new VarBytes(8).readFrom(variableInput(4, availableBytes)));

			VarCharsField charsField = new VarCharsField(8, true);
			Assert.assertThrows(IllegalArgumentException.class, () -> charsField.readFrom(variableInput(4, availableBytes)));
			Assert.assertFalse(charsField.isPresent());

			VarBytesField bytesField = new VarBytesField(8, true);
			Assert.assertThrows(IllegalArgumentException.class, () -> bytesField.readFrom(variableInput(4, availableBytes)));
			Assert.assertFalse(bytesField.isPresent());
		}
	}

	@Test
	public void testVariableFieldsRejectEveryTruncatedLengthPrefix() {
		for(int available = 0; available < Integer.BYTES; available++) {
			ByteBuffer charsInput = fixedInput(available);
			Assert.assertThrows(IllegalArgumentException.class, () -> new VarChars(8).readFrom(charsInput));

			ByteBuffer bytesInput = fixedInput(available);
			Assert.assertThrows(IllegalArgumentException.class, () -> new VarBytes(8).readFrom(bytesInput));

			ByteBuffer charsFieldInput = fixedInput(available);
			Assert.assertThrows(IllegalArgumentException.class, () -> new VarCharsField(8).readFrom(charsFieldInput));

			ByteBuffer bytesFieldInput = fixedInput(available);
			Assert.assertThrows(IllegalArgumentException.class, () -> new VarBytesField(8).readFrom(bytesFieldInput));
		}
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

	@Test
	public void testOversizedVarBytesDoesNotChangeSourceLimit() {
		ByteBuffer input = variableInput(5, 2);
		int limit = input.limit();

		Assert.assertThrows(IllegalArgumentException.class, () -> new VarBytesField(4).readFrom(input));
		Assert.assertEquals(limit, input.limit());
	}

	@Test
	public void testRepeatingGroupRejectsNegativeElementCount() {
		RepeatingGroupField group = new RepeatingGroupField(new IntField(), new IntField());

		ByteBuffer input = ByteBuffer.allocate(8);
		input.putShort((short) -1);
		input.flip();

		IllegalArgumentException e = Assert.assertThrows(IllegalArgumentException.class, () -> group.readFrom(input));
		Assert.assertEquals("Negative repeating group element count: -1", e.getMessage());
	}

	@Test
	public void testGroupsRejectEveryTruncatedBoundary() {
		assertFixedWidthFieldRejectsEveryTruncation(new GroupField(new IntField()), Integer.BYTES);

		for(int available = 0; available < Short.BYTES; available++) {
			RepeatingGroupField group = new RepeatingGroupField(new IntField());
			ByteBuffer input = fixedInput(available);
			Assert.assertThrows(BufferUnderflowException.class, () -> group.readFrom(input));
		}

		for(int available = 0; available < Integer.BYTES; available++) {
			RepeatingGroupField group = new RepeatingGroupField(new IntField());
			ByteBuffer input = ByteBuffer.allocate(32);
			input.putShort((short) 1);
			for(int i = 0; i < available; i++) input.put((byte) i);
			input.flip();
			Assert.assertThrows(BufferUnderflowException.class, () -> group.readFrom(input));
		}
	}
}
