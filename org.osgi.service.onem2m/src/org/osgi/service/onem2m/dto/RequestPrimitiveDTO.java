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

import java.util.List;

import org.osgi.dto.DTO;
import org.osgi.service.onem2m.dto.RequestPrimitiveDTO.DesiredIdentiferResultType;

/**
 * DTO expresses Request Primitive.
 * 
 * @see <a href=
 *      "http://www.onem2m.org/images/files/deliverables/Release3/TS-0004_Service_Layer_Core_Protocol_V3_11_2.pdf">oneM2M
 *      TS-0004 6.4.1</a>
 * @see <a href=
 *      "https://git.onem2m.org/PRO/XSD/blob/master/v3_11_0/CDT-requestPrimitive-v3_11_0.xsd">oneM2M
 *      XSD requestPrimitive</a>
 * @NotThreadSafe
 */
public class RequestPrimitiveDTO extends DTO {
	/**
	 * Operation This field is mandatory.
	 * 
	 * @see <a href=
	 *      "http://www.onem2m.org/images/files/deliverables/Release3/TS-0004_Service_Layer_Core_Protocol_V3_11_2.pdf">oneM2M
	 *      TS-0004 6.3.4.2.5</a>
	 */
	public Operation					operation;

	/**
	 * To Parameter
	 */
	public String						to;

	/**
	 * From Parameter.
	 * <p>
	 * Originator of the request is stored.
	 * 
	 * @see <a href=
	 *      "http://www.onem2m.org/images/files/deliverables/Release3/TS-0004_Service_Layer_Core_Protocol_V3_11_2.pdf">oneM2M
	 *      TS-0004 6.3.4.2.5</a>
	 */
	public String						from;

	/**
	 * Request Identifier
	 *
	 * @see <a href=
	 *      "http://www.onem2m.org/images/files/deliverables/Release3/TS-0004_Service_Layer_Core_Protocol_V3_11_2.pdf">oneM2M
	 *      TS-0004 6.3.3</a>
	 */
	public String						requestIdentifier;

	/**
	 * Resource Type
	 *
	 * @see <a href=
	 *      "http://www.onem2m.org/images/files/deliverables/Release3/TS-0004_Service_Layer_Core_Protocol_V3_11_2.pdf">oneM2M
	 *      TS-0004 6.3.4.2.1</a>
	 */
	public Integer						resourceType;

	/**
	 * Primitive Content
	 * 
	 * @see <a href=
	 *      "http://www.onem2m.org/images/files/deliverables/Release3/TS-0004_Service_Layer_Core_Protocol_V3_11_2.pdf">oneM2M
	 *      TS-0004 6.3.5.5</a>
	 * @see <a href=
	 *      "http://www.onem2m.org/images/files/deliverables/Release3/TS-0004_Service_Layer_Core_Protocol_V3_11_2.pdf">oneM2M
	 *      TS-0004 7.2.1.1</a>
	 */
	public PrimitiveContentDTO			content;

	/**
	 * Role IDs
	 */
	public List<String>					roleIDs;

	/**
	 * Originating Timestamp
	 */
	public String						originatingTimestamp;

	/**
	 * Request Expiration Timestamp
	 * <p>
	 * * This parameter is related to CMDH(Communication Management and Delivery
	 * Handling) policy.
	 * 
	 * @see <a href=
	 *      "http://www.onem2m.org/images/files/deliverables/Release3/TS-0004_Service_Layer_Core_Protocol_V3_11_2.pdf">oneM2M
	 *      TS-0004 D.12</a>
	 */
	public String						requestExpirationTimestamp;

	/**
	 * Result Expiration Timestamp
	 * <p>
	 * This parameter is related to CMDH(Communication Management and Delivery
	 * Handling) policy.
	 * 
	 * @see <a href=
	 *      "http://www.onem2m.org/images/files/deliverables/Release3/TS-0004_Service_Layer_Core_Protocol_V3_11_2.pdf">oneM2M
	 *      TS-0004 D.12</a>
	 */
	public String						resultExpirationTimestamp;

	/**
	 * Operation Execution Time
	 */
	public String						operationExecutionTime;

	/**
	 * Response Type Info
	 *
	 * @see <a href=
	 *      "http://www.onem2m.org/images/files/deliverables/Release3/TS-0004_Service_Layer_Core_Protocol_V3_11_2.pdf">oneM2M
	 *      TS-0004 6.3.5.30</a>
	 */
	public ResponseTypeInfoDTO			responseType;

	/**
	 * Result Persistence
	 * <p>
	 * This parameter is related to CMDH(Communication Management and Delivery
	 * Handling) policy.
	 * 
	 * @see <a href=
	 *      "http://www.onem2m.org/images/files/deliverables/Release3/TS-0004_Service_Layer_Core_Protocol_V3_11_2.pdf">oneM2M
	 *      TS-0004 D.12</a>
	 */
	public String						resultPersistence;

	/**
	 * Result Content
	 * 
	 * @see <a href=
	 *      "http://www.onem2m.org/images/files/deliverables/Release3/TS-0004_Service_Layer_Core_Protocol_V3_11_2.pdf">oneM2M
	 *      TS-0004 6.3.4.2.7</a>
	 */
	public ResultContent				resultContent;

	/**
	 * Event Category
	 * <p>
	 * allowed values are 2(Immediate), 3(BestEffort), 4(Latest), and 100-999 as
	 * user defined range.
	 * 
	 * @see <a href=
	 *      "http://www.onem2m.org/images/files/deliverables/Release3/TS-0004_Service_Layer_Core_Protocol_V3_11_2.pdf">oneM2M
	 *      TS-0004 6.3.3</a>
	 * @see <a href=
	 *      "https://git.onem2m.org/PRO/XSD/blob/master/v3_11_0/CDT-commonTypes-v3_11_0.xsd#L326">oneM2M
	 *      XSD eventCat</a>
	 * @see <a href=
	 *      "https://git.onem2m.org/PRO/XSD/blob/master/v3_11_0/CDT-enumerationTypes-v3_11_0.xsd#L208-221">oneM2M
	 *      XSD stdEventCats</a>
	 */
	public Integer						eventCategory;

	/**
	 * Delivery Aggregation
	 * <p>
	 * This parameter is related to CMDH(Communication Management and Delivery
	 * Handling) policy.
	 * 
	 * @see <a href=
	 *      "http://www.onem2m.org/images/files/deliverables/Release3/TS-0004_Service_Layer_Core_Protocol_V3_11_2.pdf">oneM2M
	 *      TS-0004 D.12</a>
	 */
	public Boolean						deliveryAggregation;

	/**
	 * Group Request Identifier TODO: search doc.
	 */
	public String						groupRequestIdentifier;

	/**
	 * Filter Criteria
	 *
	 * @see <a href=
	 *      "http://www.onem2m.org/images/files/deliverables/Release3/TS-0004_Service_Layer_Core_Protocol_V3_11_2.pdf">oneM2M
	 *      TS-0004 6.3.5.8</a>
	 */
	public FilterCriteriaDTO			filterCriteria;

	/**
	 * Desired Identifier Result Type
	 * <p>
	 * This parameter specifies identifier type in response, such as structured
	 * or unstructured. This parameter used to be Discovery Result Type in
	 * previous oneM2M release.
	 * 
	 * @see <a href=
	 *      "http://www.onem2m.org/images/files/deliverables/Release3/TS-0004_Service_Layer_Core_Protocol_V3_11_2.pdf">oneM2M
	 *      TS-0004 6.3.4.2.8</a>
	 */
	public DesiredIdentiferResultType	desiredIdentiferResultType;

	/**
	 * Tokens
	 * <p>
	 * Each token is in m2m:dynAuthJWT
	 * 
	 * @see <a href=
	 *      "https://git.onem2m.org/PRO/XSD/blob/master/v3_11_0/CDT-commonTypes-v3_11_0.xsd#L417-426">oneM2M
	 *      XSD signatureList</a>
	 */
	public List<String>					tokens;

	/**
	 * Token Identifiers
	 * <p>
	 * In oneM2M this parameter is expressed as list of m2m:tokenID.
	 * 
	 * @see <a href=
	 *      "https://git.onem2m.org/PRO/XSD/blob/master/v3_11_0/CDT-commonTypes-v3_11_0.xsd#L66-70">oneM2M
	 *      XSD signatureList</a>
	 */
	public List<String>					tokenIDs;

	/**
	 * Local Token Identifiers
	 * <p>
	 * In oneM2M this parameter is expressed as list of xs:NCName.
	 */
	public List<String>					localTokenIDs;

	/**
	 * Token Request Indicator
	 */
	public Boolean						tokenRequestIndicator;

	// Added at R3.0
	/**
	 * Group Request Target Members
	 */
	public List<String>					groupRequestTargetMembers;

	/**
	 * Authorization Signature Indicator
	 */
	public Boolean						authorizationSignatureIndicator;

	/**
	 * Authorization Signatures
	 * <p>
	 * In oneM2M this parameter is expressed in m2m:signatureList.
	 * 
	 * @see <a href=
	 *      "http://www.onem2m.org/images/files/deliverables/Release3/TS-0004_Service_Layer_Core_Protocol_V3_11_2.pdf">oneM2M
	 *      TS-0004 6.3.4.2.8</a>
	 * @see <a href=
	 *      "https://git.onem2m.org/PRO/XSD/blob/master/v3_11_0/CDT-commonTypes-v3_11_0.xsd#L129-137">oneM2M
	 *      XSD signatureList</a>
	 */
	public List<String>					authorizationSignatures;

	/**
	 * Authorization Relationship Indicator
	 */
	public Boolean						authorizationRelationshipIndicator;

	/**
	 * Semantic Query Indicator
	 * 
	 * @see <a href=
	 *      "http://www.onem2m.org/images/files/deliverables/Release3/TS-0004_Service_Layer_Core_Protocol_V3_11_2.pdf">oneM2M
	 *      TS-0004 7.3.3.19</a>
	 */
	public Boolean						semanticQueryIndicator;

	/**
	 * Release Version
	 */
	public ReleaseVersion				releaseVersionIndicator;

	/**
	 * Vendor Information
	 * <p>
	 * Used for vendor specific information. No procedure is defined for the
	 * parameter.
	 */
	public String						vendorInformation;

	/**
	 * Enum for DesiredIdentifierResultType
	 * 
	 * @see <a href=
	 *      "http://www.onem2m.org/images/files/deliverables/Release3/TS-0004_Service_Layer_Core_Protocol_V3_11_2.pdf">oneM2M
	 *      TS-0004 6.3.4.2.8</a>
	 */
	public enum DesiredIdentifierResultType {
		/**
		 * structured
		 */
		structured(1),
		/**
		 * unstructured
		 */
		unstructured(2);

		private final int value;

		private DesiredIdentifierResultType(int i) {
			value = i;
		}

		/**
		 * Return result type value.
		 * 
		 * @return The result type value.
		 */
		public int getValue() {
			return value;
		}
	}

	/**
	 * enum type for Result Content
	 * 
	 * @see <a href=
	 *      "https://git.onem2m.org/PRO/XSD/blob/master/v1_0_0/CDT-enumerationTypes-v1_0_0.xsd#L183-205">oneM2M
	 *      XSD resultContent</a>
	 */
	public static enum ResultContent {
		/**
		 * nothing
		 */
		nothing(1),
		/**
		 * attributes
		 */
		attributes(2),
		/**
		 * hierarchicalAddress
		 */
		hierarchicalAddress(3),
		/**
		 * hierarchicalAddressAndAttributes
		 */
		hierarchicalAddressAndAttributes(4),
		/**
		 * attributesAndChildResources
		 */
		attributesAndChildResources(5),
		/**
		 * attributesAndChildResourceReferences
		 */
		attributesAndChildResourceReferences(6),
		/**
		 * childResourceReferences
		 */
		childResourceReferences(7),
		/**
		 * originalResource
		 */
		originalResource(8),
		/**
		 * childResources
		 */
		childResources(9);

		private final int value;

		private ResultContent(int i) {
			value = i;
		}

		/**
		 * get assigned integer value
		 * 
		 * @return assigned integer value
		 */
		public int getValue() {
			return value;
		}
	}

	/**
	 * enum type for Operation
	 * 
	 * @see <a href=
	 *      "https://git.onem2m.org/PRO/XSD/blob/master/v1_0_0/CDT-enumerationTypes-v1_0_0.xsd#L149-166">oneM2M
	 *      XSD resultContent</a>
	 */
	public static enum Operation {
		/**
		 * Create
		 */
		Create(1),
		/**
		 * Retrieve
		 */
		Retrieve(2),
		/**
		 * Update
		 */
		Update(3),
		/**
		 * Delete
		 */
		Delete(4),
		/**
		 * Notify
		 */
		Notify(5);

		private final int value;

		private Operation(int i) {
			value = i;
		}

		/**
		 * get assigned integer value
		 * 
		 * @return assigned integer value
		 */
		public int getValue() {
			return value;
		}
	}

}
