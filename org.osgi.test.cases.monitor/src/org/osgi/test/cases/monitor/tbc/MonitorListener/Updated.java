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
 * Date         Author(s)
 * CR           Headline
 * ===========  ==============================================================
 * 18/04/2005   Alexandre Alves
 * 14           According to changes performed in interfaces.
 * ===========  ==============================================================
 */
package org.osgi.test.cases.monitor.tbc.MonitorListener;

import org.osgi.service.monitor.StatusVariable;
import org.osgi.test.cases.monitor.tbc.MonitorTestControl;
import org.osgi.test.cases.monitor.tbc.util.MessagesConstants;

/**
 * @methodUnderTest org.osgi.service.monitor.UpdateListener#updated
 * @generalDescription This class tests updated method according with MEG
 *                     specification (rfc0084)
 */
public class Updated {
	private MonitorTestControl tbc;

	public Updated(MonitorTestControl tbc) {
		this.tbc = tbc;
	}
	
	public void run(){
		testUpdated001();
		testUpdated002();
		testUpdated003();
		testUpdated004();
		testUpdated005();
	}
	
	/**
	 * @testID testUpdated001
	 * @testDescription Asserts if IllegalArgumentException is thrown when null
	 *                  is passed as argument
	 */
	public void testUpdated001() {
		try { 
			tbc.getMonitorListener().updated(null, 
					new StatusVariable(
							MonitorTestControl.SV_NAME1, StatusVariable.CM_CC,
							"test1"));
			tbc.failException("", IllegalArgumentException.class);
			
		} catch (IllegalArgumentException e) {
			tbc.pass(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_CORRECTLY_THROWN,
					new String[] { IllegalArgumentException.class.getName() }));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_THROWN, new String[] {
							IllegalArgumentException.class.getName(),
							e.getClass().getName() }));
		}
	}

	/**
	 * @testID testUpdated002
	 * @testDescription Tests if IllegalArgumentException is thrown when
	 *                  invalid characters is passed as parameter
	 */
	public void testUpdated002() {
		try {
			tbc.getMonitorListener().updated(MonitorTestControl.INVALID_ID, 
					new StatusVariable(
							MonitorTestControl.SV_NAME1, StatusVariable.CM_CC,
							"test1"));

			tbc.failException("", IllegalArgumentException.class);
			
		} catch (IllegalArgumentException e) {
			tbc.pass(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_CORRECTLY_THROWN,
					new String[] { IllegalArgumentException.class.getName() }));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_THROWN, new String[] {
							IllegalArgumentException.class.getName(),
							e.getClass().getName() }));
		} 
	}

	/**
	 * @testID testUpdated003
	 * @testDescription Tests if IllegalArgumentException is thrown when an
	 *                  invalid monitorableid is passed as parameter
	 */
	public void testUpdated003() {
		try {
			tbc.getMonitorListener().updated(MonitorTestControl.INVALID_MONITORABLE_SV, 
					new StatusVariable(
							MonitorTestControl.SV_NAME1, StatusVariable.CM_CC,
							"test1"));

			tbc.failException("", IllegalArgumentException.class);
			
		} catch (IllegalArgumentException e) {
			tbc.pass(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_CORRECTLY_THROWN,
					new String[] { IllegalArgumentException.class.getName() }));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_THROWN, new String[] {
							IllegalArgumentException.class.getName(),
							e.getClass().getName() }));
		} 
	}		
	
	/**
	 * @testID testUpdated004
	 * @testDescription Tests if IllegalArgumentException is thrown when an
	 *                  empty string is passed as monitorableid
	 */
	public void testUpdated004() {
		try {
			tbc.getMonitorListener().updated("", 
					new StatusVariable(
							MonitorTestControl.SV_NAME1, StatusVariable.CM_CC,
							"test1"));

			tbc.failException("", IllegalArgumentException.class);
			
		} catch (IllegalArgumentException e) {
			tbc.pass(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_CORRECTLY_THROWN,
					new String[] { IllegalArgumentException.class.getName() }));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_THROWN, new String[] {
							IllegalArgumentException.class.getName(),
							e.getClass().getName() }));
		} 
	}	
	
	/**
	 * @testID testUpdated005
	 * @testDescription Tests if IllegalArgumentException is thrown when 
	 *                  null is passed as statusvariable
	 */
	public void testUpdated005() {
		try {
			tbc.getMonitorListener().updated(MonitorTestControl.SV_MONITORABLEID1, null);

			tbc.failException("", IllegalArgumentException.class);
			
		} catch (IllegalArgumentException e) {
			tbc.pass(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_CORRECTLY_THROWN,
					new String[] { IllegalArgumentException.class.getName() }));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_THROWN, new String[] {
							IllegalArgumentException.class.getName(),
							e.getClass().getName() }));
		} 
	}		

}
