package com.coralblocks.coralproto.util;

import java.io.Serializable;

public class MaxLengthStringBuilder implements CharSequence, Serializable {
	
    static final long serialVersionUID = 4383685877137921098L;
	
	private final int maxLength;
	private final StringBuilder delegateStringBuilder;
	private final StringBuilder temp = new StringBuilder(64);
	
	public MaxLengthStringBuilder(int maxLength) {
		this.maxLength = maxLength;
		this.delegateStringBuilder = new StringBuilder(maxLength);
	}
	
	public int getMaxLength() {
		return maxLength;
	}
	
	public MaxLengthStringBuilder removeTrailingSpaces() {
		int toTrim = 0;
		for(int i = delegateStringBuilder.length() - 1; i >= 0; i--) {
			if (delegateStringBuilder.charAt(i) == ' ') {
				toTrim++;
			} else {
				break;
			}
		}
		if (toTrim > 0) delegateStringBuilder.setLength(delegateStringBuilder.length() - toTrim);
		return this;
	}
	
	@Override
	public int length() {
		return delegateStringBuilder.length();
	}

	@Override
	public int hashCode() {
		return delegateStringBuilder.hashCode();
	}

	public int capacity() {
		return delegateStringBuilder.capacity();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof MaxLengthStringBuilder) {
			MaxLengthStringBuilder mlsb = (MaxLengthStringBuilder) obj;
			return this.delegateStringBuilder.equals(mlsb.delegateStringBuilder);
		}
		return false;
	}
	
	private void check(int lengthToAdd) {
		int currLen = delegateStringBuilder.length();
		if (currLen + lengthToAdd > maxLength) {
			throw new ArrayIndexOutOfBoundsException("lengthToAdd=" + lengthToAdd + " currLength=" + currLen + " maxLength=" + maxLength);
		}
	}

	public StringBuilder append(String str) {
		check(str.length());
		return delegateStringBuilder.append(str);
	}

	public StringBuilder append(StringBuffer sb) {
		check(sb.length());
		return delegateStringBuilder.append(sb);
	}
	
	public MaxLengthStringBuilder clear() {
		setLength(0);
		return this;
	}

	public void setLength(int newLength) {
		if (newLength > maxLength) {
			throw new ArrayIndexOutOfBoundsException("newLength=" + newLength + " maxLength=" + maxLength);
		}
		delegateStringBuilder.setLength(newLength);
	}

	public StringBuilder append(CharSequence s) {
		check(s.length());
		return delegateStringBuilder.append(s);
	}

	public StringBuilder append(CharSequence s, int start, int end) {
		check(end - start);
		return delegateStringBuilder.append(s, start, end);
	}

	public StringBuilder append(char[] str) {
		check(str.length);
		return delegateStringBuilder.append(str);
	}

	public StringBuilder append(char[] str, int offset, int len) {
		check(len);
		return delegateStringBuilder.append(str, offset, len);
	}

	public StringBuilder append(boolean b) {
		if (b) {
			check("true".length());
		} else {
			check("false".length());
		}
		return delegateStringBuilder.append(b);
	}

	public StringBuilder append(char c) {
		check(1);
		return delegateStringBuilder.append(c);
	}

	public StringBuilder append(int i) {
		temp.setLength(0);
		temp.append(i);
		check(temp.length());
		return delegateStringBuilder.append(temp);
	}

	public StringBuilder append(long lng) {
		temp.setLength(0);
		temp.append(lng);
		check(temp.length());
		return delegateStringBuilder.append(temp);
	}

	public StringBuilder append(float f) {
		temp.setLength(0);
		temp.append(f);
		check(temp.length());
		return delegateStringBuilder.append(temp);
	}

	public StringBuilder append(double d) {
		temp.setLength(0);
		temp.append(d);
		check(temp.length());
		return delegateStringBuilder.append(temp);
	}

	@Override
	public char charAt(int index) {
		return delegateStringBuilder.charAt(index);
	}

	public StringBuilder delete(int start, int end) {
		return delegateStringBuilder.delete(start, end);
	}

	public StringBuilder deleteCharAt(int index) {
		return delegateStringBuilder.deleteCharAt(index);
	}

	@Override
	public String toString() {
		return delegateStringBuilder.toString();
	}

	public void setCharAt(int index, char ch) {
		delegateStringBuilder.setCharAt(index, ch);
	}

	public String substring(int start) {
		return delegateStringBuilder.substring(start);
	}

	@Override
	public CharSequence subSequence(int start, int end) {
		return delegateStringBuilder.subSequence(start, end);
	}

	public String substring(int start, int end) {
		return delegateStringBuilder.substring(start, end);
	}
}
