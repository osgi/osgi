/*
 * $Header$
 * 
 * Copyright (c) OSGi Alliance (2000, 2004). All Rights Reserved.
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

package org.osgi.framework;

import java.util.EventListener;

/**
 * A <code>FrameworkEvent</code> listener.
 * 
 * <p>
 * <code>FrameworkListener</code> is a listener interface that may be implemented
 * by a bundle developer. A <code>FrameworkListener</code> object is registered
 * with the Framework using the {@link BundleContext#addFrameworkListener}
 * method. <code>FrameworkListener</code> objects are called with a
 * <code>FrameworkEvent</code> objects when the Framework starts and when
 * asynchronous errors occur.
 * 
 * @version $Revision$
 * @see FrameworkEvent
 */

public abstract interface FrameworkListener extends EventListener {

	/**
	 * Receives notification of a general <code>FrameworkEvent</code> object.
	 * 
	 * @param event The <code>FrameworkEvent</code> object.
	 */
	public abstract void frameworkEvent(FrameworkEvent event);
}

