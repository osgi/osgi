package org.osgi.service.enocean.examples.telegrams;

import java.util.HashMap;
import java.util.Map;

import org.osgi.service.enocean.EnOceanException;
import org.osgi.service.enocean.EnOceanTelegram;
import org.osgi.service.enocean.datafields.EnOceanDatafield;
import org.osgi.service.enocean.examples.telegrams.datafields.LearnBitDatafield_4BS;
import org.osgi.service.enocean.examples.telegrams.datafields.TemperatureEnOceanDatafield;


/**
 * This is a sample Telegram that implements a Temperature Sensing Telegram.
 * 
 * @author Victor Perron
 *
 */
public class Telegram_A5_02_02 implements EnOceanTelegram {

	private int status;
	private EnOceanDatafield[] datafields = {new TemperatureEnOceanDatafield(), new LearnBitDatafield_4BS()};
	private Map properties;

	public Telegram_A5_02_02() {
		properties = new HashMap();
		this.properties.put((Object)EnOceanTelegram.RORG, ((Object)new Integer(0xA5)));
		this.properties.put((Object)EnOceanTelegram.SENDER_ID, ((Object)new Integer(-1)));
		this.properties.put((Object)EnOceanTelegram.DESTINATION_ID, ((Object)new Integer(-1)));
	}
	
	public byte[] serialize() throws EnOceanException {
		return null;
	}

	public Object deserialize(byte[] bytes) throws EnOceanException {
		return null;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public EnOceanDatafield[] getDatafields() {
		return datafields;
	}

	public int getSubtelegramCount() {
		return 1;
	}
}
