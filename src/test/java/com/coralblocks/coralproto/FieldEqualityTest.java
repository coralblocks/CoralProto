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
import com.coralblocks.coralproto.field.TwoCharEnumField;
import com.coralblocks.coralproto.field.VarBytesField;
import com.coralblocks.coralproto.field.VarCharsField;

public class FieldEqualityTest {

	private static void assertPresenceAwareEquality(ProtoField absentWithStaleValue, ProtoField freshAbsent,
			ProtoField presentWithSameValue) {
		Assert.assertEquals(absentWithStaleValue, freshAbsent);
		Assert.assertNotEquals(absentWithStaleValue, presentWithSameValue);
	}

	@Test
	public void testPrimitiveFieldEqualityIncludesPresence() {
		BooleanField boolean1 = new BooleanField(true);
		BooleanField boolean2 = new BooleanField(true);
		BooleanField boolean3 = new BooleanField(true);
		boolean1.set(true);
		boolean1.markAsNotPresent();
		boolean3.set(true);
		assertPresenceAwareEquality(boolean1, boolean2, boolean3);

		ByteField byte1 = new ByteField(true);
		ByteField byte2 = new ByteField(true);
		ByteField byte3 = new ByteField(true);
		byte1.set(1);
		byte1.markAsNotPresent();
		byte3.set(1);
		assertPresenceAwareEquality(byte1, byte2, byte3);

		CharField char1 = new CharField(true);
		CharField char2 = new CharField(true);
		CharField char3 = new CharField(true);
		char1.set('A');
		char1.markAsNotPresent();
		char3.set('A');
		assertPresenceAwareEquality(char1, char2, char3);

		IntField int1 = new IntField(true);
		IntField int2 = new IntField(true);
		IntField int3 = new IntField(true);
		int1.set(1);
		int1.markAsNotPresent();
		int3.set(1);
		assertPresenceAwareEquality(int1, int2, int3);

		LongField long1 = new LongField(true);
		LongField long2 = new LongField(true);
		LongField long3 = new LongField(true);
		long1.set(1);
		long1.markAsNotPresent();
		long3.set(1);
		assertPresenceAwareEquality(long1, long2, long3);

		ShortField short1 = new ShortField(true);
		ShortField short2 = new ShortField(true);
		ShortField short3 = new ShortField(true);
		short1.set(1);
		short1.markAsNotPresent();
		short3.set(1);
		assertPresenceAwareEquality(short1, short2, short3);

		FloatField float1 = new FloatField(true);
		FloatField float2 = new FloatField(true);
		FloatField float3 = new FloatField(true);
		float1.set(1.5f);
		float1.markAsNotPresent();
		float3.set(1.5f);
		assertPresenceAwareEquality(float1, float2, float3);

		DoubleField double1 = new DoubleField(true);
		DoubleField double2 = new DoubleField(true);
		DoubleField double3 = new DoubleField(true);
		double1.set(1.5);
		double1.markAsNotPresent();
		double3.set(1.5);
		assertPresenceAwareEquality(double1, double2, double3);
	}

	@Test
	public void testArrayFieldEqualityIncludesPresence() {
		BytesField bytes1 = new BytesField(4, true);
		BytesField bytes2 = new BytesField(4, true);
		BytesField bytes3 = new BytesField(4, true);
		bytes1.set(new byte[] { 1, 2, 3, 4 });
		bytes1.markAsNotPresent();
		bytes3.set(new byte[] { 1, 2, 3, 4 });
		assertPresenceAwareEquality(bytes1, bytes2, bytes3);

		CharsField chars1 = new CharsField(4, true);
		CharsField chars2 = new CharsField(4, true);
		CharsField chars3 = new CharsField(4, true);
		chars1.set("ABCD");
		chars1.markAsNotPresent();
		chars3.set("ABCD");
		assertPresenceAwareEquality(chars1, chars2, chars3);

		VarBytesField varBytes1 = new VarBytesField(4, true);
		VarBytesField varBytes2 = new VarBytesField(4, true);
		VarBytesField varBytes3 = new VarBytesField(4, true);
		varBytes1.set(new byte[] { 1, 2, 3, 4 });
		varBytes1.markAsNotPresent();
		varBytes3.set(new byte[] { 1, 2, 3, 4 });
		assertPresenceAwareEquality(varBytes1, varBytes2, varBytes3);

		VarCharsField varChars1 = new VarCharsField(4, true);
		VarCharsField varChars2 = new VarCharsField(4, true);
		VarCharsField varChars3 = new VarCharsField(4, true);
		varChars1.set("ABCD");
		varChars1.markAsNotPresent();
		varChars3.set("ABCD");
		assertPresenceAwareEquality(varChars1, varChars2, varChars3);
	}

	@Test
	public void testEnumFieldEqualityIncludesPresence() {
		CharEnumField<EnumTest.Side> charEnum1 = new CharEnumField<EnumTest.Side>(EnumTest.Side.ALL, true);
		CharEnumField<EnumTest.Side> charEnum2 = new CharEnumField<EnumTest.Side>(EnumTest.Side.ALL, true);
		CharEnumField<EnumTest.Side> charEnum3 = new CharEnumField<EnumTest.Side>(EnumTest.Side.ALL, true);
		charEnum1.set(EnumTest.Side.BUY);
		charEnum1.markAsNotPresent();
		charEnum3.set(EnumTest.Side.BUY);
		assertPresenceAwareEquality(charEnum1, charEnum2, charEnum3);

		ShortEnumField<EnumTest.RejectReason> shortEnum1 = new ShortEnumField<EnumTest.RejectReason>(EnumTest.RejectReason.ALL, true);
		ShortEnumField<EnumTest.RejectReason> shortEnum2 = new ShortEnumField<EnumTest.RejectReason>(EnumTest.RejectReason.ALL, true);
		ShortEnumField<EnumTest.RejectReason> shortEnum3 = new ShortEnumField<EnumTest.RejectReason>(EnumTest.RejectReason.ALL, true);
		shortEnum1.set(EnumTest.RejectReason.BAD_TYPE);
		shortEnum1.markAsNotPresent();
		shortEnum3.set(EnumTest.RejectReason.BAD_TYPE);
		assertPresenceAwareEquality(shortEnum1, shortEnum2, shortEnum3);

		IntEnumField<EnumTest.ReduceRejectReason> intEnum1 = new IntEnumField<EnumTest.ReduceRejectReason>(EnumTest.ReduceRejectReason.ALL, true);
		IntEnumField<EnumTest.ReduceRejectReason> intEnum2 = new IntEnumField<EnumTest.ReduceRejectReason>(EnumTest.ReduceRejectReason.ALL, true);
		IntEnumField<EnumTest.ReduceRejectReason> intEnum3 = new IntEnumField<EnumTest.ReduceRejectReason>(EnumTest.ReduceRejectReason.ALL, true);
		intEnum1.set(EnumTest.ReduceRejectReason.ZERO);
		intEnum1.markAsNotPresent();
		intEnum3.set(EnumTest.ReduceRejectReason.ZERO);
		assertPresenceAwareEquality(intEnum1, intEnum2, intEnum3);

		TwoCharEnumField<EnumTest.CancelReason> twoCharEnum1 = new TwoCharEnumField<EnumTest.CancelReason>(EnumTest.CancelReason.ALL, true);
		TwoCharEnumField<EnumTest.CancelReason> twoCharEnum2 = new TwoCharEnumField<EnumTest.CancelReason>(EnumTest.CancelReason.ALL, true);
		TwoCharEnumField<EnumTest.CancelReason> twoCharEnum3 = new TwoCharEnumField<EnumTest.CancelReason>(EnumTest.CancelReason.ALL, true);
		twoCharEnum1.set(EnumTest.CancelReason.MISSED);
		twoCharEnum1.markAsNotPresent();
		twoCharEnum3.set(EnumTest.CancelReason.MISSED);
		assertPresenceAwareEquality(twoCharEnum1, twoCharEnum2, twoCharEnum3);
	}

	@Test
	public void testGroupFieldEqualityIncludesPresence() {
		GroupField group1 = new GroupField(true, new IntField(), new LongField());
		GroupField group2 = new GroupField(true, new IntField(), new LongField());
		GroupField group3 = new GroupField(true, new IntField(), new LongField());
		((IntField) group1.getAndMarkAsPresent(0)).set(1);
		((LongField) group1.getAndMarkAsPresent(1)).set(2);
		group1.markAsNotPresent();
		((IntField) group3.getAndMarkAsPresent(0)).set(1);
		((LongField) group3.getAndMarkAsPresent(1)).set(2);
		assertPresenceAwareEquality(group1, group2, group3);
	}

	@Test
	public void testProtoEqualityIncludesPresence() {
		OptionalFieldsTest.OptionalFieldsProtoMessage proto1 = new OptionalFieldsTest.OptionalFieldsProtoMessage();
		OptionalFieldsTest.OptionalFieldsProtoMessage proto2 = new OptionalFieldsTest.OptionalFieldsProtoMessage();
		OptionalFieldsTest.OptionalFieldsProtoMessage proto3 = new OptionalFieldsTest.OptionalFieldsProtoMessage();
		proto1.myLong.set(5);
		proto1.myLong.markAsNotPresent();
		proto3.myLong.set(5);

		Assert.assertEquals(proto1, proto2);
		Assert.assertNotEquals(proto1, proto3);
	}
}
