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

package org.osgi.service.dmt;

import org.osgi.service.dmt.security.DmtPermission;

/**
 * Registered implementations of this class are notified via {@link DmtEvent}
 * objects about important changes in the tree. Events are generated after every
 * successful DMT change, and also when sessions are opened or closed. If a
 * {@link DmtSession} is opened in atomic mode, DMT events are only sent when
 * the session is committed, when the changes are actually performed.
 * <p>
 * Dmt Event Listener services must have permission {@link DmtPermission#GET}
 * for the nodes in the {@code nodes} and {@code newNodes} property in the Dmt
 * Event.
 * 
 * @author $Id$
 */
public interface DmtEventListener {

	/**
	 * A number of sub-tree top nodes that define the scope of the Dmt Event
	 * Listener. If this service property is registered then the service must
	 * only receive events for nodes that are part of one of the sub-trees. The
	 * type of this service property is {@code String+}.
	 */
	String	FILTER_SUBTREE		= "osgi.filter.subtree";

	/**
	 * A number of names of principals. If this service property is provided
	 * with a Dmt Event Listener service registration than that listener must
	 * only receive events for which at least one of the given principals has
	 * {@code Get} rights. The type of this service property is {@code String+}.
	 */
	String	FILTER_PRINCIPAL	= "osgi.filter.principal";

	/**
	 * A number of event types packed in a bitmap. If this service property is
	 * provided with a Dmt Event Listener service registration than that
	 * listener must only receive events where one of the Dmt Event types occur
	 * in the bitmap. The type of this service property must be {@code Integer}.
	 */
	String	FILTER_EVENT		= "osgi.filter.event";

	/**
	 * {@code DmtAdmin} uses this method to notify the registered listeners
	 * about the change. This method is called asynchronously from the actual
	 * event occurrence.
	 * 
	 * @param event the {@code DmtEvent} describing the change in detail
	 */
	void changeOccurred(DmtEvent event);
}
