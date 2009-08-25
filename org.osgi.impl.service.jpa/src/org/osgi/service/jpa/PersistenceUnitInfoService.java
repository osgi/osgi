/*
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
package org.osgi.service.jpa;

import java.net.URL;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;

/**
 * This interface exposes the information found or created by the JPA Extender when
 * discovering persistence units. A service will be registered in the service
 * registry for each persistence unit discovered.
 */
public interface PersistenceUnitInfoService {

    /* ===============================================================
     * Constants section
     * ===============================================================
     */
    
    /* =======================================================================
     * The following constants are used as keys in the persistenceXmlMetadata
     * Map and represent elements or attributes of a persistence unit in a
     * persistence.xml file. If the elements were not specified in the
     * persistence.xml file then the corresponding entries will not be present
     * in the persistenceXmlMetadata Map.
     * =======================================================================
     */

    /**
     * The key used to store the schema version of the persistence.xml file.
     * This entry should always be present and will always contain a value.
     *
     *   persistence.xml – <persistence version=...> attribute
     *   value type - String
     */
    public static final String SCHEMA_VERSION =
                                   "javax.persistence.schemaVersion";
    
    /**
     * The key used to store the persistence unit name.
     * This entry should always be present and will always contain a value.
     *
     *   persistence.xml – <persistence-unit name=...> attribute
     *   value type - String
     */
    public static final String UNIT_NAME = "javax.persistence.unitName";
    
    /**
     * The key used to store the transaction type.
     *
     *   persistence.xml – <persistence-unit transaction-type=...> attribute
     *   value type - String
     */
    public static final String TRANSACTION_TYPE =
                                 "javax.persistence.transactionType";

    /**
     * The key used to store the provider class name.
     *
     *   persistence.xml – <provider> element
     *   value type - String
     */
    public static final String PROVIDER_CLASSNAME = "javax.persistence.provider";

    /**
     * The key used to store the JTA data source.
     *
     *   persistence.xml – <jta-data-source> element
     *   value type - String
     */
    public static final String JTA_DATASOURCE = "javax.persistence.jtaDataSource";

    /**
     * The key used to store the non-JTA data source.
     *
     *   persistence.xml – <non-jta-data-source> element
     *   value type - String
     */
    public static final String NON_JTA_DATASOURCE =
                                 "javax.persistence.nonJtaDataSource";
    
    /**
     * The key used to store the List of mapping file names.
     *
     *   persistence.xml – <mapping-file> elements
     *   value type - List<String>
     */
    public static final String MAPPING_FILES = "javax.persistence.mappingFiles";
    
    /**
     * The key used to store the List of jar file locations.
     *
     *   persistence.xml – <jar-file> elements
     *   value type - List<URL>
     */
    public static final String JAR_FILES = "javax.persistence.jarFiles";
    
    /**
     * The key used to store the List of managed class names.
     * 
     *   persistence.xml – <class> elements
     *   value type - List<String>
     */
    public static final String MANAGED_CLASSES  =
                                 "javax.persistence.managedClasses";
    
    /**
     * The key used to store whether unlisted classes should be excluded.
     * 
     *   persistence.xml – <exclude-unlisted-classes> element
     *   value type - Boolean
     */
    public static final String EXCLUDE_UNLISTED_CLASSES =
                                 "javax.persistence.excludeUnlistedClasses";
    
    /**
     * The key used to store the shared cache mode.
     * 
     *   persistence.xml – <shared-cache-mode> element
     *   value type - String
     */
    public static final String SHARED_CACHE_MODE  =
                                 "javax.persistence.sharedCacheMode";
    
    /**
     * The key used to store the validation mode.
     * 
     *   persistence.xml – <validation-mode> element
     *   value type - String
     */
    public static final String VALIDATION_MODE  =
                                 "javax.persistence.validationMode";
 
    /**
     * The key used to store the persistence unit properties.
     *
     *   persistence.xml – <property> elements
     *   value type - Properties
     */
    public static final String PROPERTIES = "javax.persistence.properties";
    
    /* ===============================================================
     * The following constants are used as service property keys when
     * registering this object as a service in the service registry.
     * ===============================================================
     */
    
    /**
     * The service property key used to store the persistence unit name.
     */
    public static final String PERSISTENCE_UNIT_NAME =
                                   "osgi.jpa.persistence.unit.name";
    /**
     * The service property key used to store the bundle symbolic name.
     */
    public static final String PERSISTENCE_BUNDLE_SYMBOLIC_NAME =
                                   "osgi.jpa.persistence.bundle.name";
    /**
     * The service property key used to store the bundle version.
     */
    public static final String PERSISTENCE_BUNDLE_VERSION =
                                   "osgi.jpa.persistence.bundle.version";
    
    /* ===============================================================
     * The following properties are defined here for convenience.
     * ===============================================================
     */
    
    /**
     * The key used to register the JPA provider version service property.
     * It may also be used as a persistence.xml property key to specify a
     * provider version range for the persistence unit.
     */
    public static final String JPA_PROVIDER_VERSION = "osgi.jpa.provider.version";
    
    
    /* ===============================================================
     * Methods section
     * ===============================================================
     */
    
    /**
     * Expose the metadata for a particular persistence unit. The metadata is
     * obtained by parsing the elements and attributes for the persistence unit
     * defined in a persistence.xml file. Entries in the Map will have keys and
     * values as defined by the constants above.  Changes to the returned Map
     * will not be reflected in the original Map.
     *
     * @return A copy of the metadata Map
     */
    public Map<String, Object> getPersistenceXmlMetadata();
    
    /**
     * A provider has been associated with this persistence unit, either because
     * the provider was listed in the persistence.xml file, or was defaulted.
     *
     * @return A ServiceReference to the provider that is expected to service
     *         this persistence unit
     */
    public ServiceReference getProviderReference();
    
    /**
     * Return a URL that may use an OSGi-specific protocol. In order to pass the
     * URL in a PersistenceUnitInfo, in a format the provider can use to stream
     * over, the container may need to convert this URL to a file-based one.
     *
     * @return A URL to the location of the persistence.xml file that defined this
     * persistence unit
     */
    public URL getPersistenceXmlLocation();
    
    /**
     * Return a URL that may use an OSGi-specific protocol. In order to pass the
     * URL in a PersistenceUnitInfo, in a format the provider can use to stream
     * over, the container may need to convert this URL to a file-based one.
     *
     * @return A URL to the location of the persistence unit root
     */
    public URL getPersistenceUnitRoot();
    
    /**
     * Provide a reference to the bundle in which the persistence unit was
     * detected. The bundle may be a client bundle, or it may be a standalone
     * persistence unit bundle.
     *
     * @return The bundle in which the persistence.xml file was detected
     */
    public Bundle getDefiningBundle();
    
    /**
     * A classloader with visibility to the classes and resources in the bundle
     * in which the persistence unit was detected.
     *
     * @return Classloader that can access persistence unit classes and resources
     */
    public ClassLoader getClassLoader();
}