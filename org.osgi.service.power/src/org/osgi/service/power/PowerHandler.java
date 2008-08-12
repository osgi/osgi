/*
 * $Date$
 * 
 * Copyright (c) OSGi Alliance (2005, 2007). All Rights Reserved.
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

package org.osgi.service.power;

/**
 * <p>
 * A service that has to be registered within the framework to be part of query
 * sequence when the {@link PowerManager} is moving to another system power
 * state.
 * 
 * @version $Revision$
 */
public interface PowerHandler {

	/**
	 * Calls by the {@link PowerManager} to request permission to system power
	 * state change to the given value.
	 * 
	 * @param state requested state.
	 * @return <code>true</code> if the change is accepted, <code>false</code>
	 *         otherwize.
	 */
	boolean handleQuery(int state);

	/**
	 * Called by the {@link PowerManager} to notify that the previous request
	 * change has been denied. Only Power Handlers previouly notified on
	 * {@link #handleQuery(int)} must be called on this method.
	 * 
	 * @param state rejected state.
	 */
	void handleQueryFailed(int state);

}
