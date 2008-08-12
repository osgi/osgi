/*
 * $Header$
 * 
 * Copyright (c) OSGi Alliance (2005). All Rights Reserved.
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
package org.osgi.service.metatype2;

import org.osgi.service.metatype.ObjectClassDefinition;

/**
 * This service can be used to obtain MetaType information provided in the 
 * framework - from @link org.osgi.service.metatype.MetaTypeProvider MetaTypeProviders}
 *  or XML resources - in an uniform way. 
 *  <p>
 *  
 *  The <code>MetaDataService</code> identifies the <code>MetaTypeProviders</code>
 *  by their category and ID. The category is optional and defines the type 
 *  of meta-data that is provided (for example "ControlUnit", "Config", etc.). 
 *  The ID is obligatory and should be unique in the scope of the framework.
 *  <p>
 *  
 *  One way to provide MetaType information is by registering in the 
 *  framework a <code>MetaTypeProvider</code>, with optional service property 
 *  {@link #METATYPE_CATEGORY} equal to the category of the provided meta-data. 
 *  The service PID of the <code>MetaTypeProvider</code> will be considered 
 *  to be its ID. If the <code>MetaTypeProvider</code> service has no PID 
 *  specified its service ID would be taken instead.
 *  <p>
 *  
 *  Another way is by providing a XML resource in a bundle's jar file and 
 *  describing it in the bundle's manifest using the Meta-Type header.
 *  <br>
 *  Note: The XML format is defined in 
 *  RFC 69 - "Metatyping for the Control Units and Diagnostics".   
 *   
 *  <p>
 *  
 *  A metatype header must follow the syntax :
 *  <pre>Meta-Type: = 'xml' '=' &lt;resource&gt; ';' 'id' '=' &lt;MetaType ID&gt; 
 *      [ ';' 'category=' '=' &lt;meta type category&gt; ]
 *      [ ';' 'version'   '=' &lt;version&gt;]
 *  </pre>
 *  <p>
 *  
 *  For example :
 *  <pre>
 *  Meta-Type:
 *   xml=org/osgi/impl/cu/fw/fwcu.xml; category=ControlUnit; id=FRAMEWORK; version=1.0.0,
 *   xml=org/osgi/impl/cu/bundle/bundlecu.xml; category=ControlUnit; id=BUNDLE; version=1.0.0,
 *   xml=configuration.xml; category=Config; id=cu.config; version=1.0.0
 *  </pre>
 *  <p>
 *  
 *  If a non-valid MetaType XML is provided the <code>MetaDataService</code> 
 *  will log an error message and ignore the definition.
 *  <br>
 *  The MetaType definition will be ignored even if its XML is partially valid.
 *  <p>
 *  
 *  
 *  If a MetaType definition appears, which has the same category/ID pair as 
 *  an already available MetaType definition, the new one will be ignored and 
 *  the <code>MetaDataService</code> will log an error message.
 *  
 *  <p>
 *  Through this service one can obtain the MetaType definition by providing 
 *  a category and an ID, regardless of the method in which the MetaType 
 *  is made available in the framework.
 *  
 * @version $Revision$
 */
public interface MetaDataService {

  /**
   * Service property identifying a MetaType's category.
   */
  public static final String METATYPE_CATEGORY = "org.osgi.metatype.category";
  
  /**
   * Returns an object class definition with the specified category and ID localized to the specified locale.
   * 
   * @param category The category of the meta-data or null if it has no category.
   * @param metaTypeID The ID of the meta-data.
   * @param ocdID The ID of the requested object class.
   * @param locale The locale of the definition or null for default locale.
   * @return <code>ObjectClassDefinition</code>, <code>ExtendedObjectClassDefinition</code> or <code>null</code> 
   * if there is no MetaType provided with the given category and ID.
   * @throws IllegalArgumentException If the locale argument is not valid.
   * @throws NullPointerException If the meta-data or object class id is <code>null</code>.
   */
  public ObjectClassDefinition getObjectClassDefinition(String category,
    String metaTypeID, String ocdID, String locale) throws IllegalArgumentException;
  
  /**
   * Returns the IDs of all MetaTypes, for the given category, 
   * which are currently available in the framework.
   *  
   * @param category The category or null for IDs of MetaTypes with no category.
   * @return Array of meta-type IDs or null if there is no MetaType 
   * information provided in the framework for the given category.
   */
  public String[] getAvailableMetaTypes(String category);
  
  /**
   * Return all categories for which there is currently available MetaType 
   * information provided in the framework.
   * 
   * @return Array of meta-data categories or null if no MetaType in 
   * the framework has specified a category.
   */
  public String[] getAvailableCatagories();
  
  /**
   * Returns the available locales which a given MetaType provides. 
   * 
   * @param category The MetaType category or null for no category.
   * @param id The MetaType ID for which available locales are requested.
   * @return Array of locales or <code>null</code> if there is no locale 
   * specific localization available for the MetaType.
   * @throws NullPointerException If the id is <code>null</code>.
   */
  public String[] getMetaTypeLocales(String category, String id);
  
  /**
   * Returns the version of the MetaType with the given id, currently 
   * available in the framework for the specified category.
   *  
   * @param category The MetaType category or null for no category.
   * @param id The MetaType ID which version is requested.
   * @return MetaType version, <code>null</code> if there is no MetaType provided with 
   * the given category and ID or the available MetaType has no version.
   * @throws NullPointerException If the id is <code>null</code>
   */
  public String getMetaTypeVersion(String category, String id);
}
