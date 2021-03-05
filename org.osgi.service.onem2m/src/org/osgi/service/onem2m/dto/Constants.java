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

/**
 * This class defines constants for resource types.
 * 
 * @see <a href=
 *      "http://www.onem2m.org/images/files/deliverables/Release3/TS-0004_Service_Layer_Core_Protocol_V3_11_2.pdf">oneM2M
 *      TS-0004 6.3.4.2.1</a>
 */
public final class Constants {
	private Constants() {
	}

	/**
	 * resource type for accessControlPolicy
	 */
	final public static int	RT_accessControlPolicy					= 1;

	/**
	 * resource type for AE
	 */
	final public static int	RT_AE									= 2;

	/**
	 * resource type for container
	 */
	final public static int	RT_container							= 3;

	/**
	 * resource type for contentInstance
	 */
	final public static int	RT_contentInstance						= 4;

	/**
	 * resource type for CSEBase
	 */
	final public static int	RT_CSEBase								= 5;

	/**
	 * resource type for delivery
	 */
	final public static int	RT_delivery								= 6;

	/**
	 * resource type for eventConfig
	 */
	final public static int	RT_eventConfig							= 7;

	/**
	 * resource type for execInstance
	 */
	final public static int	RT_execInstance							= 8;

	/**
	 * resource type for group
	 */
	final public static int	RT_group								= 9;

	/**
	 * resource type for locationPolicy
	 */
	final public static int	RT_locationPolicy						= 10;

	/**
	 * resource type for m2mServiceSubscriptionProfile
	 */
	final public static int	RT_m2mServiceSubscriptionProfile		= 11;

	/**
	 * resource type for mgmtCmd
	 */
	final public static int	RT_mgmtCmd								= 12;

	/**
	 * resource type for mgmtObj
	 */
	final public static int	RT_mgmtObj								= 13;

	/**
	 * resource type for node
	 */
	final public static int	RT_node									= 14;

	/**
	 * resource type for pollingChannel
	 */
	final public static int	RT_pollingChannel						= 15;
	/**
	 * resource type for remoteCSE
	 */
	final public static int	RT_remoteCSE							= 16;

	/**
	 * resource type for request
	 */
	final public static int	RT_request								= 17;

	/**
	 * resource type for schedule
	 */
	final public static int	RT_schedule								= 18;
	/**
	 * resource type for serviceSubscribedAppRule
	 */
	final public static int	RT_serviceSubscribedAppRule				= 19;
	/**
	 * resource type for serviceSubscribedNode
	 */
	final public static int	RT_serviceSubscribedNode				= 20;
	/**
	 * resource type for statsCollect
	 */
	final public static int	RT_statsCollect							= 21;

	/**
	 * resource type for
	 */
	final public static int	RT_statsConfig							= 22;

	/**
	 * resource type for statsConfig
	 */
	final public static int	RT_subscription							= 23;

	/**
	 * resource type for semanticDescriptor
	 */
	final public static int	RT_semanticDescriptor					= 24;

	/**
	 * resource type for notificationTargetMgmtPolicyRef
	 */
	final public static int	RT_notificationTargetMgmtPolicyRef		= 25;

	/**
	 * resource type for notificationTargetPolicy
	 */
	final public static int	RT_notificationTargetPolicy				= 26;

	/**
	 * resource type for policyDeletionRules
	 */
	final public static int	RT_policyDeletionRules					= 27;

	/**
	 * resource type for flexContainer
	 */
	final public static int	RT_flexContainer						= 28;

	/**
	 * resource type for timeSeries
	 */
	final public static int	RT_timeSeries							= 29;

	/**
	 * resource type for timeSeriesInstance
	 */
	final public static int	RT_timeSeriesInstance					= 30;

	/**
	 * resource type for role
	 */
	final public static int	RT_role									= 31;

	/**
	 * resource type for token
	 */
	final public static int	RT_token								= 32;

	// final public static int RT_void = 33;

	/**
	 * resource type for dynamicAuthorizationConsultation
	 */
	final public static int	RT_dynamicAuthorizationConsultation		= 34;

	/**
	 * resource type for authorizationDecision
	 */
	final public static int	RT_authorizationDecision				= 35;

	/**
	 * resource type for authorizationPolicy
	 */
	final public static int	RT_authorizationPolicy					= 36;

	/**
	 * resource type for authorizationInformation
	 */
	final public static int	RT_authorizationInformation				= 37;

	/**
	 * resource type for ontologyRepository
	 */
	final public static int	RT_ontologyRepository					= 38;

	/**
	 * resource type for ontology
	 */
	final public static int	RT_ontology								= 39;

	/**
	 * resource type for semanticMashupJobProfile
	 */
	final public static int	RT_semanticMashupJobProfile				= 40;

	/**
	 * resource type for semanticMashupInstance
	 */
	final public static int	RT_semanticMashupInstance				= 41;

	/**
	 * resource type for semanticMashupResult
	 */
	final public static int	RT_semanticMashupResult					= 42;

	/**
	 * resource type for AEContactList
	 */
	final public static int	RT_AEContactList						= 43;

	/**
	 * resource type for AEContactListPerCSE
	 */
	final public static int	RT_AEContactListPerCSE					= 44;

	/**
	 * resource type for localMulticastGroup
	 */
	final public static int	RT_localMulticastGroup					= 45;

	/**
	 * resource type for multimediaSession
	 */
	final public static int	RT_multimediaSession					= 46;

	/**
	 * resource type for triggerRequest
	 */
	final public static int	RT_triggerRequest						= 47;

	/**
	 * resource type for crossResourceSubscription
	 */
	final public static int	RT_crossResourceSubscription			= 48;

	/**
	 * resource type for backgroundDataTransfer
	 */
	final public static int	RT_backgroundDataTransfer				= 49;

	/**
	 * resource type for transactionMgmt
	 */
	final public static int	RT_transactionMgmt						= 50;

	/**
	 * resource type for transaction
	 */
	final public static int	RT_transaction							= 51;

	/**
	 * resource type for accessControlPolicyAnnc
	 */
	final public static int	RT_accessControlPolicyAnnc				= 10001;

	/**
	 * resource type for AEAnnc
	 */
	final public static int	RT_AEAnnc								= 10002;

	/**
	 * resource type for containerAnnc
	 */
	final public static int	RT_containerAnnc						= 10003;

	/**
	 * resource type for contentInstanceAnnc
	 */
	final public static int	RT_contentInstanceAnnc					= 10004;

	/**
	 * resource type for groupAnnc
	 */
	final public static int	RT_groupAnnc							= 10009;

	/**
	 * resource type for locationPolicyAnnc
	 */
	final public static int	RT_locationPolicyAnnc					= 10010;

	/**
	 * resource type for mgmtObjAnnc
	 */
	final public static int	RT_mgmtObjAnnc							= 10013;

	/**
	 * resource type for nodeAnnc
	 */
	final public static int	RT_nodeAnnc								= 10014;

	/**
	 * resource type for remoteCSEAnnc
	 */
	final public static int	RT_remoteCSEAnnc						= 10016;

	/**
	 * resource type for scheduleAnnc
	 */
	final public static int	RT_scheduleAnnc							= 10018;

	/**
	 * resource type for semanticDescriptorAnnc
	 */
	final public static int	RT_semanticDescriptorAnnc				= 10024;

	/**
	 * resource type for flexContainerAnnc
	 */
	final public static int	RT_flexContainerAnnc					= 10028;

	/**
	 * resource type for timeSeriesAnnc
	 */
	final public static int	RT_timeSeriesAnnc						= 10029;

	/**
	 * resource type for timeSeriesInstanceAnnc
	 */
	final public static int	RT_timeSeriesInstanceAnnc				= 10030;

	// final public static int RT_void = 10033;

	/**
	 * resource type for dynamicAuthorizationConsultationAnnc
	 */
	final public static int	RT_dynamicAuthorizationConsultationAnnc	= 10034;

	/**
	 * resource type for ontologyRepositoryAnnc
	 */
	final public static int	RT_ontologyRepositoryAnnc				= 10038;

	/**
	 * resource type for ontologyAnnc
	 */
	final public static int	RT_ontologyAnnc							= 10039;

	/**
	 * resource type for semanticMashupJobProfileAnnc
	 */
	final public static int	RT_semanticMashupJobProfileAnnc			= 10040;

	/**
	 * resource type for semanticMashupInstanceAnnc
	 */
	final public static int	RT_semanticMashupInstanceAnnc			= 10041;

	/**
	 * resource type for semanticMashupResultAnnc
	 */
	final public static int	RT_semanticMashupResultAnnc				= 10042;

	/**
	 * resource type for multimediaSessionAnnc
	 */
	final public static int	RT_multimediaSessionAnnc				= 10046;

}
