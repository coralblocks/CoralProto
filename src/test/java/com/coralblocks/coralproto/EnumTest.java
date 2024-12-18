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

import com.coralblocks.coralproto.enums.CharEnum;
import com.coralblocks.coralproto.enums.IntEnum;
import com.coralblocks.coralproto.enums.ShortEnum;
import com.coralblocks.coralproto.enums.TwoCharEnum;
import com.coralblocks.coralproto.field.CharEnumField;
import com.coralblocks.coralproto.field.IntEnumField;
import com.coralblocks.coralproto.field.ShortEnumField;
import com.coralblocks.coralproto.field.SubtypeField;
import com.coralblocks.coralproto.field.TwoCharEnumField;
import com.coralblocks.coralproto.field.TypeField;
import com.coralblocks.coralproto.util.ByteBufferUtils;
import com.coralblocks.coralproto.util.CharMap;
import com.coralblocks.coralproto.util.CharUtils;
import com.coralblocks.coralproto.util.IntMap;


public class EnumTest {
	
	public static class EnumProtoMessage extends AbstractProto {
		
		private static final String IDL_CODE = """
				
				TYPE = E
				SUBTYPE = T
				
				myCharEnum:     charEnum(Side) 
				myShortEnum:    shortEnum(RejectReason)
				myIntEnum:      intEnum(ReduceRejectReason)
				myTwoCharEnum:  twoCharEnum(CancelReason)
				
		""";
		
		public static void main(String[] args) throws IOException {
			IDL idl = new IDL(IDL_CODE, IDL.INDENT + IDL.INDENT);
			String filePath = "src/test/java/com/coralblocks/coralproto/" + EnumTest.class.getSimpleName() + ".java";
			String sourceCode = idl.getCode();
			IDL.replaceAutoGeneratedCode(filePath, sourceCode);
		}
		
		// Auto-generated code. Do not edit or change anything below here
		
		// BEGIN_AUTO_GENERATED_CODE

        public final static char TYPE = 'E';
        public final static char SUBTYPE = 'T';

        public final TypeField typeField = new TypeField(this, TYPE);
        public final SubtypeField subtypeField = new SubtypeField(this, SUBTYPE);

        public final CharEnumField<Side> myCharEnum = new CharEnumField<Side>(this, Side.ALL);

        public final ShortEnumField<RejectReason> myShortEnum = new ShortEnumField<RejectReason>(this, RejectReason.ALL);

        public final IntEnumField<ReduceRejectReason> myIntEnum = new IntEnumField<ReduceRejectReason>(this, ReduceRejectReason.ALL);

        public final TwoCharEnumField<CancelReason> myTwoCharEnum = new TwoCharEnumField<CancelReason>(this, CancelReason.ALL);


		// END_AUTO_GENERATED_CODE
	}
	
	public static enum Side implements CharEnum { 

		BUY 			('B'), 
		SELL			('S');

		private final char b;
		public final static CharMap<Side> ALL = new CharMap<Side>();
		
		static {
			for(Side s : Side.values()) {
				if (ALL.put(s.getChar(), s) != null) {
					throw new IllegalStateException("Cannot have two sides with the same character: " + s);
				}
			}
		}
		
		private Side(char b) {
			this.b = b;
		}
		
    	@Override
        public final char getChar() {
    	    return b;
        }
	}
	
	public static enum RejectReason implements ShortEnum { 

        MISSING_FIELD       (1),
        BAD_TYPE            (2),
        BAD_TIF             (3),
        BAD_SIDE            (4),
        BAD_SYMBOL          (5),
        
        BAD_PRICE           (100), 
        BAD_SIZE            (101),
        TRADING_HALTED      (102),
        BAD_LOT             (103),
        UNKNOWN_SYMBOL      (104),
        DUPLICATE_ORDER_ID  (105);

        private final short shortValue;
        public final static IntMap<RejectReason> ALL = new IntMap<RejectReason>();
        
        static {
            for(RejectReason rr : RejectReason.values()) {
                if (ALL.put(rr.getShort(), rr) != null) throw new IllegalStateException("Duplicate: " + rr);
            }
        }
        
        private RejectReason(int shortValue) { // int here for convenience so that you don't have to cast to short above
            if (shortValue < Short.MIN_VALUE || shortValue > Short.MAX_VALUE) throw new IllegalArgumentException("Not a short: " + shortValue);
            this.shortValue = (short) shortValue;
        }
        
        @Override
        public final short getShort() {
            return shortValue;
        }
    }
	
    public static enum ReduceRejectReason implements IntEnum { 

        ZERO            (1000), 
        NEGATIVE        (2000),
        INCREASE        (3000),
        SUPERFLUOUS     (4000),
        NOT_FOUND       (5000);

        private final int intValue;
        public final static IntMap<ReduceRejectReason> ALL = new IntMap<ReduceRejectReason>();
        
        static {
            for(ReduceRejectReason rrr : ReduceRejectReason.values()) {
                if (ALL.put(rrr.getInt(), rrr) != null) throw new IllegalStateException("Duplicate: " + rrr);
            }
        }
        
        private ReduceRejectReason(int intValue) {
            this.intValue = intValue;
        }
        
        @Override
        public final int getInt() {
            return intValue;
        }
    }
    
    public static enum CancelReason implements TwoCharEnum { 

        MISSED          ("X1"), 
        USER            ("X2"),
        LIQUIDITY       ("X3"),
        PRICE           ("X4"),
        PURGED          ("X5");

        private final String twoCharString;
        public final static IntMap<CancelReason> ALL = new IntMap<CancelReason>();
        
        static {
            for(CancelReason cr : CancelReason.values()) {
                if (ALL.put(CharUtils.toShort(cr.getString()), cr) != null) throw new IllegalStateException("Duplicate: " + cr);
            }
        }
        
        private CancelReason(String twoCharString) {
            if (twoCharString.length() != 2) throw new IllegalArgumentException("TwoChar value can only have two chars: size=" + twoCharString.length() + " [" + twoCharString + "]");
            this.twoCharString = twoCharString;
        }
        
        @Override
        public final String getString() {
            return twoCharString;
        }
    }
	
	@Test
	public void testAllEnumFields() {
		
		EnumProtoMessage proto = new EnumProtoMessage();
		
		Assert.assertEquals(EnumProtoMessage.TYPE, proto.getType());
		Assert.assertEquals(EnumProtoMessage.SUBTYPE, proto.getSubtype());

		Assert.assertEquals(false, proto.myCharEnum.isOptional());
		Assert.assertEquals(true, proto.myCharEnum.isPresent());
		
		Assert.assertNull(proto.myCharEnum.get());
		
		proto.myCharEnum.set(Side.BUY);
		Assert.assertEquals(Side.BUY, proto.myCharEnum.get());
		
		proto.myCharEnum.set(Side.SELL);
		Assert.assertEquals(Side.SELL, proto.myCharEnum.get());
		
		///
		
		Assert.assertEquals(false, proto.myShortEnum.isOptional());
		Assert.assertEquals(true, proto.myShortEnum.isPresent());
		
		Assert.assertNull(proto.myShortEnum.get());
		
		proto.myShortEnum.set(RejectReason.BAD_LOT);
		Assert.assertEquals(RejectReason.BAD_LOT, proto.myShortEnum.get());
		
		proto.myShortEnum.set(RejectReason.BAD_SIZE);
		Assert.assertEquals(RejectReason.BAD_SIZE, proto.myShortEnum.get());
		
		///
		
		Assert.assertEquals(false, proto.myIntEnum.isOptional());
		Assert.assertEquals(true, proto.myIntEnum.isPresent());
		
		Assert.assertNull(proto.myIntEnum.get());
		
		proto.myIntEnum.set(ReduceRejectReason.NEGATIVE);
		Assert.assertEquals(ReduceRejectReason.NEGATIVE, proto.myIntEnum.get());
		
		proto.myIntEnum.set(ReduceRejectReason.NOT_FOUND);
		Assert.assertEquals(ReduceRejectReason.NOT_FOUND, proto.myIntEnum.get());
		
		/// 
		
		Assert.assertEquals(false, proto.myTwoCharEnum.isOptional());
		Assert.assertEquals(true, proto.myTwoCharEnum.isPresent());
		
		Assert.assertNull(proto.myTwoCharEnum.get());
		
		proto.myTwoCharEnum.set(CancelReason.MISSED);
		Assert.assertEquals(CancelReason.MISSED, proto.myTwoCharEnum.get());
		
		proto.myTwoCharEnum.set(CancelReason.LIQUIDITY);
		Assert.assertEquals(CancelReason.LIQUIDITY, proto.myTwoCharEnum.get());
	}
	
	@Test
	public void testSendAndReceive() {
		
		ByteBuffer bb = ByteBuffer.allocate(1024);
		
		EnumProtoMessage proto = new EnumProtoMessage();
		
		proto.myCharEnum.set(Side.SELL);
		proto.myShortEnum.set(RejectReason.BAD_PRICE);
		proto.myIntEnum.set(ReduceRejectReason.SUPERFLUOUS);
		proto.myTwoCharEnum.set(CancelReason.MISSED);
		
		proto.write(bb);
		
		bb.flip();
		
		Assert.assertEquals(EnumProtoMessage.TYPE, bb.get());
		Assert.assertEquals(EnumProtoMessage.SUBTYPE, bb.get());
		
		EnumProtoMessage received = new EnumProtoMessage();
		
		received.read(bb);
		
		Assert.assertEquals(Side.SELL, received.myCharEnum.get());
		Assert.assertEquals(RejectReason.BAD_PRICE, received.myShortEnum.get());
		Assert.assertEquals(ReduceRejectReason.SUPERFLUOUS, received.myIntEnum.get());
		Assert.assertEquals(CancelReason.MISSED, received.myTwoCharEnum.get());
		
		bb.clear();
		
		received.writeAscii(true, bb);
		
		bb.flip();
		
		Assert.assertEquals("ET|SELL|BAD_PRICE|SUPERFLUOUS|MISSED", ByteBufferUtils.parseString(bb));
		
		bb.clear();
		
		received.writeAscii(false, bb);
		
		bb.flip();
		
		Assert.assertEquals("ET (EnumProtoMessage)|SELL|BAD_PRICE|SUPERFLUOUS|MISSED", ByteBufferUtils.parseString(bb));
	}
}
