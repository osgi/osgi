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
 * Apr 17, 2006  Luiz Felipe Guimaraes
 * 283           [MEGTCK][DMT] DMT API changes
 * ============  ==============================================================
 */

package org.osgi.test.cases.dmt.tc2.tb1.DmtEvent;

import org.osgi.service.dmt.DmtEvent;
import org.osgi.service.dmt.DmtEventListener;



/**
 * @author Luiz Felipe Guimaraes
 * 
 */
public class DmtEventListenerImpl  implements DmtEventListener{
	int count = 0;
	boolean error = false;
	boolean ordered = true;
	
	private int[] order = new int[] { DmtEvent.ADDED, DmtEvent.DELETED, DmtEvent.REPLACED, DmtEvent.RENAMED, DmtEvent.COPIED };

	private DmtEvent[] events = new DmtEvent[5];
	
	@Override
	public void changeOccurred(DmtEvent event) {
		//Only 5 events must be sent (0 to 4)
		if (count<5) {
			//The order must be added, deleted, replaced, renamed and copied 
			if (event.getType()!=order[count]) {
				ordered=false;
			}
			events[count] = event;
		} else {
			error=true;
		}
		count++;
	} 
	
	public boolean isOrdered() {
		return ordered & (count==5);
	}
	public int getCount() {
		return count;
	}
	
	public DmtEvent[] getDmtEvents() {
		return events;
	}
	
}


