package com.coralblocks.coralproto;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import com.coralblocks.coralproto.field.BooleanField;
import com.coralblocks.coralproto.field.ByteField;
import com.coralblocks.coralproto.field.CharField;
import com.coralblocks.coralproto.field.IntField;
import com.coralblocks.coralproto.field.LongField;
import com.coralblocks.coralproto.field.ShortField;
import com.coralblocks.coralproto.field.SubtypeField;
import com.coralblocks.coralproto.field.TypeField;


public class OptionalFieldsTest {
	
	public static class OptionalFieldsProtoMessage extends AbstractProto {
		
		private static final String IDL_CODE = """
				
				TYPE = O
				SUBTYPE = F
				
				myBoolean: boolean!
				myByte: byte!
				myChar: char!
				myInt: int!
				myLong: long!
				myShort: short!
				
		""";
		
		public static void main(String[] args) throws IOException {
			IDL idl = new IDL(IDL_CODE, IDL.INDENT + IDL.INDENT);
			String filePath = "src/test/java/com/coralblocks/coralproto/OptionalFieldsTest.java";
			String sourceCode = idl.getCode();
			IDL.replaceAutoGeneratedCode(filePath, sourceCode);
		}
		
		// Auto-generated code. Do not edit or change anything below here
		
		// BEGIN_AUTO_GENERATED_CODE

        public final static char TYPE = 'O';
        public final static char SUBTYPE = 'F';

        public final TypeField typeField = new TypeField(this, TYPE);
        public final SubtypeField subtypeField = new SubtypeField(this, SUBTYPE);

        public final BooleanField myBoolean = new BooleanField(this, true);

        public final ByteField myByte = new ByteField(this, true);

        public final CharField myChar = new CharField(this, true);

        public final IntField myInt = new IntField(this, true);

        public final LongField myLong = new LongField(this, true);

        public final ShortField myShort = new ShortField(this, true);


		// END_AUTO_GENERATED_CODE
	}
	
	@Test
	public void testOptionalFields() {
		
		OptionalFieldsProtoMessage proto = new OptionalFieldsProtoMessage();
		
		Assert.assertEquals(OptionalFieldsProtoMessage.TYPE, proto.getType());
		Assert.assertEquals(OptionalFieldsProtoMessage.SUBTYPE, proto.getSubtype());

		Assert.assertEquals(true, proto.myBoolean.isOptional());
		Assert.assertEquals(false, proto.myBoolean.isPresent());
		proto.myBoolean.set(true);
		Assert.assertEquals(true, proto.myBoolean.isPresent());
		Assert.assertEquals(true, proto.myBoolean.get());
		
		Assert.assertEquals(true, proto.myByte.isOptional());
		Assert.assertEquals(false, proto.myByte.isPresent());
		proto.myByte.set(6);
		Assert.assertEquals(true, proto.myByte.isPresent());
		Assert.assertEquals(6, proto.myByte.get());
		
		Assert.assertEquals(true, proto.myChar.isOptional());
		Assert.assertEquals(false, proto.myChar.isPresent());
		proto.myChar.set('X');
		Assert.assertEquals(true, proto.myChar.isPresent());
		Assert.assertEquals('X', proto.myChar.get());
		
		Assert.assertEquals(true, proto.myInt.isOptional());
		Assert.assertEquals(false, proto.myInt.isPresent());
		proto.myInt.set(61);
		Assert.assertEquals(true, proto.myInt.isPresent());
		Assert.assertEquals(61, proto.myInt.get());
		
		Assert.assertEquals(true, proto.myLong.isOptional());
		Assert.assertEquals(false, proto.myLong.isPresent());
		proto.myLong.set(6123424243345L);
		Assert.assertEquals(true, proto.myLong.isPresent());
		Assert.assertEquals(6123424243345L, proto.myLong.get());
		
		Assert.assertEquals(true, proto.myShort.isOptional());
		Assert.assertEquals(false, proto.myShort.isPresent());
		proto.myShort.set(614);
		Assert.assertEquals(true, proto.myShort.isPresent());
		Assert.assertEquals(614, proto.myShort.get());
		
	}
}