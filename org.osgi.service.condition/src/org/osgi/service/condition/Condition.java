/*
 * Copyright (c) OSGi Alliance (2010, 2013). All Rights Reserved.
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

package org.osgi.service.condition;

import org.osgi.annotation.versioning.ConsumerType;

/**
 * A condition service.
 * 
 * @ThreadSafe
 * @author $Id$
 */
@ConsumerType
public interface Condition {
	/**
	 * Service property identifying a condition's unique identifier.
	 */
	// TODO what are the valid values of the service property? only String or
	// any Object?
	public static final String		CONDITION_ID									= "osgi.condition.id";
	/**
	 * The unique identifier for the TRUE Condition. The TRUE condition is
	 * registered by the framework and therefore can always be relied upon.
	 */
	// TODO why is this not Boolean.TRUE?
	public static final String		CONDITION_ID_TRUE								= "true";

	/**
	 * The condition factory PID. The factory PID used to configure conditions
	 * with the condition factory.
	 */
	public static final String		CONDITION_FACTORY_PID							= "osgi.condition.factory";

	/**
	 * A condition factory configuration property identifying the condition's
	 * unique identifier that will be used to register the condition when all
	 * the match filters are satisfied.
	 * 
	 * @see #CONDITION_FACTORY_CONFIGURE_MATCH_ALL
	 * @see #CONDITION_FACTORY_CONFIGURE_MATCH_NONE
	 */
	public static final String		CONDITION_FACTORY_CONFIGURE_ID					= "osgi.condition.configure.id";
	/**
	 * A condition factory configuration property prefix identifying a service
	 * property of the condition. The service property key is the remaining
	 * content of the configuration property after the prefix. For example, a
	 * configuration that contains
	 * {@code osgi.condition.configure.properties.prop1=value1} results in a
	 * service property {@code prop1=value1} when the condition is registered.
	 */
	public static final String		CONDITION_FACTORY_CONFIGURE_PROPERTIES_PREFIX	= "osgi.condition.configure.properties.";
	/**
	 * A condition factory configuration property identifying a list of target
	 * filters for the condition. The condition may be registered when each
	 * filter matches at least one service registration. The value of this
	 * configuration property must be of type {@code Collection<String>}.
	 */
	public static final String		CONDITION_FACTORY_CONFIGURE_MATCH_ALL			= "osgi.condition.configure.match.all";
	/**
	 * A condition factory configuration property identifying a list of target
	 * filters for the condition. The condition may not be registered if any of
	 * the target filters match at least one service registration.The value of
	 * this configuration property must be of type {@code Collection<String>}.
	 */
	public static final String		CONDITION_FACTORY_CONFIGURE_MATCH_NONE			= "osgi.condition.configure.match.none";

	/**
	 * A singleton condition instance that can be used to register multiple
	 * Conditions.
	 */
	public static final Condition	INSTANCE										= new ConditionImpl();

	/**
	 * A default implementation of a condition.
	 */
	static class ConditionImpl implements Condition {
		// nothing to implement
	}
}
