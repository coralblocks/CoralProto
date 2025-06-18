package com.coralblocks.coralproto.versioning;

import java.nio.ByteBuffer;

import org.junit.Assert;
import org.junit.Test;

import com.coralblocks.coralproto.Proto;

public class VersioningTest {
	
	public static class ProtoParser extends com.coralblocks.coralproto.ProtoParser {

		@Override
		protected Proto[] defineProtoMessages() {
			return new Proto[] {
					new NewOrderMessage(),
					new NewOrderMessage_1(),
					new NewOrderMessage_2()
			};
		}
	}
	
	@Test
	public void testCharsField() {
		
		ProtoParser protoParser = new ProtoParser();
		
		NewOrderMessage nom = new NewOrderMessage();

		ByteBuffer bb = ByteBuffer.allocate(1024);
		
		nom.symbol.set("IBM");
		nom.side.set(true);
		nom.size.set(200);
		nom.price.set(123.45);
		
		bb.clear();
		nom.write(bb);
		
		bb.flip();
		
		Proto proto = protoParser.parse(bb);
		
		Assert.assertTrue(proto instanceof NewOrderMessage);
		
		Assert.assertEquals(NewOrderMessage.TYPE, proto.getType());
		Assert.assertEquals(NewOrderMessage.SUBTYPE, proto.getSubtype());
		Assert.assertEquals(0, proto.getVersion());
		
		NewOrderMessage nomCheck = (NewOrderMessage) proto;
		
		Assert.assertEquals(nom.symbol.get(), nomCheck.symbol.get());
		Assert.assertEquals(nom.side.get(), nomCheck.side.get());
		Assert.assertEquals(nom.size.get(), nomCheck.size.get());
		Assert.assertTrue(nom.price.get() == nomCheck.price.get());
		
		NewOrderMessage_1 nom_1 = new NewOrderMessage_1();
		
		nom_1.clientId.set(123);
		nom_1.symbol.set("AAPL");
		nom_1.symbolId.set(2);
		nom_1.side.set(false);
		nom_1.size.set(300);
		nom_1.price.set(223.45);
		
		bb.clear();
		nom_1.write(bb);
		
		bb.flip();
		
		proto = protoParser.parse(bb);
		
		Assert.assertTrue(proto instanceof NewOrderMessage_1);
		
		Assert.assertEquals(NewOrderMessage_1.TYPE, proto.getType());
		Assert.assertEquals(NewOrderMessage_1.SUBTYPE, proto.getSubtype());
		Assert.assertEquals(NewOrderMessage_1.VERSION, proto.getVersion());
		
		NewOrderMessage_1 nom_1Check = (NewOrderMessage_1) proto;
		
		Assert.assertEquals(nom_1.clientId.get(), nom_1Check.clientId.get());
		Assert.assertEquals(nom_1.symbol.get(), nom_1Check.symbol.get());
		Assert.assertEquals(nom_1.symbolId.get(), nom_1Check.symbolId.get());
		Assert.assertEquals(nom_1.side.get(), nom_1Check.side.get());
		Assert.assertEquals(nom_1.size.get(), nom_1Check.size.get());
		Assert.assertTrue(nom_1.price.get() == nom_1Check.price.get());
		
		NewOrderMessage_2 nom_2 = new NewOrderMessage_2();
		
		nom_2.clientId.set(123);
		nom_2.symbol.set("AAPL");
		nom_2.symbolId.set(2);
		nom_2.side.set(false);
		nom_2.size.set(300);
		nom_2.price.set(22345);
		
		bb.clear();
		nom_2.write(bb);
		
		bb.flip();
		
		proto = protoParser.parse(bb);
		
		Assert.assertTrue(proto instanceof NewOrderMessage_2);
		
		Assert.assertEquals(NewOrderMessage_2.TYPE, proto.getType());
		Assert.assertEquals(NewOrderMessage_2.SUBTYPE, proto.getSubtype());
		Assert.assertEquals(NewOrderMessage_2.VERSION, proto.getVersion());
		
		NewOrderMessage_2 nom_2Check = (NewOrderMessage_2) proto;
		
		Assert.assertEquals(nom_2.clientId.get(), nom_2Check.clientId.get());
		Assert.assertEquals(nom_2.symbol.get(), nom_2Check.symbol.get());
		Assert.assertEquals(nom_2.symbolId.get(), nom_2Check.symbolId.get());
		Assert.assertEquals(nom_2.side.get(), nom_2Check.side.get());
		Assert.assertEquals(nom_2.size.get(), nom_2Check.size.get());
		Assert.assertEquals(nom_2.price.get(), nom_2Check.price.get());
	}
}