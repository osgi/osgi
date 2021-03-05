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

/* REVISION HISTORY:
 *
 * Date         Author(s)
 * CR           Headline
 * ===========  ==============================================================
 * 05/07/2005   Luiz Felipe Guimaraes
 * 1            Implement TCK
 * ===========  ==============================================================
 */

package org.osgi.test.cases.dmt.tc1.tbc.AlertItem;

import org.osgi.service.dmt.DmtData;

import org.osgi.test.cases.dmt.tc1.tbc.DmtConstants;
import org.osgi.test.cases.dmt.tc1.tbc.DmtTestControl;

/**
 * This class validates the implementation of <code>toString<code> method of AlertItem, 
 * according to MEG specification.
 */
public class ToString extends DmtTestControl {
	/**
	 * Asserts that the toString() returns the expected value
	 * 
	 * @spec AlertItem.toString()
	 */
	public void testToString001() {
		try {		
			log("#testToString001");
			String mark = "mark";
			DmtData data = new DmtData("test");
			org.osgi.service.dmt.notification.AlertItem alert = new org.osgi.service.dmt.notification.AlertItem(DmtConstants.OSGi_LOG,DmtConstants.MIMETYPE,mark,data);
			assertEquals("Asserts that the expected string is returned",
					"AlertItem(" + alert.getSource() + ", " + alert.getType()
							+ ", " + alert.getMark() + ", " + alert.getData()
							+ ")", alert.toString());
		} catch(Exception e) {
			failUnexpectedException(e);
		}
			
	}
	/**
	 * Asserts that toString() returns null when it is passed in the constructor
	 * 
	 * @spec AlertItem.toString()
	 */
	public void testToString002() {
		try {		
			log("#testToString002");
			org.osgi.service.dmt.notification.AlertItem alert = new org.osgi.service.dmt.notification.AlertItem((String)null,null,null,null);
			assertEquals("Asserts that the expected string is returned",
					"AlertItem(null, null, null, null)", alert.toString());
		} catch(Exception e) {
			failUnexpectedException(e);
		}
			
	}
}
