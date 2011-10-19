package org.osgi.dmt.ddf;

import java.util.*;

/**
 * A Multiple represents a base node with its direct children. The child's name
 * is used as the key. A Multiple's children have a cardinality of 0..n. The
 * entry can be made optional by wrapping it in an Optional.
 * 
 * @param <K>
 *            The key type. Must be a type that is convertable from/to String
 *            for the child's name.
 * @param <V>
 *            The value type, can be any type mapping to a node.
 */
public interface MAP<K, V> extends NODE {

	/**
	 * Get a value. The given key is the name of a child node, the result is a
	 * NODE at that position or its value when it is a leaf node that can only
	 * be gotten.
	 * 
	 * @param key
	 *            The name of the child node.
	 * @return The value for a given key
	 */
	V get(K key);

	/**
	 * Return the set of keys for this Multiple.
	 * 
	 * @return the set of keys
	 */
	Collection<K> keySet();

	/**
	 * Return the set of values for this Multiple.
	 * 
	 * @return the set of keys
	 */
	Collection<V> values();

	/**
	 * Return the set of Map.Entry objects for this Multiple.
	 * 
	 * @return An entry set over the key/value entries.
	 */
	Collection<Map.Entry<K, V>> entrySet();
}
