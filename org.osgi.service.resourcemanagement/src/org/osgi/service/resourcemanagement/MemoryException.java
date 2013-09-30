/**
 * 
 */
package org.osgi.service.resourcemanagement;

/**
 * This exception is used to report that a Resource Context has reached an ERROR
 * threshold about memory.
 * 
 * @Immutable
 */
public class MemoryException extends Exception {

	private final long	memoryUsage;

	/**
	 * Create a new MemoryException
	 * 
	 * @param memoryUsage the memory consumption at the moment when this
	 *        exception was thrown
	 */
	public MemoryException(final long pMemoryUsage) {
		super("Memory Error Threshold reached.");
		memoryUsage = pMemoryUsage;
	}


	/**
	 * Retrieves the memory consumption at the moment when this exception was
	 * thrown.
	 * 
	 * @return memory consumption.
	 */
	public long getMemoryUsage() {
		return memoryUsage;
	}
}
