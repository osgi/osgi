/*
 * Copyright (c) The Open Services Gateway Initiative (2004).
 * All Rights Reserved.
 *
 * Implementation of certain elements of the Open Services Gateway Initiative
 * (OSGI) Specification may be subject to third party intellectual property
 * rights, including without limitation, patent rights (such a third party may
 * or may not be a member of OSGi). OSGi is not responsible and shall not be
 * held responsible in any manner for identifying or failing to identify any or
 * all such third party intellectual property rights.
 *
 * This document and the information contained herein are provided on an "AS
 * IS" basis and OSGI DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION HEREIN WILL
 * NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF MERCHANTABILITY OR
 * FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL OSGI BE LIABLE FOR ANY
 * LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF USE OF DATA, INTERRUPTION OF
 * BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR EXEMPLARY, INCIDENTIAL,
 * PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN CONNECTION WITH THIS
 * DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH LOSS OR DAMAGE.
 *
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */

/*
 * REVISION HISTORY:
 *
 * Date         Author(s)
 * CR           Headline
 * ===========  ==============================================================
 * 14/06/2005   Alexandre Alves
 * 14           Implement Meg TCK
 * ===========  ==============================================================
 */

package org.osgi.test.cases.monitor.tb4;

import org.osgi.service.monitor.Monitorable;
import org.osgi.service.monitor.StatusVariable;
import org.osgi.test.cases.monitor.tbc.MonitorConstants;

/**
 * @author Alexandre Alves
 */
public class MonitorableSameSvImpl implements Monitorable {
	private static StatusVariable sv0;
	private static StatusVariable sv1;
	
	static {
		sv0 = new StatusVariable(MonitorConstants.SV_NAME1,StatusVariable.CM_CC,"test");
		sv1 = new StatusVariable(MonitorConstants.SV_NAME1,StatusVariable.CM_DER,"test");
	}

	public String[] getStatusVariableNames() {
		return new String[]{sv0.getID(),sv1.getID()};
	}


	public StatusVariable getStatusVariable(String arg0) throws IllegalArgumentException {
	
		if(arg0==null || arg0.equals(MonitorConstants.INVALID_MONITORABLE_SV)){
			throw new IllegalArgumentException();
		}else {
			if (arg0.equals(MonitorConstants.SV_NAME1)) {
				return sv0;				
			} else {
				throw new IllegalArgumentException();			
			}
		}
	}

	public boolean notifiesOnChange(String arg0) throws IllegalArgumentException {
		
		if(arg0==null || arg0.equals(MonitorConstants.INVALID_MONITORABLE_SV)){
			throw new IllegalArgumentException();
		}else {
			// according to JSTD-MEGTCK-CODE-INSP011.xls, use sv to return false and other to return true
			if (arg0.equals(MonitorConstants.SV_NAME1)) {
				return true;				
			} else {
				throw new IllegalArgumentException();
			}
		}
	}

	public boolean resetStatusVariable(String arg0) throws IllegalArgumentException {
		if(arg0==null || arg0.equals(MonitorConstants.INVALID_MONITORABLE_SV)){
			throw new IllegalArgumentException();
		}else {
			if (arg0.equals(MonitorConstants.SV_NAME1)) {
				return false;				
			} else {
				throw new IllegalArgumentException();
			}
		}			
	}

	public String getDescription(String arg0) throws IllegalArgumentException  {		
	
		if(arg0==null || arg0.equals(MonitorConstants.INVALID_MONITORABLE_SV)){
			throw new IllegalArgumentException();
		}else {
			if (arg0.equals(MonitorConstants.SV_NAME1)) {
				return null;				
			} else {
				throw new IllegalArgumentException();
			}
		}	
	}
 

}
