package org.osgi.dmt.ddf;

/**
 * An optional node.
 * 
 * @param <T>
 */
public interface Opt<T> extends NODE {
	T opt();
	boolean isSet();
}
