package org.osgi.service.onem2m.dto;

import java.util.List;

import org.osgi.dto.DTO;

/**
 * Expressing ResponseTypeInfo
 */
public class ResponseTypeInfoDTO extends DTO {
//	@javax.xml.bind.annotation.XmlElement(required = true)
	public ResponseType responseTypeValue;
//	@javax.xml.bind.annotation.XmlElement(required = true)
	public List<java.lang.String> notificationURI;

	public static enum ResponseType {
		nonBlockingRequestSynch(1), nonBlockingRequestAsynch(2), blockingRequest(3), flexBlocking(4);

		private int value;

		private ResponseType(int i) {
			value = i;
		}

		public int getValue() {
			return value;
		}
	}
}
