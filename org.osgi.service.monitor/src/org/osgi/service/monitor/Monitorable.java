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
 * A Monitorable can provide information about itself in the form of
 * StatusVariables. Instances of this interface should register themselves at
 * the OSGi Service Registry. The MonitorAdmin listens to the registration of
 * Monitorable services, and makes the information they provide available also
 * through the Device Management Tree (DMT).
 * <p>
 * The monitorable service is identified by its PID string which must not
 * contain the Reserved characters described in 2.2 of RFC-2396 (URI Generic
 * Syntax). Also the length of the PID should be kept as small as possible. The
 * PID will be used as a node name in the DMT and certain DMT implementations
 * may have limits on node name length. The length limit is not specified in any
 * standard, it is recommended not to use names longer than 20 characters.
 * <p>
 * A Monitorable may optionally support sending notifications when the status of
 * its StatusVariables change.
 * <p>
 * Publishing StatusVariables requires the presence of the
 * <code>MonitorPermission</code> with the <code>publish</code> action
 * string. This permission, however, is not checked during registration of the
 * Monitorable service. Instead, the MonitorAdmin implemenatation must make sure
 * that when a StatusVariable is queried, it is shown only if the Monitorable is
 * authorized to publish the given StatusVariable.
 */
public interface Monitorable {
    /**
     * Returns the list of StatusVariable identifiers published by this
     * Monitorable. A StatusVariable name is unique within the scope of a
     * Monitorable. The array contains the elements in no particular order.
     * The returned value must not be <code>null</code>. 
     * 
     * @return the name of StatusVariables published by this object
     */
    public String[] getStatusVariableNames();
    
    /**
     * Returns the StatusVariable object addressed by its identifier. The
     * StatusVariable will hold the value taken at the time of this method call.
     * 
     * @param id the identifier of the StatusVariable. The identifier does not
     *        contain the Monitorable_id, i.e. this is the name and not the path
     *        of the status variable.
     * @return the StatusVariable object
     * @throws IllegalArgumentException if the name points to a non existing
     *         StatusVariable
     */
    public StatusVariable getStatusVariable(String id)
            throws IllegalArgumentException;

    /**
     * Tells whether the StatusVariable provider is able to send instant
     * notifications when the given StatusVariable changes. If the Monitorable
     * supports sending change updates it must notify the MonitorListener when
     * the value of the StatusVariable changes. The Monitorable finds the
     * MonitorListener service through the Service Registry.
     * 
     * @param id the identifier of the StatusVariable. The identifier
     * does not contain the Monitorable_id, i.e. this is the name and
     * not the path of the status variable.
     * @return <code>true</code> if the Monitorable can send notification when
     *         the given StatusVariable chages, <code>false</code> otherwise
     * @throws IllegalArgumentException
     *             if the path is invalid or points to a non existing
     *             StatusVariable
     */
    public boolean notifiesOnChange(String id) throws IllegalArgumentException;

    /**
     * Issues a request to reset a given StatusVariable. Depending on the
     * semantics of the StatusVariable this call may or may not succeed: it
     * makes sense to reset a counter to its starting value, but e.g. a
     * StatusVariable of type String might not have a meaningful default value.
     * Note that for numeric StatusVariables the starting value may not
     * necessarily be 0. Resetting a StatusVariable triggers a monitor event.
     * 
     * @param id the identifier of the StatusVariable.
     * @return <code>true</code> if the Monitorable could successfully reset
     *         the given StatusVariable, <code>false</code> otherwise
     * @throws IllegalArgumentException if the id points to a non existing
     *         StatusVariable
     */
    public boolean resetStatusVariable(String id)
            throws IllegalArgumentException;
    
    /**
     * Returns a human readable description of a StatusVariable. This can be
     * used by management systems on their GUI. Null return value is allowed.
     * 
     * @param id Identifier of a StatusVariable published by this Monitorable
     * @return the human readable description of this StatusVariable or null if
     *         it is not set
     * @throws IllegalArgumentException if the path points to a non existing
     *         StatusVariable
     */
    public String getDescription(String id) throws IllegalArgumentException;
}
