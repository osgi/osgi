/*
 * Copyright (c) OSGi Alliance (2009). All Rights Reserved.
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


package org.osgi.service.remoteservices;

import org.osgi.framework.Version;

/**
 * 
 * TODO Add Javadoc comment for this type.
 * 
 * @version $Revision$
 */
public interface InterfaceDescription {

	/**
	 * The fully qualified Java interface name of this interface.
	 * 
	 * @return
	 */
	String getInterfaceName();

	/**
	 * The version of the Java interface of this interface. * In case no version
	 * has been provided for an interface, Discovery may use the String-value of
	 * <code>org.osgi.framework.Version.emptyVersion</code> constant.
	 * 
	 * 
	 * @return
	 */
	Version getVersion();

	/**
	 * The (non-Java) endpoint interface name is usually a communication
	 * protocol specific interface, for instance a web service interface name.
	 * Though this information is usually contained in accompanying properties
	 * e.g. a wsdl file, Discovery usually doesn't read and interprets such
	 * service meta-data. Providing this information explicitly, might allow
	 * external non-Java applications find services based on this endpoint
	 * interface.
	 * 
	 * @return
	 */
	String getEndpointInterfaceName();

}
