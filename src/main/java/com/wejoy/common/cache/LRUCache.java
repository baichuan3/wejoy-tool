package com.wejoy.common.cache;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;



/**
 * 
 *  https://code.google.com/p/concurrentlinkedhashmap/
 *  
 * */
public class LRUCache<K, V> implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private ConcurrentLinkedHashMap<K, V> map;
	private static final int DEFAULT_MAX_SIZE = 2000;
	
	public LRUCache(){
		this(DEFAULT_MAX_SIZE);
	}

	/**
	 * Creates a new LRU cache.
	 * 
	 * @param cacheSize
	 *            the maximum number of entries that will be kept in this cache.
	 */
	public LRUCache(int cacheSize) {
		map = new  ConcurrentLinkedHashMap.Builder<K, V>().maximumWeightedCapacity(cacheSize).build();
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
	public V get(K key) {
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
	public void put(K key, V value) {
		map.put(key, value);
	}
	
	public boolean contains(K key) {
		return map.containsKey(key);
	}
	
	/**
	 * Clears the cache.
	 */
	public void clear() {
		map.clear();
	}
	
	/**
	 * Returns the number of used entries in the cache.
	 * 
	 * @return the number of entries currently in the cache.
	 */
	public int usedEntries() {
		return map.size();
	}
	
	/**
	 * Remove object with key
	 * 
	 * @param key
	 */
	public void remove(K key){
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
		LRUCache<String, String> c = new LRUCache<String, String>(3);
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
		
		System.out.println(c.usedEntries());
	}
}
