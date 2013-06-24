package org.osgi.service.enocean.examples.telegrams.datafields.descriptions.enumerated.ranges;

import org.osgi.service.enocean.datafields.EnOceanDatafieldEnum;

public class DatafieldEnum_False implements EnOceanDatafieldEnum {

	public int getStart() {
		return 0;
	}

	public int getStop() {
		return 0;
	}

	public String getName() {
		return "False";
	}

	public String getDescription() {
		return "'False' value in a Boolean datafield";
	}

}
