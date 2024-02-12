package com.coralblocks.coralproto.util;

public final class CharMap<E> {

	@SuppressWarnings("unchecked")
	private final E[] data = (E[]) new Object[256];
	
	private int count = 0;
	
	public CharMap() {
		
	}

	private final int convert(char key) {
		return ((byte) key) & 0xff;
	}

	public final boolean containsKey(char key) {
		return data[convert(key)] != null;
	}

	public final E put(char key, E value) {
		if (value == null) {
			throw new NullPointerException("CharMap does not support NULL values: " + key);
		}

		int index = convert(key);
		E old = data[index];
		data[index] = value;
		if (old == null) {
			// not replacing...
			count++;
		}
		return old;
	}

	public final E get(char key) {
		return data[convert(key)];
	}

	public final E remove(char key) {
		int index = convert(key);
		E old = data[index];
		data[index] = null;
		if (old != null) {
			// really removing something...
			count--;
		}
		return old;
	}

	public final boolean isEmpty() {
		return count == 0;
	}

	public final void clear() {
		for (int i = 0; i < data.length; i++) {
			data[i] = null;
		}
		count = 0;
	}

	public final int size() {
		return count;
	}
}
