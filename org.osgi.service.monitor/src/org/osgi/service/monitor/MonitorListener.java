/*
 * Copyright (c) OSGi Alliance (2004, 2008). All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.osgi.service.monitor;

/**
 * The {@code MonitorListener} is used by {@code Monitorable} services
 * to send notifications when a {@code StatusVariable} value is changed.
 * The {@code MonitorListener} should register itself as a service at the
 * OSGi Service Registry. This interface must (only) be implemented by the
 * Monitor Admin component.
 * 
 * @version $Id$
 */
public interface MonitorListener {
    /**
     * Callback for notification of a {@code StatusVariable} change.
     * 
     * @param monitorableId the identifier of the {@code Monitorable}
     *        instance reporting the change
     * @param statusVariable the {@code StatusVariable} that has changed
     * @throws java.lang.IllegalArgumentException if the specified monitorable
     *         ID is invalid ({@code null}, empty, or contains illegal
     *         characters) or points to a non-existing {@code Monitorable}, 
     *         or if {@code statusVariable} is {@code null}
     */
    public void updated(String monitorableId, StatusVariable statusVariable)
            throws IllegalArgumentException;
}
