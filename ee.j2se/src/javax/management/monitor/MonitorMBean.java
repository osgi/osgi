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

package javax.management.monitor;
public interface MonitorMBean {
	void addObservedObject(javax.management.ObjectName var0);
	boolean containsObservedObject(javax.management.ObjectName var0);
	long getGranularityPeriod();
	java.lang.String getObservedAttribute();
	/** @deprecated */ javax.management.ObjectName getObservedObject();
	javax.management.ObjectName[] getObservedObjects();
	boolean isActive();
	void removeObservedObject(javax.management.ObjectName var0);
	void setGranularityPeriod(long var0);
	void setObservedAttribute(java.lang.String var0);
	/** @deprecated */ void setObservedObject(javax.management.ObjectName var0);
	void start();
	void stop();
}

