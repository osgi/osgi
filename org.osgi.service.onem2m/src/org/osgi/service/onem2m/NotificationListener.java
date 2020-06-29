/*
 * Copyright (c) OSGi Alliance (2019, 2020). All Rights Reserved.
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

package org.osgi.service.onem2m;

import org.osgi.annotation.versioning.ConsumerType;
import org.osgi.service.onem2m.dto.RequestPrimitiveDTO;

/**
 * Interface to receive notification from other oneM2M entities.
 * <p>
 * Application that receives notification must implement this interface and
 * register to OSGi service registry. No service property is required.
 */
@ConsumerType
public interface NotificationListener {
	/**
	 * receive notification.
	 *
	 * @param request request primitive
	 */
	public void notified(RequestPrimitiveDTO request);

}
