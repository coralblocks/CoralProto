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
import com.coralblocks.coralproto.example.ProtoMessage1A;
import com.coralblocks.coralproto.example.ProtoMessage2;
import com.coralblocks.coralproto.example.ProtoMessage2A;

public class SchemaEvolvingTest {
	
	@Test
	public void testOldToNew1() {
		
		ByteBuffer bb = ByteBuffer.allocate(1024);
		
		ProtoMessage1 p1 = new ProtoMessage1();
		
		p1.symbolId.set(2L);
		p1.symbolDesc.set("IBM");
		
		p1.write(bb);
		
		bb.flip();
		
		Assert.assertEquals(ProtoMessage1A.TYPE, bb.get());
		Assert.assertEquals(ProtoMessage1A.SUBTYPE, bb.get());
		
		// schema has evolved, it now has an extra field...
		
		ProtoMessage1A p1A = new ProtoMessage1A();
		
		p1A.read(bb);
		
		Assert.assertEquals(2L, p1A.symbolId.get());
		Assert.assertEquals("IBM", p1A.symbolDesc.get().toString());
		Assert.assertEquals(0, p1A.extraField.get()); // default value
	}
	
	@Test
	public void testNewToOld1() {
		
		ByteBuffer bb = ByteBuffer.allocate(1024);
		
		ProtoMessage1A p1A = new ProtoMessage1A();
		
		p1A.symbolId.set(2L);
		p1A.symbolDesc.set("IBM");
		p1A.extraField.set(111);
		
		p1A.write(bb);
		
		bb.flip();
		
		Assert.assertEquals(ProtoMessage1.TYPE, bb.get());
		Assert.assertEquals(ProtoMessage1.SUBTYPE, bb.get());
		
		ProtoMessage1 p1 = new ProtoMessage1();
		
		p1.read(bb);
		
		Assert.assertEquals(2L, p1.symbolId.get());
		Assert.assertEquals("IBM", p1.symbolDesc.get().toString());
	}
	
	@Test
	public void testOldToNew2() {
		
		ByteBuffer bb = ByteBuffer.allocate(1024);
		
		ProtoMessage2 p2 = new ProtoMessage2();
		
		p2.orderId.set(212L);
		p2.symbolDesc.markAsNotPresent();
		p2.isMine.set(true);
		
		p2.write(bb);
		
		bb.flip();
		
		Assert.assertEquals(ProtoMessage2A.TYPE, bb.get());
		Assert.assertEquals(ProtoMessage2A.SUBTYPE, bb.get());
		
		// schema has evolved, it now has an extra field...
		
		ProtoMessage2A p2A = new ProtoMessage2A();
		
		p2A.read(bb);
		
		Assert.assertEquals(212L, p2A.orderId.get());
		Assert.assertEquals(false, p2A.symbolDesc.isPresent());
		Assert.assertEquals(true, p2A.isMine.get());
		Assert.assertEquals(false, p2A.extraField.isPresent());
	}
	
	@Test
	public void testNewToOld2() {
		
		ByteBuffer bb = ByteBuffer.allocate(1024);
		
		ProtoMessage2A p2A = new ProtoMessage2A();
		
		p2A.orderId.set(212L);
		p2A.symbolDesc.markAsNotPresent();
		p2A.isMine.set(true);
		p2A.extraField.set(111);
		
		p2A.write(bb);
		
		bb.flip();
		
		Assert.assertEquals(ProtoMessage2.TYPE, bb.get());
		Assert.assertEquals(ProtoMessage2.SUBTYPE, bb.get());
		
		ProtoMessage2 p2 = new ProtoMessage2();
		
		p2.read(bb);
		
		Assert.assertEquals(212L, p2.orderId.get());
		Assert.assertEquals(false, p2.symbolDesc.isPresent());
		Assert.assertEquals(true, p2.isMine.get());
	}
	
}