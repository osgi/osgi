/*
 * $Header$
 * 
 * Copyright (c) The OSGi Alliance (2005). All Rights Reserved.
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
package org.osgi.test.cases.diagnostics.tb2;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import org.osgi.service.cu.ControlUnit;
import org.osgi.service.cu.ControlUnitException;
import org.osgi.service.cu.admin.*;
import org.osgi.service.cu.admin.spi.*;

/**
 * Simulates a CU Factory of doors.
 *  
 * @version $Revision$
 */
public class DoorFactory implements ControlUnitFactory {
	
	private Hashtable doors;
  private byte doorCreatedIndex;
	private CUAdminCallback adminCallback;
	
	/**
	 * Create a new factory with a number of elements limited to 4
	 */
	public DoorFactory() {
		doors = new Hashtable();
		doorCreatedIndex = 1;
	}

	/**
	 * @param adminCallback
	 * @see org.osgi.service.cu.admin.spi.ManagedControlUnit#setControlUnitCallback(org.osgi.service.cu.admin.spi.CUAdminCallback)
	 */
	public void setControlUnitCallback(CUAdminCallback adminCallback) {
		this.adminCallback = adminCallback;
	}

	/**
	 * @param cuId
	 * @return
	 * @see org.osgi.service.cu.admin.spi.ControlUnitFactory#getControlUnit(java.lang.String)
	 */
	public ControlUnit getControlUnit(String cuId) {
		return (Door)doors.get(cuId);
	}

	/**
	 * @param parentType
	 * @param parentId
	 * @return
	 * @see org.osgi.service.cu.admin.spi.ControlUnitFactory#getControlUnits(java.lang.String, java.lang.String)
	 */
	public String[] getControlUnits(String parentType, String parentId) {
		if ((parentType == null) && (parentId == null))
			return listControlUnits();
			
		return null;
	}

	/**
	 * @return
	 * @see org.osgi.service.cu.admin.spi.ControlUnitFactory#listControlUnits()
	 */
	public String[] listControlUnits() {
		Enumeration elements = doors.keys();
		Vector result = new Vector(doors.size());		
		while (elements.hasMoreElements()) {
			result.addElement(elements.nextElement());
		}
	  return copyToArray(result);
	}

	/**
	 * @param finderId
	 * @param arguments
	 * @return
	 * @throws Exception
	 * @see org.osgi.service.cu.admin.spi.ControlUnitFactory#findControlUnits(java.lang.String, java.lang.Object)
	 */
	public String[] findControlUnits(String finderId, Object arguments) throws ControlUnitException {
		if (finderId == "$find.all") {
			return listControlUnits();
    }
    throw (new ControlUnitException(ControlUnitException.NO_SUCH_ACTION_ERROR));
	}

	/**
	 * @param childId
	 * @param parentType
	 * @return
	 * @see org.osgi.service.cu.admin.spi.ControlUnitFactory#getParents(java.lang.String, java.lang.String)
	 */
	public String[] getParents(String childId, String parentType) {
		return null;
	}

	/**
	 * @param cuId
	 * @param varId
	 * @return
	 * @throws Exception
	 * @see org.osgi.service.cu.admin.spi.ControlUnitFactory#queryStateVariable(java.lang.String, java.lang.String)
	 */
	public Object queryStateVariable(String cuId, String varId) throws ControlUnitException {		
		Door door = (Door)doors.get(cuId);
		if (door == null) {
      throw (new ControlUnitException(ControlUnitException.NO_SUCH_STATE_VARIABLE_ERROR));
    }
    return door.queryStateVariable(varId);
	}

	/**
	 * @param cuId
	 * @param actionId
	 * @param arguments
	 * @return
	 * @throws Exception
	 * @see org.osgi.service.cu.admin.spi.ControlUnitFactory#invokeAction(java.lang.String, java.lang.String, java.lang.Object)
	 */
	public Object invokeAction(String cuId, String actionId, Object arguments) throws ControlUnitException {
		Door door = (Door)doors.get(cuId);
		if (door == null) {
      throw (new ControlUnitException(ControlUnitException.NO_SUCH_ACTION_ERROR));
    }
    return door.invokeAction(actionId, arguments);
	}

	/**
	 * @param constructorId
	 * @param arguments
	 * @return
	 * @throws Exception
	 * @see org.osgi.service.cu.admin.spi.ControlUnitFactory#createControlUnit(java.lang.String, java.lang.Object)
	 */
	public String createControlUnit(String constructorId, Object arguments) throws ControlUnitException {
		if (constructorId != "$create.door") {
			throw (new ControlUnitException(ControlUnitException.NO_SUCH_ACTION_ERROR));
    }
			
		if (arguments != null) {
			throw (new ControlUnitException(ControlUnitException.ILLEGAL_ACTION_ARGUMENTS_ERROR));
    }
		
		if ((constructorId == "$create.door") && (arguments == null)) {
			if (doorCreatedIndex < 5) {
				// Create a new instance of Door and register it in the list
				String name = "door." + doorCreatedIndex++; 
				doors.put(name, new Door(name, adminCallback));
				
				// Notify CUAdmin if call back is set
				if (adminCallback != null) {
					adminCallback.controlUnitEvent(ControlUnitAdminListener.CONTROL_UNIT_ADDED, "door", name);
				}
			}
		}
		
		return null;
	}

	/**
	 * @param controlUnitId
	 * @throws Exception
	 * @see org.osgi.service.cu.admin.spi.ControlUnitFactory#destroyControlUnit(java.lang.String)
	 */
	public void destroyControlUnit(String controlUnitId) throws ControlUnitException {
		Door door = (Door)doors.remove(controlUnitId);
    if (door == null) {
      throw new ControlUnitException("CU does not exist", ControlUnitException.UNDETERMINED_ERROR);
    }
    
		doorCreatedIndex--;
    if ((adminCallback != null)) {
      //Notify CUAdmin if call back is set
			adminCallback.controlUnitEvent(ControlUnitAdminListener.CONTROL_UNIT_REMOVED, "door", controlUnitId);
		}
	}
	
	private String[] copyToArray(Vector v) {
		String[] result = new String[v.size()];
		for (int i = 0; i < v.size(); i++) {
      result[i] = (String) v.elementAt(i);
    }
		return result;
	}
}
