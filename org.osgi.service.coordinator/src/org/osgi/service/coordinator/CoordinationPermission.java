/*
 * Copyright (c) OSGi Alliance (2010). All Rights Reserved.
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
package org.osgi.service.coordinator;

import java.security.BasicPermission;

import org.osgi.framework.Bundle;

/**
 * The name parameter of the Permission is a filter expression. It asserts the
 * bundle that is associated with the coordination. Additionally, the following
 * attributes can be asserted:
 * <ol>
 * <li>coordination.name - The name of the coordination</li>
 * </ol>
 * 
 * <pre>
 *   Coordinator                          INITIATE    PARTICIPATE   ADMIN   NONE
 *     alwaysFail(String)                                                    -
 *     begin(String)                          +                              -
 *     getCoordinations()                                             +      -
 *     isActive()                             +            +          +      -
 *     isFailed()                             +            +          +      -
 *     participate(Participant)                            +                 -
 *     participateOrBegin(Participant)        +    and     +                 -
 *     
 *   Coordination
 *     end()                                                                 +
 *     fail(String)                                                          +
 *     getName()                                                             +
 *     getParticipants()                                              +      -
 *     isFailed()                                                            +
 *     setTimeout(long)                       +                       +      -
 *     terminate()                                                           +
 * </pre>
 * 
 * ### Need to complete the implementation!
 */
public class CoordinationPermission extends BasicPermission {

	private static final long	serialVersionUID	= 1L;

	final Bundle				bundle;

	/**
	 * Initiate a Coordination. An owner of this permission can initiate, end,
	 * fail, and terminate a Coordination.
	 */
	public final static String	INITIATE			= "initiate";
	/**
	 * The action string {@code admin}.
	 */
	public final static String	ADMIN				= "admin";
	/**
	 * The action string {@code participate}.
	 */
	public final static String	PARTICIPATE			= "participate";

	/**
	 * The name parameter specifies a filter condition. The filter asserts the
	 * bundle that initiated the Coordination. An implicit grant is made for a
	 * bundle's own coordinations.
	 * 
	 * @param filterExpression A filter expression asserting the bundle associated with the
	 *        coordination.
	 * @param actions A comma separated combination of {@link #INITIATE},
	 *        {@link #ADMIN}, {@link #PARTICIPATE}.
	 */
	public CoordinationPermission(String filterExpression, String actions) {
		super(filterExpression, actions);
		this.bundle = null;
	}

	/**
	 * The verification permission
	 * 
	 * @param bundle The bundle that will be the target of the filter
	 *        expression.
	 * @param coordinationName The name of the coordination or {@code null}
	 * @param actions The set of actions required, which is a combination of
	 *        {@link #INITIATE}, {@link #ADMIN}, {@link #PARTICIPATE}.
	 */
	public CoordinationPermission(Bundle bundle, String coordinationName, String actions) {
		super(coordinationName, actions);
		this.bundle = bundle;
	}


	// TODO For BJ to have lots of fun!


}
