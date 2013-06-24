package org.osgi.service.enocean.examples.telegrams;

import java.util.HashMap;
import java.util.Map;

import org.osgi.service.enocean.EnOceanException;
import org.osgi.service.enocean.EnOceanTelegram;
import org.osgi.service.enocean.datafields.EnOceanDatafield;
import org.osgi.service.enocean.examples.Helpers;

/**
 * This sample class shows how we could create a generic class that coud theoretically
 * implement almost any kind of telegram.
 * 
 * The serialize method would be generic, for instance, and the datafields and properties
 * automatically generated in the case of a deserialize() call.
 * 
 * This class could then be subclassed for very particular examples.
 * 
 * @author Victor Perron
 *
 */
public class GenericTelegram implements EnOceanTelegram {

	public static final int TELEGRAM_TYPE_RPS = 0xF6;
	public static final int TELEGRAM_TYPE_1BS = 0xD5;
	public static final int TELEGRAM_TYPE_4BS = 0xA5;
	public static final int TELEGRAM_TYPE_ADT = 0xA6; // Exception
	public static final int TELEGRAM_TYPE_VLD = 0xD2;
	
	private Map properties;
	private int status = -1;
	private EnOceanDatafield[] datafields;
	
	
	public GenericTelegram() {
		properties = new HashMap();
		this.properties.put((Object)EnOceanTelegram.RORG, ((Object)new Integer(-1)));
		this.properties.put((Object)EnOceanTelegram.SENDER_ID, ((Object)new Integer(-1)));
		this.properties.put((Object)EnOceanTelegram.DESTINATION_ID, ((Object)new Integer(-1)));
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	/**
	 * A Generic Telegram is serialized as such: 
	 * - RORG
	 * - Datafields
	 * - TXID
	 * - Status
	 * - Checksum
	 * 
	 * ... With many exceptions.
	 * 
	 * If there is a destinationId ( != -1 ) then the serializer should prepend 0xA6.
	 */
	public byte[] serialize() throws EnOceanException {
		// Place the RORG
		Integer RORG = (Integer)properties.get(EnOceanTelegram.RORG);
		byte[] bRORG = Helpers.shortToByteArray(RORG.intValue());
		
		byte[] bData = null;
		switch (RORG.intValue()) {
		case TELEGRAM_TYPE_1BS:
		case TELEGRAM_TYPE_RPS:
			bData = new byte[] { 0x0 };
			break;
		case TELEGRAM_TYPE_4BS:
			bData = new byte[] { 0x0, 0x0, 0x0, 0x0 };
			break;
		case TELEGRAM_TYPE_VLD:
			bData = new byte[] { 0x0, 0x0, 0x0, 0x0 };
			break;
		default:
			throw new EnOceanException("Unknown RadioTelegram type (RORG)");
		}
		
		// Place the datafields; according to the RORG, this array is more or less long
		for(int i=0;i<datafields.length;i++) {
			// do nothing
		}
		// Place the senderID
		Integer senderId = (Integer)properties.get(EnOceanTelegram.SENDER_ID);
		byte[] bSenderId = Helpers.intToByteArray_BE(senderId.intValue());
		// Place the status
		byte[] bStatus = Helpers.shortToByteArray(status); 
								
		byte[][] bytes = {bRORG, bData, bSenderId, bStatus};
		return Helpers.combine(bytes);
	}

	public Object deserialize(byte[] bytes) throws EnOceanException {
		return null;
	}

	public EnOceanDatafield[] getDatafields() {
		return datafields;
	}

	public int getSubtelegramCount() {
		// FIXME Auto-generated method stub
		return 0;
	}
}
