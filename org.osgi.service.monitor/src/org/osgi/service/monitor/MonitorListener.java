/*
 * $Header$
 *
 * Copyright (c) OSGi Alliance (2004, 2005). All Rights Reserved.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this 
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html.
 */

package org.osgi.service.monitor;

/**
 * The <code>MonitorListener</code> is used by <code>Monitorable</code>
 * services to send notifications when a <code>StatusVariable</code> value is
 * changed. The <code>MonitorListener</code> should register itself as a
 * service at the OSGi Service Registry. This interface must (only) be 
 * implemented by the Monitor Admin component.
 */
public interface MonitorListener {
    /**
     * Callback for notification of a <code>StatusVariable</code> change.
     * 
     * @param monitorableId the identifier of the <code>Monitorable</code>
     *        instance reporting the change
     * @param statusVariable the <code>StatusVariable</code> that has changed
     * @throws java.lang.IllegalArgumentException if the specified monitorable
     *         ID is invalid (<code>null</code>, empty, or contains illegal
     *         characters), or if <code>statusVariable</code> is
     *         <code>null</code>
     */
    public void updated(String monitorableId, StatusVariable statusVariable)
            throws IllegalArgumentException;
}
