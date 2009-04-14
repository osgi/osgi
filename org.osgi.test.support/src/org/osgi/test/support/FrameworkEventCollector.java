/*
 * $Id$
 * 
 * Copyright (c) OSGi Alliance (2007). All Rights Reserved.
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
package org.osgi.test.support;

import java.util.Comparator;

import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.FrameworkListener;

public class FrameworkEventCollector extends EventCollector implements
		FrameworkListener {
	private final int	mask;

	public FrameworkEventCollector(int typeMask) {
		this.mask = typeMask;
	}

	public void frameworkEvent(FrameworkEvent event) {
		if ((event.getType() & mask) != 0)
			addEvent(event);
	}

	public Comparator getComparator() {
		return new Comparator() {
			public int compare(Object o1, Object o2) {
				FrameworkEvent event1 = (FrameworkEvent)o1;
				FrameworkEvent event2 = (FrameworkEvent)o2;
				 
				long lresult = event1.getBundle().getBundleId() - event2.getBundle().getBundleId();
				if (lresult < 0) {
					return -1;
				}
				if (lresult > 0) {
					return 1;
				}
				int result = event1.getType() - event2.getType();
				if (result != 0) {
					return result;
				}
				Throwable t1 = event1.getThrowable();
				Throwable t2 = event2.getThrowable();
				
				if (t1 == t2) {
					return 0;
				}
				if (t1 == null) {
					return -1;
				}
				if (t2 == null) {
					return 1;
				}
				
				return t1.getClass().getName().compareTo(t2.getClass().getName());
			}
		};
	}
}