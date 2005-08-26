// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   TestMidletContainerBundleActivator.java

package com.nokia.test.midletcontainer;

import java.io.*;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.*;
import org.osgi.framework.*;
import org.osgi.impl.service.midletcontainer.MidletDescriptorImpl;
import org.osgi.service.application.ApplicationDescriptor;
import org.osgi.service.application.ApplicationHandle;
import org.osgi.service.application.midlet.MidletDescriptor;
import org.osgi.service.application.midlet.MidletHandle;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventHandler;

public class TestMidletContainerBundleActivator
    implements BundleActivator, BundleListener, EventHandler, Runnable
{
    private BundleContext bc;
    private Thread testerThread;
    private Bundle midletBundle;
    private Bundle midletContainerBundle;
    private Bundle logServiceBundle;
    private ApplicationDescriptor appDescs[];
    private ApplicationHandle appHandle;
    private String installedAppUID;
    private long installedBundleID;
    private ServiceRegistration serviceReg;
    private LinkedList receivedEvents;
    private static final int APPLICATION_START = 1;
    private static final int APPLICATION_PAUSE = 2;
    private static final int APPLICATION_RESUME = 3;
    private static final int APPLICATION_STOPPED = 5;
    private static final int APPLICATION_STOPPED_AFTER_PAUSE = 6;

    public TestMidletContainerBundleActivator()
    {
        receivedEvents = new LinkedList();
    }

    public void start(BundleContext bc)
        throws Exception
    {
        this.bc = bc;
        serviceReg = bc.registerService("org.osgi.service.event.EventHandler", this, null);
        bc.addBundleListener(this);
    }

    public void stop(BundleContext bc)
        throws Exception
    {
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

    boolean waitStateChangeEvent(int state, String pid)
    {
        return true;
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

    public void run()
    {
        System.out.println("Midlet container tester thread started!\n");
        if(!testCase_lookUpMidletContainer())
        {
            System.out.println("Looking up the Midlet container                  FAILED");
            return;
        }
        System.out.println("Looking up the Midlet container                  PASSED");
        if(!testCase_lookUpLogService())
        {
            System.out.println("Looking up the log service                       FAILED");
            return;
        }
        System.out.println("Looking up the log service                       PASSED");
        if(!testCase_installMidletBundle())
            System.out.println("Midlet bundle install onto Midlet container      FAILED");
        else
            System.out.println("Midlet bundle install onto Midlet container      PASSED");
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
        if(!testCase_oatRegisterService())
          System.out.println("Checking OAT service registration                FAILED");
      else
          System.out.println("Checking OAT service registration                PASSED");
        if(!testCase_launchAfterRestart())
            System.out.println("Launching Midlet app after container restart     FAILED");
        else
            System.out.println("Launching Midlet app after container restart     PASSED");
        if(!testCase_stopAtServiceUnregistering())
            System.out.println("Checking stop after service unregistering        FAILED");
        else
            System.out.println("Checking stop after service unregistering        PASSED");
        if(!testCase_singletonCheck())
            System.out.println("Checking singleton application                   FAILED");
        else
            System.out.println("Checking singleton application                   PASSED");
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
            if(!engProps.get("application.singleton").equals("true"))
                throw new Exception("Singleton flag is " + (String)engProps.get("application.singleton") + " instead of 'true'!");
            if(!engProps.get("application.autostart").equals("false"))
                throw new Exception("Autostart flag is " + (String)engProps.get("application.autostart") + " instead of 'false'!");
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

    private boolean checkResultFile(String result)
    {
        boolean matches = false;
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
            matches = resultStr.equals(result);
            resultFile.delete();
            if(!matches)
                System.out.println("Received: " + resultStr + "  instead of " + result);
            return matches;
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return false;
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
            if(!waitStateChangeEvent(1, getPID(appDesc)))
                throw new Exception("Didn't receive the start event!");
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
            if(!waitStateChangeEvent(2, getPID(appDescs[0])))
                throw new Exception("Didn't receive the pause event!");
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
            if(!waitStateChangeEvent(3, getPID(appDescs[0])))
                throw new Exception("Didn't resumed the application correctly!");
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

    boolean testCase_stopApplication()
    {
        try
        {
            String pid = getPID(getAppDesc(appHandle));
            appHandle.destroy();
            if(!checkResultFile("STOP"))
                throw new Exception("Result of the stop is not STOP!");
            if(!waitStateChangeEvent(5, pid))
                throw new Exception("Didn't receive the stopped event!");
            ServiceReference appList[] = bc.getServiceReferences("org.osgi.service.application.ApplicationHandle", "(service.pid=" + pid + ")");
            if(appList != null && appList.length != 0)
            {
                for(int i = 0; i != appList.length; i++)
                {
                    ApplicationHandle handle = (ApplicationHandle)bc.getService(appList[i]);
                    bc.ungetService(appList[i]);
                    if(handle == appHandle)
                        throw new Exception("Application handle doesn't removed after stop!");
                }

            }
            try
            {
                appHandle.getState();
            }
            catch(Exception e)
            {
                return true;
            }
            throw new Exception("The status didn't change to NONEXISTENT!");
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return false;
    }

    public boolean restart_MidletContainer(boolean hasAppInstalled)
    {
        try
        {
            midletContainerBundle.stop();
            while(midletContainerBundle.getState() != 4) 
                try
                {
                    Thread.sleep(100L);
                }
                catch(InterruptedException interruptedexception) { }
            ServiceReference appList[] = bc.getServiceReferences("org.osgi.service.application.ApplicationDescriptor", "(service.pid=" + installedAppUID + ")");
            if(appList != null && appList.length != 0)
                throw new Exception("Application descriptor doesn't removed after container stop!");
            midletContainerBundle.start();
            while(midletContainerBundle.getState() != 32) 
                try
                {
                    Thread.sleep(100L);
                }
                catch(InterruptedException interruptedexception1) { }
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
            if(!checkResultFile("STOP"))
                throw new Exception("Result of the stop is not STOP!");
            if(!waitStateChangeEvent(5, pid))
                throw new Exception("Didn't receive the stopped event!");
            ServiceReference appList[] = bc.getServiceReferences("org.osgi.service.application.ApplicationHandle", "(service.pid=" + pid + ")");
            if(appList != null && appList.length != 0)
            {
                for(int i = 0; i != appList.length; i++)
                {
                    ApplicationHandle handle = (ApplicationHandle)bc.getService(appList[i]);
                    bc.ungetService(appList[i]);
                    if(handle == appHandle)
                        throw new Exception("Application handle doesn't removed after stop!");
                }

            }
            try
            {
                appHandle.getState();
            }
            catch(Exception e)
            {
                return true;
            }
            throw new Exception("The status didn't change to NONEXISTENT!");
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return false;
    }

    boolean testCase_singletonCheck()
    {
        try
        {
            if(!testCase_launchApplication())
                return false;
            ApplicationHandle tmpAppHandle = appHandle;
            boolean launchable = isLaunchable(appDescs[0]);
            if(testCase_launchApplicationError(true))
                return false;
            if(launchable)
                throw new Exception("Application was not launchable, but started!");
            appHandle = tmpAppHandle;
            return testCase_stopApplication();
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
            if(!hack_changeSingletonity(false))
                return false;
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
            if(!checkResultFile("STOP"))
                throw new Exception("Result of the stop is not STOP!");
            if(!waitStateChangeEvent(5, pid))
                throw new Exception("Didn't receive the stopped event!");
            ServiceReference appList[] = bc.getServiceReferences("org.osgi.service.application.ApplicationHandle", "(application.descriptor=" + pid + ")");
            if(appList != null && appList.length != 0)
            {
                for(int i = 0; i != appList.length; i++)
                {
                    ApplicationHandle handle = (ApplicationHandle)bc.getService(appList[i]);
                    bc.ungetService(appList[i]);
                    if(handle == appHandle)
                        throw new Exception("Application handle doesn't removed after destroy!");
                }

            }
            try
            {
                appHandle.getState();
            }
            catch(Exception e)
            {
                return hack_changeSingletonity(true);
            }
            throw new Exception("The status didn't change to NONEXISTENT!");
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return false;
    }

    boolean hack_changeSingletonity(boolean isSingleton)
    {
        try
        {
            MidletDescriptor midDesc = (MidletDescriptor)appDescs[0];
            Class midDescClass = org.osgi.service.application.midlet.MidletDescriptor.class;
            Field delegate = midDescClass.getDeclaredField("delegate");
            delegate.setAccessible(true);
            MidletDescriptorImpl midDescImpl = (MidletDescriptorImpl)delegate.get(midDesc);
            Class midDescImplClass = org.osgi.impl.service.midletcontainer.MidletDescriptorImpl.class;
            Field props = midDescImplClass.getDeclaredField("props");
            props.setAccessible(true);
            Properties properties = (Properties)props.get(midDescImpl);
            properties.setProperty("application.singleton", isSingleton ? "true" : "false");
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

  	private boolean testCase_oatRegisterService() {
      try {
  			sendEvent(new Event("com/nokia/megtest/CheckRegistered", null), false);
  			if (!checkResultFile("REGISTERED SUCCESSFULLY"))
  				throw new Exception("Event handler service was not registered!");
      	return true;
      }
      catch(Exception e) {
          e.printStackTrace();
      }
      return false;
  	}
}
