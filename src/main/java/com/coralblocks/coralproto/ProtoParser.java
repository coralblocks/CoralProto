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

import com.coralblocks.coralds.map.IntMap;
import com.coralblocks.coralproto.util.ByteUtils;

public abstract class ProtoParser {
	
	private final IntMap<Proto> protoMap = new IntMap<Proto>(256);
	
	public ProtoParser() {
		Proto[] protos = defineProtoMessages();
		for(Proto p : protos) {
			byte type = (byte) p.getType();
			byte subtype = (byte) p.getSubtype();
			short version = p.getVersion();
			int pKey = ByteUtils.toInt(type, subtype, version);
			if (protoMap.containsKey(pKey)) {
				Proto pOther = protoMap.get(pKey);
				throw new RuntimeException("Duplicate proto message (same type, subtype and version): " + p.getClass().getSimpleName() + " " + pOther.getClass().getSimpleName());
			}
			protoMap.put(pKey, p);
		}
	}
	
	protected abstract Proto[] defineProtoMessages();
	
	public Proto parse(ByteBuffer data) {
		
		if (data.remaining() < 4) return null;
		
		byte type = data.get();
		byte subtype = data.get();
		short version = data.getShort();
		
		int pKey = ByteUtils.toInt(type, subtype, version);
		
		Proto p = protoMap.get(pKey);
		
		if (p == null) return null;
		
		p.read(data);
		
		return p;
	}
		
}