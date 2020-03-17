package org.osgi.service.onem2m.dto;

import java.util.List;

import org.osgi.dto.DTO;

/**
 * DTO expresses ResponseTypeInfo
 * 
 * @see <a href=
 *      "http://www.onem2m.org/images/files/deliverables/Release3/TS-0004_Service_Layer_Core_Protocol_V3_11_2.pdf">oneM2M
 *      TS-0004 6.3.5.30</a>
 */
public class ResponseTypeInfoDTO extends DTO {
	/**
	 * Response Type Value
	 * 
	 * @see <a href=
	 *      "http://www.onem2m.org/images/files/deliverables/Release3/TS-0004_Service_Layer_Core_Protocol_V3_11_2.pdf">oneM2M
	 *      TS-0004 6.3.4.2.6</a>
	 */
	public ResponseType responseTypeValue;

	/**
	 * Notification URI
	 * 
	 * @see <a href=
	 *      "http://www.onem2m.org/images/files/deliverables/Release3/TS-0004_Service_Layer_Core_Protocol_V3_11_2.pdf">oneM2M
	 *      TS-0004 6.3.5.30</a>
	 * @see <a href=
	 *      "http://www.onem2m.org/images/files/deliverables/Release3/TS-0004_Service_Layer_Core_Protocol_V3_11_2.pdf">oneM2M
	 *      TS-0004 7.5.1.2.5</a>
	 * 
	 */
	public List<java.lang.String> notificationURI;

	/**
	 * enum ResponseType
	 *
	 * @see <a href=
	 *      "http://www.onem2m.org/images/files/deliverables/Release3/TS-0004_Service_Layer_Core_Protocol_V3_11_2.pdf">oneM2M
	 *      TS-0004 6.3.4.2.6</a>
	 */
	public static enum ResponseType {
		/**
		 * nonBlockingRequestSynch
		 */
		nonBlockingRequestSynch(1),
		/**
		 * nonBlockingRequestAsynch
		 */
		nonBlockingRequestAsynch(2),
		/**
		 * blockingRequest
		 */
		blockingRequest(3),
		/**
		 * flexBlocking
		 */
		flexBlocking(4);

		private int value;

		private ResponseType(int i) {
			value = i;
		}

		/**
		 * get assigned value
		 * 
		 * @return assigned integer value.
		 */
		public int getValue() {
			return value;
		}
	}
}
