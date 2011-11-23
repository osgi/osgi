package org.osgi.impl.service.tr069todmt;

import java.util.ArrayList;

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

  BundleContext context;
  
  private ServiceRegistration reg;
  private ServiceTracker logTracker;
  private ArrayList<TR069Connector> connectors;
  
  public void start(BundleContext bc) throws Exception {
    context = bc;
    connectors = new ArrayList<TR069Connector>();
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
    for (int i = 0; i < connectors.size(); i++) {
      connectors.get(i).close();
    }
    connectors = null;
  }

  public TR069Connector create(DmtSession session) {
    TR069Connector connector = new TR069ConnectorImpl(session, this);
    connectors.add(connector);
    return connector;
  }
  
  void log(int level, String message, Throwable exception) {
    LogService log = (LogService)logTracker.getService();
    if (log != null) {
      log.log(level, message, exception);
    }
  }
  
}
