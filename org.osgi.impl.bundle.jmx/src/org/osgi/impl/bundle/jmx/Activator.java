/*
 * Copyright 2008 Oracle Corporation
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.osgi.impl.bundle.jmx;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import javax.management.StandardMBean;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.impl.bundle.jmx.cm.ConfigAdminManager;
import org.osgi.impl.bundle.jmx.framework.BundleState;
import org.osgi.impl.bundle.jmx.framework.Framework;
import org.osgi.impl.bundle.jmx.framework.PackageState;
import org.osgi.impl.bundle.jmx.framework.ServiceState;
import org.osgi.impl.bundle.jmx.permissionadmin.PermissionManager;
import org.osgi.impl.bundle.jmx.provisioning.Provisioning;
import org.osgi.impl.bundle.jmx.useradmin.UserManager;
import org.osgi.jmx.framework.BundleStateMBean;
import org.osgi.jmx.framework.FrameworkMBean;
import org.osgi.jmx.framework.PackageStateMBean;
import org.osgi.jmx.framework.ServiceStateMBean;
import org.osgi.jmx.service.cm.ConfigurationAdminMBean;
import org.osgi.jmx.service.provisioning.ProvisioningServiceMBean;
import org.osgi.jmx.service.useradmin.UserAdminMBean;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.packageadmin.PackageAdmin;
import org.osgi.service.permissionadmin.PermissionAdmin;
import org.osgi.service.provisioning.ProvisioningService;
import org.osgi.service.startlevel.StartLevel;
import org.osgi.service.useradmin.UserAdmin;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * The bundle activator which starts and stops the system, as well as providing
 * the service tracker which listens for the MBeanServer. When the MBeanServer
 * is found, the MBeans representing the OSGi services will be installed.
 * 
 */
public class Activator implements BundleActivator {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(BundleContext bundleContext) throws Exception {
		this.bundleContext = bundleContext;
		frameworkName = new ObjectName(FrameworkMBean.OBJECTNAME);
		bundlesStateName = new ObjectName(BundleStateMBean.OBJECTNAME);
		serviceStateName = new ObjectName(ServiceStateMBean.OBJECTNAME);
		packageStateName = new ObjectName(PackageStateMBean.OBJECTNAME);

		mbeanServiceTracker = new ServiceTracker(bundleContext,
				MBeanServer.class.getCanonicalName(), new MBeanServiceTracker());
		log.fine("Awaiting MBeanServer service registration");
		mbeanServiceTracker.open();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext arg0) throws Exception {
		mbeanServiceTracker.close();
		for (MBeanServer mbeanServer : mbeanServers) {
			deregisterServices(mbeanServer);
		}
		mbeanServers.clear();
	}

	/**
     */
	protected synchronized void deregisterServices(MBeanServer mbeanServer) {
		if (!servicesRegistered.get()) {
			return;
		}
		log.fine("Deregistering framework with MBeanServer: " + mbeanServer);
		try {
			mbeanServer.unregisterMBean(frameworkName);
		} catch (InstanceNotFoundException e) {
			log
					.log(Level.FINE,
							"FrameworkMBean not found on deregistration", e);
		} catch (MBeanRegistrationException e) {
			log.log(Level.FINE, "FrameworkMBean deregistration problem", e);
		}
		framework = null;

		try {
			mbeanServer.unregisterMBean(bundlesStateName);
		} catch (InstanceNotFoundException e) {
			log.log(Level.FINEST,
					"OSGi BundleStateMBean not found on deregistration", e);
		} catch (MBeanRegistrationException e) {
			log.log(Level.FINE, "OSGi BundleStateMBean deregistration problem",
					e);
		}
		bundleState = null;

		log.fine("Deregistering services monitor with MBeanServer: "
				+ mbeanServer);
		try {
			mbeanServer.unregisterMBean(serviceStateName);
		} catch (InstanceNotFoundException e) {
			log.log(Level.FINEST,
					"OSGi ServiceStateMBean not found on deregistration", e);
		} catch (MBeanRegistrationException e) {
			log.log(Level.FINE,
					"OSGi ServiceStateMBean deregistration problem", e);
		}
		serviceState = null;

		log.fine("Deregistering packages monitor with MBeanServer: "
				+ mbeanServer);
		try {
			mbeanServer.unregisterMBean(packageStateName);
		} catch (InstanceNotFoundException e) {
			log.log(Level.FINEST,
					"OSGi PackageStateMBean not found on deregistration", e);
		} catch (MBeanRegistrationException e) {
			log.log(Level.FINE,
					"OSGi PackageStateMBean deregistration problem", e);
		}
		packageState = null;
		configAdminTracker.close();
		configAdminTracker = null;
		permissionAdminTracker.close();
		permissionAdminTracker = null;
		provisioningServiceTracker.close();
		provisioningServiceTracker = null;
		userAdminTracker.close();
		userAdminTracker = null;

		servicesRegistered.set(false);
	}

	/**
     */
	protected synchronized void registerServices(MBeanServer mbeanServer) {
		PackageAdmin admin = (PackageAdmin) bundleContext
				.getService(bundleContext
						.getServiceReference(PackageAdmin.class
								.getCanonicalName()));
		StartLevel sl = (StartLevel) bundleContext.getService(bundleContext
				.getServiceReference(StartLevel.class.getCanonicalName()));
		try {
			framework = new StandardMBean(new Framework(bundleContext, admin,
					sl), FrameworkMBean.class);
		} catch (NotCompliantMBeanException e) {
			log.log(Level.SEVERE,
					"Unable to create StandardMBean for Framework", e);
			return;
		}
		try {
			bundleState = new StandardMBean(new BundleState(bundleContext, sl,
					admin), BundleStateMBean.class);
		} catch (NotCompliantMBeanException e) {
			log.log(Level.SEVERE,
					"Unable to create StandardMBean for BundleState", e);
			return;
		}
		try {
			serviceState = new StandardMBean(new ServiceState(bundleContext),
					ServiceStateMBean.class);
		} catch (NotCompliantMBeanException e) {
			log.log(Level.SEVERE,
					"Unable to create StandardMBean for ServiceState", e);
			return;
		}
		try {
			packageState = new StandardMBean(new PackageState(bundleContext,
					admin), PackageStateMBean.class);
		} catch (NotCompliantMBeanException e) {
			log.log(Level.SEVERE,
					"Unable to create StandardMBean for PackageState", e);
			return;
		}

		log.fine("Registering Framework with MBeanServer: " + mbeanServer
				+ " with name: " + frameworkName);
		try {
			mbeanServer.registerMBean(framework, frameworkName);
		} catch (InstanceAlreadyExistsException e) {
			log.log(Level.SEVERE, "Cannot register OSGi framework MBean", e);
		} catch (MBeanRegistrationException e) {
			log.log(Level.SEVERE, "Cannot register OSGi framework MBean", e);
		} catch (NotCompliantMBeanException e) {
			log.log(Level.SEVERE, "Cannot register OSGi framework MBean", e);
		}

		log.fine("Registering bundle state monitor with MBeanServer: "
				+ mbeanServer + " with name: " + bundlesStateName);
		try {
			mbeanServer.registerMBean(bundleState, bundlesStateName);
		} catch (InstanceAlreadyExistsException e) {
			log.log(Level.SEVERE, "Cannot register OSGi BundleStateMBean", e);
		} catch (MBeanRegistrationException e) {
			log.log(Level.SEVERE, "Cannot register OSGi BundleStateMBean", e);
		} catch (NotCompliantMBeanException e) {
			log.log(Level.SEVERE, "Cannot register OSGi BundleStateMBean", e);
		}

		log.fine("Registering services monitor with MBeanServer: "
				+ mbeanServer + " with name: " + serviceStateName);
		try {
			mbeanServer.registerMBean(serviceState, serviceStateName);
		} catch (InstanceAlreadyExistsException e) {
			log.log(Level.SEVERE, "Cannot register OSGi ServiceStateMBean", e);
		} catch (MBeanRegistrationException e) {
			log.log(Level.SEVERE, "Cannot register OSGi ServiceStateMBean", e);
		} catch (NotCompliantMBeanException e) {
			log.log(Level.SEVERE, "Cannot register OSGi ServiceStateMBean", e);
		}

		log.fine("Registering packages monitor with MBeanServer: "
				+ mbeanServer + " with name: " + packageStateName);
		try {
			mbeanServer.registerMBean(packageState, packageStateName);
		} catch (InstanceAlreadyExistsException e) {
			log.log(Level.SEVERE, "Cannot register OSGi PackageStateMBean", e);
		} catch (MBeanRegistrationException e) {
			log.log(Level.SEVERE, "Cannot register OSGi PackageStateMBean", e);
		} catch (NotCompliantMBeanException e) {
			log.log(Level.SEVERE, "Cannot register OSGi PackageStateMBean", e);
		}

		configAdminTracker = new ServiceTracker(bundleContext,
				"org.osgi.service.cm.ConfigurationAdmin",
				new ConfigAdminTracker());
		permissionAdminTracker = new ServiceTracker(bundleContext,
				"org.osgi.service.permissionadmin.PermissionAdmin",
				new PermissionAdminTracker());
		provisioningServiceTracker = new ServiceTracker(bundleContext,
				"org.osgi.service.provisioning.ProvisioningService",
				new ProvisioningServiceTracker());
		userAdminTracker = new ServiceTracker(bundleContext,
				"org.osgi.service.useradmin.UserAdmin", new UserAdminTracker());
		configAdminTracker.open();
		permissionAdminTracker.open();
		provisioningServiceTracker.open();
		userAdminTracker.open();
		servicesRegistered.set(true);
	}

	private static final Logger log = Logger.getLogger(Activator.class
			.getCanonicalName());

	protected List<MBeanServer> mbeanServers = new CopyOnWriteArrayList<MBeanServer>();
	protected StandardMBean bundleState;
	protected StandardMBean packageState;
	protected StandardMBean serviceState;
	protected BundleContext bundleContext;
	protected ObjectName bundlesStateName;
	protected StandardMBean framework;
	protected ObjectName frameworkName;
	protected ServiceTracker mbeanServiceTracker;
	protected ObjectName packageStateName;
	protected ObjectName serviceStateName;
	protected AtomicBoolean servicesRegistered = new AtomicBoolean(false);
	protected ServiceTracker configAdminTracker;
	protected ServiceTracker permissionAdminTracker;
	protected ServiceTracker provisioningServiceTracker;
	protected ServiceTracker userAdminTracker;

	class MBeanServiceTracker implements ServiceTrackerCustomizer {

		public Object addingService(ServiceReference servicereference) {
			try {
				log.fine("Adding MBeanServer: " + servicereference);
				final MBeanServer mbeanServer = (MBeanServer) bundleContext
						.getService(servicereference);
				mbeanServers.add(mbeanServer);
				Runnable registration = new Runnable() {
					public void run() {
						registerServices(mbeanServer);
					}
				};
				Thread registrationThread = new Thread(registration,
						"JMX Core MBean Registration");
				registrationThread.setDaemon(true);
				registrationThread.start();

				return mbeanServer;
			} catch (RuntimeException e) {
				log.log(Level.SEVERE, "uncaught exception in addingService", e);
				throw e;
			}
		}

		public void modifiedService(ServiceReference servicereference,
				Object obj) {
			// no op
		}

		public void removedService(ServiceReference servicereference, Object obj) {
			try {
				log.fine("Removing MBeanServer: " + servicereference);
				final MBeanServer mbeanServer = (MBeanServer) bundleContext
						.getService(servicereference);
				mbeanServers.remove(mbeanServer);
				Runnable deregister = new Runnable() {
					public void run() {
						deregisterServices(mbeanServer);
					}
				};

				Thread deregisterThread = new Thread(deregister,
						"JMX Core MBean Deregistration");
				deregisterThread.setDaemon(true);
				deregisterThread.start();

			} catch (Throwable e) {
				log.log(Level.FINE, "uncaught exception in removedService", e);
			}
		}
	}

	class ConfigAdminTracker implements ServiceTrackerCustomizer {
		StandardMBean manager;
		ObjectName name;

		public ConfigAdminTracker() {
			try {
				name = new ObjectName(ConfigurationAdminMBean.OBJECTNAME);
			} catch (Throwable e) {
				throw new IllegalStateException(
						"unable to create object name: "
								+ ConfigurationAdminMBean.OBJECTNAME);
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.osgi.util.tracker.ServiceTrackerCustomizer#addingService(org.
		 * osgi.framework.ServiceReference)
		 */
		public Object addingService(ServiceReference reference) {
			ConfigurationAdmin admin;
			try {
				admin = (ConfigurationAdmin) bundleContext
						.getService(reference);
			} catch (ClassCastException e) {
				log
						.log(
								Level.SEVERE,
								"Incompatible class version for the Configuration Admin Manager",
								e);
				return bundleContext.getService(reference);
			}

			try {
				manager = new StandardMBean(new ConfigAdminManager(admin),
						ConfigurationAdminMBean.class);
			} catch (NotCompliantMBeanException e1) {
				log.log(Level.SEVERE,
						"Unable to create Configuration Admin Manager");
				return admin;
			}
			for (MBeanServer mbeanServer : mbeanServers) {
				log.fine("Registering configuration admin with MBeanServer: "
						+ mbeanServer + " with name: " + name);
				try {
					mbeanServer.registerMBean(manager, name);
				} catch (InstanceAlreadyExistsException e) {
					log.log(Level.SEVERE,
							"Cannot register Configuration Manager MBean", e);
				} catch (MBeanRegistrationException e) {
					log.log(Level.SEVERE,
							"Cannot register Configuration Manager MBean", e);
				} catch (NotCompliantMBeanException e) {
					log.log(Level.SEVERE,
							"Cannot register Configuration Manager MBean", e);
				}
			}
			return admin;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.osgi.util.tracker.ServiceTrackerCustomizer#modifiedService(org
		 * .osgi.framework.ServiceReference, java.lang.Object)
		 */
		public void modifiedService(ServiceReference reference, Object service) {
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.osgi.util.tracker.ServiceTrackerCustomizer#removedService(org
		 * .osgi.framework.ServiceReference, java.lang.Object)
		 */
		public void removedService(ServiceReference reference, Object service) {

			for (MBeanServer mbeanServer : mbeanServers) {
				log.fine("deregistering configuration admin from: "
						+ mbeanServer + " with name: " + name);
				try {
					mbeanServer.unregisterMBean(name);
				} catch (InstanceNotFoundException e) {
					log
							.fine("Configuration Manager MBean was never registered");
				} catch (MBeanRegistrationException e) {
					log.log(Level.SEVERE,
							"Cannot deregister Configuration Manager MBean", e);
				}
			}
		}
	}

	class PermissionAdminTracker implements ServiceTrackerCustomizer {
		StandardMBean manager;
		ObjectName name;

		public PermissionAdminTracker() {
			try {
				name = new ObjectName(ProvisioningServiceMBean.OBJECTNAME);
			} catch (Throwable e) {
				throw new IllegalStateException(
						"unable to create object name: "
								+ ProvisioningServiceMBean.OBJECTNAME);
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.osgi.util.tracker.ServiceTrackerCustomizer#addingService(org.
		 * osgi.framework.ServiceReference)
		 */
		public Object addingService(ServiceReference reference) {
			PermissionAdmin admin;
			try {
				admin = (PermissionAdmin) bundleContext.getService(reference);
			} catch (ClassCastException e) {
				log
						.log(
								Level.SEVERE,
								"Incompatible class version for the Permission Admin Manager",
								e);
				return bundleContext.getService(reference);
			}
			try {
				manager = new StandardMBean(new PermissionManager(admin),
						ProvisioningServiceMBean.class);
			} catch (NotCompliantMBeanException e1) {
				log.log(Level.SEVERE,
						"Unable to create Permission Admin Manager");
				return admin;
			}
			for (MBeanServer mbeanServer : mbeanServers) {
				log.fine("Registering permission admin with MBeanServer: "
						+ mbeanServer + " with name: " + name);
				try {
					mbeanServer.registerMBean(manager, name);
				} catch (InstanceAlreadyExistsException e) {
					log.log(Level.SEVERE,
							"Cannot register Permission Manager MBean", e);
				} catch (MBeanRegistrationException e) {
					log.log(Level.SEVERE,
							"Cannot register Permission Manager MBean", e);
				} catch (NotCompliantMBeanException e) {
					log.log(Level.SEVERE,
							"Cannot register Permission Manager MBean", e);
				}
			}
			return admin;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.osgi.util.tracker.ServiceTrackerCustomizer#modifiedService(org
		 * .osgi.framework.ServiceReference, java.lang.Object)
		 */
		public void modifiedService(ServiceReference reference, Object service) {
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.osgi.util.tracker.ServiceTrackerCustomizer#removedService(org
		 * .osgi.framework.ServiceReference, java.lang.Object)
		 */
		public void removedService(ServiceReference reference, Object service) {
			for (MBeanServer mbeanServer : mbeanServers) {
				log.fine("deregistering permission admin with MBeanServer: "
						+ mbeanServer + " with name: " + name);
				try {
					mbeanServer.unregisterMBean(name);
				} catch (InstanceNotFoundException e) {
					log.fine("Permission Manager MBean was never registered");
				} catch (MBeanRegistrationException e) {
					log.log(Level.SEVERE,
							"Cannot deregister Permission Manager MBean", e);
				}
			}
		}
	}

	class ProvisioningServiceTracker implements ServiceTrackerCustomizer {
		StandardMBean provisioning;
		ObjectName name;

		public ProvisioningServiceTracker() {
			try {
				name = new ObjectName(ProvisioningServiceMBean.OBJECTNAME);
			} catch (Throwable e) {
				throw new IllegalStateException(
						"unable to create object name: "
								+ ProvisioningServiceMBean.OBJECTNAME);
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.osgi.util.tracker.ServiceTrackerCustomizer#addingService(org.
		 * osgi.framework.ServiceReference)
		 */
		public Object addingService(ServiceReference reference) {
			ProvisioningService service;
			try {
				service = (ProvisioningService) bundleContext
						.getService(reference);
			} catch (ClassCastException e) {
				log
						.log(
								Level.SEVERE,
								"Incompatible class version for the Provisioning service",
								e);
				return bundleContext.getService(reference);
			}
			try {
				provisioning = new StandardMBean(new Provisioning(service),
						ProvisioningServiceMBean.class);
			} catch (NotCompliantMBeanException e1) {
				log.log(Level.SEVERE,
						"Unable to create Provisioning Service Manager");
				return service;
			}
			for (MBeanServer mbeanServer : mbeanServers) {
				log.fine("Registering provisioning service with MBeanServer: "
						+ mbeanServer + " with name: " + name);
				try {
					mbeanServer.registerMBean(provisioning, name);
				} catch (InstanceAlreadyExistsException e) {
					log.log(Level.SEVERE,
							"Cannot register Provisioning Service MBean", e);
				} catch (MBeanRegistrationException e) {
					log.log(Level.SEVERE,
							"Cannot register Provisioning Service MBean", e);
				} catch (NotCompliantMBeanException e) {
					log.log(Level.SEVERE,
							"Cannot register Provisioning Service MBean", e);
				}
			}
			return service;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.osgi.util.tracker.ServiceTrackerCustomizer#modifiedService(org
		 * .osgi.framework.ServiceReference, java.lang.Object)
		 */
		public void modifiedService(ServiceReference reference, Object service) {
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.osgi.util.tracker.ServiceTrackerCustomizer#removedService(org
		 * .osgi.framework.ServiceReference, java.lang.Object)
		 */
		public void removedService(ServiceReference reference, Object service) {
			for (MBeanServer mbeanServer : mbeanServers) {
				log
						.fine("deregistering provisioning service with MBeanServer: "
								+ mbeanServer + " with name: " + name);
				try {
					mbeanServer.unregisterMBean(name);
				} catch (InstanceNotFoundException e) {
					log.fine("Provisioning Service MBean was never registered");
				} catch (MBeanRegistrationException e) {
					log.log(Level.SEVERE,
							"Cannot deregister Provisioning Service MBean", e);
				}
			}
		}
	}

	class UserAdminTracker implements ServiceTrackerCustomizer {
		StandardMBean manager;
		ObjectName name;

		public UserAdminTracker() {
			try {
				name = new ObjectName(UserAdminMBean.OBJECTNAME);
			} catch (Throwable e) {
				throw new IllegalStateException(
						"unable to create object name: "
								+ UserAdminMBean.OBJECTNAME);
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.osgi.util.tracker.ServiceTrackerCustomizer#addingService(org.
		 * osgi.framework.ServiceReference)
		 */
		public Object addingService(ServiceReference reference) {
			UserAdmin admin;
			try {
				admin = (UserAdmin) bundleContext.getService(reference);
			} catch (ClassCastException e) {
				log
						.log(
								Level.SEVERE,
								"Incompatible class version for the User Admin manager",
								e);
				return bundleContext.getService(reference);
			}
			try {
				manager = new StandardMBean(new UserManager(admin),
						UserAdminMBean.class);
			} catch (NotCompliantMBeanException e1) {
				log.log(Level.SEVERE, "Unable to create User Admin Manager");
				return admin;
			}
			for (MBeanServer mbeanServer : mbeanServers) {
				log.fine("Registering user admin with MBeanServer: "
						+ mbeanServer + " with name: " + name);
				try {
					mbeanServer.registerMBean(manager, name);
				} catch (InstanceAlreadyExistsException e) {
					log.log(Level.SEVERE, "Cannot register User Manager MBean",
							e);
				} catch (MBeanRegistrationException e) {
					log.log(Level.SEVERE, "Cannot register User Manager MBean",
							e);
				} catch (NotCompliantMBeanException e) {
					log.log(Level.SEVERE, "Cannot register User Manager MBean",
							e);
				}
			}
			return admin;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.osgi.util.tracker.ServiceTrackerCustomizer#modifiedService(org
		 * .osgi.framework.ServiceReference, java.lang.Object)
		 */
		public void modifiedService(ServiceReference reference, Object service) {
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.osgi.util.tracker.ServiceTrackerCustomizer#removedService(org
		 * .osgi.framework.ServiceReference, java.lang.Object)
		 */
		public void removedService(ServiceReference reference, Object service) {
			for (MBeanServer mbeanServer : mbeanServers) {
				log.fine("Deregistering user admin with MBeanServer: "
						+ mbeanServer + " with name: " + name);
				try {
					mbeanServer.unregisterMBean(name);
				} catch (InstanceNotFoundException e) {
					log.fine("User Manager MBean was never registered");
				} catch (MBeanRegistrationException e) {
					log.log(Level.SEVERE,
							"Cannot deregister User Manager MBean", e);
				}
			}
		}
	}
}
