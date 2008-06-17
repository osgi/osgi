/*
 * $Date: 2008-04-02 12:42:59 -0800 $
 *
 * Copyright (c) OSGi Alliance (2004, 2007). All Rights Reserved.
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

package org.osgi.impl.service.discovery;

import java.util.Comparator;

import org.osgi.service.discovery.ServiceDescription;

/**
 * <code>AbstractServiceDescription</code> is a proposed super class for implementations of the <code>ServiceDescription</code>
 * interface. In its implementation, it delegates the invocations of <code>compare()</code> and <code>equals()</code>
 * of the <code>Comparator</code> interface to a comparator member. This member can be set from outside to exchange the comparison
 * algorithm dynamically.
 *
 */
public abstract class AbstractServiceDescription implements ServiceDescription {
	/**
	 * Comparator to delegate the comparison activity to.
	 */
	protected Comparator comparator; 
	
	/**
	 * Constructor taking the Comparator.
	 * @param comparator Comparator object used for comparison of two ServiceDescription objects
	 */
	public AbstractServiceDescription(Comparator comparator) {
		super();
		this.comparator = comparator;
	}

	/**
	 * Set the Comparator field.
	 * @param comparator Comparator object used for comparison of two ServiceDescription objects
	 */
	public void setComparator(Comparator comparator) {
		this.comparator = comparator;
	}

	public int compare(Object var0, Object var1) {
		return comparator != null ? comparator.compare(var0, var1) : 0;
	}

	public boolean equals(Object var0) {
		return comparator != null ? comparator.equals(var0) : super.equals(var0);
	}

}
