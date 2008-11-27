/*
 * Copyright (c) OSGi Alliance (2008). All Rights Reserved.
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

package org.osgi.service.discovery;

import java.util.Map;

/**
 * This interface describes publication of a service via Discovery. It offers
 * various functionality regarding the published service information.
 * 
 * @version $Revision: 5970 $
 */
public interface ServicePublication {
	/**
	 * Returns the published service meta-data.
	 * 
	 * @return published service information. This object becomes stale when
	 *         ServicePublication is updated.
	 */
	ServiceEndpointDescription getServiceEndpointDescription();

	/**
	 * Updates a previously published service publication. <br>
	 * Depending on Discovery's implementation and underlying protocol it may
	 * result in an update or new re-publication of the service.
	 * 
	 * This operation also replaces the managed
	 * <code>ServiceEndpointDescription</code> object with a new one.
	 * 
	 * @param newProperties
	 *            new set of service endpoint properties. It may be null.
	 */
	void updateService(Map/* <String, Object> */newProperties);

	/**
	 * Make the previously published service un-discoverable. The previous
	 * service publish operation is undone.
	 */
	void unpublishService();
}
