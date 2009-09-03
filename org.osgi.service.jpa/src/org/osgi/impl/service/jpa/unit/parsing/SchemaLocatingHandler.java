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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.xml.XMLConstants;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.osgi.framework.Bundle;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * This parser provides a quick mechanism for determining the JPA schema level,
 * and throws an EarlyParserReturn with the Schema to validate with
 */
public class SchemaLocatingHandler extends DefaultHandler
{
  /** The RFC 143 bundle object */
  private final Bundle extender;
  
  /**
   * A static cache of schemas in use in the runtime
   */
  private static final ConcurrentMap<String, Schema> schemaCache = new ConcurrentHashMap<String, Schema>();
  
  /**
   * Create a new SchemaLocatingHandler
   * @param bundle The bundle object for the RFC 143 bundle
   */
  public SchemaLocatingHandler(Bundle bundle)
  {
    extender = bundle;
  }

  @Override
  public void startElement(String uri, String localName, String name, Attributes attributes)
      throws SAXException
  {
    
    Schema s = null;
    String version = null;
    if("persistence".equals(name)) {
      version = attributes.getValue(uri, "version");
       s = validate(version);
    }
    throw new EarlyParserReturn(s, version);
  }
  
  /**
   * Find the schema for the version of JPA we're using
   * @param type  The value of the version attribute in the xml
   * @return
   * @throws SAXException
   */
  private final Schema validate(String type) throws SAXException
  {
    Schema toReturn = (type == null)? null : schemaCache.get(type);
    
    if(toReturn == null) {
      toReturn = getSchema(type);
      if(toReturn != null) schemaCache.putIfAbsent(type, toReturn);
    }
    
    return toReturn;
  }

  /**
   * Locate the schema document
   * @param type The schema version to find
   * @return
   * @throws SAXException
   */
  private final Schema getSchema(String type) throws SAXException
  {
    SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
    URL schemaURL = null;
    Schema schema = null;
    if("1.0".equals(type)) {
      try {
        schemaURL = new URL("http://java.sun.com/xml/ns/persistence/persistence_1_0.xsd");
      } catch (MalformedURLException e) {
        //This should never happen!
        e.printStackTrace();
      }
    }
    //TODO handle JPA 2.0
    if(schemaURL != null){
      schema = schemaFactory.newSchema(schemaURL);
    }
    
    return schema;
  }
  
}
