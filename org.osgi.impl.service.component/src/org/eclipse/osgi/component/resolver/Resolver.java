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

package org.eclipse.osgi.component.resolver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;

import org.osgi.framework.*;
import org.osgi.service.cm.*;
import org.osgi.service.component.ComponentException;

import org.eclipse.osgi.component.Main;
import org.eclipse.osgi.component.instance.InstanceProcess;
import org.eclipse.osgi.component.model.ComponentDescription;
import org.eclipse.osgi.component.model.ComponentDescriptionProp;
import org.eclipse.osgi.component.model.ProvideDescription;
import org.eclipse.osgi.component.model.ReferenceDescription;
import org.eclipse.osgi.component.model.ServiceDescription;
import org.eclipse.osgi.component.workqueue.WorkDispatcher;
import org.eclipse.osgi.component.workqueue.WorkQueue;


/**
 * 
 * Resolver - resolves the Service Component Descriptions This includes
 * resolving the required referenced services activating and registering
 * provided services also deactivating, binding and unbinding
 * 
 * @version $Revision$
 */
public class Resolver implements ServiceListener, ConfigurationListener,
		WorkDispatcher {

	/* set this to true to compile in debug messages */
	static final boolean DEBUG = false;

	/**
	 * Service Component instances need to be built.
	 */
	public static final int BUILD = 1;

	/**
	 * Service Component instances need to be disposed.
	 */
	public static final int DISPOSE = 2;

	/** Main class for the SCR */
	public Main main;

	public BundleContext scrBundleContext;

	/** ConfigurationAdmin instance */
	ConfigurationAdmin configurationAdmin;

	/* CD:CD+P ( 1:n) */
	protected Hashtable componentDescriptionToProps;

	/* CD+P:ref ( 1:n) */
	protected Hashtable componentDescriptionPropToRefs;

	/* CD+P:provides (1:n) */
	protected Hashtable componentDescriptionPropToProvides;

	/* [CD+P] enabled */
	protected ArrayList componentDescriptionPropsEnabled;

	private WorkQueue workQueue;

	InstanceProcess instanceProcess;

	public ComponentProperties componentProperties = null;

	/** The ConfigurationListener class */
	public final static String CONFIG_LISTENER_CLASS = "org.osgi.service.cm.ConfigurationListener";

	ServiceRegistration configListener;

	/**
	 * Resolver constructor
	 * 
	 * @param main 
	 *            Main class of SCR
	 */
	public Resolver(Main main) {
		this.main = main;

		// for now use Main's workqueue
		workQueue = main.workQueue;
		scrBundleContext = main.context;
		componentDescriptionPropsEnabled = new ArrayList();
		componentDescriptionToProps = new Hashtable();
		componentDescriptionPropToRefs = new Hashtable();
		componentDescriptionPropToProvides = new Hashtable();
		instanceProcess = new InstanceProcess(main);
		componentProperties = new ComponentProperties(main);

		addServiceListener();
		registerConfigurationListener();
	}

	/**
	 * Clean up the SCR is shutting down
	 */
	public void dispose() {

		main = null;
		componentDescriptionPropsEnabled = null;
		componentDescriptionToProps = null;
		componentDescriptionPropToRefs = null;
		instanceProcess = null;

		removeServiceListener();
		removeConfigurationListener();

	}

	
	/**
	 * enableComponents - called by the dispatchWorker
	 * 
	 * @param descriptions -
	 *            a list of all component descriptions for a single bundle to be
	 *            enabled Receive ArrayList of enabled CD's from ComponentCache
	 *            For each CD add to list of enabled create list of CD:CD+P
	 *            create list of CD+P:ref ( where ref is a Reference Object)
	 *            resolve CD+P
	 * 
	 * @param descriptions -
	 *            a list of all component descriptions for a single bundle to be
	 *            enabled
	 * 
	 */
	public void enableComponents(ArrayList componentDescriptions)
			throws IOException {

		ComponentDescriptionProp componentDescriptionProp = null;
		Configuration config = null;
		Configuration[] configs = null;

		if (componentDescriptions != null) {
			Iterator it = componentDescriptions.iterator();
			while (it.hasNext()) {
				ComponentDescription componentDescription = (ComponentDescription) it
						.next();

				// check for a Configuration properties for this component
				try {
					config = componentProperties
							.getConfiguration(componentDescription.getName());
				} catch (Exception e) {
					main.framework.publishFrameworkEvent(FrameworkEvent.ERROR,
							componentDescription.getBundle(), e);
				}

				// if no Configuration
				if (config == null) {

					// create ComponentDescriptionProp
					map(componentDescription, null);

				} else {

					// if ManagedServiceFactory
					if (config.getFactoryPid() != null) {

						// if ComponentFactory is specified
						if (componentDescription.getFactory() != null) {
							throw new ComponentException(
									"incompatible to specify both ComponentFactory and ManagedServiceFactory are incompatible");
						} else {
							try {
								// configs =
								// configurationAdmin.listConfigurations("(service.factoryPid="+config.getFactoryPid()+")");
								configs = componentProperties
										.getConfigurationAdmin()
										.listConfigurations(
												"(service.factoryPid="
														+ config
																.getFactoryPid()
														+ ")");
							} catch (Exception e) {
								main.framework.publishFrameworkEvent(
										FrameworkEvent.ERROR,
										componentDescription.getBundle(), e);
							}
							// for each MSF set of properties(P), map(CD, new
							// CD+P(CD,P))
							if (configs != null) {
								for (int index = 0; index < configs.length; index++) {
									String configPID = configs[index].getPid();
									map(componentDescription, configs[index]
											.getProperties());
								}
							}
						}
					} else {
						// if Service
						map(componentDescription, config.getProperties());
					}

				}

			}
		}
		// resolve
		getEligible();
	}

	/**
	 * Create the CDP and add to the maps
	 * 
	 * @param componentDescription
	 * @param properties
	 * @throws IOException
	 */
	public void map(ComponentDescription componentDescription,
			Dictionary properties) throws IOException {

		ComponentDescriptionProp componentDescriptionProp;
		ArrayList references = new ArrayList();
		ArrayList componentDescriptionProps = null;

		// Create CD+P
		componentDescriptionProp = new ComponentDescriptionProp(
				componentDescription, properties);

		// if CD not already in Map create list to add to map
		if (componentDescriptionToProps.get(componentDescription) == null) {
			// create list
			componentDescriptionProps = new ArrayList();
			componentDescriptionProps.add(componentDescriptionProp);

			// else add to current list
		} else {
			componentDescriptionProps = (ArrayList) componentDescriptionToProps
					.get(componentDescription);
			componentDescriptionProps.add(componentDescriptionProp);
		}

		// add to CD:CD+P map
		componentDescriptionToProps.put(componentDescription,
				componentDescriptionProps);

		ArrayList services = new ArrayList();
		ServiceDescription serviceDescription = componentDescription
				.getService();
		if (serviceDescription != null) {
			ProvideDescription[] provideDescription = serviceDescription
					.getProvides();
			for (int i = 0; i < provideDescription.length; i++) {
				services.add(provideDescription[i].getInterfacename());
			}

			componentDescriptionPropToProvides.put(componentDescriptionProp,
					services);
		}

		// Get all the required service reference descriptions for this
		// ComponentDescription
		ReferenceDescription[] referenceDescriptions = componentDescription
				.getReferences();

		// for each Reference Description, create a reference object
		if (referenceDescriptions.length > 0) {
			int i = 0;
			while (i < referenceDescriptions.length) {

				// create new Reference Object and add to CD+P:ref map
				Reference ref = new Reference(referenceDescriptions[i]);
				ref.resolveReferences(scrBundleContext);
				references.add(ref);
				i++;
			}

			// add to componentDescriptionPropToRefMap
			componentDescriptionPropToRefs.put(componentDescriptionProp,
					references);
			//add CD+P to set
			componentDescriptionPropsEnabled.add(componentDescriptionProp);
		} 
		else {
			//no required references lets just get this created
			if (!componentDescription.isEligible()){
			    componentDescription.setEligible(true);
				workQueue.enqueueWork(this, BUILD, componentDescriptionProps);
			}
		}

	}

	/**
	 * Disable list of ComponentDescriptions
	 * 
	 * get all CD+P's from CD:CD+P Map get instances from CD+P:list of instance
	 * (1:n) map
	 * 
	 * Strip out of Map all CD+P's Continue to pull string check each Ref
	 * dependency and continue to pull out CD+P's if they become not eligible
	 * Then call Resolver to re-resolve
	 * 
	 * @param componentDescriptions
	 */
	public void disableComponents(ArrayList componentDescriptions) {

		ComponentDescriptionProp componentDescriptionProp;
		ComponentDescription componentDescriptionInput;
		ComponentDescription componentDescription;
		ArrayList componentDescriptionProps;

		ArrayList removeList = new ArrayList();

		// Received list of CDs to disable
		if (componentDescriptions != null) {
			Iterator it = componentDescriptions.iterator();
			while (it.hasNext()) {

				// get the first CD
				componentDescriptionInput = (ComponentDescription) it.next();

				// process CD:CDP list and see if there is a match
				Enumeration keys = componentDescriptionToProps.keys();
				while (keys.hasMoreElements()) {
					componentDescription = (ComponentDescription) keys
							.nextElement();

					// see if the CD's match
					if ((componentDescription.getName())
							.equals(componentDescriptionInput.getName())) {

						// then get the list of CDPs for this CD
						componentDescriptionProps = (ArrayList) componentDescriptionToProps
								.get(componentDescription);
						if (componentDescriptionProps != null) {
							Iterator iter = componentDescriptionProps
									.iterator();
							while (iter.hasNext()) {

								componentDescriptionProp = (ComponentDescriptionProp) iter
										.next();
								removeList.add(componentDescriptionProp);

								componentDescriptionPropToRefs
										.remove(componentDescriptionProp);

								componentDescriptionPropsEnabled
										.remove(componentDescriptionProp);

							}
						}
						componentDescriptionToProps
								.remove(componentDescription);
					}

				}

			}
		}

		// dispose of all instances for this CDP
		workQueue.enqueueWork(this, DISPOSE, removeList);

	}

	/**
	 * Get the Eligible Components
	 * 
	 * loop through CD+P list of enabled get references check if eligible if
	 * true add to eligible list send to Instance Process
	 * 
	 */
	public void getEligible() {

		// obtain list of newly eligible components to be sent to the work quew
		ArrayList resolvedComponents = resolveEligible();
		if (!resolvedComponents.isEmpty()) {
			workQueue.enqueueWork(this, BUILD, resolvedComponents);
		}

	}

	public ArrayList resolveEligible() {
		ArrayList eligibleCDPs = new ArrayList();
		ArrayList enabledCDPs = (ArrayList) componentDescriptionPropsEnabled
				.clone();
		Hashtable references = (Hashtable) componentDescriptionPropToRefs
				.clone();
		Hashtable providers = (Hashtable) componentDescriptionPropToProvides
				.clone();
		boolean match = false;
		Enumeration e = references.keys();
		boolean runAgain = true;
		while (runAgain) {
			runAgain = false;
			while (e.hasMoreElements()) {
				ComponentDescriptionProp cdp = (ComponentDescriptionProp) e
						.nextElement();
				ArrayList refs = (ArrayList) references.get(cdp);
				Iterator iterator = refs.iterator();
				while (iterator.hasNext()) {
					// Loop though all the references (dependencies)for a given
					// cdp. If
					// a dependency is not met, remove it's associated cdp and
					// re-run the
					// algorithm
					Reference reference = (Reference) iterator.next();
					if (reference != null) {
						reference.resolveReferences(scrBundleContext);
						// loop thru all providers of services
						Enumeration p = providers.elements();
						match = false;
						while (p.hasMoreElements()) {
							ArrayList values = (ArrayList) p
									.nextElement();
							if (values.contains(reference
								.getInterfaceName())) {
								match = true;
							}
						}
						// if no match of the required reference from all the
						// provided services then remove from lists.
						if (!match) {
							providers.remove(cdp);
							references.remove(cdp);
							enabledCDPs.remove(cdp);
							runAgain = true;
						}
					}
				}
			}
		}
		// loop through selected CDPs. Keep only the ones that are eligible
		// and make sure they are in dependency order
		// circular dependencies not allowed
		Iterator iterator = enabledCDPs.iterator();
		while (iterator.hasNext()) {
			ComponentDescriptionProp cdp = (ComponentDescriptionProp) iterator
					.next();
			ComponentDescription cd = cdp.getComponentDescription();
			if (!cd.isEligible()) {
			    cd.setEligible(true);
				eligibleCDPs.add(cdp);
				try {
					addReferences(eligibleCDPs, cdp, references, null);
				} catch (Exception ex) {
					//TODO - use proper error handling
					System.out.println("Circularity Error");
				}
			}
		}
		return eligibleCDPs;
	}

	/**
	 * 
	 * 
	 * @param cdp
	 */
	public void markFailedInstance(ComponentDescriptionProp cdp)
	{
		componentDescriptionPropsEnabled.remove(cdp);
		componentDescriptionPropToProvides.remove(cdp);
		componentDescriptionPropToRefs.remove(cdp);
		
		ArrayList cdps = resolveEligible();
		if (!cdps.isEmpty()) {
			workQueue.enqueueWork(this,BUILD,cdps);
		}
		
	}
	
	
	private void addReferences(ArrayList list, ComponentDescriptionProp cdp,
			Hashtable references, ArrayList circularityCheck) throws Exception {
		// if it has already been added, then it's dependencies have already be
		// added as well
		if (list.contains(cdp))
			return;
		if (circularityCheck == null)
			circularityCheck = new ArrayList();// circularity check
		if (circularityCheck.contains(cdp)) {
			throw new Exception("Circularity Error Detected");
		}
		circularityCheck.add(cdp);

		// add all the references of the CDP first.
		ArrayList refList = (ArrayList) references.get(cdp);
		if (refList != null) {
			Iterator i = refList.iterator();
			while (i.hasNext()) {
				ComponentDescriptionProp refProp = (ComponentDescriptionProp) i
						.next();
				addReferences(list, refProp, references, circularityCheck);
			}
		}
		list.add(cdp);
	}

	/**
	 * getInEligible
	 * 
	 * Process list of enabled CD+Ps
	 * Get required references for each CDP and check eligibility
	 * if state has changed from eligible to ineligible add to ineligible list
	 *
	 * Send to Instance Process to dispose
	 * 
	 */
	public void getInEligible() {

		
		ComponentDescriptionProp componentDescriptionProp = null;
		ArrayList ineligibleComponents = new ArrayList();
		boolean eligibilityChanged;

		// loop through CD+P list of enabled
		if (componentDescriptionPropsEnabled != null) {
			Iterator it = componentDescriptionPropsEnabled.iterator();
			while (it.hasNext()) {

				// get reference objects from CD+P:ref list
				componentDescriptionProp = (ComponentDescriptionProp) it.next();
				ArrayList refs = (ArrayList) componentDescriptionPropToRefs
						.get(componentDescriptionProp);

				Iterator iter;
				// loop thru reference objects - check isEligible
				if (refs != null) {

					eligibilityChanged = false;
					iter = refs.iterator();

					while (iter.hasNext() && !eligibilityChanged) {
						Reference ref = (Reference) iter.next();
						if (ref.isEligible()) {
							ref.resolveReferences(scrBundleContext);

							// if state changed from eligible to ineligible add
							// to disable list
							if (!ref.isEligible()) {
								componentDescriptionProp
										.getComponentDescription().setEligible(
												false);
								ineligibleComponents
										.add(componentDescriptionProp);
								eligibilityChanged = true;
							}

						}
					}
				}

			}
		}
		if (!ineligibleComponents.isEmpty())
			workQueue.enqueueWork(this, DISPOSE, ineligibleComponents);
	}

	/**
	 * addService Listener - Listen for changes in the referenced services
	 * 
	 */
	public void addServiceListener() {
		try {
			scrBundleContext.addServiceListener(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * removeServiceListener -
	 * 
	 */
	public void removeServiceListener() {
		try {
			scrBundleContext.removeServiceListener(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Listen for service change events
	 * 
	 * @param event 
	 */
	public void serviceChanged(ServiceEvent event) {

		ServiceReference reference = event.getServiceReference();
		int eventType = event.getType();
				

		if (DEBUG) {
			System.out.println("ServiceChanged: serviceReference = "
					+ reference);
			System.out.println("ServiceChanged: Event type = " + eventType
					+ " , reference.getBundle() = " + reference.getBundle());
		}
		
		if (eventType == ServiceEvent.REGISTERED) {
			getEligible();
		} else if (eventType == ServiceEvent.UNREGISTERING) {
			getInEligible();
		}

	}

	/**
	 * registerConfigListener - Listen for changes in the configuration
	 * 
	 */
	public void registerConfigurationListener() {
		configListener = scrBundleContext.registerService(
				CONFIG_LISTENER_CLASS, this, null);

	}

	/**
	 * removeConfigurationListener -
	 * 
	 */
	public void removeConfigurationListener() {
		if (configListener != null)
			scrBundleContext.ungetService(configListener.getReference());

	}

	/**
	 * Listen for configuration changes
	 * 
	 * @param event -
	 *            ConfigurationEvent
	 */
	public void configurationEvent(ConfigurationEvent event) {
		if (DEBUG)
			System.out.println("Resolver: configurationEvent: event = " + event);
	}

	/**
	 * @param workAction
	 * @param workObject
	 * @see org.eclipse.osgi.component.workqueue.WorkDispatcher#dispatchWork(int,
	 *      java.lang.Object)
	 */
	public void dispatchWork(int workAction, Object workObject) {
		ArrayList componentDescriptionProps = (ArrayList) workObject;
		switch (workAction) {
		case BUILD:
			instanceProcess.buildInstances(componentDescriptionProps);
			break;
		case DISPOSE:
			instanceProcess.disposeInstances(componentDescriptionProps);
			break;
		}
	}
}
