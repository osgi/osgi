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
 * 29/03/2005   Alexandre Santos
 * 14           Implement MEG TCK
 * ===========  ==============================================================
 */

package org.osgi.test.cases.monitor.tb3;

import org.osgi.service.monitor.Monitorable;
import org.osgi.service.monitor.StatusVariable;
import org.osgi.test.cases.monitor.tbc.MonitorConstants;
import org.osgi.test.cases.monitor.tbc.MonitorTestControl;
import org.osgi.test.cases.monitor.tbc.TestingMonitorable;

/**
 * @author Leonardo Barros
 */
public class MonitorableImpl implements Monitorable, TestingMonitorable {
	private static StatusVariable sv0;
	private static StatusVariable sv1;
	private MonitorTestControl tbc = null;
	
	static {
		sv0 = new StatusVariable(MonitorConstants.SV_NAME1,StatusVariable.CM_CC,"test");
		sv1 = new StatusVariable(MonitorConstants.SV_NAME2,StatusVariable.CM_DER,"test");
	}
	
    /**
     * Returns the list of StatusVariable identifiers published by this
     * Monitorable. A StatusVariable name is unique within the scope of a
     * Monitorable. The array contains the elements in no particular order.
     * 
     * @return the name of StatusVariables published by this object
     */
	public String[] getStatusVariableNames() {
		log("#Start getStatusVariableNames()");
		return new String[]{sv0.getID(),sv1.getID()};
	}


    /**
     * Returns the StatusVariable object addressed by its identifier. The
     * StatusVariable will hold the value taken at the time of this method call.
     * 
     * @param id the identifier of the StatusVariable. The identifier does not
     *        contain the Monitorable_id, i.e. this is the name and not the path
     *        of the status variable.
     * @return the StatusVariable object
     * @throws IllegalArgumentException if the name points to a non existing
     *         StatusVariable
     */
	public StatusVariable getStatusVariable(String arg0) throws IllegalArgumentException {
		log("#Start getStatusVariable()");
		
		if(arg0==null || arg0.equals(MonitorConstants.INVALID_MONITORABLE_SV)){
			throw new IllegalArgumentException();
		}else {
			if (arg0.equals(MonitorConstants.SV_NAME1)) {
				return sv0;				
			} else if (arg0.equals(MonitorConstants.SV_NAME2) ){
				return sv1;
			} else {
				tbc.fail("Receive an argument different of original passed as parameter.");
				throw new IllegalArgumentException();			
			}
		}
	}

    /**
     * Tells whether the StatusVariable provider is able to send instant
     * notifications when the given StatusVariable changes. If the Monitorable
     * supports sending change updates it must notify the MonitorListener when
     * the value of the StatusVariable changes. The Monitorable finds the
     * MonitorListener service through the Service Registry.
     * 
     * @param id the identifier of the StatusVariable. The identifier
     * does not contain the Monitorable_id, i.e. this is the name and
     * not the path of the status variable.
     * @return <code>true</code> if the Monitorable can send notification when
     *         the given StatusVariable chages, <code>false</code> otherwise
     * @throws IllegalArgumentException
     *             if the path is invalid or points to a non existing
     *             StatusVariable
     */
	public boolean notifiesOnChange(String arg0) throws IllegalArgumentException {
		log("#Start notifiesOnChange()");
		log("#notifiesOnChange receives " + arg0 + " as argument.");
		
		if(arg0==null || arg0.equals(MonitorConstants.INVALID_MONITORABLE_SV)){
			throw new IllegalArgumentException();
		}else {
			// according to JSTD-MEGTCK-CODE-INSP011.xls, use sv to return false and other to return true
			if ((arg0.equals(MonitorConstants.SV_NAME1)) || (arg0.equals(MonitorConstants.SV_NAME2))) {
				return false;				
			} else {
				tbc.fail("Receive an argument different of original passed as parameter.");
				throw new IllegalArgumentException();
			}
		}
	}		

    /**
     * Issues a request to reset a given StatusVariable. Depending on the
     * semantics of the StatusVariable this call may or may not succeed: it
     * makes sense to reset a counter to its starting value, but e.g. a
     * StatusVariable of type String might not have a meaningful default value.
     * Note that for numeric StatusVariables the starting value may not
     * necessarily be 0. Resetting a StatusVariable triggers a monitor event.
     * 
     * @param id the identifier of the StatusVariable.
     * @return <code>true</code> if the Monitorable could successfully reset
     *         the given StatusVariable, <code>false</code> otherwise
     * @throws IllegalArgumentException if the id points to a non existing
     *         StatusVariable
     */
	public boolean resetStatusVariable(String arg0) throws IllegalArgumentException {
		log("#Start resetStatusVariable()");
		
		if(arg0==null || arg0.equals(MonitorConstants.INVALID_MONITORABLE_SV)){
			throw new IllegalArgumentException();
		}else {
			if ((arg0.equals(MonitorConstants.SV_NAME1)) || (arg0.equals(MonitorConstants.SV_NAME2))) {
				return false;				
			} else {
				tbc.fail("Receive an argument different of original passed as parameter.");
				throw new IllegalArgumentException();
			}
		}			
	}

    /**
     * Returns a human readable description of a StatusVariable. This can be
     * used by management systems on their GUI. Null return value is allowed.
     * 
     * @param id Identifier of a StatusVariable published by this Monitorable
     * @return the human readable description of this StatusVariable or null if
     *         it is not set
     * @throws IllegalArgumentException if the path points to a non existing
     *         StatusVariable
     */
	public String getDescription(String arg0) throws IllegalArgumentException  {		
		log("#Start getDescription()");
		
		if(arg0==null || arg0.equals(MonitorConstants.INVALID_MONITORABLE_SV)){
			throw new IllegalArgumentException();
		}else {
			if ((arg0.equals(MonitorConstants.SV_NAME1)) || (arg0.equals(MonitorConstants.SV_NAME2))) {
				return null;				
			} else {
				tbc.fail("Receive an argument different of original passed as parameter.");
				throw new IllegalArgumentException();
			}
		}	
	}
	
	/**
	 * This method has to be used by StartScheduledJob, to receive the values of
	 * the updated statusvariable in the report.
	 */
	public void setStatusVariable(StatusVariable sv) throws IllegalArgumentException {
		if (sv.getID().equals(sv0.getID())) {
			sv0 = sv;			
		} else if (sv.getID().equals(sv1.getID())) {
			sv1 = sv;
		} else {
			throw new IllegalArgumentException("No StatusVariable with this id was found.");
		}
		
	}	
	
	/**
	 *	To set the MonitorTestControl 
	 */
	public synchronized void setMonitorTestControlInterface(MonitorTestControl tbc) {
		this.tbc = tbc;		
	}	
	
	synchronized void log(String s) {
		if ( tbc != null )
			tbc.log(s);
	}


}
