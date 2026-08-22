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

import com.coralblocks.coralproto.field.BooleanField;
import com.coralblocks.coralproto.field.ByteField;
import com.coralblocks.coralproto.field.BytesField;
import com.coralblocks.coralproto.field.CharEnumField;
import com.coralblocks.coralproto.field.CharField;
import com.coralblocks.coralproto.field.CharsField;
import com.coralblocks.coralproto.field.DoubleField;
import com.coralblocks.coralproto.field.FloatField;
import com.coralblocks.coralproto.field.GroupField;
import com.coralblocks.coralproto.field.IntEnumField;
import com.coralblocks.coralproto.field.IntField;
import com.coralblocks.coralproto.field.LongField;
import com.coralblocks.coralproto.field.ProtoField;
import com.coralblocks.coralproto.field.ShortEnumField;
import com.coralblocks.coralproto.field.ShortField;
import com.coralblocks.coralproto.field.SubtypeField;
import com.coralblocks.coralproto.field.TwoCharEnumField;
import com.coralblocks.coralproto.field.TypeField;
import com.coralblocks.coralproto.field.VarBytesField;
import com.coralblocks.coralproto.field.VarCharsField;

public class FieldResetTest {

	private static class OldMessage extends AbstractProto {
		public final TypeField type = new TypeField(this, 'R');
		public final SubtypeField subtype = new SubtypeField(this, 'S');
		public final IntField sequence = new IntField(this);
	}

	private static class NewMessage extends AbstractProto {
		public final TypeField type = new TypeField(this, 'R');
		public final SubtypeField subtype = new SubtypeField(this, 'S');
		public final IntField sequence = new IntField(this);
		public final CharEnumField<EnumTest.Side> side = new CharEnumField<EnumTest.Side>(EnumTest.Side.ALL, true);
		public final IntField quantity = new IntField();
		public final GroupField appendedGroup = new GroupField(this, side, quantity);
	}

	private static void assertResetClearsPresence(ProtoField field) {
		Assert.assertTrue(field.isPresent());
		field.reset();
		Assert.assertFalse(field.isPresent());
	}

	@Test
	public void testPrimitiveFieldResetClearsPresence() {
		BooleanField booleanField = new BooleanField(true);
		booleanField.set(true);
		assertResetClearsPresence(booleanField);

		ByteField byteField = new ByteField(true);
		byteField.set(1);
		assertResetClearsPresence(byteField);

		CharField charField = new CharField(true);
		charField.set('A');
		assertResetClearsPresence(charField);

		IntField intField = new IntField(true);
		intField.set(1);
		assertResetClearsPresence(intField);

		LongField longField = new LongField(true);
		longField.set(1);
		assertResetClearsPresence(longField);

		ShortField shortField = new ShortField(true);
		shortField.set(1);
		assertResetClearsPresence(shortField);

		FloatField floatField = new FloatField(true);
		floatField.set(1.5f);
		assertResetClearsPresence(floatField);

		DoubleField doubleField = new DoubleField(true);
		doubleField.set(1.5);
		assertResetClearsPresence(doubleField);
	}

	@Test
	public void testArrayFieldResetClearsPresence() {
		BytesField bytesField = new BytesField(4, true);
		bytesField.set(new byte[] { 1, 2, 3, 4 });
		assertResetClearsPresence(bytesField);

		CharsField charsField = new CharsField(4, true);
		charsField.set("ABCD");
		assertResetClearsPresence(charsField);

		VarBytesField varBytesField = new VarBytesField(4, true);
		varBytesField.set(new byte[] { 1, 2, 3, 4 });
		assertResetClearsPresence(varBytesField);

		VarCharsField varCharsField = new VarCharsField(4, true);
		varCharsField.set("ABCD");
		assertResetClearsPresence(varCharsField);
	}

	@Test
	public void testEnumFieldResetClearsPresence() {
		CharEnumField<EnumTest.Side> charEnum = new CharEnumField<EnumTest.Side>(EnumTest.Side.ALL, true);
		charEnum.set(EnumTest.Side.BUY);
		assertResetClearsPresence(charEnum);

		ShortEnumField<EnumTest.RejectReason> shortEnum = new ShortEnumField<EnumTest.RejectReason>(EnumTest.RejectReason.ALL, true);
		shortEnum.set(EnumTest.RejectReason.BAD_TYPE);
		assertResetClearsPresence(shortEnum);

		IntEnumField<EnumTest.ReduceRejectReason> intEnum = new IntEnumField<EnumTest.ReduceRejectReason>(EnumTest.ReduceRejectReason.ALL, true);
		intEnum.set(EnumTest.ReduceRejectReason.ZERO);
		assertResetClearsPresence(intEnum);

		TwoCharEnumField<EnumTest.CancelReason> twoCharEnum = new TwoCharEnumField<EnumTest.CancelReason>(EnumTest.CancelReason.ALL, true);
		twoCharEnum.set(EnumTest.CancelReason.MISSED);
		assertResetClearsPresence(twoCharEnum);
	}

	@Test
	public void testGroupFieldResetClearsPresenceRecursively() {
		IntField optionalInt = new IntField(true);
		CharEnumField<EnumTest.Side> optionalEnum = new CharEnumField<EnumTest.Side>(EnumTest.Side.ALL, true);
		GroupField group = new GroupField(true, optionalInt, optionalEnum);
		group.getAndMarkAsPresent();
		optionalInt.set(1);
		optionalEnum.set(EnumTest.Side.BUY);

		group.reset();

		Assert.assertFalse(group.isPresent());
		Assert.assertFalse(optionalInt.isPresent());
		Assert.assertFalse(optionalEnum.isPresent());
	}

	@Test
	public void testSchemaEvolutionResetsMissingRequiredGroup() {
		ByteBuffer bb = ByteBuffer.allocate(128);
		NewMessage newMessage = new NewMessage();
		newMessage.sequence.set(1);
		newMessage.side.set(EnumTest.Side.BUY);
		newMessage.quantity.set(100);
		newMessage.write(bb);
		bb.flip();
		bb.position(4);

		NewMessage reader = new NewMessage();
		reader.read(bb);
		Assert.assertTrue(reader.side.isPresent());

		bb.clear();
		OldMessage oldMessage = new OldMessage();
		oldMessage.sequence.set(2);
		oldMessage.write(bb);
		bb.flip();
		bb.position(4);
		reader.read(bb);

		Assert.assertFalse(reader.side.isPresent());
		bb.clear();
		reader.write(bb);
	}
}
