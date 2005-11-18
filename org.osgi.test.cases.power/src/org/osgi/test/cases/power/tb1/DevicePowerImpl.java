package org.osgi.test.cases.power.tb1;

import java.security.AccessController;
import org.osgi.framework.*;
import org.osgi.service.power.*;

/**
 * A test case for the Device Power service.
 */
public class DevicePowerImpl implements DevicePower {

	private int currentDevicePowerState;
	private BundleContext bc;
	private Object lock = new Object();
	
	public DevicePowerImpl(BundleContext bc) {
	  this.bc = bc;
	  currentDevicePowerState = DevicePowerState.D3;
	  bc.registerService(DevicePower.class.getName(), this, null);
	}
	
  /**
   * Returns the current device power state of the device.
   * 
   * @return the current device power state.
   */
  public int getPowerState() {
  	return currentDevicePowerState;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.osgi.service.power.DevicePower#setPowerState(int, boolean)
   */
  public void setPowerState(int powerState, boolean urgency) throws PowerException, SecurityException {
  	try {
  		// TODO Added mask, please check
  		AccessController.checkPermission(new PowerPermission("*", PowerPermission.SET_DEVICE_POWER+PowerPermission.SET_SYSTEM_POWER));
  	}
  	catch (SecurityException se) {
 		// TODO Added mask, please check
  		AccessController.checkPermission(new PowerPermission("<<ALL DEVICES>>", PowerPermission.SET_DEVICE_POWER+PowerPermission.SET_SYSTEM_POWER));
  	}
  	if (powerState == currentDevicePowerState) return;
    if (powerState < DevicePowerState.D0 || powerState > DevicePowerState.D3) {
      throw new PowerException(PowerException.ILLEGAL_STATE_TRANSITION_REQUEST);
    }
    synchronized(lock) { 
	   	int previousDevicePowerState = currentDevicePowerState;
	   	PowerStateEvent event = new PowerStateEvent(this, powerState, previousDevicePowerState, urgency);
	    currentDevicePowerState = powerState;
	  	ServiceReference refs[];
	  	DevicePowerStateListener listener;
	  	Bundle bundle;
	  	try {
	    	refs = bc.getServiceReferences(DevicePowerStateListener.class.getName(), null);
	    	if (refs == null)	return;
	     	for (int i = refs.length; i-- > 0;) {
	     		if (refs[i] == null) continue;
	     		bundle = refs[i].getBundle();
	      	if ((bundle != null) && (bundle.getState() == Bundle.ACTIVE)) {
	      		listener = (DevicePowerStateListener)bc.getService(refs[i]);
	      		if (listener == null) continue;
	      		try {
	      			listener.devicePowerStateChange(event);
	       		} catch (PowerException pe) {
	       		  pe.printStackTrace();
	      		}
	      	}
	     	}
	  	} catch (InvalidSyntaxException ise) {
	  	  // do nothing; filter is null
	  	}
    }
 	}

public void setPowerState(int state) throws SecurityException, IllegalArgumentException {
	// TODO Auto-generated method stub
	
}
  
}