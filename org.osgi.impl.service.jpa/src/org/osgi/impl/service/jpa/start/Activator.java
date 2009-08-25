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
package org.osgi.impl.service.jpa.start;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.impl.service.jpa.unit.PersistenceBundleManager;
import org.osgi.service.impl.jpa.provider.PersistenceProviderManager;


public class Activator implements BundleActivator
{
  private PersistenceProviderManager providerMgr;
  private PersistenceBundleManager bundleMgr;
  
  @Override
  public void start(BundleContext ctx) throws Exception
  {
   try {
     providerMgr = new PersistenceProviderManager();
     providerMgr.start(ctx);
   } catch (Exception e) {
     //TODO log this error
     e.printStackTrace();
   }

   try {
     bundleMgr = new PersistenceBundleManager();
     bundleMgr.start(ctx);
   } catch (Exception e) {
     //TODO log this error
     e.printStackTrace();
   }
  }

  @Override
  public void stop(BundleContext ctx) throws Exception
  {
    Exception ex = null;
    
    try {
      providerMgr.stop(ctx);
    } catch (Exception e) {
      ex = e;
      //TODO Cope with this?
    }
    
    try {
      bundleMgr.stop(ctx);
    } catch (Exception e) {
      ex = e;
      //TODO Cope with this?
    }
    if (ex != null) throw ex;
  }
}
