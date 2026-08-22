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

import org.junit.Assert;
import org.junit.Test;

public class IDLTest {

	@Test
	public void testGeneratesDirectSerializationMethods() {
		IDL idl = new IDL("""
				TYPE = T
				SUBTYPE = D
				VERSION = 7

				required: long
				optional: int!
				entries:
					price: double
					quantity: int
				""");

		String code = idl.getCode();
		Assert.assertTrue(code.contains("public final int getLength()"));
		Assert.assertTrue(code.contains("+ required.size()"));
		Assert.assertTrue(code.contains("+ optional.size()"));
		Assert.assertTrue(code.contains("+ entries.size()"));
		Assert.assertTrue(code.contains("required.readFrom(buf);"));
		Assert.assertTrue(code.contains("required.reset();"));
		Assert.assertTrue(code.contains("optional.markAsNotPresent();"));
		Assert.assertTrue(code.contains("if (optional.isPresent())"));
		Assert.assertTrue(code.contains("entries.readFrom(buf);"));
		Assert.assertTrue(code.contains("buf.putShort((short) 7);"));
		Assert.assertTrue(code.contains("public final void writeAscii(boolean shortVersion, java.nio.ByteBuffer buf)"));
		Assert.assertTrue(code.contains("writeAsciiTypeSubtypeVersionName(shortVersion, buf);"));
		Assert.assertTrue(code.contains("required.writeAsciiTo(buf);"));
		Assert.assertTrue(code.contains("optional.writeAsciiTo(buf);"));
		Assert.assertTrue(code.contains("writeAscii(buf, \"BLANK\");"));
	}
}
