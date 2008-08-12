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
 * Apr 17, 2006  Luiz Felipe Guimaraes
 * 283           [MEGTCK][DMT] DMT API changes
 * ============  ==============================================================
 */

package org.osgi.test.cases.dmt.tc2.tb1.DmtEvent;

import info.dmtree.DmtEvent;
import info.dmtree.DmtEventListener;



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


