/*
 * 
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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.eclipse.osgi.component.Log;
import org.eclipse.osgi.component.Main;
import org.eclipse.osgi.component.instance.InstanceProcess;
import org.eclipse.osgi.component.model.ComponentDescription;
import org.eclipse.osgi.component.model.ComponentDescriptionProp;
import org.eclipse.osgi.component.model.PropertyDescription;
import org.eclipse.osgi.component.model.PropertyResourceDescription;
import org.eclipse.osgi.component.model.PropertyValueDescription;
import org.eclipse.osgi.component.model.ProvideDescription;
import org.eclipse.osgi.component.model.ReferenceDescription;
import org.eclipse.osgi.component.workqueue.WorkDispatcher;
import org.eclipse.osgi.component.workqueue.WorkQueue;
import org.osgi.framework.AllServiceListener;
import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServicePermission;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentConstants;
import org.osgi.service.component.ComponentException;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

/**
 * 
 * Resolver - resolves the Service Component Descriptions This includes
 * resolving the required referenced services activating and registering
 * provided services also deactivating, binding and unbinding
 * 
 * @version $Revision$
 */
public class Resolver implements AllServiceListener, WorkDispatcher {

	/* set this to true to compile in debug messages */
	static final boolean DEBUG = false;

	/** next free component id. */
	static protected long componentid;

	/**
	 * The ConfigurationListener class
	 */
	static final String CONFIG_LISTENER_CLASS = "org.osgi.service.cm.ConfigurationListener";
	static final String CMADMIN_SERVICE_CLASS = "org.osgi.service.cm.ConfigurationAdmin";

	/* ServiceTracker for configurationAdmin */
	private ServiceTracker configAdminTracker;

	/**
	 * Service Component instances need to be built.
	 */
	public static final int BUILD = 1;

	/**
	 * Service Component instances to bind dynamic
	 */
	public static final int DYNAMICBIND = 3;

	/** 
	 * Main class for the SCR 
	 */
	public Main main;

	public InstanceProcess instanceProcess;

	protected ComponentProperties componentProperties = null;

	//note satisfiedCDPs is a subset of enabledCDPs
	public List enabledCDPs, satisfiedCDPs;
	public Map enabledCDsByName;

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

		componentid = 1;
		
		// for now use Main's workqueue
		workQueue = main.workQueue;
		enabledCDPs = new ArrayList();
		satisfiedCDPs = new ArrayList();
		enabledCDsByName = new HashMap();
		instanceProcess = new InstanceProcess(main);
		componentProperties = new ComponentProperties(main);
		addServiceListener();
		configAdminTracker = new ServiceTracker(main.context, CMADMIN_SERVICE_CLASS, null);
		configAdminTracker.open(true); //true for track all services

	}

	/**
	 * Clean up the SCR is shutting down
	 */
	public void dispose() {

		removeServiceListener();
		instanceProcess.dispose();
		configAdminTracker.close();
		configAdminTracker = null;
		enabledCDPs = null;
		satisfiedCDPs = null;

	}

	/**
	 * enableComponents - called by the dispatchWorker
	 * 
	 * @param descriptions -
	 *            a list of all component descriptions for a single bundle to be
	 *            enabled. Receive List of enabled CD's from ComponentCache
	 *            For each CD add to list of enabled create list of CD:CD+P
	 *            create list of CD+P:ref ( where ref is a Reference Object)
	 *            resolve CD+P
	 * 
	 */
	public void enableComponents(List componentDescriptions) throws ComponentException {

		Configuration config = null;
		Configuration[] configs = null;

		if (componentDescriptions != null) {
			Iterator it = componentDescriptions.iterator();
			while (it.hasNext()) {
				ComponentDescription cd = (ComponentDescription) it.next();
				
				//add to our enabled lookup list
				enabledCDsByName.put(cd.getName(),cd);
				
				// check for a Configuration properties for this component
				try {
					config = componentProperties.getConfiguration(cd.getName());
				} catch (IOException e) {
					//Log it and continue
					Log.log(1, "[SCR] IOException when getting Configuration Properties. ", e);
				}

				// if no Configuration
				if (config == null) {
					// create ComponentDescriptionProp
					map(cd, null);

				} else {

					// if ManagedServiceFactory
					if (config.getFactoryPid() != null) {

						// if ComponentFactory is specified
						if (cd.getFactory() != null) {
							throw new ComponentException("incompatible to specify both ComponentFactory and ManagedServiceFactory are incompatible");
						}
						
						try {
							ConfigurationAdmin cm = (ConfigurationAdmin) configAdminTracker.getService();
							configs = cm.listConfigurations("(service.factoryPid=" + config.getFactoryPid() + ")");
						} catch (InvalidSyntaxException e) {
							Log.log(1, "[SCR] InvalidSyntaxException when getting CM Configurations. ", e);
						} catch (IOException e) {
							Log.log(1, "[SCR] IOException when getting CM Configurations. ", e);
						}
						
						// for each MSF set of properties(P), map(CD,P)
						if (configs != null) {
							for (int i = 0; i < configs.length; i++) {
								map(cd, configs[i].getProperties());
							}
						}
					} else {
						// if Service
						map(cd, config.getProperties());
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
	 * @param cd
	 * @param properties
	 * @throws IOException
	 */
	public ComponentDescriptionProp map(ComponentDescription cd, Dictionary configAdminProps) {
		return doMap(cd,configAdminProps,cd.getFactory()!=null);
	}
	public ComponentDescriptionProp mapFactoryInstance(ComponentDescription cd, Dictionary configAdminProps) {
		return doMap(cd,configAdminProps,false);
	}
	private ComponentDescriptionProp doMap(ComponentDescription cd, Dictionary configAdminProps, boolean componentFactory) {

		// Create CD+P
		
		//calculate the CDP's properties
		Hashtable properties = initProperties(cd,configAdminProps);
		
		// for each Reference Description, create a reference object
		List references = new ArrayList();
		Iterator it = cd.getReferenceDescriptions().iterator();
		while(it.hasNext()) {
			ReferenceDescription referenceDesc = (ReferenceDescription)it.next();

			// create new Reference Object and add to CD+P:ref map
			Reference ref = new Reference(referenceDesc, properties);
			references.add(ref);
		
		} 
		references = !references.isEmpty() ? references : Collections.EMPTY_LIST;
				
		ComponentDescriptionProp cdp = new ComponentDescriptionProp(cd, references, properties,componentFactory);
		cd.addComponentDescriptionProp(cdp);

		// add CD+P to set
		enabledCDPs.add(cdp);
		
		return cdp;
	}
	
	/** 
	 * Initialize Properties for a CD+P 
	 * 
	 * The property elements provide default or supplemental property values 
	 * if not overridden by the properties retrieved from Configuration Admin.
	 * 
	 * The property and properties elements are processed in top to bottom order.
	 * This allows later elements to override property values defined by earlier 
	 * elements.  There can be many property and properties elements and they may be 
	 * interleaved.
	 * 
	 * @return Dictionary properties
	 */
	private Hashtable initProperties(ComponentDescription cd, Dictionary configAdminProps){
		
		Hashtable properties = new Hashtable();

		//0) add Reference target properties
		Iterator it = cd.getReferenceDescriptions().iterator();
		while(it.hasNext()) {
			ReferenceDescription referenceDesc = (ReferenceDescription)it.next();
			if (referenceDesc.getTarget() != null) {
				properties.put(
						referenceDesc.getName() + ".target",
						referenceDesc.getTarget()
						);
			}
		}
		
		//1) get properties from Service Component XML
		PropertyDescription[] propertyDescriptions = cd.getProperties();
		for (int i = 0; i < propertyDescriptions.length; i++) {
			if (propertyDescriptions[i] instanceof PropertyValueDescription) {
				PropertyValueDescription propvalue = (PropertyValueDescription) propertyDescriptions[i];
				properties.put(propvalue.getName(), propvalue.getValue());
			} else {
				//read from seperate properties file
				properties.putAll(loadPropertyFile(cd,((PropertyResourceDescription) propertyDescriptions[i]).getEntry()));
			}
		}

		//2) Add configAdmin properties
		if (configAdminProps != null) {
			Enumeration keys = configAdminProps.keys();
			while(keys.hasMoreElements()) {
				Object key = keys.nextElement();
				properties.put(key,configAdminProps.get(key));
			}
		}
		
		//add component.name and component.id (cannot be overridden)
		properties.put(ComponentConstants.COMPONENT_NAME,cd.getName());
		properties.put(ComponentConstants.COMPONENT_ID, new Long(getNextComponentId()));
		
		//add component.factory if it's a factory
		if (cd.getFactory() != null) {
			properties.put(ComponentConstants.COMPONENT_FACTORY, cd.getFactory());
		}
		
		//add ObjectClass so we can match target filters before actually being registered
		List servicesProvided = cd.getServicesProvided();
		if (!servicesProvided.isEmpty()) {
			properties.put(Constants.OBJECTCLASS, servicesProvided.toArray(new String [servicesProvided.size()]));
		}

		return properties;
	}
	
	/**
	 * Get the Service Component properties from a properties entry file 
	 * 
	 * @param propertyEntryName - the name of the properties file
	 */
	static private Hashtable loadPropertyFile(ComponentDescription cd, String propertyEntryName) {

		URL url = null;
		Properties props = new Properties();

		url = cd.getBundleContext().getBundle().getEntry(propertyEntryName);
		if (url == null) {
			throw new ComponentException("Properties entry file " + propertyEntryName + " cannot be found");
		}

		try {
			InputStream in = null;
			File file = new File(propertyEntryName);
		
			if (file.exists()) {
				//throws FileNotFoundException if it's not there or no read access
				in = new FileInputStream(file);
			} else {
				in = url.openStream();
			}
			if (in != null) {
				props.load(new BufferedInputStream(in));
				in.close();
			} else {
				//TODO log something?
				if (DEBUG)
					System.out.println("Unable to find properties file " + propertyEntryName);
			}
		} catch (IOException e) {
			throw new ComponentException("Properties entry file " + propertyEntryName + " cannot be read");
		}

		return props;

	}

	/**
	 * Disable list of ComponentDescriptions
	 * 
	 * get all CD+P's from CD:CD+P Map get instances from CD+P:list of instance
	 * (1:n) map
	 * 
	 * Strip out of Map all CD+P's Continue to pull string check each Ref
	 * dependency and continue to pull out CD+P's if they become not satisfied
	 * Then call Resolver to re-resolve
	 * 
	 * @param componentDescriptions
	 */
	public void disableComponents(List componentDescriptions) {

		// Received list of CDs to disable
		if (componentDescriptions != null) {
			Iterator it = componentDescriptions.iterator();
			while (it.hasNext()) {

				// get the CD
				ComponentDescription cd = (ComponentDescription) it.next();

				disposeInstances((List)((ArrayList)cd.getComponentDescriptionProps()).clone());
				
				cd.clearComponentDescriptionProps();
				
				enabledCDsByName.remove(cd.getName());
			}
			
		}
	}
	
	public void disposeInstances(List cdps) {
		//unregister, deactivate, and unbind
		satisfiedCDPs.removeAll(cdps);
		enabledCDPs.removeAll(cdps);
		instanceProcess.disposeInstances(cdps);
	}

	/**
	 * Get the Eligible Components
	 * 
	 * loop through CD+P list of enabled get references check if satisfied if
	 * true add to satisfied list send to Instance Process
	 * 
	 */
	public void getEligible(ServiceEvent event) {
		
		//if added CDPs
		if (event == null) {
			//we added a CDP, so check for circularity and mark
			//cycles
			resolveCycles();

			//get list of newly satisfied CDPs and build them
			List newlySatisfiedCDPs = resolveSatisfied();
			newlySatisfiedCDPs.removeAll(satisfiedCDPs);
			
			if (!newlySatisfiedCDPs.isEmpty()) {
				workQueue.enqueueWork(this, BUILD, newlySatisfiedCDPs);
			
				satisfiedCDPs.addAll(newlySatisfiedCDPs);
			}
			
		} 
		//if service registered
		else if (event.getType() == ServiceEvent.REGISTERED) {
			
			//dynamic bind
			Hashtable dynamicBind = selectDynamicBind(event.getServiceReference());
			if (!dynamicBind.isEmpty()) {
				workQueue.enqueueWork(this, DYNAMICBIND, dynamicBind);
			}
			
			//get list of newly satisfied CDPs and build them
			List newlySatisfiedCDPs = resolveSatisfied();
			newlySatisfiedCDPs.removeAll(satisfiedCDPs);
			if (!newlySatisfiedCDPs.isEmpty()) {
				workQueue.enqueueWork(this, BUILD, newlySatisfiedCDPs);
				
				satisfiedCDPs.addAll(newlySatisfiedCDPs);
			}

		}
		//if service modified
		else if (event.getType() == ServiceEvent.MODIFIED) {

			//check for newly unsatisfied components and synchronously dispose them
			List newlyUnsatisfiedCDPs = (List)((ArrayList)satisfiedCDPs).clone();
			newlyUnsatisfiedCDPs.removeAll(resolveSatisfied());
			if (!newlyUnsatisfiedCDPs.isEmpty()) {
				satisfiedCDPs.removeAll(newlyUnsatisfiedCDPs);
				
				instanceProcess.disposeInstances(newlyUnsatisfiedCDPs);
			}

			//dynamic unbind
			//check each satisfied cdp - do we need to unbind
			List dynamicUnBind = selectDynamicUnBind(event.getServiceReference());
			if (!dynamicUnBind.isEmpty()) {
				instanceProcess.dynamicUnBind(dynamicUnBind);
			}
			
			//dynamic bind
			Hashtable dynamicBind = selectDynamicBind(event.getServiceReference());
			if (!dynamicBind.isEmpty()) {
				workQueue.enqueueWork(this, DYNAMICBIND, dynamicBind);
			}
			
			//get list of newly satisfied CDPs and build them
			List newlySatisfiedCDPs = resolveSatisfied();
			newlySatisfiedCDPs.removeAll(satisfiedCDPs);
			if (!newlySatisfiedCDPs.isEmpty()) {
				workQueue.enqueueWork(this, BUILD, newlySatisfiedCDPs);
				
				satisfiedCDPs.addAll(newlySatisfiedCDPs);
			}

		}
		//if service unregistering
		else if (event.getType() == ServiceEvent.UNREGISTERING) {
			
			//check for newly unsatisfied components and synchronously dispose them
			List newlyUnsatisfiedCDPs = (List)((ArrayList)satisfiedCDPs).clone();
			newlyUnsatisfiedCDPs.removeAll(resolveSatisfied());
			if (!newlyUnsatisfiedCDPs.isEmpty()) {
				satisfiedCDPs.removeAll(newlyUnsatisfiedCDPs);

				instanceProcess.disposeInstances(newlyUnsatisfiedCDPs);
			}

			//dynamic unbind
			List dynamicUnBind = selectDynamicUnBind(event.getServiceReference());
			if (!dynamicUnBind.isEmpty()) {
				instanceProcess.dynamicUnBind(dynamicUnBind);
			}
			
		}
		
	}

	/**
	 * Check if a particular CDP is eligible.  Also checks for circularity.
	 * If cdp is eligible it is added to satisfiedCDPs list, but not sent to instance
	 * process
	 * @param cdp
	 * @return
	 */
	public boolean isEligible(ComponentDescriptionProp cdp) {

		//we added a CDP, so check for circularity and mark
		//cycles
		resolveCycles();

		//get list of newly satisfied CDPs and build them
		List newlySatisfiedCDPs = resolveSatisfied();
		newlySatisfiedCDPs.removeAll(satisfiedCDPs);
		
		if (!newlySatisfiedCDPs.contains(cdp)) {
			return false;
		}
		satisfiedCDPs.add(cdp);
		return true;
		
	}
	/**
	 * Return list of components which are satisfied
	 * @return
	 */
	public List resolveSatisfied() {
		List resolvedSatisfiedCDPs = new ArrayList();
		
		Iterator it = enabledCDPs.iterator();
		while (it.hasNext()) {
			ComponentDescriptionProp cdp = (ComponentDescriptionProp) it.next();
			ComponentDescription cd = cdp.getComponentDescription();

			// check if all the services needed by the CDP are available
			List refs = cdp.getReferences();
			Iterator iterator = refs.iterator();
			boolean hasProviders = true;
			while (iterator.hasNext()) {
				Reference reference = (Reference) iterator.next();
				if (reference != null) {
					if (reference.getReferenceDescription().isRequired() && 
						!reference.hasProvider(cdp.getComponentDescription().getBundleContext())) {
						hasProviders=false;
						break;
					}
				}
			}
			if (!hasProviders) 
				continue;

			//check if the bundle providing the service has permission to register the provided interface(s)
			//if a service is provided
			//TODO we can cache the ServicePermission objects
			if (cd.getService() != null && System.getSecurityManager() != null) {
				ProvideDescription[] provides = cd.getService().getProvides();
				Bundle bundle = cd.getBundleContext().getBundle();
				boolean hasPermission = true;
				for (int i=0;i<provides.length;i++) {
					//make sure bundle has permission to register the service
					if (!bundle.hasPermission(new ServicePermission(provides[i].getInterfacename(),ServicePermission.REGISTER))) {
						hasPermission=false;
						break;
					}
				}
				if (!hasPermission)
					continue;
			}
			
			//we have permission and providers - this CDP is satisfied
			resolvedSatisfiedCDPs.add(cdp);
		} //end while (more enabled CDPs)
		return resolvedSatisfiedCDPs.isEmpty() ? Collections.EMPTY_LIST : resolvedSatisfiedCDPs;
	}

	/**
	 * addService Listener - Listen for changes in the referenced services
	 * 
	 */
	public void addServiceListener() {
		main.context.addServiceListener(this);
	}

	/**
	 * removeServiceListener -
	 * 
	 */
	public void removeServiceListener() {
		main.context.removeServiceListener(this);
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

		// if ((reference.getProperty(ComponentConstants.COMPONENT_ID) == null)

		switch (eventType) {
			// The properties of a registered Service have been modified
			case ServiceEvent.MODIFIED :
				getEligible(event);
				break;
			case ServiceEvent.REGISTERED :
				getEligible(event);
				break;
			case ServiceEvent.UNREGISTERING :
				getEligible(event);
				break;
		}

	}

	/**
	 * @param workAction
	 * @param workObject
	 * @see org.eclipse.osgi.component.workqueue.WorkDispatcher#dispatchWork(int,
	 *      java.lang.Object)
	 */
	public void dispatchWork(int workAction, Object workObject) {
		Iterator it;
		switch (workAction) {
			case BUILD :
				//only build if cdps are still satisfied
				List queueCDPs = (List)workObject;
				List cdps = new ArrayList(queueCDPs.size());
				it = queueCDPs.iterator();
				while(it.hasNext()) {
					ComponentDescriptionProp cdp = (ComponentDescriptionProp)it.next();
					if (this.satisfiedCDPs.contains(cdp)) {
						cdps.add(cdp);
					}
				}
				if (!cdps.isEmpty()) {
					instanceProcess.buildInstances(cdps);
				}
				break;
			case DYNAMICBIND :
				//only dynamicBind if cdps are still satisfied
				Hashtable serviceTable = (Hashtable)workObject;
				it = serviceTable.values().iterator();
				while(it.hasNext()) {
					if(!this.satisfiedCDPs.contains(it.next())) {
						//modifies underlying hashtable
						it.remove();
					}
				}
				if (!serviceTable.isEmpty()) {
					instanceProcess.dynamicBind(serviceTable);
				}
				break;
		}
	}

	private Hashtable selectDynamicBind(ServiceReference serviceReference) {
		Hashtable bindTable = new Hashtable();
		Iterator it = satisfiedCDPs.iterator();
		while (it.hasNext()) {
			ComponentDescriptionProp cdp = (ComponentDescriptionProp) it.next();
			List references = cdp.getReferences();
			Iterator refIt = references.iterator();
			while (refIt.hasNext()) {
				Reference reference = (Reference) refIt.next();
				if (reference.dynamicBindReference(serviceReference)) {
					bindTable.put(reference, cdp);
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
	private List selectDynamicUnBind(ServiceReference serviceReference) {

		List unbindJobs = new ArrayList();

		Iterator it = satisfiedCDPs.iterator();
		while (it.hasNext()) {
			ComponentDescriptionProp cdp = (ComponentDescriptionProp) it.next();
			List references = cdp.getReferences();
			Iterator it_ = references.iterator();
			while (it_.hasNext()) {
				Reference reference = (Reference) it_.next();
				//Does the cdp require this service, use the Reference object to check
				if (reference.dynamicUnbindReference(serviceReference)) {
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

	static private class ReferenceCDP {
		public ComponentDescriptionProp consumer;
		public Reference ref;
		public ComponentDescriptionProp producer;
		protected ReferenceCDP(ComponentDescriptionProp consumer, Reference ref, ComponentDescriptionProp producer) {
			this.consumer = consumer;
			this.ref = ref;
			this.producer = producer;
		}
	}

	/**
	 * Check through the enabled list for cycles.  Cycles can only exist if
	 * every service is provided by a CDP (not legacy OSGi).  If the cycle has
	 * no optional dependencies, throw CircularityException.  If cycle can be "
	 * broken" by an optional dependency, make a note and return the optional dep.
	 * to be created.
	 * 
	 * @return List of cycle breaks
	 * @throws CircularityException if cycle exists with no optional dependencies
	 */
	private void resolveCycles(){

		try {
			//find the CDPs that resolve using other CDPs and record their 
			//dependencies
			Hashtable dependencies = new Hashtable();
			Iterator it = enabledCDPs.iterator();
			while (it.hasNext()) {
				ComponentDescriptionProp enabledCDP = (ComponentDescriptionProp)it.next();
				List dependencyList = new ArrayList();
				Iterator refIt = enabledCDP.getReferences().iterator();
				while (refIt.hasNext()) {
					Reference reference = (Reference)refIt.next();
					
					//see if it resolves to one of the other enabled CDPs
					ComponentDescriptionProp providerCDP = reference.findProviderCDP(enabledCDPs);
					if (providerCDP != null) {
						dependencyList.add(new ReferenceCDP(enabledCDP,reference,providerCDP));
					}
				} //end while(more references)
				
				if (!dependencyList.isEmpty()) {
					//CDP resolves using some other CDPs, could be a cycle
					dependencies.put(enabledCDP,dependencyList);
				} else {
					dependencies.put(enabledCDP, Collections.EMPTY_LIST);
				}
			} //end while (more enabled CDPs)
			
			Set visited = new HashSet();
			it = dependencies.keySet().iterator();
			while (it.hasNext()) {
				ComponentDescriptionProp cdp = (ComponentDescriptionProp) it.next();
				if (!visited.contains(cdp)) {
					List currentStack = new ArrayList();
					traverseDependencies(cdp, visited, dependencies, currentStack);
				}
			}
		} catch (CircularityException e) {
			//log the error
			Log.log(LogService.LOG_ERROR, "[SCR] Circularity Exception.", e);
			
			//disable offending CDP
			enabledCDPs.remove(e.getCircularDependency());
			
			//try again
			resolveCycles();
		}
	}

	private void traverseDependencies(ComponentDescriptionProp cdp, Set visited, Hashtable dependencies, List currentStack) throws CircularityException {

		//the component has already been visited and it's dependencies checked
		//for cycles
		if (visited.contains(cdp)) {
			return;
		}

		List refCDPs = (List)dependencies.get(cdp);
		Iterator it = refCDPs.iterator();
		//first, add the CDP's dependencies
		while (it.hasNext()) {

			ReferenceCDP refCDP = (ReferenceCDP) it.next();

			if (currentStack.contains(refCDP)) {
				//may throw circularity exception
				handleDependencyCycle(refCDP, currentStack);
				return;
			}
			currentStack.add(refCDP);

			traverseDependencies(refCDP.producer, visited, dependencies, currentStack);

			currentStack.remove(refCDP);
		}
		//finally write the cdp
		visited.add(cdp);

	}

	/*
	 * A cycle was detected.  CDP is referenced by the last element in currentStack.
	 * returns CircularityException if the cycle does not contain an optional dependency.
	 * else choses a starting point at which to initialize the cycle (the starting point
	 * must be immediately after an optional dependency).
	 */
	private void handleDependencyCycle(ReferenceCDP refCDP, List currentStack) throws CircularityException {
		ListIterator cycleIterator = currentStack.listIterator(currentStack.indexOf(refCDP));

		//find an optional dependency
		ReferenceCDP optionalRefCDP = null;
		while (cycleIterator.hasNext()) {
			ReferenceCDP cycleRefCDP = (ReferenceCDP) cycleIterator.next();
			if (!cycleRefCDP.ref.getReferenceDescription().isRequired()) {
				optionalRefCDP = cycleRefCDP;
				break;
			}
		}

		if (optionalRefCDP == null) {
			//no optional dependency
			throw new CircularityException(refCDP.consumer);
		}

		//add note not to initiate activation of next dependency
		optionalRefCDP.consumer.setDelayActivateCDPName(optionalRefCDP.producer.getComponentDescription().getName());
	}
	
	/**
	 * Method to return the next available component id. 
	 * 
	 * @return next component id.
	 */
	private long getNextComponentId() {
		synchronized (this){
			long id = componentid;
			componentid++;
			return (id);
		}
	}

}
