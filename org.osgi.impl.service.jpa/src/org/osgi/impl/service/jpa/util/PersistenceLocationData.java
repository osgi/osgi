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

import org.osgi.framework.Bundle;

/**
 * A bean to hold metadata about the location of a persistence.xml file
 */
public class PersistenceLocationData
{
  /** The location of the persistence.xml file */
  private final URL persistenceXML;
  /** The location of the persistence unit root */
  private final URL persistenceUnitRoot;
  /** The persistence Bundle object */
  private final Bundle persistenceBundle;
  
  /**
   * Construct a new PersistenceLocationData
   * @param persistenceXML
   * @param persistenceUnitRoot
   * @param persistenceBundle
   */
  public PersistenceLocationData(URL persistenceXML, URL persistenceUnitRoot,
      Bundle persistenceBundle)
  {
    this.persistenceXML = persistenceXML;
    this.persistenceUnitRoot = persistenceUnitRoot;
    this.persistenceBundle = persistenceBundle;
  }
  
  /**
   * @return A URL to the persistence.xml file
   */
  public URL getPersistenceXML()
  {
    return persistenceXML;
  }
  
  /**
   * @return A URL to the persistence unit root
   */
  public URL getPersistenceUnitRoot()
  {
    return persistenceUnitRoot;
  }
  
  /**
   * @return The Bundle that defines this persistence unit
   */
  public Bundle getPersistenceBundle()
  {
    return persistenceBundle;
  }
  
}
