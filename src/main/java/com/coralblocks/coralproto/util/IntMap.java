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

/**
 * <p>A fast hash map that uses int primitives as keys.</p>
 * 
 * <p>It produces <b>ZERO garbage.</b>.</p>
 *  
 * <p>Its default initial capacity (before a rehash is needed) is 128. The default load factor is 80%.</p>
 *  
 * <p>Initial capacity must be a <b>power of two</b> or an IllegalArgumentException will be thrown by the constructor. That's for <b>bitwise fast hashing</b>.</p>
 *  
 * <p>You should choose the initial capacity wisely, according to your needs, in order to avoid a rehash which of course produces garbage.</p>
 *  
 * <p><b>NOTE:</b> This data structure is designed on purpose to be used by <b>single-threaded systems</b>, in other words, 
 *  it will break if used concurrently by multiple threads.</p>
 * 
 * @param <E> the entry type this hash map will hold
 */
public class IntMap<E> {
	
	private static final int DEFAULT_INITIAL_CAPACITY = 128;
	private static final float DEFAULT_LOAD_FACTOR = 0.80f;
	
	private static class Entry<T> {
		int key;
		T value;
		Entry<T> next;
	}
	
	private Entry<E>[] data;

	private int lengthMinusOne;

	private int count;

	private int threshold;

	private float loadFactor;

	private Entry<E> poolHead;

	/**
	 * Creates a IntMap with the default initial capacity and load factor.
	 */
	public IntMap() {
		this(DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR);
	}

	/**
	 * Creates a IntMap with the default load factor.
	 * 
	 * @param initialCapacity the desired initial capacity
	 */
	public IntMap(int initialCapacity) {
		this(initialCapacity, DEFAULT_LOAD_FACTOR);
	}

	/**
	 * Creates a IntMap.
	 * 
	 * @param initialCapacity the desired initial capacity
	 * @param loadFactor the desired load factor
	 */
	@SuppressWarnings("unchecked")
	public IntMap(int initialCapacity, float loadFactor) {

		if (!MathUtils.isPowerOfTwo(initialCapacity)) {
			throw new IllegalArgumentException("Size must be power of two: " + initialCapacity);
		}

		this.data = new Entry[initialCapacity];
		this.lengthMinusOne = initialCapacity - 1;
		this.loadFactor = loadFactor;
		this.threshold =  Math.round(initialCapacity * loadFactor);
	}
	
	private Entry<E> getEntryFromPool(int key, E value, Entry<E> next) {

		Entry<E> newEntry = poolHead;

		if (newEntry != null) {
			poolHead = newEntry.next;
		} else {
			newEntry = new Entry<E>();
		}

		newEntry.key = key;
		newEntry.value = value;
		newEntry.next = next;

		return newEntry;
	}

	private void releaseEntryBackToPool(Entry<E> e) {
		e.value = null;
		e.next = poolHead;
		poolHead = e;
	}
	
	/**
	 * Returns the size of this map
	 * 
	 * @return the size of this map
	 */
	public int size() {
		return count;
	}

	/**
	 * Is this map empty? (size == 0)
	 * 
	 * @return true if empty
	 */
	public boolean isEmpty() {
		return size() == 0;
	}

	/**
	 * Does the map contain the given value?
	 * 
	 * @param value the value to be checked
	 * @return true if the map contains this value
	 */
	public boolean contains(E value) {

		for(int i = data.length - 1; i >= 0; i--) {

			Entry<E> e = data[i];

			while(e != null) {

				if (e.value.equals(value)) {

					return true;
				}

				e = e.next;
			}
		}
		
		return false;
	}

	private final int toArrayIndex(int key) {
		return  (key & 0x7FFFFFFF) & lengthMinusOne;
	}

	/**
	 * Does the map contain the given key?
	 * 
	 * @param key the key to check
	 * @return true if the map contains the given key
	 */
	public boolean containsKey(int key) {

		Entry<E> e = data[toArrayIndex(key)];

		while(e != null) {

			if (e.key == key) {

				return true;
			}

			e = e.next;
		}
		
		return false;
	}

	/**
	 * Returns the value associated with the given key in this map
	 * 
	 * @param key the key to get the value
	 * @return the value associated with the given key
	 */
	public E get(int key) {

		Entry<E> e = data[toArrayIndex(key)];

		while(e != null) {

			if (e.key == key) {

				return e.value;
			}

			e = e.next;
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	private void rehash() {

		int oldCapacity = data.length;

		Entry<E> oldData[] = data;

		int newCapacity = oldCapacity * 2; // power of two, always!

		data = new Entry[newCapacity];
		lengthMinusOne = newCapacity - 1;

		threshold = Math.round(newCapacity * loadFactor);

		for(int i = oldCapacity - 1; i >= 0; i--) {

			Entry<E> old = oldData[i];

			while(old != null) {

				Entry<E> e = old;

				old = old.next;

				int index = toArrayIndex(e.key);

				e.next = data[index];

				data[index] = e;
			}
		}
	}
	
	/**
	 * Adds a value for the given key in this map
	 * 
	 * @param key the key to add
	 * @param value the value to add
	 * @return any previous value associated with the given key or null if there was not a value associated with this key
	 */
	public E put(int key, E value) {

		if (value == null) {
			throw new IllegalArgumentException("Cannot put null value!");
		}

		int index = toArrayIndex(key);

		Entry<E> e = data[index];

		while(e != null) {

			if (e.key == key) {

				E old = e.value;

				e.value = value;

				return old;
			}

			e = e.next;
		}

		if (count >= threshold) {

			rehash();

			index = toArrayIndex(key); // lengthMinusOne has changed!

			data[index] = getEntryFromPool(key, value, data[index]);

		} else {

			data[index] = getEntryFromPool(key, value, data[index]);
		}

		count++;

		return null;
	}

	/**
	 * Removes and returns the value associated with the given key in the map
	 * 
	 * @param key the key to remove
	 * @return the value for the removed key
	 */
	public E remove(int key) {

		int index = toArrayIndex(key);

		Entry<E> e = data[index];
		Entry<E> prev = null;

		while(e != null) {

			if (e.key == key) {

				if (prev != null) {

					prev.next = e.next;

				} else {

					data[index] = e.next;
				}

				E oldValue = e.value;

				releaseEntryBackToPool(e);

				count--;

				return oldValue;
			}

			prev = e;
			e = e.next;
		}

		return null;
	}

	/**
	 * Clears the map. The map will be empty (size == 0) after this operation.
	 */
	public void clear() {

		for(int index = data.length - 1; index >= 0; index--) {

			while(data[index] != null) {

				Entry<E> next = data[index].next;

				releaseEntryBackToPool(data[index]);

				data[index] = next;
			}
		}

		count = 0;
	}
}
