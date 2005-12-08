/*
 * $Header$
 * 
 * Copyright (c) The OSGi Alliance (2005). All Rights Reserved.
 * 
 * Implementation of certain elements of the OSGi Specification may be subject
 * to third party intellectual property rights, including without limitation,
 * patent rights (such a third party may or may not be a member of the OSGi
 * Alliance). The OSGi Alliance is not responsible and shall not be held
 * responsible in any manner for identifying or failing to identify any or all
 * such third party intellectual property rights.
 * 
 * This document and the information contained herein are provided on an "AS IS"
 * basis and THE OSGI ALLIANCE DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION
 * HEREIN WILL NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL THE
 * OSGI ALLIANCE BE LIABLE FOR ANY LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF
 * USE OF DATA, INTERRUPTION OF BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR
 * EXEMPLARY, INCIDENTIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN
 * CONNECTION WITH THIS DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH LOSS OR DAMAGE.
 * 
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */

package org.osgi.service.obr;

import java.util.Collection;

/**
 * Exception thrown when a resource cannot be satisfied with the current repositories.
 *
 * @version $Revision$
 */
public class UnsatisfiedException extends Exception {
	Resource	resource;
	Requirement	unsatisfied[];
	static final Requirement EMPTY_REQUIREMENT[] = new Requirement[0];
	
	public UnsatisfiedException( String msg, Resource resource, Collection missing ) {
		this(msg, resource, (Requirement[]) missing.toArray(EMPTY_REQUIREMENT));
	}
	
	public UnsatisfiedException(String msg, Resource resource,
			Requirement[] unsatisfied) {
		super(msg + toString(unsatisfied));
		this.resource = resource;
		this.unsatisfied = unsatisfied;
	}

	private static String toString(Requirement[] list) {
		StringBuffer	sb = new StringBuffer();
		String del = " : ";
		for ( int i =0; i<list.length; i++ ) {
			sb.append(del);
			sb.append(list[i]);
			del = ", ";
		}
		return sb.toString();
	}

	Resource getResource() {
		return resource;
	}

	Requirement[] getUnsatisfiedRequirements() {
		return unsatisfied;
	}
	
}
