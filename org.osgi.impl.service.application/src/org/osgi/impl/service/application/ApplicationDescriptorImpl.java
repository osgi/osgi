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

package org.osgi.impl.service.application;

import java.io.*;
import java.util.*;

import org.osgi.framework.ServiceReference;
import org.osgi.service.application.ApplicationDescriptor;
import org.osgi.service.application.ApplicationDescriptor.Delegate;

/**
 *
 * TODO Add Javadoc comment for this type.
 * 
 * @version $Revision$
 */
public class ApplicationDescriptorImpl implements Delegate {
	ApplicationDescriptor descriptor;
	boolean		locked;
	static 		Properties		locks;
	
	/**
	 * @param d
	 * @see org.osgi.service.application.ApplicationDescriptor.Delegate#setApplicationDescriptor(org.osgi.service.application.ApplicationDescriptor)
	 */
	public synchronized void setApplicationDescriptor(ApplicationDescriptor d) {
		descriptor = d;
	}

	/**
	 * @return
	 * @see org.osgi.service.application.ApplicationDescriptor.Delegate#isLocked()
	 */
	public boolean isLocked() {
		return doLock(true, false );
	}

	synchronized boolean doLock(boolean query, boolean newState) {
		try {
			File f = Activator.bc.getDataFile("locks");
			if ( locks == null ) {
				Properties locks = new Properties();
				locks = new Properties();
				if ( f.exists() )
					locks.load( new FileInputStream(f));
			}
			boolean current = locks.containsKey(descriptor.getPID()); 
			if ( query )
				return current;

			if ( newState == current ) {
				// TODO funny, somebody sets the state it is already ...
				return current;
			}
			if ( current )
				locks.remove(descriptor.getPID());
			else
				locks.put(descriptor.getPID(), "locked");
			locks.save(new FileOutputStream(f), "Saved " + new Date());
			return newState;
		} catch( IOException ioe ) {
			ioe.printStackTrace();
			// TODO log this
		}
		return false;
	}
	
	/**
	 * 
	 * @see org.osgi.service.application.ApplicationDescriptor.Delegate#lock()
	 */
	public void lock() {
		doLock(false, true);
	}

	/**
	 * 
	 * @see org.osgi.service.application.ApplicationDescriptor.Delegate#unlock()
	 */
	public void unlock() {
		doLock(false, false);
	}

	public ServiceReference schedule(Map args, String topic, String filter, boolean recurs) {
		return null;
	}

	/**
	 * @param arguments
	 * @see org.osgi.service.application.ApplicationDescriptor.Delegate#launch(java.util.Map)
	 */
	public void launch(Map arguments) {
		// TODO Auto-generated method stub
		// Can safely be ignored ...
	}

}
