/*
 * Copyright (c) OSGi Alliance (2020). All Rights Reserved.
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
 * In dynamic systems, such as OSGi, one of the more challenging problems can be
 * to define when a System or part of it is ready to do work. The answer can
 * change depending on the individual perspective. The developer of a Webserver
 * might say, the System is ready when the server starts listening to port 80.
 * An Application developer however would define the system as ready, when the
 * database connection is up and all Servlets are registered. Taking the
 * Application developers view, the Server should start Listening on Port 80
 * when the System is ready and not beforehand.
 * <p>
 * The <code>Condition</code> interface is designed to address this issue. Its
 * task is to provide a marker that can be tracked. It acts as a defined signal
 * to other services.
 * <p>
 * A Condition must be registered with the {@link Condition#CONDITION_ID} as
 * property.
 * 
 * @ThreadSafe
 * @author $Id$
 */
@ConsumerType
public interface Condition {

	/**
	 * Service property identifying a condition's unique identifier. As a
	 * Condition can potentially describe more then one state, the type of this
	 * service property is {@code String+}
	 */
	public static final String		CONDITION_ID		= "osgi.condition.id";

	/**
	 * The unique identifier for the TRUE Condition. The TRUE condition is
	 * registered by the framework and therefore can always be relied upon.
	 * 
	 * @see Condition#CONDITION_ID
	 */
	public static final String		CONDITION_ID_TRUE	= "true";

	/**
	 * A singleton condition instance that can be used to register condition
	 * services.
	 */
	public static final Condition	INSTANCE			= new Condition() {
															/* Nothing to do */};

}
