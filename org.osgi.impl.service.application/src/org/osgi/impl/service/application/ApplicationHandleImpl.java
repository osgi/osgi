package org.osgi.impl.service.application;

import org.osgi.service.application.*;
import org.osgi.framework.*;
import java.util.*;

public class ApplicationHandleImpl implements ApplicationHandle
{
    private int status;
    private ApplicationDescriptor appDesc;
    private Application application;
    private ServiceRegistration serviceReg;
    private BundleContext bc;
   
    public ApplicationHandleImpl( ApplicationDescriptor desc, BundleContext bc ) throws Exception
    {
      application = null;
      appDesc = desc;
      status = ApplicationHandle.NONEXISTENT;
      this.bc = bc;
    }
     
    public int getAppStatus()
    {
      return status;
    }
    
    public ApplicationDescriptor getAppDescriptor()
    {
      return appDesc;
    }
    
    public void destroyApplication() throws Exception
    {
      if( status == ApplicationHandle.NONEXISTENT || 
          status == ApplicationHandle.STOPPING )
        throw new Exception( "Invalid State" );
        
      if( application != null )
      {
        status = ApplicationHandle.STOPPING;
        application.stopApplication();
        status = ApplicationHandle.NONEXISTENT;
        
        unregisterAppHandle();
      }
      else
        throw new Exception( "Invalid application handle!" );
    }

    public void suspendApplication() throws Exception
    {
      if( status != ApplicationHandle.RUNNING )
        throw new Exception( "Invalid State" );
        
      if( application != null )
      {
        status = ApplicationHandle.SUSPENDING;
        application.suspendApplication();
        status = ApplicationHandle.SUSPENDED;
      }
      else
        throw new Exception( "Invalid application handle!" );
    }

    public void resumeApplication() throws Exception
    {
      if( status != ApplicationHandle.SUSPENDED )
        throw new Exception( "Invalid State" );
        
      if( application != null )
      {
        status = ApplicationHandle.RESUMING;
        application.resumeApplication();
        status = ApplicationHandle.RUNNING;
      }
      else
        throw new Exception( "Invalid application handle!" );
    }

    void applicationStarted()
    {
       status = ApplicationHandle.RUNNING;
    }
    
    void setApplication( Application app )    
    {
      application = app;
    }
    
    void registerAppHandle()
    {
      //registering the ApplicationHandle
      Hashtable props = new Hashtable();
      props.put( "unique_id", appDesc.getUniqueID() );
      serviceReg = bc.registerService("org.osgi.service.application.ApplicationHandle", this, props);      
    }
    
    void unregisterAppHandle()
    {
      if( serviceReg != null )
      {
        //unregistering the ApplicationHandle
        serviceReg.unregister(); 
        serviceReg = null;
      }
    }
}
