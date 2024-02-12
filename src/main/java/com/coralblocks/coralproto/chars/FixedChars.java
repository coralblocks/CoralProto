package com.coralblocks.coralproto.chars;

import java.util.stream.IntStream;

import com.coralblocks.coralproto.util.MaxLengthStringBuilder;

public class FixedChars implements CharSequence {
	
	private final MaxLengthStringBuilder maxLengthStringBuilder;
	
	public FixedChars(int size) {
		this.maxLengthStringBuilder = new MaxLengthStringBuilder(size);
	}
	
	public MaxLengthStringBuilder getMaxLengthStringBuilder() {
		return maxLengthStringBuilder;
	}
	
	public MaxLengthStringBuilder removeTrailingSpaces() {
		return maxLengthStringBuilder.removeTrailingSpaces();
	}

	@Override
	public int hashCode() {
		return maxLengthStringBuilder.hashCode();
	}

	public int capacity() {
		return maxLengthStringBuilder.capacity();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof FixedChars) {
			FixedChars fc = (FixedChars) obj;
			return fc.maxLengthStringBuilder.equals(this.maxLengthStringBuilder);
		}
		return false;
	}

	public StringBuilder append(String str) {
		return maxLengthStringBuilder.append(str);
	}

	public StringBuilder append(StringBuffer sb) {
		return maxLengthStringBuilder.append(sb);
	}

	public MaxLengthStringBuilder clear() {
		return maxLengthStringBuilder.clear();
	}

	public void setLength(int newLength) {
		maxLengthStringBuilder.setLength(newLength);
	}

	public StringBuilder append(CharSequence s) {
		return maxLengthStringBuilder.append(s);
	}

	public StringBuilder append(CharSequence s, int start, int end) {
		return maxLengthStringBuilder.append(s, start, end);
	}

	public StringBuilder append(char[] str) {
		return maxLengthStringBuilder.append(str);
	}

	public StringBuilder append(char[] str, int offset, int len) {
		return maxLengthStringBuilder.append(str, offset, len);
	}

	public StringBuilder append(boolean b) {
		return maxLengthStringBuilder.append(b);
	}

	public StringBuilder append(char c) {
		return maxLengthStringBuilder.append(c);
	}

	public StringBuilder append(int i) {
		return maxLengthStringBuilder.append(i);
	}

	public StringBuilder append(long lng) {
		return maxLengthStringBuilder.append(lng);
	}

	public StringBuilder append(float f) {
		return maxLengthStringBuilder.append(f);
	}

	public StringBuilder append(double d) {
		return maxLengthStringBuilder.append(d);
	}

	public StringBuilder delete(int start, int end) {
		return maxLengthStringBuilder.delete(start, end);
	}

	public StringBuilder deleteCharAt(int index) {
		return maxLengthStringBuilder.deleteCharAt(index);
	}

	@Override
	public IntStream chars() {
		return maxLengthStringBuilder.chars();
	}

	@Override
	public String toString() {
		return maxLengthStringBuilder.toString();
	}

	public void setCharAt(int index, char ch) {
		maxLengthStringBuilder.setCharAt(index, ch);
	}

	public String substring(int start) {
		return maxLengthStringBuilder.substring(start);
	}

	public String substring(int start, int end) {
		return maxLengthStringBuilder.substring(start, end);
	}

	@Override
	public IntStream codePoints() {
		return maxLengthStringBuilder.codePoints();
	}
	
	public int getTotalLength() {
		return maxLengthStringBuilder.getMaxLength();
	}

	public int getFixedLength() {
		return maxLengthStringBuilder.getMaxLength();
	}

	@Override
	public int length() {
		return maxLengthStringBuilder.length();
	}

	@Override
	public char charAt(int index) {
		return maxLengthStringBuilder.charAt(index);
	}

	@Override
	public CharSequence subSequence(int start, int end) {
		return maxLengthStringBuilder.subSequence(start, end);
	}
}