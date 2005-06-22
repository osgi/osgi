/*
 * Copyright (c) The Open Services Gateway Initiative (2004).
 * All Rights Reserved.
 *
 * Implementation of certain elements of the Open Services Gateway Initiative
 * (OSGI) Specification may be subject to third party intellectual property
 * rights, including without limitation, patent rights (such a third party may
 * or may not be a member of OSGi). OSGi is not responsible and shall not be
 * held responsible in any manner for identifying or failing to identify any or
 * all such third party intellectual property rights.
 *
 * This document and the information contained herein are provided on an "AS
 * IS" basis and OSGI DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION HEREIN WILL
 * NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF MERCHANTABILITY OR
 * FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL OSGI BE LIABLE FOR ANY
 * LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF USE OF DATA, INTERRUPTION OF
 * BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR EXEMPLARY, INCIDENTIAL,
 * PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN CONNECTION WITH THIS
 * DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH LOSS OR DAMAGE.
 *
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */

/*
 * REVISION HISTORY:
 *
 * Date         Author(s)
 * CR           Headline
 * ===========  ==============================================================
 * Mar 29, 2005 Alexandre Santos
 * 14           Implement MEG TCK
 * ===========  ==============================================================
 */
package org.osgi.test.cases.monitor.tbc.Activators;

import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;
import org.osgi.test.cases.monitor.tbc.MonitorTestControl;

/**
 * @author Alexandre Santos
 */
public class MonitorHandlerImpl implements EventHandler {

	private MonitorTestControl tbc;

	public MonitorHandlerImpl(MonitorTestControl tbc) {
		this.tbc = tbc;
	}

	/**
	 * Should be called by EventAdmin when a StatusVariable was updated and will
	 * increment the variable in MonitorTestControl.
	 */
	public void handleEvent(Event arg0) {
		String listenerId = (String) arg0.getProperty(MonitorTestControl.CONST_LISTENER_ID); 
		
		if ((listenerId!=null) && (listenerId.equals(
				MonitorTestControl.INITIATOR))) {

			MonitorTestControl.EVENT_COUNT += 1;
			
			tbc.resetEvent();

			String properties[] = arg0.getPropertyNames();

			for (int i = 0; i < properties.length; i++) {
				if (properties[i]
						.equals(MonitorTestControl.CONST_MONITORABLE_PID)) {
					tbc.setMonitorablePid(true);
				} else if (properties[i]
						.equals(MonitorTestControl.CONST_STATUSVARIABLE_NAME)) {
					tbc.setStatusVariableName(true);
				} else if (properties[i]
						.equals(MonitorTestControl.CONST_STATUSVARIABLE_VALUE)) {
					tbc.setStatusVariableValue(true);
				} else if (properties[i]
						.equals(MonitorTestControl.CONST_LISTENER_ID)) {
					tbc.setListenerId(true);
				}
			}
			
			tbc.setStatusVariableName((String) arg0
									.getProperty(MonitorTestControl.CONST_STATUSVARIABLE_NAME));
			
			tbc.setStatusVariableValue((String) arg0
									.getProperty(MonitorTestControl.CONST_STATUSVARIABLE_VALUE));
			
			tbc.setMonitorablePid((String) arg0.getProperty(MonitorTestControl.CONST_MONITORABLE_PID));
			
			tbc.setListenerId((String) arg0
							.getProperty(MonitorTestControl.CONST_LISTENER_ID));
			
		} else {
			
			MonitorTestControl.SWITCH_EVENTS_COUNT += 1;					
			
		}
	}

}
