// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   TestMidletContainerBundleActivator.java

package com.nokia.test.midletcontainer;

import java.io.*;
import java.net.URL;
import java.util.*;

import org.osgi.framework.*;
import org.osgi.service.application.ApplicationDescriptor;
import org.osgi.service.application.ApplicationHandle;
import org.osgi.service.application.ScheduledApplication;
import org.osgi.service.application.midlet.MidletHandle;
import org.osgi.service.dmt.DmtAdmin;
import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.DmtSession;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventHandler;
import org.osgi.service.log.*;

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

    String getPID(ApplicationDescriptor appDesc)
    {
        return (String)appDesc.getProperties("").get("service.pid");
    }

    boolean isLocked(ApplicationDescriptor appDesc)
    {
        return Boolean.valueOf((String)appDesc.getProperties("").get("application.locked")).booleanValue();
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
  				"org.osgi.service.application.ApplicationHandle",
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
    		if (!testCase_appPluginDeleteNode()) 															
    			  System.out.println("AppPlugin: checking the node removal             FAILED"); 	
    		else 																																				
    			  System.out.println("AppPlugin: checking the node removal             PASSED"); 	
    		if (!testCase_appPluginLock()) 															
    			  System.out.println("AppPlugin: checking the lock changing            FAILED"); 	
    		else 																																				
    			  System.out.println("AppPlugin: checking the lock changing            PASSED"); 	
        if(!testCase_oatRegisterService())
            System.out.println("Checking OAT service registration                FAILED");
        else
            System.out.println("Checking OAT service registration                PASSED");
        if(!testCase_oatLocateService())
            System.out.println("Checking OAT locate service                      FAILED");
        else
            System.out.println("Checking OAT locate service                      PASSED");
        if(!testCase_oatLocateServices())
            System.out.println("Checking OAT locate services                     FAILED");
        else
            System.out.println("Checking OAT locate services                     PASSED");
        if(!testCase_oatBundleListener())
            System.out.println("Checking OAT bundle listener                     FAILED");
        else
            System.out.println("Checking OAT bundle listener                     PASSED");
        if(!testCase_oatServiceListener())
            System.out.println("Checking OAT service listener                    FAILED");
        else
            System.out.println("Checking OAT service listener                    PASSED");
        if(!testCase_oatFrameworkListener())
            System.out.println("Checking OAT framework listener                  FAILED");
        else
            System.out.println("Checking OAT framework listener                  PASSED");
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
            midletBundle.start();
            installedBundleID = midletBundle.getBundleId();
            ServiceReference appList[] = (ServiceReference[])null;
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
                appList = bc.getServiceReferences("org.osgi.service.application.ApplicationDescriptor", "(application.bundle.id=" + installedBundleID + ")");
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
                if(bundles[i].getState() == 32)
                {
                    Dictionary dict = bundles[i].getHeaders();
                    Object name = dict.get("MIDlet-Container-Name");
                    Object version = dict.get("MIDlet-Container-Version");
                    if(name != null && version != null)
                    {
                        System.out.println((String)name + " (" + (String)version + ") found!");
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
            ServiceReference appList[] = bc.getServiceReferences("org.osgi.service.application.ApplicationDescriptor", "(service.pid=" + appUID + ")");
            if(appList == null || appList.length == 0)
                throw new Exception("No ApplicationUID(" + appUID + ") registered!");
            long bundleID = -1L;
            boolean registered = false;
            for(int i = 0; i != appList.length; i++)
            {
                if((ApplicationDescriptor)bc.getService(appList[i]) == appDesc)
                {
                    registered = true;
                    if(!appList[i].getProperty("service.pid").equals(getPID(appDesc)))
                        throw new Exception("Illegal service.pid in the AppDesc (" + (String)appList[i].getProperty("service.pid") + " != " + getPID(appDesc) + ")");
                    if(!appList[i].getProperty("application.name").equals(appDesc.getProperties(Locale.getDefault().getLanguage()).get("application.name")))
                        throw new Exception("Illegal application name in the AppDesc (" + (String)appList[i].getProperty("application.name") + " != " + appDesc.getProperties("").get("application.name") + ")");
                    bundleID = appList[i].getBundle().getBundleId();
                }
                bc.ungetService(appList[i]);
            }

            Map engProps = appDesc.getProperties("en");
            if(!engProps.get("application.name").equals("TestMidlet"))
                throw new Exception("The application name is " + (String)engProps.get("application.name") + " instead of 'TestMidlet'!");
            if(!((String)engProps.get("application.icon")).endsWith("/TestIcon.gif"))
                throw new Exception("The icon path " + (String)engProps.get("application.icon") + " doesn't ends with '/TestIcon.gif'!");
            if(!engProps.get("application.version").equals("1.0.0"))
                throw new Exception("The application version is " + (String)engProps.get("application.version") + " instead of '1.0.0'!");
            if(!engProps.get("application.type").equals("MIDlet"))
                throw new Exception("The application type is " + (String)engProps.get("application.type") + " instead of 'MIDlet'!");
            if(engProps.get("application.bundle.id") == null)
                throw new Exception("No application bundle id found!");
            if(!engProps.get("application.visible").equals("true"))
                throw new Exception("Visible flag is " + (String)engProps.get("application.visible") + " instead of 'true'!");
            if(!engProps.get("application.vendor").equals("Nokia"))
                throw new Exception("Vendor flag is " + (String)engProps.get("application.vendor") + " instead of 'Nokia'!");
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
            Hashtable args = new Hashtable();
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
        String launchable = (String)appDesc.getProperties("en").get("application.launchable");
        return launchable != null && launchable.equalsIgnoreCase("true");
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
            boolean launchable = isLaunchable(appDesc);
            appHandle = appDesc.launch(args);
            if(!checkResultFile("START"))
                throw new Exception("Result of the launch is not START!");
            if(!launchable)
                throw new Exception("Application started, but originally was not launchable!");
            ServiceReference appList[] = bc.getServiceReferences("org.osgi.service.application.ApplicationHandle", "(application.descriptor=" + getPID(appDesc) + ")");
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
      ServiceReference appList[] = bc.getServiceReferences("org.osgi.service.application.ApplicationHandle", "(service.pid=" + pid + ")");
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
      ServiceReference references[] = bc.getServiceReferences("org.osgi.service.application.ApplicationDescriptor", "(application.bundle.id=" + installedBundleID + ")");
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
            ServiceReference appList[] = bc.getServiceReferences("org.osgi.service.application.ApplicationDescriptor", "(service.pid=" + installedAppUID + ")");
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

  	boolean testCase_oatBundleListener() {
      try {
      	if( !testCase_launchApplication() )
      		return false;
  	  	if( !checkResponseForEvent( "com/nokia/megtest/AddBundleListener", 
                                    "BUNDLE LISTENER ADDED") )
  		  	return false;
  	  	
        URL dummyBundleURL = bc.getBundle().getResource( "dummybundle.jar" );
        if(dummyBundleURL == null)
            throw new Exception("Can't find dummybundle.jar in the resources!");  	  	
        Bundle bundle = bc.installBundle( dummyBundleURL.toString(), dummyBundleURL.openStream() );
  	  	
  			if (!checkResultFile( "BUNDLE CHANGE RECEIVED" ))
  				throw new Exception("Event handler service was not registered!");
  	  	
  	  	if( !checkResponseForEvent( "com/nokia/megtest/RemoveBundleListener", 
                                    "BUNDLE LISTENER REMOVED") )
          return false;
  	  	
  	  	bundle.start();
  	  	
  			String content = getResultFileContent();
  			if ( content != null && content.equals( "BUNDLE CHANGE RECEIVED" ) )
  				throw new Exception("Bundle listener was not removed!");
  	  	
  	  	if( !checkResponseForEvent( "com/nokia/megtest/AddBundleListener", 
                                    "BUNDLE LISTENER ADDED") )
          return false;
  	  	
  		  if( !testCase_stopApplication() )
	  	  	return false;
  		  
  		  bundle.uninstall();
  	  	
  			content = getResultFileContent();
  			if ( content != null && content.equals( "BUNDLE CHANGE RECEIVED" ) )
  				throw new Exception("Bundle listener was not removed after stop!");
  		  
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

  	boolean testCase_oatFrameworkListener() {
      try {     	
      	if( !testCase_launchApplication() )
      		return false;
  	  	if( !checkResponseForEvent( "com/nokia/megtest/AddFrameworkListener", 
                                    "FRAMEWORK LISTENER ADDED") )
          return false;
  	  	if( !checkResponseForEvent( "com/nokia/megtest/RemoveFrameworkListener", 
                                    "FRAMEWORK LISTENER REMOVED") )
          return false;
  	  	if( !checkResponseForEvent( "com/nokia/megtest/AddFrameworkListener", 
                                    "FRAMEWORK LISTENER ADDED") )  	
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
  	
  	public boolean testCase_lockApplication() {
  		try {
  			ApplicationDescriptor appDesc = appDescs[0];
  			if (isLocked( appDesc ))
  				throw new Exception("Application is locked and cannot launch!");
  			appDesc.lock();
  			if (!isLocked( appDesc ))
  				throw new Exception("Lock doesn't work!");
  			if (!appDesc.getProperties("en").get("application.locked").equals(
  					"true"))
  				throw new Exception("Lock property is incorrect!");
  			boolean launchable = isLaunchable(appDesc);
  			boolean started = false;
  			try {
  				appHandle = appDesc.launch(new Hashtable());
  				started = true;
  			}catch (Exception e) {}
  			
  			if (started)
  				throw new Exception("Application was launched inspite of lock!");
  			appDesc.unlock();
  			if (!appDesc.getProperties("en").get("application.locked").equals(
  					"false"))
  				throw new Exception("Lock property is incorrect!");
  			if (isLocked( appDesc ))
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
  			if (!appDesc.getProperties("en").get("application.locked").equals(
  					"true"))
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
  			if (!appDesc.getProperties("en").get("application.locked").equals(
  					"false"))
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
  			appDesc.schedule(args, "org/osgi/timer", getFilterFromNow( 4 ), false);
  			appDesc.schedule(args, "org/osgi/timer", getFilterFromNow( 2 ), false);
  			Thread.sleep(3000);
  			appHandle = lookupAppHandle(appDesc);
  			if (appHandle == null
  					|| !appHandle.getState().equals( ApplicationHandle.RUNNING ) )
  				throw new Exception("Application wasn't scheduled!");
  			if (!checkResultFile("START"))
  				throw new Exception("Result of the schedule is not START!");
  			if (!testCase_stopApplication())
  				return false;
  			Thread.sleep(2000);
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
  			appDesc.schedule(args, "org/osgi/timer", getFilterFromNow( 3 ), false);
  			if (!restart_scheduler())
  				return false;
  			Thread.sleep(4000);
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
  					schedule(args, "org/osgi/timer", getFilterFromNow( 2 ), false);
  			
  			if( schedApp.getApplicationDescriptor() != appDesc )
  				throw new Exception( "Invalid application descriptor was received!" );
  			
  			schedApp.remove();
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

  			ScheduledApplication schedApp = appDesc.schedule(args, "com/nokia/test/ScheduleEvent", null, true);
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
  			DmtSession session = dmtFactory.getSession("./OSGi/apps");
  			
  			String[] nodeNames = session.getChildNodeNames( "./OSGi/apps" );
  		
  			if( nodeNames.length != 1)
  				throw new Exception( "Too many nodenames are present! Only one meglet is installed!" );
  			
  			if( !appUID.equals( nodeNames[ 0 ] ) )
  				throw new Exception( "Illegal node name found! (" + nodeNames[ 0 ] + " instead of " + appUID );
  	
  			String[] properties = session.getChildNodeNames( "./OSGi/apps/" + appUID );
  			
  			String names[]  = new String [] { "localizedname", "version", "vendor", "locked", 
  					                              "bundle_id", "required_services", "launch" };
  			Object values[] = new Object [ names.length ];
  			
  			Map props = appDesc.getProperties( Locale.getDefault().getLanguage() );
  			
  			values[ 0 ] = (String)( props.get( ApplicationDescriptor.APPLICATION_NAME ) );
  			values[ 1 ] = (String)( props.get( ApplicationDescriptor.APPLICATION_VERSION ) );
  			values[ 2 ] = (String)( props.get( ApplicationDescriptor.APPLICATION_VENDOR ) );
  			values[ 3 ] = Boolean.valueOf( (String)props.get( ApplicationDescriptor.APPLICATION_LOCKED ) );
  			values[ 4 ] = (String)( props.get( "application.bundle.id" ) );
  			
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
  						
  						DmtData value = session.getNodeValue( "./OSGi/apps/" + appUID + "/" + names[ i ] );
  						
  						switch( value.getFormat() )
  						{
  						case DmtData.FORMAT_BOOLEAN:
  							if( ((Boolean)values[j]).booleanValue() != value.getBoolean() )
  								throw new Exception( "Invalid value of " + names[ j ] + " (" + values [ j ] + ") !" );
  							break;
  						case DmtData.FORMAT_STRING:
  							if( !((String)values[j]).equals( value.getString() ) )
  								throw new Exception( "Invalid value of " + names[ j ] + " (" + values [ j ] + ") !" );														
  							break;
  						case DmtData.FORMAT_INTEGER:
  							if( ((Integer)values[j]).intValue() != value.getInt() )
  								throw new Exception( "Invalid value of " + names[ j ] + " (" + values [ j ] + ") !" );
  							break;
  						default:
  							throw new Exception( "Illegal type for " + names [ j ] + "parameter !" );
  						}
  						
  						break;
  					}
  					if( j == properties.length )
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
  		
  		try {
  			if( !testCase_launchApplication() )
  				return false;
  			
  			ServiceReference[] references = bc.getServiceReferences(
  					"org.osgi.service.application.ApplicationHandle",
  					"(" + ApplicationHandle.APPLICATION_DESCRIPTOR + "=" + getPID( appDesc ) + ")");
  			
  			if( references == null || references.length == 0 )
  				throw new Exception( "Service reference not found!" );
  			
  			DmtSession session = dmtFactory.getSession("./OSGi/app_instances");

  			String[] nodeNames = session.getChildNodeNames( "./OSGi/app_instances" );
  			
  			if( nodeNames == null || nodeNames.length != 1 )
  				throw new Exception( "Couldn't find the application instance node!" );
  			
  			ApplicationHandle appHandle = lookupAppHandle( appDesc );
  			
  			String instID = appHandle.getInstanceID();
  			
  			if( !nodeNames[ 0 ].equals( appHandle.getInstanceID() ) )
  				throw new Exception( "Illegal node name (" + nodeNames[ 0 ] + 
  						                 " instead of " + instID +")" );
  			
  			String[] childNodes = session.getChildNodeNames( "./OSGi/app_instances/" + appHandle.getInstanceID() );
  			
  			if( childNodes == null || childNodes.length != 2 )
  				throw new Exception( "Invalid child nodes of the application instance!" );
  			
  			List childList = Arrays.asList( childNodes );
  			
  			if( childList.indexOf( "state" ) == -1 || childList.indexOf( "type" ) == -1 )
  				throw new Exception( "Invalid child nodes of the application instance!" );
  			
  			DmtData typeValue = session.getNodeValue( "./OSGi/app_instances/" + instID + "/type" );
  			if( !getPID( appDesc ).equals( typeValue.getString() ) )
  				throw new Exception( "Bad type value (" + typeValue.getString() +"/" + getPID( appDesc ) + ")!" );

  			DmtData stateValue = session.getNodeValue( "./OSGi/app_instances/" + instID + "/state" );
  			if( !stateValue.getString().equals( ApplicationHandle.RUNNING ) )
  				throw new Exception( "Bad state value (" + stateValue.getInt() + " " + 
  						                  ApplicationHandle.RUNNING + ")!" );

  			if( ! testCase_pauseApplication() )
  				return false;

  			stateValue = session.getNodeValue( "./OSGi/app_instances/" + instID + "/state" );
  			if( !stateValue.getString().equals( MidletHandle.PAUSED ) )
  				throw new Exception( "Bad state value (" + stateValue.getInt() + " " + 
  						MidletHandle.PAUSED + ")!" );
  			
  			if( ! testCase_resumeApplication() )
  				return false;

  			stateValue = session.getNodeValue( "./OSGi/app_instances/" + instID + "/state" );
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
  		
  		try {
  			DmtSession session = dmtFactory.getSession("./OSGi/apps");
  			
  			String[] nodeNames = session.getChildNodeNames( "./OSGi/apps/" + appUID + "/launch" );
  			if( nodeNames != null && nodeNames.length != 0 )
  				throw new Exception( "Exec-id found without setting it manually!" );
  		
  			session.createInteriorNode( "./OSGi/apps/" + appUID + "/launch/exec_id" );
  			
  			nodeNames = session.getChildNodeNames( "./OSGi/apps/" + appUID + "/launch" );
  			if( nodeNames == null || nodeNames.length != 1 )
  				throw new Exception( "Interior node wasn't created properly!" );
  			if( !nodeNames[ 0 ].equals("exec_id") )
  				throw new Exception( "The name of the interior node is " + nodeNames [ 0 ] + 
  						                  "instead if exec_id !" );
  			
  			String[] childNodes = session.getChildNodeNames( "./OSGi/apps/" + appUID + "/launch/exec_id" );
  			if( childNodes != null && childNodes.length != 0 )
  				throw new Exception( "Extra parameters added to the exec_id interior node!" );
  			
  			Map args = createArgs();
  			
  			Iterator it = args.keySet().iterator();
  			while( it.hasNext() )
  			{
  				String prop = (String)it.next();
  				String value = (String)args.get( prop );
  				
  				session.createLeafNode( "./OSGi/apps/" + appUID + "/launch/exec_id/" + prop, 
  						                    new DmtData( value ) );
  				
  				childNodes = session.getChildNodeNames( "./OSGi/apps/" + appUID + "/launch/exec_id" );
  				if( childNodes == null || childNodes.length == 0 )
  					throw new Exception( "Property wasn't added properly!" );
  				if( Arrays.asList( childNodes ).indexOf( prop ) == -1 )
  					throw new Exception( "Property wasn't added properly!" );
  				
  				DmtData content = session.getNodeValue( "./OSGi/apps/" + appUID + "/launch/exec_id/" + prop );
  				if( !content.getString().equals( value ) )
  					throw new Exception( "Illegal value was set to the property!" );
  			}
  			
  			session.execute( "./OSGi/apps/" + appUID + "/launch/exec_id", null );

  			if (!checkResultFile("START"))
  				throw new Exception("Result of the launch is not START!");
  			ServiceReference[] appList = bc.getServiceReferences(
  					"org.osgi.service.application.ApplicationHandle",
  					"(" + ApplicationHandle.APPLICATION_DESCRIPTOR + "=" + getPID( appDesc ) + ")");
  			if (appList == null || appList.length == 0)
  				throw new Exception("No registered application handle found!");

  			appHandle = (ApplicationHandle) bc.getService(appList[0]);
  			
  			if ( !appHandle.getState().equals( ApplicationHandle.RUNNING ) )
  				throw new Exception("Application didn't started!");

  			bc.ungetService( appList[0] );

  			nodeNames = session.getChildNodeNames( "./OSGi/apps/" + appUID + "/launch" );
  			if( nodeNames != null && nodeNames.length != 0 )
  				throw new Exception( "Exec-id didn't removed after launch!" );
  			
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
  		
  		try {
  			
  			if( !testCase_launchApplication() )
  				return false;
  			
  			DmtSession session = dmtFactory.getSession("./OSGi/app_instances");

  			String[] nodeNames = session.getChildNodeNames( "./OSGi/app_instances" );
  			
  			if( nodeNames == null || nodeNames.length != 1 )
  				throw new Exception( "Couldn't find the application instance node!" );
  			
  			String instanceName = nodeNames[ 0 ];			
  			session.execute( "./OSGi/app_instances/" + instanceName, "STOP" );			

  			nodeNames = session.getChildNodeNames( "./OSGi/app_instances" );
  			if( nodeNames != null && nodeNames.length != 0 )
  				throw new Exception( "Application didn't stop!" );
  			
  			if (!checkResultFile("STOP"))
  				throw new Exception("Result of the stop is not STOP!");

  			ServiceReference[] appList = bc.getServiceReferences(
  					"org.osgi.service.application.ApplicationHandle",
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
  	
  	boolean testCase_appPluginDeleteNode() {
  		ApplicationDescriptor appDesc = appDescs[0];
  		String appUID = getPID( appDesc );
  		
  		try {
  			DmtSession session = dmtFactory.getSession("./OSGi/apps");
  			
  			String[] nodeNames = session.getChildNodeNames( "./OSGi/apps/" + appUID + "/launch" );
  			if( nodeNames != null && nodeNames.length != 0 )
  				throw new Exception( "Exec-id found when no one is expected!" );
  		
  			session.createInteriorNode( "./OSGi/apps/" + appUID + "/launch/exec_id" );
  			
  			nodeNames = session.getChildNodeNames( "./OSGi/apps/" + appUID + "/launch" );
  			if( nodeNames == null || nodeNames.length != 1 )
  				throw new Exception( "Interior node wasn't created properly!" );
  			if( !nodeNames[ 0 ].equals("exec_id") )
  				throw new Exception( "The name of the interior node is " + nodeNames [ 0 ] + 
  						                  "instead if exec_id !" );
  			
  			session.createLeafNode( "./OSGi/apps/" + appUID + "/launch/exec_id/myprop", 
            new DmtData( "myvalue" ) );
  			
  			String[] childNodes = session.getChildNodeNames( "./OSGi/apps/" + appUID + "/launch/exec_id" );
  			if( childNodes == null || childNodes.length != 1 )
  				throw new Exception( "Property wasn't added properly!" );
  			
  			DmtData value = session.getNodeValue( "./OSGi/apps/" + appUID + "/launch/exec_id/myprop" );
  			if( !value.getString().equals( "myvalue" ) )
  				throw new Exception( "Invalid node value was received!" );
  			
  			session.setNodeValue( "./OSGi/apps/" + appUID + "/launch/exec_id/myprop", new DmtData( "newvalue" ) );
  			value = session.getNodeValue( "./OSGi/apps/" + appUID + "/launch/exec_id/myprop" );
  			if( !value.getString().equals( "newvalue" ) )
  				throw new Exception( "Node value was not changed!" );
  			
  			if( !childNodes[ 0 ].equals( "myprop" ) )
  				throw new Exception( "Property wasn't added properly!" );
  			
  			session.deleteNode( "./OSGi/apps/" + appUID + "/launch/exec_id/myprop" );
  			
  			childNodes = session.getChildNodeNames( "./OSGi/apps/" + appUID + "/launch/exec_id" );
  			if( childNodes != null && childNodes.length != 0 )
  				throw new Exception( "Property wasn't deleted properly!" );			
  			
  			session.deleteNode( "./OSGi/apps/" + appUID + "/launch/exec_id" );
  			
  			childNodes = session.getChildNodeNames( "./OSGi/apps/" + appUID + "/launch" );
  			if( childNodes != null && childNodes.length != 0 )
  				throw new Exception( "Node wasn't deleted properly!" );
  			
  			session.close();
  			return true;
  		}
  		catch (Exception e) {
  			e.printStackTrace();
  			return false;
  		}					
  	}	
  	
  	boolean testCase_appPluginLock() {
  		ApplicationDescriptor appDesc = appDescs[0];
  		String appUID = getPID( appDesc );
  		
  		try {
  			if( isLocked( appDesc ) )
  				throw new Exception( "ApplicationDescriptor is unexpectedly locked!" );
  			
  			DmtSession session = dmtFactory.getSession("./OSGi/apps");
  			DmtData value = session.getNodeValue( "./OSGi/apps/" + appUID + "/locked" );			
  			if( value.getBoolean() )
  				throw new Exception( "Application is unlocked, but AppPlugin reports locked!" );
  			
  			session.setNodeValue( "./OSGi/apps/" + appUID + "/locked", new DmtData( true ) );
  			
  			if( !isLocked( appDesc ) )
  				throw new Exception( "AppPlugin failed to set the application locked!" );
  			value = session.getNodeValue( "./OSGi/apps/" + appUID + "/locked" );			
  			if( !value.getBoolean() )
  				throw new Exception( "Application is locked, but AppPlugin reports unlocked!" );
  			
  			session.setNodeValue( "./OSGi/apps/" + appUID + "/locked", new DmtData( false ) );
  			if( isLocked( appDesc ) )
  				throw new Exception( "AppPlugin failed to set the application unlocked!" );
  			value = session.getNodeValue( "./OSGi/apps/" + appUID + "/locked" );			
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
}
