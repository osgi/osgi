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
 * Date          Author(s)
 * CR            Headline
 * ============  ==============================================================
 * 06/05/2005    Andre Assad
 * 76            Implement Test Cases for Deployment Configuration
 * ============  ==============================================================
 */

package org.osgi.test.cases.deploymentadmin.tbc;

import java.util.Dictionary;

import org.osgi.service.cm.ManagedServiceFactory;

/**
 * @author Andre Assad
 *
 */

public interface TestingManagedServiceFactory extends ManagedServiceFactory {
	
	public static final String ATTRIBUTE_A = "ATTRIBUTE_A";
	public static Integer[] ATTRIBUTE_A_VALUE = {new Integer(1)};
	public static final String ATTRIBUTE_B = "ATTRIBUTE_B";
	public static Float[] ATTRIBUTE_B_VALUE = {new Float(0)};
	public static final String ATTRIBUTE_C = "ATTRIBUTE_C";
	public static String[] ATTRIBUTE_C_VALUE = {"", ""};
	
	public String getPid();
	public Dictionary getProperties();
	public boolean isUpdated();
}
