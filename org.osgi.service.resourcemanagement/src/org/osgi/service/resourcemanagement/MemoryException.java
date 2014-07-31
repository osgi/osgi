/*
 * Copyright (c) OSGi Alliance (2010, 2013). All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.osgi.service.resourcemanagement;

/**
 * This exception is used to report that a Resource Context has reached an ERROR
 * threshold about memory.
 * 
 * @Immutable
 * 
 * @author $Id$
 */
public class MemoryException extends RuntimeException {

	/** generated */
	private static final long	serialVersionUID	= -5945055263601915960L;

	private final long			memoryUsage;

	/**
	 * Create a new MemoryException
	 * 
	 * @param pMemoryUsage the memory consumption at the moment when this
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
