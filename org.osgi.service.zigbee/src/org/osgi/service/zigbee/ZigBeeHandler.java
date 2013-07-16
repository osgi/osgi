/*
 * Copyright (c) OSGi Alliance (2013). All Rights Reserved.
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

package org.osgi.service.zigbee;

import java.util.Map;

/**
 * ZigBeeHandler manages response of a request to the Base Driver
 * 
 * @version 1.0
 */

public interface ZigBeeHandler {
	/**
	 * Notifies the request response
	 * 
	 * @param status request response status : SUCCESS or FAILURE
	 * @param response type is Map<int, Object>.
	 */
	public void notifyResponse(short status, Map response);
}
