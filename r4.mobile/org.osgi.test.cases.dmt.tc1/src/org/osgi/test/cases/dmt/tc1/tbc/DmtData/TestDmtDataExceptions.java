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
 * Date          Author(s)
 * CR            Headline
 * ============  ==============================================================
 * Aug 01, 2005  Luiz Felipe Guimaraes
 * 1             Implement TCK
 * ============  ==============================================================
 */

package org.osgi.test.cases.dmt.tc1.tbc.DmtData;

import info.dmtree.DmtData;
import info.dmtree.DmtIllegalStateException;

import org.osgi.test.cases.dmt.tc1.tbc.DmtConstants;
import org.osgi.test.cases.dmt.tc1.tbc.DmtTestControl;

/**
 * 
 * This test case asserts that DmtIllegalStateException is thrown if we try to get a different format from the defined.
 * e.g: Calling DmtData.getFloat() in a DmtData.FORMAT_STRING
 * 
 * It also asserts that it is not possible to create a DmtData(String value,int format) if the format is not one of the
 * allowed. (FORMAT_STRING, FORMAT_XML, FORMAT_DATE, FORMAT_TIME).
 * e.g: Calling new DmtData("String",FORMAT_BINARY)
 * 
 * Finally, it ensures that the DmtData(String value,int format) must be parseabet an ISO 8601 date or time
 * (in case of FORMAT_DATE or FORMAT_TIME)  
 */
public class TestDmtDataExceptions {
	private DmtTestControl tbc;

	public TestDmtDataExceptions(DmtTestControl tbc) {
		this.tbc = tbc;

	}

	public void run() {
	    testDmtDataExceptions001();
	    testDmtDataExceptions002();
	    testDmtDataExceptions003();
	    testDmtDataExceptions004();
	    testDmtDataExceptions005();
	    testDmtDataExceptions006();
        testDmtDataExceptions007();
        testDmtDataExceptions008();
        testDmtDataExceptions009();
        testDmtDataExceptions010();
        testDmtDataExceptions011();
        testDmtDataExceptions012();
        testDmtDataExceptions013();
        testDmtDataExceptions014();
	}
    /**
     * Asserts that NullPointerException is thrown if a date format is constructed and value is null
     * 
     * @spec DmtData.DmtData(String,int)
     */
    private void testDmtDataExceptions001() {
        try {       
            tbc.log("#testDmtDataExceptions001");
            new DmtData(null,DmtData.FORMAT_DATE);
            tbc.failException("", NullPointerException.class);
        } catch (NullPointerException e) {
            tbc.pass("Asserts that NullPointerException is thrown if a date is constructed and value is null");
        } catch (Exception e) {
        	tbc.failExpectedOtherException(NullPointerException.class,e);
        }
    }
    
    /**
     * Asserts that NullPointerException is thrown if a time format is constructed and value is null
     * 
     * @spec DmtData.DmtData(String,int)
     */
    private void testDmtDataExceptions002() {
        try {       
            tbc.log("#testDmtDataExceptions002");
            new DmtData(null,DmtData.FORMAT_TIME);
            tbc.failException("", NullPointerException.class);
        } catch (NullPointerException e) {
            tbc.pass("Asserts that NullPointerException is thrown if a date is constructed and value is null");
        } catch (Exception e) {
        	tbc.failExpectedOtherException(NullPointerException.class,e);
        }
    }
    
    /**
     * Asserts that NullPointerException is thrown if a bin format is constructed and bytes parameter is null
     * 
     * @spec DmtData.DmtData(byte[])
     */
    private void testDmtDataExceptions003() {
        try {       
            tbc.log("#testDmtDataExceptions003");
            new DmtData((byte[])null);
            tbc.failException("", NullPointerException.class);
        } catch (NullPointerException e) {
            tbc.pass("Asserts that NullPointerException is thrown if a bin format is constructed and bytes parameter is null");
        } catch (Exception e) {
        	tbc.failExpectedOtherException(NullPointerException.class,e);
        }
    }
    
    /**
     * Asserts that NullPointerException is thrown if a base64 format is constructed and bytes parameter is null
     * 
     * @spec DmtData.DmtData(byte[],boolean)
     */
    private void testDmtDataExceptions004() {
        try {       
            tbc.log("#testDmtDataExceptions004");
            new DmtData((byte[])null,true);
            tbc.failException("", NullPointerException.class);
        } catch (NullPointerException e) {
            tbc.pass("Asserts that NullPointerException is thrown if a base64 format is constructed and bytes parameter is null");
        } catch (Exception e) {
        	tbc.failExpectedOtherException(NullPointerException.class,e);
        }
    }
    
    
    /**
     * Asserts that NullPointerException is thrown if a raw string format is 
     * constructed and formatName parameter is null
     * 
     * @spec DmtData.DmtData(String,String)
     */
    private void testDmtDataExceptions005() {
        try {       
            tbc.log("#testDmtDataExceptions005");
            new DmtData(null,"a");
            tbc.failException("", NullPointerException.class);
        } catch (NullPointerException e) {
            tbc.pass("Asserts that NullPointerException is thrown if a raw string format is constructed and formatName parameter is null");
        } catch (Exception e) {
        	tbc.failExpectedOtherException(NullPointerException.class,e);
        }
    }
    /**
     * Asserts that NullPointerException is thrown if a raw string format is 
     * constructed and data parameter is null
     * 
     * @spec DmtData.DmtData(String,String)
     */
    private void testDmtDataExceptions006() {
        try {       
            tbc.log("#testDmtDataExceptions006");
            new DmtData("a",(String)null);
            tbc.failException("", NullPointerException.class);
        } catch (NullPointerException e) {
            tbc.pass("Asserts that NullPointerException is thrown if a raw string format is constructed and data parameter is null");
        } catch (Exception e) {
        	tbc.failExpectedOtherException(NullPointerException.class,e);
        }
    }
    
    /**
     * Asserts that NullPointerException is thrown if a raw string format is 
     * constructed and formatName parameter is null
     * 
     * @spec DmtData.DmtData(String,String)
     */
    private void testDmtDataExceptions007() {
        try {       
            tbc.log("#testDmtDataExceptions007");
            new DmtData(null,new byte[0]);
            tbc.failException("", NullPointerException.class);
        } catch (NullPointerException e) {
            tbc.pass("Asserts that NullPointerException is thrown if a raw string format is constructed and formatName parameter is null");
        } catch (Exception e) {
        	tbc.failExpectedOtherException(NullPointerException.class,e);
        }
    }
    /**
     * Asserts that NullPointerException is thrown if a raw binary format is 
     * constructed and data parameter is null
     * 
     * @spec DmtData.DmtData(String,String)
     */
    private void testDmtDataExceptions008() {
        try {       
            tbc.log("#testDmtDataExceptions008");
            new DmtData("a",(byte[])null);
            tbc.failException("", NullPointerException.class);
        } catch (NullPointerException e) {
            tbc.pass("Asserts that NullPointerException is thrown if a raw binary format is constructed and data parameter is null");
        } catch (Exception e) {
        	tbc.failExpectedOtherException(NullPointerException.class,e);
        }
    }
	/**
	 * Asserts that DmtIllegalStateException is thrown when an incorrect DmtData is gotten (for all cases)
	 * 
	 * @spec 117.12.5 DmtData
	 */
	private void testDmtDataExceptions009() {
		try {		
			tbc.log("#testDmtDataExceptions009");
			//It's from FORMAT_INTEGER [1] to FORMAT_RAW_BINARY[4096]   
			for (int i=DmtData.FORMAT_INTEGER;i<=DmtData.FORMAT_RAW_BINARY;i=i<<1){

				//Exception: FORMAT_NODE because a DmtData instance can not have this value
				if (i!=DmtData.FORMAT_NODE) {
					String baseName = DmtConstants.getDmtDataCodeText(i);
				    for (int j=DmtData.FORMAT_INTEGER;j<=DmtData.FORMAT_RAW_BINARY;j=j<<1){
				    	
				    	//Checks only different formats, because equal ones do not throw any exception (it is checked at org.osgi.test.cases.dmt.tc1.tbc.DmtData) 
				    	//FORMAT_NULL and FORMAT_NODE doesnt have a get associated
				    	if (i!=j && j!=DmtData.FORMAT_NULL && j!=DmtData.FORMAT_NODE) {
							tbc.assertTrue("Asserts that DmtIllegalStateException is thrown when "+ 
									DmtConstants.getExpectedDmtDataMethod(j) +" is called in a DmtData."+ baseName,
									invalidGetThrowsException(DmtConstants.getDmtData(i),j));
						}
					}
				}
				
			}
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		}
	}
	
	/**
	 * This method asserts that IllegalArgumentException is thrown if DmtData(String value,int format) is created
	 * with a unspecified format 
	 * 
	 * @spec DmtData.DmtData(String,int)
	 */
	private void testDmtDataExceptions010() {
		try {		
			tbc.log("#testDmtDataExceptions010");
			
			int[] invalidStringFormats =  new int[] {
					info.dmtree.DmtData.FORMAT_BASE64,
					info.dmtree.DmtData.FORMAT_BINARY,
					info.dmtree.DmtData.FORMAT_BOOLEAN,
					info.dmtree.DmtData.FORMAT_FLOAT,
					info.dmtree.DmtData.FORMAT_INTEGER,
					info.dmtree.DmtData.FORMAT_NODE,
					info.dmtree.DmtData.FORMAT_NULL,
					info.dmtree.DmtData.FORMAT_RAW_BINARY,
					info.dmtree.DmtData.FORMAT_RAW_STRING,
					};
			
			for (int i=0; i<invalidStringFormats.length; i++) {
				tbc.assertTrue("Asserts that IllegalArgumentException is thrown when an incorrect format ("+ DmtConstants.getDmtDataCodeText(invalidStringFormats[i]) +") is passed in DmtData(String value,int format)",
						invalidFormatThrowsException(invalidStringFormats[i]));
			}
		} catch(Exception e) {
			tbc.failUnexpectedException(e);

		}
	}
	
	/**
	 * This method asserts that IllegalArgumentException is thrown if DmtData(String value,int format) is created
	 * but 'value' is not a valid string for the given format (time)
	 * 
	 * @spec DmtData.DmtData(String,int)
	 */
	private void testDmtDataExceptions011() {
		try {		
			tbc.log("#testDmtDataExceptions011");
			
			String[] invalidTimes =  new String[] {
			    	"12000", //Less than 6 digits
					"120000Z0", //More than 7 characters
					"120000z", // the 7th character is not 'Z'
					"12000Z", // A character is not expected within the time
					"126000", // 60th minute is invalid
					"120060", // 60th second is invalid
			};
			
			for (int i=0; i<invalidTimes.length; i++) {
				tbc.assertTrue("Asserts that IllegalArgumentException is thrown when an invalid value (\""+ invalidTimes[i] +"\") is passed in a FORMAT_TIME DmtData.",
						invalidFormatThrowsException(invalidTimes[i],DmtData.FORMAT_TIME));
			}
		} catch(Exception e) {
			tbc.failUnexpectedException(e);

		}
	}
	
	/**
	 * This method asserts that no exception is thrown if DmtData(String value,int format) is created
	 * and 'value' is a valid string for the given format (time)
	 * 
	 * @spec DmtData.DmtData(String,int)
	 */
	private void testDmtDataExceptions012() {
		try {		
			tbc.log("#testDmtDataExceptions012");
			
			String[] validTimes =  new String[] {
			    	"000000",
			    	"235959",
					"240000", // 24th hour is valid			    	
			    	"000000Z",
			    	"235959Z",
			    	"240000Z"
			};
			
			for (int i=0; i<validTimes.length; i++) {
				tbc.assertTrue("Asserts that no exception is thrown when an invalid value (\""+ validTimes[i] +"\") is passed in a FORMAT_TIME DmtData.",
						!invalidFormatThrowsException(validTimes[i],DmtData.FORMAT_TIME));
			}
		} catch(Exception e) {
			tbc.failUnexpectedException(e);

		}
	}
	
	/**
	 * This method asserts that IllegalArgumentException is thrown if DmtData(String value,int format) is created
	 * but 'value' is not a valid string for the given format (date)
	 * 
	 * @spec DmtData.DmtData(String,int)
	 */
	private void testDmtDataExceptions013() {
		try {		
			tbc.log("#testDmtDataExceptions013");
			
			String[] invalidDates =  new String[] {
					"2005010", //Less than 8 digits
					"200501010", //More than 8 digits
					"2005A101", // 8 characters but not all digits
					"20051301", // 13th month is invalid
					"20050229", // There is no 29 of february in 2005
					"20050431", // April 31th is invalid
					"20050631", // June 31th is invalid
					"20050931", // September 31th is invalid
					"20051131", // November 31th is invalid
			};
			
			for (int i=0; i<invalidDates.length; i++) {
				tbc.assertTrue("Asserts that IllegalArgumentException is thrown when an invalid value (\""+ invalidDates[i] +"\") is passed in a FORMAT_DATE DmtData.",
						invalidFormatThrowsException(invalidDates[i],DmtData.FORMAT_DATE));
			}
			
		} catch(Exception e) {
			tbc.failUnexpectedException(e);

		}
	}
	/**
	 * This method asserts that no exception is thrown if DmtData(String value,int format) is created
	 * and 'value' is a valid string for the given format (date)
	 * 
	 * @spec DmtData.DmtData(String,int)
	 */
	private void testDmtDataExceptions014() {
		try {		
			tbc.log("#testDmtDataExceptions014");
			
			String[] validDates =  new String[] {
					"20050131", 
					"20040229", // february 29th 2004 is valid
					"20050228",
					"20050331",
					"20050430",
					"20050531",
					"20050630",
					"20050731",
					"20050831",
					"20050930",
					"20051031",
					"20051130",
					"20051231",
			};
			for (int i=0; i<validDates.length; i++) {
				tbc.assertTrue("Asserts that no exception is thrown when a valid value (\""+ validDates[i] +"\") is passed in a FORMAT_DATE DmtData.",
						!invalidFormatThrowsException(validDates[i],DmtData.FORMAT_DATE));
			}
		} catch(Exception e) {
			tbc.failUnexpectedException(e);

		}
	}
	private boolean invalidFormatThrowsException(int format) {
	    return invalidFormatThrowsException("a",format);
	}
	private boolean invalidFormatThrowsException(String value, int format) {
		boolean threw = false;
		try {
			new info.dmtree.DmtData(value,format);
		} catch (IllegalArgumentException e) {
			threw = true;
		}

		return threw;
	}
	private boolean invalidGetThrowsException(info.dmtree.DmtData data, int format) {
		boolean threw = false;
		try {
			switch (format) {
			case info.dmtree.DmtData.FORMAT_BASE64:
				data.getBase64();
				break;
			case info.dmtree.DmtData.FORMAT_BINARY:
				data.getBinary();
				break;
			case info.dmtree.DmtData.FORMAT_BOOLEAN:
				data.getBoolean();
				break;
			case info.dmtree.DmtData.FORMAT_DATE:
				data.getDate();
				break;
			case info.dmtree.DmtData.FORMAT_FLOAT:
				data.getFloat();
				break;
			case info.dmtree.DmtData.FORMAT_INTEGER:
				data.getInt();
				break;
			case info.dmtree.DmtData.FORMAT_STRING:
				data.getString();
				break;
			case info.dmtree.DmtData.FORMAT_TIME:
				data.getTime();
				break;
			case info.dmtree.DmtData.FORMAT_XML:
				data.getXml();
				break;
			case info.dmtree.DmtData.FORMAT_RAW_BINARY:
				data.getRawBinary();
				break;				
			case info.dmtree.DmtData.FORMAT_RAW_STRING:
				data.getRawString();
				break;								
			}
		} catch (DmtIllegalStateException e) {
			threw = true;
		}

		return threw;
		
	}
	
}
