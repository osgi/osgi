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

package org.osgi.service.onem2m.dto;

import java.util.Map;

/**
 * DTO expresses NotificationEventDTO
 * <p>
 * This data structure is held in NotificationDTO.
 * 
 * @see <a href=
 *      "http://www.onem2m.org/images/files/deliverables/Release3/TS-0004_Service_Layer_Core_Protocol_V3_11_2.pdf">oneM2M
 *      TS-0004 6.3.5.13</a>
 * @NotThreadSafe
 */
public class NotificationEventDTO {
	/**
	 * m2m:representation
	 * 
	 * @see <a href=
	 *      "http://www.onem2m.org/images/files/deliverables/Release3/TS-0004_Service_Layer_Core_Protocol_V3_11_2.pdf">oneM2M
	 *      TS-0004 6.3.5.62</a>
	 */
	public Object					representation;

	/**
	 * operationMonitor
	 * 
	 * @see <a href=
	 *      "http://www.onem2m.org/images/files/deliverables/Release3/TS-0004_Service_Layer_Core_Protocol_V3_11_2.pdf">oneM2M
	 *      TS-0004 6.3.5.57</a>
	 */
	public Map<String,Object>		operationMonitor;

	/**
	 * notificationEventType
	 * 
	 * @see <a href=
	 *      "http://www.onem2m.org/images/files/deliverables/Release3/TS-0004_Service_Layer_Core_Protocol_V3_11_2.pdf">oneM2M
	 *      TS-0004 6.3.4.2.19</a>
	 */
	public NotificationEventType	notificationEventType;

	/**
	 * NotificationEventType
	 * 
	 * @see <a href=
	 *      "http://www.onem2m.org/images/files/deliverables/Release3/TS-0004_Service_Layer_Core_Protocol_V3_11_2.pdf">oneM2M
	 *      TS-0004 6.3.4.2.19 </a>
	 */
	public enum NotificationEventType {
		/**
		 * update_of_resouce. This is the default value.
		 */
		update_of_resource(1),
		/**
		 * delete_of_resource
		 */
		delete_of_resource(2),
		/**
		 * create_of_direct_child_resource
		 */
		create_of_direct_child_resource(3),
		/**
		 * create_of_direct_child_resouce
		 */
		delete_of_direct_child_resouce(4),
		/**
		 * retrieve_of_container_resource_with_no_child_resource
		 */
		retrieve_of_container_resource_with_no_child_resource(5);

		private final int value;

		private NotificationEventType(int i) {
			value = i;
		}

		/**
		 * Return notification type value.
		 * 
		 * @return The notification type value.
		 */
		public int getValue() {
			return value;
		}
	}
}
