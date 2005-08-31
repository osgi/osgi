package org.osgi.impl.service.midletcontainer;

import java.lang.reflect.Method;
import java.util.*;
import javax.microedition.midlet.MIDlet;
import org.osgi.framework.*;
import org.osgi.service.application.*;

public final class MidletHandle extends ApplicationHandle {
	
	private String					      status;
	private MIDlet					      midlet;
	private ServiceRegistration		serviceReg;
	private MidletDescriptor    	midletDescriptor;
	private MidletContainer			  midletContainer;
	private BundleContext			    bc;
	private Object					      baseClass;

	public final static String PAUSED = "PAUSED";
	
	public MidletHandle(BundleContext bc, String instanceId, MidletDescriptor descriptor,  
			                MidletContainer midletContainer, MIDlet midlet, Object baseClass) {
		super(instanceId, descriptor);
		this.bc = bc;
		this.midletContainer = midletContainer;
		this.midlet = midlet;
		this.baseClass = baseClass;
		midletDescriptor = descriptor;
		status = null;
	}

	public String getState() {
		if (status == null)
			throw new RuntimeException("Invalid state!");
		else
			return status;
	}

	public void startHandle(Map args) throws Exception {
		if (status != null)
			throw new Exception("Invalid State");
		if (midlet != null) {
			midletCommand(midlet, "MidletHandle", this);
			registerToOATHash( args );
			midletCommand(midlet, "Start", args);
			setStatus("RUNNING");
			registerAppHandle();
		}
		else {
			throw new Exception("Invalid midlet handle!");
		}
	}

	public void destroySpecific() throws Exception {
		if (status == null)
			throw new Exception("Invalid State");
		if (midlet != null)
			midletCommand(midlet, "Destroy", new Boolean(true));
		
		setStatus(null);
		unregisterFromOATHash();
		unregisterAppHandle();
		midlet = null;
	}

	public void pause() throws Exception {
		if (status != "RUNNING")
			throw new Exception("Invalid State");
		if (midlet != null) {
			midletCommand(midlet, "Pause", null);
			setStatus("PAUSED");
		}
		else {
			throw new Exception("Invalid midlet handle!");
		}
	}

	public void resume() throws Exception {
		if (status != "PAUSED")
			throw new Exception("Invalid State");
		if (midlet != null) {
			midletCommand(midlet, "Resume", null);
			setStatus("RUNNING");
		}
		else {
			throw new Exception("Invalid midlet handle!");
		}
	}

	private void setStatus(String status) {
		this.status = status;
		if (status != null && serviceReg != null)
			serviceReg.setProperties(properties());
	}

	private Hashtable properties() {
		Hashtable props = new Hashtable();
		props.put(Constants.SERVICE_PID, getInstanceID());
		props.put(ApplicationHandle.APPLICATION_STATE, status);
		props.put(ApplicationHandle.APPLICATION_DESCRIPTOR, midletDescriptor.getPID());
		return props;
	}

	private void registerToOATHash( Map args ) throws Exception {
		midletContainer.getOATInterface().createApplicationContext( midlet, this, args, midletDescriptor.getBundle());
	}
	
	private void unregisterFromOATHash() throws Exception {
		midletContainer.getOATInterface().removeApplicationContext( midlet );		
	}
	
	private void registerAppHandle() {
		serviceReg = bc.registerService(
				ApplicationHandle.class.getName(), this, properties());
	}

	private void unregisterAppHandle() {
		if (serviceReg != null) {
			serviceReg.unregister();
			serviceReg = null;
		}
	}

	void midletCommand(MIDlet midlet, String command, Object param)
			throws Exception {
		Class midletClass = javax.microedition.midlet.MIDlet.class;
		Method setupMethod = midletClass.getDeclaredMethod("reflectionMethod",
				new Class[] {java.lang.String.class, java.lang.Object.class});
		setupMethod.setAccessible(true);
		setupMethod.invoke(midlet, new Object[] {command, param});
	}
}
