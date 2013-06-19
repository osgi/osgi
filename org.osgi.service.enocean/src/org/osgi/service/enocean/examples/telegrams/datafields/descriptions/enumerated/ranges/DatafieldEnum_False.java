package org.osgi.service.enocean.examples.telegrams.datafields.descriptions.enumerated.ranges;

import org.osgi.service.enocean.datafields.EnOceanDatafieldEnum;

public class DatafieldEnum_False implements EnOceanDatafieldEnum {

	@Override
	public int getStart() {
		return 0;
	}

	@Override
	public int getStop() {
		return 0;
	}

	@Override
	public String getName() {
		return "False";
	}

	@Override
	public String getDescription() {
		return "'False' value in a Boolean datafield";
	}

}
