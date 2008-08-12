/*
 * Copyright (c) The OSGi Alliance (2004). All Rights Reserved.
 * 
 * Implementation of certain elements of the OSGi Specification may be subject
 * to third party intellectual property rights, including without limitation,
 * patent rights (such a third party may or may not be a member of the OSGi
 * Alliance). The OSGi Alliance is not responsible and shall not be held
 * responsible in any manner for identifying or failing to identify any or all
 * such third party intellectual property rights.
 * 
 * This document and the information contained herein are provided on an "AS IS"
 * basis and THE OSGI ALLIANCE DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION
 * HEREIN WILL NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL THE
 * OSGI ALLIANCE BE LIABLE FOR ANY LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF
 * USE OF DATA, INTERRUPTION OF BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR
 * EXEMPLARY, INCIDENTIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN
 * CONNECTION WITH THIS DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH LOSS OR DAMAGE.
 * 
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 * 
 */

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

import info.dmtree.notification.AlertItem;
import info.dmtree.notification.spi.RemoteAlertSender;



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
