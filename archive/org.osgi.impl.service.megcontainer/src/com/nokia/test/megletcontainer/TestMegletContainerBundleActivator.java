/*
 * ============================================================================
 * (c) Copyright 2004 Nokia This material, including documentation and any
 * related computer programs, is protected by copyright controlled by Nokia and
 * its licensors. All rights are reserved.
 * 
 * These materials have been contributed to the Open Services Gateway Initiative
 * (OSGi)as "MEMBER LICENSED MATERIALS" as defined in, and subject to the terms
 * of, the OSGi Member Agreement specifically including, but not limited to, the
 * license rights and warranty disclaimers as set forth in Sections 3.2 and 12.1
 * thereof, and the applicable Statement of Work. All company, brand and product
 * names contained within this document may be trademarks that are the sole
 * property of the respective owners. The above notice must be included on all
 * copies of this document.
 * ============================================================================
 */
package com.nokia.test.megletcontainer;

import java.io.*;
import java.net.URL;
import java.util.*;

import org.osgi.framework.*;
import org.osgi.service.application.*;
import org.osgi.service.event.*;
import org.osgi.meglet.MegletHandle;

public class TestMegletContainerBundleActivator extends Object implements
		BundleActivator, BundleListener, EventHandler, Runnable {
	private BundleContext						bc;
	private Thread									testerThread;
	private Bundle									megBundle;
	private Bundle									megletContainerBundle;
	private Bundle									schedulerBundle;
	private ApplicationDescriptor[]	appDescs;
	private ApplicationHandle				appHandle;
	private String									installedAppUID;
	private long										installedBundleID;
	private ServiceRegistration			serviceReg;
	private LinkedList							receivedEvents;

	private final static int				APPLICATION_START = 1;
	private final static int				APPLICATION_SUSPEND = 2;
	private final static int				APPLICATION_RESUME = 3;
	private final static int				APPLICATION_STOPPING = 4;
	private final static int        APPLICATION_STOPPED = 5;
	private final static int				APPLICATION_STOPPED_AFTER_SUSPEND = 6;
	
	public TestMegletContainerBundleActivator() {
		super();
		receivedEvents = new LinkedList();
	}

	public void start(BundleContext bc) throws Exception {
		this.bc = bc;
		serviceReg = bc.registerService("org.osgi.service.event.EventHandler",
				this, null);
		bc.addBundleListener(this);
	}

	public void stop(BundleContext bc) throws Exception {
		serviceReg.unregister();
		this.bc = null;
	}

	public synchronized void handleEvent(Event event) {
		receivedEvents.add(event);
		notify();
	}

	synchronized Event getEvent() {
		while (receivedEvents.isEmpty()) {
			try {
				wait();
			}
			catch (InterruptedException e) {}
		}
		return (Event) receivedEvents.removeFirst();
	}

	synchronized void deleteEventQueue() {
		receivedEvents.clear();
	}
	
	boolean checkEvent(String name) {
		try {
			Event event;
			do {
				event = getEvent();
			} while (!event.getTopic().startsWith("org/osgi/application/")
					&& !event.getTopic().startsWith("com/nokia/megtest/"));
			if (!event.getTopic().equals(name))
				throw new Exception("Received: " + event.getTopic()
						+ "    Expected:" + name);
			return true;
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	boolean waitStateChangeEvent( int state, String pid ) {
		long time = System.currentTimeMillis();
		
		try {
			Event event;
			do {
				event = getEvent();
				
				String  requiredTopic = null;
				Integer requiredState = null;
								
				switch( state ) {
					case APPLICATION_START:
						requiredTopic = "org/osgi/framework/ServiceEvent/REGISTERED";
						requiredState = new Integer( ApplicationHandle.RUNNING );
						break;
					case APPLICATION_SUSPEND:
						requiredTopic = "org/osgi/framework/ServiceEvent/MODIFIED";
						requiredState = new Integer(MegletHandle.SUSPENDED );
						break;
					case APPLICATION_RESUME:
						requiredTopic = "org/osgi/framework/ServiceEvent/MODIFIED";
						requiredState = new Integer( ApplicationHandle.RUNNING );
						break;
					case APPLICATION_STOPPING:
						requiredTopic = "org/osgi/framework/ServiceEvent/MODIFIED";
						requiredState = new Integer( ApplicationHandle.STOPPING );
						break;
					case APPLICATION_STOPPED:
						requiredTopic = "org/osgi/framework/ServiceEvent/UNREGISTERING";
						requiredState = new Integer( ApplicationHandle.STOPPING );
						break;
					case APPLICATION_STOPPED_AFTER_SUSPEND:
						requiredTopic = "org/osgi/framework/ServiceEvent/UNREGISTERING";
						requiredState = new Integer( MegletHandle.SUSPENDED );
						break;
				}
				
				if( event.getTopic().equals( requiredTopic ) ) {					
					ServiceReference serviceRef = ((ServiceEvent)event.
							 														getProperty( "event" )).getServiceReference();					

					if( serviceRef == null )
						continue;
					
					String objectClasses[] = (String []) serviceRef.getProperty( Constants.OBJECTCLASS );
					if( objectClasses == null )
						continue;
					boolean found = false;
					for( int q=0; q != objectClasses.length; q++ )
						if( objectClasses[ q ].equals( ApplicationHandle.class.getName()) ) {
							found = true;
							break;
						}
					if( !found )
						continue;
					
					if( serviceRef.getProperty( "application.pid" ) != null &&
							serviceRef.getProperty( "application.pid" ).equals( pid ) &&
							serviceRef.getProperty( "application.state" ) != null &&
							serviceRef.getProperty( "application.state" ).equals( requiredState ) ) {
						String descriptorPID = (String)serviceRef.getProperty( "descriptor.pid" );
						if( descriptorPID != null ) {
							ServiceReference[] appDescRefs = bc
							.getServiceReferences( "org.osgi.service.application.ApplicationDescriptor",
									"(" + Constants.SERVICE_PID + "=" + descriptorPID + ")");
							if (appDescRefs != null && appDescRefs.length != 0 ) {
								ApplicationDescriptor appDesc = (ApplicationDescriptor)bc.getService( appDescRefs [ 0 ] );
								String appPID = appDesc.getPID();
								bc.ungetService( appDescRefs [ 0 ] );
								if( appPID.equals( pid ) )
									return true;
							}
						}
					}
				}
			} while ( System.currentTimeMillis() < time + 5000 ); /* wait maximum 5s */
			throw new Exception ( "The required state change didn't occurred!" );
		}
  	catch (Exception e) {
			e.printStackTrace();
			return false;
		}		
	}
	
	public ApplicationDescriptor getAppDesc( ApplicationHandle appHnd ) {
		ServiceReference appDescRef = appHnd.getApplicationDescriptor();
		ApplicationDescriptor appDesc = (ApplicationDescriptor)bc.getService( appDescRef );
		bc.ungetService( appDescRef );
		return appDesc;
	}
	
	public void bundleChanged(BundleEvent e) {
		if (e.getBundle().getBundleId() == bc.getBundle().getBundleId()
				&& e.getType() == BundleEvent.STARTED) {
			// here we start the tester thread
			testerThread = new Thread(this);
			testerThread.start();
		}
	}

	public void run() {
		System.out.println("Meglet container tester thread started!\n");
		if (!testCase_lookUpMegletContainer()) {
			System.out
					.println("Looking up the Meglet container                  FAILED");
			return;
		}
		else
			System.out
					.println("Looking up the Meglet container                  PASSED");
		if (!testCase_lookUpScheduler()) {
			System.out
					.println("Looking up the scheduler                         FAILED");
			return;
		}
		else
			System.out
					.println("Looking up the scheduler                         PASSED");
		if (!testCase_installMegletBundle())
			System.out
					.println("Meglet bundle install onto Meglet container      FAILED");
		else
			System.out
					.println("Meglet bundle install onto Meglet container      PASSED");
		if (!testCase_checkAppDescs())
			System.out
					.println("Checking the installed Meglet app descriptors    FAILED");
		else
			System.out
					.println("Checking the installed Meglet app descriptors    PASSED");
		if (!testCase_launchApplication())
			System.out
					.println("Launching the Meglet application                 FAILED");
		else
			System.out
					.println("Launching the Meglet application                 PASSED");
		if (!testCase_suspendApplication())
			System.out
					.println("Suspending the Meglet application                FAILED");
		else
			System.out
					.println("Suspending the Meglet application                PASSED");
		if (!testCase_resumeApplication())
			System.out
					.println("Resuming the Meglet application                  FAILED");
		else
			System.out
					.println("Resuming the Meglet application                  PASSED");
		if (!testCase_stopApplication())
			System.out
					.println("Stopping the Meglet application                  FAILED");
		else
			System.out
					.println("Stopping the Meglet application                  PASSED");
		if (!testCase_lockApplication())
			System.out
					.println("Locking the application                          FAILED");
		else
			System.out
					.println("Locking the application                          PASSED");
		if (!testCase_saveLockingState())
			System.out
					.println("Save the locking state of the application        FAILED");
		else
			System.out
					.println("Save the locking state of the application        PASSED");
		if (!testCase_resumingAfterLocking())
			System.out
					.println("Resuming a Meglet application after locking      FAILED");
		else
			System.out
					.println("Resuming a Meglet application after locking      PASSED");
		if (!testCase_launchAfterRestart())
			System.out
					.println("Launching Meglet app after container restart     FAILED");
		else
			System.out
					.println("Launching Meglet app after container restart     PASSED");
		if (!testCase_singletonCheck())
			System.out
					.println("Checking singleton application                   FAILED");
		else
			System.out
					.println("Checking singleton application                   PASSED");
		if (!testCase_uninstallMegletBundle())
			System.out
					.println("Meglet bundle uninstall from Meglet container    FAILED");
		else
			System.out
					.println("Meglet bundle uninstall from Meglet container    PASSED");
		if (!testCase_autoStartCheck())
			System.out
					.println("Checking autostart application                   FAILED");
		else
			System.out
					.println("Checking autostart application                   PASSED");
		if (!testCase_nonsingletonCheck())
			System.out
					.println("Checking non-singleton application               FAILED");
		else
			System.out
					.println("Checking non-singleton application               PASSED");
		if (!testCase_lifeCycleChangeByEvents())
			System.out
					.println("Lifecycle change by specific events              FAILED");
		else
			System.out
					.println("Lifecycle change by specific events              PASSED");
		if (!testCase_requestStop())
			System.out
					.println("Checking the stop request                        FAILED");
		else
			System.out
					.println("Checking the stop request                        PASSED");
		if (!testCase_requestSuspend())
			System.out
					.println("Checking the suspend request                     FAILED");
		else
			System.out
					.println("Checking the suspend request                     PASSED");
		if (!testCase_eventListening())
			System.out
					.println("Listening to specific events                     FAILED");
		else
			System.out
					.println("Listening to specific events                     PASSED");
		if (!testCase_eventChannelLogService())
			System.out
					.println("Checking getEventChannel, getLogService          FAILED");
		else
			System.out
					.println("Checking getEventChannel, getLogService          PASSED");
		if (!testCase_registerForEvents())
			System.out
					.println("Checking register for events                     FAILED");
		else
			System.out
					.println("Checking register for events                     PASSED");
		if (!testCase_scheduleAnApplication())
			System.out
					.println("Scheduling an application                        FAILED");
		else
			System.out
					.println("Scheduling an application                        PASSED");
		if (!testCase_scheduleTwoApplications())
			System.out
					.println("Scheduling two applications                      FAILED");
		else
			System.out
					.println("Scheduling two applications                      PASSED");
		if (!testCase_scheduleApplicationsWithAARestart())
			System.out
					.println("Scheduling an applications with AA restart       FAILED");
		else
			System.out
					.println("Scheduling an applications with AA restart       PASSED");
		if (!testCase_removeScheduledApplication())
			System.out
					.println("Checking scheduled application remove            FAILED");
		else
			System.out
					.println("Checking scheduled application remove            PASSED");
		if (!testCase_recurringSchedule())
			System.out
					.println("Checking the recurring schedule                  FAILED");
		else
			System.out
					.println("Checking the recurring schedule                  PASSED");
		if (!testCase_schedulerFilterMatching())
			System.out
					.println("Checking the filter matching of the scheduler    FAILED");
		else
			System.out
					.println("Checking the filter matching of the scheduler    PASSED");
		if (!testCase_uninstallMegletBundle())
			System.out
					.println("Meglet bundle uninstall from Meglet container    FAILED");
		else
			System.out
					.println("Meglet bundle uninstall from Meglet container    PASSED");
		System.out.println("\n\nMEG container tester thread finished!");
	}

	boolean installMegletBundle(String resourceName) {
		try {
			URL resourceURL = bc.getBundle().getResource(resourceName);
			if (resourceURL == null)
				throw new Exception("Can't find " + resourceName
						+ " in the resources!");
			megBundle = bc.installBundle(resourceURL.toString(), resourceURL
					.openStream());
			megBundle.start();
			installedBundleID = megBundle.getBundleId();
			ServiceReference[] appList = null;
			int tries = 0;
			do {
				if (++tries == 20)
					throw new Exception(
							"Can't find the installed ApplicationDescriptor!");
				try {
					Thread.sleep(250);
				}
				catch (InterruptedException e) {}
				if (megBundle.getState() == Bundle.ACTIVE) {
					appList = bc
							.getServiceReferences(
									"org.osgi.service.application.ApplicationDescriptor",
									"(application.bundle.id="
											+ installedBundleID + ")");
					if (appList != null && appList.length != 0)
						break;
				}
			} while (true);
			appDescs = new ApplicationDescriptor[appList.length];
			for (int i = 0; i != appList.length; i++) {
				appDescs[i] = (ApplicationDescriptor) bc.getService(appList[i]);
				bc.ungetService(appList[i]);
			}
			installedAppUID = appDescs[0].getPID();
			if (appDescs == null || appDescs.length != 1)
				throw new Exception(
						"Illegal number of applications were installed!");
			return true;
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	boolean testCase_installMegletBundle() {
		return installMegletBundle("megletsample.jar");
	}

	boolean testCase_lookUpMegletContainer() {
		try {
			Bundle[] bundles = bc.getBundles();
			for (int i = 0; i < bundles.length; i++) {
				if (bundles[i].getState() == Bundle.ACTIVE) {
					Dictionary dict = bundles[i].getHeaders();
					Object name = dict.get("Meglet-Container-Name");
					Object version = dict.get("Meglet-Container-Version");
					if (name != null && version != null) {
						System.out.println((String) name + " ("
								+ (String) version + ") found!");
						megletContainerBundle = bundles[i];
						return true;
					}
				}
			}
			System.out.println("Meglet Container is not running!");
			return false;
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	boolean testCase_lookUpScheduler() {
		try {
			Bundle[] bundles = bc.getBundles();
			for (int i = 0; i < bundles.length; i++) {
				if (bundles[i].getState() == Bundle.ACTIVE) {
					Dictionary dict = bundles[i].getHeaders();
					Object name = dict.get("Scheduler-Name");
					Object version = dict.get("Scheduler-Version");
					if (name != null && version != null) {
						System.out.println((String) name + " ("
								+ (String) version + ") found!");
						schedulerBundle = bundles[i];
						return true;
					}
				}
			}
			System.out.println("Scheduler bundle is not running!");
			return false;
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	boolean testCase_uninstallMegletBundle() {
		try {
			megBundle.stop();
			megBundle.uninstall();
			if (megBundle.getState() != Bundle.UNINSTALLED)
				throw new Exception(
						"Couldn't uninstall Meglet bundle properly!");
			return true;
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	boolean testCase_checkAppDescs() {
		ApplicationDescriptor appDesc = appDescs[0];
		String appUID = appDesc.getPID();
		try {
			ServiceReference[] appList = bc.getServiceReferences(
					"org.osgi.service.application.ApplicationDescriptor",
					"(service.pid=" + appUID + ")");
			if (appList == null || appList.length == 0)
				throw new Exception("No ApplicationUID(" + appUID
						+ ") registered!");
			long bundleID = -1;
			boolean registered = false;
			for (int i = 0; i != appList.length; i++) {
				if ((ApplicationDescriptor) bc.getService(appList[i]) == appDesc) {
					registered = true;
					if (!appList[i].getProperty("service.pid").equals(
							appDesc.getPID()))
						throw new Exception(
								"Illegal service.pid in the AppDesc ("
										+ (String) appList[i]
												.getProperty("service.pid")
										+ " != " + appDesc.getPID()
										+ ")");
					if (!appList[i].getProperty("application.name").equals(
							appDesc.getProperties("").get(
									ApplicationDescriptor.APPLICATION_NAME)))
						throw new Exception(
								"Illegal application name in the AppDesc ("
										+ (String) appList[i]
												.getProperty("application.name")
										+ " != "
										+ appDesc
												.getProperties("")
												.get(
														ApplicationDescriptor.APPLICATION_NAME)
										+ ")");
					bundleID = appList[i].getBundle().getBundleId();
				}
				bc.ungetService(appList[i]);
			}
			Map engProps = appDesc.getProperties("en");
			if (!engProps.get("application.name")
					.equals("MEG Test Application"))
				throw new Exception("The application name is "
						+ (String) engProps.get("application.name")
						+ " instead of 'MEG Test Application'!");
			if (!((String) engProps.get("application.icon"))
					.endsWith("/TestIcon.gif"))
				throw new Exception("The icon path "
						+ (String) engProps.get("application.icon")
						+ " doesn't ends with '/TestIcon.gif'!");
			if (!engProps.get(ApplicationDescriptor.APPLICATION_VERSION)
					.equals("0.1"))
				throw new Exception("The application version is "
						+ (String) engProps
								.get(ApplicationDescriptor.APPLICATION_VERSION)
						+ " instead of '0.1'!");
			if (!engProps.get("application.type").equals("MEG"))
				throw new Exception("The application type is "
						+ (String) engProps.get("application.type")
						+ " instead of 'MEG'!");
			if (engProps.get("application.bundle.id") == null)
				throw new Exception("No application bundle id found!");
			if (!engProps.get("application.singleton").equals("true"))
				throw new Exception("Singleton flag is "
						+ (String) engProps.get("application.singleton")
						+ " instead of 'true'!");
			if (!engProps.get("application.autostart").equals("false"))
				throw new Exception("Autostart flag is "
						+ (String) engProps.get("application.autostart")
						+ " instead of 'false'!");
			if (!engProps.get("application.visible").equals("true"))
				throw new Exception("Visible flag is "
						+ (String) engProps.get("application.visible")
						+ " instead of 'true'!");
			if (!engProps.get("application.vendor").equals("Nokia"))
				throw new Exception("Vendor flag is "
						+ (String) engProps.get("application.vendor")
						+ " instead of 'Nokia'!");
			Map huProps = appDesc.getProperties("hu");
			if (!huProps.get("application.name").equals("MEG Teszt progi"))
				throw new Exception("The application name is "
						+ (String) huProps.get("application.name")
						+ " instead of 'MEG Teszt progi'!");
			if (!((String) huProps.get("application.icon"))
					.endsWith("/TestIcon.gif"))
				throw new Exception("The icon path "
						+ (String) huProps.get("application.icon")
						+ " doesn't ends with '/TestIcon.gif'!");
			Map ziProps = appDesc.getProperties("zi");
			if (!ziProps.get("application.name").equals("MEG Test Application")
					&& !ziProps.get("application.name").equals(
							"MEG Teszt progi"))
				throw new Exception(
						"The application name is "
								+ (String) ziProps.get("application.name")
								+ " instead of 'MEG Test Application or MEG Teszt progi'!");
			if (!((String) ziProps.get("application.icon"))
					.endsWith("/TestIcon.gif"))
				throw new Exception("The icon path "
						+ (String) ziProps.get("application.icon")
						+ " doesn't ends with '/TestIcon.gif'!");
			return true;
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private Map createArgs() {
		try {
			File file = bc.getDataFile("TestResult");
			if (file.exists())
				file.delete();
			Hashtable args = new Hashtable();
			args.put("TestResult", file.getAbsolutePath());
			return args;
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private boolean isLaunchable(ApplicationDescriptor appDesc) {
		String launchable = (String) (appDesc.getProperties("en")
				.get("application.launchable"));
		return launchable != null && launchable.equalsIgnoreCase("true");
	}

	private boolean checkResultFile(String result) {
		boolean matches = false;
		try {
			File resultFile = bc.getDataFile("TestResult");
			String resultStr = "";
			if (resultFile.exists()) {
				FileInputStream stream = new FileInputStream(resultFile);
				byte[] buffer = new byte[1024];
				int length;
				while ((length = stream.read(buffer, 0, buffer.length)) > 0)
					resultStr += new String(buffer);
				stream.close();
				resultStr = resultStr.trim();
			}
			matches = resultStr.equals(result);
			resultFile.delete();
			if (!matches)
				System.out.println("Received: " + resultStr + "  instead of "
						+ result);
			return matches;
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	boolean testCase_launchApplication() {
		return testCase_launchApplicationError(false);
	}

	boolean testCase_launchApplicationError(boolean errorFlag) {
		try {
			ApplicationDescriptor appDesc = appDescs[0];
			Map args = createArgs();
			if (args == null)
				throw new Exception("Cannot create the arguments of launch!");
			boolean launchable = isLaunchable(appDesc);
			ServiceReference appHandleRef = appDesc.launch(args);
			appHandle = (ApplicationHandle)bc.getService( appHandleRef );
			bc.ungetService( appHandleRef );
			if (!checkResultFile("START"))
				throw new Exception("Result of the launch is not START!");
			if (!launchable)
				throw new Exception(
						"Application started, but originally was not launchable!");
			if (!waitStateChangeEvent( APPLICATION_START, appDesc.getPID() ))
				throw new Exception("Didn't receive the start event!");
			ServiceReference[] appList = bc.getServiceReferences(
					"org.osgi.service.application.ApplicationHandle",
					"(application.pid=" + appDesc.getPID() + ")");
			if (appList == null || appList.length == 0)
				throw new Exception("No registered application handle found!");
			if (getAppDesc( appHandle ) != appDesc)
				throw new Exception(
						"The application descriptor differs from the found one!");
			if (appHandle.getState() != ApplicationHandle.RUNNING)
				throw new Exception("Application didn't started!");
			return true;
		}
		catch (Exception e) {
			if (!errorFlag)
				e.printStackTrace();
			return false;
		}
	}

	boolean testCase_suspendApplication() {
		try {
			((MegletHandle)appHandle).suspend();
			if (!checkResultFile("SUSPEND:StorageTestString"))
				throw new Exception("Result of the suspend is not SUSPEND!");
			if( !waitStateChangeEvent( APPLICATION_SUSPEND, appDescs[ 0 ].getPID() ) )
				throw new Exception("Didn't receive the suspend event!");
			if (appHandle.getState() != MegletHandle.SUSPENDED)
				throw new Exception("The status didn't change to suspended!");
			return true;
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	boolean testCase_resumeApplication() {
		try {
			((MegletHandle)appHandle).resume();
			if (!checkResultFile("RESUME:StorageTestString"))
				throw new Exception(
						"Result of the resume is not RESUME:StorageTestString!");
			if (!waitStateChangeEvent( APPLICATION_RESUME, appDescs[ 0 ].getPID() ) )
				throw new Exception("Didn't resumed the application correctly!");
			if (appHandle.getState() != ApplicationHandle.RUNNING)
				throw new Exception("The status didn't change to running!");
			return true;
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	boolean testCase_stopApplication() {
		try {
			String pid = getAppDesc( appHandle ).getPID();
			appHandle.destroy();
			if (!checkResultFile("STOP"))
				throw new Exception("Result of the stop is not STOP!");

			if( !waitStateChangeEvent( APPLICATION_STOPPING, pid ) )
				throw new Exception("Didn't receive the stopping event!");
			if( !waitStateChangeEvent( APPLICATION_STOPPED, pid ) )
				throw new Exception("Didn't receive the stopped event!");

			ServiceReference[] appList = bc.getServiceReferences(
					"org.osgi.service.application.ApplicationHandle",
					"(application.pid="
							+ getAppDesc( appHandle ).getPID()
							+ ")");
			if (appList != null && appList.length != 0) {
				for (int i = 0; i != appList.length; i++) {
					ApplicationHandle handle = (ApplicationHandle) bc
							.getService(appList[i]);
					bc.ungetService(appList[i]);
					if (handle == appHandle)
						throw new Exception(
								"Application handle doesn't removed after stop!");
				}
			}

			try {
			  appHandle.getState();
			}catch( Exception e ) 
			{
				return true;
			}
		throw new Exception("The status didn't change to NONEXISTENT!");
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean restart_MegletContainer(boolean hasAppInstalled) {
		try {
			megletContainerBundle.stop();
			while (megletContainerBundle.getState() != Bundle.RESOLVED) {
				try {
					Thread.sleep(100);
				}
				catch (InterruptedException e) {}
			}
			ServiceReference[] appList = bc.getServiceReferences(
					"org.osgi.service.application.ApplicationDescriptor",
					"(service.pid=" + installedAppUID + ")");
			if (appList != null && appList.length != 0)
				throw new Exception(
						"Application descriptor doesn't removed after container stop!");
			megletContainerBundle.start();
			while (megletContainerBundle.getState() != Bundle.ACTIVE) {
				try {
					Thread.sleep(100);
				}
				catch (InterruptedException e) {}
			}
			ServiceReference[] references = bc.getServiceReferences(
					"org.osgi.service.application.ApplicationDescriptor",
					"(application.bundle.id=" + installedBundleID + ")");
			if (hasAppInstalled) {
				if (references == null || references.length == 0)
					throw new Exception(
							"ApplicationDescriptor doesn't restored after container restart!");
				appDescs = new ApplicationDescriptor[references.length];
				for (int i = 0; i != references.length; i++) {
					appDescs[i] = (ApplicationDescriptor) bc
							.getService(references[i]);
					bc.ungetService(references[i]);
				}
			}
			else {
				appDescs = null;
				if (references != null && references.length != 0)
					throw new Exception(
							"New ApplicationDescriptor's appeared after container restart!");
			}
			return true;
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean restart_scheduler() {
		try {
			schedulerBundle.stop();
			while (schedulerBundle.getState() != Bundle.RESOLVED) {
				try {
					Thread.sleep(100);
				}
				catch (InterruptedException e) {}
			}
			schedulerBundle.start();
			while (schedulerBundle.getState() != Bundle.ACTIVE) {
				try {
					Thread.sleep(100);
				}
				catch (InterruptedException e) {}
			}
			return true;
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	boolean testCase_launchAfterRestart() {
		try {
			if (!restart_MegletContainer(true))
				return false;
			if (!testCase_checkAppDescs())
				return false;
			if (!testCase_launchApplication())
				return false;
			if (!testCase_stopApplication())
				return false;
			return true;
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	boolean testCase_singletonCheck() {
		try {
			if (!testCase_launchApplication())
				return false;
			ApplicationHandle tmpAppHandle = appHandle;
			boolean launchable = isLaunchable(appDescs[0]);
			if (testCase_launchApplicationError(true))
				return false;
			if (launchable)
				throw new Exception(
						"Application was not launchable, but started!");
			appHandle = tmpAppHandle;
			if (!testCase_stopApplication())
				return false;
			return true;
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	boolean testCase_autoStartCheck() {
		try {
			if (!installMegletBundle("megletsample2.jar"))
				return false;
			ApplicationDescriptor appDesc = appDescs[0];
			ServiceReference[] appList = bc.getServiceReferences(
					"org.osgi.service.application.ApplicationHandle",
					"(application.pid=" + appDesc.getPID() + ")");
			if (appList == null || appList.length == 0)
				throw new Exception(
						"Application didn't autostart. The appHandle is missing!");
			appHandle = (ApplicationHandle) bc.getService(appList[0]);
			if (getAppDesc( appHandle ) != appDesc)
				throw new Exception(
						"The autostarted apphandle differs from the expected one!");
			if (appHandle.getState() != ApplicationHandle.RUNNING)
				throw new Exception(
						"The autostarted application's status is not running!");
			bc.ungetService(appList[0]);
			if (!waitStateChangeEvent( APPLICATION_START, appDesc.getPID() ))
				throw new Exception("Didn't receive the start event!");
			return true;
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	boolean testCase_nonsingletonCheck() {
		try {
			ApplicationDescriptor appDesc = appDescs[0];
			ApplicationHandle oldHandle = appHandle;
			if (!testCase_launchApplication())
				return false;
			if (!testCase_stopApplication())
				return false;
			appHandle = oldHandle;
			appHandle.destroy();
			if( !waitStateChangeEvent( APPLICATION_STOPPING, appDesc.getPID() ) )
				throw new Exception("Didn't receive the stopping event!");
			if( !waitStateChangeEvent( APPLICATION_STOPPED, appDesc.getPID() ) )
				throw new Exception("Didn't receive the stopped event!");
			ServiceReference[] appList = bc.getServiceReferences(
					"org.osgi.service.application.ApplicationHandle",
					"(application.pid="
							+ getAppDesc( appHandle ).getPID()
							+ ")");
			if (appList != null && appList.length != 0) {
				for (int i = 0; i != appList.length; i++) {
					ApplicationHandle handle = (ApplicationHandle) bc
							.getService(appList[i]);
					bc.ungetService(appList[i]);
					if (handle == appHandle)
						throw new Exception(
								"Application handle doesn't removed after destroy!");
				}
			}
			try {
			  appHandle.getState();
			}catch( Exception e )
			{
				return true;
			}
			throw new Exception("The status didn't change to NONEXISTENT!");
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	boolean sendEvent(Event event, boolean asynchron) {
		ServiceReference serviceRef = bc
				.getServiceReference("org.osgi.service.event.EventAdmin");
		if (serviceRef != null) {
			EventAdmin eventAdmin = (EventAdmin) bc.getService(serviceRef);
			if (eventAdmin != null) {
				try {
					if (asynchron)
						eventAdmin.postEvent(event);
					else
						eventAdmin.sendEvent(event);
					return true;
				}
				finally {
					bc.ungetService(serviceRef);
				}
			}
		}
		System.out.println("Can't send the event properly!");
		return false;
	}

	ApplicationHandle lookupAppHandle(ApplicationDescriptor appDesc)
			throws Exception {
		Map props = appDesc.getProperties("en");
		long bundleID = Long.parseLong((String) props
				.get("application.bundle.id"));
		ServiceReference[] references = bc.getServiceReferences(
				"org.osgi.service.application.ApplicationHandle",
				"(application.pid=" + appDesc.getPID() + ")");
		if (references == null || references.length == 0)
			return null;
		for (int i = 0; i != references.length; i++) {
			ApplicationHandle handle = (ApplicationHandle) bc
					.getService(references[i]);
			ApplicationDescriptor desc = getAppDesc( handle );
			bc.ungetService(references[i]);
			if (appDesc == desc)
				return handle;
		}
		return null;
	}

	boolean testCase_lifeCycleChangeByEvents() {
		try {
			ApplicationDescriptor appDesc = appDescs[0];
			if (lookupAppHandle(appDesc) != null)
				throw new Exception(
						"There's a running instance of the appDesc!");
			sendEvent(new Event("com/nokia/megtest/StartEvent", null), false);
			ApplicationHandle handle = lookupAppHandle(appDesc);
			if (handle == null
					|| handle.getState() != ApplicationHandle.RUNNING)
				throw new Exception(
						"Application didn't start for the start event!");
			if (!waitStateChangeEvent( APPLICATION_START, appDesc.getPID() ))
				throw new Exception("Didn't receive the start event!");
			sendEvent(new Event("com/nokia/megtest/SuspendEvent", null), false);
			handle = lookupAppHandle(appDesc);
			if (handle == null
					|| handle.getState() != MegletHandle.SUSPENDED)
				throw new Exception("Application didn't go to SUSPENDED state!");
			if( !waitStateChangeEvent( APPLICATION_SUSPEND, appDescs[ 0 ].getPID() ) )
				throw new Exception("Didn't receive the suspend event!");
			sendEvent(new Event("com/nokia/megtest/ResumeEvent", null), false);
			handle = lookupAppHandle(appDesc);
			if (handle == null
					|| handle.getState() != ApplicationHandle.RUNNING)
				throw new Exception("Application didn't go to RUNNING state!");
			if (!waitStateChangeEvent( APPLICATION_RESUME, appDescs[ 0 ].getPID() ) )
				throw new Exception("Didn't resumed the application correctly!");
			sendEvent(new Event("com/nokia/megtest/StopEvent", null), false);
			handle = lookupAppHandle(appDesc);
			if (handle != null)
				throw new Exception("Application didn't terminate!");
			if( !waitStateChangeEvent( APPLICATION_STOPPING, appDesc.getPID() ) )
				throw new Exception("Didn't receive the stopping event!");
			if( !waitStateChangeEvent( APPLICATION_STOPPED, appDesc.getPID() ) )
				throw new Exception("Didn't receive the stopped event!");
			return true;
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	boolean testCase_requestStop() {
		try {
			ApplicationDescriptor appDesc = appDescs[0];
			if (!testCase_launchApplication())
				return false;
			sendEvent(new Event("com/nokia/megtest/RequestStop", null), false);
			int tries = 0;
			
			try {
				while( true ) {
					appHandle.getState();
					Thread.sleep(100);

					if (tries++ == 50)
						return false;
				}
			}catch (Exception e) {}
			
			if (!checkResultFile("STOP"))
				throw new Exception("Result of the stop is not STOP!");
			appHandle = lookupAppHandle(appDesc);
			if (appHandle != null)
				throw new Exception("Application didn't terminate!");
			if( !waitStateChangeEvent( APPLICATION_STOPPING, appDesc.getPID() ) )
				throw new Exception("Didn't receive the stopping event!");
			if( !waitStateChangeEvent( APPLICATION_STOPPED, appDesc.getPID() ) )
				throw new Exception("Didn't receive the stopped event!");
			return true;
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	boolean testCase_requestSuspend() {
		try {
			ApplicationDescriptor appDesc = appDescs[0];
			if (!testCase_launchApplication())
				return false;
			sendEvent(new Event("com/nokia/megtest/RequestSuspend", null),
					false);
			int tries = 0;
			while (appHandle.getState() != MegletHandle.SUSPENDED) {
				try {
					Thread.sleep(100);
				}
				catch (InterruptedException e) {}
				if (tries++ == 50)
					return false;
			}
			if (!checkResultFile("SUSPEND:StorageTestString"))
				throw new Exception("Result of the stop is not STOP!");
			if( !waitStateChangeEvent( APPLICATION_SUSPEND, appDescs[ 0 ].getPID() ) )
				throw new Exception("Didn't receive the suspend event!");

			appHandle.destroy();
			
			if( !waitStateChangeEvent( APPLICATION_STOPPED_AFTER_SUSPEND, appDescs[ 0 ].getPID() ) )
				throw new Exception("Didn't receive the stopped event!");

			ServiceReference[] appList = bc.getServiceReferences(
					"org.osgi.service.application.ApplicationHandle",
					"(application.pid="
							+ getAppDesc( appHandle ).getPID()
							+ ")");
			if (appList != null && appList.length != 0) {
				for (int i = 0; i != appList.length; i++) {
					ApplicationHandle handle = (ApplicationHandle) bc
							.getService(appList[i]);
					bc.ungetService(appList[i]);
					if (handle == appHandle)
						throw new Exception(
								"Application handle doesn't removed after stop!");
				}
			}

			try {
				appHandle.getState();
			}catch( Exception e )
			{
				return true;
			}
			throw new Exception("The status didn't change to NONEXISTENT!");
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	boolean testCase_eventListening() {
		try {
			if (!testCase_launchApplication())
				return false;
			sendEvent(new Event("com/nokia/megtest/ListenerEvent", null), false);
			if (!checkResultFile("EVENT LISTENED"))
				throw new Exception(
						"Result of the listener event is not EVENT LISTENED!");
			if (!testCase_stopApplication())
				return false;
			return true;
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	boolean testCase_eventChannelLogService() {
		try {
			if (!testCase_launchApplication())
				return false;
			
			deleteEventQueue();
			
			sendEvent(new Event("com/nokia/megtest/EchoEvent", null), false);
			if (!checkResultFile("EVENT ECHOED"))
				throw new Exception(
						"Result of the listener event is not EVENT ECHOED!");
			if (!checkEvent("com/nokia/megtest/EchoEvent"))
				return false;
			if (!checkEvent("com/nokia/megtest/EchoReplyEvent"))
				return false;
			sendEvent(new Event("com/nokia/megtest/LogEvent", null), false);
			if (!checkResultFile("EVENT LOGGED"))
				throw new Exception(
						"Result of the listener event is not EVENT LOGGED!");
			if (!testCase_stopApplication())
				return false;
			return true;
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	boolean testCase_registerForEvents() {
		try {
			if (!testCase_launchApplication())
				return false;
			sendEvent(new Event("com/nokia/megtest/RegisteredEvent", null),
					false);
			if (!checkResultFile(""))
				throw new Exception(
						"Result of the listener event is not empty which is invalid!");
			Hashtable props = new Hashtable();
			props.put("task", "subscribe");
			sendEvent(new Event("com/nokia/megtest/SubscribeEvent", props),
					false);
			if (!checkResultFile("EVENT SUBSCRIBED"))
				throw new Exception("Cannot subscribe for an event!");
			sendEvent(new Event("com/nokia/megtest/RegisteredEvent", null),
					false);
			if (!checkResultFile("EVENT REGISTERED"))
				throw new Exception("Subscibe was unsuccessful!");
			props.put("task", "unsubscribe");
			sendEvent(new Event("com/nokia/megtest/SubscribeEvent", props),
					false);
			if (!checkResultFile("EVENT SUBSCRIBED"))
				throw new Exception("Cannot unsubscribe from an event!");
			sendEvent(new Event("com/nokia/megtest/RegisteredEvent", null),
					false);
			if (!checkResultFile(""))
				throw new Exception("Unsubscribe was unsuccessful!");
			if (!testCase_stopApplication())
				return false;
			return true;
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	String getFilterFromNow( int delaySec ) {
		Calendar calendar = Calendar.getInstance();
		calendar.add( Calendar.SECOND, delaySec );
		
		String filter = "(&";
		filter += "(year=" 		+ calendar.get( Calendar.YEAR ) +")";
		filter += "(month=" 	+	calendar.get( Calendar.MONTH ) +")";
		filter += "(day=" 		+	calendar.get( Calendar.DAY_OF_MONTH ) +")";
		filter += "(hour=" 		+	calendar.get( Calendar.HOUR_OF_DAY ) +")";
		filter += "(minute="	+ calendar.get( Calendar.MINUTE ) +")";
		filter += "(second="	+ calendar.get( Calendar.SECOND ) +")";
		filter += ")";
		
		return filter;
	}

	boolean testCase_scheduleAnApplication() {
		try {
			ApplicationDescriptor appDesc = appDescs[0];
			if (lookupAppHandle(appDesc) != null)
				throw new Exception(
						"There's a running instance of the appDesc!");
			Map args = createArgs();
			if (args == null)
				throw new Exception("Cannot create the arguments of launch!");
			appDesc.schedule(args, "org/osgi/timer", getFilterFromNow( 2 ), false);
			Thread.sleep(3000);
			appHandle = lookupAppHandle(appDesc);
			if (appHandle == null
					|| appHandle.getState() != ApplicationHandle.RUNNING)
				throw new Exception("Application wasn't scheduled!");
			if (!checkResultFile("START"))
				throw new Exception("Result of the schedule is not START!");
			if (!waitStateChangeEvent( APPLICATION_START, appDesc.getPID() ))
				throw new Exception("Didn't receive the start event!");
			if (!testCase_stopApplication())
				return false;
			return true;
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	boolean testCase_scheduleTwoApplications() {
		try {
			ApplicationDescriptor appDesc = appDescs[0];
			if (lookupAppHandle(appDesc) != null)
				throw new Exception(
						"There's a running instance of the appDesc!");
			Map args = createArgs();
			if (args == null)
				throw new Exception("Cannot create the arguments of launch!");
			appDesc.schedule(args, "org/osgi/timer", getFilterFromNow( 4 ), false);
			appDesc.schedule(args, "org/osgi/timer", getFilterFromNow( 2 ), false);
			Thread.sleep(3000);
			appHandle = lookupAppHandle(appDesc);
			if (appHandle == null
					|| appHandle.getState() != ApplicationHandle.RUNNING)
				throw new Exception("Application wasn't scheduled!");
			if (!checkResultFile("START"))
				throw new Exception("Result of the schedule is not START!");
			if (!waitStateChangeEvent( APPLICATION_START, appDesc.getPID() ))
				throw new Exception("Didn't receive the start event!");
			if (!testCase_stopApplication())
				return false;
			Thread.sleep(2000);
			appHandle = lookupAppHandle(appDesc);
			if (appHandle == null
					|| appHandle.getState() != ApplicationHandle.RUNNING)
				throw new Exception("Application wasn't scheduled!");
			if (!checkResultFile("START"))
				throw new Exception("Result of the schedule is not START!");
			if (!waitStateChangeEvent( APPLICATION_START, appDesc.getPID() ))
				throw new Exception("Didn't receive the start event!");
			if (!testCase_stopApplication())
				return false;
			return true;
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean testCase_scheduleApplicationsWithAARestart() {
		try {
			ApplicationDescriptor appDesc = appDescs[0];
			if (lookupAppHandle(appDesc) != null)
				throw new Exception(
						"There's a running instance of the appDesc!");
			Map args = createArgs();
			if (args == null)
				throw new Exception("Cannot create the arguments of launch!");
			appDesc.schedule(args, "org/osgi/timer", getFilterFromNow( 3 ), false);
			if (!restart_scheduler())
				return false;
			Thread.sleep(4000);
			appHandle = lookupAppHandle(appDesc);
			if (appHandle == null
					|| appHandle.getState() != ApplicationHandle.RUNNING)
				throw new Exception("Application wasn't scheduled!");
			if (!checkResultFile("START"))
				throw new Exception("Result of the schedule is not START!");
			if (!waitStateChangeEvent( APPLICATION_START, appDesc.getPID() ))
				throw new Exception("Didn't receive the start event!");
			if (!testCase_stopApplication())
				return false;
			return true;
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean testCase_removeScheduledApplication() {
		try {
			ApplicationDescriptor appDesc = appDescs[0];
			if (lookupAppHandle(appDesc) != null)
				throw new Exception(
						"There's a running instance of the appDesc!");
			Map args = createArgs();
			if (args == null)
				throw new Exception("Cannot create the arguments of launch!");
			ServiceReference serviceRef = appDesc.
					schedule(args, "org/osgi/timer", getFilterFromNow( 2 ), false);
			ScheduledApplication schedApp = (ScheduledApplication)bc.getService( serviceRef );
			schedApp.remove();
			bc.ungetService( serviceRef );
			Thread.sleep(3000);
			appHandle = lookupAppHandle(appDesc);
			if (appHandle != null )
				throw new Exception("Application was scheduled inspite of removing!");
			return true;
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean testCase_recurringSchedule() {
		try {
			ApplicationDescriptor appDesc = appDescs[0];
			if (lookupAppHandle(appDesc) != null)
				throw new Exception(
						"There's a running instance of the appDesc!");
			Map args = createArgs();
			if (args == null)
				throw new Exception("Cannot create the arguments of launch!");
			appDesc.schedule(args, "com/nokia/test/ScheduleEvent", null, false);
			
			sendEvent(new Event("com/nokia/test/ScheduleEvent", null), false);
			
			appHandle = lookupAppHandle(appDesc);
			if (appHandle == null
					|| appHandle.getState() != ApplicationHandle.RUNNING)
				throw new Exception("Application wasn't scheduled!");
			if (!checkResultFile("START"))
				throw new Exception("Result of the schedule is not START!");
			if (!waitStateChangeEvent( APPLICATION_START, appDesc.getPID() ))
				throw new Exception("Didn't receive the start event!");
			if (!testCase_stopApplication())
				return false;
			
			sendEvent(new Event("com/nokia/test/ScheduleEvent", null), false);
			appHandle = lookupAppHandle(appDesc);
			if (appHandle != null )
				throw new Exception("Application was scheduled inspite of non-recurring!");

			ServiceReference serviceRef = appDesc.
			    schedule(args, "com/nokia/test/ScheduleEvent", null, true);
			sendEvent(new Event("com/nokia/test/ScheduleEvent", null), false);
			
			appHandle = lookupAppHandle(appDesc);
			if (appHandle == null
					|| appHandle.getState() != ApplicationHandle.RUNNING)
				throw new Exception("Application wasn't scheduled!");
			if (!checkResultFile("START"))
				throw new Exception("Result of the schedule is not START!");
			if (!waitStateChangeEvent( APPLICATION_START, appDesc.getPID() ))
				throw new Exception("Didn't receive the start event!");
			if (!testCase_stopApplication())
				return false;

			sendEvent(new Event("com/nokia/test/ScheduleEvent", null), false);
			
			appHandle = lookupAppHandle(appDesc);
			if (appHandle == null
					|| appHandle.getState() != ApplicationHandle.RUNNING)
				throw new Exception("Application wasn't scheduled!");
			if (!checkResultFile("START"))
				throw new Exception("Result of the schedule is not START!");
			if (!waitStateChangeEvent( APPLICATION_START, appDesc.getPID() ))
				throw new Exception("Didn't receive the start event!");
			if (!testCase_stopApplication())
				return false;
			
			ScheduledApplication schedApp = (ScheduledApplication)bc.getService( serviceRef );
			schedApp.remove();
			bc.ungetService( serviceRef );
			return true;
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean testCase_schedulerFilterMatching() {
		try {
			ApplicationDescriptor appDesc = appDescs[0];
			if (lookupAppHandle(appDesc) != null)
				throw new Exception(
						"There's a running instance of the appDesc!");
			Map args = createArgs();
			if (args == null)
				throw new Exception("Cannot create the arguments of launch!");
			appDesc.schedule(args, "com/nokia/test/ScheduleEvent", "(propi=hallo)", false);
			
			Hashtable propi = new Hashtable();
			propi.put( "propi", "hello" );
			
			sendEvent(new Event("com/nokia/test/ScheduleEvent", null), false);
			sendEvent(new Event("com/nokia/test/ScheduleEvent", propi), false);
			appHandle = lookupAppHandle(appDesc);
			if (appHandle != null )
				throw new Exception("Application was scheduled inspite of non-recurring!");
			
			propi.put( "propi", "hallo" );
			
			sendEvent(new Event("com/nokia/test/ScheduleEvent", propi), false);
			
			appHandle = lookupAppHandle(appDesc);
			if (appHandle == null
					|| appHandle.getState() != ApplicationHandle.RUNNING)
				throw new Exception("Application wasn't scheduled!");
			if (!checkResultFile("START"))
				throw new Exception("Result of the schedule is not START!");
			if (!waitStateChangeEvent( APPLICATION_START, appDesc.getPID() ))
				throw new Exception("Didn't receive the start event!");
			if (!testCase_stopApplication())
				return false;
			return true;
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean testCase_lockApplication() {
		try {
			ApplicationDescriptor appDesc = appDescs[0];
			if (appDesc.isLocked())
				throw new Exception("Application is locked and cannot launch!");
			appDesc.lock();
			if (!appDesc.isLocked())
				throw new Exception("Lock doesn't work!");
			if (!appDesc.getProperties("en").get("application.locked").equals(
					"true"))
				throw new Exception("Lock property is incorrect!");
			boolean launchable = isLaunchable(appDesc);
			boolean started = false;
			try {
				ServiceReference appHandleRef = appDesc.launch(new Hashtable());
				appHandle = (ApplicationHandle)bc.getService( appHandleRef );
				bc.ungetService( appHandleRef );
				started = true;
			}catch (Exception e) {}
			
			if (started)
				throw new Exception("Application was launched inspite of lock!");
			appDesc.unlock();
			if (!appDesc.getProperties("en").get("application.locked").equals(
					"false"))
				throw new Exception("Lock property is incorrect!");
			if (appDesc.isLocked())
				throw new Exception("Unlock doesn't work!");
			if (launchable)
				throw new Exception(
						"Application was not launchable but started!");
			if (!testCase_launchApplication())
				return false;
			return testCase_stopApplication();
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean testCase_saveLockingState() {
		try {
			ApplicationDescriptor appDesc = appDescs[0];
			if (appDesc.isLocked())
				throw new Exception("Application is locked and cannot launch!");
			if (!restart_MegletContainer(true))
				return false;
			appDesc = appDescs[0];
			if (!testCase_launchApplication())
				return false;
			if (appDesc.isLocked())
				throw new Exception("Application is locked and cannot launch!");
			appDesc.lock();
			if (!appDesc.getProperties("en").get("application.locked").equals(
					"true"))
				throw new Exception("Lock property is incorrect!");
			if (!appDesc.isLocked())
				throw new Exception("Lock doesn't work!");
			if (!restart_MegletContainer(true))
				return false;
			appDesc = appDescs[0];
			if (!appDesc.isLocked())
				throw new Exception("Lock doesn't work after restart!");
			boolean launchable = isLaunchable(appDesc);
			boolean started = false;
			try {
				ServiceReference appHandleRef = appDesc.launch(new Hashtable());
				appHandle = (ApplicationHandle)bc.getService( appHandleRef );
				bc.ungetService( appHandleRef );
				started = true;
			}
			catch (Exception e) {}
			if (started)
				throw new Exception("Application was launched inspite of lock!");
			appDesc.unlock();
			if (!appDesc.getProperties("en").get("application.locked").equals(
					"false"))
				throw new Exception("Lock property is incorrect!");
			if (launchable)
				throw new Exception(
						"Application was not launchable but started!");
			if (appDesc.isLocked())
				throw new Exception("Unlock doesn't work!");
			return true;
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean testCase_resumingAfterLocking() {
		try {
			ApplicationDescriptor appDesc = appDescs[0];

			if (!testCase_launchApplication())
				return false;
			if (!testCase_suspendApplication())
				return false;
			
			appDesc.lock();
			if (!appDesc.isLocked())
				throw new Exception("Lock doesn't work!");
			if (!appDesc.getProperties("en").get("application.locked").equals(
					"true"))
				throw new Exception("Lock property is incorrect!");

			if(!testCase_resumeApplication())
				return false;
			
			appDesc.unlock();
			if (appDesc.isLocked())
				throw new Exception("Unlock doesn't work!");
			if (!appDesc.getProperties("en").get("application.locked").equals(
					"false"))
				throw new Exception("Lock property is incorrect!");
			
			return testCase_stopApplication();
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}		
	}
}