package com.nokia.test.midletcontainer;

import java.io.*;
import java.net.URL;
import java.util.*;

import org.osgi.framework.*;
import org.osgi.service.application.ApplicationDescriptor;
import org.osgi.service.application.ApplicationHandle;
import org.osgi.service.application.ScheduledApplication;
import org.osgi.impl.bundle.midletcontainer.*;
import info.dmtree.DmtAdmin;
import info.dmtree.DmtData;
import info.dmtree.DmtSession;
import info.dmtree.Uri;

import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventHandler;
import org.osgi.service.log.*;
import org.osgi.util.tracker.ServiceTracker;

public class TestMidletContainerBundleActivator
    implements BundleActivator, BundleListener, EventHandler, Runnable
{
    private BundleContext           bc;
    private Thread                  testerThread;
    private Bundle                  midletBundle;
    private Bundle                  midletContainerBundle;
    private Bundle                  logServiceBundle;
  	private Bundle									schedulerBundle;
    private ApplicationDescriptor   appDescs[];
    private ApplicationHandle       appHandle;
    private String                  installedAppUID;
    private long                    installedBundleID;
    private ServiceRegistration     serviceReg;
    private Hashtable               serviceRegProps;
    private LinkedList              receivedEvents;
  	private ServiceReference				dmtFactoryRef;
  	private DmtAdmin 								dmtFactory;
  	private ServiceTracker          dmtTracker;

    public TestMidletContainerBundleActivator()
    {
        receivedEvents = new LinkedList();
        serviceRegProps = new Hashtable();
    }

    public void start(BundleContext bc)
        throws Exception
    {
        this.bc = bc;
        serviceReg = bc.registerService("org.osgi.service.event.EventHandler", this, serviceRegProps);
        bc.addBundleListener(this);
        
    		
    		dmtTracker = new ServiceTracker( bc, DmtAdmin.class.getName(), null );
    		dmtTracker.open();        
    }

    public void stop(BundleContext bc)
        throws Exception
    {
  		  if( dmtFactory != null )
  			  bc.ungetService( dmtFactoryRef );
  		
        serviceReg.unregister();
        this.bc = null;
    }

    public synchronized void handleEvent(Event event)
    {
        receivedEvents.add(event);
        notify();
    }

    synchronized Event getEvent()
    {
        while(receivedEvents.isEmpty()) 
            try
            {
                wait();
            }
            catch(InterruptedException interruptedexception) { }
        return (Event)receivedEvents.removeFirst();
    }

    synchronized void deleteEventQueue()
    {
        receivedEvents.clear();
    }

    boolean checkEvent(String name)
    {
        try
        {
            Event event;
            do {
            	event = getEvent();
            }while(!event.getTopic().startsWith("org/osgi/application/") && !event.getTopic().startsWith("com/nokia/midlettest/"));
                
            if(!event.getTopic().equals(name))
                throw new Exception("Received: " + event.getTopic() + "    Expected:" + name);
            else
                return true;
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return false;
    }
  	
  	String mangle( String in ) {
  		DmtAdmin dmtAdmin = (DmtAdmin)dmtTracker.getService();
  		if( dmtAdmin == null )
  			throw new RuntimeException("DmtAdmin not running!");
  		return Uri.mangle( in );
  	}

    String getPID(ApplicationDescriptor appDesc)
    {
        return (String)appDesc.getProperties("").get(Constants.SERVICE_PID);
    }

    boolean isLocked(ApplicationDescriptor appDesc)
    {
        return ((Boolean)appDesc.getProperties("").get(ApplicationDescriptor.APPLICATION_LOCKED)).booleanValue();
    }

    public ApplicationDescriptor getAppDesc(ApplicationHandle appHnd)
    {
        return appHnd.getApplicationDescriptor();
    }

    public void bundleChanged(BundleEvent e)
    {
        if(e.getBundle().getBundleId() == bc.getBundle().getBundleId() && e.getType() == 2)
        {
            testerThread = new Thread(this);
            testerThread.start();
        }
    }

  	ApplicationHandle lookupAppHandle(ApplicationDescriptor appDesc)
  			throws Exception {
  		Map props = appDesc.getProperties("en");
  		long bundleID = Long.parseLong((String) props
  				.get("application.bundle.id"));
  		ServiceReference[] references = bc.getServiceReferences(
  				ApplicationHandle.class.getName(),
  				"(" + ApplicationHandle.APPLICATION_DESCRIPTOR + "=" + getPID( appDesc ) + ")");
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

    public void run()
    {
        System.out.println("Midlet container tester thread started!\n");
        if(!testCase_lookUpMidletContainer()) {
            System.out.println("Looking up the Midlet container                  FAILED");
            return;
        } else
            System.out.println("Looking up the Midlet container                  PASSED");
        if(!testCase_lookUpLogService()) {
            System.out.println("Looking up the log service                       FAILED");
            return;
        } else
            System.out.println("Looking up the log service                       PASSED");
    	if (!testCase_lookUpDmtAdmin()) {
    		System.out.println("Looking up the DMT admin                         FAILED");
    		return;
    	} else
    		System.out.println("Looking up the DMT admin                         PASSED");
    	if (!testCase_lookUpScheduler()) {
    		System.out.println("Looking up the scheduler                         FAILED");
    		return;
    	} else
    		System.out.println("Looking up the scheduler                         PASSED");
        if(!testCase_installMidletBundle())
            System.out.println("Midlet bundle install onto Midlet container      FAILED");
        else
            System.out.println("Midlet bundle install onto Midlet container      PASSED");
    	if (!testCase_appPluginCheckInstalledApps()) 																
      		System.out.println("AppPlugin: checking the installed application    FAILED"); 	
    	else 																																				
    		System.out.println("AppPlugin: checking the installed application    PASSED"); 	
        if(!testCase_checkAppDescs())
            System.out.println("Checking the installed Midlet app descriptors    FAILED");
        else
            System.out.println("Checking the installed Midlet app descriptors    PASSED");
        if(!testCase_launchApplication())
            System.out.println("Launching the Midlet application                 FAILED");
        else
            System.out.println("Launching the Midlet application                 PASSED");
        if(!testCase_pauseApplication())
            System.out.println("Pausing the Midlet application                   FAILED");
        else
            System.out.println("Pausing the Midlet application                   PASSED");
        if(!testCase_resumeApplication())
            System.out.println("Resuming the Midlet application                  FAILED");
        else
            System.out.println("Resuming the Midlet application                  PASSED");
        if(!testCase_stopApplication())
            System.out.println("Stopping the Midlet application                  FAILED");
        else
            System.out.println("Stopping the Midlet application                  PASSED");
        if(!testCase_checkNotifyDestroyed())
            System.out.println("Checking MIDlet::notifyDestroyed                 FAILED");
        else
            System.out.println("Checking MIDlet::notifyDestroyed                 PASSED");        
        if(!testCase_checkNotifyPaused())
            System.out.println("Checking MIDlet::notifyPaused                    FAILED");
        else
            System.out.println("Checking MIDlet::notifyPaused                    PASSED");        
        if(!testCase_checkResumeRequest())
            System.out.println("Checking MIDlet::resumeRequest                   FAILED");
        else
            System.out.println("Checking MIDlet::resumeRequest                   PASSED");        
    	if (!testCase_lockApplication())
    		System.out.println("Locking the application                          FAILED");
    	else
    		System.out.println("Locking the application                          PASSED");
    	if (!testCase_saveLockingState())
    		System.out.println("Save the locking state of the application        FAILED");
    	else
    		System.out.println("Save the locking state of the application        PASSED");
    	if (!testCase_scheduleAnApplication())
       		System.out.println("Scheduling an application                        FAILED");
    	else
    		System.out.println("Scheduling an application                        PASSED");
    	if (!testCase_scheduleTwoApplications())
    		System.out.println("Scheduling two applications                      FAILED");
    	else
    		System.out.println("Scheduling two applications                      PASSED");
    	if (!testCase_scheduleApplicationsWithAARestart())
    		System.out.println("Scheduling an applications with AA restart       FAILED");
    	else
    		System.out.println("Scheduling an applications with AA restart       PASSED");
    	if (!testCase_removeScheduledApplication())
    		System.out.println("Checking scheduled application remove            FAILED");
    	else
    		System.out.println("Checking scheduled application remove            PASSED");
    	if (!testCase_recurringSchedule())
    		System.out.println("Checking the recurring schedule                  FAILED");
    	else
    		System.out.println("Checking the recurring schedule                  PASSED");
    	if (!testCase_schedulerFilterMatching())
    		System.out.println("Checking the filter matching of the scheduler    FAILED");
    	else
    		System.out.println("Checking the filter matching of the scheduler    PASSED");
    	if (!testCase_appPluginCheckInstalledApps())
    		System.out.println("AppPlugin: checking the installed application    FAILED");
    	else
    		System.out.println("AppPlugin: checking the installed application    PASSED");
    	if (!testCase_appPluginCheckRunningApps()) 																	
    		System.out.println("AppPlugin: checking a running application        FAILED"); 	
    	else 																																				
    		System.out.println("AppPlugin: checking a running application        PASSED"); 	
    	if (!testCase_appPluginCheckApplicationLaunch()) 														
    		System.out.println("AppPlugin: checking the application launching    FAILED"); 	
    	else 																																				
    		System.out.println("AppPlugin: checking the application launching    PASSED"); 	
    	if (!testCase_appPluginCheckApplicationStop()) 															
    		System.out.println("AppPlugin: checking the application stopping     FAILED"); 	
    	else 																																				
    		System.out.println("AppPlugin: checking the application stopping     PASSED"); 	
    	if (!testCase_appPluginLock()) 															
    		System.out.println("AppPlugin: checking the lock changing            FAILED"); 	
    	else 																																				
    		System.out.println("AppPlugin: checking the lock changing            PASSED"); 	
    	if (!testCase_appPluginScheduleSynchronization()) 															
  		    System.out.println("AppPlugin: schedule synchronization check        FAILED"); 	
  		else 																																				
  		    System.out.println("AppPlugin: schedule synchronization check        PASSED"); 	
    	if (!testCase_appPluginModifySchedule()) 															
	  	    System.out.println("AppPlugin: schedule modification                 FAILED"); 	
  		else 																																				
		    System.out.println("AppPlugin: schedule modification                 PASSED"); 	
    	if (!testCase_appPluginCreateAndDeleteSchedule()) 															
  		    System.out.println("AppPlugin: create and delete schedule            FAILED"); 	
		else 																																				
	  	    System.out.println("AppPlugin: create and delete schedule            PASSED"); 	
        if(!testCase_oatRegisterService())
            System.out.println("Checking OAT service registration                FAILED");
        else
            System.out.println("Checking OAT service registration                PASSED");
        if(!testCase_oatLocateService())
            System.out.println("Checking OAT locate service                      FAILED");
        else
            System.out.println("Checking OAT locate service                      PASSED");
        if(!testCase_oatGetServiceProps())
            System.out.println("Checking OAT getting service properties          FAILED");
        else
            System.out.println("Checking OAT getting service properties          PASSED");
        if(!testCase_oatLocateServices())
            System.out.println("Checking OAT locate services                     FAILED");
        else
            System.out.println("Checking OAT locate services                     PASSED");
        if(!testCase_oatServiceListener())
            System.out.println("Checking OAT service listener                    FAILED");
        else
            System.out.println("Checking OAT service listener                    PASSED");
        if(!testCase_oatStaticServiceDies())
            System.out.println("Checking OAT static service dies                 FAILED");
        else
            System.out.println("Checking OAT static service dies                 PASSED");
        if(!testCase_oatMandatoryServiceDies())
            System.out.println("Checking OAT mandatory service dies              FAILED");
        else
            System.out.println("Checking OAT mandatory service dies              PASSED");
        if(!testCase_oatCheckStartupParams())
            System.out.println("Checking OAT startup parameters                  FAILED");
        else
            System.out.println("Checking OAT startup parameters                  PASSED");
        if(!testCase_oatSelfRegistration())
            System.out.println("Checking whether self registration is forbidden  FAILED");
        else
            System.out.println("Checking whether self registration is forbidden  PASSED");
        if(!testCase_launchAfterRestart())
            System.out.println("Launching Midlet app after container restart     FAILED");
        else
            System.out.println("Launching Midlet app after container restart     PASSED");
        if(!testCase_stopAtServiceUnregistering())
            System.out.println("Checking stop after service unregistering        FAILED");
        else
            System.out.println("Checking stop after service unregistering        PASSED");
        if(!testCase_separateClassLoaderCheck())
            System.out.println("Checking if the class loader is separate         FAILED");
        else
            System.out.println("Checking if the class loader is separate         PASSED");
        if(!testCase_uninstallMidletBundle())
            System.out.println("Midlet bundle uninstall from Midlet container    FAILED");
        else
            System.out.println("Midlet bundle uninstall from Midlet container    PASSED");
        if(!testCase_checkOrphanedSchedules())
            System.out.println("AppPlugin: checking orphaned schedules           FAILED");
        else
            System.out.println("AppPlugin: checking orphaned schedules           PASSED");
        
        System.out.println("\n\nMidlet container tester thread finished!");
    }

	boolean installMidletBundle(String resourceName)
    {
        try
        {
            URL resourceURL = bc.getBundle().getResource(resourceName);
            if(resourceURL == null)
                throw new Exception("Can't find " + resourceName + " in the resources!");
            midletBundle = bc.installBundle(resourceURL.toString(), resourceURL.openStream());
            
            ServiceReference appList[] = null;
            
            for( int counter = 0; counter != 5; counter ++ ) {
              try {
              	Thread.sleep(250L);
              }catch(InterruptedException interruptedexception) {}
          
              appList = bc.getServiceReferences(ApplicationDescriptor.class.getName(), "(application.bundle.id=" + installedBundleID + ")");              
              if( appList != null && appList.length != 0 )
               	throw new Exception("ApplicationDescriptor was installed without starting the bundle!");
            }
            
            midletBundle.start();
            installedBundleID = midletBundle.getBundleId();
            int tries = 0;
            do
            {
                do
                {
                    if(++tries == 20)
                        throw new Exception("Can't find the installed ApplicationDescriptor!");
                    try
                    {
                        Thread.sleep(250L);
                    }
                    catch(InterruptedException interruptedexception) { }
                } while(midletBundle.getState() != 32);
                appList = bc.getServiceReferences(ApplicationDescriptor.class.getName(), "(application.bundle.id=" + installedBundleID + ")");
            } while(appList == null || appList.length == 0);
            appDescs = new ApplicationDescriptor[appList.length];
            for(int i = 0; i != appList.length; i++)
            {
                appDescs[i] = (ApplicationDescriptor)bc.getService(appList[i]);
                bc.ungetService(appList[i]);
            }

            installedAppUID = getPID(appDescs[0]);
            if(appDescs == null || appDescs.length != 1)
                throw new Exception("Illegal number of applications were installed!");
            else
                return true;
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return false;
    }

    boolean testCase_installMidletBundle()
    {
        return installMidletBundle("midletsample.jar");
    }

    boolean testCase_lookUpMidletContainer()
    {
        try
        {
            Bundle bundles[] = bc.getBundles();   
            for(int i = 0; i < bundles.length; i++)
                if(bundles[i].getState() == Bundle.ACTIVE)
                {                    
                	  String symbolicName = bundles[i].getSymbolicName(); 
                    if( symbolicName != null && symbolicName.equals("org.osgi.impl.bundle.midletcontainer") )
                    {
                        midletContainerBundle = bundles[i];
                        return true;
                    }
                }

            System.out.println("MIDlet Container is not running!");
            return false;
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return false;
    }

    boolean testCase_lookUpScheduler() {
  		try {
  			Bundle[] bundles = bc.getBundles();
  			for (int i = 0; i < bundles.length; i++) {
  				if (bundles[i].getState() == Bundle.ACTIVE) {
  					Dictionary dict = bundles[i].getHeaders();
  					Object name = dict.get("Bundle-Name");
  					if ( name != null && name.equals( "impl.service.application" )) {
  						System.out.println("NOKIA scheduler found!");
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

    boolean testCase_lookUpDmtAdmin() {
  		try {
  			dmtFactoryRef = bc.getServiceReference( DmtAdmin.class.getName() );
        if(dmtFactoryRef == null)      
        	throw new Exception("Can't find the DmtAdmin service!");
        
        dmtFactory = (DmtAdmin) bc.getService( dmtFactoryRef );
        if( dmtFactory == null )
        	throw new Exception("Can't get the DmtAdmin service!");
        
  			return true;
  		}
  		catch (Exception e) {
  			e.printStackTrace();
  			return false;
  		}
  	}

    boolean testCase_lookUpLogService()
    {
        try
        {
            Bundle bundles[] = bc.getBundles();
            for(int i = 0; i < bundles.length; i++)
                if(bundles[i].getState() == 32)
                {
                    Dictionary dict = bundles[i].getHeaders();
                    Object name = dict.get("Bundle-Name");
                    if(name != null && name.equals("impl.service.log"))
                    {
                        System.out.println("Log service found!");
                        logServiceBundle = bundles[i];
                        return true;
                    }
                }

            System.out.println("MIDlet Container is not running!");
            return false;
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return false;
    }

    boolean testCase_uninstallMidletBundle()
    {
        try
        {
            midletBundle.stop();
            midletBundle.uninstall();
            if(midletBundle.getState() != 1)
                throw new Exception("Couldn't uninstall Midlet bundle properly!");
            else
                return true;
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return false;
    }

    boolean testCase_checkAppDescs()
    {
        ApplicationDescriptor appDesc = appDescs[0];
        String appUID = getPID(appDesc);
        try
        {
            ServiceReference appList[] = bc.getServiceReferences(ApplicationDescriptor.class.getName(), "("+Constants.SERVICE_PID+"=" + appUID + ")");
            if(appList == null || appList.length == 0)
                throw new Exception("No ApplicationUID(" + appUID + ") registered!");
            long bundleID = -1L;
            boolean registered = false;
            for(int i = 0; i != appList.length; i++)
            {
                if((ApplicationDescriptor)bc.getService(appList[i]) == appDesc)
                {
                    registered = true;
                    if(!appList[i].getProperty(Constants.SERVICE_PID).equals(getPID(appDesc)))
                        throw new Exception("Illegal service.pid in the AppDesc (" + (String)appList[i].getProperty(Constants.SERVICE_PID) + " != " + getPID(appDesc) + ")");
                    if(!appList[i].getProperty(ApplicationDescriptor.APPLICATION_NAME).equals(appDesc.getProperties(Locale.getDefault().getLanguage()).get(ApplicationDescriptor.APPLICATION_NAME)))
                        throw new Exception("Illegal application name in the AppDesc (" + (String)appList[i].getProperty(ApplicationDescriptor.APPLICATION_NAME) + " != " + appDesc.getProperties("").get(ApplicationDescriptor.APPLICATION_NAME) + ")");
                    bundleID = appList[i].getBundle().getBundleId();
                }
                bc.ungetService(appList[i]);
            }

            Map engProps = appDesc.getProperties("en");
            if(!engProps.get(ApplicationDescriptor.APPLICATION_NAME).equals("TestMidlet"))
                throw new Exception("The application name is " + (String)engProps.get(ApplicationDescriptor.APPLICATION_NAME) + " instead of 'TestMidlet'!");
            if(!((URL)engProps.get(ApplicationDescriptor.APPLICATION_ICON)).toString().endsWith("/TestIcon.gif"))
                throw new Exception("The icon path " + (String)engProps.get(ApplicationDescriptor.APPLICATION_ICON) + " doesn't ends with '/TestIcon.gif'!");
            if(!engProps.get(ApplicationDescriptor.APPLICATION_VERSION).equals("1.0.0"))
                throw new Exception("The application version is " + (String)engProps.get(ApplicationDescriptor.APPLICATION_VERSION) + " instead of '1.0.0'!");
            if(!engProps.get(ApplicationDescriptor.APPLICATION_CONTAINER).equals("MIDlet"))
                throw new Exception("The application type is " + (String)engProps.get(ApplicationDescriptor.APPLICATION_CONTAINER) + " instead of 'MIDlet'!");
            if(engProps.get("application.bundle.id") == null)
                throw new Exception("No application bundle id found!");
            if(!engProps.get(ApplicationDescriptor.APPLICATION_VISIBLE).equals( new Boolean( "true") ))
                throw new Exception("Visible flag is " + (Boolean)engProps.get(ApplicationDescriptor.APPLICATION_VISIBLE) + " instead of 'true'!");
            if(!engProps.get(ApplicationDescriptor.APPLICATION_VENDOR).equals("Nokia"))
                throw new Exception("Vendor flag is " + (String)engProps.get(ApplicationDescriptor.APPLICATION_VENDOR) + " instead of 'Nokia'!");
            else
                return true;
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return false;
    }

    private Map createArgs()
    {
        try
        {
            File file = bc.getDataFile("TestResult");
            if(file.exists())
                file.delete();
            HashMap args = new HashMap();
            args.put("TestResult", file.getAbsolutePath());
            return args;
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    private boolean isLaunchable(ApplicationDescriptor appDesc)
    {
    		Boolean launchable = (Boolean)appDesc.getProperties("en").get(ApplicationDescriptor.APPLICATION_LAUNCHABLE);
        return launchable != null && launchable.booleanValue();
    }

    private String  getResultFileContent() {
      try
      {
          File resultFile = bc.getDataFile("TestResult");
          String resultStr = "";
          if(resultFile.exists())
          {
              FileInputStream stream = new FileInputStream(resultFile);
              byte buffer[] = new byte[1024];
              int length;
              while((length = stream.read(buffer, 0, buffer.length)) > 0) 
                  resultStr = resultStr + new String(buffer);
              stream.close();
              resultStr = resultStr.trim();
          }
          resultFile.delete();
          return resultStr;
      }
      catch(Exception e)
      {
          e.printStackTrace();
      }
      return null;    	
    }
    
    private boolean checkResultFile(String expResult)
    {
    	  String result = getResultFileContent();
    	  if( !result.equals( expResult ) ) {
          System.out.println("Received: " + result + "  instead of " + expResult);
          return false;
    	  }
        return true;
    }

    boolean testCase_launchApplication()
    {
        return testCase_launchApplicationError(false);
    }

    boolean testCase_launchApplicationError(boolean errorFlag)
    {
        try
        {
            ApplicationDescriptor appDesc = appDescs[0];
            Map args = createArgs();
            if(args == null)
                throw new Exception("Cannot create the arguments of launch!");
            args.put( "Null value", null );
            boolean launchable = isLaunchable(appDesc);            
            appHandle = appDesc.launch(args);
            if(!checkResultFile("START"))
                throw new Exception("Result of the launch is not START!");
            if(!launchable)
                throw new Exception("Application started, but originally was not launchable!");
            ServiceReference appList[] = bc.getServiceReferences(ApplicationHandle.class.getName(), "("+ ApplicationHandle.APPLICATION_DESCRIPTOR + "=" + getPID(appDesc) + ")");
            if(appList == null || appList.length == 0)
                throw new Exception("No registered application handle found!");
            if(getAppDesc(appHandle) != appDesc)
                throw new Exception("The application descriptor differs from the found one!");
            if(!appHandle.getState().equals("RUNNING"))
                throw new Exception("Application didn't started!");
            else
                return true;
        }
        catch(Exception e)
        {
            if(!errorFlag)
                e.printStackTrace();
        }
        return false;
    }

    boolean testCase_pauseApplication()
    {
        try
        {
            ((MidletHandle)appHandle).pause();
            if(!checkResultFile("PAUSE"))
                throw new Exception("Result of the pause is not PAUSE!");
            if(!appHandle.getState().equals("PAUSED"))
                throw new Exception("The status didn't change to paused!");
            else
                return true;
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return false;
    }

    boolean testCase_resumeApplication()
    {
        try
        {
            ((MidletHandle)appHandle).resume();
            if(!checkResultFile("RESUME"))
                throw new Exception("Result of the resume is not RESUME!");
            if(!appHandle.getState().equals("RUNNING"))
                throw new Exception("The status didn't change to running!");
            else
                return true;
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return false;
    }

    private void checkApplicationStop(String pid) throws Exception {
      if(!checkResultFile("STOP"))
        throw new Exception("Result of the stop is not STOP!");
      ServiceReference appList[] = bc.getServiceReferences(ApplicationHandle.class.getName(), "("+ Constants.SERVICE_PID + "=" + pid + ")");
      if(appList != null && appList.length != 0) {
        for(int i = 0; i != appList.length; i++) {
            ApplicationHandle handle = (ApplicationHandle)bc.getService(appList[i]);
            bc.ungetService(appList[i]);
            if(handle == appHandle)
                throw new Exception("Application handle doesn't removed after stop!");
        }

      }
      try {
        appHandle.getState();
      }
      catch(Exception e) {
        return;
      }
      throw new Exception("The status didn't change to NONEXISTENT!");    	
    }
    
    boolean testCase_stopApplication()
    {
        try {
            String pid = getPID(getAppDesc(appHandle));
            appHandle.destroy();
            checkApplicationStop( pid );
            return true;
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return false;
    }

    private boolean reloadInstalledApps( boolean hasAppInstalled ) throws Exception {
      ServiceReference references[] = bc.getServiceReferences(ApplicationDescriptor.class.getName(), "(application.bundle.id=" + installedBundleID + ")");
      if(hasAppInstalled)
      {
          if(references == null || references.length == 0)
              throw new Exception("ApplicationDescriptor doesn't restored after container restart!");
          appDescs = new ApplicationDescriptor[references.length];
          for(int i = 0; i != references.length; i++)
          {
              appDescs[i] = (ApplicationDescriptor)bc.getService(references[i]);
              bc.ungetService(references[i]);
          }

      } else
      {
          appDescs = null;
          if(references != null && references.length != 0)
              throw new Exception("New ApplicationDescriptor's appeared after container restart!");
      }
      return true;    	
    }
    
    public boolean restart_MidletContainer(boolean hasAppInstalled)
    {
        try
        {
            midletContainerBundle.stop();
            while(midletContainerBundle.getState() != Bundle.RESOLVED) 
                try
                {
                    Thread.sleep(100L);
                }
                catch(InterruptedException interruptedexception) { }
            ServiceReference appList[] = bc.getServiceReferences(ApplicationDescriptor.class.getName(), "(" + Constants.SERVICE_PID + "=" + installedAppUID + ")");
            if(appList != null && appList.length != 0)
                throw new Exception("Application descriptor doesn't removed after container stop!");
            midletContainerBundle.start();
            while(midletContainerBundle.getState() != Bundle.ACTIVE) 
                try
                {
                    Thread.sleep(100L);
                }
                catch(InterruptedException interruptedexception1) { }
            return reloadInstalledApps( hasAppInstalled );
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return false;
    }

    public boolean restart_LogService()
    {
        try
        {
            logServiceBundle.stop();
            while(logServiceBundle.getState() != 4) 
                try
                {
                    Thread.sleep(100L);
                }
                catch(InterruptedException interruptedexception) { }
            logServiceBundle.start();
            while(logServiceBundle.getState() != 32) 
                try
                {
                    Thread.sleep(100L);
                }
                catch(InterruptedException interruptedexception1) { }
            return true;
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return false;
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
  			
  			/* stopping the scheduler may result stopping the MIDlet container */
  			
  			if( midletContainerBundle.getState() != Bundle.ACTIVE ) {
  				midletContainerBundle.start();
          while(midletContainerBundle.getState() != Bundle.ACTIVE) 
            try {
                Thread.sleep(100L);
            }catch(InterruptedException interruptedexception1) { }
            
          reloadInstalledApps( appDescs != null && appDescs.length != 0 );
  			}	
  			return true;
  		}
  		catch (Exception e) {
  			e.printStackTrace();
  			return false;
  		}
  	}

    boolean testCase_launchAfterRestart()
    {
        try
        {
            if(!restart_MidletContainer(true))
                return false;
            if(!testCase_checkAppDescs())
                return false;
            if(!testCase_launchApplication())
                return false;
            return testCase_stopApplication();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return false;
    }

    boolean testCase_stopAtServiceUnregistering()
    {
        try
        {
            if(!testCase_launchApplication())
                return false;
            String pid = getPID(getAppDesc(appHandle));
            if(!restart_LogService())
                return false;
            checkApplicationStop( pid );
            return true;
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return false;
    }

    boolean testCase_separateClassLoaderCheck()
    {
        try
        {
            if(!testCase_launchApplication())
                return false;
            try
            {
                Thread.sleep(100L);
            }
            catch(InterruptedException interruptedexception) { }
            ApplicationDescriptor appDesc = appDescs[0];
            ApplicationHandle oldHandle = appHandle;
            String pid = getPID(appDesc);
            if(!testCase_launchApplication())
                return false;
            					
            if(!testCase_stopApplication())
                return false;
            appHandle = oldHandle;
            appHandle.destroy();
            checkApplicationStop( pid );
            return true;
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return false;
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

  	private boolean checkResponseForEvent( String eventName, String eventAnswer ) {
      try {
  			sendEvent(new Event( eventName, null), false);
  			if (!checkResultFile( eventAnswer ))
  				throw new Exception("Event handler service was not registered!");
      	return true;
      }
      catch(Exception e) {
          e.printStackTrace();
      }
      return false;  		
  	}
  	
  	boolean testCase_oatRegisterService() {
      try {
      	if( !testCase_launchApplication() )
      		return false;
  		  if( !checkResponseForEvent( "com/nokia/megtest/CheckRegistered", 
  				                          "REGISTERED SUCCESSFULLY") )
  		    return false;
  		  if( !testCase_stopApplication() )
  		  	return false;
  			sendEvent(new Event( "com/nokia/megtest/CheckRegistered", null), false);
  			String content = getResultFileContent();
  			if ( content != null && content.equals( "REGISTERED SUCCESSFULLY" ) )
  				throw new Exception("Event handler was not unregistered after stop!");
  			return true;  		  
      }
      catch(Exception e) {
          e.printStackTrace();
      }
      return false;  		
  	}

  	boolean testCase_oatLocateService() {
      try {
      	if( !testCase_launchApplication() )
      		return false;
  	  	if( !checkResponseForEvent( "com/nokia/megtest/LocateService", 
                                    "LOG SERVICE OPERABLE") )
  		  	return false;
  		  if( !testCase_stopApplication() )
	  	  	return false;
      	if( !testCase_launchApplication() )
      		return false;
  	  	if( !checkResponseForEvent( "com/nokia/megtest/LocateService", 
                                    "LOG SERVICE OPERABLE") )
  		  	return false;
  	  	if( !checkResponseForEvent( "com/nokia/megtest/LocateService", 
                                    "LOG SERVICE OPERABLE") )
          return false;
  		  if( !testCase_stopApplication() )
	  	  	return false;
        return true;  		
      }
      catch(Exception e) {
          e.printStackTrace();
      }
      return false;  		
  	}

  	boolean testCase_oatLocateServices() {
      try {
      	if( !testCase_launchApplication() )
      		return false;
  	  	if( !checkResponseForEvent( "com/nokia/megtest/LocateServices", 
                                    "LOCATE SERVICES OK") )
  		  	return false;
  		  if( !testCase_stopApplication() )
	  	  	return false;
      	if( !testCase_launchApplication() )
      		return false;
  	  	if( !checkResponseForEvent( "com/nokia/megtest/LocateService", 
                                    "LOG SERVICE OPERABLE") )
  		  	return false;
  	  	if( !checkResponseForEvent( "com/nokia/megtest/LocateServices", 
                                    "LOCATE SERVICES OK") )
          return false;
  	  	if( !checkResponseForEvent( "com/nokia/megtest/LocateService", 
                                    "LOG SERVICE OPERABLE") )
          return false;
  		  if( !testCase_stopApplication() )
	  	  	return false;
        return true;  		
      }
      catch(Exception e) {
          e.printStackTrace();
      }
      return false;  		
  	}

  	boolean testCase_oatGetServiceProps() {
      try {
      	if( !testCase_launchApplication() )
      		return false;
  	  	if( !checkResponseForEvent( "com/nokia/megtest/LocateService", 
                                    "LOG SERVICE OPERABLE") )
          return false;
  	  	if( !checkResponseForEvent( "com/nokia/megtest/CheckProperties", 
                                    "PROPERTIES OK") )
  		  	return false;
  		  if( !testCase_stopApplication() )
	  	  	return false;
        return true;  		
      }
      catch(Exception e) {
          e.printStackTrace();
      }
      return false;  		
  	}

  	boolean testCase_oatServiceListener() {
      try {     	
      	if( !testCase_launchApplication() )
      		return false;
  	  	if( !checkResponseForEvent( "com/nokia/megtest/AddServiceListener", 
                                    "SERVICE LISTENER ADDED") )
  		  	return false;

  	  	serviceRegProps.put( "MyPropKey", "MyPropValue" );  	  	
  	  	serviceReg.setProperties( serviceRegProps );
  	  	
  			if (!checkResultFile( "SERVICE CHANGE RECEIVED" ))
  				throw new Exception("Event handler service was not registered!");
  	  	
  	  	if( !checkResponseForEvent( "com/nokia/megtest/RemoveServiceListener", 
                                    "SERVICE LISTENER REMOVED") )
          return false;
  	  	
  	  	serviceRegProps.put( "MyPropKey", "MyPropValue2" );  	  	
  	  	serviceReg.setProperties( serviceRegProps );
  	  	
  			String content = getResultFileContent();
  			if ( content != null && content.equals( "SERVICE CHANGE RECEIVED" ) )
  				throw new Exception("Service listener was not removed!");
  	  	
  	  	if( !checkResponseForEvent( "com/nokia/megtest/AddServiceListener", 
                                    "SERVICE LISTENER ADDED") )
          return false;
  	  	
  		  if( !testCase_stopApplication() )
	  	  	return false;
  		  
  		  serviceRegProps.remove( "MyPropKey" );
  	  	serviceReg.setProperties( serviceRegProps );
  	  	
  			content = getResultFileContent();
  			if ( content != null && content.equals( "SERVICE CHANGE RECEIVED" ) )
  				throw new Exception("Service listener was not removed after stop!");
  		  
        return true;  		
      }
      catch(Exception e) {
          e.printStackTrace();
      }
      return false;  		
  	}
  	
  	private boolean restart_logService() {
      try
      {
      	ServiceReference ref = bc.getServiceReference( LogService.class.getName() );
      	Bundle logServiceBundle = ref.getBundle();
      	
      	System.out.println( "Stopping the log service..." );
      	logServiceBundle.stop();
      	
        while(logServiceBundle.getState() != Bundle.RESOLVED ) 
          try {
            Thread.sleep(100L);
          }catch(InterruptedException interruptedexception) {}
        
        
       	System.out.println( "Starting the log service..." );
        logServiceBundle.start();
        while(midletContainerBundle.getState() != Bundle.ACTIVE) 
          try {
            Thread.sleep(100L);
        }catch(InterruptedException interruptedexception1) { }
        return true;
      }
      catch(Exception e) {
          e.printStackTrace();
      }
      return false;  		
  	}


  	boolean testCase_oatStaticServiceDies() {
      try {     	
      	if( !testCase_launchApplication() )
      		return false;

      	String pid = getPID(getAppDesc(appHandle));
        
  	  	if( !checkResponseForEvent( "com/nokia/megtest/LocateService", 
                                    "LOG SERVICE OPERABLE") )
  	  		return false;
  	  	if( !restart_logService() )
  	  		return false;
  	  	
  	  	checkApplicationStop( pid );
        return true;
      }
      catch(Exception e) {
          e.printStackTrace();
      }
      return false;  		
  	}

  	boolean testCase_oatMandatoryServiceDies() {
      try {     	
      	if( !testCase_launchApplication() )
      		return false;

      	String pid = getPID(getAppDesc(appHandle));
        
  	  	if( !restart_logService() )
  	  		return false;
  	  	
  	  	checkApplicationStop( pid );
        return true;
      }
      catch(Exception e) {
          e.printStackTrace();
      }
      return false;  		
  	}

  	boolean testCase_oatCheckStartupParams() {
      try {
      	if( !testCase_launchApplication() )
      		return false;
  	  	if( !checkResponseForEvent( "com/nokia/megtest/CheckStartupParams", 
                                    "STARTUP PARAMETERS OK") )
  		  	return false;
  		  if( !testCase_stopApplication() )
	  	  	return false;
        return true;  		
      }
      catch(Exception e) {
          e.printStackTrace();
      }
      return false;  		
  	}

  	boolean testCase_oatSelfRegistration() {
      try {
      	if( !testCase_launchApplication() )
      		return false;
  	  	if( !checkResponseForEvent( "com/nokia/megtest/RegisterMyself", 
                                    "REGISTER MYSELF OK") )
  		  	return false;
  		  if( !testCase_stopApplication() )
	  	  	return false;
        return true;  		
      }
      catch(Exception e) {
          e.printStackTrace();
      }
      return false;  		
  	}
  	
  	public boolean testCase_lockApplication() {
  		try {
  			ApplicationDescriptor appDesc = appDescs[0];
  			if (isLocked( appDesc ))
  				throw new Exception("Application is locked and cannot launch!");
  			appDesc.lock();
  			if (!isLocked( appDesc ))
  				throw new Exception("Lock doesn't work!");
  			if ( !((Boolean)appDesc.getProperties("en").get(ApplicationDescriptor.APPLICATION_LOCKED)).booleanValue() )
  				throw new Exception("Lock property is incorrect!");
  			boolean launchable = isLaunchable(appDesc);
  			boolean started = false;
  			try {
  				appHandle = appDesc.launch(new Hashtable());
  				started = true;
  			}catch (Exception e) {}
  			
  			if (started)
  				throw new Exception("Application was launched inspite of lock!");

  			appDesc.lock();

  			appDesc.unlock();
  			if (((Boolean)appDesc.getProperties("en").get(ApplicationDescriptor.APPLICATION_LOCKED)).booleanValue())
  				throw new Exception("Lock property is incorrect!");
  			if (isLocked( appDesc ))
  				throw new Exception("Unlock doesn't work!");
  			if (launchable)
  				throw new Exception(
  						"Application was not launchable but started!");
  			if (!testCase_launchApplication())
  				return false;

  			appDesc.unlock();
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
  			if (isLocked( appDesc ))
  				throw new Exception("Application is locked and cannot launch!");
  			if (!restart_MidletContainer(true))
  				return false;
  			appDesc = appDescs[0];
  			if (!testCase_launchApplication())
  				return false;
  			if (isLocked( appDesc ))
  				throw new Exception("Application is locked and cannot launch!");
  			appDesc.lock();
  			if (!((Boolean)appDesc.getProperties("en").get(ApplicationDescriptor.APPLICATION_LOCKED)).booleanValue())
  				throw new Exception("Lock property is incorrect!");
  			if (!isLocked( appDesc ))
  				throw new Exception("Lock doesn't work!");
  			if (!restart_MidletContainer(true))
  				return false;
  			appDesc = appDescs[0];
  			if (!isLocked( appDesc ))
  				throw new Exception("Lock doesn't work after restart!");
  			boolean launchable = isLaunchable(appDesc);
  			boolean started = false;
  			try {
  				appHandle = appDesc.launch(new Hashtable());
  				started = true;
  			}
  			catch (Exception e) {}
  			if (started)
  				throw new Exception("Application was launched inspite of lock!");
  			appDesc.unlock();
  			if (((Boolean)appDesc.getProperties("en").get(ApplicationDescriptor.APPLICATION_LOCKED)).booleanValue())
  				throw new Exception("Lock property is incorrect!");
  			if (launchable)
  				throw new Exception(
  						"Application was not launchable but started!");
  			if (isLocked( appDesc ))
  				throw new Exception("Unlock doesn't work!");
  			return true;
  		}
  		catch (Exception e) {
  			e.printStackTrace();
  			return false;
  		}
  	}  	
  	
  	String getFilterFromNow( int minute ) {
  		Calendar calendar = Calendar.getInstance();
  		calendar.add( Calendar.SECOND, 60*minute + 2 );
  		
  		String filter = "(&";
  		filter += "(" + ScheduledApplication.YEAR +         "=" + calendar.get( Calendar.YEAR )         +")";
  		filter += "(" + ScheduledApplication.MONTH +        "=" + calendar.get( Calendar.MONTH )        +")";
  		filter += "(" + ScheduledApplication.DAY_OF_MONTH + "="	+ calendar.get( Calendar.DAY_OF_MONTH ) +")";
  		filter += "(" + ScheduledApplication.HOUR_OF_DAY +  "="	+ calendar.get( Calendar.HOUR_OF_DAY )  +")";
  		filter += "(" + ScheduledApplication.MINUTE +       "="	+ calendar.get( Calendar.MINUTE )       +")";
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
  			args.put( "NullChecking", null );
  			if (args == null)
  				throw new Exception("Cannot create the arguments of launch!");
  			appDesc.schedule(null, args, ScheduledApplication.TIMER_TOPIC, getFilterFromNow( 1 ), false);
  			Thread.sleep(62000);
  			appHandle = lookupAppHandle(appDesc);
  			if (appHandle == null
  					|| !appHandle.getState().equals( ApplicationHandle.RUNNING ) )
  				throw new Exception("Application wasn't scheduled!");
  			if (!checkResultFile("START"))
  				throw new Exception("Result of the schedule is not START!");
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
  			appDesc.schedule(null, args, ScheduledApplication.TIMER_TOPIC, getFilterFromNow( 2 ), false);
  			appDesc.schedule(null, args, ScheduledApplication.TIMER_TOPIC, getFilterFromNow( 1 ), false);
  			Thread.sleep( 92000 );
  			appHandle = lookupAppHandle(appDesc);
  			if (appHandle == null
  					|| !appHandle.getState().equals( ApplicationHandle.RUNNING ) )
  				throw new Exception("Application wasn't scheduled!");
  			if (!checkResultFile("START"))
  				throw new Exception("Result of the schedule is not START!");
  			if (!testCase_stopApplication())
  				return false;
  			Thread.sleep( 30000);
  			appHandle = lookupAppHandle(appDesc);
  			if (appHandle == null
  					|| !appHandle.getState().equals( ApplicationHandle.RUNNING ) ) 
  				throw new Exception("Application wasn't scheduled!");
  			if (!checkResultFile("START"))
  				throw new Exception("Result of the schedule is not START!");
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
  			appDesc.schedule(null, args, ScheduledApplication.TIMER_TOPIC, getFilterFromNow( 1 ), false);
  			if (!restart_scheduler())
  				return false;
  			Thread.sleep( 62000 );
  			appDesc = appDescs[0];
  			appHandle = lookupAppHandle(appDesc);
  			if (appHandle == null
  					|| !appHandle.getState().equals( ApplicationHandle.RUNNING ) )
  				throw new Exception("Application wasn't scheduled!");
  			if (!checkResultFile("START"))
  				throw new Exception("Result of the schedule is not START!");
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
  			ScheduledApplication schedApp = appDesc.
  					schedule(null, args, ScheduledApplication.TIMER_TOPIC, getFilterFromNow( 1 ), false);
  			
  			if( schedApp.getApplicationDescriptor() != appDesc )
  				throw new Exception( "Invalid application descriptor was received!" );
  			
  			schedApp.remove();
  			Thread.sleep( 64000);
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
  			appDesc.schedule(null, args, "com/nokia/test/ScheduleEvent", null, false);
  			
  			sendEvent(new Event("com/nokia/test/ScheduleEvent", null), false);
  			
  			appHandle = lookupAppHandle(appDesc);
  			if (appHandle == null
  					|| !appHandle.getState().equals( ApplicationHandle.RUNNING ) )
  				throw new Exception("Application wasn't scheduled!");
  			if (!checkResultFile("START"))
  				throw new Exception("Result of the schedule is not START!");
  			if (!testCase_stopApplication())
  				return false;
  			
  			sendEvent(new Event("com/nokia/test/ScheduleEvent", null), false);
  			appHandle = lookupAppHandle(appDesc);
  			if (appHandle != null )
  				throw new Exception("Application was scheduled inspite of non-recurring!");

  			ScheduledApplication schedApp = appDesc.schedule(null, args, "com/nokia/test/ScheduleEvent", null, true);
  			sendEvent(new Event("com/nokia/test/ScheduleEvent", null), false);
  			
  			appHandle = lookupAppHandle(appDesc);
  			if (appHandle == null
  					|| !appHandle.getState().equals( ApplicationHandle.RUNNING ) )
  				throw new Exception("Application wasn't scheduled!");
  			if (!checkResultFile("START"))
  				throw new Exception("Result of the schedule is not START!");
  			if (!testCase_stopApplication())
  				return false;

  			sendEvent(new Event("com/nokia/test/ScheduleEvent", null), false);
  			
  			appHandle = lookupAppHandle(appDesc);
  			if (appHandle == null
  					|| !appHandle.getState().equals( ApplicationHandle.RUNNING ) )
  				throw new Exception("Application wasn't scheduled!");
  			if (!checkResultFile("START"))
  				throw new Exception("Result of the schedule is not START!");
  			if (!testCase_stopApplication())
  				return false;
  			
  			schedApp.remove();
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
  			appDesc.schedule(null, args, "com/nokia/test/ScheduleEvent", "(propi=hallo)", false);
  			
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
  					|| !appHandle.getState().equals( ApplicationHandle.RUNNING ) )
  				throw new Exception("Application wasn't scheduled!");
  			if (!checkResultFile("START"))
  				throw new Exception("Result of the schedule is not START!");
  			if (!testCase_stopApplication())
  				return false;
  			return true;
  		}
  		catch (Exception e) {
  			e.printStackTrace();
  			return false;
  		}
  	}  	
  	
  	boolean testCase_appPluginCheckInstalledApps()
  	{
  		ApplicationDescriptor appDesc = appDescs[0];
  		String appUID = getPID( appDesc );
  		
  		try {
  			DmtSession session = dmtFactory.getSession("./OSGi/Application");
  			
  			String[] nodeNames = session.getChildNodeNames( "./OSGi/Application" );
  		
  			if( nodeNames.length != 1)
  				throw new Exception( "Too many nodenames are present! Only one meglet is installed!" );
  			
  			String mangledAppUID = mangle( appUID ); 
  			
  			if( !mangledAppUID.equals( nodeNames[ 0 ] ) )
  				throw new Exception( "Illegal node name found! (" + nodeNames[ 0 ] + " instead of " + mangledAppUID );
  	
  			String[] properties = session.getChildNodeNames( "./OSGi/Application/" + mangledAppUID );
  			
  			String names[]  = new String [] { 	"Name", "IconURI", "Version", "Vendor", 
  					                          	"Locked", "Location", "ContainerID",
  					                          	"ApplicationID", "Valid", "Instances", 
												"Operations", "Schedules" };
  			Object values[] = new Object [ names.length ];
  			
  			Map props = appDesc.getProperties( Locale.getDefault().getLanguage() );
  			
  			values[ 0 ] = (String)( props.get( ApplicationDescriptor.APPLICATION_NAME ) );
  			values[ 1 ] = ((URL)( props.get( ApplicationDescriptor.APPLICATION_ICON ) )).toString();
  			values[ 2 ] = (String)( props.get( ApplicationDescriptor.APPLICATION_VERSION ) );
  			values[ 3 ] = (String)( props.get( ApplicationDescriptor.APPLICATION_VENDOR ) );
  			values[ 4 ] = (Boolean)(props.get( ApplicationDescriptor.APPLICATION_LOCKED ) );
  			values[ 5 ] = (String)( props.get( ApplicationDescriptor.APPLICATION_LOCATION ) );
  			values[ 6 ] = (String)( props.get( ApplicationDescriptor.APPLICATION_CONTAINER ) );
  			values[ 7 ] = (String)( props.get( ApplicationDescriptor.APPLICATION_PID ) );
  			values[ 8 ] = new Boolean( true );
  			
  			boolean found[] = new boolean[ names.length ];				
  			for( int i = 0; i != names.length; i++ )
  				found [ i ] = false;
  			
  			for( int i = 0; i != properties.length; i++ ) {
  				int j = 0;
  				for(; j != names.length; j++ )
  					if( properties[ i ].equals( names [ j ] ) ) {
  						if( found [ j ] )
  							throw new Exception( "Parameter duplication:" + properties[ i ] + "!" );
  						found[ j ] = true;
  						if( values[ j ] == null )
  							break;
  						
  						DmtData value = session.getNodeValue( "./OSGi/Application/" + mangledAppUID + "/" + names[ j ] );
  						
  						switch( value.getFormat() )
  						{
  						case DmtData.FORMAT_BOOLEAN:
  							if( ((Boolean)values[j]).booleanValue() != value.getBoolean() )
  								throw new Exception( "Invalid value of " + names[ j ] + " (" + values [ j ] + "!=" + value.getBoolean() + ") !" );
  							break;
  						case DmtData.FORMAT_STRING:
  							if( !((String)values[j]).equals( value.getString() ) )
  								throw new Exception( "Invalid value of " + names[ j ] + " (" + values [ j ] + "!=" + value.getString() + ") !" );														
  							break;
  						case DmtData.FORMAT_INTEGER:
  							if( ((Integer)values[j]).intValue() != value.getInt() )
  								throw new Exception( "Invalid value of " + names[ j ] + " (" + values [ j ] + "!=" + value.getInt() +  ") !" );
  							break;
  						default:
  							throw new Exception( "Illegal type for " + names [ j ] + "parameter !" );
  						}
  						
  						break;
  					}
  					if( j == names.length )
  						throw new Exception( "Invalid parameter:" +  properties[ i ] + "!" );
  			}

  			for( int i = 0; i != found.length; i++ )
  				if( !found [ i ] )
  					throw new Exception( "The " + names[ i ] + " is missing from the properties!" );
  			
  			session.close();
  			return true;
  		}
  		catch (Exception e) {
  			e.printStackTrace();
  			return false;
  		}
  	}
  	
  	
  	boolean testCase_appPluginCheckRunningApps() {
  		ApplicationDescriptor appDesc = appDescs[0];
  		String appUID = getPID( appDesc );
			String mangledAppUID = mangle( appUID ); 
  		
  		try {
  			if( !testCase_launchApplication() )
  				return false;
  			
  			ServiceReference[] references = bc.getServiceReferences(
  					ApplicationHandle.class.getName(),
  					"(" + ApplicationHandle.APPLICATION_DESCRIPTOR + "=" + getPID( appDesc ) + ")");
  			
  			if( references == null || references.length == 0 )
  				throw new Exception( "Service reference not found!" );
  			
  			DmtSession session = dmtFactory.getSession("./OSGi/Application/" + mangledAppUID + "/Instances" );

  			String[] nodeNames = session.getChildNodeNames( "./OSGi/Application/" + mangledAppUID + "/Instances" );
  			
  			if( nodeNames == null || nodeNames.length != 1 )
  				throw new Exception( "Couldn't find the application instance node!" );
  			
  			ApplicationHandle appHandle = lookupAppHandle( appDesc );
  			
  			String mangledInstID = mangle( appHandle.getInstanceId() );
  			
  			if( !nodeNames[ 0 ].equals( mangledInstID ) ) 
  				throw new Exception( "Illegal node name (" + nodeNames[ 0 ] + 
  						                 " instead of " + mangledInstID +")" );
  			String[] childNodes = session.getChildNodeNames( "./OSGi/Application/" + mangledAppUID + "/Instances/" + mangledInstID );
  			
  			if( childNodes == null || childNodes.length != 3 )
  				throw new Exception( "Invalid child nodes of the application instance!" );
  			
  			List childList = Arrays.asList( childNodes );
  			
  			if( childList.indexOf( "State" ) == -1 || childList.indexOf( "Operations" ) == -1 || 
  					childList.indexOf( "InstanceID" ) == -1 ) 
  				throw new Exception( "Invalid child nodes of the application instance!" );
  			
  			DmtData instidValue = session.getNodeValue( "./OSGi/Application/" + mangledAppUID + "/Instances/" + mangledInstID + "/InstanceID" );
  			if( !instidValue.getString().equals( appHandle.getInstanceId() ) )
  				throw new Exception("InstanceID node has illegal value!");
  			
  			DmtData stateValue = session.getNodeValue( "./OSGi/Application/" + mangledAppUID + "/Instances/" + mangledInstID + "/State" );
  			if( !stateValue.getString().equals( ApplicationHandle.RUNNING ) )
  				throw new Exception( "Bad state value (" + stateValue.getInt() + " " + 
  						                  ApplicationHandle.RUNNING + ")!" );

  			if( ! testCase_pauseApplication() )
  				return false;

  			stateValue = session.getNodeValue( "./OSGi/Application/" + mangledAppUID + "/Instances/" + mangledInstID + "/State" );
  			if( !stateValue.getString().equals( MidletHandle.PAUSED ) )
  				throw new Exception( "Bad state value (" + stateValue.getInt() + " " + 
  						MidletHandle.PAUSED + ")!" );
  			
  			if( ! testCase_resumeApplication() )
  				return false;

  			stateValue = session.getNodeValue( "./OSGi/Application/" + mangledAppUID + "/Instances/" + mangledInstID + "/State" );
  			if( !stateValue.getString().equals( ApplicationHandle.RUNNING ) )
  				throw new Exception( "Bad state value (" + stateValue.getInt() + " " + 
            ApplicationHandle.RUNNING + ")!" );

  			session.close();

  			if( !testCase_stopApplication() )
  				return false;
  			
  			return true;
  		}
  		catch (Exception e) {
  			e.printStackTrace();
  			return false;
  		}
  	}
  	
  	boolean testCase_appPluginCheckApplicationLaunch()
  	{
  		ApplicationDescriptor appDesc = appDescs[0];
  		String appUID = getPID( appDesc );
			String mangledAppUID = mangle( appUID ); 
  		
  		try {
  			DmtSession session = dmtFactory.getSession("./OSGi/Application");
  			
  			String[] launchNode = session.getChildNodeNames( "./OSGi/Application/" + mangledAppUID + "/Operations" );
  			boolean found = false;
  			for( int q=0; q != launchNode.length; q++ )
  				if( launchNode[ q ].equals( "Launch" ) ) {
  					found = true;
  					break;
  				}
  			if( !found )
  				throw new Exception( "Launch node is missing!" );
  			
  			String[] nodeNames = session.getChildNodeNames( "./OSGi/Application/" + mangledAppUID + "/Operations/Launch" );
  			if( nodeNames != null && nodeNames.length != 0 )
  				throw new Exception( "Launch-id found without setting it manually!" );
  		
  			session.createInteriorNode( "./OSGi/Application/" + mangledAppUID + "/Operations/Launch/my_launch_id" );
  			
  			nodeNames = session.getChildNodeNames( "./OSGi/Application/" + mangledAppUID + "/Operations/Launch" );
  			if( nodeNames == null || nodeNames.length != 1 )
  				throw new Exception( "Interior node wasn't created properly!" );
  			if( !nodeNames[ 0 ].equals("my_launch_id") )
  				throw new Exception( "The name of the interior node is " + nodeNames [ 0 ] + 
  						                  "instead if my_launch_id !" );
  			
  			String[] childNodes = session.getChildNodeNames( "./OSGi/Application/" + mangledAppUID + "/Operations/Launch/my_launch_id" );
  			if( childNodes == null || childNodes.length != 2 )
  				throw new Exception( "Invalid parameters placed into to the my_launch_id interior node!" );
  			List childList = Arrays.asList( childNodes );  			
  			if( childList.indexOf( "Arguments" ) == -1 || childList.indexOf( "Result" ) == -1 )
  				throw new Exception( "Invalid child nodes of my_launch_id!" );
  			
  			String[] resultNodes = session.getChildNodeNames( "./OSGi/Application/" + mangledAppUID + "/Operations/Launch/my_launch_id/Result" );
  			if( resultNodes == null || resultNodes.length != 3 )
  				throw new Exception( "Invalid parameters placed into to the my_launch_id/Result interior node!" );
  			List resultList = Arrays.asList( resultNodes );  			
  			if( resultList.indexOf( "InstanceID" ) == -1 || resultList.indexOf( "Status" ) == -1 || 
  					resultList.indexOf( "Message" ) == -1 )
  				throw new Exception( "Invalid child nodes of the my_launch_id/Result!" );

  			String resultInstance = session.getNodeValue( "./OSGi/Application/" + mangledAppUID + "/Operations/Launch/my_launch_id/Result/InstanceID" ).getString();
  			String resultStatus = session.getNodeValue( "./OSGi/Application/" + mangledAppUID + "/Operations/Launch/my_launch_id/Result/Status" ).getString();
  			String resultMessage = session.getNodeValue( "./OSGi/Application/" + mangledAppUID + "/Operations/Launch/my_launch_id/Result/Message" ).getString();
  			
  			if( !resultInstance.equals("") || !resultStatus.equals("") || !resultMessage.equals("") )
  				throw new Exception( "Invalid default values for Result subnodes!" );
  			
  			String[] argNodes = session.getChildNodeNames( "./OSGi/Application/" + mangledAppUID + "/Operations/Launch/my_launch_id/Arguments" );
  			if( argNodes != null && argNodes.length != 0 )
  				throw new Exception( "Extra parameters placed into to the my_launch_id/Arguments interior node!" );

  			session.createInteriorNode( "./OSGi/Application/" + mangledAppUID + "/Operations/Launch/my_launch_id/Arguments/dummyArg" );
  			argNodes = session.getChildNodeNames( "./OSGi/Application/" + mangledAppUID + "/Operations/Launch/my_launch_id/Arguments" );
  			if( argNodes == null || argNodes.length != 1 )
  				throw new Exception( "The my_launch_id/Arguments/dummyArg interior node was not created!" );

  			session.deleteNode( "./OSGi/Application/" + mangledAppUID + "/Operations/Launch/my_launch_id/Arguments/dummyArg" );

  			argNodes = session.getChildNodeNames( "./OSGi/Application/" + mangledAppUID + "/Operations/Launch/my_launch_id/Arguments" );
  			if( argNodes != null && argNodes.length != 0 )
  				throw new Exception( "The my_launch_id/Arguments/dummyArg interior node was not deleted!" );
  			
  			Map args = createArgs();
  			int argIDcnt = 0;
  			
  			Iterator it = args.keySet().iterator();
  			while( it.hasNext() )
  			{
  				String prop = (String)it.next();
  				String value = (String)args.get( prop );
  				
  				String argID = "Arg" + argIDcnt++;
  				
    			session.createInteriorNode( "./OSGi/Application/" + mangledAppUID + "/Operations/Launch/my_launch_id/Arguments/" + argID );
  				
    			argNodes = session.getChildNodeNames( "./OSGi/Application/" + mangledAppUID + "/Operations/Launch/my_launch_id/Arguments" );
    			if( argNodes == null || argNodes.length != argIDcnt )
    				throw new Exception( "The my_launch_id/Arguments/<arg_id> interior node was not created!" );
    			boolean foundArgID = false;
    			for( int w=0; w != argNodes.length; w++ )
    				if( argNodes[ w ].equals( argID ) ) {
    					found = true;
    					break;
    				}
    			
    			String []argIDNodes = session.getChildNodeNames( "./OSGi/Application/" + mangledAppUID + "/Operations/Launch/my_launch_id/Arguments/" + argID );
    			if( argIDNodes == null || argIDNodes.length != 2 )
    				throw new Exception( "Invalid leaf nodes of my_launch_id/Arguments/<arg_id>/" );
    			if( !( ( argIDNodes[ 0 ].equals( "Name" ) && argIDNodes[ 1 ].equals( "Value" ) ) ||
    					 ( argIDNodes[ 0 ].equals( "Value" ) && argIDNodes[ 1 ].equals( "Name" ) ) ) )
    				throw new Exception( "Invalid leaf nodes of my_launch_id/Arguments/<arg_id>/" );
    			
    			String defaultName = session.getNodeValue( "./OSGi/Application/" + mangledAppUID + "/Operations/Launch/my_launch_id/Arguments/" + argID + "/Name" ).getString();
    			if( !defaultName.equals("") )
    				throw new Exception( "Invalid default value for my_launch_id/Arguments/<arg>/Name!" );
    			DmtData defaultValue = session.getNodeValue( "./OSGi/Application/" + mangledAppUID + "/Operations/Launch/my_launch_id/Arguments/" + argID + "/Value" );
    			if( defaultValue.getFormat() != DmtData.FORMAT_NULL )
    				throw new Exception( "Invalid default value for my_launch_id/Arguments/<arg>/Value!" );
    			
    			session.setNodeValue( "./OSGi/Application/" + mangledAppUID + "/Operations/Launch/my_launch_id/Arguments/" + argID + "/Name", new DmtData( prop ) );
          if( !session.getNodeValue( "./OSGi/Application/" + mangledAppUID + "/Operations/Launch/my_launch_id/Arguments/" + argID + "/Name" ).getString().equals( prop ) )
    				throw new Exception( "The my_launch_id/Arguments/<arg>/Name was not set correctly!" );          	
          
    			session.setNodeValue( "./OSGi/Application/" + mangledAppUID + "/Operations/Launch/my_launch_id/Arguments/" + argID + "/Value", new DmtData( value ) );
          if( !session.getNodeValue( "./OSGi/Application/" + mangledAppUID + "/Operations/Launch/my_launch_id/Arguments/" + argID + "/Value" ).getString().equals( value ) )
    				throw new Exception( "The my_launch_id/Arguments/<arg>/Value was not set correctly!" );          	    			
  			}
  			
  			session.execute( "./OSGi/Application/" + mangledAppUID + "/Operations/Launch/my_launch_id", null );

  			resultInstance = session.getNodeValue( "./OSGi/Application/" + mangledAppUID + "/Operations/Launch/my_launch_id/Result/InstanceID" ).getString();  			
  			resultStatus = session.getNodeValue( "./OSGi/Application/" + mangledAppUID + "/Operations/Launch/my_launch_id/Result/Status" ).getString();
  			if( !resultStatus.equals("OK") )
  				throw new Exception("Invalid value for result status!");
  			resultMessage = session.getNodeValue( "./OSGi/Application/" + mangledAppUID + "/Operations/Launch/my_launch_id/Result/Message" ).getString();
  			if( !resultMessage.equals("") )
  				throw new Exception("Invalid value for result message!");
  			
  			if (!checkResultFile("START"))
  				throw new Exception("Result of the launch is not START!");
  			ServiceReference[] appList = bc.getServiceReferences(
  					ApplicationHandle.class.getName(),
  					"(" + ApplicationHandle.APPLICATION_DESCRIPTOR + "=" + getPID( appDesc ) + ")");
  			if (appList == null || appList.length == 0)
  				throw new Exception("No registered application handle found!");

  			appHandle = (ApplicationHandle) bc.getService(appList[0]);
  			
  			if( !resultInstance.equals( appHandle.getInstanceId() ) )
  				throw new Exception("Result instance was not set properly!");
  			
  			if ( !appHandle.getState().equals( ApplicationHandle.RUNNING ) )
  				throw new Exception("Application didn't started!");

  			bc.ungetService( appList[0] );

  			session.deleteNode( "./OSGi/Application/" + mangledAppUID + "/Operations/Launch/my_launch_id" );

  			nodeNames = session.getChildNodeNames( "./OSGi/Application/" + mangledAppUID + "/Operations/Launch" );
  			if( nodeNames != null && nodeNames.length != 0 )
  				throw new Exception( "my_launch_id wasn't removed after deleting its node!" );
  			
  			if( !testCase_stopApplication() )
  				return false;
  			
  			session.close();
  			
  			return true;
  		}
  		catch (Exception e) {
  			e.printStackTrace();
  			return false;
  		}
  	}
  	
  	boolean testCase_appPluginCheckApplicationStop() {
  		ApplicationDescriptor appDesc = appDescs[0];
  		String appUID = getPID( appDesc );
			String mangledAppUID = mangle( appUID ); 
  		
  		try {
  			
  			if( !testCase_launchApplication() )
  				return false;
  			
  			DmtSession session = dmtFactory.getSession("./OSGi/Application/" + mangledAppUID + "/Instances");

  			String[] nodeNames = session.getChildNodeNames( "./OSGi/Application/" + mangledAppUID + "/Instances" );
  			
  			if( nodeNames == null || nodeNames.length != 1 )
  				throw new Exception( "Couldn't find the application instance node!" );
  			
  			String instanceName = nodeNames[ 0 ];
  			
  			String[] operationNames = session.getChildNodeNames( "./OSGi/Application/" + mangledAppUID + "/Instances/" + instanceName + "/Operations" );  			
  			if( operationNames == null || operationNames.length == 0 || operationNames.length > 2 )
  				throw new Exception( "Invalid child nodes of the application instance operations!" );

  			List childList = Arrays.asList( operationNames );  			
  			if( childList.indexOf( "Stop" ) == -1 )
  				throw new Exception( "Invalid child nodes of the application instance operations!" );
  			
  			session.execute( "./OSGi/Application/" + mangledAppUID + "/Instances/" + instanceName + "/Operations/Stop", "STOP" );			

  			nodeNames = session.getChildNodeNames( "./OSGi/Application/" + mangledAppUID + "/Instances" );
  			if( nodeNames != null && nodeNames.length != 0 )
  				throw new Exception( "Application didn't stop!" );
  			
  			if (!checkResultFile("STOP"))
  				throw new Exception("Result of the stop is not STOP!");

  			ServiceReference[] appList = bc.getServiceReferences(
  					ApplicationHandle.class.getName(),
  					"(" + ApplicationHandle.APPLICATION_DESCRIPTOR + "="
  							+ appUID + ")");
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

  			session.close();
  			
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
  	  	
  	boolean testCase_appPluginLock() {
  		ApplicationDescriptor appDesc = appDescs[0];
  		String appUID = getPID( appDesc );
			String mangledAppUID = mangle( appUID ); 
  		
  		try {
  			if( isLocked( appDesc ) )
  				throw new Exception( "ApplicationDescriptor is unexpectedly locked!" );
  			
  			DmtSession session = dmtFactory.getSession("./OSGi/Application" );
  			DmtData value = session.getNodeValue("./OSGi/Application/" + mangledAppUID +"/Locked" );			
  			if( value.getBoolean() )
  				throw new Exception( "Application is unlocked, but AppPlugin reports locked!" );

  			String[] operationNames = session.getChildNodeNames( "./OSGi/Application/" + mangledAppUID + "/Operations" );  			
  			if( operationNames == null || operationNames.length < 2 )
  				throw new Exception( "Invalid child nodes of the application instance operations!" );
  			List childList = Arrays.asList( operationNames );  			
  			if( childList.indexOf( "Lock" ) == -1 || childList.indexOf( "Unlock" ) == -1 )
  				throw new Exception( "Invalid child nodes of the operations node!" );
  			
  			session.execute( "./OSGi/Application/" + mangledAppUID +"/Operations/Lock", "" );
  			
  			if( !isLocked( appDesc ) )
  				throw new Exception( "AppPlugin failed to set the application locked!" );
  			value = session.getNodeValue("./OSGi/Application/" + mangledAppUID +"/Locked" );			
  			if( !value.getBoolean() )
  				throw new Exception( "Application is locked, but AppPlugin reports unlocked!" );
  			
  			session.execute( "./OSGi/Application/" + mangledAppUID +"/Operations/Unlock", "" );

  			if( isLocked( appDesc ) )
  				throw new Exception( "AppPlugin failed to set the application unlocked!" );
  			value = session.getNodeValue("./OSGi/Application/" + mangledAppUID +"/Locked" );			
  			if( value.getBoolean() )
  				throw new Exception( "Application is unlocked, but AppPlugin reports locked!" );
  			
  			session.close();
  			return true;
  		}
  		catch (Exception e) {
  			e.printStackTrace();
  			return false;
  		}							
  	}
  	
  	boolean testCase_appPluginScheduleSynchronization() {
  		ApplicationDescriptor appDesc = appDescs[0];
  		String appUID = getPID( appDesc );
			String mangledAppUID = mangle( appUID ); 
  		
  		try {
  			DmtSession session = dmtFactory.getSession("./OSGi/Application" );

  			String[] scheduleNames = session.getChildNodeNames( "./OSGi/Application/" + mangledAppUID + "/Schedules" );  			
  			if( scheduleNames != null && scheduleNames.length != 0 )
  				throw new Exception( "Schedule found without scheduling an application!" );

  			HashMap args = new HashMap();
  			args.put( "String", "String" );
  			args.put( "Boolean", new Boolean( true ) );
  			args.put( "Integer", new Integer( 23 ) );
  			args.put( "null", null );
  			args.put( "Unmappable", new Hashtable() );
  			args.put( "Binary", new byte [] {1,2,3});
  			args.put( "Float", new Float( 2.25 ));
  			
  			String topicFilter = "x/y/z/*";
  			String eventFilter = "(Zizi=OKSA)";
  			boolean recurring = false;
  			
  			ScheduledApplication schedApp = appDesc.schedule( null, args, topicFilter, eventFilter, recurring );
  			
  			scheduleNames = session.getChildNodeNames( "./OSGi/Application/" + mangledAppUID + "/Schedules" );  			
  			if( scheduleNames != null && scheduleNames.length != 1 )
  				throw new Exception( "Schedule was not registered in ApplicationPlugin!" );
  			
  			String schedID = scheduleNames[ 0 ];

  			String[] scheduleChildNames = session.getChildNodeNames( "./OSGi/Application/" + mangledAppUID + "/Schedules/" + schedID );  			
  			if( scheduleChildNames == null || scheduleChildNames.length != 5 )
  				throw new Exception( "Invalid items found in the schedule node!" );  			
  			List schedChildList = Arrays.asList( scheduleChildNames );
  			
  			if( schedChildList.indexOf( "Enabled" ) == -1 || 
  					schedChildList.indexOf( "TopicFilter" ) == -1 ||
						schedChildList.indexOf( "EventFilter" ) == -1 ||
						schedChildList.indexOf( "Recurring" ) == -1 ||
						schedChildList.indexOf( "Arguments" ) == -1 )
  				throw new Exception( "Schedule child node was not found!" );

  			DmtData enabledValue = session.getNodeValue( "./OSGi/Application/" + mangledAppUID + "/Schedules/" + schedID + "/Enabled");
  			DmtData topicFilterValue = session.getNodeValue( "./OSGi/Application/" + mangledAppUID + "/Schedules/" + schedID + "/TopicFilter");
  			DmtData eventFilterValue = session.getNodeValue( "./OSGi/Application/" + mangledAppUID + "/Schedules/" + schedID + "/EventFilter");
  			DmtData recurringValue = session.getNodeValue( "./OSGi/Application/" + mangledAppUID + "/Schedules/" + schedID + "/Recurring");
  			
  			if( enabledValue.getBoolean() != true )
  				throw new Exception( "Invalid enabled value!" );  			
  			if( !topicFilterValue.getString().equals( topicFilter ) )
  				throw new Exception( "Invalid topic filter value!" );  			
  			if( !eventFilterValue.getString().equals( eventFilter ) )
  				throw new Exception( "Invalid event filter value!" );  			
  			if( recurringValue.getBoolean() != recurring )
  				throw new Exception( "Invalid recurring value!" );  			
  				
  			String[] argumentPairs = session.getChildNodeNames( "./OSGi/Application/" + mangledAppUID + "/Schedules/" + schedID + "/Arguments" );
  			if( argumentPairs == null || argumentPairs.length != args.size() )
  				throw new Exception( "Invalid argument number was received!" );
  			
  			for( int q=0; q != argumentPairs.length; q++ ) {
  				String argID = argumentPairs[ q ];
  				String [] pairElems =  session.getChildNodeNames( "./OSGi/Application/" + mangledAppUID + "/Schedules/" + schedID + "/Arguments/" + argID );
  				if( pairElems == null || ( pairElems.length != 2 && pairElems.length != 1) )
  					throw new Exception( "Invalid element number!" );

  				List pairElemList = Arrays.asList( pairElems );
    			
    			if( pairElemList.indexOf( "Name" ) == -1 )
    				throw new Exception( "Name is missing from the list" );
    			
    			if( pairElems.length == 2 && pairElemList.indexOf( "Value" ) == -1 )
    				throw new Exception( "Value is missing from the list" );
  				
    			String key = session.getNodeValue( "./OSGi/Application/" + mangledAppUID + "/Schedules/" + schedID + "/Arguments/" + argID + "/Name" ).getString();
    			Object content = args.get( key );
    			if( key.equals( "Unmappable" ) ) {
    				if( pairElems.length != 1 )
    					throw new Exception("AppPlugin mapped an unmappable element!");
    				continue;    			
    			}
    			if( pairElems.length != 2 )
    				throw new Exception("Value is missing from a mappable element!");
    			
    			DmtData argValue = session.getNodeValue( "./OSGi/Application/" + mangledAppUID + "/Schedules/" + schedID + "/Arguments/" + argID + "/Value" );
    			
    			if( content == null ) {
    				if( argValue.getFormat() != DmtData.FORMAT_NULL )
    					throw new Exception("Invalid null value");
    				continue;
    			}
    			else if( content instanceof Boolean ) {
    				if( argValue.getBoolean() != ((Boolean)content).booleanValue() )
    					throw new Exception("Invalid Boolean value");
    				continue;
    			}
    			else if( content instanceof Integer ) {
    				if( argValue.getInt() != ((Integer)content).intValue() )
    					throw new Exception("Invalid Integer value");
    				continue;
    			}
    			else if( content instanceof Float ) {
    				if( argValue.getFloat() != ((Float)content).floatValue() )
    					throw new Exception("Invalid Float value");
    				continue;
    			}
    			else if( content instanceof byte [] ) {
    				byte recvBin[] = argValue.getBinary();
    				byte expBin[] = (byte [])content;
    				
    				if( recvBin.length != expBin.length )
    					throw new Exception( "Invalid binary value" );
    				
    				for( int p=0; p != expBin.length; p++ )
    					if( recvBin[ p ] != expBin[ p ] )
    						throw new Exception( "Invalid binary value" );    				
    				continue;
    			}
    			else if( content instanceof String ) {
    				if( !argValue.getString().equals( content ) )
  						throw new Exception( "Invalid string value" );    				    					
    				continue;
    			}
    			throw new Exception("Invalid mapping received!");
  			}
  			
  			schedApp.remove();

  			scheduleNames = session.getChildNodeNames( "./OSGi/Application/" + mangledAppUID + "/Schedules" );  			
  			if( scheduleNames != null && scheduleNames.length != 0 )
  				throw new Exception( "Schedule was not deleted from the registry after remove!" );
  			
  			session.close();  			
  			return true;
  		}
  		catch (Exception e) {
  			e.printStackTrace();
  			return false;
  		}							  		
  	}
  	
  	boolean testCase_appPluginModifySchedule() {
  		ApplicationDescriptor appDesc = appDescs[0];
  		String appUID = getPID( appDesc );
			String mangledAppUID = mangle( appUID ); 
  		
  		try {
  			DmtSession session = dmtFactory.getSession("./OSGi/Application" );

  			HashMap args = new HashMap();  			
  			String topicFilter = "x/y/z/*";
  			String eventFilter = "(Zizi=OKSA)";
  			boolean recurring = true;  			
  			ScheduledApplication schedApp = appDesc.schedule( null, args, topicFilter, eventFilter, recurring );

  			String[] scheduleNames = session.getChildNodeNames( "./OSGi/Application/" + mangledAppUID + "/Schedules" );  			
  			if( scheduleNames == null || scheduleNames.length != 1 )
  				throw new Exception( "Schedule was not registered in the ApplicationPlugin!" );
  			String schedID = scheduleNames[ 0 ];
  			
  			DmtData newTopicFilter = new DmtData("a/y/z/*");
  			session.setNodeValue( "./OSGi/Application/" + mangledAppUID + "/Schedules/" + schedID + "/TopicFilter", newTopicFilter);
  			if( !session.getNodeValue( "./OSGi/Application/" + mangledAppUID + "/Schedules/" + schedID + "/TopicFilter" )
  					   .getString().equals( newTopicFilter.getString() ))
  				throw new Exception("TopicFilter cannot be modified!");
  			
  			if( session.getNodeValue( "./OSGi/Application/" + mangledAppUID + "/Schedules/" + schedID + "/Enabled" ).getBoolean() )
  				throw new Exception("Schedule was not disabled after TopicFilter modification!");
  			
  			ServiceReference refs[] = bc.getServiceReferences( ScheduledApplication.class.getName(), null );
  			if( refs != null && refs.length != 0 )
  				throw new Exception("Schedule was not disabled by the modification!");

  			session.setNodeValue( "./OSGi/Application/" + mangledAppUID + "/Schedules/" + schedID + "/Enabled", new DmtData( true ));
  			if( !session.getNodeValue( "./OSGi/Application/" + mangledAppUID + "/Schedules/" + schedID + "/Enabled" ).getBoolean() )
  				throw new Exception("Cannot set the enabled field to true!");

  			refs = bc.getServiceReferences( ScheduledApplication.class.getName(), null );
  			if( refs == null || refs.length != 1 )
  				throw new Exception("Schedule was not registered by setting Enabled to true!");
  			
  			DmtData newEventFilter = new DmtData( "(Zizi=Hali)" );
  			session.setNodeValue( "./OSGi/Application/" + mangledAppUID + "/Schedules/" + schedID + "/EventFilter", newEventFilter);
  			if( !session.getNodeValue( "./OSGi/Application/" + mangledAppUID + "/Schedules/" + schedID + "/EventFilter" )
  					   .getString().equals( newEventFilter.getString() ))
  				throw new Exception("EventFilter cannot be modified!");

  			if( session.getNodeValue( "./OSGi/Application/" + mangledAppUID + "/Schedules/" + schedID + "/Enabled" ).getBoolean() )
  				throw new Exception("Schedule was not disabled after EventFilter modification!");
  			session.setNodeValue( "./OSGi/Application/" + mangledAppUID + "/Schedules/" + schedID + "/Enabled", new DmtData( true ));
  			if( !session.getNodeValue( "./OSGi/Application/" + mangledAppUID + "/Schedules/" + schedID + "/Enabled" ).getBoolean() )
  				throw new Exception("Cannot set the enabled field to true!");

  			DmtData newRecurring = new DmtData( false );
  			session.setNodeValue( "./OSGi/Application/" + mangledAppUID + "/Schedules/" + schedID + "/Recurring", newRecurring);
  			if( session.getNodeValue( "./OSGi/Application/" + mangledAppUID + "/Schedules/" + schedID + "/Recurring" ).getBoolean() )
   				throw new Exception("Recurring cannot be modified!");

  			if( session.getNodeValue( "./OSGi/Application/" + mangledAppUID + "/Schedules/" + schedID + "/Enabled" ).getBoolean() )
  				throw new Exception("Schedule was not disabled after Recurring modification!");
  			session.setNodeValue( "./OSGi/Application/" + mangledAppUID + "/Schedules/" + schedID + "/Enabled", new DmtData( true ));
  			if( !session.getNodeValue( "./OSGi/Application/" + mangledAppUID + "/Schedules/" + schedID + "/Enabled" ).getBoolean() )
  				throw new Exception("Cannot set the enabled field to true!");

  			session.createInteriorNode( "./OSGi/Application/" + mangledAppUID + "/Schedules/" + schedID + "/Arguments/Arg0" );
  			session.setNodeValue( "./OSGi/Application/" + mangledAppUID + "/Schedules/" + schedID + "/Arguments/Arg0/Name", new DmtData( "nyuszi") );

  			if( session.getNodeValue( "./OSGi/Application/" + mangledAppUID + "/Schedules/" + schedID + "/Enabled" ).getBoolean() )
  				throw new Exception("Schedule was not disabled after adding a new argument node!");
  			session.setNodeValue( "./OSGi/Application/" + mangledAppUID + "/Schedules/" + schedID + "/Enabled", new DmtData( true ));
  			  			
  			if( !session.getNodeValue( "./OSGi/Application/" + mangledAppUID + "/Schedules/" + schedID + "/Enabled" ).getBoolean() )
  				throw new Exception("Cannot set the enabled field to true!");

  			session.setNodeValue( "./OSGi/Application/" + mangledAppUID + "/Schedules/" + schedID + "/Arguments/Arg0/Name", 
  					                  new DmtData( "Boolean" ));
  			
  			if( session.getNodeValue( "./OSGi/Application/" + mangledAppUID + "/Schedules/" + schedID + "/Enabled" ).getBoolean() )
  				throw new Exception("Schedule was not disabled after changing the value of a node!");
  			
  			session.setNodeValue( "./OSGi/Application/" + mangledAppUID + "/Schedules/" + schedID + "/Arguments/Arg0/Value", 
                              new DmtData( true ));
  			session.createInteriorNode( "./OSGi/Application/" + mangledAppUID + "/Schedules/" + schedID + "/Arguments/Arg1" );
  			session.setNodeValue( "./OSGi/Application/" + mangledAppUID + "/Schedules/" + schedID + "/Arguments/Arg1/Name", 
  					                  new DmtData( "Integer" ));
  			session.setNodeValue( "./OSGi/Application/" + mangledAppUID + "/Schedules/" + schedID + "/Arguments/Arg1/Value", 
                              new DmtData( 23 ));
  			session.createInteriorNode( "./OSGi/Application/" + mangledAppUID + "/Schedules/" + schedID + "/Arguments/Arg2" );
  			session.setNodeValue( "./OSGi/Application/" + mangledAppUID + "/Schedules/" + schedID + "/Arguments/Arg2/Name", 
  					                  new DmtData( "String" ));
  			session.setNodeValue( "./OSGi/Application/" + mangledAppUID + "/Schedules/" + schedID + "/Arguments/Arg2/Value", 
                              new DmtData( "Hali" ));
  			session.createInteriorNode( "./OSGi/Application/" + mangledAppUID + "/Schedules/" + schedID + "/Arguments/Arg3" );
  			session.setNodeValue( "./OSGi/Application/" + mangledAppUID + "/Schedules/" + schedID + "/Arguments/Arg3/Name", 
  					                  new DmtData( "null" ));
  			session.setNodeValue( "./OSGi/Application/" + mangledAppUID + "/Schedules/" + schedID + "/Arguments/Arg3/Value", 
                              DmtData.NULL_VALUE);
  			session.createInteriorNode( "./OSGi/Application/" + mangledAppUID + "/Schedules/" + schedID + "/Arguments/Arg4" );
  			session.setNodeValue( "./OSGi/Application/" + mangledAppUID + "/Schedules/" + schedID + "/Arguments/Arg4/Name", 
  					                  new DmtData( "Binary" ));
  			session.setNodeValue( "./OSGi/Application/" + mangledAppUID + "/Schedules/" + schedID + "/Arguments/Arg4/Value", 
                              new DmtData( new byte[] {1,2,3} ));
  			session.createInteriorNode( "./OSGi/Application/" + mangledAppUID + "/Schedules/" + schedID + "/Arguments/Arg5" );
  			session.setNodeValue( "./OSGi/Application/" + mangledAppUID + "/Schedules/" + schedID + "/Arguments/Arg5/Name", 
  					                  new DmtData( "Float" ));
  			session.setNodeValue( "./OSGi/Application/" + mangledAppUID + "/Schedules/" + schedID + "/Arguments/Arg5/Value", 
                              new DmtData( (float)12.5 ));

  			session.setNodeValue( "./OSGi/Application/" + mangledAppUID + "/Schedules/" + schedID + "/Enabled", new DmtData( true ));
  			if( !session.getNodeValue( "./OSGi/Application/" + mangledAppUID + "/Schedules/" + schedID + "/Enabled" ).getBoolean() )
  				throw new Exception("Cannot set the enabled field to true!");
  			
  			refs = bc.getServiceReferences( ScheduledApplication.class.getName(), null );
  			if( refs == null || refs.length != 1 )
  				throw new Exception("Schedule was not registered by setting Enabled to true!");
  			
  			schedApp = (ScheduledApplication)bc.getService( refs[ 0 ]);
  			
  			Map newArgs = schedApp.getArguments();
  			
  			if( newArgs.size() != 6 )
  				throw new Exception("Invalid argument size!");
  			
  			if( !newArgs.containsKey( "null" ) )
  				throw new Exception("Null variable disappeared!");
  			if( newArgs.get( "null" ) != null )
  				throw new Exception("Invalid null value!");
  			if( !newArgs.get( "String" ).equals( "Hali" ) )
  				throw new Exception("Invalid string value!");
  			if( !((Boolean)newArgs.get( "Boolean" )).booleanValue() )
  				throw new Exception("Invalid string value!");
  			if( ((Integer)newArgs.get( "Integer" )).intValue() != 23)
  				throw new Exception("Invalid integer value!");
  			if( ((Float)newArgs.get( "Float" )).floatValue() != (float)12.5)
  				throw new Exception("Invalid float value!");
  			
  			byte [] bin = (byte [])newArgs.get( "Binary" );
  			if( bin.length != 3 )
  				throw new Exception("Invalid binary length!");
  			if( bin[ 0 ] != 1 || bin[ 1 ] != 2 || bin[ 2 ] != 3 )
  				throw new Exception("Invalid binary value!");
  			
  			schedApp.remove();
				
  			session.close();  			
  			return true;
  		}
  		catch (Exception e) {
  			e.printStackTrace();
  			return false;
  		}  		
  	}
  	
    boolean testCase_appPluginCreateAndDeleteSchedule() {
  		ApplicationDescriptor appDesc = appDescs[0];
  		String appUID = getPID( appDesc );
			String mangledAppUID = mangle( appUID ); 
  		
  		try {
  			DmtSession session = dmtFactory.getSession("./OSGi/Application" );
  			
  			Map args = createArgs();
  			
  			session.createInteriorNode( "./OSGi/Application/" + mangledAppUID + "/Schedules/my_schedule_id" );

  			DmtData topicFilter = new DmtData("com/nokia/test/ScheduleEvent");
  			session.setNodeValue( "./OSGi/Application/" + mangledAppUID + "/Schedules/my_schedule_id/TopicFilter", topicFilter);
  			if( !session.getNodeValue( "./OSGi/Application/" + mangledAppUID + "/Schedules/my_schedule_id/TopicFilter" )
  					   .getString().equals( topicFilter.getString() ))
  				throw new Exception("TopicFilter cannot be modified!");

  			DmtData eventFilter = new DmtData( "" );
  			session.setNodeValue( "./OSGi/Application/" + mangledAppUID + "/Schedules/my_schedule_id/EventFilter", eventFilter);
  			if( !session.getNodeValue( "./OSGi/Application/" + mangledAppUID + "/Schedules/my_schedule_id/EventFilter" )
  					   .getString().equals("") )
  				throw new Exception("EventFilter cannot be modified!");
  			
  			DmtData recurring = new DmtData( true );
  			session.setNodeValue( "./OSGi/Application/" + mangledAppUID + "/Schedules/my_schedule_id/Recurring", recurring);
  			if( !session.getNodeValue( "./OSGi/Application/" + mangledAppUID + "/Schedules/my_schedule_id/Recurring" ).getBoolean() )
   				throw new Exception("Recurring cannot be modified!");
  			
  			int argCnt = 1;
  			Iterator iter = args.keySet().iterator();
  			while( iter.hasNext() ) {
  				String key = (String)iter.next();
  				String value = (String)args.get( key );
  				String argID = "Arg" + argCnt++;
  				
    			session.createInteriorNode( "./OSGi/Application/" + mangledAppUID + "/Schedules/my_schedule_id/Arguments/" + argID );
    			session.setNodeValue( "./OSGi/Application/" + mangledAppUID + "/Schedules/my_schedule_id/Arguments/" + argID +  "/Name", 
    					                  new DmtData( key ));
    			session.setNodeValue( "./OSGi/Application/" + mangledAppUID + "/Schedules/my_schedule_id/Arguments/" + argID + "/Value", 
                                new DmtData( value ));
  			}

  			session.setNodeValue( "./OSGi/Application/" + mangledAppUID + "/Schedules/my_schedule_id/Enabled", new DmtData( true ));
  			if( !session.getNodeValue( "./OSGi/Application/" + mangledAppUID + "/Schedules/my_schedule_id/Enabled" ).getBoolean() )
   				throw new Exception("Recurring cannot be modified!");

  			sendEvent(new Event( "com/nokia/test/ScheduleEvent", null), false);
  			
  			appHandle = lookupAppHandle(appDesc);
  			if (appHandle == null
  					|| !appHandle.getState().equals( ApplicationHandle.RUNNING ) )
  				throw new Exception("Application wasn't scheduled!");
  			if (!checkResultFile("START"))
  				throw new Exception("Result of the schedule is not START!");
  			if (!testCase_stopApplication())
  				return false;
  			
  			session.deleteNode( "./OSGi/Application/" + mangledAppUID + "/Schedules/my_schedule_id" );

  			String[] scheduleNames = session.getChildNodeNames( "./OSGi/Application/" + mangledAppUID + "/Schedules" );  			
  			if( scheduleNames != null && scheduleNames.length != 0 )
  				throw new Exception( "Schedule was not registered in the ApplicationPlugin!" );

  			ServiceReference refs[] = bc.getServiceReferences( ScheduledApplication.class.getName(), null );
  			if( refs != null && refs.length != 0 )
  				throw new Exception("Schedule was not disabled after delete!");

  			sendEvent(new Event( "com/nokia/test/ScheduleEvent", null), false);
  			
  			appHandle = lookupAppHandle(appDesc);
  			if (appHandle != null)
  				throw new Exception("Schedule was not properly deleted!");
  			
  			session.close();  			
  			return true;
  		}
  		catch (Exception e) {
  			e.printStackTrace();
  			return false;
  		}  		    	
    }
  	
  	boolean testCase_checkNotifyDestroyed() {
  		try {
  			
  			if( !testCase_launchApplication() )
  				return false;

  			String pid = getPID(getAppDesc(appHandle));

  			sendEvent(new Event( "com/nokia/megtest/NotifyDestroyed", null), false);
  			
  			try {
  			  while( true ) {
  			  	Thread.sleep( 100L );
  			    appHandle.getState();
  			  }
  			}catch( Exception e ) {}
  			
  			checkApplicationStop( pid );
  			
  			return true;
  		}
  		catch (Exception e) {
  			e.printStackTrace();
  			return false;
  		}							  		
  	}
  	
  	boolean testCase_checkNotifyPaused() {
  		try {
  			
  			if( !testCase_launchApplication() )
  				return false;

  			sendEvent(new Event( "com/nokia/megtest/NotifyPaused", null), false);
  			
 			  while( appHandle.getState() != MidletHandle.PAUSED ) {
  			  	Thread.sleep( 100L );
 			  }
  			
  			if( !testCase_stopApplication() )
  				return false;
  			
  			return true;
  		}
  		catch (Exception e) {
  			e.printStackTrace();
  			return false;
  		}							  		
  	}  
  	
  	boolean testCase_checkResumeRequest() {
  		try {
  			
  			if( !testCase_launchApplication() )
  				return false;

  			if( !testCase_pauseApplication() )
  				return false;  			
  			
  			sendEvent(new Event( "com/nokia/megtest/ResumeRequest", null), false);
  			
 			  while( appHandle.getState() != ApplicationHandle.RUNNING ) {
  			  	Thread.sleep( 100L );
 			  }
  			
  			if( !testCase_stopApplication() )
  				return false;
  			
  			return true;
  		}
  		catch (Exception e) {
  			e.printStackTrace();
  			return false;
  		}							  		
  	}  

  	boolean testCase_checkOrphanedSchedules() {
  		try {
  			if( !testCase_installMidletBundle() )
  				return false;
  			
  			ApplicationDescriptor appDesc = appDescs[0];
  			String pid = appDesc.getApplicationId();
  			String mangledAppUID = mangle( pid );  			
  			
  			String eventTopic = ScheduledApplication.TIMER_TOPIC;
  			String eventFilter = getFilterFromNow( 1 );
  			Map args = createArgs();
  			
  			appDesc.schedule(null, args, eventTopic, eventFilter, false);
  			
  			if( !testCase_uninstallMidletBundle() )
  				return false;
  			
  			DmtSession session = dmtFactory.getSession("./OSGi/Application" );

  			String[] appPids = session.getChildNodeNames( "./OSGi/Application" );  			
  			if( appPids == null || appPids.length != 1 )
  				throw new Exception( "Orphaned schedule node mnot found!" );
  			if( !appPids[ 0 ].equals( mangledAppUID ) )
  				throw new Exception( "Invalid PID for the schedule!" );
  			
  			
  			String[] properties = session.getChildNodeNames( "./OSGi/Application/" + mangledAppUID );
  			
  			String names[]  = new String [] { 	"Name", "IconURI", "Version", "Vendor", 
  					                          	"Locked", "Location", "ContainerID",
  					                          	"ApplicationID", "Valid", "Instances", 
												"Operations", "Schedules" };
  			Object values[] = new Object [] {   "", "", "", "", new Boolean( false ), "", "", pid, 
  				                                new Boolean( false ), null, null, null };
  			
  			
  			boolean found[] = new boolean[ names.length ];				
  			for( int i = 0; i != names.length; i++ )
  				found [ i ] = false;
  			
  			for( int i = 0; i != properties.length; i++ ) {
  				int j = 0;
  				for(; j != names.length; j++ )
  					if( properties[ i ].equals( names [ j ] ) ) {
  						if( found [ j ] )
  							throw new Exception( "Parameter duplication:" + properties[ i ] + "!" );
  						found[ j ] = true;
  						if( values[ j ] == null )
  							break;
  						
  						DmtData value = session.getNodeValue( "./OSGi/Application/" + mangledAppUID + "/" + names[ j ] );
  						
  						switch( value.getFormat() )
  						{
  						case DmtData.FORMAT_BOOLEAN:
  							if( ((Boolean)values[j]).booleanValue() != value.getBoolean() )
  								throw new Exception( "Invalid value of " + names[ j ] + " (" + values [ j ] + "!=" + value.getBoolean() + ") !" );
  							break;
  						case DmtData.FORMAT_STRING:
  							if( !((String)values[j]).equals( value.getString() ) )
  								throw new Exception( "Invalid value of " + names[ j ] + " (" + values [ j ] + "!=" + value.getString() + ") !" );														
  							break;
  						case DmtData.FORMAT_INTEGER:
  							if( ((Integer)values[j]).intValue() != value.getInt() )
  								throw new Exception( "Invalid value of " + names[ j ] + " (" + values [ j ] + "!=" + value.getInt() +  ") !" );
  							break;
  						default:
  							throw new Exception( "Illegal type for " + names [ j ] + "parameter !" );
  						}
  						
  						break;
  					}
  				if( j == names.length )
  					throw new Exception( "Invalid parameter:" +  properties[ i ] + "!" );
  			}
  			
  			String[] scheduleNames = session.getChildNodeNames( "./OSGi/Application/" + mangledAppUID + "/Schedules" );  			
  			if( scheduleNames == null || scheduleNames.length != 1 )
  				throw new Exception( "Schedule was not registered in the ApplicationPlugin!" );
  			String schedID = scheduleNames[ 0 ];

  			if( !session.getNodeValue( "./OSGi/Application/" + mangledAppUID + "/Schedules/" + schedID + "/Enabled" ).getBoolean() )
  				throw new Exception("Illegal value for Enabled node!");
  			
  			if( !session.getNodeValue( "./OSGi/Application/" + mangledAppUID + "/Schedules/" + schedID + "/TopicFilter" )
  					.getString().equals( eventTopic ))
				throw new Exception("Illegal value for EventTopic node!");

  			if( !session.getNodeValue( "./OSGi/Application/" + mangledAppUID + "/Schedules/" + schedID + "/EventFilter" )
					.getString().equals( eventFilter ))
				throw new Exception("Illegal value for EventFilter node!");
  			
  			if( session.getNodeValue( "./OSGi/Application/" + mangledAppUID + "/Schedules/" + schedID + "/Recurring" ).getBoolean() )
   				throw new Exception("Illegal value for Recurring node!");
  			
  			String[] argumentPairs = session.getChildNodeNames( "./OSGi/Application/" + mangledAppUID + "/Schedules/" + schedID + "/Arguments" );
  			if( argumentPairs == null || argumentPairs.length != args.size() )
  				throw new Exception( "Invalid argument number was received!" );
  			
  			for( int q=0; q != argumentPairs.length; q++ ) {
  				String argID = argumentPairs[ q ];
  				String [] pairElems =  session.getChildNodeNames( "./OSGi/Application/" + mangledAppUID + "/Schedules/" + schedID + "/Arguments/" + argID );
  				if( pairElems == null || ( pairElems.length != 2 && pairElems.length != 1) )
  					throw new Exception( "Invalid element number!" );

  				List pairElemList = Arrays.asList( pairElems );
    			
    			if( pairElemList.indexOf( "Name" ) == -1 )
    				throw new Exception( "Name is missing from the list" );
    			
    			if( pairElems.length == 2 && pairElemList.indexOf( "Value" ) == -1 )
    				throw new Exception( "Value is missing from the list" );
  				
    			String key = session.getNodeValue( "./OSGi/Application/" + mangledAppUID + "/Schedules/" + schedID + "/Arguments/" + argID + "/Name" ).getString();
    			Object content = args.get( key );
    			if( key.equals( "Unmappable" ) ) {
    				if( pairElems.length != 1 )
    					throw new Exception("AppPlugin mapped an unmappable element!");
    				continue;    			
    			}
    			if( pairElems.length != 2 )
    				throw new Exception("Value is missing from a mappable element!");
    			
    			DmtData argValue = session.getNodeValue( "./OSGi/Application/" + mangledAppUID + "/Schedules/" + schedID + "/Arguments/" + argID + "/Value" );
    			
    			if( content == null ) {
    				if( argValue.getFormat() != DmtData.FORMAT_NULL )
    					throw new Exception("Invalid null value");
    				continue;
    			}
    			else if( content instanceof Boolean ) {
    				if( argValue.getBoolean() != ((Boolean)content).booleanValue() )
    					throw new Exception("Invalid Boolean value");
    				continue;
    			}
    			else if( content instanceof Integer ) {
    				if( argValue.getInt() != ((Integer)content).intValue() )
    					throw new Exception("Invalid Integer value");
    				continue;
    			}
    			else if( content instanceof Float ) {
    				if( argValue.getFloat() != ((Float)content).floatValue() )
    					throw new Exception("Invalid Float value");
    				continue;
    			}
    			else if( content instanceof byte [] ) {
    				byte recvBin[] = argValue.getBinary();
    				byte expBin[] = (byte [])content;
    				
    				if( recvBin.length != expBin.length )
    					throw new Exception( "Invalid binary value" );
    				
    				for( int p=0; p != expBin.length; p++ )
    					if( recvBin[ p ] != expBin[ p ] )
    						throw new Exception( "Invalid binary value" );    				
    				continue;
    			}
    			else if( content instanceof String ) {
    				if( !argValue.getString().equals( content ) )
  						throw new Exception( "Invalid string value" );    				    					
    				continue;
    			}
    			throw new Exception("Invalid mapping received!");
  			}
  			
  			session.deleteNode( "./OSGi/Application/" + mangledAppUID + "/Schedules/"  + schedID );

  			appPids = session.getChildNodeNames( "./OSGi/Application" );  			
  			if( appPids != null && appPids.length != 0 )
  				throw new Exception( "Orphaned schedule was not removed after delete!" );

  			ServiceReference refs[] = bc.getServiceReferences( ScheduledApplication.class.getName(), null );
  			if( refs != null && refs.length != 0 )
  				throw new Exception("Orphaned schedule was not unregistered after delete!");

  			session.close();  			
  			return true;
  		}
  		catch (Exception e) {
  			e.printStackTrace();
  			return false;
  		}							  		
	}
}
