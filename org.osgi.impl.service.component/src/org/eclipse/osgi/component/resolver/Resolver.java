/*
 * $Header$
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
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentConstants;
import org.osgi.service.component.ComponentException;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

/**
 * Resolver - resolves the Service Components.  This includes creating Component 
 * Configurations, resolving the required referenced services, and checking for 
 * circular dependencies.
 * 
 * The Resolver implements AllServiceListener so it can be informed about service
 * changes in the framework.
 * 
 * @version $Revision$
 */
public class Resolver implements AllServiceListener, WorkDispatcher {

	/* set this to true to compile in debug messages */
	static final boolean			DEBUG					= false;

	/** 
	 * next free component id.
	 * See OSGi R4 Specification section 112.6 "Component Properties"
	 */
	static protected long			componentid;

	/* ServiceTracker for configurationAdmin */
	public ServiceTracker			configAdminTracker;

	/**
	 * Service Component instances need to be built.
	 */
	public static final int			BUILD					= 1;

	/**
	 * Service Component instances to bind dynamically
	 */
	public static final int			DYNAMICBIND				= 3;

	/**
	 * Main class for the SCR
	 */
	public Main						main;

	public InstanceProcess			instanceProcess;

	/**
	 * List of {@link ComponentDescriptionProp}s - the currently "enabled" 
	 * Component Configurations.
	 */
	public List						enabledCDPs; 
	
	/**
	 * List of {@link ComponentDescriptionProp}s - the currently "satisfied" 
	 * Component Configurations.  Note that to be satisfied a Component 
	 * Configuration must first be enabled, so this list is a subset of 
	 * {@link Resolver#enabledCDPs enabledCDPs}.
	 */
	public List						satisfiedCDPs;
	
	/**
	 * A map of name:Service Component (String):({@link ComponentDescription})
	 */
	public Map						enabledCDsByName;

	private WorkQueue				workQueue;

	/**
	 * Resolver constructor
	 * 
	 * @param main Main class of SCR
	 */
	public Resolver(Main main) {
		this.main = main;

		componentid = 1;

		// for now use Main's workqueue
		workQueue = main.workQueue;
		
		enabledCDPs = new ArrayList();
		satisfiedCDPs = new ArrayList();
		enabledCDsByName = new HashMap();

		configAdminTracker = new ServiceTracker(main.context,
				ConfigurationAdmin.class.getName(), null);
		configAdminTracker.open();

		instanceProcess = new InstanceProcess(main);

		//start listening to ServiceChanged events
		main.context.addServiceListener(this);

	}

	/**
	 * Clean up the SCR is shutting down
	 */
	public void dispose() {

		//stop listening to ServiceChanged events
		main.context.removeServiceListener(this);

		instanceProcess.dispose();
		instanceProcess = null;
		
		configAdminTracker.close();
		configAdminTracker = null;

		enabledCDPs = null;
		satisfiedCDPs = null;
		enabledCDsByName = null;

	}

	/**
	 * Enable Service Components - create Component Configuration(s) for the 
	 * Service Components and try to satisfy their dependencies.
	 * 
	 * <p>
	 * For each Service Component ({@link ComponentDescription}) check 
	 * ConfigurationAdmin for properties and create a Component Configuration 
	 * ({@link ComponentDescriptionProp}).
	 * </p>
	 * 
	 * <p>
	 * If a {@link org.osgi.service.cm.ManagedServiceFactory ManagedServiceFactory}
	 * is registered for the Service Component, we may create multiple Component
	 * Configurations.
	 * </p>
	 * 
	 * <p>
	 * After the Component Configuration(s) are created, call 
	 * {@link Resolver#resolve(ServiceEvent) getEligible(null)} to try to
	 * satisfy them.
	 * </p>
	 * 
	 * @param componentDescriptions - a List of {@link ComponentDescription}s to 
	 *        be enabled 
	 */
	public void enableComponents(List componentDescriptions)
			throws ComponentException {

		if (componentDescriptions != null) {
			Iterator it = componentDescriptions.iterator();
			while (it.hasNext()) {
				ComponentDescription cd = (ComponentDescription) it.next();

				// add to our enabled lookup list
				enabledCDsByName.put(cd.getName(), cd);

				// check for a Configuration properties for this component
				Configuration config = null;
				try {
					ConfigurationAdmin configurationAdmin = (ConfigurationAdmin) configAdminTracker
							.getService();
					if (configurationAdmin != null) {
						config = configurationAdmin.getConfiguration(cd
								.getName(), cd.getBundleContext().getBundle()
								.getLocation());
					}
				}
				catch (IOException e) {
					// Log it and continue
					Log
							.log(
									1,
									"[SCR] IOException when getting Configuration Properties. ",
									e);
				}

				// if no Configuration
				if (config == null) {
					// create ComponentDescriptionProp
					map(cd, null);

				}
				else {

					// if ManagedServiceFactory
					if (config.getFactoryPid() != null) {

						// if ComponentFactory is specified
						if (cd.getFactory() != null) {
							throw new ComponentException(
									"incompatible to specify both ComponentFactory and ManagedServiceFactory are incompatible");
						}

						Configuration[] configs = null;
						try {
							ConfigurationAdmin cm = (ConfigurationAdmin) configAdminTracker
									.getService();
							configs = cm
									.listConfigurations("(service.factoryPid="
											+ config.getFactoryPid() + ")");
						}
						catch (InvalidSyntaxException e) {
							Log
									.log(
											1,
											"[SCR] InvalidSyntaxException when getting CM Configurations. ",
											e);
						}
						catch (IOException e) {
							Log
									.log(
											1,
											"[SCR] IOException when getting CM Configurations. ",
											e);
						}

						// for each MSF set of properties(P), map(CD,P)
						if (configs != null) {
							for (int i = 0; i < configs.length; i++) {
								map(cd, configs[i].getProperties());
							}
						}
					}
					else {
						// if Service
						map(cd, config.getProperties());
					}
				}
			}
		}
		// resolve
		resolve(null);
	}

	/**
	 * Combine ConfigAdmin properties with a Service Component 
	 * ({@link ComponentDescription}) to create a Component Configuration 
	 * ({@link ComponentConfiguration}), and add it to our list of enabled 
	 * Component Configurations ({@link Resolver#enabledCDPs}).
	 * 
	 * The ConfigAdmin properties are combined with the properties from the 
	 * Service Component's XML.
	 * 
	 * @param cd Service Component
	 * @param configAdminProps ConfigAdmin properties for this Component
	 *        Configuration 
	 */
	public ComponentDescriptionProp map(ComponentDescription cd,
			Dictionary configAdminProps) {
		return doMap(cd, configAdminProps, cd.getFactory() != null);
	}

	/**
	 * Create a Component Configuration of a Service Component that has the
	 * "factory" attribute.  
	 * 
	 * @see Resolver#map(ComponentDescription, Dictionary)
	 * @see ComponentDescriptionProp#componentFactory
	 */
	public ComponentDescriptionProp mapFactoryInstance(ComponentDescription cd,
			Dictionary configAdminProps) {
		return doMap(cd, configAdminProps, false);
	}

	private ComponentDescriptionProp doMap(ComponentDescription cd,
			Dictionary configAdminProps, boolean componentFactory) {

		// Create CD+P

		// calculate the CDP's properties
		Hashtable properties = initProperties(cd, configAdminProps);

		// for each Reference Description, create a reference object
		List references = new ArrayList();
		Iterator it = cd.getReferenceDescriptions().iterator();
		while (it.hasNext()) {
			ReferenceDescription referenceDesc = (ReferenceDescription) it
					.next();

			// create new Reference Object
			Reference ref = new Reference(referenceDesc, properties);
			references.add(ref);

		}
		references = !references.isEmpty() ? references
				: Collections.EMPTY_LIST;

		ComponentDescriptionProp cdp = new ComponentDescriptionProp(cd,
				references, properties, componentFactory);
		
		//for each Reference, set it's "parent" (the CDP)
		it = cdp.getReferences().iterator();
		while (it.hasNext()) {
			Reference reference = (Reference) it.next();

			// set parent CDP
			reference.setComponentDescriptionProp(cdp);
		}
		
		cd.addComponentDescriptionProp(cdp);

		// add CD+P to set
		enabledCDPs.add(cdp);

		return cdp;
	}

	/**
	 * Initialize Properties for a CD+P
	 * 
	 * The property elements provide default or supplemental property values if
	 * not overridden by the properties retrieved from Configuration Admin.
	 * 
	 * The property and properties elements are processed in top to bottom
	 * order. This allows later elements to override property values defined by
	 * earlier elements. There can be many property and properties elements and
	 * they may be interleaved.
	 * 
	 * @return Dictionary properties
	 */
	private Hashtable initProperties(ComponentDescription cd,
			Dictionary configAdminProps) {

		Hashtable properties = new Hashtable();

		// 0) add Reference target properties
		Iterator it = cd.getReferenceDescriptions().iterator();
		while (it.hasNext()) {
			ReferenceDescription referenceDesc = (ReferenceDescription) it
					.next();
			if (referenceDesc.getTarget() != null) {
				properties.put(referenceDesc.getName() + ".target",
						referenceDesc.getTarget());
			}
		}

		// 1) get properties from Service Component XML, in parse order
		PropertyDescription[] propertyDescriptions = cd.getProperties();
		for (int i = 0; i < propertyDescriptions.length; i++) {
			if (propertyDescriptions[i] instanceof PropertyValueDescription) {
				PropertyValueDescription propvalue = (PropertyValueDescription) propertyDescriptions[i];
				properties.put(propvalue.getName(), propvalue.getValue());
			}
			else {
				// read from seperate properties file
				properties.putAll(loadPropertyFile(cd,
						((PropertyResourceDescription) propertyDescriptions[i])
								.getEntry()));
			}
		}

		// 2) Add configAdmin properties
		if (configAdminProps != null) {
			Enumeration keys = configAdminProps.keys();
			while (keys.hasMoreElements()) {
				Object key = keys.nextElement();
				properties.put(key, configAdminProps.get(key));
			}
		}

		// add component.name and component.id (cannot be overridden)
		properties.put(ComponentConstants.COMPONENT_NAME, cd.getName());
		properties.put(ComponentConstants.COMPONENT_ID, new Long(
				getNextComponentId()));

		// add component.factory if it's a factory
		if (cd.getFactory() != null) {
			properties.put(ComponentConstants.COMPONENT_FACTORY, cd
					.getFactory());
		}

		// add ObjectClass so we can match target filters before actually being
		// registered
		List servicesProvided = cd.getServicesProvided();
		if (!servicesProvided.isEmpty()) {
			properties.put(Constants.OBJECTCLASS, servicesProvided
					.toArray(new String[servicesProvided.size()]));
		}

		return properties;
	}

	/**
	 * Get the Service Component properties from a properties entry file
	 * 
	 * @param propertyEntryName - the name of the properties file
	 */
	static private Hashtable loadPropertyFile(ComponentDescription cd,
			String propertyEntryName) {

		URL url = null;
		Properties props = new Properties();

		url = cd.getBundleContext().getBundle().getEntry(propertyEntryName);
		if (url == null) {
			throw new ComponentException("Properties entry file "
					+ propertyEntryName + " cannot be found");
		}

		try {
			InputStream in = null;
			File file = new File(propertyEntryName);

			if (file.exists()) {
				// throws FileNotFoundException if it's not there or no read
				// access
				in = new FileInputStream(file);
			}
			else {
				in = url.openStream();
			}
			if (in != null) {
				props.load(new BufferedInputStream(in));
				in.close();
			}
			else {
				// TODO log something?
				if (DEBUG)
					System.out.println("Unable to find properties file "
							+ propertyEntryName);
			}
		}
		catch (IOException e) {
			throw new ComponentException("Properties entry file "
					+ propertyEntryName + " cannot be read");
		}

		return props;

	}

	/**
	 * Disable Service Components.
	 * 
	 * For each Service Component ({@link ComponentDescription}),
	 * dispose of all of it's Component Configurations 
	 * ({@link ComponentDescriptionProp}s).
	 * 
	 * @see Resolver#disposeComponentConfigs(List)
	 * 
	 * @param componentDescriptions List of {@link ComponentDescriptionProp}s to
	 *        disable
	 */
	public void disableComponents(List componentDescriptions) {

		// Received list of CDs to disable
		if (componentDescriptions != null) {
			Iterator it = componentDescriptions.iterator();
			while (it.hasNext()) {

				// get the CD
				ComponentDescription cd = (ComponentDescription) it.next();

				disposeComponentConfigs((List) ((ArrayList) cd
						.getComponentDescriptionProps()).clone());

				cd.clearComponentDescriptionProps();

				enabledCDsByName.remove(cd.getName());
			}

		}
	}

	/**
	 * Dispose of Component Configurations ({@link ComponentDescriptionProp}s).
	 * 
	 * Remove Component Configurations from satisfied and enabled lists, and send
	 * to InstanceProcess to be unregistered, deactivated, and unbound.
	 * 
	 * @see InstanceProcess#disposeComponentConfigs(List)
	 * 
	 * @param cdps List of {@link ComponentDescriptionProp}s
	 */
	public void disposeComponentConfigs(List cdps) {
		// unregister, deactivate, and unbind
		satisfiedCDPs.removeAll(cdps);
		enabledCDPs.removeAll(cdps);
		instanceProcess.disposeComponentConfigs(cdps);
	}

	/**
	 * Process a service change
	 * <p>
	 * A change has happened in the OSGi service environment, or new
	 * Component Configurations have been added to the system.
	 * </p>
	 * Depending on the change, take the following actions:
	 * <p>
	 * If new Component Configurations were added (param event is null):
	 *  <ol>
	 *     <li>Check for circularity and mark cycles</li>
	 *     <li>Send newly satisfied Component Configurations to Instance 
	 *     process</li>
	 *  </ol>
	 *  </p>
	 *  <p>
	 * If a service was registered:
	 * <ol>
	 *    <li>Put "Dynamic Bind" events on the queue for any Component 
	 *    Configurations which should be bound to the new service</li>
	 *    <li>Send newly satisfied Component Configurations to Instance 
	 *    process</li>
	 * </ol>
	 * </p>
	 * <p>
	 * If a service was modified:
	 * <ol>
	 *    <li>Synchronously dispose of all Component Configurations that 
	 *    become unsatisfied</li>
	 *    <li>Put "Dynamic Unbind Bind" events on the queue for any remaining 
	 *    Component Configurations which should be unbound from the service</li>
	 *    <li>Put "Dynamic Bind" events on the queue for any Component 
	 *    Configurations which should be bound to the modified service</li>
	 *    <li>Send newly satisfied Component Configurations to Instance 
	 *    process</li>
	 * </ol>
	 * </p>
	 * <p>
	 * If a service was unregistered:
	 * <ol>
	 *    <li>Synchronously dispose of all Component Configurations that 
	 *    become unsatisfied</li>
	 *    <li>Put "Dynamic Unbind Bind" events on the queue for any remaining 
	 *    Component Configurations which should be unbound from the service</li>
	 * </ol>
	 * </p>
	 * 
	 * @param event the service event or null if new cdps were added to the enabled list
	 */
	public void resolve(ServiceEvent event) {

		// if added CDPs
		if (event == null) {
			// we added a CDP, so check for circularity and mark
			// cycles
			resolveCycles();

			// get list of newly satisfied CDPs and build them
			List newlySatisfiedCDPs = resolveSatisfied();
			newlySatisfiedCDPs.removeAll(satisfiedCDPs);

			if (!newlySatisfiedCDPs.isEmpty()) {
				workQueue.enqueueWork(this, BUILD, newlySatisfiedCDPs);

				satisfiedCDPs.addAll(newlySatisfiedCDPs);
			}

		}
		// if service registered
		else if (event.getType() == ServiceEvent.REGISTERED) {

			// dynamic bind
			List dynamicBind = selectDynamicBind(event
					.getServiceReference());
			if (!dynamicBind.isEmpty()) {
				workQueue.enqueueWork(this, DYNAMICBIND, dynamicBind);
			}

			// get list of newly satisfied CDPs and build them
			List newlySatisfiedCDPs = resolveSatisfied();
			newlySatisfiedCDPs.removeAll(satisfiedCDPs);
			if (!newlySatisfiedCDPs.isEmpty()) {
				workQueue.enqueueWork(this, BUILD, newlySatisfiedCDPs);

				satisfiedCDPs.addAll(newlySatisfiedCDPs);
			}

		}
		// if service modified
		else if (event.getType() == ServiceEvent.MODIFIED) {

			// check for newly unsatisfied components and synchronously
			// dispose them
			List newlyUnsatisfiedCDPs = (List) ((ArrayList) satisfiedCDPs)
					.clone();
			newlyUnsatisfiedCDPs.removeAll(resolveSatisfied());
			if (!newlyUnsatisfiedCDPs.isEmpty()) {
				satisfiedCDPs.removeAll(newlyUnsatisfiedCDPs);

				instanceProcess.disposeComponentConfigs(newlyUnsatisfiedCDPs);
			}

			// dynamic unbind
			// check each satisfied cdp - do we need to unbind
			Map dynamicUnBind = selectDynamicUnBind(event
					.getServiceReference());
			if (!dynamicUnBind.isEmpty()) {
				instanceProcess.dynamicUnBind(dynamicUnBind);
			}

			// dynamic bind
			List dynamicBind = selectDynamicBind(event
					.getServiceReference());
			if (!dynamicBind.isEmpty()) {
				workQueue.enqueueWork(this, DYNAMICBIND, dynamicBind);
			}

			// get list of newly satisfied CDPs and build them
			List newlySatisfiedCDPs = resolveSatisfied();
			newlySatisfiedCDPs.removeAll(satisfiedCDPs);
			if (!newlySatisfiedCDPs.isEmpty()) {
				workQueue.enqueueWork(this, BUILD, newlySatisfiedCDPs);

				satisfiedCDPs.addAll(newlySatisfiedCDPs);
			}

		}
		// if service unregistering
		else if (event.getType() == ServiceEvent.UNREGISTERING) {

			// check for newly unsatisfied components and
			// synchronously dispose them
			List newlyUnsatisfiedCDPs = (List) ((ArrayList) satisfiedCDPs)
					.clone();
			newlyUnsatisfiedCDPs.removeAll(resolveSatisfied());
			if (!newlyUnsatisfiedCDPs.isEmpty()) {
				satisfiedCDPs.removeAll(newlyUnsatisfiedCDPs);

				instanceProcess
						.disposeComponentConfigs(newlyUnsatisfiedCDPs);
			}

			// dynamic unbind
			Map dynamicUnBind = selectDynamicUnBind(event
					.getServiceReference());
			if (!dynamicUnBind.isEmpty()) {
				instanceProcess.dynamicUnBind(dynamicUnBind);
			}

		}

	}

	/**
	 * Check if a particular CDP is satisfied. Also checks for circularity. If
	 * cdp is satisfied it is added to satisfiedCDPs list, but not sent to
	 * instance process
	 * 
	 * @param cdp
	 * @return
	 */
	public boolean justResolve(ComponentDescriptionProp cdp) {

		// we added a CDP, so check for circularity and mark
		// cycles
		resolveCycles();

		// get list of newly satisfied CDPs and build them
		List newlySatisfiedCDPs = resolveSatisfied();
		newlySatisfiedCDPs.removeAll(satisfiedCDPs);

		if (!newlySatisfiedCDPs.contains(cdp)) {
			return false;
		}
		satisfiedCDPs.add(cdp);
		return true;

	}

	/**
	 * Calculate which of the currently enabled Component Configurations 
	 * ({@link Resolver#enabledCDPs}) are "satisfied".  
	 * 
	 * <p>
	 * An "enabled" Component 
	 * Configuration is "satisfied" if there is at least one OSGi Service
	 * registered that has the correct interface and matches the target filter 
	 * for each of it's required (cardinality = "1..1" or "1..n") references.
	 * </p>
	 * <p>
	 * If a Component Configuration will register a service and security is 
	 * enabled, check if the bundle it comes from has 
	 * {@link ServicePermission#REGISTER} for that service.  If the Component
	 * Configuration does not have the necessary permission it is not "satisfied".
	 * </p>
	 * @return List of {@link ComponentDescriptionProp}s that are "satisfied"
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
					if (reference.getReferenceDescription().isRequired()
							&& !reference.hasProvider(cdp
									.getComponentDescription()
									.getBundleContext())) {
						hasProviders = false;
						break;
					}
				}
			}
			if (!hasProviders)
				continue;

			// check if the bundle providing the service has permission to
			// register the provided interface(s)
			// if a service is provided
			// TODO we can cache the ServicePermission objects
			if (cd.getService() != null && System.getSecurityManager() != null) {
				ProvideDescription[] provides = cd.getService().getProvides();
				Bundle bundle = cd.getBundleContext().getBundle();
				boolean hasPermission = true;
				for (int i = 0; i < provides.length; i++) {
					// make sure bundle has permission to register the service
					if (!bundle.hasPermission(new ServicePermission(provides[i]
							.getInterfacename(), ServicePermission.REGISTER))) {
						hasPermission = false;
						break;
					}
				}
				if (!hasPermission)
					continue;
			}

			// we have providers and permission - this CDP is satisfied
			resolvedSatisfiedCDPs.add(cdp);
		} // end while (more enabled CDPs)
		return resolvedSatisfiedCDPs.isEmpty() ? Collections.EMPTY_LIST
				: resolvedSatisfiedCDPs;
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

		// if ((reference.getProperty(ComponentConstants.COMPONENT_ID) == null)

		switch (eventType) {
			case ServiceEvent.MODIFIED :
			case ServiceEvent.REGISTERED :
			case ServiceEvent.UNREGISTERING :

				resolve(event);
				break;
		}

	}

	/**
	 * Called asynchronously by the work queue thread to perform work.
	 * <p>
	 * There are two possible work actions:
	 * <ul>
	 *    <li>BUILD - workObject is a list of Component Configurations to be
	 *    sent to the Instance process.  The Component Configurations have become
	 *    satisfied.  Check that the Component Configurations are still satisfied 
	 *    (system state may have changed while they were waiting on the work 
	 *    queue) and send them to the instance process 
	 *    ({@link InstanceProcess#registerComponentConfigs(List)}).
	 *    </li>
	 *    <li>DYNAMICBIND - workObject is a List of References that need to be 
	 *    dynamically bound.  Check that the Component Configurations are still 
	 *    satisfied (system state may have changed while they were waiting on 
	 *    the work queue) and send them to the instance process 
	 *    ({@link InstanceProcess#dynamicBind(List)}).
	 * </ul>
	 * </p>
	 * @param workAction {@link Resolver#BUILD} or {@link Resolver#DYNAMICBIND}
	 * @param workObject a List of {@link ComponentDescriptionProp}s if workAction
	 *        is {@link Resolver#BUILD} or a List of {@link Reference}s if workAction 
	 *        is {@link Resolver#DYNAMICBIND} 
	 * @see org.eclipse.osgi.component.workqueue.WorkDispatcher#dispatchWork(int,
	 *      java.lang.Object)
	 */
	public void dispatchWork(int workAction, Object workObject) {
		Iterator it;
		switch (workAction) {
			case BUILD :
				// only build if cdps are still satisfied
				List queueCDPs = (List) workObject;
				List cdps = new ArrayList(queueCDPs.size());
				it = queueCDPs.iterator();
				while (it.hasNext()) {
					ComponentDescriptionProp cdp = (ComponentDescriptionProp) it
							.next();
					if (this.satisfiedCDPs.contains(cdp)) {
						cdps.add(cdp);
					}
				}
				if (!cdps.isEmpty()) {
					instanceProcess.registerComponentConfigs(cdps);
				}
				break;
			case DYNAMICBIND :
				// only dynamicBind if cdps are still satisfied
				List references = (List) workObject;
				it = references.iterator();
				while (it.hasNext()) {
					if (!this.satisfiedCDPs.contains(((Reference) it.next())
							.getComponentDescriptionProp())) {
						// modifies underlying list
						it.remove();
					}
				}
				if (!references.isEmpty()) {
					instanceProcess.dynamicBind(references);
				}
				break;
		}
	}

	/**
	 * Calculate which of the currently satisfied CDPs 
	 * ({@link Resolver#satisfiedCDPs}) need to be dynamically bound to an OSGi
	 * service.
	 * 
	 * @param serviceReference the service
	 * @return a List of {@link Reference}s that need to be dynamically bound 
	 *         to this service
	 */
	private List selectDynamicBind(ServiceReference serviceReference) {
		List bindList = new ArrayList();
		Iterator it = satisfiedCDPs.iterator();
		while (it.hasNext()) {
			ComponentDescriptionProp cdp = (ComponentDescriptionProp) it.next();
			List references = cdp.getReferences();
			Iterator refIt = references.iterator();
			while (refIt.hasNext()) {
				Reference reference = (Reference) refIt.next();
				if (reference.dynamicBindReference(serviceReference)) {
					bindList.add(reference);
				}
			}
		}
		return bindList;
	}

	/**
	 * An OSGi service is unregistering, calculate which of the satisfied 
	 * Component Configurations need to dynamically unbind from it.
	 * <p>
	 *  A Component Configuration needs to dynamically unbind from a service
	 *  if it was bound to the service and the reference it was policy="dynamic".
	 *  </p>
	 * @param serviceReference
	 * @return a Map of {@link Reference}:{@link ServiceReference} to unbind
	 */
	private Map selectDynamicUnBind(ServiceReference serviceReference) {

		Map unbindJobs = new Hashtable();

		Iterator it = satisfiedCDPs.iterator();
		while (it.hasNext()) {
			ComponentDescriptionProp cdp = (ComponentDescriptionProp) it.next();
			List references = cdp.getReferences();
			Iterator it_ = references.iterator();
			while (it_.hasNext()) {
				Reference reference = (Reference) it_.next();
				// Is reference dynamic and bound to this service? - must unbind
				if (reference.dynamicUnbindReference(serviceReference)) {
					unbindJobs.put(reference,serviceReference);
				}
			}
		}
		return unbindJobs.isEmpty() ? Collections.EMPTY_MAP : unbindJobs;
	}

	/**
	 * Doubly-linked node used to traverse the dependency tree in order to
	 * find cycles.
	 *  
	 * @version $Revision$
	 */
	static private class ReferenceCDP {
		public Reference				ref;
		public ComponentDescriptionProp	producer;

		protected ReferenceCDP(Reference ref, ComponentDescriptionProp producer) {
			this.ref = ref;
			this.producer = producer;
		}
	}

	/**
	 * Check through the enabled list for cycles. Cycles can only exist if every
	 * service is provided by a Service Component (not legacy OSGi). If the cycle 
	 * has no optional dependencies, log an error and disable a Component 
	 * Configuration in the cycle. If cycle can be "broken" by an optional 
	 * dependency, make a note (stored in the 
	 * {@link ComponentDescriptionProp#delayActivateCDPNames} List).
	 * 
	 * @throws CircularityException if cycle exists with no optional
	 *         dependencies
	 */
	private void resolveCycles() {

		try {
			// find the CDPs that resolve using other CDPs and record their
			// dependencies
			Hashtable dependencies = new Hashtable();
			Iterator it = enabledCDPs.iterator();
			while (it.hasNext()) {
				ComponentDescriptionProp enabledCDP = (ComponentDescriptionProp) it
						.next();
				List dependencyList = new ArrayList();
				Iterator refIt = enabledCDP.getReferences().iterator();
				while (refIt.hasNext()) {
					Reference reference = (Reference) refIt.next();

					// see if it resolves to one of the other enabled CDPs
					ComponentDescriptionProp providerCDP = reference
							.findProviderCDP(enabledCDPs);
					if (providerCDP != null) {
						dependencyList.add(new ReferenceCDP(reference,
								providerCDP));
					}
				} // end while(more references)

				if (!dependencyList.isEmpty()) {
					// CDP resolves using some other CDPs, could be a cycle
					dependencies.put(enabledCDP, dependencyList);
				}
				else {
					dependencies.put(enabledCDP, Collections.EMPTY_LIST);
				}
			} // end while (more enabled CDPs)

			//traverse dependency tree and look for cycles
			Set visited = new HashSet();
			it = dependencies.keySet().iterator();
			while (it.hasNext()) {
				ComponentDescriptionProp cdp = (ComponentDescriptionProp) it
						.next();
				if (!visited.contains(cdp)) {
					List currentStack = new ArrayList();
					traverseDependencies(cdp, visited, dependencies,
							currentStack);
				}
			}
		}
		catch (CircularityException e) {
			// log the error
			Log.log(LogService.LOG_ERROR, "[SCR] Circularity Exception.", e);

			// disable offending CDP
			enabledCDPs.remove(e.getCircularDependency());

			// try again
			resolveCycles();
		}
	}

	/**
	 * Recursively do a depth-first traversal of a dependency tree, looking for 
	 * cycles.
	 * <p>
	 * If a cycle is found, calls 
	 * {@link Resolver#handleDependencyCycle(ReferenceCDP, List)}.
	 * </p>
	 * 
	 * @param cdp current node in dependency tree
	 * @param visited Set of {@link ComponentDescriptionProp} that are visited 
	 *        nodes
	 * @param dependencies Dependency tree - a Hashtable of 
	 * ({@link ComponentDescriptionProp}):(List of {@link ReferenceCDP}s)
	 * @param currentStack List of {@link ReferenceCDP}s - the history of our
	 *        traversal so far (the path back to the root of the tree)
	 * @throws CircularityException if an cycle with no optional dependencies is
	 * found.
	 */
	private void traverseDependencies(ComponentDescriptionProp cdp,
			Set visited, Hashtable dependencies, List currentStack)
			throws CircularityException {

		// the component has already been visited and it's dependencies checked
		// for cycles
		if (visited.contains(cdp)) {
			return;
		}

		List refCDPs = (List) dependencies.get(cdp);
		Iterator it = refCDPs.iterator();
		// first, add the CDP's dependencies
		while (it.hasNext()) {

			ReferenceCDP refCDP = (ReferenceCDP) it.next();

			if (currentStack.contains(refCDP)) {
				// may throw circularity exception
				handleDependencyCycle(refCDP, currentStack);
				return;
			}
			currentStack.add(refCDP);

			traverseDependencies(refCDP.producer, visited, dependencies,
					currentStack);

			currentStack.remove(refCDP);
		}
		// finally write the cdp
		visited.add(cdp);

	}

	/**
	 * A cycle was detected. CDP is referenced by the last element in
	 * currentStack. Throws CircularityException if the cycle does not contain
	 * an optional dependency, else choses a point at which to
	 * "break" the cycle (the break point must be immediately after an
	 * optional dependency) and adds a "cycle note".
	 * 
	 * @see ComponentDescriptionProp#delayActivateCDPNames
	 */
	private void handleDependencyCycle(ReferenceCDP refCDP, List currentStack)
			throws CircularityException {
		ListIterator cycleIterator = currentStack.listIterator(currentStack
				.indexOf(refCDP));

		// find an optional dependency
		ReferenceCDP optionalRefCDP = null;
		while (cycleIterator.hasNext()) {
			ReferenceCDP cycleRefCDP = (ReferenceCDP) cycleIterator.next();
			if (!cycleRefCDP.ref.getReferenceDescription().isRequired()) {
				optionalRefCDP = cycleRefCDP;
				break;
			}
		}

		if (optionalRefCDP == null) {
			// no optional dependency
			throw new CircularityException(refCDP.ref
					.getComponentDescriptionProp());
		}

		// add note not to initiate activation of next dependency
		optionalRefCDP.ref.getComponentDescriptionProp()
				.setDelayActivateCDPName(
						optionalRefCDP.producer.getComponentDescription()
								.getName());
	}

	/**
	 * Method to return the next available component id.
	 * 
	 * @return next component id.
	 */
	private long getNextComponentId() {
		synchronized (this) {
			return componentid++;
		}
	}

}
