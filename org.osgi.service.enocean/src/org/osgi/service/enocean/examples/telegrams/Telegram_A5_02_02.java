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
	private Map<String, Object> properties;

	public Telegram_A5_02_02() {
		properties = new HashMap<String, Object>();
		this.properties.put(EnOceanTelegram.RORG, 0xA5);
		this.properties.put(EnOceanTelegram.SENDER_ID, -1);
		this.properties.put(EnOceanTelegram.DESTINATION_ID, -1);
	}
	
	@Override
	public byte[] serialize() throws EnOceanException {
		return null;
	}

	@Override
	public Object deserialize(byte[] bytes) throws EnOceanException {
		return null;
	}

	@Override
	public int getStatus() {
		return status;
	}

	@Override
	public void setStatus(int status) {
		this.status = status;
	}

	@Override
	public EnOceanDatafield[] getDatafields() {
		return datafields;
	}

	@Override
	public int getSubtelegramCount() {
		return 1;
	}
}
