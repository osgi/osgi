/*
 * Copyright (c) OSGi Alliance (2001, 2009). All Rights Reserved.
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

package javax.management;
public class JMX {
	public final static java.lang.String DEFAULT_VALUE_FIELD = "defaultValue";
	public final static java.lang.String IMMUTABLE_INFO_FIELD = "immutableInfo";
	public final static java.lang.String INTERFACE_CLASS_NAME_FIELD = "interfaceClassName";
	public final static java.lang.String LEGAL_VALUES_FIELD = "legalValues";
	public final static java.lang.String MAX_VALUE_FIELD = "maxValue";
	public final static java.lang.String MIN_VALUE_FIELD = "minValue";
	public final static java.lang.String MXBEAN_FIELD = "mxbean";
	public final static java.lang.String OPEN_TYPE_FIELD = "openType";
	public final static java.lang.String ORIGINAL_TYPE_FIELD = "originalType";
	public static boolean isMXBeanInterface(java.lang.Class<?> var0) { return false; }
	public static <T> T newMBeanProxy(javax.management.MBeanServerConnection var0, javax.management.ObjectName var1, java.lang.Class<T> var2) { return null; }
	public static <T> T newMBeanProxy(javax.management.MBeanServerConnection var0, javax.management.ObjectName var1, java.lang.Class<T> var2, boolean var3) { return null; }
	public static <T> T newMXBeanProxy(javax.management.MBeanServerConnection var0, javax.management.ObjectName var1, java.lang.Class<T> var2) { return null; }
	public static <T> T newMXBeanProxy(javax.management.MBeanServerConnection var0, javax.management.ObjectName var1, java.lang.Class<T> var2, boolean var3) { return null; }
	private JMX() { } /* generated constructor to prevent compiler adding default public constructor */
}

