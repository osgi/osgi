/*
 * Copyright (c) OSGi Alliance (2010, 2012). All Rights Reserved.
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
 * It can be used to retrieve the declared capabilities, declared requirements,
 * and wiring for the current and past revisions of bundles.
 * 
 * @ThreadSafe
 */
public interface BundleWiringStateMBean {
	/**
	 * The Object Name prefix for this mbean. The full object name also contains
	 * the framework name and uuid as properties.
	 */
	String			OBJECTNAME							= JmxConstants.OSGI_CORE + ":type=wiringState,version=1.1";

	/**
	 * The key of {@link #KEY_ITEM}.
	 */
	String			KEY									= "Key";

	/**
	 * The item containing the key of a capability or requirement directive.
	 * Used in {@link #DIRECTIVE_TYPE}. The key is {@link #KEY} and the type is
	 * a String.
	 */
	Item			KEY_ITEM							= new Item(KEY, "The directive key", SimpleType.STRING);

	/**
	 * The key of {@link #VALUE}.
	 */
	String			VALUE								= "Value";

	/**
	 * The item containing the value of a capability or requirement directive.
	 * Used in {@link #DIRECTIVE_TYPE}. They key is {@link #VALUE} and the type
	 * is a String.
	 */
	Item			VALUE_ITEM							= new Item(VALUE, "The directive value", SimpleType.STRING);

	/**
	 * The Composite Type that represents a directive for a capability or
	 * requirement. The composite consists of:
	 * <ul>
	 * <li>{@link #KEY}</li>
	 * <li>{@link #VALUE}</li>
	 * </ul>
	 */
	CompositeType	DIRECTIVE_TYPE						= Item.compositeType("DIRECTIVE", "Describes a directive of a capability or requirement", KEY_ITEM, VALUE_ITEM);

	/**
	 * The Tabular Type that holds the directives for a capability or
	 * requirement. The row type is {@link #DIRECTIVE_TYPE} and the index is
	 * {@link #KEY}.
	 */
	TabularType		DIRECTIVES_TYPE						= Item.tabularType("DIRECTIVES", "Describes the directives of a capability or requirement", DIRECTIVE_TYPE, KEY);

	/**
	 * The key of {@link #DIRECTIVES_ITEM}.
	 */
	String			DIRECTIVES							= "Directives";

	/**
	 * The item containing the directives of a capability or requirement. Used
	 * in {@link #BUNDLE_REQUIREMENT_TYPE} and {@link #BUNDLE_CAPABILITY_TYPE}.
	 * The key is {@link #DIRECTIVES} and the type is {@link #DIRECTIVES_TYPE}.
	 */
	Item			DIRECTIVES_ITEM						= new Item(DIRECTIVES, "The directives of a capability or requirement", DIRECTIVES_TYPE);

	/**
	 * The Tabular Type that holds the attributes for a capability or
	 * requirements. The row type is {@link JmxConstants#PROPERTY_TYPE} and the
	 * index is {@link JmxConstants#KEY}.
	 */
	TabularType		ATTRIBUTES_TYPE						= Item.tabularType("ATTRIBUTES", "Describes attributes of a capability or requirement", JmxConstants.PROPERTY_TYPE, JmxConstants.KEY);

	/**
	 * The key of {@link #ATTRIBUTES_ITEM}.
	 */
	String			ATTRIBUTES							= "Attributes";

	/**
	 * The item containing the attributes of a capability or requirement. Used
	 * in {@link #BUNDLE_REQUIREMENT_TYPE} and {@link #BUNDLE_CAPABILITY_TYPE}.
	 * The key is {@link #ATTRIBUTES} and the type is {@link #ATTRIBUTES_TYPE}.
	 */
	Item			ATTRIBUTES_ITEM						= new Item(ATTRIBUTES, "The attributes of a capability or requirement", ATTRIBUTES_TYPE);

	/**
	 * The key of {@link #NAMESPACE_ITEM}.
	 */
	String			NAMESPACE							= "Namespace";

	/**
	 * The item containing the namespace for a capability or requirement. Used
	 * in {@link #BUNDLE_REQUIREMENT_TYPE} and {@link #BUNDLE_CAPABILITY_TYPE}.
	 * The key is {@link #NAMESPACE} and the type is a String.
	 */
	Item			NAMESPACE_ITEM						= new Item(NAMESPACE, "The namespace of a capability or requirement", SimpleType.STRING);

	/**
	 * The Composite Type that represents the requirement of a bundle.
	 * 
	 * The composite consists of:
	 * <ul>
	 * <li>{@link #NAMESPACE}</li>
	 * <li>{@link #ATTRIBUTES}</li>
	 * <li>{@link #DIRECTIVES}</li>
	 * </ul>
	 */
	CompositeType	BUNDLE_REQUIREMENT_TYPE				= Item.compositeType("BUNDLE_REQUIREMENT", "Describes the requirement of a bundle", ATTRIBUTES_ITEM, DIRECTIVES_ITEM, NAMESPACE_ITEM);

	/**
	 * The Composite Type that represents the capability of a bundle.
	 * 
	 * The composite consists of:
	 * <ul>
	 * <li>{@link #NAMESPACE}</li>
	 * <li>{@link #ATTRIBUTES}</li>
	 * <li>{@link #DIRECTIVES}</li>
	 * </ul>
	 */
	CompositeType	BUNDLE_CAPABILITY_TYPE				= Item.compositeType("BUNDLE_CAPABILITY", "Describes the capability of a bundle", ATTRIBUTES_ITEM, DIRECTIVES_ITEM, NAMESPACE_ITEM);

	/**
	 * The key of {@link #PROVIDER_BUNDLE_ID_ITEM}.
	 */
	String			PROVIDER_BUNDLE_ID					= "ProviderBundleId";

	/**
	 * The item containing the provider bundle ID in {@link #BUNDLE_WIRE_TYPE}.
	 * The key is {@link #PROVIDER_BUNDLE_ID} and the type is a long.
	 */
	Item			PROVIDER_BUNDLE_ID_ITEM				= new Item(PROVIDER_BUNDLE_ID, "The identifier of the bundle that is the provider of the capability", SimpleType.LONG);

	/**
	 * The key of {@link #REQUIRER_BUNDLE_ID_ITEM}.
	 */
	String			REQUIRER_BUNDLE_ID					= "RequirerBundleId";

	/**
	 * The item containing the requirer bundle ID in {@link #BUNDLE_WIRE_TYPE}.
	 * The key is {@link #REQUIRER_BUNDLE_ID} and the type is long.
	 */
	Item			REQUIRER_BUNDLE_ID_ITEM				= new Item(REQUIRER_BUNDLE_ID, "The identifier of the bundle that is the requirer of the requirement", SimpleType.LONG);

	/**
	 * The key of {@link #BUNDLE_REQUIREMENT_ITEM}.
	 */
	String			BUNDLE_REQUIREMENT					= "BundleRequirement";

	/**
	 * The item containing a requirement for a bundle in
	 * {@link #BUNDLE_WIRE_TYPE}. The key is {@link #BUNDLE_REQUIREMENT} and the
	 * type is {@link #BUNDLE_REQUIREMENT_TYPE}.
	 */
	Item			BUNDLE_REQUIREMENT_ITEM				= new Item(BUNDLE_REQUIREMENT, "The wired requirements of a bundle", BUNDLE_REQUIREMENT_TYPE);

	/**
	 * The key of {@link #BUNDLE_CAPABILITY_ITEM}.
	 */
	String			BUNDLE_CAPABILITY					= "BundleCapability";

	/**
	 * The item containing a capability for a bundle in
	 * {@link #BUNDLE_WIRE_TYPE}. The key is {@link #BUNDLE_CAPABILITY} and the
	 * type is {@link #BUNDLE_CAPABILITY_TYPE}.
	 */
	Item			BUNDLE_CAPABILITY_ITEM				= new Item(BUNDLE_CAPABILITY, "The wired capabilities of a bundle", BUNDLE_CAPABILITY_TYPE);

	/**
	 * The key of {@link #PROVIDER_BUNDLE_REVISION_ID_ITEM}.
	 */
	String			PROVIDER_BUNDLE_REVISION_ID			= "ProviderBundleRevisionId";

	/**
	 * The local ID of a provider revision in {@link #BUNDLE_WIRE_TYPE}. This ID
	 * is local to the result where it resides and has no defined meaning across
	 * multiple invocations. The key is {@link #PROVIDER_BUNDLE_REVISION_ID} and
	 * the type is an int.
	 */
	Item			PROVIDER_BUNDLE_REVISION_ID_ITEM	= new Item(PROVIDER_BUNDLE_REVISION_ID, "A local id for the bundle revision that is the provider of the capability", SimpleType.INTEGER);

	/**
	 * The key of {@link #REQUIRER_BUNDLE_REVISION_ID_ITEM}.
	 */
	String			REQUIRER_BUNDLE_REVISION_ID			= "RequirerBundleRevisionId";

	/**
	 * The local ID of a requirer revision in {@link #BUNDLE_WIRE_TYPE}. This ID
	 * is local to the result where it resides and has no defined meaning across
	 * multiple invocations. The key is {@link #REQUIRER_BUNDLE_REVISION_ID} and
	 * the type is an int.
	 */
	Item			REQUIRER_BUNDLE_REVISION_ID_ITEM	= new Item(REQUIRER_BUNDLE_REVISION_ID, "A local id for the bundle revision that is the requirer of the requirement", SimpleType.INTEGER);

	/**
	 * The Composite type that represents a bundle wire describing the live
	 * association between a provider of a capability and a requirer of the
	 * corresponding requirement.
	 * <p/>
	 * The composite consists of:
	 * <ul>
	 * <li>{@link #BUNDLE_REQUIREMENT}</li>
	 * <li>{@link #BUNDLE_CAPABILITY}</li>
	 * <li>{@link #PROVIDER_BUNDLE_ID}</li>
	 * <li>{@link #PROVIDER_BUNDLE_REVISION_ID}</li>
	 * <li>{@link #REQUIRER_BUNDLE_ID}</li>
	 * <li>{@link #REQUIRER_BUNDLE_REVISION_ID}</li>
	 * </ul>
	 */
	CompositeType	BUNDLE_WIRE_TYPE					= Item.compositeType("BUNDLE_WIRE",
																"Describes the live association between a provider and a requirer",
																BUNDLE_REQUIREMENT_ITEM,
																BUNDLE_CAPABILITY_ITEM,
																PROVIDER_BUNDLE_ID_ITEM,
																PROVIDER_BUNDLE_REVISION_ID_ITEM,
																REQUIRER_BUNDLE_ID_ITEM,
																REQUIRER_BUNDLE_REVISION_ID_ITEM);

	/**
	 * An array of {@link #BUNDLE_WIRE_TYPE}s.
	 */
	ArrayType		BUNDLE_WIRES_TYPE_ARRAY				= Item.arrayType(1, BUNDLE_WIRE_TYPE);

	/**
	 * The key of {@link #BUNDLE_REVISION_ID_ITEM}.
	 */
	String			BUNDLE_REVISION_ID					= "BundleRevisionId";

	/**
	 * The item containing a bundle revision ID. A bundle revision ID is always
	 * local to the result of a JMX invocation and do not have a defined meaning
	 * across invocation calls. They are used where a result can potentially
	 * contain multiple revisions of the same bundle. The key is
	 * {@link #BUNDLE_REVISION_ID} and the type is an integer.
	 */
	Item			BUNDLE_REVISION_ID_ITEM				= new Item(BUNDLE_REVISION_ID, "The local identifier of the bundle revision", SimpleType.INTEGER);

	/**
	 * The key of {@link #BUNDLE_ID_ITEM}.
	 */
	String			BUNDLE_ID							= "BundleId";

	/**
	 * The item containing a bundle ID. They key is {@link #BUNDLE_ID} and the
	 * type is a long.
	 */
	Item			BUNDLE_ID_ITEM						= new Item(BUNDLE_ID, "The bundle identifier of the bundle revision", SimpleType.LONG);

	/**
	 * An array of {@link #BUNDLE_REQUIREMENT_TYPE}s.
	 */
	ArrayType		REQUIREMENT_TYPE_ARRAY				= Item.arrayType(1, BUNDLE_REQUIREMENT_TYPE);

	/**
	 * An array of {@link #BUNDLE_CAPABILITY_TYPE}s.
	 */
	ArrayType		CAPABILITY_TYPE_ARRAY				= Item.arrayType(1, BUNDLE_CAPABILITY_TYPE);

	/**
	 * The key of {@link #REQUIREMENTS_ITEM}.
	 */
	String			REQUIREMENTS						= "Requirements";

	/**
	 * The item containing the requirements in
	 * {@link #REVISION_REQUIREMENTS_TYPE} and {@link #BUNDLE_WIRING_TYPE}. The
	 * key is {@link #REQUIREMENTS} and the type is
	 * {@link #REQUIREMENT_TYPE_ARRAY}.
	 */
	Item			REQUIREMENTS_ITEM					= new Item(REQUIREMENTS, "The bundle requirements of a bundle revision wiring", REQUIREMENT_TYPE_ARRAY);

	/**
	 * The Composite Type that represents the requirements of a revision. The
	 * composite consists of:
	 * <ul>
	 * <li>{@link #BUNDLE_REVISION_ID}</li>
	 * <li>{@link #REQUIREMENTS}</li>
	 * </ul>
	 */
	CompositeType	REVISION_REQUIREMENTS_TYPE			= Item.compositeType("REVISION_REQUIREMENTS", "Describes the requirements for a bundle revision", BUNDLE_REVISION_ID_ITEM, REQUIREMENTS_ITEM);

	/**
	 * The Tabular Type that hold the requirements of a revision. The row type
	 * is {@link #REVISION_REQUIREMENTS_TYPE} and the index is
	 * {@link #BUNDLE_REVISION_ID}.
	 */
	TabularType		REVISIONS_REQUIREMENTS_TYPE			= Item.tabularType("REVISIONS_REQUIREMENTS", "The bundle requirements for all bundle revisions", REVISION_REQUIREMENTS_TYPE, BUNDLE_REVISION_ID);

	/**
	 * The key of {@link #CAPABILITIES_ITEM}.
	 */
	String			CAPABILITIES						= "Capabilities";

	/**
	 * The item containing the capabilities in
	 * {@link #REVISION_CAPABILITIES_TYPE} and {@link #BUNDLE_WIRING_TYPE}. The
	 * key is {@link #CAPABILITIES} and the type is
	 * {@link #CAPABILITY_TYPE_ARRAY}.
	 */
	Item			CAPABILITIES_ITEM					= new Item(CAPABILITIES, "The bundle capabilities of a bundle revision wiring", CAPABILITY_TYPE_ARRAY);

	/**
	 * The Composite Type that represents the capabilities for a revision. The
	 * composite consists of:
	 * <ul>
	 * <li>{@link #BUNDLE_REVISION_ID}</li>
	 * <li>{@link #CAPABILITIES}</li>
	 * </ul>
	 */
	CompositeType	REVISION_CAPABILITIES_TYPE			= Item.compositeType("REVISION_CAPABILITIES", "Describes the capabilities for a bundle revision", BUNDLE_REVISION_ID_ITEM, CAPABILITIES_ITEM);

	/**
	 * The Tabular Type that holds the capabilities of a revision. The row type
	 * is {@link #REVISION_CAPABILITIES_TYPE} and the index is
	 * {@link #BUNDLE_REVISION_ID}.
	 */
	TabularType		REVISIONS_CAPABILITIES_TYPE			= Item.tabularType("REVISIONS_CAPABILITIES", "The bundle capabilities for all bundle revisions", REVISION_CAPABILITIES_TYPE, BUNDLE_REVISION_ID);

	/**
	 * The key of {@link #PROVIDED_WIRES_ITEM}.
	 */
	String			PROVIDED_WIRES						= "ProvidedWires";

	/**
	 * The item containing the provided wires in {@link #BUNDLE_WIRING_TYPE}.
	 * The key is {@link #PROVIDED_WIRES} and the type is
	 * {@link #BUNDLE_WIRES_TYPE_ARRAY}.
	 */
	Item			PROVIDED_WIRES_ITEM					= new Item(PROVIDED_WIRES, "The bundle wires to the capabilities provided by this bundle wiring.", BUNDLE_WIRES_TYPE_ARRAY);

	/**
	 * The key of {@link #REQUIRED_WIRES_ITEM}.
	 */
	String			REQUIRED_WIRES						= "RequiredWires";

	/**
	 * The item containing the required wires in {@link #BUNDLE_WIRING_TYPE}.
	 * The key is {@link #REQUIRED_WIRES} and the type is
	 * {@link #BUNDLE_WIRES_TYPE_ARRAY}.
	 */
	Item			REQUIRED_WIRES_ITEM					= new Item(REQUIRED_WIRES, "The bundle wires to requirements in use by this bundle wiring", BUNDLE_WIRES_TYPE_ARRAY);

	/**
	 * The Composite Type that represents a bundle wiring. The composite
	 * consists of:
	 * <ul>
	 * <li>{@link #BUNDLE_ID}</li>
	 * <li>{@link #BUNDLE_REVISION_ID}</li>
	 * <li>{@link #REQUIREMENTS}</li>
	 * <li>{@link #CAPABILITIES}</li>
	 * <li>{@link #REQUIRED_WIRES}</li>
	 * <li>{@link #PROVIDED_WIRES}</li>
	 * </ul>
	 */
	CompositeType	BUNDLE_WIRING_TYPE					= Item.compositeType("BUNDLE_WIRING",
																"Describes the runtime association between a provider and a requirer",
																BUNDLE_ID_ITEM,
																BUNDLE_REVISION_ID_ITEM,
																REQUIREMENTS_ITEM,
																CAPABILITIES_ITEM,
																REQUIRED_WIRES_ITEM,
																PROVIDED_WIRES_ITEM);

	/**
	 * The Tabular Type to hold the wiring of a number of bundles. The row type
	 * is {@link #BUNDLE_WIRING_TYPE} and the index is the combination of the
	 * {@link #BUNDLE_ID} and the {@link #BUNDLE_REVISION_ID}.
	 */
	TabularType		BUNDLES_WIRING_TYPE					= Item.tabularType("BUNDLES_WIRING", "The bundle wiring for all bundle revisions", BUNDLE_WIRING_TYPE, BUNDLE_ID, BUNDLE_REVISION_ID);

	/**
	 * Returns the requirements for the current bundle revision.
	 * 
	 * @see #REQUIREMENT_TYPE_ARRAY for the details of the CompositeData.
	 * 
	 * @param bundleId The bundle ID.
	 * @param namespace The namespace of the requirements to be returned by this
	 *        operation.
	 * @return the declared requirements for the current revision of
	 *         {@code bundleId} and {@code namespace}.
	 * @throws JMException if there is a JMX problem.
	 * @throws IOException if the connection could not be made because of a
	 *         communication problem.
	 */
	CompositeData[] getCurrentRevisionDeclaredRequirements(long bundleId, String namespace) throws IOException, JMException;

	/**
	 * Returns the capabilities for the current bundle revision.
	 * 
	 * @see #CAPABILITY_TYPE_ARRAY for the details of the CompositeData.
	 * 
	 * @param bundleId The bundle ID.
	 * @param namespace The namespace of the capabilities to be returned by this
	 *        operation.
	 * @return the declared capabilities for the current revision of
	 *         {@code bundleId} and {@code namespace}.
	 * @throws JMException if there is a JMX problem.
	 * @throws IOException if the connection could not be made because of a
	 *         communication problem.
	 */
	CompositeData[] getCurrentRevisionDeclaredCapabilities(long bundleId, String namespace) throws IOException, JMException;

	/**
	 * Returns the bundle wiring for the current bundle revision.
	 * 
	 * @see #BUNDLE_WIRING_TYPE for the details of the CompositeData.
	 * 
	 * @param bundleId The bundle ID.
	 * @param namespace The namespace of the requirements and capabilities for
	 *        which to return information.
	 * @return the wiring information for the current revision of
	 *         {@code bundleId} and {@code namespace}.
	 * @throws JMException if there is a JMX problem.
	 * @throws IOException if the connection could not be made because of a
	 *         communication problem.
	 */
	CompositeData getCurrentWiring(long bundleId, String namespace) throws IOException, JMException;

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
	TabularData getCurrentWiringClosure(long rootBundleId, String namespace) throws IOException, JMException;

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
	 * @return the declared requirements for all revisions of {@code bundleId}.
	 * @throws JMException if there is a JMX problem.
	 * @throws IOException if the connection could not be made because of a
	 *         communication problem.
	 */
	TabularData getRevisionsDeclaredRequirements(long bundleId, String namespace) throws IOException, JMException;

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
	 * @return the declared capabilities for all revisions of {@code bundleId}
	 * @throws JMException if there is a JMX problem.
	 * @throws IOException if the connection could not be made because of a
	 *         communication problem.
	 */
	TabularData getRevisionsDeclaredCapabilities(long bundleId, String namespace) throws IOException, JMException;

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
	 * @return the wiring information for all revisions of {@code bundleId} and
	 *         {@code namespace}.
	 * @throws JMException if there is a JMX problem.
	 * @throws IOException if the connection could not be made because of a
	 *         communication problem.
	 */
	TabularData getRevisionsWiring(long bundleId, String namespace) throws IOException, JMException;

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
	 * @return a tabular representation of all the wirings in the closure. The
	 *         bundle revision IDs only have meaning in the context of the
	 *         current result.
	 * @throws JMException if there is a JMX problem.
	 * @throws IOException if the connection could not be made because of a
	 *         communication problem.
	 */
	TabularData getRevisionsWiringClosure(long rootBundleId, String namespace) throws IOException, JMException;

}
