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

/**
 * Defines standard names for {@code DmtAdmin}.
 * 
 * @author $Id$
 * @since 2.0
 */
public class DmtConstants {

	private DmtConstants() {
		// non-instantiable
	}

	/**
	 * A string defining a DDF URI, indicating that the node is a LIST node.
	 */
	public static final String	DDF_LIST					= "org.osgi/1.0/LIST";

	/**
	 * A string defining a DDF URI, indicating that the node is a SCAFFOLD node.
	 */
	public static final String	DDF_SCAFFOLD				= "org.osgi/1.0/SCAFFOLD";

	/**
	 * A string defining a DDF URI, indicating that the node is a MAP node.
	 */
	public static final String	DDF_MAP						= "org.osgi/1.0/MAP";

	/**
	 * A string defining the topic for the event that is sent for added nodes.
	 */
	public static final String	EVENT_TOPIC_ADDED			= "org/osgi/service/dmt/DmtEvent/ADDED";

	/**
	 * A string defining the topic for the event that is sent for deleted nodes.
	 */
	public static final String	EVENT_TOPIC_DELETED			= "org/osgi/service/dmt/DmtEvent/DELETED";

	/**
	 * A string defining the topic for the event that is sent for replaced
	 * nodes.
	 */
	public static final String	EVENT_TOPIC_REPLACED		= "org/osgi/service/dmt/DmtEvent/REPLACED";

	/**
	 * A string defining the topic for the event that is sent for renamed nodes.
	 */
	public static final String	EVENT_TOPIC_RENAMED			= "org/osgi/service/dmt/DmtEvent/RENAMED";

	/**
	 * A string defining the topic for the event that is sent for copied nodes.
	 */
	public static final String	EVENT_TOPIC_COPIED			= "org/osgi/service/dmt/DmtEvent/COPIED";

	/**
	 * A string defining the topic for the event that is sent for a newly opened
	 * session.
	 */
	public static final String	EVENT_TOPIC_SESSION_OPENED	= "org/osgi/service/dmt/DmtEvent/SESSION_OPENED";

	/**
	 * A string defining the topic for the event that is sent for a closed
	 * session.
	 */
	public static final String	EVENT_TOPIC_SESSION_CLOSED	= "org/osgi/service/dmt/DmtEvent/SESSION_CLOSED";

	/**
	 * A string defining the property key for the {@code session.id} property in
	 * node related events.
	 */
	public static final String	EVENT_PROPERTY_SESSION_ID	= "session.id";

	/**
	 * A string defining the property key for the @{code nodes} property in node
	 * related events.
	 */
	public static final String	EVENT_PROPERTY_NODES		= "nodes";

	/**
	 * A string defining the property key for the {@code newnodes} property in
	 * node related events.
	 */
	public static final String	EVENT_PROPERTY_NEW_NODES	= "newnodes";

}
