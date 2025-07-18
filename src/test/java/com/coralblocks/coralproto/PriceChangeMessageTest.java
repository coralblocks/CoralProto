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

import com.coralblocks.coralproto.example.PriceChangeMessage;

public class PriceChangeMessageTest {
	
	@Test
	public void testMessage() {
		
		PriceChangeMessage proto = new PriceChangeMessage();
		
		proto.symbolId.set(1111L);
		proto.symbolDesc.set("IBM");
		proto.mqReqId.markAsNotPresent();
		
			proto.orders.clear();
		
			proto.orders.nextElement();
			proto.orders.side.set(true);
			proto.orders.levelId.set(11111111L);
			proto.orders.priceLevel.set(200.15);
			proto.orders.qty.set(1000);
		
				proto.orders.legs.clear();
			
				proto.orders.legs.nextElement();
				proto.orders.legs.legId.set(1);
				proto.orders.legs.legDesc.markAsNotPresent();
				
				proto.orders.legs.nextElement();
				proto.orders.legs.legId.set(2);
				proto.orders.legs.legDesc.set("myLeg2  ");
		
			proto.orders.orderId.set(1234L);
		
			proto.orders.nextElement();
			proto.orders.side.set(false);
			proto.orders.levelId.set(22222222L);
			proto.orders.priceLevel.set(200.75);
			proto.orders.qty.set(800);
			
				proto.orders.legs.clear();
			
				proto.orders.legs.nextElement();
				proto.orders.legs.legId.set(1);
				proto.orders.legs.legDesc.set("myLeg1  ");
				
				proto.orders.legs.nextElement();
				proto.orders.legs.legId.set(2);
				proto.orders.legs.legDesc.markAsNotPresent();
			
			proto.orders.orderId.set(5678L);
		
		proto.lastTradeQty.set(100);
		proto.lastTradePrice.set(200.55);
		
		ByteBuffer bb = ByteBuffer.allocate(1024);
		
		proto.write(bb);
		bb.flip();
		
		PriceChangeMessage oldProto = proto; // save for testing the equals method below
		
		proto = new PriceChangeMessage();
		
		Assert.assertEquals(PriceChangeMessage.TYPE, bb.get());
		Assert.assertEquals(PriceChangeMessage.SUBTYPE, bb.get());
		Assert.assertEquals(0, bb.getShort());
		
		proto.read(bb);
		
		Assert.assertTrue(oldProto.equals(proto));
		oldProto.orders.clear();
		Assert.assertFalse(oldProto.equals(proto));
		
		Assert.assertEquals(PriceChangeMessage.TYPE, proto.getType());
		Assert.assertEquals(PriceChangeMessage.SUBTYPE, proto.getSubtype());
		
		Assert.assertEquals(1111L, proto.symbolId.get());
		Assert.assertEquals("IBM", proto.symbolDesc.get().toString());
		Assert.assertEquals(false, proto.mqReqId.isPresent());
		
		Assert.assertEquals(2, proto.orders.getNumberOfElements());
		
		proto.orders.beginIteration();
		
		Assert.assertEquals(true, proto.orders.iterHasNext());
		proto.orders.iterNext();
		
		Assert.assertEquals(true, proto.orders.side.get());
		Assert.assertEquals(11111111L, proto.orders.levelId.get());
		Assert.assertTrue(200.15 == proto.orders.priceLevel.get());
		Assert.assertEquals(1000, proto.orders.qty.get());
		
		Assert.assertEquals(2, proto.orders.legs.getNumberOfElements());
		
		proto.orders.legs.beginIteration();
		
		Assert.assertEquals(true, proto.orders.legs.iterHasNext());
		proto.orders.legs.iterNext();
		
		Assert.assertEquals(1, proto.orders.legs.legId.get());
		Assert.assertEquals(false, proto.orders.legs.legDesc.isPresent());
		
		Assert.assertEquals(true, proto.orders.legs.iterHasNext());
		proto.orders.legs.iterNext();
		
		Assert.assertEquals(2, proto.orders.legs.legId.get());
		Assert.assertEquals("myLeg2  ", proto.orders.legs.legDesc.get().toString());
		
		Assert.assertEquals(false, proto.orders.legs.iterHasNext());
		
		Assert.assertEquals(true, proto.orders.iterHasNext());
		proto.orders.iterNext();
		
		Assert.assertEquals(false, proto.orders.side.get());
		Assert.assertEquals(22222222L, proto.orders.levelId.get());
		Assert.assertTrue(200.75 == proto.orders.priceLevel.get());
		Assert.assertEquals(800, proto.orders.qty.get());
		
		Assert.assertEquals(2, proto.orders.legs.getNumberOfElements());
		
		proto.orders.legs.beginIteration();
		
		Assert.assertEquals(true, proto.orders.legs.iterHasNext());
		proto.orders.legs.iterNext();
		
		Assert.assertEquals(1, proto.orders.legs.legId.get());
		Assert.assertEquals("myLeg1  ", proto.orders.legs.legDesc.get().toString());
		
		Assert.assertEquals(true, proto.orders.legs.iterHasNext());
		proto.orders.legs.iterNext();
		
		Assert.assertEquals(2, proto.orders.legs.legId.get());
		Assert.assertEquals(false, proto.orders.legs.legDesc.isPresent());
		
		Assert.assertEquals(false, proto.orders.legs.iterHasNext());
		
		Assert.assertEquals(false, proto.orders.iterHasNext());
		
		Assert.assertEquals(100, proto.lastTradeQty.get());
		Assert.assertTrue(200.55 == proto.lastTradePrice.get());
	}
}