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
 */

/* 
 * REVISION HISTORY:
 *
 * Date          Author(s)
 * CR            Headline
 * ============  ==============================================================
 * Abr 14, 2005  Luiz Felipe Guimaraes
 * 46            [MEGTCK][DMT] Implement AdminPermission Test Cases
 * ============  ==============================================================
 * Jun 17, 2005  Alexandre Alves
 * 28            [MEGTCK][DMT] Implement test cases for DmtSession.close()
 * ============  ==============================================================
 */
package org.osgi.test.cases.dmt.main.tbc;

import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;
import org.osgi.test.cases.dmt.main.tbc.Plugin.TestExecPluginActivator;

/**
 * @author Luiz Felipe Guimaraes
 */
public class DmtHandlerImpl implements EventHandler {

	private static int EVENT_COUNT = 0;

	/*
	 * private static final String ADDED =
	 * "org/osgi/service/dmt/DmtEvent/ADDED";
	 * 
	 * private static final String DELETED =
	 * "org/osgi/service/dmt/DmtEvent/DELETED";
	 * 
	 * private static final String REPLACED =
	 * "org/osgi/service/dmt/DmtEvent/REPLACED";
	 * 
	 * private static final String RENAMED =
	 * "org/osgi/service/dmt/DmtEvent/RENAMED";
	 * 
	 * private static final String COPIED =
	 * "org/osgi/service/dmt/DmtEvent/COPIED";
	 */

	private static final String ADDED = "org/osgi/service/dmt/ADDED";

	private static final String DELETED = "org/osgi/service/dmt/DELETED";

	private static final String REPLACED = "org/osgi/service/dmt/REPLACED";

	private static final String RENAMED = "org/osgi/service/dmt/RENAMED";

	private static final String COPIED = "org/osgi/service/dmt/COPIED";

	private static final String SESSION_ID = "session.id";

	private static final String NODES = "nodes";

	private static final String NEWNODES = "newnodes";

	private static final String TOPIC = "event.topics";

	private static boolean addedInexistentNode = false;

	private static boolean renamedInteriorNode = false;

	private static boolean renamedToInexistentNode = false;

	private static boolean replacedLeafNode = false;

	private static boolean deletedInteriorNode = false;

	private static boolean copiedFromInteriorNode = false;

	private static boolean copiedToInexistentNode = false;

	private static boolean ordered = true;

	private static boolean isSessionId = false;

	private static boolean isNodes = false;

	private static boolean isTopic = false;

	private DmtTestControl tbc;

	public DmtHandlerImpl(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	/**
	 * Should be called by EventAdmin when a close() method is called.
	 */
	public synchronized void handleEvent(Event event) {

		String properties[] = event.getPropertyNames();

		EVENT_COUNT++;

		// validate the values in properties
		for (int z = 0; z < properties.length; z++) {
			if (properties[z].equals(SESSION_ID)) {
				isSessionId = true;
			} else if (properties[z].equals(NODES)) {
				isNodes = true;
			} else if (properties[z].equals(TOPIC)) {
				isTopic = true;
			}
		}

		if (isAllProperties()) {

			int id = ((Integer) event.getProperty(SESSION_ID)).intValue();
			String topic = (String) event.getProperty(TOPIC);
			String[] nodes = (String[]) event.getProperty(NODES);
			String[] newNodes = null;

			for (int i = 0; i < nodes.length; i++) {
				if (topic == ADDED) {
					if (nodes[i] == TestExecPluginActivator.INEXISTENT_NODE) {
						addedInexistentNode = true;
					}
					if (EVENT_COUNT != 1) {
						ordered = false;
					}

				} else if (topic == DELETED) {
					if (nodes[i] == TestExecPluginActivator.INTERIOR_NODE) {
						deletedInteriorNode = true;
					}
					if (EVENT_COUNT != 2) {
						ordered = false;
					}

				} else if (topic == REPLACED) {
					if (nodes[i] == TestExecPluginActivator.LEAF_NODE) {
						replacedLeafNode = true;
					}
					if (EVENT_COUNT != 3) {
						ordered = false;
					}
				} else if (topic == RENAMED) {
					newNodes = (String[]) event.getProperty(NEWNODES);
					if (nodes[i] == TestExecPluginActivator.INTERIOR_NODE) {
						renamedInteriorNode = true;
						if ((newNodes.length == 1)
								&& (newNodes[0] == TestExecPluginActivator.RENAMED_VALUE)) {
							renamedToInexistentNode = true;
						}
					}
					if (EVENT_COUNT != 4) {
						ordered = false;
					}
				} else if (topic == COPIED) {
					newNodes = (String[]) event.getProperty(NEWNODES);
					if (nodes[i] == TestExecPluginActivator.INTERIOR_NODE) {
						copiedFromInteriorNode = true;
						if ((newNodes.length == 1)
								&& (newNodes[0] == TestExecPluginActivator.INEXISTENT_NODE)) {
							copiedToInexistentNode = true;
						}
					}
					if (EVENT_COUNT != 5) {
						ordered = false;
					}
				}
			}
		}
		if (EVENT_COUNT == 5) {
			notifyAll();
		}

	}

	public static boolean passed() {
		if (addedInexistentNode && renamedInteriorNode
				&& renamedToInexistentNode && replacedLeafNode
				&& deletedInteriorNode && copiedFromInteriorNode
				&& copiedToInexistentNode) {
			return true;
		} else {
			return false;
		}

	}

	public static int getEventCount() {
		return EVENT_COUNT;
	}

	public static boolean isOrdered() {
		return ordered;
	}

	public static boolean isAllProperties() {
		return (isSessionId && isNodes && isTopic);
	}
}