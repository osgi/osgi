package org.osgi.impl.service.tr069todmt;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import org.osgi.util.tracker.ServiceTracker;

import org.osgi.service.dmt.DmtSession;
import org.osgi.service.log.LogService;

import org.osgi.service.tr069todmt.TR069Connector;
import org.osgi.service.tr069todmt.TR069ConnectorFactory;


/**
 * 
 *
 */
public class TR069ConnectorFactoryImpl implements BundleActivator, TR069ConnectorFactory {

  private ServiceRegistration reg;
  
  BundleContext context;
  ServiceTracker logTracker;
  
  public void start(BundleContext bc) throws Exception {
    context = bc;
    logTracker = new ServiceTracker(bc, LogService.class.getName(), null);
    logTracker.open();
    reg = bc.registerService(new String[] {TR069ConnectorFactory.class.getName()}, this, null);
  }

  public void stop(BundleContext bc) throws Exception {
    if (reg != null) {
      reg.unregister();
    }
    if (logTracker != null) {
      logTracker.close();
    }
  }

  public TR069Connector create(DmtSession session) {
    return new TR069ConnectorImpl(session, this);
  }

}
