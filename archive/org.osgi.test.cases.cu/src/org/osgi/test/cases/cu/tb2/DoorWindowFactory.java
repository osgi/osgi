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
package org.osgi.test.cases.cu.tb2;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import org.osgi.service.cu.ControlUnit;
import org.osgi.service.cu.ControlUnitException;
import org.osgi.service.cu.admin.*;
import org.osgi.service.cu.admin.spi.*;

/**
 * Simulates a CU Factory of doors and windows.
 *  
 * @version $Revision$
 */
public class DoorWindowFactory implements ControlUnitFactory {
	
	private byte nbEltCreated;
	private Hashtable elts;
	private CUAdminCallback adminCallback;
	private String type;
	
	/**
	 * Create a new factory with a number of elements limited to 4
	 * 
	 * @param type the factory type door or window
	 */
	public DoorWindowFactory(String type) {
		elts = new Hashtable();
		this.type = type;
		nbEltCreated = 1;
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
		return (DoorWindow)elts.get(cuId);
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
		Enumeration elements = elts.keys();
		Vector result = new Vector( elts.size() );
		
		while (elements.hasMoreElements()) {
			result.addElement(elements.nextElement());
		}
		
	    return toStringArray(result);
	}

	/**
	 * @param finderId
	 * @param arguments
	 * @return
	 * @throws Exception
	 * @see org.osgi.service.cu.admin.spi.ControlUnitFactory#findControlUnits(java.lang.String, java.lang.Object)
	 */
	public String[] findControlUnits(String finderId, Object arguments)
			throws ControlUnitException {
		if (finderId == "$find.all")
			return listControlUnits();
		else throw (new ControlUnitException(ControlUnitException.NO_SUCH_ACTION_ERROR));
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
	public Object queryStateVariable(String cuId, String varId)
			throws ControlUnitException {
		
		DoorWindow elt = (DoorWindow)elts.get(cuId);

		if (elt != null)
			return elt.queryStateVariable(varId);
		else throw (new ControlUnitException(ControlUnitException.NO_SUCH_STATE_VARIABLE_ERROR));
	}

	/**
	 * @param cuId
	 * @param actionId
	 * @param arguments
	 * @return
	 * @throws Exception
	 * @see org.osgi.service.cu.admin.spi.ControlUnitFactory#invokeAction(java.lang.String, java.lang.String, java.lang.Object)
	 */
	public Object invokeAction(String cuId, String actionId, Object arguments)
			throws ControlUnitException {
		DoorWindow elt;
		elt = (DoorWindow)elts.get(cuId);

		if (elt != null)
			return elt.invokeAction(actionId, arguments);
		else throw (new ControlUnitException(ControlUnitException.NO_SUCH_ACTION_ERROR));
	}

	/**
	 * @param constructorId
	 * @param arguments
	 * @return
	 * @throws Exception
	 * @see org.osgi.service.cu.admin.spi.ControlUnitFactory#createControlUnit(java.lang.String, java.lang.Object)
	 */
	public String createControlUnit(String constructorId, Object arguments)
			throws ControlUnitException {
		
		if ((constructorId != "$create.door") && (constructorId != "$create.window"))
			throw (new ControlUnitException(ControlUnitException.NO_SUCH_ACTION_ERROR));
			
		if (arguments != null)
			throw (new ControlUnitException(ControlUnitException.ILLEGAL_ACTION_ARGUMENTS_ERROR));
		
		if (((constructorId == "$create.door") || (constructorId == "$create.window")) && (arguments == null)) {
			if (nbEltCreated < 5) {
				
				// Create a new instance of Door and register it
				// in the list
				String name = type + "." + nbEltCreated++; 
				elts.put(name, new DoorWindow(type, name, adminCallback));
				
				// Notify CUAdmin if call back is set
				if (adminCallback != null) {
					adminCallback.controlUnitEvent(ControlUnitAdminListener.CONTROL_UNIT_ADDED, type, name);
					
					if (constructorId == "$create.window")
						adminCallback.hierarchyChanged(HierarchyListener.ATTACHED, type, name, "door", "door.1");
				}
			}
			else
				return null;
		}
		
		return null;
	}

	/**
	 * @param controlUnitId
	 * @throws Exception
	 * @see org.osgi.service.cu.admin.spi.ControlUnitFactory#destroyControlUnit(java.lang.String)
	 */
	public void destroyControlUnit(String controlUnitId) throws ControlUnitException {
		
		DoorWindow elt = (DoorWindow)elts.remove(controlUnitId);
		
		// Notify CUAdmin if call back is set
		if ((elt != null) && (adminCallback != null)) {
			nbEltCreated--;
			adminCallback.controlUnitEvent(ControlUnitAdminListener.CONTROL_UNIT_REMOVED, type, controlUnitId);
		}
	}
	
	/**
	 * Copies all elements of a Vector to a String array.
	 * 
	 * @param v
	 * @return
	 */
	private String[] toStringArray(Vector v) {
		String[] ret = new String[v.size()];
		for (int i = 0; i < v.size(); i++)
			ret[i] = (String) v.elementAt(i);
		return ret;
	}
}

/**
 * Represents a CU on a door or window. 
 *
 * @version $Revision$
 */
class DoorWindow implements ControlUnit {
	private String id;
	private String type;
	private byte state;
	private CUAdminCallback adminCallback;
	
	public byte CLOSED = 1;
	public byte OPENED = 2;
	
	/**
	 * Create a new DoorWindow object
	 * 
	 * @param
	 * @param
	 * 
	 */
	public DoorWindow(String type, String id, CUAdminCallback adminCallback) {
		this.adminCallback = adminCallback;
		this.state = CLOSED;
		this.type = type;
		this.id = id;
	}
	
	public byte getState() {
		return state;
	}
	
	public void open() {
		state = OPENED;
	}
	
	public void close() {
		state = CLOSED;
	}

	/**
	 * @return
	 * @see org.osgi.service.cu.ControlUnit#getId()
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return
	 * @see org.osgi.service.cu.ControlUnit#getType()
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param varId
	 * @return
	 * @throws Exception
	 * @see org.osgi.service.cu.ControlUnit#queryStateVariable(java.lang.String)
	 */
	public Object queryStateVariable(String varId) throws ControlUnitException {		
		if (varId == "state")
			return new Byte (getState());
		else throw (new ControlUnitException(ControlUnitException.NO_SUCH_STATE_VARIABLE_ERROR));
	}

	/**
	 * @param actionId
	 * @param arguments
	 * @return
	 * @throws Exception
	 * @see org.osgi.service.cu.ControlUnit#invokeAction(java.lang.String, java.lang.Object)
	 */
	public Object invokeAction(String actionId, Object arguments) throws ControlUnitException {
		
		// The following actions can be invoked on a door
		// void open()
		// void close()
		if (((actionId == "door.open") || (actionId == "window.open")) && (arguments == null)) {
			if (state == CLOSED) {
				open();
				adminCallback.stateVariableChanged(type, id, "state", new Byte(state));
			}
			return null;
		}
		else if (((actionId == "door.close") || (actionId == "window.close")) && (arguments == null)){
			if (state == OPENED) {
				close();
				adminCallback.stateVariableChanged(type, id, "state", new Byte(state));
			}
			return null;
		}
		else throw (new ControlUnitException(ControlUnitException.NO_SUCH_ACTION_ERROR));
	}
}
