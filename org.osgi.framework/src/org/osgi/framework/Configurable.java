/*
 * $Header$
 *
 * Copyright (c) The OSGi Alliance (2000-2001).
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

package org.osgi.framework;

/**
 * Supports a configuration object.
 *
 * <p><tt>Configurable</tt> is an interface that should be used by a bundle developer in support
 * of a configurable service.
 * Bundles that need to configure a service may test to determine
 * if the service object is an <tt>instanceof Configurable</tt>.
 *
 * @version $Revision$
 * @deprecated Please use the Configuration Admin
 */
public abstract interface Configurable
{
    /**
     * Returns this service's configuration object.
     *
     * <p>Services implementing <tt>Configurable</tt> should take care when returning a
     * service configuration object since this object is probably sensitive.
     * <p>If the Java Runtime Environment supports permissions, it is recommended that
     * the caller is checked for the appropriate permission before returning the configuration object.
     * It is recommended that callers possessing the appropriate
     * {@link AdminPermission} always be allowed to get the configuration object.
     *
     * @return The configuration object for this service.
     * @exception java.lang.SecurityException If the caller does not have
     * an appropriate permission and the Java Runtime Environment supports permissions.
     * @deprecated Please use the Configuration Admin
     */
    public abstract Object getConfigurationObject();
}


