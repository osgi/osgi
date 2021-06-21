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

package org.osgi.service.tr069todmt;

import org.osgi.service.dmt.DmtSession;

/**
 * A service that can create TR069 Connector
 */
public interface TR069ConnectorFactory {

	/**
	 * Create a TR069 connector based on the given session .
	 * <p>
	 * The session must be an atomic session when objects are added and/or
	 * parameters are going to be set, otherwise it can be a read only or
	 * exclusive session. Due to the lazy creation nature of the TR069 Connector
	 * it is possible that a node must be created in a read method after a node
	 * has been added, it is therefore necessary to always provide an atomic
	 * session when an ACS session requires modifying parameters.
	 * 
	 * @param session The session to use for the adaption. This session must not
	 *        be closed before the TR069 Connector is closed.
	 * @return A new TR069 Connector bound to the given session
	 */

	TR069Connector create(DmtSession session);

}
