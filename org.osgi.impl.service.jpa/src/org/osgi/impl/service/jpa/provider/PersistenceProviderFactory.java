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

import java.util.concurrent.atomic.AtomicReference;

import javax.persistence.spi.PersistenceProvider;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceRegistration;

/**
 * A factory for lazily creating PersistenceProvider Implementations
 */
public class PersistenceProviderFactory implements ServiceFactory
{

  /** The JPA provider's implementation class name */
  private final String className;
  
  /** The bundle containing the JPA provider */
  private final Bundle providerBundle;

  /** The persistence provider object */
  private final AtomicReference<PersistenceProvider> provider = new AtomicReference<PersistenceProvider>();
  
  /** Constructor
   *
   *@param b The Persistence Bundle
   *@param providerClassName The provider's implementation class name
   */
  public PersistenceProviderFactory(Bundle b, String providerClassName)
  {
    className = providerClassName;
    providerBundle = b;
  }

  @SuppressWarnings("unchecked")
  public PersistenceProvider getService(Bundle arg0, ServiceRegistration arg1)
  {
    if(provider.get() == null)
    {
      try {
        Class<? extends PersistenceProvider> providerClass = providerBundle.loadClass(className);
        provider.compareAndSet(null, providerClass.newInstance());
      } catch (Exception e) {
        try {
          providerBundle.stop();
        } catch (BundleException e1) {
          // TODO Auto-generated catch block
          e1.printStackTrace();
        }
        e.printStackTrace();
      }
    }
    return provider.get();
  }

  public void ungetService(Bundle arg0, ServiceRegistration arg1, Object arg2)
  {
    // No-op. We never want to tidy this up as it doesn't consume many
    // resources once instantiated.
  }

}
