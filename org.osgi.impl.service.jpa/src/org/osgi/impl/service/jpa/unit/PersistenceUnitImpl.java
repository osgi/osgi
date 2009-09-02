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
package org.osgi.impl.service.jpa.unit;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;
import org.osgi.impl.service.jpa.util.PersistenceLocationData;
import org.osgi.service.jpa.PersistenceUnitInfoService;


/**
 * An implementation of {@link PersistenceUnitInfoService} for parsed persistence unit metadata
 */
@SuppressWarnings("unchecked")
public class PersistenceUnitImpl implements PersistenceUnitInfoService
{
  /** A map to hold the metadata from the xml */
  private final Map<String,Object> metadata = new HashMap<String, Object>();
  /** Information about the location of this persistence unit */
  private final PersistenceLocationData xmlLocationData;

  /**
   * The Service Reference for the provider to which this persistence
   * unit is tied
   */
  private ServiceReference provider;

  
  /**
   * Create a new persistence unit with the given name, transaction type, location and
   * defining bundle
   * 
   * @param name         may be null
   * @param transactionType  may be null
   * @param location
   * @param version    The version of the JPA schema used in persistence.xml
   */
  public PersistenceUnitImpl(String name, String transactionType, PersistenceLocationData location, String version)
  {
    xmlLocationData = location;
    
    metadata.put(SCHEMA_VERSION, version);

    if (name != null)metadata.put(UNIT_NAME, name);
    if (transactionType != null) metadata.put(TRANSACTION_TYPE, transactionType);

  }
  
  
  public Bundle getDefiningBundle()
  {
    return xmlLocationData.getPersistenceBundle();
  }

  public ServiceReference getProviderReference()
  {
    return provider;
  }

  public Map<String, Object> getPersistenceXmlMetadata()
  {
    Map<String, Object> data = new HashMap<String, Object>(metadata);
    if(data.containsKey(MAPPING_FILES))
      data.put(MAPPING_FILES, ((ArrayList) metadata.get(MAPPING_FILES)).clone());
    if(data.containsKey(JAR_FILES))
    data.put(JAR_FILES, ((ArrayList) metadata.get(JAR_FILES)).clone());
    if(data.containsKey(MANAGED_CLASSES))
    data.put(MANAGED_CLASSES, ((ArrayList) metadata.get(MANAGED_CLASSES)).clone());
    if(data.containsKey(PROPERTIES))
    data.put(PROPERTIES, ((Properties)metadata.get(PROPERTIES)).clone());
    
    return data;
  }

  public URL getPersistenceXmlLocation()
  {
    return xmlLocationData.getPersistenceXML();
  }
  
  public URL getPersistenceUnitRoot()
  {
    return xmlLocationData.getPersistenceUnitRoot();
  }

  /**
   * @param provider
   */
  public void setProviderClassName(String provider)
  {
    metadata.put(PROVIDER_CLASSNAME, provider);
  }

  /**
   * @param jtaDataSource
   */
  public void setJtaDataSource(String jtaDataSource)
  {
    metadata.put(JTA_DATASOURCE, jtaDataSource);
  }

  /**
   * @param nonJtaDataSource
   */
  public void setNonJtaDataSource(String nonJtaDataSource)
  {
    metadata.put(NON_JTA_DATASOURCE, nonJtaDataSource);
  }

  /**
   * @param mappingFileName
   */
  public void addMappingFileName(String mappingFileName)
  {
    List<String> files = (List<String>) metadata.get(MAPPING_FILES);
    if(files == null) {
      files = new ArrayList<String>();
      metadata.put(MAPPING_FILES, files);
    }
    files.add(mappingFileName);
  }

  /**
   * @param jarFile
   */
  public void addJarFileName(String jarFile)
  {
    List<String> jars = (List<String>) metadata.get(JAR_FILES);
    if(jars == null) {
      jars = new ArrayList<String>();
      metadata.put(JAR_FILES, jars);
    }
      
    jars.add(jarFile);
  }

  /**
   * @param className
   */
  public void addClassName(String className)
  {
    List<String> classes = (List<String>) metadata.get(MANAGED_CLASSES);
    if(classes == null) {
      classes = new ArrayList<String>();
      metadata.put(MANAGED_CLASSES, classes);
    }
    classes.add(className);
  }

  /**
   * @param exclude
   */
  public void setExcludeUnlisted(boolean exclude)
  {
    metadata.put(EXCLUDE_UNLISTED_CLASSES, exclude);
  }

  /**
   * @param name
   * @param value
   */
  public void addProperty(String name, String value)
  {
    Properties props = (Properties) metadata.get(PROPERTIES);
    if(props == null) {
      props = new Properties();
      metadata.put(PROPERTIES, props);
    }
    props.setProperty(name, value);
  }

  /**
   * @param providerRef
   */
  public void setProviderReference(ServiceReference providerRef)
  {
    provider = providerRef;
  }

  public ClassLoader getClassLoader()
  {
    return new BundleDelegatingClassLoader(getDefiningBundle());
  }
  
  private static class BundleDelegatingClassLoader extends ClassLoader
  {
    private final Bundle bundle;
    
    public BundleDelegatingClassLoader(Bundle b)
    {
      super(ClassLoader.getSystemClassLoader());
      bundle = b;
    }
    
    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException
    {
      return bundle.loadClass(name);
    }
    
    @Override
    protected URL findResource(String resName)
    {
      return bundle.getResource(resName);
    }
    
    @Override
    protected Enumeration<URL> findResources(String resName) throws IOException
    {
      return bundle.getResources(resName);
    }
  }
}
