package org.osgi.impl.service.application;

import org.osgi.framework.*;

public class ApplicationManagerBundleActivator extends Object implements BundleActivator
{
  private BundleContext          bc;
  private ServiceRegistration    serviceReg;
  private ApplicationManagerImpl appManImpl;

  public ApplicationManagerBundleActivator()
  {
    super();
  }

  public void start(BundleContext bc) throws Exception
  {
    this.bc = bc;
    
    ServiceReference reference = bc.getServiceReference( "org.osgi.service.application.ApplicationManager" );    
    if( reference != null )
      throw new BundleException( "AppManager service already started!" );
    
    appManImpl = new ApplicationManagerImpl( bc );
    
    //registering the ApplicationManager service
    serviceReg = bc.registerService("org.osgi.service.application.ApplicationManager", appManImpl, null);    
    System.out.println( "ApplicationManager started successfully!" );
  }

  public void stop(BundleContext bc) throws Exception
  {
    //unregistering the ApplicationManager service
    serviceReg.unregister();
    this.bc = null;
    System.out.println( "ApplicationManager stopped successfully!" );
  }
}
