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
		short version = bb.getShort();
		
		Assert.assertEquals(type, spm.getType());
		Assert.assertEquals(subtype, spm.getSubtype());
		Assert.assertEquals(version, spm.getVersion());
		
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