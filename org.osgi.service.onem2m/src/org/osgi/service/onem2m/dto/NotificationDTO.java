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

package org.osgi.service.onem2m.dto;

import org.osgi.dto.DTO;

/**
 * DTO expresses Notification.
 * 
 * @see <a href=
 *      "http://www.onem2m.org/images/files/deliverables/Release3/TS-0004_Service_Layer_Core_Protocol_V3_11_2.pdf">oneM2M
 *      TS-0004 6.3.5.13</a>
 * @NotThreadSafe
 */
public class NotificationDTO extends DTO {
	/**
	 * Notification Event
	 */
	public NotificationEventDTO		notificationEvent;

	/**
	 * Flag showing verification request. This field is optional.
	 */
	public Boolean					verificationRequest;

	/**
	 * Flag showing subscription deletion This field is optional.
	 */
	public Boolean					subscriptionDeletion;

	/**
	 * URI referring subscription resource.
	 */
	public String					subscriptionReference;

	/**
	 * creator
	 */
	public String					creator;

	/**
	 * notification forwarding URI
	 */
	public String					notificationForwardingURI;

	/**
	 * ID for notification target
	 */
	public String					notificationTarget;

	/**
	 * IPE Discovery Request.
	 */
	public IPEDiscoveryRequestDTO	ipeDiscoveryRequest;

	/**
	 * AE Registration Point Change
	 * 
	 * @see <a href=
	 *      "http://www.onem2m.org/images/files/deliverables/Release3/TS-0004_Service_Layer_Core_Protocol_V3_11_2.pdf">oneM2M
	 *      TS-0004 7.5.1.2.16</a>
	 */
	public Boolean					aeRegistrationPointChange;

	/**
	 * aeReferenceIDChange element
	 * 
	 * @see <a href=
	 *      "http://www.onem2m.org/images/files/deliverables/Release3/TS-0004_Service_Layer_Core_Protocol_V3_11_2.pdf">oneM2M
	 *      TS-0004 7.5.1.2.17</a>
	 */
	public Boolean					aeReferenceIDChange;

	/**
	 * tracking ID 1
	 * 
	 * @see <a href=
	 *      "http://www.onem2m.org/images/files/deliverables/Release3/TS-0004_Service_Layer_Core_Protocol_V3_11_2.pdf">oneM2M
	 *      TS-0004 7.5.1.2.16</a>
	 * @see <a href=
	 *      "http://www.onem2m.org/images/files/deliverables/Release3/TS-0004_Service_Layer_Core_Protocol_V3_11_2.pdf">oneM2M
	 *      TS-0004 7.5.1.2.17</a>
	 */
	public String					trackingID1;

	/**
	 * tracking ID 1
	 * 
	 * @see <a href=
	 *      "http://www.onem2m.org/images/files/deliverables/Release3/TS-0004_Service_Layer_Core_Protocol_V3_11_2.pdf">oneM2M
	 *      TS-0004 7.5.1.2.16</a>
	 * @see <a href=
	 *      "http://www.onem2m.org/images/files/deliverables/Release3/TS-0004_Service_Layer_Core_Protocol_V3_11_2.pdf">oneM2M
	 *      TS-0004 7.5.1.2.17</a>
	 */
	public String					trackingID2;

}
