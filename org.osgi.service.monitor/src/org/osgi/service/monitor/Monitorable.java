/*
 * Copyright (c) OSGi Alliance (2004, 2010). All Rights Reserved.
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
 * A {@code Monitorable} can provide information about itself in the form
 * of {@code StatusVariables}. Instances of this interface should register
 * themselves at the OSGi Service Registry. The {@code MonitorAdmin}
 * listens to the registration of {@code Monitorable} services, and makes
 * the information they provide available also through the Device Management
 * Tree (DMT) for remote access.
 * <p>
 * The monitorable service is identified by its PID string which must be a non-
 * {@code null}, non-empty string that conforms to the "symbolic-name"
 * definition in the OSGi core specification. This means that only the
 * characters [-_.a-zA-Z0-9] may be used. The length of the PID must not exceed
 * 20 characters.
 * <p>
 * A {@code Monitorable} may optionally support sending notifications when
 * the status of its {@code StatusVariables} change. Support for change
 * notifications can be defined per {@code StatusVariable}.
 * <p>
 * Publishing {@code StatusVariables} requires the presence of the
 * {@code MonitorPermission} with the {@code publish} action string.
 * This permission, however, is not checked during registration of the
 * {@code Monitorable} service. Instead, the {@code MonitorAdmin}
 * implementation must make sure that when a {@code StatusVariable} is
 * queried, it is shown only if the {@code Monitorable} is authorized to
 * publish the given {@code StatusVariable}.
 * 
 * @version $Id$
 */
public interface Monitorable {
	/**
	 * Returns the list of {@code StatusVariable} identifiers published by this
	 * {@code Monitorable}. A {@code StatusVariable} name is unique within the
	 * scope of a {@code Monitorable}. The array contains the elements in no
	 * particular order. The returned value must not be {@code null}.
	 * 
	 * @return the {@code StatusVariable} identifiers published by this object,
	 *         or an empty array if none are published
	 */
    public String[] getStatusVariableNames();
    
    /**
     * Returns the {@code StatusVariable} object addressed by its
     * identifier. The {@code StatusVariable} will hold the value taken
     * at the time of this method call.
     * <p>
     * The given identifier does not contain the Monitorable PID, i.e. it 
     * specifies the name and not the path of the Status Variable.
     * 
     * @param id the identifier of the {@code StatusVariable}, cannot be
     *        {@code null} 
     * @return the {@code StatusVariable} object
     * @throws java.lang.IllegalArgumentException if {@code id} points to a
     *         non-existing {@code StatusVariable}
     */
    public StatusVariable getStatusVariable(String id)
            throws IllegalArgumentException;

    /**
     * Tells whether the {@code StatusVariable} provider is able to send
     * instant notifications when the given {@code StatusVariable}
     * changes. If the {@code Monitorable} supports sending change
     * updates it must notify the {@code MonitorListener} when the value
     * of the {@code StatusVariable} changes. The
     * {@code Monitorable} finds the {@code MonitorListener}
     * service through the Service Registry.
     * <p>
     * The given identifier does not contain the Monitorable PID, i.e. it 
     * specifies the name and not the path of the Status Variable.
     * 
     * @param id the identifier of the {@code StatusVariable}, cannot be
     *        {@code null} 
     * @return {@code true} if the {@code Monitorable} can send
     *         notification when the given {@code StatusVariable}
     *         changes, {@code false} otherwise
     * @throws java.lang.IllegalArgumentException if {@code id} points to a
     *         non-existing {@code StatusVariable}
     */
    public boolean notifiesOnChange(String id) throws IllegalArgumentException;

    /**
     * Issues a request to reset a given {@code StatusVariable}.
     * Depending on the semantics of the actual Status Variable this call may or
     * may not succeed: it makes sense to reset a counter to its starting value,
     * but for example a {@code StatusVariable} of type {@code String}
     * might not have a meaningful default value. Note that for numeric
     * {@code StatusVariables} the starting value may not necessarily be
     * 0. Resetting a {@code StatusVariable} must trigger a monitor event.
     * <p>
     * The given identifier does not contain the Monitorable PID, i.e. it 
     * specifies the name and not the path of the Status Variable.
     * 
     * @param id the identifier of the {@code StatusVariable}, cannot be
     *        {@code null} 
     * @return {@code true} if the {@code Monitorable} could
     *         successfully reset the given {@code StatusVariable},
     *         {@code false} otherwise
     * @throws java.lang.IllegalArgumentException if {@code id} points to a
     *         non-existing {@code StatusVariable}
     */
    public boolean resetStatusVariable(String id)
            throws IllegalArgumentException;
    
    /**
     * Returns a human readable description of a {@code StatusVariable}.
     * This can be used by management systems on their GUI. The 
     * {@code null} return value is allowed if there is no description for
     * the specified Status Variable.
     * <p>
     * The given identifier does not contain the Monitorable PID, i.e. it 
     * specifies the name and not the path of the Status Variable.
     * 
     * @param id the identifier of the {@code StatusVariable}, cannot be
     *        {@code null} 
     * @return the human readable description of this
     *         {@code StatusVariable} or {@code null} if it is not
     *         set
     * @throws java.lang.IllegalArgumentException if {@code id} points to a
     *         non-existing {@code StatusVariable}
     */
    public String getDescription(String id) throws IllegalArgumentException;
}
