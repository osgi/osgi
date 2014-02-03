package org.osgi.util.promise;

/**
 * Exception thrown for Parallel execution. Provides access to all the
 * exceptions thrown.
 * 
 */
public class ParallelException extends RuntimeException {
	private static final long serialVersionUID = -1877951607034493220L;
	private Object[] values;
	private Throwable[] exceptions;

	public ParallelException(Object[] values, Throwable[] exceptions) {
		this.values=values;
		this.exceptions=exceptions;
	}

	public Throwable[] getExceptions() {
		return exceptions;
	}

	public Object[] getValues() {
		return values;
	}

}