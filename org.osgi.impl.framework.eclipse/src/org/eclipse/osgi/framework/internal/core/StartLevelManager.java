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

package org.eclipse.osgi.framework.internal.core;

import java.io.IOException;
import java.security.*;
import java.util.EventListener;
import java.util.List;
import org.eclipse.osgi.framework.debug.Debug;
import org.eclipse.osgi.framework.eventmgr.*;
import org.eclipse.osgi.util.NLS;
import org.osgi.framework.*;

/**
 * StartLevel service implementation for the OSGi specification.
 *
 * Framework service which allows management of framework and bundle startlevels.
 *
 * This class also acts as the StartLevel service factory class, providing StartLevel objects
 * to those requesting org.osgi.service.startlevel.StartLevel service. 
 * 
 * If present, there will only be a single instance of this service
 * registered in the framework.
 */
public class StartLevelManager implements EventDispatcher, EventListener, ServiceFactory {

	protected static Framework framework;
	protected static EventManager eventManager;
	protected static EventListeners startLevelListeners;

	/** The framework beginning startlevel.  Default is 1 */
	protected int frameworkBeginningStartLevel = 1;

	/** The initial bundle start level for newly installed bundles */
	protected int initialBundleStartLevel = 1;
	// default value is 1 for compatibility mode

	/** The currently active framework start level */
	private static int activeSL = 0;

	/** An object used to lock the active startlevel while it is being referenced */
	private static final Object lock = new Object();

	/** This constructor is called by the Framework */
	protected StartLevelManager(Framework framework) {
		StartLevelManager.framework = framework;
	}

	protected void initialize() {
		initialBundleStartLevel = framework.adaptor.getInitialBundleStartLevel();

		// Set Framework Beginning Start Level Property
		String value = framework.getProperty(Constants.OSGI_FRAMEWORKBEGINNINGSTARTLEVEL);
		if (value == null) {
			value = Constants.DEFAULT_STARTLEVEL;
		} else {
			try {
				if (Integer.parseInt(value) <= 0) {
					System.err.println(NLS.bind(Msg.PROPERTIES_INVALID_FW_STARTLEVEL, Constants.DEFAULT_STARTLEVEL)); //$NON-NLS-1$
					value = Constants.DEFAULT_STARTLEVEL;
				}
			} catch (NumberFormatException nfe) {
				System.err.println(NLS.bind(Msg.PROPERTIES_INVALID_FW_STARTLEVEL, Constants.DEFAULT_STARTLEVEL)); //$NON-NLS-1$
				value = Constants.DEFAULT_STARTLEVEL;
			}
		}
		framework.setProperty(Constants.OSGI_FRAMEWORKBEGINNINGSTARTLEVEL, value);
		frameworkBeginningStartLevel = Integer.parseInt(value);

		// create an event manager and a start level listener
		eventManager = new EventManager("Start Level Event Dispatcher"); //$NON-NLS-1$
		startLevelListeners = new EventListeners();
		startLevelListeners.addListener(this, this);
	}

	protected void cleanup() {
		eventManager.close();
		eventManager = null;
		startLevelListeners.removeAllListeners();
		startLevelListeners = null;
	}

	/**
	 * Return the initial start level value that is assigned
	 * to a Bundle when it is first installed.
	 *
	 * @return The initial start level value for Bundles.
	 * @see #setInitialBundleStartLevel
	 */
	public int getInitialBundleStartLevel() {
		return initialBundleStartLevel;
	}

	/**
	 * Return the initial start level used when the framework is started.
	 *
	 * @return The framework start level.
	 */
	public int getFrameworkStartLevel() {
		return frameworkBeginningStartLevel;
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
		framework.checkAdminPermission(framework.systemBundle, AdminPermission.STARTLEVEL);
		if (startlevel <= 0) {
			throw new IllegalArgumentException();
		}
		initialBundleStartLevel = startlevel;
		framework.adaptor.setInitialBundleStartLevel(startlevel);
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
		return activeSL;
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
	public void setStartLevel(int newSL, org.osgi.framework.Bundle callerBundle) {
		if (newSL <= 0) {
			throw new IllegalArgumentException(NLS.bind(Msg.STARTLEVEL_EXCEPTION_INVALID_REQUESTED_STARTLEVEL, "" + newSL)); //$NON-NLS-1$ //$NON-NLS-2$
		}
		framework.checkAdminPermission(framework.systemBundle, AdminPermission.STARTLEVEL);

		if (Debug.DEBUG && Debug.DEBUG_STARTLEVEL) {
			Debug.println("StartLevelImpl: setStartLevel: " + newSL + "; callerBundle = " + callerBundle.getBundleId()); //$NON-NLS-1$ //$NON-NLS-2$
		}
		issueEvent(new StartLevelEvent(StartLevelEvent.CHANGE_FW_SL, newSL, (AbstractBundle) callerBundle));

	}

	protected void setStartLevel(int newSL) {
		setStartLevel(newSL, framework.systemBundle);
	}

	/**
	 *  Internal method to allow the framework to be launched synchronously by calling the
	 *  StartLevelListener worker calls directly
	 *
	 *  This method does not return until all bundles that should be started are started
	 */
	protected void launch(int startlevel) {

		doSetStartLevel(startlevel, framework.systemBundle);
	}

	/**
	 *  Internal method to shut down the framework synchronously by setting the startlevel to zero
	 *  and calling the StartLevelListener worker calls directly
	 *
	 *  This method does not return until all bundles are stopped and the framework is shut down.
	 */
	protected void shutdown() {

		doSetStartLevel(0, framework.systemBundle);
	}

	/**
	 *  Internal worker method to set the startlevel
	 *
	 * @param newSL start level value                  
	 * @param callerBundle - the bundle initiating the change in start level
	 */
	private void doSetStartLevel(int newSL, AbstractBundle callerBundle) {
		synchronized (lock) {
			int tempSL = activeSL;

			if (newSL > tempSL) {
				for (int i = tempSL; i < newSL; i++) {
					if (Debug.DEBUG && Debug.DEBUG_STARTLEVEL) {
						Debug.println("sync - incrementing Startlevel from " + tempSL); //$NON-NLS-1$
					}
					tempSL++;
					incFWSL(i + 1, callerBundle);
				}
			} else {
				for (int i = tempSL; i > newSL; i--) {
					if (Debug.DEBUG && Debug.DEBUG_STARTLEVEL) {
						Debug.println("sync - decrementing Startlevel from " + tempSL); //$NON-NLS-1$
					}
					tempSL--;
					decFWSL(i - 1);
				}
			}
			framework.publishFrameworkEvent(FrameworkEvent.STARTLEVEL_CHANGED, callerBundle, null);
			if (Debug.DEBUG && Debug.DEBUG_STARTLEVEL) {
				Debug.println("StartLevelImpl: doSetStartLevel: STARTLEVEL_CHANGED event published"); //$NON-NLS-1$
			}
		}
	}

	/** 
	 * This method is used within the package to save the actual active startlevel value for the framework.
	 * Externally the setStartLevel method must be used.
	 * 
	 * @param newSL - the new startlevel to save
	 */
	protected void saveActiveStartLevel(int newSL) {
		synchronized (lock) {
			activeSL = newSL;
		}
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
	public boolean isBundlePersistentlyStarted(org.osgi.framework.Bundle bundle) {

		if (bundle.getState() == AbstractBundle.UNINSTALLED) {
			throw new IllegalArgumentException(Msg.BUNDLE_UNINSTALLED_EXCEPTION); //$NON-NLS-1$
		}
		AbstractBundle b = (AbstractBundle) bundle;
		int status = b.getBundleData().getStatus();
		return ((status & org.eclipse.osgi.framework.internal.core.Constants.BUNDLE_STARTED) == Constants.BUNDLE_STARTED);
	}

	/**
	 * Return the assigned start level value for the specified Bundle.
	 *
	 * @param bundle The target bundle.
	 * @return The start level value of the specified Bundle.
	 * @exception java.lang.IllegalArgumentException If the specified bundle has been uninstalled.
	 */
	public int getBundleStartLevel(org.osgi.framework.Bundle bundle) {

		if (bundle.getState() == AbstractBundle.UNINSTALLED) {
			throw new IllegalArgumentException(Msg.BUNDLE_UNINSTALLED_EXCEPTION); //$NON-NLS-1$
		}
		return ((AbstractBundle) bundle).getStartLevel();
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
	public void setBundleStartLevel(org.osgi.framework.Bundle bundle, int newSL) {

		String exceptionText = ""; //$NON-NLS-1$
		if (bundle.getBundleId() == 0) { // system bundle has id=0
			exceptionText = Msg.STARTLEVEL_CANT_CHANGE_SYSTEMBUNDLE_STARTLEVEL; //$NON-NLS-1$
		} else if (bundle.getState() == AbstractBundle.UNINSTALLED) {
			exceptionText = Msg.BUNDLE_UNINSTALLED_EXCEPTION; //$NON-NLS-1$
		} else if (newSL <= 0) {
			exceptionText = NLS.bind(Msg.STARTLEVEL_EXCEPTION_INVALID_REQUESTED_STARTLEVEL, "" + newSL); //$NON-NLS-1$ //$NON-NLS-2$
		}
		if (exceptionText.length() > 0) {
			throw new IllegalArgumentException(exceptionText);
		}

		try {
			// if the bundle's startlevel is not already at the requested startlevel
			if (newSL != ((org.eclipse.osgi.framework.internal.core.AbstractBundle) bundle).getStartLevel()) {
				final AbstractBundle b = (AbstractBundle) bundle;
				b.getBundleData().setStartLevel(newSL);
				try {
					AccessController.doPrivileged(new PrivilegedExceptionAction() {
						public Object run() throws Exception {
							b.getBundleData().save();
							return null;
						}
					});
				} catch (PrivilegedActionException e) {
					if (e.getException() instanceof IOException) {
						throw (IOException) e.getException();
					}
					throw (RuntimeException) e.getException();
				}

				framework.checkAdminPermission(bundle, AdminPermission.EXECUTE);

				// handle starting or stopping the bundle asynchronously
				issueEvent(new StartLevelEvent(StartLevelEvent.CHANGE_BUNDLE_SL, newSL, (AbstractBundle) bundle));
			}
		} catch (IOException e) {
			framework.publishFrameworkEvent(FrameworkEvent.ERROR, bundle, e);
		}

	}

	/**
	 *  This method sends the StartLevelEvent to the EventManager for dispatching
	 * 
	 * @param sle The event to be queued to the Event Manager
	 */
	private void issueEvent(StartLevelEvent sle) {

		/* queue to hold set of listeners */
		ListenerQueue queue = new ListenerQueue(eventManager);

		/* add set of StartLevelListeners to queue */
		queue.queueListeners(startLevelListeners, this);

		/* dispatch event to set of listeners */
		queue.dispatchEventAsynchronous(sle.getType(), sle);
	}

	/**
	 * This method is the call back that is called once for each listener.
	 * This method must cast the EventListener object to the appropriate listener
	 * class for the event type and call the appropriate listener method.
	 *
	 * @param listener This listener must be cast to the appropriate listener
	 * class for the events created by this source and the appropriate listener method
	 * must then be called.
	 * @param listenerObject This is the optional object that was passed to
	 * EventListeners.addListener when the listener was added to the EventListeners.
	 * @param eventAction This value was passed to the ListenerQueue object via one of its
	 * dispatchEvent* method calls. It can provide information (such
	 * as which listener method to call) so that this method
	 * can complete the delivery of the event to the listener.
	 * @param eventObject This object was passed to the ListenerQueue object via one of its
	 * dispatchEvent* method calls. This object was created by the event source and
	 * is passed to this method. It should contain all the necessary information (such
	 * as what event object to pass) so that this method
	 * can complete the delivery of the event to the listener.
	 */
	public void dispatchEvent(Object listener, Object listenerObject, int eventAction, Object eventObject) {
		try {
			switch (eventAction) {
				case StartLevelEvent.CHANGE_BUNDLE_SL :
					setBundleSL((StartLevelEvent) eventObject);
					break;
				case StartLevelEvent.CHANGE_FW_SL :
					doSetStartLevel(((StartLevelEvent) eventObject).getNewSL(), ((StartLevelEvent) eventObject).getBundle());
					break;
			}
		} catch (Throwable t) {
			// allow the adaptor to handle this unexpected error
			framework.adaptor.handleRuntimeError(t);
		}
	}

	/** 
	 *  Increment the active startlevel by one
	 */
	protected void incFWSL(int activeSL, AbstractBundle callerBundle) {
		if (Debug.DEBUG && Debug.DEBUG_STARTLEVEL) {
			Debug.println("SLL: incFWSL: saving activeSL of " + activeSL); //$NON-NLS-1$
		}

		framework.startLevelManager.saveActiveStartLevel(activeSL);

		AbstractBundle[] launch;
		BundleRepository bundles = framework.bundles;

		launch = getInstalledBundles(bundles);

		if (activeSL == 1) { // framework was not active

			/* Load all installed bundles */
			loadInstalledBundles(launch);
			/* Resume all bundles */
			resumeBundles(launch, true);

			/* publish the framework started event */
			if (Debug.DEBUG && Debug.DEBUG_STARTLEVEL) {
				Debug.println("SLL: Framework started"); //$NON-NLS-1$
			}

			framework.publishFrameworkEvent(FrameworkEvent.STARTED, callerBundle, null);

		} else {
			// incrementing an already active framework
			resumeBundles(launch, false);
		}
	}

	/**
	 * Build an array of all installed bundles to be launch.
	 * The returned array is sorted by increasing startlevel/id order.
	 * @param bundles - the bundles installed in the framework
	 * @return A sorted array of bundles 
	 */
	private AbstractBundle[] getInstalledBundles(BundleRepository bundles) {

		/* make copy of bundles vector in case it is modified during launch */
		AbstractBundle[] installedBundles;

		synchronized (bundles) {
			List allBundles = bundles.getBundles();
			installedBundles = new AbstractBundle[allBundles.size()];
			allBundles.toArray(installedBundles);

			/* sort bundle array in ascending startlevel / bundle id order
			 * so that bundles are started in ascending order.
			 */
			Util.sort(installedBundles, 0, installedBundles.length);
		}

		return installedBundles;
	}

	/**
	 * Load all bundles in the list
	 * @param installedBundles a list of bundles to load
	 */
	private void loadInstalledBundles(AbstractBundle[] installedBundles) {

		for (int i = 0; i < installedBundles.length; i++) {
			AbstractBundle bundle = installedBundles[i];
			if (Debug.DEBUG && Debug.DEBUG_STARTLEVEL) {
				Debug.println("SLL: Trying to load bundle " + bundle); //$NON-NLS-1$
			}
			bundle.load();
		}
	}

	/**
	 *  Resume all bundles in the launch list
	 * @param launch a list of Bundle Objects to launch
	 * @param launchingFW tells whether or not to launch the framework (system bundle)
	 */
	private void resumeBundles(AbstractBundle[] launch, boolean launchingFW) {
		if (launchingFW) {
			/* Start the system bundle */
			try {
				framework.systemBundle.context.start();
			} catch (BundleException be) {
				if (Debug.DEBUG && Debug.DEBUG_STARTLEVEL) {
					Debug.println("SLL: Bundle resume exception: " + be.getMessage()); //$NON-NLS-1$
					Debug.printStackTrace(be.getNestedException());
				}

				framework.publishFrameworkEvent(FrameworkEvent.ERROR, framework.systemBundle, be);
				throw new RuntimeException(be.getMessage());
			}

		}
		/* Resume all bundles that were previously started and whose startlevel is <= the active startlevel */

		int fwsl = framework.startLevelManager.getStartLevel();
		for (int i = 0; i < launch.length; i++) {
			int bsl = launch[i].getStartLevel();
			if (bsl < fwsl) {
				// skip bundles who should have already been started
				continue;
			} else if (bsl == fwsl) {
				if (Debug.DEBUG && Debug.DEBUG_STARTLEVEL) {
					Debug.println("SLL: Active sl = " + fwsl + "; Bundle " + launch[i].getBundleId() + " sl = " + bsl); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				}
				framework.resumeBundle(launch[i]);
			} else {
				// can stop resuming bundles since any remaining bundles have a greater startlevel than the framework active startlevel
				break;
			}
		}

		framework.systemBundle.state = AbstractBundle.ACTIVE;

	}

	/** 
	 *  Decrement the active startlevel by one
	 * @param activeSL -  the startlevel value to set the framework to
	 */
	protected void decFWSL(int activeSL) {
		if (Debug.DEBUG && Debug.DEBUG_STARTLEVEL) {
			Debug.println("SLL: decFWSL: saving activeSL of " + activeSL); //$NON-NLS-1$
		}

		framework.startLevelManager.saveActiveStartLevel(activeSL);

		BundleRepository bundles = framework.bundles;

		if (activeSL == 0) { // stopping the framework
			/* stop all running bundles */

			suspendAllBundles(bundles);

			unloadAllBundles(bundles);

		} else {
			// just decrementing the active startlevel - framework is not shutting down
			// get the list of installed bundles, sorted by startlevel
			AbstractBundle[] shutdown = getInstalledBundles(bundles);
			for (int i = shutdown.length - 1; i >= 0; i--) {
				int bsl = shutdown[i].getStartLevel();
				if (bsl > activeSL + 1) {
					// don't need to mess with bundles with startlevel > the previous active - they should
					// already have been stopped
					continue;
				} else if (bsl <= activeSL) {
					// don't need to keep going - we've stopped all we're going to stop
					break;
				} else if (shutdown[i].isActive()) {
					// if bundle is active or starting, then stop the bundle
					if (Debug.DEBUG && Debug.DEBUG_STARTLEVEL) {
						Debug.println("SLL: stopping bundle " + shutdown[i].getBundleId()); //$NON-NLS-1$
					}
					framework.suspendBundle(shutdown[i], false);
				}
			}
		}
	}

	/**
	 *  Suspends all bundles in the vector passed in.
	 * @param bundles list of Bundle objects to be suspended
	 */
	private void suspendAllBundles(BundleRepository bundles) {
		boolean changed;
		do {
			changed = false;

			AbstractBundle[] shutdown = this.getInstalledBundles(bundles);

			// shutdown all running bundles
			for (int i = shutdown.length - 1; i >= 0; i--) {
				AbstractBundle bundle = shutdown[i];

				if (framework.suspendBundle(bundle, false)) {
					if (Debug.DEBUG && Debug.DEBUG_STARTLEVEL) {
						Debug.println("SLL: stopped bundle " + bundle.getBundleId()); //$NON-NLS-1$
					}
					changed = true;
				}
			}
		} while (changed);

		try {
			framework.systemBundle.context.stop();
		} catch (BundleException sbe) {
			if (Debug.DEBUG && Debug.DEBUG_STARTLEVEL) {
				Debug.println("SLL: Bundle suspend exception: " + sbe.getMessage()); //$NON-NLS-1$
				Debug.printStackTrace(sbe.getNestedException());
			}

			framework.publishFrameworkEvent(FrameworkEvent.ERROR, framework.systemBundle, sbe);
		}

		framework.systemBundle.state = AbstractBundle.STARTING;
	}

	/**
	 *  Unloads all bundles in the vector passed in.
	 * @param bundles list of Bundle objects to be unloaded
	 */
	private void unloadAllBundles(BundleRepository bundles) {
		synchronized (bundles) {
			/* unload all installed bundles */
			List allBundles = bundles.getBundles();
			int size = allBundles.size();

			for (int i = 0; i < size; i++) {
				AbstractBundle bundle = (AbstractBundle) allBundles.get(i);

				if (Debug.DEBUG && Debug.DEBUG_STARTLEVEL) {
					Debug.println("SLL: Trying to unload bundle " + bundle); //$NON-NLS-1$
				}
				bundle.refresh();
			}
		}
	}

	/** 
	 *  Set the bundle's startlevel to the new value
	 *  This may cause the bundle to start or stop based on the active framework startlevel
	 * @param startLevelEvent - the event requesting change in bundle startlevel
	 */
	protected void setBundleSL(StartLevelEvent startLevelEvent) {
		synchronized (lock) {
			int activeSL = framework.startLevelManager.getStartLevel();
			int newSL = startLevelEvent.getNewSL();
			AbstractBundle bundle = startLevelEvent.getBundle();

			int bundlestate = bundle.getState();
			if (Debug.DEBUG && Debug.DEBUG_STARTLEVEL) {
				Debug.print("SLL: bundle active=" + bundle.isActive()); //$NON-NLS-1$
				Debug.print("; newSL = " + newSL); //$NON-NLS-1$
				Debug.println("; activeSL = " + activeSL); //$NON-NLS-1$
			}

			if (bundle.isActive() && (newSL > activeSL)) {
				if (Debug.DEBUG && Debug.DEBUG_STARTLEVEL) {
					Debug.println("SLL: stopping bundle " + bundle.getBundleId()); //$NON-NLS-1$
				}
				framework.suspendBundle(bundle, false);
			} else {
				if (!bundle.isActive() && (newSL <= activeSL)) {
					if (Debug.DEBUG && Debug.DEBUG_STARTLEVEL) {
						Debug.println("SLL: starting bundle " + bundle.getBundleId()); //$NON-NLS-1$
					}
					framework.resumeBundle(bundle);
				}
			}
			if (Debug.DEBUG && Debug.DEBUG_STARTLEVEL) {
				Debug.println("SLL: Bundle Startlevel set to " + newSL); //$NON-NLS-1$
			}
		}
	}

	/**
	 * Returns a StartLevel object, created for each requesting bundle.
	 * 
	 * @param owner bundle, requested to get StartLevel service.
	 * @pre callerBundle!=null
	 * @param registration ServiceRegistration of the StartLevel service
	 * @pre sReg!=null
	 * @return StartLevel object
	 */
	public Object getService(Bundle owner, ServiceRegistration registration) {
		return new StartLevelImpl(owner, framework);
	}

	/**
	 * Does nothing, as the StartLevel bundle does not keep references to StartLevel objects.
	 *
	 * @param owner bundle requesting to unget StartLevel service.
	 * @param registration ServiceRegistration of StartLevel
	 * @param service Service object, already been got by this bundle.
	 */
	public void ungetService(Bundle owner, ServiceRegistration registration, Object service) {
	}
}
