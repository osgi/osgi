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

/**
 * A <tt>ServiceEvent</tt> listener.
 *
 * <p><tt>AllServiceListener</tt> is a listener interface that may be implemented by a bundle
 * developer.
 * <p>An <tt>AllServiceListener</tt> object is registered with the Framework using the
 * <tt>BundleContext.addServiceListener</tt> method.
 * <tt>AllServiceListener</tt> objects are called with a <tt>ServiceEvent</tt> object when
 * a service has been registered or modified, or is in the process of unregistering.
 *
 * <p><tt>ServiceEvent</tt> object delivery to <tt>AllServiceListener</tt> objects is filtered by the
 * filter specified when the listener was registered. If the Java Runtime Environment
 * supports permissions, then additional filtering is done.
 * <tt>ServiceEvent</tt> objects are only delivered to the listener if the bundle which defines
 * the listener object's class has the appropriate <tt>ServicePermission</tt> to get the service
 * using at least one of the named classes the service was registered under.
 * 
 * <p>
 * Unlike normal <tt>ServiceListener</tt> objects,
 * <tt>AllServiceListener</tt> objects have all ServiceEvent objects delivered regardless of the
 * package scope of the listening bundle.
 * 
 * @version $Revision$
 * @see ServiceEvent
 * @see ServicePermission
 */

public abstract interface AllServiceListener extends ServiceListener
{
	//This is a marker interface
}


