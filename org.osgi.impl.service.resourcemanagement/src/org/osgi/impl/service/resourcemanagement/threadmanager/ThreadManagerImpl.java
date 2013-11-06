/**
 * 
 */
package org.osgi.impl.service.resourcemanagement.threadmanager;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;
import org.osgi.framework.wiring.BundleWiring;
import org.osgi.impl.service.resourcemanagement.bundlemanagement.BundleManager;
import org.osgi.service.resourcemanagement.ResourceContext;

/**
 * This class maintains a map associating thread and resource context
 * 
 * @author mpcy8647
 * 
 */
public class ThreadManagerImpl implements ThreadManager, BundleListener {

	/**
	 * the CREATED_STATE indicates this ThreadManager has been created but needs
	 * to be started (i.e call {@link ThreadManager#start(BundleContext)})
	 */
	private static final int CREATED_STATE = 0;

	/**
	 * the STARTED_STATE indicates the ThreadManager is ready to be used.
	 */
	private static final int STARTED_STATE = 1;

	/**
	 * the STOPPED_STATE indicates the ThreadManager has been stopped (i.e the
	 * {@link ThreadManager#stop(BundleContext)} method has been called).
	 * Further public method calls will throw an {@link IllegalStateException}.
	 */
	private static final int STOPPED_STATE = 2;

	/**
	 * Current state of the ThreadManager;
	 * <p>
	 * Expected values are:
	 * <ul>
	 * <li>{@link #CREATED_STATE}</li>
	 * <li>{@link #STARTED_STATE}</li>
	 * <li>{@link #STOPPED_STATE}</li>
	 * </ul>
	 * </p>
	 */
	private int state;

	private final Map<ClassLoader, Long> classLoaderToBundle;
	private final Map<Long, ClassLoader> bundleToClassloader;

	private final BundleManager bundleManager;

	public ThreadManagerImpl(BundleManager pBundleManager) {
		classLoaderToBundle = new HashMap<ClassLoader, Long>();
		bundleToClassloader = new HashMap<Long, ClassLoader>();
		bundleManager = pBundleManager;

		state = CREATED_STATE;
	}

	/**
	 * <p>
	 * Start the thread manager and initializes it.
	 * </p>
	 * <p>
	 * This method registers this class as a Bundle listener and iterate over
	 * the existing bundles in order to get their classloaders.
	 * </p>
	 * 
	 * @param context
	 *            context of the bundle.
	 */
	public void start(BundleContext context) {
		checkState(CREATED_STATE, state);

		context.addBundleListener(this);

		Bundle[] bundles = context.getBundles();
		for (int i = 0; i < bundles.length; i++) {
			Bundle bundle = bundles[i];
			long bundleId = bundle.getBundleId();
			if (bundle.getState() != Bundle.UNINSTALLED) {
				ClassLoader bundleClassloader = getBundleClassloader(bundle);

				if (bundleClassloader != null) {
					addBundleClassloader(bundleId, bundleClassloader);
				} // TODO what to do if the classloader is null ??
			}
		}

		// set initial thread context
		setInitialThreadContextClassLoader();

		state = STARTED_STATE;

	}

	/**
	 * <p>
	 * Stop the thread manager.
	 * </p>
	 * <p>
	 * Unregister the thread manager as a bundle listener.
	 * </p>
	 * 
	 * @param context
	 */
	public void stop(BundleContext context) {
		state = STOPPED_STATE;

		context.removeBundleListener(this);

		bundleToClassloader.clear();
		classLoaderToBundle.clear();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleListener#bundleChanged(org.osgi.framework.
	 * BundleEvent)
	 */
	public void bundleChanged(BundleEvent event) {
		// check event type
		if ((event.getType() == BundleEvent.INSTALLED)
				|| (event.getType() == BundleEvent.RESOLVED)) {
			// a new bundle has been installed
			// add its classloader into the classloader map
			Bundle b = event.getBundle();
			long bundleId = b.getBundleId();
			ClassLoader bundleClassloader = getBundleClassloader(b);

			addBundleClassloader(bundleId, bundleClassloader);
		} else if (event.getType() == BundleEvent.UNINSTALLED) {
			// a bundle has been removed
			// update classloader map
			Bundle bundle = event.getBundle();
			removeBundleClassloader(bundle.getBundleId());
		}

		// TODO Do we have to deal other kind of event ?
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.impl.service.resourcemanagement.threadmanager.ThreadManager#
	 * getResourceContext(java.lang.Thread)
	 */
	public ResourceContext getResourceContext(Thread t) {

		checkState(STARTED_STATE, state);

		ResourceContext resourceContext = null;

		// get context class loader of the thread
		ClassLoader threadClassLoader = t.getContextClassLoader();

		// get the id of the bundle associated to the thread classloader
		long bundleId = getBundle(threadClassLoader);

		if (bundleId != -1) {
			// this thread is associated to the thread t.
			// get resource context from the bundle manager
			resourceContext = bundleManager.getResourceContext(bundleId);

		}

		return resourceContext;
	}

	public void switchContext(Thread t, ResourceContext rc) {
		long[] bundles = rc.getBundleIds();
		long refBundleId = -1;
		if (bundles.length > 0) {
			refBundleId = bundles[0];
		}

		if (refBundleId != -1) {
			ClassLoader bundleClassLoader = getBundleClassLoader(refBundleId);
			t.setContextClassLoader(bundleClassLoader);
		}
	}

	/**
	 * Get the bundle class loader from the map
	 * 
	 * @param bundleId
	 *            bundle identifier
	 * @return classloader
	 */
	private ClassLoader getBundleClassLoader(long bundleId) {
		ClassLoader classLoader = null;
		synchronized (bundleToClassloader) {
			classLoader = bundleToClassloader.get(bundleId);
		}

		return classLoader;
	}

	/**
	 * Add a new classloader into the classloader map. This classloader is
	 * associated with the bundleId bundle.
	 * 
	 * @param bundleId
	 *            bundle id
	 * @param bundleClassloader
	 *            bundle classloader
	 */
	private void addBundleClassloader(long bundleId,
			ClassLoader bundleClassloader) {
		System.out.println("bundle: " + bundleId + ", classloader:"
				+ bundleClassloader);
		synchronized (classLoaderToBundle) {
			classLoaderToBundle.put(bundleClassloader, bundleId);
		}
		synchronized (bundleToClassloader) {
			bundleToClassloader.put(bundleId, bundleClassloader);
		}
	}

	/**
	 * Remove the bundle and its classloader from the classloader map.
	 * 
	 * @param bundleId
	 *            bundle identifier.
	 */
	private void removeBundleClassloader(long bundleId) {
		synchronized (classLoaderToBundle) {
			classLoaderToBundle.values().remove(bundleId);
		}
		synchronized (bundleToClassloader) {
			bundleToClassloader.remove(bundleId);
		}
	}

	/**
	 * Get the bundle id associated to the provided classloader.
	 * 
	 * @param classloader
	 * @return bundleId
	 */
	private long getBundle(ClassLoader classloader) {
		long bundleId = -1;
		synchronized (classLoaderToBundle) {
			if (classLoaderToBundle.containsKey(classloader)) {
				bundleId = classLoaderToBundle.get(classloader);
			}
		}
		return bundleId;
	}

	/**
	 * Get the classloader of the bundle b.
	 * 
	 * @param b
	 *            bundle
	 * @return the classloader of b.
	 */
	private ClassLoader getBundleClassloader(Bundle b) {
		ClassLoader bundleClassloader = null;

		// adapt the current bundle to BundleWiring
		BundleWiring bw = b.adapt(BundleWiring.class);
		if (bw != null) {
			// System.out.println("classloader : " + bw.getClassLoader());
			try {
				bundleClassloader = bw.getClassLoader();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} // TODO is it possible that bundlewiring is null ?

		return bundleClassloader;
	}

	/**
	 * Check the expectedState is equal to the currentState.
	 * 
	 * @param expectedState
	 * @param currentState
	 * @throws IllegalStateException
	 *             if the currentState is not equal to the expected state.
	 */
	private static void checkState(int expectedState, int currentState)
			throws IllegalStateException {
		if (expectedState != currentState) {
			throw new IllegalStateException();
		}
	}

	/**
	 * Set the context class loader of all existing threads. A thread is
	 * associated to a bundle based on its Runnable implementation class.
	 */
	private void setInitialThreadContextClassLoader() {

		Thread[] activeThreads = getActiveThreads();

		// iterate over the active threads
		for (int i = 0; i < activeThreads.length; i++) {
			Thread t = activeThreads[i];

			// retrieve the classloader of the Runnable class of the thread
			ClassLoader classLoader = getBundleClassLoader(t);

			if (classLoader != null) {
				t.setContextClassLoader(classLoader);
			}

			System.out.println("thread :" + t + ", context : "
					+ t.getContextClassLoader());
		}

	}

	/**
	 * Get all active threads currently existing on the JVM.
	 * 
	 * @return array of Thread.
	 */
	private Thread[] getActiveThreads() {
		// retrieve the current thread
		Thread currentThread = Thread.currentThread();

		// retrieve the ThreadGroup of the current thread
		ThreadGroup currentThreadGroup = currentThread.getThreadGroup();
		while (currentThreadGroup.getParent() != null) {
			currentThreadGroup = currentThreadGroup.getParent();
		}

		// at this point, currentThreadGroup is the initial thread group of the
		// JVM

		// retrieve all the threads
		int nbOfActiveThread = currentThreadGroup.activeCount();
		Thread[] activeThreads = new Thread[nbOfActiveThread];
		nbOfActiveThread = currentThreadGroup.enumerate(activeThreads);

		return activeThreads;

	}

	/**
	 * Retrieves the bundle holding the Runnable class of the thread.
	 * 
	 * @param t
	 *            thread
	 * 
	 */
	private ClassLoader getBundleClassLoader(Thread t) {
		ClassLoader bundleClassloader = null;
		StackTraceElement[] elements = t.getStackTrace();

		// iterate over the stack trace of the thread
		// begins by the end
		for (int i = elements.length - 1; i >= 0; i--) {
			StackTraceElement currentElement = elements[i];

			String className = currentElement.getClassName();
			bundleClassloader = getClassloader(className);
			if (bundleClassloader != null) {
				// break the loop when a classloader has been found
				break;
			}
		}

		return bundleClassloader;
	}

	/**
	 * Get the classloader able to load the provided className.
	 * 
	 * @param className
	 * @return classloader or null
	 */
	private ClassLoader getClassloader(String className) {
		ClassLoader classloader = null;

		synchronized (classLoaderToBundle) {
			for (Iterator<ClassLoader> it = classLoaderToBundle.keySet()
					.iterator(); it.hasNext();) {
				ClassLoader currentClassloader = it.next();
				Class c = null;

				try {
					c = currentClassloader.loadClass(className);
					if (currentClassloader.equals(c.getClassLoader())) {
						classloader = currentClassloader;
						break;
					}
				} catch (ClassNotFoundException e) {
				}
			}
		}

		return classloader;
	}

}
