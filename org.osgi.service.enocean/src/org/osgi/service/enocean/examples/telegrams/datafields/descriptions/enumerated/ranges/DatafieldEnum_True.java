package org.osgi.service.enocean.examples.telegrams.datafields.descriptions.enumerated.ranges;

import org.osgi.service.enocean.datafields.EnOceanDatafieldEnum;

public class DatafieldEnum_True implements EnOceanDatafieldEnum {

	public int getStart() {
		return 1;
	}

	public int getStop() {
		return 1;
	}

	public String getName() {
		return "True";
	}

	public String getDescription() {
		return "'True' value in a Boolean datafield";
	}

}
