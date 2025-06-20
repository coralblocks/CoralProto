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
package com.coralblocks.coralproto;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.junit.Assert;
import org.junit.Test;

import com.coralblocks.coralproto.field.DoubleField;
import com.coralblocks.coralproto.field.FloatField;
import com.coralblocks.coralproto.field.SubtypeField;
import com.coralblocks.coralproto.field.TypeField;
import com.coralblocks.coralproto.util.ByteBufferUtils;


public class FloatDoubleTest {
	
	public static class FloatDoubleProtoMessage extends AbstractProto {
		
		private static final String IDL_CODE = """
				
				TYPE = F
				SUBTYPE = D
				
				myFloat1: float 
				myFloat2: float(2)
				myFloat3: float(3)!
				myDouble1: double 
				myDouble2: double(6)
				myDouble3: double(7)!				
				
		""";
		
		public static void main(String[] args) throws IOException {
			IDL idl = new IDL(IDL_CODE, IDL.INDENT + IDL.INDENT);
			String filePath = "src/test/java/com/coralblocks/coralproto/" + FloatDoubleTest.class.getSimpleName() + ".java";
			String sourceCode = idl.getCode();
			IDL.replaceAutoGeneratedCode(filePath, sourceCode);
		}
		
		// Auto-generated code. Do not edit or change anything below here
		
		// BEGIN_AUTO_GENERATED_CODE

        public static final char TYPE = 'F';
        public static final char SUBTYPE = 'D';

        public final TypeField typeField = new TypeField(this, TYPE);
        public final SubtypeField subtypeField = new SubtypeField(this, SUBTYPE);

        public final FloatField myFloat1 = new FloatField(this);

        public final FloatField myFloat2 = new FloatField(this, 2);

        public final FloatField myFloat3 = new FloatField(this, 3, true);

        public final DoubleField myDouble1 = new DoubleField(this);

        public final DoubleField myDouble2 = new DoubleField(this, 6);

        public final DoubleField myDouble3 = new DoubleField(this, 7, true);


		// END_AUTO_GENERATED_CODE
	}
	
	@Test
	public void testAllFields() {
		
		FloatDoubleProtoMessage proto = new FloatDoubleProtoMessage();
		
		Assert.assertEquals(FloatDoubleProtoMessage.TYPE, proto.getType());
		Assert.assertEquals(FloatDoubleProtoMessage.SUBTYPE, proto.getSubtype());
		Assert.assertEquals(0, proto.getVersion());

		Assert.assertEquals(false, proto.myFloat1.isOptional());
		Assert.assertEquals(false, proto.myFloat2.isOptional());
		Assert.assertEquals(true, proto.myFloat3.isOptional());
		Assert.assertEquals(false, proto.myDouble1.isOptional());
		Assert.assertEquals(false, proto.myDouble2.isOptional());
		Assert.assertEquals(true, proto.myDouble3.isOptional());
		
		Assert.assertTrue(0.0f == proto.myFloat1.get());
		Assert.assertTrue(0.0f == proto.myFloat2.get());
		Assert.assertEquals(false, proto.myFloat3.isPresent());
		Assert.assertTrue(0.0 == proto.myDouble1.get());
		Assert.assertTrue(0.0 == proto.myDouble2.get());
		Assert.assertEquals(false, proto.myDouble3.isPresent());
		
		proto.myFloat1.set(1.1234f);
		proto.myFloat2.set(1.12f);
		proto.myFloat3.set(1.1f);
		proto.myDouble1.set(1.12345678);
		proto.myDouble2.set(1.123456);
		proto.myDouble3.set(1.12345);
		
		Assert.assertTrue(1.1234f == proto.myFloat1.get());
		Assert.assertTrue(1.12f == proto.myFloat2.get());
		Assert.assertTrue(1.1f == proto.myFloat3.get());
		Assert.assertTrue(1.12345678 == proto.myDouble1.get());
		Assert.assertTrue(1.123456 == proto.myDouble2.get());
		Assert.assertTrue(1.12345 == proto.myDouble3.get());
	}
	
	@Test
	public void testSendReceive() {
		
		ByteBuffer bb = ByteBuffer.allocate(1024);
		
		FloatDoubleProtoMessage proto = new FloatDoubleProtoMessage();
		
		proto.myFloat1.set(1.1234f);
		proto.myFloat2.set(1.12f);
		proto.myFloat3.markAsNotPresent();
		proto.myDouble1.set(1.12345678);
		proto.myDouble2.set(1.123456);
		proto.myDouble3.set(1.12345);
		
		proto.write(bb);
		
		bb.flip();
		
		Assert.assertEquals(FloatDoubleProtoMessage.TYPE, bb.get());
		Assert.assertEquals(FloatDoubleProtoMessage.SUBTYPE, bb.get());
		Assert.assertEquals(0, bb.getShort());
		
		FloatDoubleProtoMessage received = new FloatDoubleProtoMessage();
		
		received.read(bb);
		
		Assert.assertTrue(1.1234f == received.myFloat1.get());
		Assert.assertTrue(1.12f == received.myFloat2.get());
		Assert.assertTrue(false == received.myFloat3.isPresent());
		Assert.assertTrue(1.12345678 == received.myDouble1.get());
		Assert.assertTrue(1.123456 == received.myDouble2.get());
		Assert.assertTrue(1.12345 == received.myDouble3.get());
		
		bb.clear();
		
		received.writeAscii(true, bb);
		
		bb.flip();
		
		Assert.assertEquals("FD|1.1234|1.12|BLANK|1.12345678|1.123456|1.12345", ByteBufferUtils.parseString(bb));
		
		bb.clear();
		
		received.writeAscii(false, bb);
		
		bb.flip();
		
		Assert.assertEquals("FD (FloatDoubleProtoMessage)|1.1234|1.12|BLANK|1.12345678|1.123456|1.12345", ByteBufferUtils.parseString(bb));
	}
}
