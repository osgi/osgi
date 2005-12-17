/*******************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.osgi.framework.internal.core;

import org.osgi.framework.Bundle;
import org.osgi.service.startlevel.StartLevel;

/**
 * StartLevel service for the OSGi specification.
 *
 * Framework service which allows management of framework and bundle startlevels.
 *
 * If present, there will only be a single instance of this service
 * registered in the framework.
 *
 */
public class StartLevelImpl implements StartLevel {

	protected StartLevelManager manager;
	protected Bundle owner;

	/** This constructor is called by the StartLevel factory */
	protected StartLevelImpl(Bundle owner, Framework framework) {
		this.owner = owner;
		this.manager = framework.startLevelManager;
	}

	/**
	 * Return the initial start level value that is assigned
	 * to a Bundle when it is first installed.
	 *
	 * @return The initial start level value for Bundles.
	 * @see #setInitialBundleStartLevel
	 */
	public int getInitialBundleStartLevel() {
		return manager.getInitialBundleStartLevel();
	}

	/**
	 * Set the initial start level value that is assigned
	 * to a Bundle when it is first installed.
	 *
	 * <p>The initial bundle start level will be set to the specified start level. The
	 * initial bundle start level value will be persistently recorded
	 * by the Framework.
	 *
	 * <p>When a Bundle is installed via <tt>BundleContext.installBundle</tt>,
	 * it is assigned the initial bundle start level value.
	 *
	 * <p>The default initial bundle start level value is 1
	 * unless this method has been
	 * called to assign a different initial bundle
	 * start level value.
	 *
	 * <p>This method does not change the start level values of installed
	 * bundles.
	 *
	 * @param startlevel The initial start level for newly installed bundles.
	 * @throws IllegalArgumentException If the specified start level is less than or
	 * equal to zero.
	 * @throws SecurityException if the caller does not have the
	 * <tt>AdminPermission</tt> and the Java runtime environment supports
	 * permissions.
	 */
	public void setInitialBundleStartLevel(int startlevel) {
		manager.setInitialBundleStartLevel(startlevel);
	}

	/**
	 * Return the active start level value of the Framework.
	 *
	 * If the Framework is in the process of changing the start level
	 * this method must return the active start level if this
	 * differs from the requested start level.
	 *
	 * @return The active start level value of the Framework.
	 */
	public int getStartLevel() {
		return manager.getStartLevel();
	}

	/**
	 * Modify the active start level of the Framework.
	 *
	 * <p>The Framework will move to the requested start level. This method
	 * will return immediately to the caller and the start level
	 * change will occur asynchronously on another thread.
	 *
	 * <p>If the specified start level is
	 * higher than the active start level, the
	 * Framework will continue to increase the start level
	 * until the Framework has reached the specified start level,
	 * starting bundles at each
	 * start level which are persistently marked to be started as described in the
	 * <tt>Bundle.start</tt> method.
	 *
	 * At each intermediate start level value on the
	 * way to and including the target start level, the framework must:
	 * <ol>
	 * <li>Change the active start level to the intermediate start level value.
	 * <li>Start bundles at the intermediate start level in
	 * ascending order by <tt>Bundle.getBundleId</tt>.
	 * </ol>
	 * When this process completes after the specified start level is reached,
	 * the Framework will broadcast a Framework event of
	 * type <tt>FrameworkEvent.STARTLEVEL_CHANGED</tt> to announce it has moved to the specified
	 * start level.
	 *
	 * <p>If the specified start level is lower than the active start level, the
	 * Framework will continue to decrease the start level
	 * until the Framework has reached the specified start level
	 * stopping bundles at each
	 * start level as described in the <tt>Bundle.stop</tt> method except that their
	 * persistently recorded state indicates that they must be restarted in the
	 * future.
	 *
	 * At each intermediate start level value on the
	 * way to and including the specified start level, the framework must:
	 * <ol>
	 * <li>Stop bundles at the intermediate start level in
	 * descending order by <tt>Bundle.getBundleId</tt>.
	 * <li>Change the active start level to the intermediate start level value.
	 * </ol>
	 * When this process completes after the specified start level is reached,
	 * the Framework will broadcast a Framework event of
	 * type <tt>FrameworkEvent.STARTLEVEL_CHANGED</tt> to announce it has moved to the specified
	 * start level.
	 *
	 * <p>If the specified start level is equal to the active start level, then
	 * no bundles are started or stopped, however, the Framework must broadcast
	 * a Framework event of type <tt>FrameworkEvent.STARTLEVEL_CHANGED</tt> to
	 * announce it has finished moving to the specified start level. This
	 * event may arrive before the this method return.
	 *
	 * @param newSL The requested start level for the Framework.
	 * @throws IllegalArgumentException If the specified start level is less than or
	 * equal to zero.
	 * @throws SecurityException If the caller does not have the
	 * <tt>AdminPermission</tt> and the Java runtime environment supports
	 * permissions.
	 */
	public void setStartLevel(int newSL) {
		manager.setStartLevel(newSL, owner);
	}

	/**
	 * Return the persistent state of the specified bundle.
	 *
	 * <p>This method returns the persistent state of a bundle.
	 * The persistent state of a bundle indicates whether a bundle
	 * is persistently marked to be started when it's start level is
	 * reached.
	 *
	 * @return <tt>true</tt> if the bundle is persistently marked to be started,
	 * <tt>false</tt> if the bundle is not persistently marked to be started.
	 * @exception java.lang.IllegalArgumentException If the specified bundle has been uninstalled.
	 */
	public boolean isBundlePersistentlyStarted(Bundle bundle) {
		return manager.isBundlePersistentlyStarted(bundle);
	}

	/**
	 * Return the assigned start level value for the specified Bundle.
	 *
	 * @param bundle The target bundle.
	 * @return The start level value of the specified Bundle.
	 * @exception java.lang.IllegalArgumentException If the specified bundle has been uninstalled.
	 */
	public int getBundleStartLevel(Bundle bundle) {

		return manager.getBundleStartLevel(bundle);
	}

	/**
	 * Assign a start level value to the specified Bundle.
	 *
	 * <p>The specified bundle will be assigned the specified start level. The
	 * start level value assigned to the bundle will be persistently recorded
	 * by the Framework.
	 *
	 * If the new start level for the bundle is lower than or equal to the active start level of
	 * the Framework, the Framework will start the specified bundle as described
	 * in the <tt>Bundle.start</tt> method if the bundle is persistently marked
	 * to be started. The actual starting of this bundle must occur asynchronously.
	 *
	 * If the new start level for the bundle is higher than the active start level of
	 * the Framework, the Framework will stop the specified bundle as described
	 * in the <tt>Bundle.stop</tt> method except that the persistently recorded
	 * state for the bundle indicates that the bundle must be restarted in the
	 * future. The actual stopping of this bundle must occur asynchronously.
	 *
	 * @param bundle The target bundle.
	 * @param newSL The new start level for the specified Bundle.
	 * @throws IllegalArgumentException
	 * If the specified bundle has been uninstalled or
	 * if the specified start level is less than or equal to zero, or the  specified bundle is
	 * the system bundle.
	 * @throws SecurityException if the caller does not have the
	 * <tt>AdminPermission</tt> and the Java runtime environment supports
	 * permissions.
	 */
	public void setBundleStartLevel(Bundle bundle, int newSL) {
		manager.setBundleStartLevel(bundle, newSL);

	}

}
