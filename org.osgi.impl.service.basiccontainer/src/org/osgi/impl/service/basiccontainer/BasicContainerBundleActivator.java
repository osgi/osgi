package org.osgi.impl.service.basiccontainer;

import org.osgi.framework.*;
import java.util.*;

public class BasicContainerBundleActivator extends Object implements BundleActivator
{
  private BundleContext bc;
  private ServiceRegistration serviceReg;
  private BasicContainer containerImpl;

  public BasicContainerBundleActivator()
  {
    super();
  }

  public void start(BundleContext bc) throws Exception
  {
    this.bc = bc;
    
    containerImpl = new BasicContainer( bc, "BasicContainer" );
    
    Dictionary properties = new Hashtable();
    properties.put("application_type", "BasicContainer");
    properties.put("bundle_id", new Long( bc.getBundle().getBundleId() ).toString() );

    //registering the service
    serviceReg = bc.registerService("org.osgi.service.application.ApplicationContainer", containerImpl, properties);
    
    System.out.println( "Bundle started successfully!" );
  }

  public void stop(BundleContext bc) throws Exception
  {
    //unregistering the service
    containerImpl.unregisterAllApplications();
    serviceReg.unregister();
    this.bc = null;
    System.out.println( "Bundle stopped successfully!" );
  }
}
