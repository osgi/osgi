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
 * The MonitorListener is used by Monitorable services to send notifications
 * when a StatusVariable value is changed. The MonitorListener should register
 * itself as a service at the OSGi Service Registry. This interface is
 * implemented by the Monitor Admin component.
 */
public interface MonitorListener {
    /**
     * Callback for notification of a StatusVariable change.
     * 
     * @param monitorableId the identifier of the Monitorable instance reporting
     *        the change
     * @param statusVariable the StatusVariable that has changed
     * @throws IllegalArgumentException if the specified monitorable ID is
     *         invalid (null, empty, or contains illegal characters)
     */
    public void updated(String monitorableId, StatusVariable statusVariable);
}
