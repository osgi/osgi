package org.osgi.service.application;


/**
 * ApplicationHandle is an OSGi service interface which represents an instance
 * of an application. It provides the functionality to query and manipulate the
 * lifecycle state of the represented application instance. It defines constants
 * for the lifecycle states.
 * 
 * @modelguid {0967B96E-F06C-4EA1-96E7-3263670F4F49}
 */
public abstract class ApplicationHandle {
	String						instanceId;
	ApplicationDescriptor		descriptor;
	Delegate	delegate;
	static Class				implementation;
	static String				cName;

	{
		try {
			cName = System
					.getProperty("org.osgi.vendor.application.ApplicationHandle");
			implementation = Class.forName(cName);
		}
		catch (Throwable t) {
			// Ignore
		}
	}

	protected ApplicationHandle(String instanceId, ApplicationDescriptor descriptor ) {
		this.instanceId = instanceId;
		this.descriptor = descriptor;
		try {
			delegate = (Delegate) implementation.newInstance();
			delegate.setApplicationHandle(this,this.descriptor.delegate);
		} catch( Throwable t ) {
			// Too bad ...
			System.err
					.println("No implementation available for ApplicationHandle, property is: "
							+ cName);
		}
	}

	/**
	 * The application instance is stopping. This is the state of a stopping
	 * application instance.
	 * 
	 * @modelguid {541E1C88-4DAD-47B1-AAB5-A523BF9AD01E}
	 */
	public final static int	STOPPING	= 0;

	/**
	 * The application instance is running. This is the initial state of a newly
	 * created application instance.
	 * 
	 * @modelguid {8EBD44E3-883B-4515-8EEA-8469F6F16408}
	 */
	public final static int	RUNNING		= 1;

	/**
	 * Get the state of the application instance.
	 * 
	 * @return the state of the application.
	 * 
	 * @throws IllegalStateException if the application handle is unregistered
	 * 
	 * @modelguid {8C7D95E9-A8E2-40F1-9BFD-C55A5B80148F}
	 */
	public abstract int getState();

	/**
	 * Returns the unique identifier of this instance. This value is also
	 * available as a service property of this application handle's service.pid.
	 * 
	 * @throws IllegalStateException if the application handle is unregistered
	 * 
	 * @return the unique identifier of the instance
	 */
	public final String getInstanceID() { return instanceId; }

	/**
	 * Retrieves the application descriptor which represents the application of
	 * this application instance. It should not be null.
	 * 
	 * @return the service reference of the registered application descriptor
	 *         which represents the application of this application instance,
	 *         should not be null
	 * 
	 * @throws IllegalStateException if the application handle is unregistered
	 * 
	 * @modelguid {A8CFA5DA-8F7E-49B7-BA5A-42EDDA6D6B59}
	 */
	public final ApplicationDescriptor getApplicationDescriptor() { return descriptor; }

	/**
	 * The application instance's lifecycle state can be influenced by this
	 * method. It lets the application instance to perform operations to stop
	 * the application safely, e.g. saving its state to a permanent storage.
	 * <p>
	 * The method must check if the lifecycle transition is valid; a STOPPING
	 * application cannot be stopped. If it is invalid then the method must
	 * exit. Otherwise the lifecycle state of the application instance must be
	 * set to STOPPING. Then the destroySpecific() method must be called to
	 * perform any application model specific steps for safe stopping of the
	 * represented application instance.
	 * <p>
	 * At the end the ApplicationHandle must be unregistered. This method should
	 * free all the resources related to this ApplicationHandle.
	 * <p>
	 * When this method is completed the application instance has already made
	 * its operations for safe stopping, the ApplicationHandle has been
	 * unregistered and its related resources has been freed. Further calls on
	 * this application should not be made because they may have unexpected
	 * results.
	 * 
	 * @throws SecurityException if the caller doesn't have "manipulate"
	 *         ApplicationAdminPermission for the corresponding application.
	 * 
	 * @throws Exception is thrown if an exception or an error occurred during
	 *         the method execution.
	 * @throws IllegalStateException if the application handle is unregistered
	 * 
	 * @modelguid {CEAB58E4-91B8-4E7A-AEEB-9C14C812E607}
	 */
	public final void destroy() throws Exception {

	}

	/**
	 * Called by the destroy() method to perform application model specific
	 * steps to stop and destroy an application instance safely.
	 * 
	 * @throws Exception is thrown if an exception or an error occurred during
	 *         the method execution.
	 */
	protected abstract void destroySpecific() throws Exception;

	public interface Delegate {
		void setApplicationHandle(ApplicationHandle d, ApplicationDescriptor.Delegate descriptor );
		boolean destroy();
	}

}