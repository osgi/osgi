package org.osgi.dmt.ddf;

public interface Mutable<T> {
	T get();
	void replace(T v);
}
