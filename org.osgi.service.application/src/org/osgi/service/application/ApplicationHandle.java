/*
 * Copyright (c) The Open Services Gateway Initiative (2004).
 * All Rights Reserved.
 *
 * Implementation of certain elements of the Open Services Gateway Initiative
 * (OSGI) Specification may be subject to third party intellectual property
 * rights, including without limitation, patent rights (such a third party may
 * or may not be a member of OSGi). OSGi is not responsible and shall not be
 * held responsible in any manner for identifying or failing to identify any or
 * all such third party intellectual property rights.
 *
 * This document and the information contained herein are provided on an "AS
 * IS" basis and OSGI DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION HEREIN WILL
 * NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF MERCHANTABILITY OR
 * FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL OSGI BE LIABLE FOR ANY
 * LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF USE OF DATA, INTERRUPTION OF
 * BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR EXEMPLARY, INCIDENTIAL,
 * PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN CONNECTION WITH THIS
 * DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH LOSS OR DAMAGE.
 *
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */
package org.osgi.service.application;

public interface ApplicationHandle {
	/**
	 * The application instance is running
	 * 
	 */
	public final static int	RUNNING		= 0;
	/**
	 * The application instance is being suspended
	 * 
	 */
	public final static int	SUSPENDING	= 1;
	/**
	 * The application instance has been suspended
	 * 
	 */
	public final static int	SUSPENDED	= 2;
	/**
	 * The application instance is being resumed. Status 'resumed' is equivalent
	 * to status 'running'
	 * 
	 */
	public final static int	RESUMING	= 3;
	/**
	 * The application instance is being stopped. Status 'stopped' is equivalent
	 * to status 'nonexistent'
	 * 
	 */
	public final static int	STOPPING	= 4;
	/**
	 * The application instance does not exist. Either an instance with the ID
	 * is never created .
	 * 
	 */
	public final static int	NONEXISTENT	= 5;

	/**
	 * Get the status (constants defined in the Application class) of the
	 * application instance
	 * 
	 * @modelguid {8C7D95E9-A8E2-40F1-9BFD-C55A5B80148F}
	 */
	public int getAppStatus();

	public ApplicationDescriptor getAppDescriptor();

	public void destroyApplication() throws Exception;

	public void suspendApplication() throws Exception;

	public void resumeApplication() throws Exception;
}
