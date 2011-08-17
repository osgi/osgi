/*
 * Copyright (c) OSGi Alliance (2011). All Rights Reserved.
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

package org.osgi.service.tr069todmt;

import java.util.*;

/**
 * A TR-069 Connector is an assistant to a TR-069 Protocol Adapter developer. The
 * connector manages the low level details of converting the different TR-069 RPCs
 * to a Device Management Tree managed by Dmt Admin. The connector manages the
 * conversions from the TR-069 Object Names to a node in the DMT and vice versa.
 * <p>
 * The connector uses a Dmt Session from the caller, which is given when the
 * connector is created. The connector does not implement the exact RPCs but only
 * provides the basic functions to set, get, add, delete, and get the
 * (parameter) names of an object. A TR-069 developer must still parse the XML,
 * handle the relative and absolute path issues, open a Dmt Session etc.
 * <p>
 * The connector assumes that each parameter or object name is relative to this
 * root.
 * <p>
 * This connector must convert the TR-069 Names to Dmt Admin URIs. This conversion
 * must take into account the {@code LIST} and {@code MAP} concepts defined in
 * the specifications. These concepts define the use of an {@code InstanceId}
 * node that must be used by the connector to provide a TR-069 table view on the
 * {@code LIST} and {@code MAP} nodes.
 * 
 * @remark Move sessions into the object (create a factory)
 * @remark Use absolute paths instead of relative paths
 */
public interface TR069Connector {

	/**
	 * The MIME type prefix.
	 */
	final static String PREFIX = "application/x-osgi.dmt.tr-069-";

	/**
	 * Constant representing the default or unknown type. If this type is used a
	 * default conversion will take place
	 */
	public final static String TR069_MIME_DEFAULT = PREFIX + "default";

	/**
	 * Constant representing the TR-069 integer type.
	 */
	public final static String TR069_MIME_INT = PREFIX + "int";

	/**
	 * Constant representing the TR-069 unsigned integer type.
	 */
	public final static String TR069_MIME_UNSIGNED_INT = PREFIX + "unsignedInt";

	/**
	 * Constant representing the TR-069 long type.
	 */
	public final static String TR069_MIME_LONG = PREFIX + "long";

	/**
	 * Constant representing the TR-069 unsigned long type.
	 */
	public final static String TR069_MIME_UNSIGNED_LONG = "unsignedLong";

	/**
	 * Constant representing the TR-069 string type.
	 */
	public final static String TR069_MIME_STRING = "string";

	/**
	 * Constant representing the TR-069 boolean type.
	 */
	public final static String TR069_MIME_BOOLEAN = "string";

	/**
	 * Constant representing the TR-069 base64 type.
	 */
	public final static String TR069_MIME_BASE64 = "base64";

	/**
	 * Constant representing the TR-069 hex binary type.
	 */
	public final static String TR069_MIME_HEXBINARY = PREFIX + "hexBinary";

	/**
	 * Constant representing the TR-069 date time type.
	 */
	public final static String TR069_MIME_DATETIME = PREFIX + "dateTime";

	/**
	 * Constant representing the default or unknown type. If this type is used a
	 * default conversion will take place
	 */
	public final static int TR069_DEFAULT = 0;

	/**
	 * Constant representing the TR-069 integer type.
	 */
	public final static int TR069_INT = 1;

	/**
	 * Constant representing the TR-069 unsigned integer type.
	 */
	public final static int TR069_UNSIGNED_INT = 2;

	/**
	 * Constant representing the TR-069 long type.
	 */
	public final static int TR069_LONG = 4;

	/**
	 * Constant representing the TR-069 unsigned long type.
	 */
	public final static int TR069_UNSIGNED_LONG = 8;

	/**
	 * Constant representing the TR-069 string type.
	 */
	public final static int TR069_STRING = 16;

	/**
	 * Constant representing the TR-069 boolean type.
	 */
	public final static int TR069_BOOLEAN = 32;

	/**
	 * Constant representing the TR-069 base64 type.
	 */
	public final static int TR069_BASE64 = 64;

	/**
	 * Constant representing the TR-069 hex binary type.
	 */
	public final static int TR069_HEXBINARY = 128;

	/**
	 * Constant representing the TR-069 date time type.
	 */
	public final static int TR069_DATETIME = 256;

	/**
	 * Setting a parameter. This method should be used to provide the
	 * SetParameterValues RPC. This method must convert the parameter Name to a
	 * URI and replace the DMT node at that place. It must follow the type
	 * conversions as described in the specification.
	 * <p>
	 * The connector must attempt to create any missing nodes along the way, creating
	 * parent nodes on demand.
	 * <p>
	 * If the value of a an Alias node is set then the parent node must be
	 * renamed. For example, if the value of {@code M/X/Alias} is set to
	 * {@code Y} then the node will have a URI of {@code M/Y/Alias}. The value
	 * must not be escaped as the connector will escape it.
	 * 
	 * @param fullParameterName
	 *            A parameter name, must not be partial (end in dot).
	 * @param value
	 *            A trimmed string value that has the given type. The value can
	 *            be in either canonical or lexical representation by TR069.
	 * 
	 * @param type
	 *            The type of the parameter ({@link #TR069_INT},
	 *            {@link #TR069_UNSIGNED_INT},{@link #TR069_LONG},
	 *            {@link #TR069_UNSIGNED_LONG},{@link #TR069_STRING},
	 *            {@link #TR069_DATETIME},{@link #TR069_BASE64},
	 *            {@link #TR069_HEXBINARY})
	 * @throws TR069Exception
	 *             if the value cannot be set
	 */
	void setParameterValue(String fullParameterName, String value, int type)
			throws TR069Exception;

	/**
	 * Getting a Parameter. This method should be used to implement
	 * GetParameterValues RPC. This method must convert the Name to the
	 * appropriate node and convert the value to a TR-069 type as specified in
	 * the specification.
	 * <p>
	 * If the ACS requests the values for partial names then it is the
	 * responsibility of the caller to expand the node with GetParameterNames
	 * and then call this method for each parameter.
	 * <p>
	 * 
	 * <p>
	 * If the value of a the {@code Alias} node is requested then the name of
	 * the parent node must be returned. For example, if the URI is
	 * {@code M/X/Alias} then the returned value must be {@code X}.
	 * 
	 * @param fullParameterName
	 *            A parameter name, must not be partial (end in dot).
	 * @return The name, value, and type triad of the requested parameter as
	 *         defined by the TR-069 {@code ParameterValueStruct}.
	 * @throws TR069Exception
	 */
	ParameterValue getParameterValue(String fullParameterName)
			throws TR069Exception;

	/**
	 * Getting the names of the parameters for the node addressed by path. This
	 * method should be used to implement the GetParameterNames RPC. It must
	 * return the children of the addressed node.
	 * <p>
	 * If the child nodes have an InstanceId node then the returned names must
	 * be the set of InstanceId values.
	 * <p>
	 * If the parent node is a MAP, then the returned names must include the
	 * synthetic name {@code Alias}, this node can be used to get the real name
	 * of the node as well as rename the node.
	 * <p>
	 * Partial paths and the nextLevel boolean must be handled by the caller.
	 * 
	 * @param fullParameterName
	 *            A parameter name, must not be partial and it must end with full stop.
	 * @return The full name of the parameter and the write status as defined by the
	 *         TR-069 {@code ParameterInfoStruct}.
	 * @throws TR069Exception
	 */
	Collection getParameterNames(String fullParameterName)
			throws TR069Exception;

	/**
	 * Add a new node to the Dmt Admin as defined by the AddObject RPC.
	 * 
	 * The objectName must map to either a LIST or MAP node as no other nodes
	 * can accept new children. The connector must calculate a unique id for the
	 * new node name that follows the TR-069 names for instance ids. If the new
	 * node has an InstanceId node, then this name must be returned, otherwise
	 * the calculated name must be returned.
	 * 
	 * @param objectName
	 *            The path name of the collection of objects for which a new
	 *            instance is to be created. The path name MUST end with a “.”
	 *            (full stop) after the last node in the hierarchical name of
	 *            the object.
	 * @return The name of the new node, either the new nodes InstanceId node's
	 *         value or the node name.
	 * @throws TR069Exception
	 */
	String addObject(String objectName) throws TR069Exception;

	/**
	 * Delete an object.
	 * 
	 * @param objectName
	 *            The path name of the object to be deleted. The path name MUST
	 *            end with a “.” (dot) after the last node in the hierarchical
	 *            name of the object.
	 * @throws TR069Exception
	 */
	void deleteObject(String objectName) throws TR069Exception;

	/**
	 * Convert a relative Dmt Admin URI to a TR-069 Name (Parameter or Object)
	 * that can be used to address the node.
	 * 
	 * ### RELATIVE TO THE SESSION ROOT
	 * 
	 * @param uri
	 *            A relative URI from the start of the session
	 * @return A relative path
	 * @throws TR069Exception
	 * 
	 * @remark Needed without session?
	 */
	String toName(String uri) throws TR069Exception;

	/**
	 * Convert an absolute TR-069 complete Name (Object or Name) to a relative
	 * Dmt Admin URI. The conversion must traverse the tree and can only convert
	 * a name that has nodes present in the DMT. The given name must be escaped
	 * using the TR-069 escaping and will be unescaped. The returned URI is
	 * properly escaped for Dmt Admin. The conversion must include the use of
	 * the {@code InstanceId} nodes to address MAPs and LISTs.
	 * 
	 * @param name
	 *            A TR-069 Object or Parameter Name
	 * @param create If true, create missing nodes when they reside under a MAP
	 * @return A relative Dmt Admin URI
	 * @throws TR069Exception
	 */
	String toURI(String name, boolean create) throws TR069Exception;

	/**
	 * Close this connector. This will not close the corresponding session.
	 */
	void close();

}
