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


package org.osgi.impl.service.enocean.basedriver;

import java.util.ArrayList;
import org.osgi.service.enocean.EnOceanException;
import org.osgi.service.enocean.EnOceanHost;

public class EnOceanSerialHost implements EnOceanHost {

	private ArrayList	listeners;

	public EnOceanSerialHost(String serialPort, int serialSpeed) {
		listeners = new ArrayList();
		/*
		 * TODO: - try and open the serial port - register a handler for any new
		 * char read (use a thread or some callback in rxtx) - for any new full
		 * sequence of bytes, report them to the base driver who will interpret
		 * it
		 */

	}

	public void reset() throws EnOceanException {
		// TODO Auto-generated method stub

	}

	public String appVersion() throws EnOceanException {
		// TODO Auto-generated method stub
		return null;
	}

	public String apiVersion() throws EnOceanException {
		// TODO Auto-generated method stub
		return null;
	}

	public int getBaseID() throws EnOceanException {
		// TODO Auto-generated method stub
		return 0;
	}

	public void setBaseID(int baseID) throws EnOceanException {
		// TODO Auto-generated method stub

	}

	public void setRepeaterLevel(int level) throws EnOceanException {
		// TODO Auto-generated method stub

	}

	public int getRepeaterLevel() throws EnOceanException {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getSenderId(String servicePID) {
		// TODO Auto-generated method stub
		return 0;
	}

	public void start() {
		// TODO Auto-generated method stub

	}

	public void stop() {
		// TODO Auto-generated method stub

	}

	/**
	 * Implementation-specific method to add a remote packet listener.
	 * 
	 * @param packetListener an object implementing the
	 *        {@link EnOceanPacketListener} interface.
	 */
	public void addPacketListener(EnOceanPacketListener packetListener) {
		if (!listeners.contains(packetListener)) {
			listeners.add(packetListener);
		}
	}

}
