package com.coralblocks.coralproto.util;

public class ByteArrayCharSequence implements CharSequence {

    private final byte[] byteArray;

    public ByteArrayCharSequence(int size) {
        this.byteArray = new byte[size];
    }
    
    public byte[] getByteArray() {
    	return byteArray;
    }
    
    @Override
    public int length() {
        return byteArray.length;
    }

    @Override
    public char charAt(int index) {
        return (char) byteArray[index];
    }

    @Override
    public CharSequence subSequence(int start, int end) {
    	throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        return new String(byteArray);
    }
    
    @Override
    public int hashCode() {
    	return byteArray.hashCode();
    }
    
    @Override
    public boolean equals(Object o) {
    	if (o instanceof ByteArrayCharSequence) {
    		ByteArrayCharSequence bacs = (ByteArrayCharSequence) o;
    		return bacs.byteArray.equals(this.byteArray);
    	}
    	return false;
    }
}
