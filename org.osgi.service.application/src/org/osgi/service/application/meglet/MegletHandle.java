package org.osgi.service.application.meglet;

import org.osgi.service.application.ApplicationHandle;

/**
 * This service represents a Meglet instance. It is a specialization of the
 * application handle and provides features specific to the Meglet model.
 */
public final class MegletHandle extends ApplicationHandle {

	protected MegletHandle(String instanceId, MegletDescriptor descriptor) {
		super(instanceId, descriptor );

		this.descriptor = descriptor;
		try {
			delegate = (Delegate) implementation.newInstance();
			delegate.setMegletHandle( this, descriptor, descriptor.delegate );
		} catch( Throwable t ) {
			// Too bad ...
			System.err
					.println("No implementation available for MegletHandle, property is: "
							+ cName);
		}
	}

	/**
	 * The Meglet instance is suspended.
	 */
	public final static String SUSPENDED = "SUSPENDED";

	/**
	 * Returns the state of the Meglet instance specific to the Meglet model.
	 * 
	 * @throws IllegalStateException
	 *             if the Meglet handle is unregistered
	 * 
	 * @return the state of the Meglet instance
	 */
	public String getState() {
		return delegate.getState();
	}

	/**
	 * Destroys a Meglet according to the Meglet model. It calls the associated
	 * Meglet instance's stop() method with null parameter.
	 *  
	 */
	protected void destroySpecific() throws Exception {
		delegate.destroySpecific();
	}

	/**
	 * Suspends the Melet instance. It calls the associated Meglet instance's
	 * stop() method and passes a non-null output stream as a parameter. It must
	 * preserve the contents of the output stream written by the Meglet instance
	 * even across device restarts. The same content must be provided to a
	 * resuming Meglet instance.
	 * 
	 * @throws SecurityException
	 *             if the caller doesn't have "manipulate"
	 *             ApplicationAdminPermission for the corresponding application.
	 * @throws IllegalStateException
	 *             if the Meglet handle is unregistered
	 */
	public void suspend() throws Exception {
		delegate.suspend();
	}

	/**
	 * Resumes the Meglet instance. It calls the associated Meglet instance's
	 * start() method and passes a non-null input stream as a parameter. It must
	 * have the same contents that was saved by the suspending Meglet instance.
	 * The same startup arguments must also be passed to the resuming Meglet
	 * instance that was passed to the first starting instance.
	 * 
	 * @throws SecurityException
	 *             if the caller doesn't have "manipulate"
	 *             ApplicationAdminPermission for the corresponding application.
	 * @throws IllegalStateException
	 *             if the Meglet handle is unregistered
	 */
	public void resume() throws Exception {
		delegate.resume();
	}

	MegletDescriptor		descriptor;
	Delegate						delegate;
	
	static Class				implementation;
	static String				cName;
	{
		try {
			cName = System
					.getProperty("org.osgi.vendor.application.meglet.MegletHandle");
			implementation = Class.forName(cName);
		}
		catch (Throwable t) {
			// Ignore
		}
	}
	
	public interface Delegate {
		void setMegletHandle( MegletHandle handle, MegletDescriptor megDesc, MegletDescriptor.Delegate delegate );
		String getState();
		void destroySpecific() throws Exception;
		void suspend() throws Exception;
		void resume() throws Exception;
	}
}
