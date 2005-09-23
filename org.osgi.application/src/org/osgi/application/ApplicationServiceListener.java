/*
 * $Header$
 * 
 * Copyright (c) OSGi Alliance (2005). All Rights Reserved.
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

package org.osgi.application;

import java.util.EventListener;

import org.osgi.framework.Bundle;
import org.osgi.framework.ServicePermission;
import org.osgi.framework.ServiceReference;

/**
 * An <code>ApplicationServiceEvent</code> listener. When an 
 * <code>ServiceEvent</code> is
 * fired, it is converted to an <code>ApplictionServiceEvent</code>
 * and it is synchronously delivered to an <code>ApplicationServiceListener</code>.
 * 
 * <p>
 * <code>ApplicationServiceListener</code> is a listener interface that may be
 * implemented by an application developer.
 * <p>
 * An <code>ApplicationServiceListener</code> object is registered with the Framework
 * using the <code>ApplicationContext.addServiceListener</code> method.
 * <code>ApplicationServiceListener</code> objects are called with an
 * <code>ApplicationServiceEvent</code> object when a service is registered, modified, or
 * is in the process of unregistering.
 * 
 * <p>
 * <code>ApplicationServiceEvent</code> object delivery to 
 * <code>ApplicationServiceListener</code>
 * objects is filtered by the filter specified when the listener was registered.
 * If the Java Runtime Environment supports permissions, then additional
 * filtering is done. <code>ApplicationServiceEvent</code> objects are only delivered to
 * the listener if the application which defines the listener object's class has the
 * appropriate <code>ServicePermission</code> to get the service using at
 * least one of the named classes the service was registered under, and the application
 * specified its dependece on the corresponding service in the application metadata.
 * 
 * <p>
 * <code>ApplicationServiceEvent</code> object delivery to <code>ApplicationServiceListener</code>
 * objects is further filtered according to package sources as defined in
 * {@link ServiceReference#isAssignableTo(Bundle, String)}.
 * 
 * @version $Revision$
 * @see ApplicationServiceEvent
 * @see ServicePermission
 */
public interface ApplicationServiceListener extends EventListener {
	/**
	 * Receives notification that a service has had a lifecycle change.
	 * 
	 * @param event The <code>ApplicationServiceEvent</code> object.
	 */
	public void serviceChanged(ApplicationServiceEvent event);
}
