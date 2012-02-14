/*
 * Copyright (c) OSGi Alliance (2010-2012). All Rights Reserved.
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
package org.osgi.jmx.framework.wiring;

import java.io.IOException;

import javax.management.JMException;
import javax.management.openmbean.ArrayType;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.SimpleType;
import javax.management.openmbean.TabularData;
import javax.management.openmbean.TabularType;

import org.osgi.jmx.Item;
import org.osgi.jmx.JmxConstants;


/**
 * This MBean represents the bundle wiring state.
 * <p>
 * It can be used to retrieve the declared capabilities, declared requirements, and wiring
 * for the current and past revisions of bundles.
 *
 * @ThreadSafe
 */
public interface BundleWiringStateMBean {
    /*
     * The Object Name for a Bundle Revisions MBean.
     */
    String OBJECTNAME = JmxConstants.OSGI_CORE
        + ":type=wiringState,version=1.7";

    /*
     * Items, CompositeData, TabularData, ArrayTypes
     *
     */
    String KEY = "Key";
    Item KEY_ITEM = new Item(KEY, "The directive key", SimpleType.STRING);

    String VALUE = "Value";
    Item VALUE_ITEM = new Item(VALUE, "The directive value",
            SimpleType.STRING);

    CompositeType DIRECTIVE_TYPE = Item.compositeType("DIRECTIVE",
            "Describes a directive of a capability or requirement",
            KEY_ITEM, VALUE_ITEM);
    TabularType DIRECTIVES_TYPE = Item.tabularType("DIRECTIVES",
            "Describes the directives of a capability or requirement",
            DIRECTIVE_TYPE, KEY
            );

    String DIRECTIVES = "Directives";
    Item DIRECTIVES_ITEM = new Item(DIRECTIVES,
            "The directives of a capability or requirement",
            DIRECTIVES_TYPE);

    TabularType ATTRIBUTES_TYPE = Item.tabularType("ATTRIBUTES",
            "Describes attributes of a capability or requirement",
            JmxConstants.PROPERTY_TYPE, JmxConstants.KEY
            );
    String ATTRIBUTES = "Attributes";
    Item ATTRIBUTES_ITEM = new Item(ATTRIBUTES,
            "The attributes of a capability or requirement",
            ATTRIBUTES_TYPE);

    String NAMESPACE = "Namespace";
    Item NAMESPACE_ITEM = new Item(NAMESPACE,
            "The namespace of a capability or requirement",
            SimpleType.STRING);

    CompositeType BUNDLE_REQUIREMENT_TYPE =
        Item.compositeType("BUNDLE_REQUIREMENT",
                "Describes the live wired requirements of a bundle",
                ATTRIBUTES_ITEM, DIRECTIVES_ITEM, NAMESPACE_ITEM);

    CompositeType BUNDLE_CAPABILITY_TYPE =
        Item.compositeType("BUNDLE_CAPABILITY",
                "Describes the live wired capabilities of a bundle",
                ATTRIBUTES_ITEM, DIRECTIVES_ITEM, NAMESPACE_ITEM);

    String PROVIDER_BUNDLE_ID = "ProviderBundleId";
    Item PROVIDER_BUNDLE_ID_ITEM = new Item(PROVIDER_BUNDLE_ID,
            "The identifier of the bundle that is the provider of the capability",
            SimpleType.LONG);

    String REQUIRER_BUNDLE_ID = "RequirerBundleId";
    Item REQUIRER_BUNDLE_ID_ITEM = new Item(REQUIRER_BUNDLE_ID,
            "The identifier of the bundle that is the requirer of the requirement",
            SimpleType.LONG);

    String BUNDLE_REQUIREMENT = "BundleRequirement";
    Item BUNDLE_REQUIREMENT_ITEM = new Item(BUNDLE_REQUIREMENT,
            "The wired requirements of a bundle",
            BUNDLE_REQUIREMENT_TYPE);

    String BUNDLE_CAPABILITY = "BundleCapability";
    Item BUNDLE_CAPABILITY_ITEM = new Item(BUNDLE_CAPABILITY,
            "The wired capabilities of a bundle",
            BUNDLE_CAPABILITY_TYPE);

    String PROVIDER_BUNDLE_REVISION_ID = "ProviderBundleRevisionId";
    Item PROVIDER_BUNDLE_REVISION_ID_ITEM = new Item(PROVIDER_BUNDLE_REVISION_ID,
            "A local id for the bundle revision that is the provider of the capability",
            SimpleType.INTEGER);

    String REQUIRER_BUNDLE_REVISION_ID = "RequirerBundleRevisionId";
    Item REQUIRER_BUNDLE_REVISION_ID_ITEM =  new Item(REQUIRER_BUNDLE_REVISION_ID,
            "A local id for the bundle revision that is the requirer of the requirement",
            SimpleType.INTEGER);

    /**
     * Describes the live association between a provider of
     * a capability and a requirer of the corresponding requirement.
     */
    CompositeType BUNDLE_WIRE_TYPE =
        Item.compositeType("BUNDLE_WIRE",
                "Describes the live association between a provider and a requirer",
                BUNDLE_REQUIREMENT_ITEM,
                BUNDLE_CAPABILITY_ITEM,
                PROVIDER_BUNDLE_ID_ITEM,
                PROVIDER_BUNDLE_REVISION_ID_ITEM,
                REQUIRER_BUNDLE_ID_ITEM,
                REQUIRER_BUNDLE_REVISION_ID_ITEM
                );
    ArrayType BUNDLE_WIRES_TYPE_ARRAY =
        Item.arrayType(1, BUNDLE_WIRE_TYPE);

    String BUNDLE_REVISION_ID = "BundleRevisionId";
    Item BUNDLE_REVISION_ID_ITEM = new Item(BUNDLE_REVISION_ID,
            "The local identifier of the bundle revision",
            SimpleType.INTEGER);

    String BUNDLE_ID = "BundleId";
    Item BUNDLE_ID_ITEM = new Item(BUNDLE_ID,
            "The bundle identifier of the bundle revision",
            SimpleType.LONG);

    ArrayType REQUIREMENT_TYPE_ARRAY =
        Item.arrayType(1, BUNDLE_REQUIREMENT_TYPE);
    ArrayType CAPABILITY_TYPE_ARRAY =
        Item.arrayType(1, BUNDLE_CAPABILITY_TYPE);

    String REQUIREMENTS = "Requirements";
    Item REQUIREMENTS_ITEM = new Item(REQUIREMENTS,
            "The bundle requirements of a bundle revision wiring",
            REQUIREMENT_TYPE_ARRAY);

    CompositeType REVISION_REQUIREMENTS_TYPE =
        Item.compositeType("REVISION_REQUIREMENTS",
            "Describes the requirements for a bundle revision",
            BUNDLE_REVISION_ID_ITEM,
            REQUIREMENTS_ITEM);

    TabularType REVISIONS_REQUIREMENTS_TYPE =
        Item.tabularType("REVISIONS_REQUIREMENTS",
            "The bundle requirements for all bundle revisions",
            REVISION_REQUIREMENTS_TYPE,
            BUNDLE_REVISION_ID);

    String CAPABILITIES = "Capabilities";
    Item CAPABILITIES_ITEM = new Item(CAPABILITIES,
            "The bundle capabilities of a bundle revision wiring",
            CAPABILITY_TYPE_ARRAY);

    CompositeType REVISION_CAPABILITIES_TYPE =
        Item.compositeType("REVISION_CAPABILITIES",
            "Describes the capabilities for a bundle revision",
            BUNDLE_REVISION_ID_ITEM,
            CAPABILITIES_ITEM);

    TabularType REVISIONS_CAPABILITIES_TYPE =
        Item.tabularType("REVISIONS_CAPABILITIES",
            "The bundle capabilities for all bundle revisions",
            REVISION_CAPABILITIES_TYPE,
            BUNDLE_REVISION_ID);

    String PROVIDED_WIRES = "ProvidedWires";
    Item PROVIDED_WIRES_ITEM = new Item(PROVIDED_WIRES,
            "The bundle wires to the capabilities provided by this bundle wiring.",
            BUNDLE_WIRES_TYPE_ARRAY);

    String REQUIRED_WIRES = "RequiredWires";
    Item REQUIRED_WIRES_ITEM = new Item(REQUIRED_WIRES,
            "The bundle wires to requirements in use by this bundle wiring",
            BUNDLE_WIRES_TYPE_ARRAY);

    CompositeType BUNDLE_WIRING_TYPE =
        Item.compositeType("BUNDLE_WIRING",
                "Describes the runtime association between a provider and a requirer",
                BUNDLE_ID_ITEM,
                BUNDLE_REVISION_ID_ITEM,
                REQUIREMENTS_ITEM,
                CAPABILITIES_ITEM,
                REQUIRED_WIRES_ITEM,
                PROVIDED_WIRES_ITEM);

    TabularType BUNDLES_WIRING_TYPE =
        Item.tabularType("BUNDLES_WIRING",
            "The bundle wiring for all bundle revisions",
            BUNDLE_WIRING_TYPE,
            BUNDLE_ID, BUNDLE_REVISION_ID);

    /**
	 * Returns the requirements for the current bundle revision.
	 * 
	 * @see #REQUIREMENT_TYPE_ARRAY for the details of the CompositeData.
	 * 
	 * @param bundleId The bundle ID.
	 * @param namespace The namespace of the requirements to be returned by this
	 *        operation.
	 * @return the declared requirements for the current revision of
	 *         <code>bundleId</code> and <code>namespace</code>.
	 * @throws JMException if there is a JMX problem.
	 * @throws IOException if the connection could not be made because of a
	 *         communication problem.
	 */
    CompositeData[] getCurrentRevisionDeclaredRequirements(long bundleId, String namespace)
            throws IOException, JMException;

    /**
	 * Returns the capabilities for the current bundle revision.
	 * 
	 * @see #CAPABILITY_TYPE_ARRAY for the details of the CompositeData.
	 * 
	 * @param bundleId The bundle ID.
	 * @param namespace The namespace of the capabilities to be returned by this
	 *        operation.
	 * @return the declared capabilities for the current revision of
	 *         <code>bundleId</code> and <code>namespace</code>.
	 * @throws JMException if there is a JMX problem.
	 * @throws IOException if the connection could not be made because of a
	 *         communication problem.
	 */
    CompositeData[] getCurrentRevisionDeclaredCapabilities(long bundleId, String namespace)
            throws IOException, JMException;

    /**
	 * Returns the bundle wiring for the current bundle revision.
	 * 
	 * @see #BUNDLE_WIRING_TYPE for the details of the CompositeData.
	 * 
	 * @param bundleId The bundle ID.
	 * @param namespace The namespace of the requirements and capabilities for
	 *        which to return information.
	 * @return the wiring information for the current revision of
	 *         <code>bundleId</code> and <code>namespace</code>.
	 * @throws JMException if there is a JMX problem.
	 * @throws IOException if the connection could not be made because of a
	 *         communication problem.
	 */
    CompositeData getCurrentWiring(long bundleId, String namespace)
            throws IOException, JMException;

    /**
	 * Returns the bundle wiring closure for the current revision of the
	 * specified bundle. The wiring closure contains all the wirings from the
	 * root bundle revision to all bundle revisions it is wired to and all their
	 * transitive wirings.
	 * 
	 * @see #BUNDLES_WIRING_TYPE for the details of the TabularData.
	 * 
	 * @param rootBundleId the root bundle of the closure.
	 * @param namespace The namespace of the requirements and capabilities for
	 *        which to return information.
	 * @return a tabular representation of all the wirings in the closure. The
	 *         bundle revision IDs only have meaning in the context of the
	 *         current result. The revision of the rootBundle is set to 0.
	 *         Therefore the root bundle of the closure can be looked up in the
	 *         table by its bundle ID and revision 0.
	 * @throws JMException if there is a JMX problem.
	 * @throws IOException if the connection could not be made because of a
	 *         communication problem.
	 */
    TabularData getCurrentWiringClosure(long rootBundleId, String namespace)
            throws IOException, JMException;

    /**
	 * Returns the requirements for all revisions of the bundle.
	 * 
	 * @see #REVISIONS_REQUIREMENTS_TYPE for the details of TabularData.
	 * 
	 *      The requirements are in no particular order, and may change in
	 *      subsequent calls to this operation.
	 * 
	 * @param bundleId The bundle ID.
	 * @param namespace The namespace of the requirements to be returned by this
	 *        operation.
	 * @return the declared requirements for all revisions of
	 *         <code>bundleId</code>.
	 * @throws JMException if there is a JMX problem.
	 * @throws IOException if the connection could not be made because of a
	 *         communication problem.
	 */
    TabularData getRevisionsDeclaredRequirements(long bundleId, String namespace)
            throws IOException, JMException;


    /**
	 * Returns the capabilities for all revisions of the bundle.
	 * 
	 * @see #REVISIONS_CAPABILITIES_TYPE for the details of TabularData.
	 * 
	 *      The capabilities are in no particular order, and may change in
	 *      subsequent calls to this operation.
	 * 
	 * @param bundleId The bundle ID.
	 * @param namespace The namespace of the capabilities to be returned by this
	 *        operation.
	 * @return the declared capabilities for all revisions of
	 *         <code>bundleId</code>
	 * @throws JMException if there is a JMX problem.
	 * @throws IOException if the connection could not be made because of a
	 *         communication problem.
	 */
    TabularData getRevisionsDeclaredCapabilities(long bundleId, String namespace)
            throws IOException, JMException;

    /**
	 * Returns the bundle wirings for all revisions of the bundle.
	 * 
	 * @see #BUNDLES_WIRING_TYPE for the details of TabularData.
	 * 
	 *      The bundle wirings are in no particular order, and may change in
	 *      subsequent calls to this operations.
	 * 
	 * @param bundleId The bundle ID.
	 * @param namespace The namespace of the requirements and capabilities for
	 *        which to return information.
	 * @return the wiring information for all revisions of <code>bundleId</code>
	 *         and <code>namespace</code>.
	 * @throws JMException if there is a JMX problem.
	 * @throws IOException if the connection could not be made because of a
	 *         communication problem.
	 */
    TabularData getRevisionsWiring(long bundleId, String namespace)
            throws IOException, JMException;

    /**
	 * Returns the bundle wiring closure for all revisions of the specified
	 * bundle. The wiring closure contains all the wirings from the root bundle
	 * revision to all bundle revisions it is wired to and all their transitive
	 * wirings.
	 * 
	 * @see #BUNDLES_WIRING_TYPE for the details of TabularData.
	 * 
	 *      The bundle wirings are in no particular order, and may change in
	 *      subsequent calls to this operation. Furthermore, the bundle revision
	 *      IDs are local and cannot be reused across invocations.
	 * 
	 * @param rootBundleId The root bundle ID.
	 * @param namespace The namespace of the requirements and capabilities for
	 *        which to return information.
	 * @return a closure of bundle wirings linked together by wires.
	 * @return a tabular representation of all the wirings in the closures. The
	 *         bundle revision IDs only have meaning in the context of the
	 *         current result.
	 * @throws JMException if there is a JMX problem.
	 * @throws IOException if the connection could not be made because of a
	 *         communication problem.
	 */
    TabularData getRevisionsWiringClosure(long rootBundleId, String namespace)
            throws IOException, JMException;

}