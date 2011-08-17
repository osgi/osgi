/*
 * Copyright (c) OSGi Alliance (2011). All Rights Reserved.
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

package org.osgi.service.tr069todmt;

import org.osgi.service.dmt.*;

/**
 * A service that can create TR069 Connector
 */
public interface TR069Factory {
	/**
	 * Create a TR069 connector based on the given session .
	 * 
	 * @param session
	 *            The session to use for the adaption. This session must not be
	 *            closed before the TR069 connector is closed.
	 * @return A new TR069 Connector bound to the given session
	 */

	TR069Connector create(DmtSession session);
	
}
