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
package org.osgi.test.cases.dmt.main.tbc.Activators;

import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;
import org.osgi.test.cases.dmt.main.tbc.DmtTestControl;
import org.osgi.test.cases.dmt.main.tbc.Plugin.ExecPlugin.TestExecPluginActivator;

/**
 * @author Luiz Felipe Guimaraes
 */
public class EventHandlerImpl implements EventHandler {

	private static int EVENT_COUNT = 0;

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

	private static boolean orderedAtomic = true;
    
    private static boolean orderedExclusive = true;

	private static boolean isSessionId = false;

	private static boolean isNodes = false;

	private static boolean isTopic = false;
	
	private static boolean isNewNodes = false;
	
	private static int sessionId = -1;

	private DmtTestControl tbc;

	public EventHandlerImpl(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	/**
	 * Should be called by EventAdmin when a close() method is called.
	 */
	public void handleEvent(Event event) {

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
		try { 
		    sessionId = Integer.parseInt(event.getProperty(SESSION_ID).toString());
		} catch (Exception e) {
		    sessionId=-1;
		}
		
		if (isMandatoryProperties()) {
		    
			String topic = (String) event.getProperty(TOPIC);
			String[] nodes = (String[]) event.getProperty(NODES);
			String[] newNodes = null;
			for (int i = 0; i < nodes.length; i++) {
				if (topic.equals(ADDED)) {
					if (nodes[i].equals(TestExecPluginActivator.INEXISTENT_NODE)) {
						addedInexistentNode = true;
					}
					if (EVENT_COUNT != 1) {
						orderedAtomic = false;
					}
                    if (EVENT_COUNT != 4) {
                        orderedExclusive = false;
                    }


				} else if (topic.equals(DELETED)) {
					if (nodes[i].equals(TestExecPluginActivator.INTERIOR_NODE)) {
						deletedInteriorNode = true;
					}
					if (EVENT_COUNT != 2) {
						orderedAtomic = false;
					}
                    if (EVENT_COUNT != 5) {
                        orderedExclusive = false;
                    }


				} else if (topic.equals(REPLACED)) {
					if (nodes[i].equals(TestExecPluginActivator.LEAF_NODE)) {
						replacedLeafNode = true;
					}
					if (EVENT_COUNT != 3) {
						orderedAtomic = false;
					}
                    if (EVENT_COUNT != 1) {
                        orderedExclusive= false;
                    }
				} else if (topic.equals(RENAMED)) {
					newNodes = (String[]) event.getProperty(NEWNODES);
					isNewNodes = true;
					if (nodes[i].equals(TestExecPluginActivator.INTERIOR_NODE)) {
						renamedInteriorNode = true;
						if ((newNodes.length == 1)
								&& (newNodes[0].equals(TestExecPluginActivator.RENAMED_NODE))) {
							renamedToInexistentNode = true;
						}
					}
					if (EVENT_COUNT != 4) {
						orderedAtomic = false;
					}
                    if (EVENT_COUNT != 3) {
                        orderedExclusive = false;
                    }

				} else if (topic.equals(COPIED)) {
					newNodes = (String[]) event.getProperty(NEWNODES);
					isNewNodes = true;		
					if (nodes[i].equals(TestExecPluginActivator.INTERIOR_NODE)) {
						copiedFromInteriorNode = true;
						if ((newNodes.length == 1)
								&& (newNodes[0].equals(TestExecPluginActivator.INEXISTENT_NODE))) {
							copiedToInexistentNode = true;
						}
					}
					if (EVENT_COUNT != 5) {
						orderedAtomic = false;
					}
                    if (EVENT_COUNT != 2) {
                        orderedExclusive = false;
                    }
				}
			}
		}
		if (EVENT_COUNT == 5) {
			synchronized (tbc) {
				tbc.notifyAll();
			}
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
	public static void reset() {
		EVENT_COUNT=0;
		orderedAtomic=true;
        orderedExclusive=true;
		isNewNodes=false;
		isNodes=false;
		isSessionId=false;
		isTopic=false;
		addedInexistentNode = false;
		renamedInteriorNode = false;
		renamedToInexistentNode = false;
		replacedLeafNode = false;
		deletedInteriorNode = false;
		copiedFromInteriorNode = false;
		copiedToInexistentNode = false;
		sessionId = -1;
	}
	public static int getEventCount() {
		return EVENT_COUNT;
	}

	public static boolean isOrderedAtomic() {
		return orderedAtomic;
	}
    public static boolean isOrderedExclusive() {
        return orderedExclusive;
    }
	public static boolean isMandatoryProperties() {
		return (isSessionId && isNodes && isTopic);
	}
	public static boolean isAllProperties() {
		return (isNewNodes && isMandatoryProperties());
	}
	public static int getSessionId() {
		return sessionId;
	}
	
}