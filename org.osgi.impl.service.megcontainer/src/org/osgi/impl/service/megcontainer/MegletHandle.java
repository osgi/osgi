package org.osgi.impl.service.megcontainer;


import org.osgi.framework.ServiceReference;
import org.osgi.service.application.ApplicationHandle;

/**
 * This service represents a Meglet instance. It is a specialization of the
 * application handle and provides features specific to the Meglet model.
 */
public final class MegletHandle extends ApplicationHandle {

	/**
	 * The Meglet instance is suspended.
	 * 
	 * @modelguid {8EBD44E3-883B-4515-8EEA-8469F6F16408}
	 */
	public final static int SUSPENDED = 2;

	/**
	 * Returns the state of the Meglet instance specific to the Meglet model.
	 * 
	 * @throws IllegalStateException
	 *             if the Meglet handle is unregistered
	 * 
	 * @return the state of the Meglet instance
	 */
	public int getState() {
		return 0;
	}

	/**
	 * Returns the instance id of the Meglet instance. Must be unique on the
	 * device.
	 * 
	 * @throws IllegalStateException
	 *             if the Meglet handle is unregistered
	 * 
	 * @return the instance id of the Meglet instance
	 */
	public String getInstanceID() {
		return null;
	}

	/**
	 * Returns service reference to the application descriptor of the Meglet to
	 * which this Meglet instance belongs to.
	 * 
	 * @return the application descriptor of the Meglet to which this Meglet
	 *         instance belongs to
	 * 
	 * @throws IllegalStateException
	 *             if the Meglet handle is unregistered
	 */
	public ServiceReference getApplicationDescriptor() {
		return null;
	}

	/**
	 * Destroys a Meglet according to the Meglet model. It calls the associated
	 * Meglet instance's stop() method with null parameter.
	 *  
	 */
	protected void destroySpecific() throws Exception {
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
	}

}