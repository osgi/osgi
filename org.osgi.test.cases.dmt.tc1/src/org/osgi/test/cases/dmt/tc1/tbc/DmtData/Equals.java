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

/* REVISION HISTORY:
 *
 * Date         Author(s)
 * CR           Headline
 * ===========  ==============================================================
 * 26/01/2005   Andre Assad
 * 1            Implement TCK
 * ===========  ==============================================================
 * 14/02/2005   Leonardo Barros
 * 1            Updates after formal inspection (BTC_MEG_TCK_CODE-INSPR-002)
 */

package org.osgi.test.cases.dmt.tc1.tbc.DmtData;

import info.dmtree.DmtData;
import org.osgi.test.cases.dmt.tc1.tbc.DmtConstants;
import org.osgi.test.cases.dmt.tc1.tbc.DmtTestControl;
/**
 * 
 * This Test Case Validates the implementation of <code>equals</code> method of DmtData, 
 * according to MEG specification
 */
public class Equals {
	private DmtTestControl tbc;
	
	public Equals(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testEquals001();

	}

	/**
	 * Asserts that a DmtData object with FORMAT_STRING is different from each other type of DmtData
	 * 
	 * @spec DmtData.equals(Object)
	 */
	private void testEquals001() {
		try {
			tbc.log("#testEquals001");

			for (int i=DmtData.FORMAT_INTEGER;i<=DmtData.FORMAT_RAW_BINARY;i=i<<1){
				//A DmtData instance can not have FORMAT_NODE,
				if (i!=DmtData.FORMAT_NODE) { 
					DmtData baseData = DmtConstants.getDmtData(i);
					String baseName = DmtConstants.getDmtDataCodeText(i);
					for (int j=DmtData.FORMAT_INTEGER;j<=DmtData.FORMAT_RAW_BINARY;j=j<<1){
						if (i==j) {
							tbc.assertEquals("Asserts that two DmtData with the same format ("+ baseName +") and value are equal",baseData,DmtConstants.getDmtData(j));
							//Obviously format null cannot have a different value 
							if (i!=info.dmtree.DmtData.FORMAT_NULL) {
							    DmtData variantDataDifferentValue = DmtConstants.getDmtData(j,true);
								tbc.assertTrue("Asserts that two DmtData with the same format ("+ baseName +") but different values (\""+ baseData.toString() +"\" x \""+ variantDataDifferentValue.toString() +"\") are different",!baseData.equals(variantDataDifferentValue));
							}
						} else {
							tbc.assertTrue("Asserts that two DmtData with the different formats ("+ baseName +" x "+ DmtConstants.getDmtDataCodeText(j) +") are different",
							    !baseData.equals(DmtConstants.getDmtData(j)));
						}
					}
				}
			}
			
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		}
	}

}
