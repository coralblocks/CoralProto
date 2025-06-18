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

import java.nio.ByteBuffer;

import org.junit.Assert;
import org.junit.Test;

import com.coralblocks.coralproto.example.ProtoMessage1;
import com.coralblocks.coralproto.example.ProtoMessage2;

public class ProtoParserTest {
	
	public static class ProtoParser extends com.coralblocks.coralproto.ProtoParser {

		@Override
		protected Proto[] defineProtoMessages() {
			return new Proto[] {
					new ProtoMessage1(),
					new ProtoMessage2()
			};
		}
	}
	
	@Test
	public void testProtoParser() {
		
		ByteBuffer bb = ByteBuffer.allocate(1024);
		
		ProtoParser protoParser = new ProtoParser();
		
		ProtoMessage1 p1 = new ProtoMessage1();
		ProtoMessage2 p2 = new ProtoMessage2();
		
		p1.symbolId.set(2L);
		p1.symbolDesc.set("IBM");
		
		p1.write(bb);
		
		bb.flip();
		
		Proto proto = protoParser.parse(bb);
		
		Assert.assertNotNull(proto);
		Assert.assertEquals(ProtoMessage1.TYPE, proto.getType());
		Assert.assertEquals(ProtoMessage1.SUBTYPE, proto.getSubtype());
		Assert.assertEquals(0, proto.getVersion());
		Assert.assertTrue(proto instanceof ProtoMessage1);
		
		ProtoMessage1 pParsed1 = (ProtoMessage1) proto;
		
		Assert.assertEquals(2L, pParsed1.symbolId.get());
		Assert.assertEquals("IBM", pParsed1.symbolDesc.get().toString());
		
		p2.orderId.set(33L);
		p2.symbolDesc.markAsNotPresent();
		p2.isMine.set(true);
		
		bb.clear();
		
		p2.write(bb);
		
		bb.flip();
		
		proto = protoParser.parse(bb);
		
		Assert.assertNotNull(proto);
		Assert.assertEquals(ProtoMessage2.TYPE, proto.getType());
		Assert.assertEquals(ProtoMessage2.SUBTYPE, proto.getSubtype());
		Assert.assertEquals(0, proto.getVersion());
		Assert.assertTrue(proto instanceof ProtoMessage2);
		
		ProtoMessage2 pParsed2 = (ProtoMessage2) proto;
		
		Assert.assertEquals(33L, pParsed2.orderId.get());
		Assert.assertEquals(false, pParsed2.symbolDesc.isPresent());
		Assert.assertEquals(true, pParsed2.isMine.get());
	}
	
}