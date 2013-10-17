/*
 * Copyright (c) OSGi Alliance (2013). All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.osgi.test.cases.enocean.descriptions;

import java.util.Hashtable;
import java.util.Map;
import org.osgi.service.enocean.EnOceanMessageDescription;
import org.osgi.service.enocean.sets.EnOceanMessageDescriptionSet;

public class EnOceanMessageDescriptionSetImpl implements EnOceanMessageDescriptionSet {

	private Map	messageTable	= new Hashtable();

	public void putMessage(int rorg, int func, int type, EnOceanMessageDescription msg) {
		String key = generateKey(rorg, func, type);
		messageTable.put(key, msg);
	}

	private String generateKey(int rorg, int func, int type) {
		return "" + rorg + "-" + func + "-" + type;
	}

	private String generateKey(int rorg, int func, int type, int extra) {
		return "" + rorg + "-" + func + "-" + type + "-" + extra;
	}

	public EnOceanMessageDescription getMessageDescription(int rorg, int func, int type, int extra) {
		String key = null;
		if (extra == -1) {
			key = generateKey(rorg, func, type);
		} else {
			key = generateKey(rorg, func, type, extra);
		}

		try {
			EnOceanMessageDescription instance = (EnOceanMessageDescription) messageTable.get(key);
			return instance;
		} catch (Exception e) {
			System.out.println("There was an error reading the messageSet : " + e.getMessage());
		}
		return null;
	}

}
