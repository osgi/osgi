package org.osgi.service.application.midlet;

import org.osgi.service.application.ApplicationHandle;

public final class MidletHandle extends ApplicationHandle {
	public final static String PAUSED = "PAUSED";

	protected MidletHandle(String instanceId, MidletDescriptor descriptor) {
		super(instanceId, descriptor );

		this.descriptor = descriptor;
		try {
			delegate = (Delegate) implementation.newInstance();
			delegate.setMidletHandle( this, descriptor, descriptor.delegate );
		} catch( Throwable t ) {
			// Too bad ...
			System.err
					.println("No implementation available for MidletHandle, property is: "
							+ cName);
		}
	}

	public String getState() {
		return delegate.getState();
	}

	protected void destroySpecific() throws Exception {
		delegate.destroySpecific();
	}

	public void pause() throws Exception {
		delegate.pause();
	}

	public void resume() throws Exception {
		delegate.resume();
	}

	MidletDescriptor		descriptor;
	Delegate						delegate;
	
	static Class				implementation;
	static String				cName;
	{
		try {
			cName = System
					.getProperty("org.osgi.vendor.application.midlet.MidletHandle");
			implementation = Class.forName(cName);
		}
		catch (Throwable t) {
			// Ignore
		}
	}
	
	public interface Delegate {
		void setMidletHandle( MidletHandle handle, MidletDescriptor megDesc, MidletDescriptor.Delegate delegate );
		String getState();
		void destroySpecific() throws Exception;
		void pause() throws Exception;
		void resume() throws Exception;
	}
}
