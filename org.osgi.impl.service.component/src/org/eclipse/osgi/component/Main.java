/*
 * Copyright (c) IBM Corporation (2005)
 *
 * These materials have been contributed  to the OSGi Alliance as 
 * "MEMBER LICENSED MATERIALS" as defined in, and subject to the terms of, 
 * the OSGi Member Agreement, specifically including but not limited to, 
 * the license rights and warranty disclaimers as set forth in Sections 3.2 
 * and 12.1 thereof, and the applicable Statement of Work. 
 *
 * All company, brand and product names contained within this document may be 
 * trademarks that are the sole property of the respective owners.
 */

package org.eclipse.osgi.component;

import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.eclipse.osgi.component.model.ComponentDescription;
import org.eclipse.osgi.component.model.ComponentDescriptionCache;
import org.eclipse.osgi.component.resolver.Resolver;
import org.eclipse.osgi.component.workqueue.WorkDispatcher;
import org.eclipse.osgi.component.workqueue.WorkQueue;
import org.eclipse.osgi.util.tracker.BundleTracker;
import org.eclipse.osgi.util.tracker.BundleTrackerCustomizer;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkEvent;
import org.osgi.service.component.ComponentConstants;
import org.osgi.service.packageadmin.PackageAdmin;
import org.osgi.util.tracker.ServiceTracker;

/**
 * 
 * Main class for the SCR. This class will start the SCR bundle and begin
 * processing other bundles.
 * 
 * @version $Revision$
 */
public class Main implements BundleActivator, BundleTrackerCustomizer, WorkDispatcher {

	public BundleContext context;
	public FrameworkHook framework;
	public ComponentDescriptionCache cache;
	public BundleTracker bundleTracker;
	public Resolver resolver;
	public ServiceTracker packageAdminTracker;
	public WorkQueue workQueue;

	protected Hashtable bundleToComponentDescriptions;
	protected Hashtable bundleToLastModified;
	protected List enableCDs;

	/**
	 * Bundle is being added to SCR tracked bundles.
	 */
	public static final int ADD = 1;
	/**
	 * Bundle is being removed from SCR tracked bundles.
	 */
	public static final int REMOVE = 2;
	
	/**
	 * Start the SCR bundle.
	 * 
	 * @param context BundleContext for SCR implementation.
	 */
	public void start(BundleContext context) {
		this.context = context;
		framework = new FrameworkHook();
		cache = new ComponentDescriptionCache(this);
		bundleToComponentDescriptions = new Hashtable();
		bundleToLastModified = new Hashtable();

		packageAdminTracker = new ServiceTracker(context, PackageAdmin.class.getName(), null);
		packageAdminTracker.open(true);
		bundleTracker = new BundleTracker(context, Bundle.ACTIVE, this);
		workQueue = new WorkQueue(this, "SCR Work Queue");
		resolver = new Resolver(this);
		workQueue.start();
		bundleTracker.open();
	}

	/**
	 * Stop the SCR bundle.
	 * 
	 * @param context BundleContext for SCR implementation.
	 */
	public void stop(BundleContext context) {
		
		bundleTracker.close();
		workQueue.closeAndJoin();
		resolver.dispose();
		packageAdminTracker.close();
		cache.dispose();
		cache = null;
		framework = null;
		resolver = null;
		bundleToComponentDescriptions = null;
		bundleToLastModified = null;
		this.context = null;

		//	TODO
		//when SCR is shutting down write/flush database to disk (cache)

	}

	/**
	 * Returns the value of the candidate bundle's Service-Component manifest
	 * header. If the bundle has the header, then the bundle will be tracked. If
	 * not, null is returned and the bundle will not be tracked.
	 * 
	 * If the bundle is to be tracked, then this method will enqueue that the
	 * bundle will be tracked.
	 * 
	 * @param bundle Candidate bundle to be tracked.
	 * @return Value of the candidate bundle's Service-Component manifest header
	 *         or null if the bundle does not specify the header.
	 */
	public Object addingBundle(Bundle bundle) {

		List enableComponentDescriptions = new ArrayList();

		PackageAdmin packageAdmin = (PackageAdmin) packageAdminTracker.getService();
		if (packageAdmin.getBundleType(bundle) != 0) {
			return null; // don't process fragments.
		}

		Dictionary headers = getHeaders(bundle);
		Object value = headers.get(ComponentConstants.SERVICE_COMPONENT);
		if (value == null) {
			return null;
		}

		Long bundleID = new Long(bundle.getBundleId());

		// get the bundle's last modified date
		Long bundleLastModified = new Long(bundle.getLastModified());

		// get the last saved value for the bundle's last modified date
		Long bundleOldLastModified = (Long) bundleToLastModified.get(bundleID);

		//compare the two and if changed ( or if first time ) update the maps
		if ((!bundleLastModified.equals(bundleOldLastModified)) || (bundleOldLastModified == null)) {

			// get all ComponentDescriptions for this bundle
			List componentDescriptions = cache.getComponentDescriptions(bundle);

			// update map of bundle to ComponentDescriptions
			bundleToComponentDescriptions.put(bundleID, componentDescriptions);

			// update bundle:lastModifiedDate map
			bundleToLastModified.put(bundleID, bundleLastModified);

			//for each CD in bundle set enable flag based on autoenable 
			if (componentDescriptions != null) {
				Iterator it = componentDescriptions.iterator();
				while (it.hasNext()) {
					ComponentDescription componentDescription = (ComponentDescription) it.next();
					validate(componentDescription);
					if (componentDescription.isAutoenable() && componentDescription.isValid()) {
						componentDescription.setEnabled(true);
						enableComponentDescriptions.add(componentDescription);
					}
				}
			}
		}

		//publish all CDs to be enabled, to resolver (add to the workqueue and publish event)
		workQueue.enqueueWork(this, ADD, enableComponentDescriptions);
		//workQueue.enqueueWork(this, ADD, bundle);
		return value;
	}

	/**
	 * Empty implementation. No work is needed for modifiedBundle.
	 * 
	 * @param bundle
	 * @param object
	 */
	public void modifiedBundle(Bundle bundle, Object object) {

		Long bundleID = new Long(bundle.getBundleId());

		// flush map
		bundleToComponentDescriptions.remove(bundleID);

		// flush map
		bundleToLastModified.remove(bundleID);

	}

	/**
	 * Enqueue that the bundle is untracking.
	 * 
	 * @param bundle Bundle becoming untracked.
	 * @param object Value returned by addingBundle.
	 */

	public void removedBundle(Bundle bundle, Object object) {
		
		List disableComponentDescriptions = new ArrayList();
		Long bundleID = new Long(bundle.getBundleId());

		//get CD's for this bundle
		List ComponentDescriptions = (List) bundleToComponentDescriptions.get(new Long(bundle.getBundleId()));
		if (ComponentDescriptions != null) {
			Iterator it = ComponentDescriptions.iterator();

			//for each CD in bundle
			while (it.hasNext()) {
				ComponentDescription ComponentDescription = (ComponentDescription) it.next();

				//check if enabled && eligible
				if ((ComponentDescription.isEnabled())) {

					//add to disabled list
					disableComponentDescriptions.add(ComponentDescription);

					//mark disabled
					ComponentDescription.setEnabled(false);
				}
			}
		}

		//remove the bundle from the lists/maps
		bundleToComponentDescriptions.remove(bundleID);
		bundleToLastModified.remove(bundleID);

		// publish event to resolver ( or dissolver if there is one) with list of CD's to disable 
		//workQueue.enqueueWork(this, REMOVE, bundle);
		workQueue.enqueueWork(this, REMOVE, disableComponentDescriptions);
		return;
	}

	/**
	 * enableComponents - called by resolver
	 * 
	 * @param CDs - List of ComponentDescriptions to mark successfully enabled
	 */

	public void enableComponents(List CDs) {

		if (CDs != null) {
			Iterator it = CDs.iterator();

			//for each CD in list
			while (it.hasNext()) {
				ComponentDescription CD = (ComponentDescription) it.next();

				//if CD is not valid and is disabled then enable it
				if (CD.isValid() && !CD.isEnabled()) {

					// set CD enabled
					CD.setEnabled(true);
				}
				//else it is either not valid or it is already enabled - do nothing 
			}
		}
	}

	/**
	 * enableComponent - called by SC via ComponentContext
	 * 
	 * @param name The name of a component or <code>null</code> to indicate all
	 *        components in the bundle.
	 *        
	 * @param bundle The bundle which contains the Service Component to be enabled
	 */

	public void enableComponent(String name, Bundle bundle) {

		// get all ComponentDescriptions for this bundle
      List componentDescriptions = (List) bundleToComponentDescriptions.get(new Long(bundle.getBundleId()));
      
		//Create the list of CD's to be enabled
		List enableCDs = new ArrayList();

		if (componentDescriptions != null) {
			Iterator it = componentDescriptions.iterator();

			//for each CD in list
			while (it.hasNext()) {
				ComponentDescription componentDescription = (ComponentDescription) it.next();
				validate(componentDescription);

				//if name is null then enable ALL Component Descriptions in this bundle
				if (name == null) {

					// if CD is valid and is disabled then enable it
					if (componentDescription.isValid() && !componentDescription.isEnabled()) {

						// add to list of CDs to enable
						enableCDs.add(componentDescription);

						// set CD enabled
						componentDescription.setEnabled(true);
					}
				} else {
					if (componentDescription.getName().equals(name)) {

						//if CD is valid and is disabled then enable it
						if (componentDescription.isValid() && !componentDescription.isEnabled()) {

							// add to list of CDs to enable
							enableCDs.add(componentDescription);

							// set CD enabled
							componentDescription.setEnabled(true);
						}
					}
				}
				//else it is either not valid or it is already enabled - do nothing 
			}
		}

		// publish to resolver the list of CDs to enable
		if (!enableCDs.isEmpty())
			workQueue.enqueueWork(this, ADD, enableCDs);
	}

	/**
	 * disableComponents - called by resolver
	 * 
	 * @param componentDescriptions - List of ComponentDescriptions to mark disabled
	 */

	public void disableComponents(List componentDescriptions) {

		ComponentDescription componentDescription = null;
		//Long bundleID;

		if (componentDescriptions != null) {
			Iterator it = componentDescriptions.iterator();

			//for each ComponentDescription in list
			while (it.hasNext()) {
				componentDescription = (ComponentDescription) it.next();
				//bundleID = new Long (componentDescription.getBundle().getBundleId());

				if (componentDescription.isEnabled()) {

					// mark disabled
					componentDescription.setEnabled(false);

				}
			}
		}

		return;
	}

	/**
	 * disableComponent -  The specified component name must be in the same bundle as this component.
	 * Called by SC componentContext method
	 * 
	 * @param name The name of a component to disable 
	 *        
	 * @param bundle The bundle which contains the Service Component to be disabled
	 * 
	 */

	public void disableComponent(String name, Bundle bundle) {

		List disableComponentDescriptions = new ArrayList();
		//Long bundleID = null;

		//	Get the list of CDs for this bundle
		List componentDescriptionsList = (List) bundleToComponentDescriptions.get(new Long(bundle.getBundleId()));

		if (componentDescriptionsList != null) {
			Iterator it = componentDescriptionsList.iterator();

			//for each ComponentDescription in list
			while (it.hasNext()) {
				ComponentDescription componentDescription = (ComponentDescription) it.next();

				//bundleID = new Long (componentDescription.getBundle().getBundleId());

				//find the ComponentDescription with the specified name
				if (componentDescription.getName().equals(name)) {

					// if enabled then add to disabled list and mark disabled
					if (componentDescription.isEnabled()) {

						disableComponentDescriptions.add(componentDescription);

						componentDescription.setEnabled(false);

					}
				}
			}
		}

		// publish to resolver the list of CDs to disable
		workQueue.enqueueWork(this, REMOVE, disableComponentDescriptions);
		return;
	}

	/**
	 * @param workAction
	 * @param workObject
	 * @see org.eclipse.osgi.component.workqueue.WorkDispatcher#dispatchWork(int,
	 *      java.lang.Object)
	 */
	public void dispatchWork(int workAction, Object workObject) {

		List descriptions;
		descriptions = (List) workObject;
		switch (workAction) {
			case ADD :
				try {
					resolver.enableComponents(descriptions);
				} catch (IOException ex) {
					ex.printStackTrace();
					//TODO - fix to use correct error handling
				}
				break;
			case REMOVE :
				resolver.disableComponents(descriptions);
				break;
		}
	}

	/**
	 * Return the headers for the specified bundle.
	 * 
	 * @param bundle Bundle for which headers are desired.
	 * @return Headers for the specified bundle.
	 */
	public Dictionary getHeaders(final Bundle bundle) {
		if (System.getSecurityManager() == null) {
			return bundle.getHeaders();
		}

		return (Dictionary) AccessController.doPrivileged(new PrivilegedAction() {
			public Object run() {
				return bundle.getHeaders();
			}
		});

	}

	/**
	 * Validate the Component Description
	 * 
	 * @param componentDescription to be validated
	 * 
	 */
	public void validate(ComponentDescription componentDescription) {

		// if ComponentFactory and ServiceFactory are both specified then mark ComponentDescription as invalid
		if (
				(componentDescription.getFactory() != null) &&
				(componentDescription.getService() != null) &&
				(componentDescription.getService().isServicefactory())) {
			componentDescription.setValid(false);
			framework.publishFrameworkEvent(FrameworkEvent.ERROR, componentDescription.getBundle(), new Throwable("invalid to specify both ComponentFactory and ServiceFactory"));
		} else {
			componentDescription.setValid(true);
		}

	}

}
