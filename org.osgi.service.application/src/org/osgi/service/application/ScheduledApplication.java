package org.osgi.service.application;

import java.util.Map;

import org.osgi.framework.ServiceReference;

/**
 * It is allowed to schedule an application based on a specific event.
 * ScheduledApplication service keeps the scheduling information. When the
 * specified event is fired a new instance must be launched. Note that launching
 * operation may fail because e.g. the application is locked or the application
 * is a singleton and already has an instance.
 * 
 * @modelguid {625F9BD1-19D5-4EFA-A000-AE8FE1B5593A}
 */
public interface ScheduledApplication {

	/**
	 * Queries the topic of the triggering event. The topic may contain a
	 * trailing asterisk as wildcard.
	 * 
	 * @return the topic of the triggering event
	 * 
	 * @throws IllegalStateException
	 *             if the scheduled application service is unregistered
	 * 
	 * @modelguid {A505D4C7-198B-4D7F-BB35-B72F70B97561}
	 */
	public String getTopic();

	/**
	 * Queries the event filter for the triggering event.
	 * 
	 * @return the event filter for triggering event
	 * 
	 * @throws IllegalStateException
	 *             if the scheduled application service is unregistered
	 * 
	 * @modelguid {A505D4C7-198B-4D7F-BB35-B72F70B97561}
	 */
	public String getEventFilter();

	/**
	 * Queries if the scheduling is recurring.
	 * 
	 * @return true if the scheduling is recurring, otherwise returns false
	 * 
	 * @throws IllegalStateException
	 *             if the scheduled application service is unregistered
	 * 
	 * @modelguid {A505D4C7-198B-4D7F-BB35-B72F70B97561}
	 */
	public boolean isRecurring();

	/**
	 * Retrieves the ApplicationDescriptor which represents the application and
	 * necessary for launching.
	 * 
	 * @return the service reference to the application descriptor that
	 *         represents the scheduled application
	 * 
	 * @throws IllegalStateException
	 *             if the scheduled application service is unregistered
	 * 
	 * @modelguid {55C3C652-E899-48F2-BDBE-6F75EBB6518F}
	 */
	public ServiceReference getApplicationDescriptor();

	/**
	 * Queries the startup arguments specified when the application was
	 * scheduled. The method returns a copy of the arguments, it is not possible
	 * to modify the arguments after scheduling.
	 * 
	 * @return the startup arguments of the scheduled application. It may be
	 *         null if null argument was specified.
	 * 
	 * @throws IllegalStateException
	 *             if the scheduled application service is unregistered
	 */
	public Map getArguments();

	/**
	 * Cancels this schedule of the application.
	 * 
	 * @throws SecurityException
	 *             if the caller doesn't have "schedule"
	 *             ApplicationAdminPermission for the scheduled application.
	 * @throws IllegalStateException
	 *             if the scheduled application service is unregistered
	 * 
	 * @modelguid {5EA8CD87-9BA8-47A3-86A9-F2CBDFD24D2A}
	 */
	public void remove();
}