/*******************************************************************************
 * Copyright (c) Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0 
 *******************************************************************************/
package org.osgi.service.conditionfactory;

/**
 * Defines standard names for Condition Factory configurations.
 * 
 * @ThreadSafe
 * @author $Id$
 */
public final class ConditionFactoryConstants {
	private ConditionFactoryConstants() {
	}

	/**
	 * The PID for the condition factory. The PID used to configure conditions
	 * with the condition factory.
	 */
	public static final String	CONDITIONFACTORY_PID				= "osgi.conditionfactory";

	/**
	 * A condition factory configuration property identifying the condition's
	 * unique identifier that will be used to register the condition when all
	 * the match filters are satisfied.
	 * 
	 * @see #CONDITIONFACTORY_MATCH_ALL
	 * @see #CONDITIONFACTORY_MATCH_NONE
	 */
	public static final String	CONDITIONFACTORY_CONFIGURE_ID		= "configure.id";

	/**
	 * A condition factory configuration property prefix identifying a service
	 * property of the condition. The service property key is the remaining
	 * content of the configuration property after the prefix. For example, a
	 * configuration that contains
	 * {@code osgi.condition.configure.properties.prop1=value1} results in a
	 * service property {@code prop1=value1} when the condition is registered.
	 */
	public static final String	CONDITIONFACTORY_PROPERTIES_PREFIX	= "condition.properties.";

	/**
	 * A condition factory configuration property identifying a list of target
	 * filters for the condition. The condition may be registered when each
	 * filter matches at least one service registration. The value of this
	 * configuration property must be of type {@code Collection<String>}.
	 */
	public static final String	CONDITIONFACTORY_MATCH_ALL			= "match.all";

	/**
	 * A condition factory configuration property identifying a list of target
	 * filters for the condition. The condition may not be registered if any of
	 * the target filters match at least one service registration. The value of
	 * this configuration property must be of type {@code Collection<String>}.
	 */
	public static final String	CONDITIONFACTORY_MATCH_NONE			= "match.none";

}
