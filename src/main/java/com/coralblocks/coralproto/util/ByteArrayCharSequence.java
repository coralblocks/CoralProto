package com.coralblocks.coralproto.util;

public class ByteArrayCharSequence implements CharSequence {

    private final byte[] byteArray;
    private int actualSize;

    public ByteArrayCharSequence(int maxSize) {
        this.byteArray = new byte[maxSize];
        this.actualSize = maxSize;
    }
    
    public void setSize(int size) {
    	if (size > byteArray.length) throw new ArrayIndexOutOfBoundsException();
    	this.actualSize = size;
    }
    
    public byte[] getByteArray() {
    	return byteArray;
    }
    
    @Override
    public int length() {
        return actualSize;
    }

    @Override
    public char charAt(int index) {
    	if (index >= actualSize) throw new ArrayIndexOutOfBoundsException();
        return (char) byteArray[index];
    }

    @Override
    public CharSequence subSequence(int start, int end) {
    	throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        return new String(byteArray, 0, actualSize);
    }
}
