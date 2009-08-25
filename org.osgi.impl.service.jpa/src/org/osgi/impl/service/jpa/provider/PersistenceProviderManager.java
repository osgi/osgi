/*
 * Copyright (c) IBM Corporation (2009). All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.osgi.impl.service.jpa.provider;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.persistence.spi.PersistenceProvider;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.Constants;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.SynchronousBundleListener;

/**
 *  This class registers PersistenceProvider implementations on behalf
 *  of JPA provider bundles.
 */
public class PersistenceProviderManager implements SynchronousBundleListener, BundleActivator
{
  /** The Service Registrations for Persistence Providers */
  private final ConcurrentMap<Bundle, ServiceRegistration> registrations = new ConcurrentHashMap<Bundle, ServiceRegistration>();
  
  private BundleContext ctx;
  
  public void start(BundleContext ctx) throws Exception
  {
    this.ctx = ctx;
    
    ctx.addBundleListener(this);
    Bundle[] bundles = ctx.getBundles();
    
    for (Bundle b : bundles) {
      if (b.getState() == Bundle.STARTING ||
          b.getState() == Bundle.ACTIVE) {
        registerPotentialPersistenceProvider(b);
      }
    }
  }
  
  /**
   * This method determines whether a bundle is a persistence
   * provider bundle
   * 
   * @param b
   */
  private void registerPotentialPersistenceProvider(Bundle b)
  {
    URL provider = b.getResource("META-INF/services/javax.persistence.spi.PersistenceProvider");
    
    if (provider != null) {
      try {
        BufferedReader reader = null;
        String providerClassName = null;
        try {
          reader = new BufferedReader(new InputStreamReader(provider.openStream()));
          providerClassName = reader.readLine();
        } finally {
          if(reader != null)reader.close();
        }
        
        String[] classNames = new String[] {PersistenceProvider.class.getName(), providerClassName};
        String version = b.getVersion().toString();
        
        if(!!!alreadyRegistered(b, providerClassName))
        {
          PersistenceProviderFactory ppf = new PersistenceProviderFactory(b, providerClassName);
        
          Dictionary<String, Object> props = new Hashtable<String, Object>();
          props.put("osgi.jpa.provider.version", version);
        
          ServiceRegistration reg = b.getBundleContext().registerService(classNames, ppf, props);
        
          if(registrations.putIfAbsent(b,reg) != null)
            reg.unregister();
         }
      } catch (IOException e) {
        //TODO log this properly
        e.printStackTrace();
      }
    }
    
  }
  
  /**
   * Return true if the Bundle b has already registered a PersistenceProvider with the
   * implementation class name providerClassName
   * @param b
   * @param providerClassName
   * @return
   */
  private boolean alreadyRegistered(Bundle b, String providerClassName)
  {
    ServiceReference[] refs = b.getRegisteredServices();
    
    boolean registered = false;
    
    if(refs != null) {
      Filter f;
      try {
        f = ctx.createFilter("(&(" + Constants.OBJECTCLASS + "=" + PersistenceProvider.class.getName() +
            ")("  + Constants.OBJECTCLASS + "=" + providerClassName + "))");
        
        for(ServiceReference r : refs)
        {
          registered = f.match(r);
          if(registered) break;
        }
      } catch (InvalidSyntaxException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    return registered;
  }

  public void bundleChanged(BundleEvent event)
  {
    int eventType = event.getType();
    
    if(eventType == BundleEvent.STARTING || eventType == BundleEvent.LAZY_ACTIVATION)
      registerPotentialPersistenceProvider(event.getBundle());
    else if (eventType == BundleEvent.STOPPING) {
      ServiceRegistration reg = registrations.remove(event.getBundle());
      if(reg != null)
        reg.unregister();
    }
  }
  
  /**
   * Destroy this PersistenceProviderManager
   */
  public void stop(BundleContext ctx) throws Exception
  {
    ctx.removeBundleListener(this);
    //We do not need to unregister the PersistenceProviders on behalf of the
    //bundles. This will happen automatically when they shut down.
  }

}
