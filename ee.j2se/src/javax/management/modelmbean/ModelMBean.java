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

package javax.management.modelmbean;
public interface ModelMBean extends javax.management.DynamicMBean, javax.management.PersistentMBean, javax.management.modelmbean.ModelMBeanNotificationBroadcaster {
	void setManagedResource(java.lang.Object var0, java.lang.String var1) throws javax.management.InstanceNotFoundException, javax.management.MBeanException, javax.management.modelmbean.InvalidTargetObjectTypeException;
	void setModelMBeanInfo(javax.management.modelmbean.ModelMBeanInfo var0) throws javax.management.MBeanException;
}

