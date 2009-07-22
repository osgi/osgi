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
 * Aug 3, 2005  Leonardo Barros
 * 33           Implement MEGTCK for the application RFC
 * 2009/05/13   Yan Pujante: copied/adapted from org.osgi.test.cases.policy
 * ===========  ==============================================================
 */
package org.osgi.test.cases.gsm.junit;

public class PolicyConstants
{
    public static final String IMEI_VALID_CODE = "012345678912345";

  	public static final String IMEI_VALID_CODE_WILDCARD = "01234567891234*";

	  public static final String IMEI_INVALID_CODE = "0123456-8912345";

  	public static final String IMEI_CHAR_CODE = "abcdefghijklmno";

  	public static final String IMEI_LESS_DIGIT_CODE = "12345";

  	public static final String IMEI_MORE_DIGIT_CODE = "1234567890123456";

    public static final String IMSI_VALID_CODE = "012345678912345";

    public static final String IMSI_VALID_CODE_WILDCARD = "01234567891234*";

    public static final String IMSI_CHAR_CODE = "abcdefghijklmno";

    public static final String IMSI_LESS_DIGIT_CODE = "12345";

    public static final String IMSI_INVALID_CODE = "0123456-8912345";

    public static final String IMSI_MORE_DIGIT_CODE = "1234567890123456";

  	public static final String INVALID_CODE = "@#$%sA!&_";
}