package org.osgi.service.application;

import java.util.Map;

import org.osgi.framework.ServiceReference;

/**
 * It is allowed to schedule an application based on a specific event.
 * ScheduledApplication service keeps the scheduling information. When the
 * specified event is fired a new instance must be launched. Note that launching
 * operation may fail because e.g. the application is locked or the application
 * is a singleton and already has an instance.
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
	 */
	public String getTopic();

	/**
	 * Queries the event filter for the triggering event.
	 * 
	 * @return the event filter for triggering event
	 * 
	 * @throws IllegalStateException
	 *             if the scheduled application service is unregistered
	 */
	public String getEventFilter();

	/**
	 * Queries if the scheduling is recurring.
	 * 
	 * @return true if the scheduling is recurring, otherwise returns false
	 * 
	 * @throws IllegalStateException
	 *             if the scheduled application service is unregistered
	 */
	public boolean isRecurring();

	/**
	 * Retrieves the ApplicationDescriptor which represents the application and
	 * necessary for launching.
	 * 
	 * @return the application descriptor that
	 *         represents the scheduled application
	 * 
	 * @throws IllegalStateException
	 *             if the scheduled application service is unregistered
	 */
	public ApplicationDescriptor getApplicationDescriptor();

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
	 */
	public void remove();
}