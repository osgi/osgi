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
 * This test case validates the implementation of <code>AlertItem</code> constructor, 
 * according to MEG specification.
 */
public class AlertItem extends DmtTestControl {
	private String mark = "mark";
	private DmtData data = new DmtData("test");
	private String[] nodeUri = {".","OSGi","Log"};
	private String nodeUriMangled = "./OSGi/Log";
	
	/**
	 * Asserts that the get methods returns the expected value 
	 * using the constructor that takes a nodeUri as a String
	 * 
	 * @spec AlertItem.AlertItem(String,String,String,DmtData)
	 */
	public void testAlertItem001() {
		try {		
			log("#testAlertItem001");
			org.osgi.service.dmt.notification.AlertItem alert = new org.osgi.service.dmt.notification.AlertItem(DmtConstants.OSGi_LOG,DmtConstants.MIMETYPE,mark,data);
			assertEquals("Asserts that the expected data is returned", data,
					alert.getData());
			assertEquals("Asserts that the expected mark is returned", mark,
					alert.getMark());
			assertEquals("Asserts that the expected type is returned",
					DmtConstants.MIMETYPE, alert.getType());
			assertEquals("Asserts that the expected source is returned",
					DmtConstants.OSGi_LOG, alert.getSource());
		} catch(Exception e) {
			failUnexpectedException(e);
		}
			
	}
	/**
	 * Asserts that null can be passed on the source parameter
	 * using the constructor that takes a nodeUri as a String
	 * 
	 * @spec AlertItem.AlertItem(String,String,String,DmtData)
	 */
	public void testAlertItem002() {
		try {		
			log("#testAlertItem002");
            org.osgi.service.dmt.notification.AlertItem alert = new org.osgi.service.dmt.notification.AlertItem((String)null,DmtConstants.MIMETYPE,mark,data);
            assertEquals("Asserts that the expected data is returned", data,
					alert.getData());
			assertEquals("Asserts that the expected mark is returned", mark,
					alert.getMark());
			assertEquals("Asserts that the expected type is returned",
					DmtConstants.MIMETYPE, alert.getType());
			assertNull("Asserts that the expected source is returned", alert
					.getSource());
            
			
		} catch(Exception e) {
			failUnexpectedException(e);
		}
			
	}
    /**
     * Asserts that null can be passed on the type parameter
     * using the constructor that takes a nodeUri as a String
     * 
     * @spec AlertItem.AlertItem(String,String,String,DmtData)
     */
    public void testAlertItem003() {
        try {       
            log("#testAlertItem003");
            org.osgi.service.dmt.notification.AlertItem alert = new org.osgi.service.dmt.notification.AlertItem(DmtConstants.OSGi_LOG,null,mark,data);
            assertEquals("Asserts that the expected data is returned", data,
					alert.getData());
			assertEquals("Asserts that the expected mark is returned", mark,
					alert.getMark());
			assertNull("Asserts that the expected type is returned", alert
					.getType());
			assertEquals("Asserts that the expected source is returned",
					DmtConstants.OSGi_LOG, alert.getSource());
            
            
        } catch(Exception e) {
        	failUnexpectedException(e);
        }
            
    }
    /**
     * Asserts that null can be passed on the mark parameter
     * using the constructor that takes a nodeUri as a String
     * 
     * @spec AlertItem.AlertItem(String,String,String,DmtData)
     */
    public void testAlertItem004() {
        try {       
            log("#testAlertItem004");
            org.osgi.service.dmt.notification.AlertItem alert = new org.osgi.service.dmt.notification.AlertItem(DmtConstants.OSGi_LOG,DmtConstants.MIMETYPE,null,data);
            assertEquals("Asserts that the expected data is returned", data,
					alert.getData());
			assertNull("Asserts that the expected mark is returned", alert
					.getMark());
			assertEquals("Asserts that the expected type is returned",
					DmtConstants.MIMETYPE, alert.getType());
			assertEquals("Asserts that the expected source is returned",
					DmtConstants.OSGi_LOG, alert.getSource());
            
            
        } catch(Exception e) {
        	failUnexpectedException(e);
        }
            
    }
    /**
     * Asserts that null can be passed on the data parameter
     * using the constructor that takes a nodeUri as a String
     * 
     * @spec AlertItem.AlertItem(String,String,String,DmtData)
     */
    public void testAlertItem005() {
        try {       
            log("#testAlertItem005");
            org.osgi.service.dmt.notification.AlertItem alert = new org.osgi.service.dmt.notification.AlertItem(DmtConstants.OSGi_LOG,DmtConstants.MIMETYPE,mark,null);
            assertEquals("Asserts that the expected mark is returned", mark,
					alert.getMark());
			assertEquals("Asserts that the expected type is returned",
					DmtConstants.MIMETYPE, alert.getType());
			assertEquals("Asserts that the expected source is returned",
					DmtConstants.OSGi_LOG, alert.getSource());
			assertNull("Asserts that the expected data is returned", alert
					.getData());
            
        } catch(Exception e) {
        	failUnexpectedException(e);
        }
            
    }
    /**
	 * Asserts that the get methods returns the expected value 
	 * using the constructor that takes a nodeUri as an array of String
	 * 
	 * @spec AlertItem.AlertItem(String[],String,String,DmtData)
	 */
	public void testAlertItem006() {
		try {		
			log("#testAlertItem006");
			org.osgi.service.dmt.notification.AlertItem alert = new org.osgi.service.dmt.notification.AlertItem(nodeUri,DmtConstants.MIMETYPE,mark,data);
			assertEquals("Asserts that the expected data is returned", data,
					alert.getData());
			assertEquals("Asserts that the expected mark is returned", mark,
					alert.getMark());
			assertEquals("Asserts that the expected type is returned",
					DmtConstants.MIMETYPE, alert.getType());
			assertEquals("Asserts that the expected source is returned",
					nodeUriMangled, alert.getSource());
		} catch(Exception e) {
			failUnexpectedException(e);
		}
			
	}
	/**
	 * Asserts that null can be passed on the source parameter
	 * using the constructor that takes a nodeUri as an array of String
	 * 
	 * @spec AlertItem.AlertItem(String[],String,String,DmtData)
	 */
	public void testAlertItem007() {
		try {		
			log("#testAlertItem007");
            org.osgi.service.dmt.notification.AlertItem alert = new org.osgi.service.dmt.notification.AlertItem((String[])null,DmtConstants.MIMETYPE,mark,data);
            assertEquals("Asserts that the expected data is returned", data,
					alert.getData());
			assertEquals("Asserts that the expected mark is returned", mark,
					alert.getMark());
			assertEquals("Asserts that the expected type is returned",
					DmtConstants.MIMETYPE, alert.getType());
			assertNull("Asserts that the expected source is returned", alert
					.getSource());
            
			
		} catch(Exception e) {
			failUnexpectedException(e);
		}
			
	}
    /**
     * Asserts that null can be passed on the type parameter
     * using the constructor that takes a nodeUri as an array of String
     * 
     * @spec AlertItem.AlertItem(String[],String,String,DmtData)
     */
    public void testAlertItem008() {
        try {       
            log("#testAlertItem008");

            org.osgi.service.dmt.notification.AlertItem alert = new org.osgi.service.dmt.notification.AlertItem(nodeUri,null,mark,data);
            assertEquals("Asserts that the expected data is returned", data,
					alert.getData());
			assertEquals("Asserts that the expected mark is returned", mark,
					alert.getMark());
			assertNull("Asserts that the expected type is returned", alert
					.getType());
			assertEquals("Asserts that the expected source is returned",
					nodeUriMangled, alert.getSource());
            
            
        } catch(Exception e) {
        	failUnexpectedException(e);
        }
            
    }
    /**
     * Asserts that null can be passed on the mark parameter
     * using the constructor that takes a nodeUri as an array of String
     * 
     * @spec AlertItem.AlertItem(String[],String,String,DmtData)
     */
    public void testAlertItem009() {
        try {       
            log("#testAlertItem009");
            org.osgi.service.dmt.notification.AlertItem alert = new org.osgi.service.dmt.notification.AlertItem(nodeUri,DmtConstants.MIMETYPE,null,data);
            assertEquals("Asserts that the expected data is returned", data,
					alert.getData());
			assertNull("Asserts that the expected mark is returned", alert
					.getMark());
			assertEquals("Asserts that the expected type is returned",
					DmtConstants.MIMETYPE, alert.getType());
			assertEquals("Asserts that the expected source is returned",
					nodeUriMangled, alert.getSource());
            
            
        } catch(Exception e) {
        	failUnexpectedException(e);
        }
            
    }
    /**
     * Asserts that null can be passed on the data parameter
     * using the constructor that takes a nodeUri as an array of String
     * 
     * @spec AlertItem.AlertItem(String[],String,String,DmtData)
     */
    public void testAlertItem010() {
        try {       
            log("#testAlertItem010");
            org.osgi.service.dmt.notification.AlertItem alert = new org.osgi.service.dmt.notification.AlertItem(nodeUri,DmtConstants.MIMETYPE,mark,null);
            assertEquals("Asserts that the expected mark is returned", mark,
					alert.getMark());
			assertEquals("Asserts that the expected type is returned",
					DmtConstants.MIMETYPE, alert.getType());
			assertEquals("Asserts that the expected source is returned",
					nodeUriMangled, alert.getSource());
			assertNull("Asserts that the expected data is returned", alert
					.getData());
            
        } catch(Exception e) {
        	failUnexpectedException(e);
        }
            
    }
}
