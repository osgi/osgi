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
 * 05/05/2005   Leonardo Barros
 * 38           Implement MEG TCK
 * ===========  ==============================================================
 */
package org.osgi.test.cases.application.tbc.Event;

import org.osgi.framework.Constants;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;
import org.osgi.test.cases.application.tbc.ApplicationConstants;
import org.osgi.test.cases.application.tbc.ApplicationTestControl;

public class EventHandlerImpl implements EventHandler {
	private ApplicationTestControl tbc;

	public static boolean waitNotify = true;

	public EventHandlerImpl(ApplicationTestControl tbc) {
		this.tbc = tbc;
	}

	public void handleEvent(Event arg0) {
		String pid = (String) arg0.getProperty(Constants.SERVICE_PID);
		if ((pid != null) && (pid.startsWith(ApplicationConstants.TEST_PID))) {
			if (arg0.getTopic().equals(
					"org/osgi/framework/ServiceEvent/REGISTERED")) {
				tbc.setRegistered(true);
			} else if (arg0.getTopic().equals(
					"org/osgi/framework/ServiceEvent/MODIFIED")) {
				tbc.setModified(true);
			} else if (arg0.getTopic().equals(
					"org/osgi/framework/ServiceEvent/UNREGISTERING")) {
				tbc.setUnregistered(true);
			}
			if (waitNotify) {
				synchronized (tbc) {
					tbc.notifyAll();
				}

			}
		}
	}

}
