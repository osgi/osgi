package org.osgi.impl.service.upnp.cd.control;

import java.util.*;

// This class contains the error codes, their errorDescription and Description
// which are relevent to control layer. The error codes are mapped to errorDescription 
// and Description which can be retreived through the provided methods.
public class SOAPErrorCodes {
	// This table maps the error codes to their description.
	private static Hashtable	errorMap;

	// This is the constructor which initializes the error table and populates
	// it.
	public SOAPErrorCodes() {
		errorMap = new Hashtable();
		errorMap.put("401", new String[] {"Invalid Action",
				"No action by that name at this service."});
		errorMap
				.put(
						"402",
						new String[] {
								"Invalid Args",
								"Could be any of the following: not "
										+ "enough in args, too many in args, no in args, no in args by that name, "
										+ "one or more in args are of the wrong data type."});
		errorMap.put("403", new String[] {"Out of Sync",
				"Out of synchronization."});
		errorMap.put("501", new String[] {
				"Action Failed",
				"May be returned in current state of "
						+ "service prevents invoking that action."});
		errorMap.put("404", new String[] {"Invalid Var",
				"No state variable by that name at this service."});
	}

	// This method takes the error code and returns the description.
	static String getDesc(String code) {
		String[] str = (String[]) errorMap.get(code);
		if (str == null) {
			return null;
		}
		return str[1];
	}

	// This method takes the error code and returns the error description.
	static String getErrorDesc(String code) {
		String[] str = (String[]) errorMap.get(code);
		if (str == null) {
			return null;
		}
		return str[0];
	}
}
