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
package com.coralblocks.coralproto.util;

import java.nio.ByteBuffer;

public class ByteBufferCharSequence implements CharSequence {

    private final ByteBuffer byteBuffer;
    private int actualSize;

    public ByteBufferCharSequence(int maxSize) {
        this.byteBuffer = ByteBuffer.allocateDirect(maxSize);
        this.actualSize = maxSize;
    }
    
    public void setSize(int size) {
    	if (size > byteBuffer.capacity()) throw new ArrayIndexOutOfBoundsException();
    	this.actualSize = size;
    }
    
    public ByteBuffer getByteBuffer() {
    	byteBuffer.limit(actualSize).position(0);
    	return byteBuffer;
    }
    
    @Override
    public boolean equals(Object o) {
    	if (o instanceof ByteBufferCharSequence) {
    		ByteBufferCharSequence bbcs = (ByteBufferCharSequence) o;
    		if (bbcs.getByteBuffer().equals(this.getByteBuffer())) return true;
    	}
    	return false;
    }
    
    @Override
    public int length() {
        return actualSize;
    }

    @Override
    public char charAt(int index) {
    	if (index >= actualSize) throw new ArrayIndexOutOfBoundsException();
    	byteBuffer.limit(actualSize).position(0);
        return (char) byteBuffer.get(index);
    }

    @Override
    public CharSequence subSequence(int start, int end) {
    	throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
    	byteBuffer.limit(actualSize).position(0);
        return ByteBufferUtils.parseString(byteBuffer);
    }
}
