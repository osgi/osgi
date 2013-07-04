/*
 * Copyright (c) OSGi Alliance (2013). All Rights Reserved.
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


package org.osgi.service.functionaldevice;

/**
 * The functional group can unite similar functionality based on the group type.
 * The grouping is optionally supported by the <code>FunctionalDevice</code>s.
 * The <code>FunctionalGroup</code> instances are registered in the OSGi service
 * registry. Note that <code>FunctionalGroup</code> services are registered
 * after <code>DeviceFunction</code> services and before
 * <code>FunctionalDevice</code> services. It's possible that
 * {@link #PROPERTY_DEVICE_UID} points to missing service at the moment of the
 * registration. The reverse order is used when the services are unregistered.
 * <code>FunctionalGroup</code> services are unregistered after
 * <code>FunctionalDevice</code> and before <code>DeviceFunction</code>
 * services.
 */
public interface FunctionalGroup {

	/**
	 * The service property value contains the functional group type. It's a
	 * mandatory property. The value type is <code>java.lang.String</code>.
	 */
	public static final String	PROPERTY_TYPE	= "functional.group.type";

	/**
	 * The service property value contains the Device
	 * <code>FunctionalGroup</code> unique identifier. It's a mandatory
	 * property. The value type is <code>java.lang.String</code>. To simplify
	 * the unique identifier generation, the property value must follow the
	 * rule:
	 * <p>
	 * group UID ::= device-id ':' functional-group-id
	 * <p>
	 * group UID - device functional group unique identifier
	 * <p>
	 * device-id - the value of the {@link FunctionalDevice#PROPERTY_UID}
	 * <code>FunctionalDevice</code> service property
	 * <p>
	 * functional-group-id - device functional group identifier in the scope of
	 * the device
	 */
	public static final String	PROPERTY_UID	= "functional.group.UID";

	/**
	 * The service property value contains the functional group description.
	 * It's an optional property. The value type is
	 * <code>java.lang.String</code>.
	 */
	public static final String	PROPERTY_DESCRIPTION	= "functional.group.description";

	/**
	 * The service property value contains the device unique identifier. The
	 * functional group belongs to this device. It's an optional property. The
	 * value type is <code>java.lang.String</code>.
	 */
	public static final String	PROPERTY_DEVICE_UID		= "functional.group.device.UID";

	/**
	 * The service property value contains the function unique identifiers.
	 * Those functions belong to this functional group. It's an optional
	 * property. The value type is <code>java.lang.String[]</code>.
	 */
	public static final String	PROPERTY_FUNCTION_UIDS	= "functional.group.function.UIDs";

}
