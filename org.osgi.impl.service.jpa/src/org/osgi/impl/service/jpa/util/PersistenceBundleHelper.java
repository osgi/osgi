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
package org.osgi.impl.service.jpa.util;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;

/**
 * This helper can be used to locate persistence.xml files in a bundle
 */
public class PersistenceBundleHelper
{
  /** The persistence xml location */
  private static final String PERSISTENCE_XML = "META-INF/persistence.xml";

  /**
   * This method locates persistence xml files in the following
   * locations:
   * META-INF
   * WEB-INF/classes
   * the META-INF of any jar in WEB-INF/lib
   * the META-INF of any jar on the Bundle-ClassPath
   * 
   * Note that getEntry and getEntryPaths are used to ensure
   * we do not transition the bundle to RESOLVED
   * 
   * @param bundle
   * @return
   */
  public static Collection<PersistenceLocationData> findPersistenceXmlFiles(Bundle bundle)
  {
    
    Collection<PersistenceLocationData> persistenceXmlFiles = new ArrayList<PersistenceLocationData>();
    
    addLocationToCollectionIfFound(persistenceXmlFiles, "", bundle);
    
    addLocationToCollectionIfFound(persistenceXmlFiles, "WEB-INF/classes", bundle);
   
    @SuppressWarnings("unchecked")
    Enumeration<String> webInfLibJars = bundle.getEntryPaths("WEB-INF/lib");
    
    if(webInfLibJars != null) {
      
      List<String> paths = Collections.list(webInfLibJars);
      Iterator<String> it = paths.iterator();
      
      while(it.hasNext()){
        String s = it.next();
        // We want to process jars in WEB-INF/lib/ so it should end
        // .jar, and not contain any / separators after character 11
        if(s.endsWith(".jar") && s.lastIndexOf('/') < 12) {
          processNestedJar(bundle, persistenceXmlFiles, s);
        }
      }
    }
    
    String bundleClassPath = (String) bundle.getHeaders().get(Constants.BUNDLE_CLASSPATH);

    if(bundleClassPath != null) {
      String[] cpEntries = bundleClassPath.split(",");
      
      for (String s : cpEntries) {
        s = s.trim();
        if(s.endsWith(".jar")) {
          processNestedJar(bundle, persistenceXmlFiles, s);
        }
      }
    }
      return persistenceXmlFiles;
  }

  /**
   * Check to see if a nested jar contains a "META-INF/persistence.xml" file
   * and add it to the list if it does
   * 
   * @param bundle
   * @param persistenceXmlFiles
   * @param jarLocation
   */
  private static void processNestedJar(Bundle bundle,
      Collection<PersistenceLocationData> persistenceXmlFiles, String jarLocation)
  {
    URL jar = bundle.getEntry(jarLocation);
    if(jar != null) {
    ClassLoader cl = new URLClassLoader(new URL[] {jar});
    URL xml = cl.getResource(PERSISTENCE_XML);

    if(xml != null)
      persistenceXmlFiles.add(new PersistenceLocationData(xml, jar, bundle));
    }
  }
  
  /**
   * This method will attempt to find an entry for a given path in a given bundle
   * and add it to the collection if the entry exists
   * @param collection
   * @param rootPath
   * @param bundle
   */
  private static void addLocationToCollectionIfFound(Collection<PersistenceLocationData> collection, String rootPath, Bundle bundle)
  {
    rootPath = (rootPath.endsWith("/")) ? rootPath : rootPath + "/";
    URL root = bundle.getEntry(rootPath);
    if(root != null) {
      String xmlPath = rootPath + PERSISTENCE_XML;
      URL xml = bundle.getEntry(xmlPath);
      if(xml != null)
        collection.add(new PersistenceLocationData(xml, root, bundle));
    }
  }
  
}
