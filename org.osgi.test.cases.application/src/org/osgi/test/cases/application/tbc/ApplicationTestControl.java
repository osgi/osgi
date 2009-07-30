/*
 * Copyright (c) The OSGi Alliance (2004). All Rights Reserved. Implementation
 * of certain elements of the OSGi Specification may be subject to third party
 * intellectual property rights, including without limitation, patent rights
 * (such a third party may or may not be a member of the OSGi Alliance). The
 * OSGi Alliance is not responsible and shall not be held responsible in any
 * manner for identifying or failing to identify any or all such third party
 * intellectual property rights. This document and the information contained
 * herein are provided on an "AS IS" basis and THE OSGI ALLIANCE DISCLAIMS ALL
 * WARRANTIES, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO ANY WARRANTY
 * THAT THE USE OF THE INFORMATION HEREIN WILL NOT INFRINGE ANY RIGHTS AND ANY
 * IMPLIED WARRANTIES OF MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE. IN
 * NO EVENT WILL THE OSGI ALLIANCE BE LIABLE FOR ANY LOSS OF PROFITS, LOSS OF
 * BUSINESS, LOSS OF USE OF DATA, INTERRUPTION OF BUSINESS, OR FOR DIRECT,
 * INDIRECT, SPECIAL OR EXEMPLARY, INCIDENTIAL, PUNITIVE OR CONSEQUENTIAL
 * DAMAGES OF ANY KIND IN CONNECTION WITH THIS DOCUMENT OR THE INFORMATION
 * CONTAINED HEREIN, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH LOSS OR DAMAGE.
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */
/*
 * REVISION HISTORY:
 *
 * Date         Author(s)
 * CR           Headline
 * ===========  ==============================================================
 * 24/08/2005   Alexandre Santos
 * 153          Implement OAT test cases  
 * ===========  ==============================================================
 */

package org.osgi.test.cases.application.tbc;

import info.dmtree.DmtAdmin;
import info.dmtree.DmtSession;
import info.dmtree.Uri;

import java.io.FilePermission;
import java.lang.reflect.ReflectPermission;
import java.net.SocketPermission;
import java.util.Hashtable;

import org.osgi.framework.AdminPermission;
import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.PackagePermission;
import org.osgi.framework.ServicePermission;
import org.osgi.framework.ServiceReference;
import org.osgi.service.application.ApplicationAdminPermission;
import org.osgi.service.application.ApplicationDescriptor;
import org.osgi.service.application.ApplicationHandle;
import org.osgi.service.application.ScheduledApplication;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.TopicPermission;
import org.osgi.service.permissionadmin.PermissionAdmin;
import org.osgi.service.permissionadmin.PermissionInfo;
import org.osgi.test.cases.application.tbc.ApplicationAdminPermission.ApplicationAdminPermissionConstants;
import org.osgi.test.cases.application.tbc.ApplicationAdminPermission.Equals;
import org.osgi.test.cases.application.tbc.ApplicationAdminPermission.HashCode;
import org.osgi.test.cases.application.tbc.ApplicationAdminPermission.Implies;
import org.osgi.test.cases.application.tbc.ApplicationAdminPermission.SetCurrentApplicationId;
import org.osgi.test.cases.application.tbc.ApplicationContext.AddServiceListener;
import org.osgi.test.cases.application.tbc.ApplicationContext.GetApplicationId;
import org.osgi.test.cases.application.tbc.ApplicationContext.GetInstanceId;
import org.osgi.test.cases.application.tbc.ApplicationContext.GetServiceProperties;
import org.osgi.test.cases.application.tbc.ApplicationContext.GetStartupParameters;
import org.osgi.test.cases.application.tbc.ApplicationContext.LocateService;
import org.osgi.test.cases.application.tbc.ApplicationContext.LocateServices;
import org.osgi.test.cases.application.tbc.ApplicationContext.RegisterService;
import org.osgi.test.cases.application.tbc.ApplicationException.ApplicationException;
import org.osgi.test.cases.application.tbc.ApplicationException.ApplicationExceptionConstants;
import org.osgi.test.cases.application.tbc.Event.EventHandlerActivator;
import org.osgi.test.cases.application.tbc.Framework.GetApplicationContext;
import org.osgi.test.cases.application.tbc.Others.ApplicationServiceEvent;
import org.osgi.test.cases.application.tbc.TreeStructure.TreeStructure;
import org.osgi.test.cases.application.tbc.UseCases.LifecycleStates;
import org.osgi.test.cases.application.tbc.util.TestAppControllerImpl;
import org.osgi.test.cases.application.tbc.util.TestingActivator;
import org.osgi.test.cases.application.tbc.util.TestingActivatorImplHigh;
import org.osgi.test.cases.application.tbc.util.TestingActivatorImplLow;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

/**
 * Controls the execution of the test case
 */
public class ApplicationTestControl extends DefaultTestBundleControl {

	private static String					tb2Location;

	private static TestInterface[]			testInterfaces;

	private static PermissionAdmin			permissionAdmin;

	private static ApplicationDescriptor	appDescriptor;

	private static Bundle					bundleTestApplication;

	private static Bundle					testBundle;

	private static DmtAdmin					dmtAdmin;

	private static boolean					isUnregistered;

	private static boolean					isRegistered;

	private static boolean					isModified;

	private static TestAppControllerImpl	appController;

	private static PermissionWorker			worker;

	private static EventHandlerActivator	eventBundle;

	private static TestingActivator			testingActivator;

	private static TestingActivator			testingActivator2;

	public void prepare() {
		try {
			log("#prepare");
			dmtAdmin = (DmtAdmin) getContext().getService(
					getContext().getServiceReference(DmtAdmin.class.getName()));
			permissionAdmin = (PermissionAdmin) getContext().getService(
					getContext().getServiceReference(
							PermissionAdmin.class.getName()));
			eventBundle = new EventHandlerActivator(this);
			eventBundle.start(getContext());
			installBundle("tb2.jar");
			installDescriptor();
			ServiceReference srvReference = getContext().getServiceReference(
					TB2Service.class.getName());
			tb2Location = srvReference.getBundle().getLocation();
			TB2Service tcb1Service = (TB2Service) getContext().getService(
					srvReference);
			testInterfaces = tcb1Service.getTestClasses(this);
			appController = new TestAppControllerImpl();
			appController.start(this.getContext());
			startPermissionWorker();
		} catch (Exception e) {
			this.log("Unexpected exception at prepare." + e.toString() + " : " + e.getMessage());
		}
	}

	private void updateConstants() {
	    ApplicationConstants.OSGI_APPLICATION_APPID = ApplicationConstants.OSGI_APPLICATION + "/" + Uri.mangle(ApplicationConstants.TEST_PID);
	    ApplicationConstants.OSGI_APPLICATION_APPID_NAME = ApplicationConstants.OSGI_APPLICATION_APPID + "/Name";
	    ApplicationConstants.OSGI_APPLICATION_APPID_VALID = ApplicationConstants.OSGI_APPLICATION_APPID + "/Valid";
	    ApplicationConstants.OSGI_APPLICATION_APPID_APPLICATION_ID = ApplicationConstants.OSGI_APPLICATION_APPID + "/ApplicationID";
	    ApplicationConstants.OSGI_APPLICATION_APPID_ICONURI = ApplicationConstants.OSGI_APPLICATION_APPID + "/IconURI";
	    ApplicationConstants.OSGI_APPLICATION_APPID_VENDOR = ApplicationConstants.OSGI_APPLICATION_APPID + "/Vendor";
	    ApplicationConstants.OSGI_APPLICATION_APPID_VERSION = ApplicationConstants.OSGI_APPLICATION_APPID + "/Version";
	    ApplicationConstants.OSGI_APPLICATION_APPID_LOCKED = ApplicationConstants.OSGI_APPLICATION_APPID + "/Locked";
	    ApplicationConstants.OSGI_APPLICATION_APPID_CONTAINERID = ApplicationConstants.OSGI_APPLICATION_APPID + "/ContainerID";
	    ApplicationConstants.OSGI_APPLICATION_APPID_PACKAGEID = ApplicationConstants.OSGI_APPLICATION_APPID + "/PackageID";
	    ApplicationConstants.OSGI_APPLICATION_APPID_LOCATION = ApplicationConstants.OSGI_APPLICATION_APPID + "/Location";
	    ApplicationConstants.OSGI_APPLICATION_APPID_EXT = ApplicationConstants.OSGI_APPLICATION_APPID + "/Ext";    
	    ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS = ApplicationConstants.OSGI_APPLICATION_APPID + "/Operations";
	    ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES = ApplicationConstants.OSGI_APPLICATION_APPID + "/Schedules";
	    ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_LOCK = ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS + "/Lock";
	    ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_UNLOCK = ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS + "/Unlock";
	    ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH = ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS + "/Launch";
	    ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID = ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH + "/Cesar";
	    ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID_RESULT = ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID + "/Result";
	    ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID_RESULT_INSTANCEID = ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID_RESULT + "/InstanceID";
	    ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID_RESULT_STATUS = ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID_RESULT + "/Status";
	    ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID_RESULT_MESSAGE = ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID_RESULT + "/Message";
	    ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID_ARGUMENTS = ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID + "/Arguments";
	    ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID_ARGUMENTS_ID = ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID_ARGUMENTS + "/1";
	    ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID_ARGUMENTS_ID_NAME = ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID_ARGUMENTS_ID + "/Name";
	    ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID_ARGUMENTS_ID_VALUE = ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID_ARGUMENTS_ID + "/Value";
	    ApplicationConstants.OSGI_APPLICATION_APPID_INSTANCES = ApplicationConstants.OSGI_APPLICATION_APPID + "/Instances";
	    ApplicationConstants.OSGI_APPLICATION_APPID_INSTANCES_ID_STATE = ApplicationConstants.OSGI_APPLICATION_APPID_INSTANCES_ID + "/State";
	    ApplicationConstants.OSGI_APPLICATION_APPID_INSTANCES_ID_INSTANCEID = ApplicationConstants.OSGI_APPLICATION_APPID_INSTANCES_ID + "/InstanceID";
	    ApplicationConstants.OSGI_APPLICATION_APPID_INSTANCES_ID_OPERATIONS = ApplicationConstants.OSGI_APPLICATION_APPID_INSTANCES_ID + "/Operations";
	    ApplicationConstants.OSGI_APPLICATION_APPID_INSTANCES_ID_OPERATIONS_STOP = ApplicationConstants.OSGI_APPLICATION_APPID_INSTANCES_ID_OPERATIONS + "/Stop";
	    ApplicationConstants.OSGI_APPLICATION_APPID_INSTANCES_ID_OPERATIONS_EXT = ApplicationConstants.OSGI_APPLICATION_APPID_INSTANCES_ID_OPERATIONS + "/Ext";
	    ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID = ApplicationConstants.OSGI_APPLICATION_APPID + "/Schedule/Cesar";
	    ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID_ARGUMENTS = ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID + "Cesar/Arguments";
	    ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID_ENABLED = ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID + "Cesar/Enabled";
	    ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID_TOPICFILTER = ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID + "Cesar/TopicFilter";
	    ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID_EVENTFILTER = ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID + "Cesar/EventFilter";
	    ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID_RECURRING = ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID + "Cesar/Recurring";		
	}

  private void startPermissionWorker() {
    worker = new PermissionWorker(this);
    worker.start();
    //make sure the thread has started
    synchronized (worker) {
      while (!worker.isRunning()) {
        try {
          worker.wait(50);
        } catch (InterruptedException ie) {
        }
      }
    }
  }

	public void setLocalPermission(PermissionInfo permission) {
		PermissionInfo[] defaults = new PermissionInfo[] {
				new PermissionInfo(SocketPermission.class.getName(), "*",
						"accept,connect,listen,resolve"),
				new PermissionInfo(ReflectPermission.class.getName(), "*", "*"),
				new PermissionInfo(RuntimePermission.class.getName(), "*", "*"),
				new PermissionInfo(FilePermission.class.getName(),
						"<<ALL FILES>>", "read, write, execute, delete"),
				new PermissionInfo(AdminPermission.class.getName(), "*", "*"),
				new PermissionInfo(PackagePermission.class.getName(), "*",
						"EXPORT, IMPORT"),
						new PermissionInfo(TopicPermission.class.getName(), "*",
						"PUBLISH, SUBSCRIBE"),
				new PermissionInfo(ServicePermission.class.getName(), "*",
						"GET,REGISTER"), permission };

		setPermission(defaults);
	}

  public void setPermission(PermissionInfo[] permissions) {
    synchronized (worker) {
      worker.setLocation(getTb2Location());
      worker.setPermissions(permissions);
      worker.notifyAll();
      long start = System.currentTimeMillis();
      while (worker.isWorking() && (System.currentTimeMillis() - start < 10000)) {
        try {
          worker.wait(1000);
        } catch (InterruptedException e) {}
      }
    }
  }

	public void setDefaultPermission() {
		PermissionInfo[] defaults = new PermissionInfo[] {
				new PermissionInfo(SocketPermission.class.getName(), "*",
						"accept,connect,listen,resolve"),
				new PermissionInfo(ReflectPermission.class.getName(), "*", "*"),
				new PermissionInfo(RuntimePermission.class.getName(), "*", "*"),
				new PermissionInfo(FilePermission.class.getName(),
						"<<ALL FILES>>", "read, write, execute, delete"),
				new PermissionInfo(AdminPermission.class.getName(), "*", "*"),
				new PermissionInfo(PackagePermission.class.getName(), "*",
						"EXPORT, IMPORT"),
				new PermissionInfo(ServicePermission.class.getName(), "*",
						"GET,REGISTER") };
		setPermission(defaults);
	}

	public String getTb2Location() {
		return tb2Location;
	}

	public void testApplicationDescriptorLock() {
		testInterfaces[0].run();
	}

	public void testApplicationDescriptorUnLock() {
		testInterfaces[1].run();
	}

	/**
	 * Executes Tcs for MidletHandle#GetApplicationDescriptor
	 */
	public void testMidletHandleGetApplicationDescriptor() {
		testInterfaces[2].run();
	}

	/**
	 * Executes Tcs for MidletHandle#GetInstanceId
	 */
	public void testMidletHandleGetInstanceID() {
		testInterfaces[3].run();
	}

	/**
	 * Executes Tcs for MidletHandle#GetState
	 */
	public void testMidletHandleGetState() {
		testInterfaces[4].run();
	}

	/**
	 * Executes Tcs for MidletDescriptor#GetProperties
	 */
	public void testMidletDescriptorGetProperties() {
		testInterfaces[5].run();
	}

	/**
	 * Executes Tcs for MidletDescriptor#Launch
	 */
	public void testMidletDescriptorLaunch() {
		testInterfaces[6].run();
	}

	/**
	 * Executes Tcs for TreeStructure
	 */
	public void testTreeStructure() {
		new TreeStructure(this).run();
	}

	/**
	 * Executes Tcs for MidletDescriptor#schedule
	 */
	public void testMidletDescriptorSchedule() {
		testInterfaces[8].run();
	}

	/**
	 * Executes Tcs for ScheduledApplication#getTopic
	 */
	public void testScheduledApplicationGetTopic() {
		testInterfaces[9].run();
	}

	/**
	 * Executes Tcs for ScheduledApplication#getEventFilter
	 */
	public void testScheduledApplicationGetEventFilter() {
		testInterfaces[10].run();
	}

	/**
	 * Executes Tcs for ScheduledApplication#isRecurring
	 */
	public void testScheduledApplicationIsRecurring() {
		testInterfaces[11].run();
	}

	/**
	 * Executes Tcs for ScheduledApplication#getArguments
	 */
	public void testScheduledApplicationGetArguments() {
		testInterfaces[12].run();
	}

	/**
	 * Executes Tcs for ScheduledApplication#getApplicationDescriptor
	 */
	public void testScheduledApplicationGetApplicationDescriptor() {
		testInterfaces[13].run();
	}

	/**
	 * Executes Tcs for ScheduledApplication#remove
	 */
	public void testScheduledApplicationRemove() {
		testInterfaces[14].run();
	}

	/**
	 * Executes Tcs for MidletHandle#Destroy
	 */
	public void testMidletHandleDestroy() {
		testInterfaces[7].run();
	}

	/**
	 * Executes Tcs for constants
	 */
	public void testApplicationDescriptorConstants() {
		testInterfaces[15].run();
	}

	/**
	 * Executes Tcs for constants
	 */
	public void testApplicationHandleConstants() {
		testInterfaces[16].run();
	}

	/**
	 * Executes Tcs for constants
	 */
	public void testGetApplicationId() {
		testInterfaces[17].run();
	}

	/**
	 * Executes use cases
	 */
	public void testLifecycleStates() {
		new LifecycleStates(this).run();
	}

	/**
	 * Executes Tcs for constants
	 */
	public void testApplicationAdminPermissionConstants() throws Exception {
		new ApplicationAdminPermissionConstants(this).run();
	}

	/**
	 * Executes Tcs for ApplicationAdminPermission
	 */
	public void testApplicationAdminPermission() {
		new org.osgi.test.cases.application.tbc.ApplicationAdminPermission.ApplicationAdminPermission(
				this).run();
	}
	
	/**
	 * Executes Tcs for
	 * ApplicationException#ApplicationException
	 */
	public void testApplicationException() {
		new ApplicationException(this).run();
	}
	
	/**
	 * Executes Tcs for
	 * ApplicationException#ApplicationExceptionConstants
	 */
	public void testApplicationExceptionConstants() {
		new ApplicationExceptionConstants(this).run();
	}	

	/**
	 * Executes Tcs for
	 * ApplicationContext#addServiceListener,removeServiceListener
	 */
	public void testAddServiceListener() {
		new AddServiceListener(this).run();
	}

	/**
	 * Executes Tcs for ApplicationContext#getStartupParameters
	 */
	public void testGetStartupParameters() {
		new GetStartupParameters(this).run();
	}

	/**
	 * Executes Tcs for ApplicationContext#locateService
	 */
	public void testLocateService() {
		new LocateService(this).run();
	}
	
	public void sendEvent(String topic) {
		EventAdmin event = (EventAdmin) getContext().getService(getContext().getServiceReference(EventAdmin.class.getName()));
		event.sendEvent(new Event(topic, new Hashtable()));	
	}

	/**
	 * Executes Tcs for ApplicationContext#locateServices
	 */
	public void testLocateServices() {
		new LocateServices(this).run();
	}

	/**
	 * Executes Tcs for ApplicationContext#registerService
	 */
	public void testRegisterService() {
		new RegisterService(this).run();
	}

	/**
	 * Executes Tcs for ApplicationContext#getServiceProperties
	 */
	public void testGetServiceProperties() {
		new GetServiceProperties(this).run();
	}

	/**
	 * Executes Tcs for Framework#getApplicationContext
	 */
	public void testGetApplicationContext() {
		new GetApplicationContext(this).run();
	}

	/**
	 * Executes Tcs for ApplicationDescriptor#MatchDNChain
	 */
	public void testMatchDNChain() {
		testInterfaces[18].run();
	}
	
	/**
	 * Executes Tcs for ScheduledApplication#getScheduleId
	 */
	public void testGetScheduleId() {
		testInterfaces[19].run();
	}	

	/**
	 * Executes Tcs for ApplicationAdminPermission#setCurrentApplicationId
	 */
	public void testSetCurrentApplicationId() {
		new SetCurrentApplicationId(this).run();
	}

	/**
	 * Executes Tcs for ApplicationAdminPermission#implies
	 */
	public void testApplicationAdminPermissionImplies() {
		new Implies(this).run();
	}

	/**
	 * Executes Tcs for ApplicationAdminPermission#equals
	 */
	public void testApplicationAdminPermissionEquals() {
		new Equals(this).run();
	}

	/**
	 * Executes Tcs for ApplicationAdminPermission#hashCode
	 */
	public void testApplicationAdminPermissionHashCode() {
		new HashCode(this).run();
	}

	/**
	 * Executes Tcs for ApplicationContext#getApplicationId
	 */
	public void testApplicationContextGetApplicationId() {
		new GetApplicationId(this).run();
	}

	/**
	 * Executes Tcs for ApplicationServiceEvent
	 */
	public void testApplicationServiceEvent() {
		new ApplicationServiceEvent(this).run();
	}

	/**
	 * Executes Tcs for ApplicationContext#getInstanceId
	 */
	public void testApplicationContextGetInstanceId() {
		new GetInstanceId(this).run();
	}

	public PermissionAdmin getPermissionAdmin() {
		return permissionAdmin;
	}

	/**
	 * @return Returns the appDescriptor.
	 */
	public ApplicationDescriptor getAppDescriptor() {
		return appDescriptor;
	}

	public void installDescriptor() {
		ServiceReference[] appDescRefsOld, appDescRefsNew;
		try {
			appDescRefsOld = getContext().getServiceReferences("org.osgi.service.application.ApplicationDescriptor", null);
			bundleTestApplication = installBundle("tb1.jar");
			if (bundleTestApplication == null) {
				fail("The tb1 installation returns null as bundle.");
			}
			bundleTestApplication.start();
			synchronized (this) {
				this.wait(ApplicationConstants.SHORT_TIMEOUT);
			}
			
			
			appDescRefsNew = getContext().getServiceReferences("org.osgi.service.application.ApplicationDescriptor", null);
			
			if (appDescRefsOld == null) {
				if (appDescRefsNew == null) {
					fail("After the tb1 installation, no descriptor was registered. Check for the xml parser.");
				}
				appDescriptor = (ApplicationDescriptor) getContext().getService(appDescRefsNew[0]);
				log("#other line");
				updateTestPid((String) appDescRefsNew[0].getProperty("service.pid"));
				updateConstants();
			}
			else if (appDescRefsOld.length+1 == appDescRefsNew.length) {
				boolean found = false;
				for (int i=0; i<appDescRefsNew.length && !found; i++) {
					found=true;
					for (int j=0; j<appDescRefsOld.length; j++) {
						if (appDescRefsOld[j].equals(appDescRefsNew[i])) {
							found = false;							
						}
					}
					if (found) {
						appDescriptor = (ApplicationDescriptor) getContext().getService(appDescRefsNew[i]);
						updateTestPid((String) appDescRefsNew[i].getProperty("service.pid"));	
						updateConstants();
					}
				}
			} 
			else {
				fail("the application descriptor was not found.");
			}
		} catch (Exception e) {
			fail("installation of tb1.jar has failed: " + e.toString());
			e.printStackTrace();
		}
	}
	
	public void updateTestPid(String pid) {
		ApplicationConstants.TEST_PID = pid;
		//updating the constants
		ApplicationConstants.APPLICATION_PERMISSION_FILTER1 = "(&(signer="+ApplicationConstants.SIGNER_FILTER+")"+"(pid="+ApplicationConstants.TEST_PID+"))";
		ApplicationConstants.APPLICATION_PERMISSION_FILTER2 = "(&(signer="+ApplicationConstants.SIGNER_FILTER_WILDCARD+")"+"(pid="+ApplicationConstants.TEST_PID+"))";
		ApplicationConstants.APPLICATION_PERMISSION_FILTER_DIFFERENT_PID = "(&(signer="+ApplicationConstants.SIGNER_FILTER+")"+"(pid="+ApplicationConstants.TEST_PID2+"))";
		ApplicationConstants.APPLICATION_PERMISSION_FILTER_DIFFERENT_SIGNER = "(&(signer="+ApplicationConstants.SIGNER_FILTER2+")"+"(pid="+ApplicationConstants.TEST_PID+"))";
		ApplicationConstants.APPLICATION_PERMISSION_FILTER_INVALID1 = "(&!!!dfs#"+ApplicationConstants.SIGNER_FILTER_INVALID1+")"+"(pid="+ApplicationConstants.TEST_PID+"))";							
	}

	public void unregisterDescriptor() {
		try {
			bundleTestApplication.stop();
			bundleTestApplication.uninstall();
			bundleTestApplication = null;
		} catch (Exception e) {
			fail("Exception unregistering the descriptor: " + e.getMessage());
		}
	}

	public boolean isApplicationHandleRegistered() {
		ServiceReference[] appDescRefs = null;
		try {
			appDescRefs = getContext().getServiceReferences(
					"org.osgi.service.application.ApplicationHandle",
					"(application.descriptor=" + ApplicationConstants.TEST_PID
							+ ")");
			if (appDescRefs != null) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			log("#fail getting the ApplicationHandle.");
			return false;
		}
	}

	public void cleanUp(ApplicationHandle handle) {
		try {
			if (handle != null) {
				handle.destroy();
			}
		}
		catch (IllegalStateException e) {
			log("#error at handle destroy");
		}
	}

	// general
	public void cleanUp(ApplicationHandle handle, PermissionInfo[] infos) {
		try {
			setPermission(infos);
			if (handle != null) {
				handle.destroy();
			}			
		}
		catch (IllegalStateException e) {
			log("#fail at handle destroy.");
		} finally {
			try {
				getAppDescriptor().unlock();
			} catch (Exception e) {
				log("#fail at unlock method.");
			}
		}
	}

	// only treestructure
	public void cleanUp(DmtSession session, String[] nodes) {
		try {
			if (session != null) {
				for (int i = 0; i < nodes.length; i++) {
					session.deleteNode(nodes[i]);
				}
			}
		} catch (Exception e) {
			log("#error deleting the nodes.");
		} finally {
			closeSession(session);
		}
	}

	public void cleanUp(PermissionInfo[] infos) {
		try {
			setPermission(infos);
			getAppDescriptor().unlock();
		} catch (Exception e) {
			log("#fail at cleanUp(setPermission,unLock)");
		}
	}

	// to get the ApplicationHandle
	public ApplicationHandle getAppHandle() {
		ServiceReference[] appDescRefs;
		try {
			appDescRefs = getContext().getServiceReferences(
					"org.osgi.service.application.ApplicationHandle",
					"(application.descriptor=" + ApplicationConstants.TEST_PID
							+ ")");
			return (appDescRefs != null) ? (ApplicationHandle) getContext()
					.getService(appDescRefs[0]) : null;
		} catch (InvalidSyntaxException e) {
			return null;
		}
	}

	public int getNumberAppHandle() {
		ServiceReference[] appDescRefs;
		try {
			appDescRefs = getContext().getServiceReferences(
					"org.osgi.service.application.ApplicationHandle",
					"(application.descriptor=" + ApplicationConstants.TEST_PID
							+ ")");
			return (appDescRefs != null) ? appDescRefs.length : 0;
		} catch (InvalidSyntaxException e) {
			return 0;
		}
	}
	
	public void cleanUp(ScheduledApplication sa, PermissionInfo[] infos) {
		try {
			setLocalPermission(new PermissionInfo(
					ApplicationAdminPermission.class.getName(),
					"(pid=*)", ApplicationAdminPermission.SCHEDULE_ACTION+","+ApplicationAdminPermission.LIFECYCLE_ACTION));// only schedule, schedule implies lifecycle 			
			if (sa != null) {
				try {
					sa.remove();
				}catch( Exception e ) {}
			}
		} catch (Exception e) {
			log("#error removing the scheduled application. " + e.getMessage());
		}
		try {	
			ApplicationHandle handle = getAppHandle();
			while (handle != null) {
				handle.destroy();
				handle = getAppHandle();
			}	
		} catch (Exception e) {
			log("#error destroying the handle. " + e.getMessage());
		}	
		setPermission(infos);			
	}	
	
	public void destroyHandles() {
		try {
			ApplicationHandle handle = getAppHandle();
			while (handle != null) {
				handle.destroy();
				handle = getAppHandle();
			}
		} catch (Exception e) {
			log("#error destroying the handles.");
		}
	}

	public Bundle installTestBundle() {
		try {
			if (testBundle == null) {
				testBundle = installBundle("tb3.jar");
			}
		} catch (Exception e) {
			fail("error installing the testBundle");
		}
		return testBundle;
	}
	
	public void startActivator(boolean highValue) {
		try {
			if (testingActivator == null) {
				testingActivator = new TestingActivatorImplLow();
				if (highValue) {
					testingActivator.start(this.getContext());
				} else {
					testingActivator.startWithoutRanking(this.getContext());
				}
			}
		} catch (Exception e) {
			fail("error starting the TestingActivator.");
		}
	}

	public void stopActivator() {
		try {
			if (testingActivator != null) {
				testingActivator.stop(this.getContext());
				testingActivator = null;
			}
		} catch (Exception e) {
			fail("error stopping the TestingActivator.");
		}
	}

	public void startActivator2(boolean highValue) {
		try {
			if (testingActivator2 == null) {
				testingActivator2 = new TestingActivatorImplHigh();
				if (highValue) {
					testingActivator2.start(this.getContext());
				} else {
					testingActivator2.startWithoutRanking(this.getContext());
				}
			}
		} catch (Exception e) {
			fail("error starting the TestingActivator2.");
		}
	}

	public void stopActivator2() {
		try {
			if (testingActivator2 != null) {
				testingActivator2.stop(this.getContext());
				testingActivator2 = null;
			}
		} catch (Exception e) {
			fail("error stopping the TestingActivator2.");
		}
	}

	public void uninstallTestBundle() {
		try {
			if (testBundle != null) {
				testBundle.stop();
				testBundle.uninstall();
				testBundle = null;
			}
		} catch (Exception e) {
			fail("error uninstalling the testBundle");
		}
	}

	public ScheduledApplication getScheduledApplication() {
		ServiceReference[] appDescRefs = null;
		try {
			appDescRefs = getContext().getServiceReferences(
					"org.osgi.service.application.ScheduledApplication", null);
		} catch (Exception e) {
			log("error getting the ScheduledApplication");
		}
		return appDescRefs != null ? (ScheduledApplication) getContext()
				.getService(appDescRefs[0]) : null;
	}

	/**
	 * Verify if the handle was stopped.
	 */
	public boolean isHandleStopped() {
		try {
			ServiceReference[] appDescRefs = getContext().getServiceReferences(
					"org.osgi.service.application.ApplicationHandle",
					"(application.descriptor=" + ApplicationConstants.TEST_PID
							+ ")");
			if (appDescRefs == null) {
				return true;
			} else {
				return false;
			}
		} catch (InvalidSyntaxException e) {
			return false;
		}
	}

	/**
	 * @return Returns the isModified.
	 */
	public boolean isModified() {
		return isModified;
	}

	/**
	 * @param isModified
	 *            The isModified to set.
	 */
	public void setModified(boolean isModified) {
		this.isModified = isModified;
	}

	/**
	 * @return Returns the isRegistered.
	 */
	public boolean isRegistered() {
		return isRegistered;
	}

	/**
	 * @param isRegistered
	 *            The isRegistered to set.
	 */
	public void setRegistered(boolean isRegistered) {
		this.isRegistered = isRegistered;
	}

	/**
	 * @return Returns the isUnregistered.
	 */
	public boolean isUnregistered() {
		return isUnregistered;
	}

	/**
	 * @param isUnregistered
	 *            The isUnregistered to set.
	 */
	public void setUnregistered(boolean isUnregistered) {
		this.isUnregistered = isUnregistered;
	}

	public void resetEventProperties() {
		this.isUnregistered = false;
		this.isRegistered = false;
		this.isModified = false;
	}

	public Object getAppInstance() {
		return appController.getApplicationInstance();
	}

	public int getServiceId(String className) {
		ServiceReference refs = getContext().getServiceReference(className);
		return refs == null ? -1 : ((Long) refs
				.getProperty(Constants.SERVICE_ID)).intValue();
	}

	public Object getServiceProperty(String className, String key, String filter) {
		if (filter == null) {
			ServiceReference refs = getContext().getServiceReference(className);
			return refs == null ? null : (refs.getProperty(key));
		} else {
			ServiceReference[] refs;
			try {
				refs = getContext().getServiceReferences(className, "("+Constants.SERVICE_PID+"="+filter+")");
			} catch (InvalidSyntaxException e) {
				return null;
			}
			return refs == null ? null : (refs[0].getProperty(key));
		}
	}

	/**
	 * @return Returns the dmtAdmin.
	 */
	public DmtAdmin getDmtAdmin() {
		return dmtAdmin;
	}

	public void closeSession(DmtSession session) {
		try {
			if (session != null) {
				if (session.getState() == DmtSession.STATE_OPEN) {
					session.close();
				}
			}
		} catch (Exception e) {
			log("error closing the session. " + e.getMessage());
		}
	}

	public void unprepare() {
		try {
			appController.stop(this.getContext());
		} catch (Exception e) {
			log("#error on unPrepare");
		}
        synchronized (worker) {
        	worker.setRunning(false);
        	worker.notifyAll();
        }
	}

	public TestAppControllerImpl getAppController() {
		return appController;
	}

	public boolean isTestClassRegistered(String className) {
		ServiceReference rfs = getContext().getServiceReference(className);
		if (rfs != null) {
			return true;
		} else {
			return false;
		}
	}

	public ServiceReference getServiceReference() {
		return getContext().getServiceReference(
				ApplicationDescriptor.class.getName());
	}

	public String getServicePid() {

		ServiceReference[] appDescRefs = null;
		try {
			appDescRefs = getContext().getServiceReferences(
					"org.osgi.service.application.ApplicationDescriptor",
					"(service.pid=" + ApplicationConstants.TEST_PID + ")");
		} catch (InvalidSyntaxException e) {
			fail("#error getting the ApplicationDescriptor", e);
		}

		if (appDescRefs != null) {
			return (String) appDescRefs[0].getProperty(Constants.SERVICE_PID);
		} else {
			return null;
		}

	}

	public EventHandlerActivator getEventBundle() {
		return eventBundle;
	}

	public TestingActivator getTestingActivator() {
		return testingActivator;
	}
	
	public TestingActivator getTestingActivator2() {
		return testingActivator2;
	}	

}