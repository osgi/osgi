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
package org.eclipse.osgi.internal.resolver;

import java.io.File;
import java.io.IOException;
import org.eclipse.osgi.service.resolver.*;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;

public class StateManager implements PlatformAdmin, Runnable {
	public static boolean DEBUG = false;
	public static boolean DEBUG_READER = false;
	public static boolean DEBUG_PLATFORM_ADMIN = false;
	public static boolean DEBUG_PLATFORM_ADMIN_RESOLVER = false;
	public static boolean MONITOR_PLATFORM_ADMIN = false;
	public static String PROP_NO_LAZY_LOADING = "osgi.noLazyStateLoading"; //$NON-NLS-1$
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

	public StateManager(File stateFile, File lazyFile, BundleContext context) {
		// a negative timestamp means no timestamp checking
		this(stateFile, lazyFile, context, -1);
	}

	public StateManager(File stateFile, File lazyFile, BundleContext context, long expectedTimeStamp) {
		this.stateFile = stateFile;
		this.lazyFile = lazyFile;
		this.context = context;
		this.expectedTimeStamp = expectedTimeStamp;
		factory = new StateObjectFactoryImpl();
	}

	public void shutdown(File stateFile, File lazyFile) throws IOException {
		BundleDescription[] removalPendings = systemState.getRemovalPendings();
		if (removalPendings.length > 0)
			systemState.resolve(removalPendings);
		writeState(stateFile, lazyFile);
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
			if (systemState == null)
				return;
			initializeSystemState();
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

	private void writeState(File stateFile, File lazyFile) throws IOException {
		if (systemState == null)
			return;
		if (cachedState && lastTimeStamp == systemState.getTimeStamp())
			return;
		systemState.fullyLoad(); // make sure we are fully loaded before saving
		factory.writeState(systemState, stateFile, lazyFile);
	}

	private void initializeSystemState() {
		systemState.setResolver(getResolver(System.getSecurityManager() != null));
		if (systemState.setPlatformProperties(System.getProperties()))
			systemState.resolve(false); // cause a full resolve; some platform properties have changed
		lastTimeStamp = systemState.getTimeStamp();
	}

	public synchronized StateImpl createSystemState() {
		if (systemState == null) {
			systemState = factory.createSystemState();
			initializeSystemState();
		}
		return systemState;
	}

	public synchronized StateImpl readSystemState() {
		if (systemState == null)
			readSystemState(stateFile, lazyFile, expectedTimeStamp);
		return systemState;
	}

	public StateImpl getSystemState() {
		return systemState;
	}

	public long getCachedTimeStamp() {
		return lastTimeStamp;
	}

	public State getState(boolean mutable) {
		return mutable ? factory.createState(systemState) : new ReadOnlyState(systemState);
	}

	public State getState() {
		return getState(true);
	}

	public StateObjectFactory getFactory() {
		return factory;
	}

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

	public Resolver getResolver() {
		return getResolver(false);
	}

	private Resolver getResolver(boolean checkPermissions) {
		return new org.eclipse.osgi.internal.module.ResolverImpl(context, checkPermissions);
	}

	public StateHelper getStateHelper() {
		return StateHelperImpl.getInstance();
	}

	public BundleInstaller getInstaller() {
		return installer;
	}

	public void setInstaller(BundleInstaller installer) {
		this.installer = installer;
	}

	public void run() {
		while (true) {
			try {
				Thread.sleep(expireTime);
			} catch (InterruptedException e) {
				return;
			}
			if (systemState != null && lastTimeStamp == systemState.getTimeStamp())
				systemState.unloadLazyData(expireTime);
		}
	}
}
