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
package org.osgi.impl.service.jpa.unit.parsing;

import java.util.Collection;
import java.util.Stack;

import org.osgi.impl.service.jpa.unit.PersistenceUnitImpl;
import org.osgi.impl.service.jpa.util.PersistenceLocationData;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;


/**
 *  This code is responsible for parsing the persistence.xml into PersistenceUnits
 */
public class JPAHandler extends DefaultHandler
{
  /** The Persistence Units that we have parsed */
  private final Stack<PersistenceUnitImpl> persistenceUnits = new Stack<PersistenceUnitImpl>();
  /** The name of the current element */
  private String elementName;
  /** The persistence xml location data */
  private final PersistenceLocationData xmlLocationData;
  /** The version of the persistence.xml file */
  private final String jpaVersion;
  /** A StringBuilder for caching the information from getCharacters */
  private StringBuilder builder = new StringBuilder();
  
  /**
   * Create a new JPA Handler for the given peristence.xml
   * @param data
   * @param version  the version of the JPA schema used in the xml
   */
  public JPAHandler(PersistenceLocationData data, String version){
    xmlLocationData = data;
    jpaVersion = version;
  }
  
  /**
   * Collect up the characters, as element's characters may be split across multiple
   * calls. Isn't SAX lovely...
   */
  @Override
  public void characters(char[] ch, int start, int length) throws SAXException
  {
    builder.append(ch, start, length);
  }

  @Override
  public void startElement(String uri, String localName, String name, Attributes attributes)
      throws SAXException
  {
    //Do this setting first as we use it later.
    elementName = (localName == null || "".equals(localName))? name : localName;

    if("persistence-unit".equals(elementName)) {
      persistenceUnits.push(new PersistenceUnitImpl(attributes.getValue("name"), attributes.getValue("transaction-type"), xmlLocationData, jpaVersion));
    } else if("exclude-unlisted-classes".equals(elementName))
      persistenceUnits.peek().setExcludeUnlisted(true);
    else if("property".equals(elementName))
      persistenceUnits.peek().addProperty(attributes.getValue("name"), attributes.getValue("value"));
    
  }
  

  @Override
  public void endElement(String uri, String localName, String name) throws SAXException
  {
    String s = builder.toString().trim();
    //This step is VERY important, otherwise we pollute subsequent
    //elements
    builder = new StringBuilder();
    
    if("".equals(s)) return;
    
    PersistenceUnitImpl pu = persistenceUnits.peek();
    
    if("provider".equals(elementName))
      pu.setProviderClassName(s);
    else if("jta-data-source".equals(elementName))
      pu.setJtaDataSource(s);
    else if("non-jta-data-source".equals(elementName))
      pu.setNonJtaDataSource(s);
    else if("mapping-file".equals(elementName))
      pu.addMappingFileName(s);
    else if("jar-file".equals(elementName)) {
      pu.addJarFileName(s);
    } else if("class".equals(elementName))
      pu.addClassName(s);
    else if("exclude-unlisted-classes".equals(elementName))
      pu.setExcludeUnlisted(Boolean.parseBoolean(s));
  }

  @Override
  public void error(SAXParseException spe) throws SAXException
  {
    // We throw this exception to be caught further up and logged
    // as an error there
    throw spe;
  }

  /**
   * @return The collection of persistence units that we have parsed
   */
  public Collection<PersistenceUnitImpl> getPersistenceUnits()
  {
    return persistenceUnits;
  }

}
