/*
 * Copyright (c) IBM Corporation (2005)
 *
 * These materials have been contributed  to the OSGi Alliance as 
 * "MEMBER LICENSED MATERIALS" as defined in, and subject to the terms of, 
 * the OSGi Member Agreement, specifically including but not limited to, 
 * the license rights and warranty disclaimers as set forth in Sections 3.2 
 * and 12.1 thereof, and the applicable Statement of Work. 
 *
 * All company, brand and product names contained within this document may be 
 * trademarks that are the sole property of the respective owners.
 */


package org.eclipse.osgi.component.workqueue;

/**
 * The WorkDispatcher interface contains the method that is called by the
 * WorkQueue to dispatch work.
 */

public interface WorkDispatcher {
	/**
	 * This method is called once for each work item.
	 * This method can then complete processing work on the work queue thread.
	 * 
	 * <p>The WorkQueue will ignore any Throwable thrown by this method
	 * in order to continue dispatch of the next work item.
	 *
	 * @param workAction Work action value passed from the work enqueuer.
	 * @param workObject Work object passed from the work enqueuer.
	 */
	public void dispatchWork(int workAction, Object workObject);
}