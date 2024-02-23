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
