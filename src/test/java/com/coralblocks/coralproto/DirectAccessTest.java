package com.coralblocks.coralproto;

import java.nio.ByteBuffer;

import org.junit.Assert;
import org.junit.Test;

import com.coralblocks.coralproto.example.SampleProtoMessage;
import com.coralblocks.coralproto.util.ByteBufferUtils;


public class DirectAccessTest {
	
	@Test
	public void testDirectAccess() {
		
		SampleProtoMessage spm = new SampleProtoMessage();
		
		spm.aByte = -23;
		spm.aShort = 3333;
		spm.aInt = -1111111;
		spm.aLong = 2222222222L;
		spm.aChar = 'c';
		spm.aBoolean = true;
		spm.chars.set("ABCDEFGH");
		spm.bytes.set("abcdefgh".getBytes());
		spm.varChars.set("XXXX");
		spm.varBytes.set("YYYY".getBytes());
		
		ByteBuffer bb = ByteBuffer.allocate(1024);
		
		spm.write(bb);
		
		bb.flip();
		
		char type = (char) bb.get();
		char subtype = (char) bb.get();
		
		Assert.assertEquals(type, spm.getType());
		Assert.assertEquals(subtype, spm.getSubtype());
		
		SampleProtoMessage spmDst = new SampleProtoMessage();
		
		spmDst.read(bb);
		
		Assert.assertEquals(spm.aByte, spmDst.aByte);
		Assert.assertEquals(spm.aShort, spmDst.aShort);
		Assert.assertEquals(spm.aInt, spmDst.aInt);
		Assert.assertEquals(spm.aLong, spmDst.aLong);
		Assert.assertEquals(spm.aChar, spmDst.aChar);
		Assert.assertEquals(spm.aBoolean, spmDst.aBoolean);
		Assert.assertEquals(spm.chars, spmDst.chars);
		Assert.assertEquals(spm.bytes, spmDst.bytes);
		Assert.assertEquals(spm.varChars, spmDst.varChars);
		Assert.assertEquals(spm.varBytes, spmDst.varBytes);
		
		bb.clear();
		
		spmDst.writeAscii(true, bb);
		
		bb.flip();
		
		Assert.assertEquals("PM|-23|3333|-1111111|2222222222|abcdefgh|YYYY|ABCDEFGH|XXXX|c|Y", ByteBufferUtils.parseString(bb));
		
		bb.clear();
		
		spmDst.writeAscii(false, bb);
		
		bb.flip();
		
		Assert.assertEquals("PM (SampleProtoMessage)|-23|3333|-1111111|2222222222|abcdefgh|YYYY|ABCDEFGH|XXXX|c|Y", ByteBufferUtils.parseString(bb));
		
	}
}