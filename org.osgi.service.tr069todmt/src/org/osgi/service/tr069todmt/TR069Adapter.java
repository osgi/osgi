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

import info.dmtree.*;

/**
 * A TR-069 Adapter is an assistant to a TR-069 Protocol Adapter developer. The
 * adapter manages the low level details of converting the different TR-069 RPCs
 * to a Device Management Tree managed by Dmt Admin. The adapter manages the
 * conversions from the TR-069 Object Names to a node in the DMT and vice versa.
 * <p>
 * The Adapter uses a Dmt Session from the caller, the adapter is therefore
 * state less. The adapter does not implement the exact RPCs but only provides
 * the basic functions to set, get, add, delete, and get the (parameter) names
 * of an object. A TR-069 developer must still parse the XML, handle the
 * relative and absolute path issues, open a Dmt Session etc.
 * <p>
 * Each call receives a Dmt Session. The adapter assumes that each parameter or
 * object name is relative to this root.
 * <p>
 * This adapter must convert the TR-069 Names to Dmt Admin URIs. This conversion
 * must take into account the {@code LIST} and {@code MAP} concepts defined in
 * the specifications. These concepts define the use of an {@code InstanceId}
 * node that must be used by the adapter to provide a TR-069 table view on the
 * {@code LIST} and {@code MAP} nodes.
 */
public interface TR069Adapter {

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
	 * 
	 * @param session
	 *            The Dmt Session to use
	 * @param fullParameterName
	 *            A parameter name, must not be partial (end in dot).
	 * @param value
	 *            A trimmed string value that has the given type
	 * @param type
	 *            The type of the parameter ({@link #TR069_INT},
	 *            {@link #TR069_UNSIGNED_INT},{@link #TR069_LONG},
	 *            {@link #TR069_UNSIGNED_LONG},{@link #TR069_STRING},
	 *            {@link #TR069_DATETIME},{@link #TR069_BASE64},
	 *            {@link #TR069_HEXBINARY})
	 * @throws TR069Exception
	 *             if the value cannot be set
	 * @throws DmtException 
	 */
	void setParameterValue(DmtSession session, String fullParameterName,
			String value, int type) throws TR069Exception, DmtException;

	/**
	 * Getting a Parameter. This method should be used to implement
	 * GetParameterValues RPC. This method must convert the Name to the
	 * appropriate node and convert the value to a TR-069 type as specified in
	 * the specification.
	 * <p>
	 * If the ACS requests the values for partial names then it is the
	 * responsibility of the caller to expand the node with GetParameterNames
	 * and then call this method for each parameter.
	 * 
	 * @param session
	 *            The Dmt Session to use
	 * @param fullParameterName
	 *            A parameter name, must not be partial (end in dot).
	 * @return The name, value, and type triad of the requested parameter as
	 *         defined by the TR-069 {@code ParameterValueStruct}.
	 * @throws TR069Exception
	 * @throws DmtException 
	 */
	ParameterValue getParameterValue(DmtSession session,
			String fullParameterName) throws TR069Exception, DmtException;

	/**
	 * Getting the names of the parameters for the node addressed by path. This
	 * method should be used to implement the GetParameterNames RPC. It must
	 * return the children of the addressed node.
	 * <p>
	 * Partial paths and the nextLevel boolean must be handled by the caller.
	 * 
	 * @param session
	 *            The Dmt Session to use
	 * @param fullParameterName
	 *            A parameter name, must not be partial (end in dot).
	 * @return The name of the parameter and the write status as defined by the
	 *         TR-069 {@code ParameterInfoStruct}.
	 * @throws TR069Exception
	 * @throws DmtException 
	 */
	ParameterInfo getParameterNames(DmtSession session, String fullParameterName)
			throws TR069Exception, DmtException;

	/**
	 * Add a new node to the Dmt Admin as defined by the AddObject RPC.
	 * 
	 * @param session
	 *            The Dmt Session to use
	 * @param objectName
	 *            The path name of the collection of objects for which a new
	 *            instance is to be created. The path name MUST end with a “.”
	 *            (dot) after the last node in the hierarchical name of the
	 *            object.
	 * @return The name of the new node
	 * @throws TR069Exception
	 * @throws DmtException 
	 */
	String addObject(DmtSession session, String objectName)
			throws TR069Exception, DmtException;

	/**
	 * @param session
	 *            The Dmt Session to use
	 * @param objectName
	 *            The path name of the collection of objects for which a new
	 *            instance is to be created. The path name MUST end with a “.”
	 *            (dot) after the last node in the hierarchical name of the
	 *            object.
	 * @throws TR069Exception
	 * @throws DmtException 
	 */
	void deleteObject(DmtSession session, String objectName)
			throws TR069Exception, DmtException;

	/**
	 * Convert a relative Dmt Admin URI to a TR-069 Name (Parameter or Object)
	 * that can be used to address the node.
	 * 
	 * @param session
	 *            The Dmt Session to use
	 * @param uri
	 *            A relative URI from the start of the session
	 * @return A relative path
	 * @throws TR069Exception
	 * @throws DmtException 
	 */
	String toName(DmtSession session, String uri) throws TR069Exception, DmtException;

	/**
	 * Convert an absolute TR-069 complete Name (Object or Name) to a relative
	 * Dmt Admin URI.
	 * 
	 * @param session
	 *            The Dmt Session to use
	 * @param name
	 *            A TR-069 Object or Parameter Name
	 * @return A relative Dmt Admin URI
	 * @throws TR069Exception
	 * @throws DmtException 
	 */
	String toURI(DmtSession session, String name) throws TR069Exception, DmtException;

}
