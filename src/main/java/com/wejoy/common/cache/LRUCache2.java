package com.wejoy.common.cache;



import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

/**
 * An LRU cache, based on <code>LinkedHashMap</code>.
 * 
 * <p>
 * This cache has a fixed maximum number of elements (<code>cacheSize</code>).
 * If the cache is full and entry is added, the LRU (least recently
 * used) entry is dropped.
 * 
 * <p>
 * This class is thread-safe. All methods of this class are synchronized.
 * 
 */
public class LRUCache2<K, V> implements Serializable{

	private static final long serialVersionUID = -1666374475314255875L;

	private static final float hashTableLoadFactor = 0.75f;

	private Map<K, V> map;
	
	private int cacheSize;
	
	private static final int DEFAULT_MAX_SIZE = 2000;
	
	public LRUCache2(){
		this(DEFAULT_MAX_SIZE);
	}

	/**
	 * Creates a new LRU cache.
	 * 
	 * @param cacheSize
	 *            the maximum number of entries that will be kept in this cache.
	 */
	public LRUCache2(int cacheSize) {
		this.cacheSize = cacheSize;
		int hashTableCapacity = (int) Math
				.ceil(cacheSize / hashTableLoadFactor) + 1;
//		map = Collections.synchronizedMap(new LinkedHashMap<K, V>(hashTableCapacity, hashTableLoadFactor,
		map = Collections.synchronizedMap(new LinkedHashMap<K, V>(hashTableCapacity, hashTableLoadFactor,
				true) {
			// (an anonymous inner class)
			private static final long serialVersionUID = 1;

			@Override
			protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
				return size() > LRUCache2.this.cacheSize;
			}
		});
	}

	/**
	 * Retrieves an entry from the cache.<br>
	 * The retrieved entry becomes the MRU (most recently used) entry.
	 * 
	 * @param key
	 *            the key whose associated value is to be returned.
	 * @return the value associated to this key, or null if no value with this
	 *         key exists in the cache.
	 */
	public synchronized V get(K key) {
		return map.get(key);
	}

	/**
	 * Adds an entry to this cache. The new entry becomes the MRU (most recently
	 * used) entry. If an entry with the specified key already exists in the
	 * cache, it is replaced by the new entry. If the cache is full, the LRU
	 * (least recently used) entry is removed from the cache.
	 * 
	 * @param key
	 *            the key with which the specified value is to be associated.
	 * @param value
	 *            a value to be associated with the specified key.
	 */
	public synchronized void put(K key, V value) {
		map.put(key, value);
	}
	
	public synchronized boolean contains(K key) {
		return map.containsKey(key);
	}

	/**
	 * Clears the cache.
	 */
	public synchronized void clear() {
		map.clear();
	}

	/**
	 * Returns the number of used entries in the cache.
	 * 
	 * @return the number of entries currently in the cache.
	 */
	public synchronized int usedEntries() {
		return map.size();
	}
	
	/**
	 * Remove object with key
	 * 
	 * @param key
	 */
	public synchronized void remove(K key){
		map.remove(key);
	}

	/**
	 * Returns a <code>Collection</code> that contains a copy of all cache
	 * entries.
	 * 
	 * @return a <code>Collection</code> with a copy of the cache content.
	 */
	public synchronized List<Map.Entry<K, V>> getAll() {
		return new ArrayList<Map.Entry<K, V>>(map.entrySet());
	}

	// Test routine for the LRUCache class.
	public static void main(String[] args) {
		LRUCache2<String, String> c = new LRUCache2<String, String>(3);
		c.put("1", "one"); // 1
		c.put("2", "two"); // 2 1
		c.put("3", "three"); // 3 2 1
		c.put("4", "four"); // 4 3 2
		c.put("5", "five"); // 5 4 3
		c.put("4", "second four"); // 4 5 3
		
		List<Map.Entry<String, String>> list = c.getAll();
		for (Map.Entry<String, String> e : list)
			System.out.println(e.getKey() + " : " + e.getValue());
		System.out.println();
		c.put("6", "six");
		c.remove("5");
		list = c.getAll();
		for (Map.Entry<String, String> e : list)
			System.out.println(e.getKey() + " : " + e.getValue());
		
	}
	
} 