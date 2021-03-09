/*******************************************************************************
 * Copyright (c) Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0 
 *******************************************************************************/

package org.osgi.service.tr069todmt;

import java.util.Collection;

/**
 * A TR-069 Connector is an assistant to a TR-069 Protocol Adapter developer.
 * The connector manages the low level details of converting the different
 * TR-069 RPCs to a Device Management Tree managed by Dmt Admin. The connector
 * manages the conversions from the TR-069 Object Names to a node in the DMT and
 * vice versa.
 * <p>
 * The connector uses a Dmt Session from the caller, which is given when the
 * connector is created. The connector does not implement the exact RPCs but
 * only provides the basic functions to set and get the parameters of an object
 * as well as adding and deleting an object in a table. A TR-069 developer must
 * still parse the XML, handle the relative and absolute path issues, open a Dmt
 * Session etc.
 * <p>
 * The connector assumes that each parameter or object path is relative to the
 * root of the Dmt Session.
 * <p>
 * This connector must convert the TR-069 paths to Dmt Admin URIs. This
 * conversion must take into account the {@code LIST} and {@code MAP} concepts
 * defined in the specifications as well as the synthetic parameters
 * {@code NumberOfEntries} and {@code Alias}. These concepts define the use of
 * an {@code InstanceId} node that must be used by the connector to provide a
 * TR-069 table view on the {@code LIST} and {@code MAP} nodes.
 */
public interface TR069Connector {

	/**
	 * The MIME type prefix.
	 */
	final static String			PREFIX						= "application/x-tr-069-";

	/**
	 * Constant representing the default or unknown type. If this type is used a
	 * default conversion will take place
	 */
	public final static String	TR069_MIME_DEFAULT			= PREFIX + "default";

	/**
	 * Constant representing the TR-069 integer type.
	 */
	public final static String	TR069_MIME_INT				= PREFIX + "int";

	/**
	 * Constant representing the TR-069 unsigned integer type.
	 */
	public final static String	TR069_MIME_UNSIGNED_INT		= PREFIX + "unsignedInt";

	/**
	 * Constant representing the TR-069 long type.
	 */
	public final static String	TR069_MIME_LONG				= PREFIX + "long";

	/**
	 * Constant representing the TR-069 unsigned long type.
	 */
	public final static String	TR069_MIME_UNSIGNED_LONG	= PREFIX + "unsignedLong";

	/**
	 * Constant representing the TR-069 string type.
	 */
	public final static String	TR069_MIME_STRING			= PREFIX + "string";

	/**
	 * Constant representing the TR-069 string list type.
	 */
	public final static String	TR069_MIME_STRING_LIST		= PREFIX + "string-list";

	/**
	 * Constant representing the TR-069 boolean type.
	 */
	public final static String	TR069_MIME_BOOLEAN			= PREFIX + "boolean";

	/**
	 * Constant representing the TR-069 base64 type.
	 */
	public final static String	TR069_MIME_BASE64			= PREFIX + "base64";

	/**
	 * Constant representing the TR-069 hex binary type.
	 */
	public final static String	TR069_MIME_HEXBINARY		= PREFIX + "hexBinary";

	/**
	 * Constant representing the TR-069 date time type.
	 */
	public final static String	TR069_MIME_DATETIME			= PREFIX + "dateTime";

	/**
	 * Constant representing the TR-069 eager type.
	 */
	public final static String	TR069_MIME_EAGER			= PREFIX + "eager";

	/**
	 * Constant representing the default or unknown type. If this type is used a
	 * default conversion will take place
	 */
	public final static int		TR069_DEFAULT				= 0;

	/**
	 * Constant representing the TR-069 integer type.
	 */
	public final static int		TR069_INT					= 1;

	/**
	 * Constant representing the TR-069 unsigned integer type.
	 */
	public final static int		TR069_UNSIGNED_INT			= 2;

	/**
	 * Constant representing the TR-069 long type.
	 */
	public final static int		TR069_LONG					= 4;

	/**
	 * Constant representing the TR-069 unsigned long type.
	 */
	public final static int		TR069_UNSIGNED_LONG			= 8;

	/**
	 * Constant representing the TR-069 string type.
	 */
	public final static int		TR069_STRING				= 16;

	/**
	 * Constant representing the TR-069 boolean type.
	 */
	public final static int		TR069_BOOLEAN				= 32;

	/**
	 * Constant representing the TR-069 base64 type.
	 */
	public final static int		TR069_BASE64				= 64;

	/**
	 * Constant representing the TR-069 hex binary type.
	 */
	public final static int		TR069_HEXBINARY				= 128;

	/**
	 * Constant representing the TR-069 date time type.
	 */
	public final static int		TR069_DATETIME				= 256;

	/**
	 * Setting a parameter. This method should be used to provide the
	 * SetParameterValues RPC. This method must convert the parameter Name to a
	 * URI and replace the DMT node at that place. It must follow the type
	 * conversions as described in the specification.
	 * <p>
	 * The connector must attempt to create any missing nodes along the way,
	 * creating parent nodes on demand.
	 * <p>
	 * If the value of a an Alias node is set then the parent node must be
	 * renamed. For example, if the value of {@code M/X/Alias} is set to
	 * {@code Y} then the node will have a URI of {@code M/Y/Alias}. The value
	 * must not be escaped as the connector will escape it.
	 * 
	 * @param parameterPath The parameter path
	 * @param value A trimmed string value that has the given type. The value
	 *        can be in either canonical or lexical representation by TR069.
	 * 
	 * @param type The type of the parameter ({@link #TR069_INT},
	 *        {@link #TR069_UNSIGNED_INT},{@link #TR069_LONG},
	 *        {@link #TR069_UNSIGNED_LONG},{@link #TR069_STRING},
	 *        {@link #TR069_DATETIME},{@link #TR069_BASE64},
	 *        {@link #TR069_HEXBINARY}, {@link #TR069_BOOLEAN})
	 * 
	 * @throws TR069Exception The following fault codes are defined for this
	 *         method: 9001, 9002, 9003, 9004, 9005, 9006, 9007, 9008.
	 *         <ul>
	 *         <li> 9001 {@link TR069Exception#REQUEST_DENIED} </li> <li> 9002
	 *         {@link TR069Exception#INTERNAL_ERROR} </li><li> 9003
	 *         {@link TR069Exception#INVALID_ARGUMENTS} </li><li> 9004
	 *         {@link TR069Exception#RESOURCES_EXCEEDED} </li><li> 9005
	 *         {@link TR069Exception#INVALID_PARAMETER_NAME} </li><li> 9006
	 *         {@link TR069Exception#INVALID_PARAMETER_TYPE} </li><li> 9007
	 *         {@link TR069Exception#INVALID_PARAMETER_VALUE} </li><li> 9008
	 *         {@link TR069Exception#NON_WRITABLE_PARAMETER}</li>
	 *         </ul>
	 */
	void setParameterValue(String parameterPath, String value, int type) throws TR069Exception;

	/**
	 * Getting a parameter value. This method should be used to implement the
	 * GetParameterValues RPC. This method does <b>not</b> handle retrieving
	 * multiple values as the corresponding RPC can request with an object or
	 * table path, this method only accepts a parameter path. Retrieving
	 * multiple values can be achieved with the
	 * {@link #getParameterNames(String, boolean)}.
	 * <p>
	 * If the {@code parameterPath} ends in {@code NumberOfEntries} then the
	 * method must synthesize the value. The {@code parameterPath} then has a
	 * pattern like {@code (object-path)(table-name)NumberOfEntries}. The
	 * returned value must be an {@link #TR069_UNSIGNED_INT} that contains the
	 * number of child nodes in the table {@code (object-path)(table-name)}. For
	 * example, if {@code A.B.CNumberOfEntries} is requested the return value
	 * must be the number of child nodes under {@code A/B/C}.
	 * <p>
	 * If the value of a an {@code Alias} node is requested then the name of the
	 * parent node must be returned. For example, if the path is
	 * {@code M.X.Alias} then the returned value must be {@code X}.
	 * <p>
	 * The connector must attempt to create any missing nodes along the way,
	 * creating parent nodes on demand.
	 * 
	 * @param parameterPath A parameter path (must refer to a valid parameter,
	 *        not an object or table).
	 * @return The name, value, and type triad of the requested parameter as
	 *         defined by the TR-069 {@code ParameterValueStruct}.
	 * @throws TR069Exception The following fault codes are defined for this
	 *         method: 9001, 9002, 9003, 9004, 9005.
	 *         <ul>
	 *         <li> 9001 {@link TR069Exception#REQUEST_DENIED}</li><li> 9002
	 *         {@link TR069Exception#INTERNAL_ERROR}</li><li> 9003
	 *         {@link TR069Exception#INVALID_ARGUMENTS}</li><li> 9004
	 *         {@link TR069Exception#RESOURCES_EXCEEDED}</li><li> 9005
	 *         {@link TR069Exception#INVALID_PARAMETER_NAME}</li>
	 *         </ul>
	 */
	ParameterValue getParameterValue(String parameterPath) throws TR069Exception;

	/**
	 * Getting the {@link ParameterInfo} objects addressed by path. This method
	 * is intended to be used to implement the GetParameterNames RPC.
	 * <p>
	 * The connector must attempt to create any missing nodes that are needed
	 * for the {@code objectOrTablePath} by using the
	 * {@link #toURI(String, boolean)} method with {@code true}.
	 * <p>
	 * This method must traverse the sub-tree addressed by the path and return
	 * the paths to all the objects, tables, and parameters in that tree. If the
	 * nextLevel argument is {@code true} then only the children object, table,
	 * and parameter information must be returned.
	 * <p>
	 * The returned {@link ParameterInfo} objects must be usable to discover the
	 * sub-tree.
	 * <p>
	 * If the child nodes have an {@code InstanceId} node then the returned
	 * names must include the {@code InstanceId} values instead of the node
	 * names.
	 * <p>
	 * If the parent node is a {@code MAP}, then the synthetic {@code Alias}
	 * parameter must be included.
	 * <p>
	 * Any {@code MAP} and {@code LIST} node must include a
	 * {@link ParameterInfo} for the corresponding {@code NumberOfEntries}
	 * parameter.
	 * 
	 * @param objectOrTablePath A path to an object or table.
	 * @param nextLevel If {@code true} consider only the children of the object
	 *        or table addressed by {@code path}, otherwise include the whole
	 *        sub-tree, including the addressed object or table.
	 * @return A collection of {@link ParameterInfo} objects representing the
	 *         resulting child parameter, objects, and tables as defined by the
	 *         TR-069 {@code ParameterInfoStruct}.
	 * @throws TR069Exception If the fault is caused by an invalid ParameterPath
	 *         value, the Invalid Parameter Name fault code (9005) MUST be used
	 *         instead of the more general Invalid Arguments fault code (9003).
	 *         A ParameterPath value must be considered invalid if it is not an
	 *         empty string and does not exactly match a parameter or object
	 *         name currently present in the data model. If {@code nextLevel} is
	 *         {@code true} and {@code objectOrTablePath} is a parameter path
	 *         rather than an object/table path, the method must return a fault
	 *         response with the Invalid Arguments fault code (9003). If the
	 *         value cannot be gotten for some reason, this method can generate
	 *         the following fault codes::
	 *         <ul>
	 *         <li> 9001 {@link TR069Exception#REQUEST_DENIED} </li> <li> 9002
	 *         {@link TR069Exception#INTERNAL_ERROR} </li> <li> 9003
	 *         {@link TR069Exception#INVALID_ARGUMENTS} </li> <li> 9005
	 *         {@link TR069Exception#INVALID_PARAMETER_NAME}</li>
	 *         </ul>
	 */
	Collection<ParameterInfo> getParameterNames(String objectOrTablePath, boolean nextLevel) throws TR069Exception;

	/**
	 * Add a new node to the Dmt Admin as defined by the AddObject RPC.
	 * 
	 * The path must map to either a {@code LIST} or {@code MAP} node as no
	 * other nodes can accept new children.
	 * <p>
	 * If the path ends in an alias ({@code [ ALIAS ]}) then the node name must
	 * be the alias, however, no new node must be created. Otherwise, the
	 * Connector must calculate a unique instance id for the new node name that
	 * follows the TR-069 rules for instance ids. That is, this id must not be
	 * reused and must not be in use. That is, the id must be reserved
	 * persistently.
	 * 
	 * <p>
	 * If the {@code LIST} or {@code MAP} node has a Meta Node with a MIME type
	 * application/x-tr-069-eager then the node must be immediately created.
	 * Otherwise no new node must be created, this node must be created when the
	 * node is accessed in a subsequent RPC.
	 * <p>
	 * The alias name or instance id must be returned as identifier for the ACS.
	 * 
	 * @param path A table path with an optional alias at the end
	 * @return The name of the new node.
	 * @throws TR069Exception The following fault codes are defined for this
	 *         method: 9001, 9002, 9003, 9004, 9005. If an AddObject request
	 *         would result in exceeding the maximum number of such objects
	 *         supported by the CPE, the CPE MUST return a fault response with
	 *         the Resources Exceeded (9004) fault code.
	 */
	String addObject(String path) throws TR069Exception;

	/**
	 * Delete an object from a table. A missing node must be ignored.
	 * 
	 * @param objectPath The path to an object in a table to be deleted.
	 * @throws TR069Exception The following fault codes are defined for this
	 *         method: 9001, 9002, 9003, 9005. If the fault is caused by an
	 *         invalid objectPath value, the Invalid Parameter Name fault code
	 *         (9005) must be used instead of the more general Invalid Arguments
	 *         fault code (9003). A missing node for {@code objectPath} must be
	 *         ignored.
	 */
	void deleteObject(String objectPath) throws TR069Exception;

	/**
	 * Convert a Dmt Session relative Dmt Admin URI to a valid TR-069 path,
	 * either a table, object, or parameter path depending on the structure of
	 * the DMT. The translation takes into account the special meaning
	 * {@code LIST}, {@code MAP} , {@code Alias}, and {@code InstanceId} nodes.
	 * 
	 * @param uri A Dmt Session relative URI
	 * @return An object, table, or parameter path
	 * @throws TR069Exception If there is an error
	 */
	String toPath(String uri) throws TR069Exception;

	/**
	 * Convert a TR-069 path to a Dmt Session relative Dmt Admin URI. The
	 * translation takes into account the special meaning {@code LIST},
	 * {@code MAP}, {@code InstanceId} node semantics.
	 * <p>
	 * The synthetic {@code Alias} or {@code NumberOfEntries} parameter cannot
	 * be mapped and must throw an {@link TR069Exception#INVALID_PARAMETER_NAME}.
	 * <p>
	 * The returned path is properly escaped for TR-069.
	 * <p>
	 * The mapping from the path to a URI requires support from the meta data in
	 * the DMT, it is not possible to use a mapping solely based on string
	 * replacements. The translation takes into account the semantics of the MAP
	 * and LIST nodes. If at a certain point a node under a {@code MAP} node
	 * does not exist then the Connector can create it if the {@code create}
	 * flag is set to {@code true}. Otherwise a non-existent node will terminate
	 * the mapping.
	 * 
	 * @param name A TR-069 path
	 * @param create If {@code true}, create missing nodes when they reside
	 *        under a MAP or LIST
	 * @return A relative Dmt Admin URI
	 * @throws TR069Exception If there is an error
	 */
	String toURI(String name, boolean create) throws TR069Exception;

	/**
	 * Close this connector. This will <b>not</b> close the corresponding
	 * session.
	 */
	void close();

}
