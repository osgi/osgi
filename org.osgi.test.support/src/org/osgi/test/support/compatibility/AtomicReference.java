package org.osgi.test.support.compatibility;

public class AtomicReference<T> {
	T reference;
	
    public AtomicReference(T reference) {
    	this.reference = reference;
    }

    /**
     * Creates a new AtomicReference with null initial value.
     */
    public AtomicReference() {
    }

    public final synchronized  T get() {
        return reference;
    }

    public final synchronized void set(T reference) {
        this.reference	 = reference;
    }

    public final synchronized boolean compareAndSet(T expect, T update) {
    	if ( this.reference == expect) {
    		this.reference = update;
    		return true;
    	}
    	return false;
    }

    public final  synchronized  T getAndSet(T reference) {
    	T old = this.reference;
    	this.reference = reference;
    	return old;
    }

    public String toString() {
        return String.valueOf(get());
    }


}
