/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.osgi.framework.adaptor.core;

import java.io.File;
import java.io.IOException;
import org.eclipse.osgi.internal.resolver.*;
import org.eclipse.osgi.service.resolver.*;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;

/**
 * The StateManager manages the system state for the framework.  It also provides the implementation
 * to the PlatformAdmin service.
 * <p>
 * Clients may extend this class.
 * </p>
 * @since 3.1
 */
public class StateManager implements PlatformAdmin, Runnable {
	/**
	 * General debug flag
	 */
	public static boolean DEBUG = false;
	/**
	 * Reader debug flag
	 */
	public static boolean DEBUG_READER = false;
	/**
	 * PlatformAdmin debug flag
	 */
	public static boolean DEBUG_PLATFORM_ADMIN = false;
	/**
	 * PlatformAdmin resolver debug flag
	 */
	public static boolean DEBUG_PLATFORM_ADMIN_RESOLVER = false;
	/**
	 * Monitor PlatformAdmin debug flag
	 */
	public static boolean MONITOR_PLATFORM_ADMIN = false;
	/**
	 * System property used to disable lazy state loading
	 */
	public static String PROP_NO_LAZY_LOADING = "osgi.noLazyStateLoading"; //$NON-NLS-1$
	/**
	 * System property used to specify to amount time before lazy data can be flushed from memory
	 */
	public static String PROP_LAZY_UNLOADING_TIME = "osgi.lazyStateUnloadingTime"; //$NON-NLS-1$
	private long expireTime = 300000; // default to five minutes
	private long readStartupTime;
	private StateImpl systemState;
	private StateObjectFactoryImpl factory;
	private long lastTimeStamp;
	private BundleInstaller installer;
	private boolean cachedState = false;
	private File stateFile;
	private File lazyFile;
	private long expectedTimeStamp;
	private BundleContext context;

	/**
	 * Constructs a StateManager using the specified files and context
	 * @param stateFile a file with the data required to persist in memory
	 * @param lazyFile a file with the data that may be lazy loaded and can be flushed from memory
	 * @param context the bundle context of the system bundle
	 */
	public StateManager(File stateFile, File lazyFile, BundleContext context) {
		// a negative timestamp means no timestamp checking
		this(stateFile, lazyFile, context, -1);
	}

	/**
	 * Constructs a StateManager using the specified files and context
	 * @param stateFile a file with the data required to persist in memory
	 * @param lazyFile a file with the data that may be lazy loaded and can be flushed from memory
	 * @param context the bundle context of the system bundle
	 * @param expectedTimeStamp the expected timestamp of the persisted system state.  A negative
	 * value indicates that no timestamp checking is done
	 */
	public StateManager(File stateFile, File lazyFile, BundleContext context, long expectedTimeStamp) {
		this.stateFile = stateFile;
		this.lazyFile = lazyFile;
		this.context = context;
		this.expectedTimeStamp = expectedTimeStamp;
		factory = new StateObjectFactoryImpl();
	}

	/**
	 * Shutsdown the state manager.  If the timestamp of the system state has changed
	 * @param stateFile
	 * @param lazyFile
	 * @throws IOException
	 */
	public void shutdown(File stateFile, File lazyFile) throws IOException {
		BundleDescription[] removalPendings = systemState.getRemovalPendings();
		if (removalPendings.length > 0)
			systemState.resolve(removalPendings);
		writeState(systemState, stateFile, lazyFile);
	}

	/**
	 * Update the given target files with the state data in memory.
	 * @param stateFile
	 * @param lazyFile
	 * @throws IOException
	 */
	public void update(File stateFile, File lazyFile) throws IOException {
		BundleDescription[] removalPendings = systemState.getRemovalPendings();
		StateImpl state = systemState;
		if (removalPendings.length > 0) {
			state = (StateImpl) state.getFactory().createState(systemState);
			state.setResolver(getResolver());
			state.setPlatformProperties(System.getProperties());
			state.resolve(false);
		}
		writeState(state, stateFile, lazyFile);
		lastTimeStamp = state.getTimeStamp();
		// TODO consider updating the state files for lazy loading
	}

	private void readSystemState(File stateFile, File lazyFile, long expectedTimeStamp) {
		if (stateFile == null || !stateFile.isFile())
			return;
		if (DEBUG_READER)
			readStartupTime = System.currentTimeMillis();
		try {
			boolean lazyLoad = !Boolean.valueOf(System.getProperty(PROP_NO_LAZY_LOADING)).booleanValue();
			systemState = factory.readSystemState(stateFile, lazyFile, lazyLoad, expectedTimeStamp);
			// problems in the cache (corrupted/stale), don't create a state object
			if (systemState == null || !initializeSystemState()) {
				systemState = null;
				return;
			}
			cachedState = true;
			try {
				expireTime = Long.parseLong(System.getProperty(PROP_LAZY_UNLOADING_TIME, Long.toString(expireTime)));
			} catch (NumberFormatException nfe) {
				// default to not expire
				expireTime = 0;
			}
			if (lazyLoad && expireTime > 0) {
				Thread t = new Thread(this, "State Data Manager"); //$NON-NLS-1$
				t.setDaemon(true);
				t.start();
			}
		} catch (IOException ioe) {
			// TODO: how do we log this?
			ioe.printStackTrace();
		} finally {
			if (DEBUG_READER)
				System.out.println("Time to read state: " + (System.currentTimeMillis() - readStartupTime)); //$NON-NLS-1$
		}
	}

	private void writeState(StateImpl state, File stateFile, File lazyFile) throws IOException {
		if (state == null)
			return;
		if (cachedState && lastTimeStamp == state.getTimeStamp())
			return;
		state.fullyLoad(); // make sure we are fully loaded before saving
		factory.writeState(state, stateFile, lazyFile);
	}

	private boolean initializeSystemState() {
		systemState.setResolver(getResolver(System.getSecurityManager() != null));
		lastTimeStamp = systemState.getTimeStamp();
		return !systemState.setPlatformProperties(System.getProperties());
	}

	/**
	 * Creates a new State used by the system.  If the system State already 
	 * exists then a new system State is not created.
	 * @return the State used by the system.
	 */
	public synchronized State createSystemState() {
		if (systemState == null) {
			systemState = factory.createSystemState();
			initializeSystemState();
		}
		return systemState;
	}

	/**
	 * Reads the State used by the system.  If the system State already
	 * exists then the system State is not read from a cache.  If the State could 
	 * not be read from a cache then <code>null</code> is returned.
	 * @return the State used by the system or <code>null</code> if the State
	 * could not be read from a cache.
	 */
	public synchronized State readSystemState() {
		if (systemState == null)
			readSystemState(stateFile, lazyFile, expectedTimeStamp);
		return systemState;
	}

	/**
	 * Returns the State used by the system.  If the system State does 
	 * not exist then <code>null</code> is returned.
	 * @return the State used by the system or <code>null</code> if one
	 * does not exist.
	 */
	public State getSystemState() {
		return systemState;
	}

	/**
	 * Returns the cached time stamp of the system State.  This value is the 
	 * original time stamp of the system state when it was created or read from
	 * a cache.
	 * @see State#getTimeStamp()
	 * @return the cached time stamp of the system State
	 */
	public long getCachedTimeStamp() {
		return lastTimeStamp;
	}

	/**
	 * @see PlatformAdmin#getState(boolean)
	 */
	public State getState(boolean mutable) {
		return mutable ? factory.createState(systemState) : new ReadOnlyState(systemState);
	}

	/**
	 * @see PlatformAdmin#getState()
	 */
	public State getState() {
		return getState(true);
	}

	/**
	 * @see PlatformAdmin#getFactory()
	 */
	public StateObjectFactory getFactory() {
		return factory;
	}

	/**
	 * @see PlatformAdmin#commit(State)
	 */
	public synchronized void commit(State state) throws BundleException {
		// no installer have been provided - commit not supported
		if (installer == null)
			throw new IllegalArgumentException("PlatformAdmin.commit() not supported"); //$NON-NLS-1$
		if (!(state instanceof UserState))
			throw new IllegalArgumentException("Wrong state implementation"); //$NON-NLS-1$		
		if (state.getTimeStamp() != systemState.getTimeStamp())
			throw new BundleException(StateMsg.COMMIT_INVALID_TIMESTAMP); 		
		StateDelta delta = state.compare(systemState);
		BundleDelta[] changes = delta.getChanges();
		for (int i = 0; i < changes.length; i++)
			if ((changes[i].getType() & BundleDelta.ADDED) > 0)
				installer.installBundle(changes[i].getBundle());
			else if ((changes[i].getType() & BundleDelta.REMOVED) > 0)
				installer.uninstallBundle(changes[i].getBundle());
			else if ((changes[i].getType() & BundleDelta.UPDATED) > 0)
				installer.updateBundle(changes[i].getBundle());
			else {
				// bug in StateDelta#getChanges
			}
	}

	/**
	 * @see PlatformAdmin#getResolver()
	 */
	public Resolver getResolver() {
		return getResolver(false);
	}

	private Resolver getResolver(boolean checkPermissions) {
		return new org.eclipse.osgi.internal.module.ResolverImpl(context, checkPermissions);
	}

	/**
	 * @see PlatformAdmin#getStateHelper()
	 */
	public StateHelper getStateHelper() {
		return StateHelperImpl.getInstance();
	}

	/**
	 * Returns the bundle installer.
	 * @return the bundle installer
	 */
	public BundleInstaller getInstaller() {
		return installer;
	}

	/**
	 * Sets the bundle installer.  The bundle installer will be used when a state is commited
	 * using the commit(State) method.
	 * @param installer the bundle installer
	 */
	public void setInstaller(BundleInstaller installer) {
		this.installer = installer;
	}

	public void run() {
		long timeStamp = lastTimeStamp; // cache the original timestamp incase of updates
		while (true) {
			try {
				Thread.sleep(expireTime);
			} catch (InterruptedException e) {
				return;
			}
			if (systemState != null && timeStamp == systemState.getTimeStamp())
				systemState.unloadLazyData(expireTime);
		}
	}
}
