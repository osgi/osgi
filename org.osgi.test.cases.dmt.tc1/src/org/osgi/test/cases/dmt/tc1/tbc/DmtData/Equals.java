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
 * 26/01/2005   Andre Assad
 * 1            Implement TCK
 * ===========  ==============================================================
 * 14/02/2005   Leonardo Barros
 * 1            Updates after formal inspection (BTC_MEG_TCK_CODE-INSPR-002)
 */

package org.osgi.test.cases.dmt.tc1.tbc.DmtData;

import org.osgi.service.dmt.DmtData;

import org.osgi.test.cases.dmt.tc1.tbc.DmtConstants;
import org.osgi.test.cases.dmt.tc1.tbc.DmtTestControl;
/**
 * 
 * This Test Case Validates the implementation of <code>equals</code> method of DmtData, 
 * according to MEG specification
 */
public class Equals extends DmtTestControl {
	/**
	 * Asserts that a DmtData object with FORMAT_STRING is different from each other type of DmtData
	 * 
	 * @spec DmtData.equals(Object)
	 */
	public void testEquals001() {
		try {
			log("#testEquals001");

			for (int i=DmtData.FORMAT_INTEGER;i<=DmtData.FORMAT_RAW_BINARY;i=i<<1){
				//A DmtData instance can not have FORMAT_NODE,
				if (i!=DmtData.FORMAT_NODE) { 
					DmtData baseData = DmtConstants.getDmtData(i);
					String baseName = DmtConstants.getDmtDataCodeText(i);
					for (int j=DmtData.FORMAT_INTEGER;j<=DmtData.FORMAT_RAW_BINARY;j=j<<1){
						if (i==j) {
							assertEquals(
									"Asserts that two DmtData with the same format ("
											+ baseName
											+ ") and value are equal",
									baseData, DmtConstants.getDmtData(j));
							//Obviously format null cannot have a different value 
							if (i!=org.osgi.service.dmt.DmtData.FORMAT_NULL) {
							    DmtData variantDataDifferentValue = DmtConstants.getDmtData(j,true);
								assertTrue(
										"Asserts that two DmtData with the same format ("
												+ baseName
												+ ") but different values (\""
												+ baseData.toString()
												+ "\" x \""
												+ variantDataDifferentValue
														.toString()
												+ "\") are different",
										!baseData
												.equals(variantDataDifferentValue));
							}
						} else {
							assertTrue(
									"Asserts that two DmtData with the different formats ("
											+ baseName
											+ " x "
											+ DmtConstants
													.getDmtDataCodeText(j)
											+ ") are different",
							    !baseData.equals(DmtConstants.getDmtData(j)));
						}
					}
				}
			}
			
		} catch (Exception e) {
			failUnexpectedException(e);
		}
	}

}
