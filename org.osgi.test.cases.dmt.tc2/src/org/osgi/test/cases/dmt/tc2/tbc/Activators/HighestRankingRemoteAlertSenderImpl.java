/*******************************************************************************
 * Copyright (c) Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0 
 *******************************************************************************/

/*
 * REVISION HISTORY:
 *
 * Date          Author(s)
 * CR            Headline
 * ============  ==============================================================
 * Jul 12, 2005  Luiz Felipe Guimaraes
 * 118           Implement sendAlert
 * ============  ==============================================================
 */
package org.osgi.test.cases.dmt.tc2.tbc.Activators;

import org.osgi.service.dmt.notification.AlertItem;
import org.osgi.service.dmt.notification.spi.RemoteAlertSender;



/**
 *	This class tests the alert sent by the DmtAdmin.
 */
public class HighestRankingRemoteAlertSenderImpl implements
		RemoteAlertSender {
	
	public static int codeException = 99;
	
	public static String serverIdFound = "";
	public static int codeFound = -1;
	public static String correlatorFound = "";
	public static AlertItem[] itemsFound = new AlertItem[] {};
	
	public static void resetAlert() {
		serverIdFound = "";
		codeFound = -1;
		correlatorFound = "";
		itemsFound = new AlertItem[] {};
	}
	public HighestRankingRemoteAlertSenderImpl() {
		
	}

	@Override
	public void sendAlert(String serverId, int code, String correlator,
			AlertItem[] items) throws Exception {
		//Any exception thrown on this method will be propagated to the original sender of the event, wrapped in a DmtException with the code REMOTE_ERROR. 
		if (code==codeException)
			throw new Exception();
		
		serverIdFound = serverId;
		codeFound = code;
		correlatorFound = correlator;
		itemsFound = items;
	}

}
