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
 * Abr 14, 2005  Luiz Felipe Guimaraes
 * 46            [MEGTCK][DMT] Implement AdminPermission Test Cases
 * ============  ==============================================================
 * Jun 17, 2005  Alexandre Alves
 * 28            [MEGTCK][DMT] Implement test cases for DmtSession.close()
 * ============  ==============================================================
 */
package org.osgi.test.cases.dmt.tc2.tbc.Activators;

import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;
import org.osgi.test.cases.dmt.tc2.tbc.DmtConstants;
import org.osgi.test.cases.dmt.tc2.tbc.DmtTestControl;
import org.osgi.test.cases.dmt.tc2.tbc.Plugin.ExecPlugin.TestExecPluginActivator;

/**
 * @author Luiz Felipe Guimaraes
 */
public class EventHandlerImpl implements EventHandler {

	private static int EVENT_COUNT = 0;

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
	@Override
	public void handleEvent(Event event) {

		String properties[] = event.getPropertyNames();

		EVENT_COUNT++;

		// validate the values in properties
		for (int z = 0; z < properties.length; z++) {
			if (properties[z].equals(DmtConstants.SESSION_ID)) {
				isSessionId = true;
			} else if (properties[z].equals(DmtConstants.NODES)) {
				isNodes = true;
			} else if (properties[z].equals(DmtConstants.TOPIC)) {
				isTopic = true;
			}
		}
		try { 
		    sessionId = Integer.parseInt(event.getProperty(DmtConstants.SESSION_ID).toString());
		} catch (Exception e) {
		    sessionId=-1;
		}
		
		if (isMandatoryProperties()) {
		    
			String topic = (String) event.getProperty(DmtConstants.TOPIC);
			String[] nodes = (String[]) event.getProperty(DmtConstants.NODES);
			String[] newNodes = null;
			for (int i = 0; i < nodes.length; i++) {
				if (topic.equals(DmtConstants.ADDED)) {
					if (nodes[i].equals(TestExecPluginActivator.INEXISTENT_NODE)) {
						addedInexistentNode = true;
					}
					if (EVENT_COUNT != 1) {
						orderedAtomic = false;
					}
                    if (EVENT_COUNT != 4) {
                        orderedExclusive = false;
                    }


				} else if (topic.equals(DmtConstants.DELETED)) {
					if (nodes[i].equals(TestExecPluginActivator.INTERIOR_NODE)) {
						deletedInteriorNode = true;
					}
					if (EVENT_COUNT != 2) {
						orderedAtomic = false;
					}
                    if (EVENT_COUNT != 5) {
                        orderedExclusive = false;
                    }


				} else if (topic.equals(DmtConstants.REPLACED)) {
					if (nodes[i].equals(TestExecPluginActivator.LEAF_NODE)) {
						replacedLeafNode = true;
					}
					if (EVENT_COUNT != 3) {
						orderedAtomic = false;
					}
                    if (EVENT_COUNT != 1) {
                        orderedExclusive= false;
                    }
				} else if (topic.equals(DmtConstants.RENAMED)) {
					newNodes = (String[]) event.getProperty(DmtConstants.NEWNODES);
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

				} else if (topic.equals(DmtConstants.COPIED)) {
					newNodes = (String[]) event.getProperty(DmtConstants.NEWNODES);
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
