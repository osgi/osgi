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
 * Jan 26, 2005  Leonardo Barros
 * 1             Implement TCK
 * ============  ==============================================================
 * Feb 14, 2005  Luiz Felipe Guimarães
 * 1             Updates after formal inspection (BTC_MEG_TCK_CODE-INSPR-002)
 * ============  ==============================================================
 */

package org.osgi.test.cases.dmt.tbc.DmtPrincipalPermission;

import org.osgi.test.cases.dmt.tbc.DmtTestControl;

/**
 * This class tests DmtPrincipalPermission constructors
 *                     according with MEG specification (rfc0085)
 */

public class DmtPrincipalPermission {
    
	private DmtTestControl tbc;

	public DmtPrincipalPermission(DmtTestControl tbc) {
		this.tbc = tbc;
	}
	
	public void run(){
		testDmtPrincipalPermission001();
		testDmtPrincipalPermission002();
        testDmtPrincipalPermission003();
        testDmtPrincipalPermission004();
        testDmtPrincipalPermission005();
        testDmtPrincipalPermission006();
        testDmtPrincipalPermission007();
	}

	/**
	 * 
	 *  It asserts if the principal passed in the constructor is equals to
	 * 					DmtPrincipalPermission.getName() method.
	 * 
	 * @spec DmtPrincipalPermission.DmtPrincipalPermission(String)
	 * @spec DmtPrincipalPermission.getName()
	 */
	public void testDmtPrincipalPermission001() {
		tbc
				.assertEquals(
						"Asserts if the principal passed as parameter [DmtPrincipalPermission(DmtTestControl.PRINCIPAL)] is equal to DmtPrincipalPermission().getName() returned value",
						DmtTestControl.PRINCIPAL,
						new org.osgi.service.dmt.DmtPrincipalPermission(DmtTestControl.PRINCIPAL)
								.getName());
	}
	
	/**
	 * 
	 *  It asserts if a null value passed in the constructor as principal
	 * 					throws an NullPointerException
	 * @spec DmtPrincipalPermission.DmtPrincipalPermission(String)
	 */
	public void testDmtPrincipalPermission002() {
		try {

			org.osgi.service.dmt.DmtPrincipalPermission principalPermission = new org.osgi.service.dmt.DmtPrincipalPermission(
					null);
		} catch (NullPointerException e) {
			tbc.pass("NullPointerException caught correctly");
		}
	}
	/**
	 * 
	 *  Tests the constructor which has two string parameters.
	 *                  The second parameter is ignored and the first one
	 *                  sets the principal
	 *                  
	 * @spec DmtPrincipalPermission.DmtPrincipalPermission(String,String)
	 * @spec DmtPrincipalPermission.getName()
	 */
	public void testDmtPrincipalPermission003() {
		tbc
				.assertEquals(
						"Asserts if \"DmtPrincipalPermission\" is equals to DmtPrincipalPermission(DmtTestControl.PRINCIPAL,\"actions\").getName() returned value",
						DmtTestControl.PRINCIPAL,
						new org.osgi.service.dmt.DmtPrincipalPermission(
								DmtTestControl.PRINCIPAL,
								"actions").getName());
	}
    
    /**
     * 
     *  Tests the constructor which has two string parameters.
     *                  The second parameter is ignored so this method asserts if
     *                  DmtPrincipalPermission actions is empty
	 * @spec DmtPrincipalPermission.DmtPrincipalPermission(String,String)
	 * @spec DmtPrincipalPermission.getActions()
     */
    public void testDmtPrincipalPermission004() {
        tbc.assertEquals("Asserts if an empty string is equals to DmtPrincipalPermission(DmtTestControl.PRINCIPAL,\"actions\").getActions() returned value",
                        "",
                        new org.osgi.service.dmt.DmtPrincipalPermission(DmtTestControl.PRINCIPAL,
                                "actions").getActions());
    }
    /**
     * 
     *  Tests the constructor which has two string parameters.
     *                  The second parameter is ignored so this method asserts if
     *                  DmtPrincipalPermission actions is empty
	 * @spec DmtPrincipalPermission.DmtPrincipalPermission(String,String)
	 * @spec DmtPrincipalPermission.getActions()
     */
    public void testDmtPrincipalPermission005() {
        tbc.assertEquals("Asserts if an empty string is equals to DmtPrincipalPermission(DmtTestControl.PRINCIPAL,null).getActions() returned value",
                        "",
                        new org.osgi.service.dmt.DmtPrincipalPermission(DmtTestControl.PRINCIPAL,
                                null).getActions());
    }
    
    /**
     * 
     *  Tests the constructor which has two string parameters.
     *                  To the second method is passed null, so this method asserts if
     *                  the principal passed is correctly returned
	 * @spec DmtPrincipalPermission.DmtPrincipalPermission(String,String)
	 * @spec DmtPrincipalPermission.getName()
     */
    public void testDmtPrincipalPermission006() {
		tbc
				.assertEquals(
						"Asserts if an empty string is equals to DmtPrincipalPermission(DmtTestControl.PRINCIPAL,null).getActions() returned value",
						DmtTestControl.PRINCIPAL,
						new org.osgi.service.dmt.DmtPrincipalPermission(
								DmtTestControl.PRINCIPAL,
								null).getName());
	}
	/**
	 * 
	 *  It asserts if a null value passed in the constructor as
	 *                  principal throws an NullPointerException
	 * @spec DmtPrincipalPermission.DmtPrincipalPermission(String,String)
	 * @spec DmtPrincipalPermission.getActions()
	 */
	public void testDmtPrincipalPermission007() {
		try {

			org.osgi.service.dmt.DmtPrincipalPermission principalPermission = new org.osgi.service.dmt.DmtPrincipalPermission(
					null,null);
			
			// ### so what happens in this case? This is an error isnt it?
		} catch (NullPointerException e) {
			tbc.pass("NullPointerException caught correctly");
		}
	}
    


}
