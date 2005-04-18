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
import java.util.*;
import org.eclipse.osgi.component.Main;
import org.eclipse.osgi.component.instance.InstanceProcess;
import org.eclipse.osgi.component.model.*;
import org.eclipse.osgi.component.workqueue.WorkDispatcher;
import org.eclipse.osgi.component.workqueue.WorkQueue;
import org.osgi.framework.*;
import org.osgi.service.cm.*;
import org.osgi.service.component.ComponentConstants;
import org.osgi.service.component.ComponentException;

/**
 * 
 * Resolver - resolves the Service Component Descriptions This includes
 * resolving the required referenced services activating and registering
 * provided services also deactivating, binding and unbinding
 * 
 * @version $Revision$
 */
public class Resolver implements ServiceListener, ConfigurationListener, WorkDispatcher {

	/* set this to true to compile in debug messages */
	static final boolean DEBUG = false;

	/**
	 * The ConfigurationListener class
	 */
	static final String CONFIG_LISTENER_CLASS = "org.osgi.service.cm.ConfigurationListener";

	/**
	 * Service Component instances need to be built.
	 */
	public static final int BUILD = 1;

	/**
	 * Service Component instances need to be disposed.
	 */
	public static final int DISPOSE = 2;

	/**
	 * Service Component instances to bind dynamic
	 */
	public static final int DYNAMICBIND = 3;

	/**
	 * Service Component instances to unbind dynamic
	 */
	public static final int DYNAMICUNBIND = 4;

	/** Main class for the SCR */
	public Main main;

	public InstanceProcess instanceProcess;

	protected ComponentProperties componentProperties = null;

	/** ServiceComponent Runtime Bundle Context */
	protected BundleContext scrBundleContext;

	/** ConfigurationAdmin instance */
	protected ConfigurationAdmin configurationAdmin;

	/* CD:CD+P ( 1:n) */
	protected Hashtable componentDescriptionToProps;
	/* [CD+P] enabled */
	protected List componentDescriptionPropsEnabled;

	protected ServiceRegistration configListener;

	private WorkQueue workQueue;

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
		instanceProcess = new InstanceProcess(main);
		componentProperties = new ComponentProperties(main);
		addServiceListener();
		registerConfigurationListener();

	}

	/**
	 * Clean up the SCR is shutting down
	 */
	public void dispose() {

		removeServiceListener();
		removeConfigurationListener();
		instanceProcess.dispose();
		componentDescriptionPropsEnabled = null;
		componentDescriptionToProps = null;
	}

	/**
	 * enableComponents - called by the dispatchWorker
	 * 
	 * @param descriptions -
	 *            a list of all component descriptions for a single bundle to be
	 *            enabled Receive List of enabled CD's from ComponentCache
	 *            For each CD add to list of enabled create list of CD:CD+P
	 *            create list of CD+P:ref ( where ref is a Reference Object)
	 *            resolve CD+P
	 * 
	 * @param descriptions -
	 *            a list of all component descriptions for a single bundle to be
	 *            enabled
	 * 
	 */
	public void enableComponents(List componentDescriptions) throws IOException {

		//ComponentDescriptionProp componentDescriptionProp = null;
		Configuration config = null;
		Configuration[] configs = null;

		if (componentDescriptions != null) {
			Iterator it = componentDescriptions.iterator();
			while (it.hasNext()) {
				ComponentDescription componentDescription = (ComponentDescription) it.next();

				// check for a Configuration properties for this component
				try {
					config = componentProperties.getConfiguration(componentDescription.getName());
				} catch (Exception e) {
					main.framework.publishFrameworkEvent(FrameworkEvent.ERROR, componentDescription.getBundle(), e);
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
							throw new ComponentException("incompatible to specify both ComponentFactory and ManagedServiceFactory are incompatible");
						}
						try {
							// configs = configurationAdmin.listConfigurations("(service.factoryPid="+config.getFactoryPid()+")");
							configs = componentProperties.getConfigurationAdmin().listConfigurations("(service.factoryPid=" + config.getFactoryPid() + ")");
						} catch (Exception e) {
							main.framework.publishFrameworkEvent(FrameworkEvent.ERROR, componentDescription.getBundle(), e);
						}
						// for each MSF set of properties(P), map(CD, new CD+P(CD,P))
						if (configs != null) {
							for (int index = 0; index < configs.length; index++) {
								map(componentDescription, configs[index].getProperties());
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
		getEligible(null);
	}

	/**
	 * Create the CDP and add to the maps
	 * 
	 * @param componentDescription
	 * @param properties
	 * @throws IOException
	 */
	public void map(ComponentDescription componentDescription, Dictionary properties) throws IOException {

		ComponentDescriptionProp componentDescriptionProp;
		List references = new ArrayList();
		List componentDescriptionProps = null;

		// Create CD+P
		componentDescriptionProp = new ComponentDescriptionProp(componentDescription, properties);

		// if CD not already in Map create list to add to map
		if (componentDescriptionToProps.get(componentDescription) == null) {
			// create list
			componentDescriptionProps = new ArrayList();
			componentDescriptionProps.add(componentDescriptionProp);

			// else add to current list
		} else {
			componentDescriptionProps = (List) componentDescriptionToProps.get(componentDescription);
			componentDescriptionProps.add(componentDescriptionProp);
		}

		// add to CD:CD+P map
		componentDescriptionToProps.put(componentDescription, componentDescriptionProps);

		List services = new ArrayList();
		ServiceDescription serviceDescription = componentDescription.getService();
		if (serviceDescription != null) {
			ProvideDescription[] provideDescription = serviceDescription.getProvides();
			for (int i = 0; i < provideDescription.length; i++) {
				services.add(provideDescription[i].getInterfacename());
			}

			componentDescriptionProp.setServiceProvided(services);
		}

		// Get all the required service reference descriptions for this
		// ComponentDescription
		ReferenceDescription[] referenceDescriptions = componentDescription.getReferences();

		// for each Reference Description, create a reference object
		if (referenceDescriptions.length > 0) {
			int i = 0;
			while (i < referenceDescriptions.length) {

				// create new Reference Object and add to CD+P:ref map
				Reference ref = new Reference(referenceDescriptions[i], properties);
				references.add(ref);
				i++;
			}

			// add to componentDescriptionPropToRefMap
			componentDescriptionProp.setReferences(references);

		}

		// add CD+P to set
		componentDescriptionPropsEnabled.add(componentDescriptionProp);
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
	public void disableComponents(List componentDescriptions) {

		ComponentDescriptionProp componentDescriptionProp;
		ComponentDescription componentDescriptionInput;
		ComponentDescription componentDescription;
		List componentDescriptionProps;

		List removeList = new ArrayList();

		// Received list of CDs to disable
		if (componentDescriptions != null) {
			Iterator it = componentDescriptions.iterator();
			while (it.hasNext()) {

				// get the first CD
				componentDescriptionInput = (ComponentDescription) it.next();

				// process CD:CDP list and see if there is a match
				Enumeration keys = componentDescriptionToProps.keys();
				while (keys.hasMoreElements()) {
					componentDescription = (ComponentDescription) keys.nextElement();

					// see if the CD's match
					if ((componentDescription.getName()).equals(componentDescriptionInput.getName())) {

						// then get the list of CDPs for this CD
						componentDescriptionProps = (List) componentDescriptionToProps.get(componentDescription);
						if (componentDescriptionProps != null) {
							Iterator iter = componentDescriptionProps.iterator();
							while (iter.hasNext()) {

								componentDescriptionProp = (ComponentDescriptionProp) iter.next();
								removeList.add(componentDescriptionProp);
								componentDescriptionPropsEnabled.remove(componentDescriptionProp);
							}
						}
						componentDescriptionToProps.remove(componentDescription);
					}
				}
			}
		}
		// re-run resolver - there might be components that depended on this component
		getEligible(null);

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
	public void getEligible(ServiceEvent event) {

		List resolvedComponents = resolveEligible();

		if ((event != null) && (event.getType() == ServiceEvent.REGISTERED)) {
			Hashtable dynamicBind = selectDynamicBind(resolvedComponents, event.getServiceReference());
			if (!dynamicBind.isEmpty()) {
				workQueue.enqueueWork(this, DYNAMICBIND, dynamicBind);
			}
			// check if there are Service Components which  need to dynamically unbind from this unregistering Service
		} else if ((event != null) && (event.getType() == ServiceEvent.UNREGISTERING)) {
			//Pass in the set of currently resolved components, check each one - do we need to unbind
			List dynamicUnBind = selectDynamicUnBind(resolvedComponents, event.getServiceReference());
			if (!dynamicUnBind.isEmpty()) {
				workQueue.enqueueWork(this, DYNAMICUNBIND, dynamicUnBind);
			}
		}

		// obtain list of newly eligible components to be sent to the work quew
		List newlyEligibleComponents = selectNewlyEligible(resolvedComponents);
		if (!newlyEligibleComponents.isEmpty()) {
			workQueue.enqueueWork(this, BUILD, newlyEligibleComponents);
		}
		List newlyIneligibleComponents = selectNewlyInEligible(resolvedComponents);
		if (!newlyIneligibleComponents.isEmpty())
			workQueue.enqueueWork(this, DISPOSE, newlyIneligibleComponents);
	}

	public List resolveEligible() {
		List enabledCDPs = (List) ((ArrayList) componentDescriptionPropsEnabled).clone();
		boolean runAgain = true;
		while (runAgain) {
			Iterator it = enabledCDPs.iterator();
			runAgain = false;
			while (it.hasNext() && !runAgain) {
				ComponentDescriptionProp cdp = (ComponentDescriptionProp) it.next();
				List refs = cdp.getReferences();
				Iterator iterator = refs.iterator();
				while (iterator.hasNext()) {
					// Loop though all the references (dependencies)for a given
					// cdp. If a dependency is not met, remove it's associated cdp and
					// re-run the algorithm
					Reference reference = (Reference) iterator.next();
					if (reference != null) {
						if (!resolveReference(cdp, reference, enabledCDPs)) {
							cdp.clearReferenceCDPs();
							enabledCDPs.remove(cdp);
							runAgain = true;
							break; //we need to re-run as our lists have changed
						}
					}
				}
			}
		}
		try {
			List sortedCDPs = sortCDPs(enabledCDPs);
			return sortedCDPs;
		} catch (CircularityException ex) {
			ComponentDescriptionProp circularCDP = ex.getCircularDependency();

			//log the error
			main.framework.publishFrameworkEvent(FrameworkEvent.ERROR, circularCDP.getComponentDescription().getBundle(), ex);

			//remove offending CDP and re-resolve
			componentDescriptionPropsEnabled.remove(ex.getCircularDependency());
			return resolveEligible();
		}
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
			System.out.println("ServiceChanged: serviceReference = " + reference);
			System.out.println("ServiceChanged: Event type = " + eventType + " , reference.getBundle() = " + reference.getBundle());
		}

		// No need to process events that SCR initiated
		// check for component.id and ignore the event if set
		if ((reference.getProperty(ComponentConstants.COMPONENT_ID) == null) && (eventType == ServiceEvent.REGISTERED) || eventType == ServiceEvent.UNREGISTERING) {

			getEligible(event);
		}

	}

	/**
	 * @param workAction
	 * @param workObject
	 * @see org.eclipse.osgi.component.workqueue.WorkDispatcher#dispatchWork(int,
	 *      java.lang.Object)
	 */
	public void dispatchWork(int workAction, Object workObject) {
		switch (workAction) {
			case BUILD :
				instanceProcess.buildInstances((List) workObject);
				break;
			case DISPOSE :
				instanceProcess.disposeInstances((List) workObject);
				break;
			case DYNAMICBIND :
				instanceProcess.dynamicBind((Hashtable) workObject);
				break;
			case DYNAMICUNBIND :
				instanceProcess.dynamicUnBind((List) workObject);
				break;
		}
	}

	private List selectNewlyEligible(List enabledCDPs) {
		// loop through selected CDPs. Keep only the ones that are newly eligible
		List eligibleCDPs = new ArrayList();
		Iterator iterator = enabledCDPs.iterator();
		while (iterator.hasNext()) {
			ComponentDescriptionProp cdp = (ComponentDescriptionProp) iterator.next();
			ComponentDescription cd = cdp.getComponentDescription();
			if (!cd.isEligible()) {
				cd.setEligible(true);
				eligibleCDPs.add(cdp);
			}
		}
		return eligibleCDPs;
	}

	private List selectNewlyInEligible(List eligibleCDPs) {
		//		 loop through selected CDPs. Keep only the ones that are newly ineligible
		List inEligibleCDPs = new ArrayList();
		Iterator iterator = componentDescriptionPropsEnabled.iterator();
		while (iterator.hasNext()) {
			ComponentDescriptionProp cdp = (ComponentDescriptionProp) iterator.next();
			ComponentDescription cd = cdp.getComponentDescription();
			//if it is no longer on the eligible list, put it on the newlyInEligible list
			if (cd.isEligible() && !eligibleCDPs.contains(cdp)) {
				cd.setEligible(false);
				inEligibleCDPs.add(cdp);
			}
		}
		return inEligibleCDPs;
	}

	private boolean resolveReference(ComponentDescriptionProp cdp, Reference reference, List enabledCDPs) {
		//if the cardinality is "0..1" or "0..n" then this refernce is not required
		//TODO - how do we place this refernece in the ordering?
		if (!reference.isRequiredFor(cdp.getComponentDescription()))
			return true;

		if (reference.hasLegacyProviders(scrBundleContext))
			return true; //we found an availible provider in the legacy services

		// loop thru all components to match providers of services
		Iterator it = enabledCDPs.iterator();
		while (it.hasNext()) {
			ComponentDescriptionProp cdpRefLookup = (ComponentDescriptionProp) it.next();
			List provideList = cdpRefLookup.getServicesPrivided();

			if (provideList.contains(reference.getReferenceDescription().getInterfacename())) {
				cdp.setReferenceCDP(cdpRefLookup);
				return true;
			}
		}

		return false;
	}

	/**
	 * Listen for configuration changes
	 * 
	 * Service Components can receive properties from the Configuration 
	 * Admin service. If a Service Component is activated and it’s properties
	 * are updated in the Configuration Admin service, the SCR must deactivate the component
	 * and activate the component again using the new properties.
	 * 
	 * @param event ConfigurationEvent
	 */
	public void configurationEvent(ConfigurationEvent event) {

		List componentDescriptions = new ArrayList();

		String pid = event.getPid();
		if (pid == null)
			pid = event.getFactoryPid();

		// If configuration change was for a Service Component
		// then deactivate and reactivate the Service Component
		Enumeration keys = componentDescriptionToProps.keys();
		while (keys.hasMoreElements()) {
			ComponentDescription componentDescription = (ComponentDescription) keys.nextElement();
			if (componentDescription.getName().equals(pid)) {
				componentDescription.setReactivate(true);
				componentDescriptions.add(componentDescription);
			}
		}
		disableComponents(componentDescriptions);
	}

	/**
	 * Called from Instance Process when dispose is complete
	 * 
	 * @param List of componentDescriptionProps that have been disposed of
	 */
	public void disposedComponents(List componentDescriptionProps) {

		List componentDescriptions = new ArrayList();
		ComponentDescriptionProp componentDescriptionProp = null;
		ComponentDescription componentDescription = null;

		//If reactivate is set then collect the CDs and call enableComponents
		Iterator it = componentDescriptionProps.iterator();
		while (it.hasNext()) {
			componentDescriptionProp = (ComponentDescriptionProp) it.next();
			componentDescription = componentDescriptionProp.getComponentDescription();
			if (componentDescription.isReactivate()) {
				componentDescription.setReactivate(false);
				componentDescription.setEligible(false);
				componentDescriptions.add(componentDescription);
			}
		}

		if (!componentDescriptions.isEmpty()) {
			try {
				enableComponents(componentDescriptions);
			} catch (Exception e) {
				main.framework.publishFrameworkEvent(FrameworkEvent.ERROR, componentDescription.getBundle(), e);
			}
		}
	}

	/**
	 * Called from Instance Process when build is complete
	 * 
	 * @param List of componentDescriptionProps that have been built
	 */
	public void builtComponents(List componentDescriptionProps) {
		//TODO does any action need to take place on build complete
	}

	/**
	 * registerConfigListener - Listen for changes in the configuration
	 * 
	 */
	public void registerConfigurationListener() {
		configListener = scrBundleContext.registerService(CONFIG_LISTENER_CLASS, this, null);

	}

	/**
	 * removeConfigurationListener -
	 * 
	 */
	public void removeConfigurationListener() {
		if (configListener != null)
			scrBundleContext.ungetService(configListener.getReference());

	}

	private Hashtable selectDynamicBind(List cdps, ServiceReference serviceReference) {
		Hashtable bindTable = new Hashtable();
		Iterator it = cdps.iterator();
		while (it.hasNext()) {
			ComponentDescriptionProp cdp = (ComponentDescriptionProp) it.next();
			//if it is not already eligible it will bind with the static cdps
			if (cdp.getComponentDescription().isEligible()) {
				List references = cdp.getReferences();
				Iterator it_ = references.iterator();
				while (it_.hasNext()) {
					Reference reference = (Reference) it_.next();
					if (reference.bindNewReference(serviceReference)) {
						bindTable.put(reference, cdp);
					}
				}
			}
		}
		return bindTable;
	}

	/**
	 * selectDynamicUnBind
	 * 		Determine which resolved component description with properties need to unbind from this unregistering service
	 * 		Return map of reference description and component description with properties, for each.
	 * @param cdps
	 * @param serviceReference
	 * @return
	 */
	private List selectDynamicUnBind(List cdps, ServiceReference serviceReference) {

		List unbindJobs = new ArrayList();

		Iterator it = cdps.iterator();
		while (it.hasNext()) {
			ComponentDescriptionProp cdp = (ComponentDescriptionProp) it.next();
			List references = (List) cdp.getReferences();
			Iterator it_ = references.iterator();
			while (it_.hasNext()) {
				Reference reference = (Reference) it_.next();
				//Does the cdp require this service, use the Reference object to check
				if (reference.unBindReference(serviceReference)) {
					DynamicUnbindJob unbindJob = new DynamicUnbindJob();
					unbindJob.component = cdp;
					unbindJob.reference = reference;
					unbindJob.serviceReference = serviceReference;
					unbindJobs.add(unbindJob);
				}
			}
		}
		return unbindJobs;
	}

	static public class DynamicUnbindJob {
		public ComponentDescriptionProp component;
		public Reference reference;
		public ServiceReference serviceReference;
	}

	private List sortCDPs(List cdps) throws CircularityException {
		Iterator it = cdps.iterator();
		List sortedList = new ArrayList();
		List circularityCheck;
		while (it.hasNext()) {
			ComponentDescriptionProp cdp = (ComponentDescriptionProp) it.next();
			circularityCheck = new ArrayList();
			writeCDPDependencies(cdp, sortedList, circularityCheck);
		}
		return sortedList;
	}

	private void writeCDPDependencies(ComponentDescriptionProp cdp, List sortedList, List circularityCheck) throws CircularityException {
		if (circularityCheck.contains(cdp)) {
			throw new CircularityException(cdp);
		}
		circularityCheck.add(cdp);
		//the component is already added it and all it's dependencies -egad, how do we unwind?
		if (sortedList.contains(cdp)) {
			return;
		}
		List referenceCDPs = cdp.getReferenceCDPs();
		Iterator it = referenceCDPs.iterator();
		//first, add the CDP's dependencies
		while (it.hasNext()) {
			ComponentDescriptionProp referenceCDP = (ComponentDescriptionProp) it.next();
			writeCDPDependencies(referenceCDP, sortedList, circularityCheck);
		}
		//finally write the cdp
		sortedList.add(cdp);
	}
}
