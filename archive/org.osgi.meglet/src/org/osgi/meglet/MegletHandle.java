package org.osgi.meglet;


import org.osgi.service.application.*;

/**
 * This service represents a Meglet instance. It is a specialization of the
 * application handle and provides features specific to the Meglet model.
 */
public abstract class MegletHandle extends ApplicationHandle {
	
	protected MegletHandle(String instanceId, ApplicationDescriptor descriptor ) {
		super(instanceId, descriptor); 
	}
	/**
	 * The Meglet instance is suspended.
	 * 
	 */
	public final static int SUSPENDED = 2;

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
	public abstract void suspend() throws Exception;

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
	public abstract void resume() throws Exception;

}