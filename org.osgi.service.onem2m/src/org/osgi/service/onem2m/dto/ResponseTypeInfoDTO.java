package org.osgi.service.onem2m.dto;

import java.util.List;

import org.osgi.dto.DTO;

/**
 * Expressing ResponseTypeInfo
 */
public class ResponseTypeInfoDTO extends DTO {
//	@javax.xml.bind.annotation.XmlElement(required = true)
	/**
	 * Response Type Value
	 */
	public ResponseType responseTypeValue;
//	@javax.xml.bind.annotation.XmlElement(required = true)
	/**
	 * Notification URI
	 */
	public List<java.lang.String> notificationURI;

	/**
	 * enum ResponseType 
	 * 
	 *
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
		 * @return assigned integer value.
		 */
		public int getValue() {
			return value;
		}
	}
}
