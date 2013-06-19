package org.osgi.service.enocean.examples.telegrams.datafields.descriptions.enumerated.ranges;

import org.osgi.service.enocean.datafields.EnOceanDatafieldEnum;

public class DatafieldEnum_True implements EnOceanDatafieldEnum {

	@Override
	public int getStart() {
		return 1;
	}

	@Override
	public int getStop() {
		return 1;
	}

	@Override
	public String getName() {
		return "True";
	}

	@Override
	public String getDescription() {
		return "'True' value in a Boolean datafield";
	}

}
