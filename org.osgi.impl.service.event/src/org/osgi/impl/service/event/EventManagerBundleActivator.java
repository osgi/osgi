package org.osgi.impl.service.event;

import org.osgi.framework.*;
import org.osgi.service.event.*;

public class EventManagerBundleActivator extends Object implements BundleActivator
{
  private BundleContext          bc;
  private ServiceRegistration    eventChannelServiceReg;
  private EventChannelImpl       eventChannelImpl;

  public EventManagerBundleActivator()
  {
    super();
  }

  public void start(BundleContext bc) throws Exception
  {
    this.bc = bc;
    
    ServiceReference reference = bc.getServiceReference( "org.osgi.service.event.EventChannel" );    
    if( reference != null )
      throw new BundleException( "EventChannel service already started!" );
    
    eventChannelImpl = new EventChannelImpl( bc );
      
    //registering the EventChannel service
    eventChannelServiceReg = bc.registerService("org.osgi.service.event.EventChannel", eventChannelImpl, null);
    System.out.println( "EventChannel started successfully!" );
  }

  public void stop(BundleContext bc) throws Exception
  {
    eventChannelImpl.stop();
    eventChannelServiceReg.unregister();
    this.bc = null;
    System.out.println( "EventChannel stopped successfully!" );
  }
}
