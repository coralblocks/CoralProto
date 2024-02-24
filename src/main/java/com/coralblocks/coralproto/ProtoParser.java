/* 
 * Copyright 2024 (c) CoralBlocks - http://www.coralblocks.com
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

import com.coralblocks.coralproto.util.CharUtils;
import com.coralblocks.coralproto.util.IntMap;

public abstract class ProtoParser {
	
	private final IntMap<Proto> protoMap = new IntMap<Proto>(256);
	
	public ProtoParser() {
		Proto[] protos = defineProtoMessages();
		for(Proto p : protos) {
			byte type = (byte) p.getType();
			byte subtype = (byte) p.getSubtype();
			short pKey = CharUtils.toShort(type, subtype);
			if (protoMap.containsKey(pKey)) {
				Proto pOther = protoMap.get(pKey);
				throw new RuntimeException("Duplicate proto message (same type and subtype): " + p.getClass().getSimpleName() + " " + pOther.getClass().getSimpleName());
			}
			protoMap.put(pKey, p);
		}
	}
	
	protected abstract Proto[] defineProtoMessages();
	
	public Proto parse(ByteBuffer data) {
		
		if (data.remaining() < 2) return null;
		
		byte type = data.get();
		byte subtype = data.get();
		
		short pKey = CharUtils.toShort(type, subtype);
		
		Proto p = protoMap.get(pKey);
		
		if (p == null) return null;
		
		p.read(data);
		
		return p;
	}
		
}