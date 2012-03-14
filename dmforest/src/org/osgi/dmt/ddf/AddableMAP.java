
package org.osgi.dmt.ddf;

/**
 * Allows the Multiple to be mutated, mapping to the actions Add and Delete.
 * 
 * @param <K> The key type, must be convertable to/from a string
 * @param <V> The value type
 */
public interface AddableMAP<K, V> extends MAP<K, V> {
	V add();

	V put(K key);
}
