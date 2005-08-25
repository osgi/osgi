package org.osgi.impl.service.midletcontainer;

import java.io.File;
import java.lang.reflect.Method;
import java.util.*;
import javax.microedition.midlet.MIDlet;
import org.osgi.framework.*;
import org.osgi.service.application.midlet.MidletDescriptor;
import org.osgi.service.application.midlet.MidletHandle;
import org.osgi.impl.service.application.OATContainerInterface;

public final class MidletHandleImpl implements ServiceListener,
		org.osgi.service.application.midlet.MidletHandle.Delegate {
	
	private String					      status;
	private MIDlet					      midlet;
	private ServiceReference		  appDescRef;
	private ServiceRegistration		serviceReg;
	private File					        suspendedFileName;
	private String					      pid;
	private MidletHandle			    midletHandle;
	private MidletDescriptorImpl	midletDelegate;
	private MidletContainer			  midletContainer;
	private BundleContext			    bc;
	private Vector					      serviceRefs;
	private Object					      baseClass;
	public MidletHandleImpl() {
		suspendedFileName = null;
	}

	public String getState() {
		if (status == null)
			throw new RuntimeException("Invalid state!");
		else
			return status;
	}

	public ServiceReference startHandle(Map args) throws Exception {
		if (status != null)
			throw new Exception("Invalid State");
		if (midlet != null) {
			midletCommand(midlet, "MidletHandle", midletHandle);
			registerToOATHash( args );
			midletCommand(midlet, "Start", args);
			setStatus("RUNNING");
			registerAppHandle();
			return serviceReg.getReference();
		}
		else {
			throw new Exception("Invalid midlet handle!");
		}
	}

	public void destroySpecific() throws Exception {
		if (status == null)
			throw new Exception("Invalid State");
		if (midlet != null) {
			midletCommand(midlet, "Destroy", new Boolean(true));
			midlet = null;
		}
		setStatus(null);
		unregisterFromOATHash();
		unregisterAppHandle();
		bc.removeServiceListener(this);
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
		props.put("service.pid", midletHandle.getInstanceID());
		props.put("application.state", status);
		props.put("application.descriptor", appDescRef
				.getProperty("service.pid"));
		return props;
	}

	private void registerToOATHash( Map args ) throws Exception {
		midletContainer.getOATInterface().createApplicationContext(baseClass, midletHandle, args, midletDelegate.getBundle());
	}
	
	private void unregisterFromOATHash() throws Exception {
		midletContainer.getOATInterface().removeApplicationContext( baseClass );		
	}
	
	private void registerAppHandle() {
		serviceReg = bc.registerService(
				"org.osgi.service.application.ApplicationHandle", midletHandle,
				properties());
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

	public void setMidletHandle(
			MidletHandle handle,
			MidletDescriptor appDesc,
			org.osgi.service.application.midlet.MidletDescriptor.Delegate delegate) {
		midletHandle = handle;
		midletDelegate = (MidletDescriptorImpl) delegate;
		status = null;
	}

	public MidletHandle getMidletHandle() {
		return midletHandle;
	}

	public void init(BundleContext bc, MidletContainer midletContainer,
			MIDlet midlet, Vector refs, Object baseClass) {
		this.bc = bc;
		this.midletContainer = midletContainer;
		this.midlet = midlet;
		this.baseClass = baseClass;
		serviceRefs = refs;
		bc.addServiceListener(this);
		appDescRef = midletContainer.getReference(midletDelegate);
		pid = (String) appDescRef.getProperty("service.pid");
	}

	public void serviceChanged(ServiceEvent e) {
		if (e.getType() == 4 && status != null && midletHandle != null
				&& serviceRefs.contains(e.getServiceReference()))
			try {
				midletHandle.destroy();
			}
			catch (Exception exception) {}
	}
}
