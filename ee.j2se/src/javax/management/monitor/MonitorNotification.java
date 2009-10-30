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
public class MonitorNotification extends javax.management.Notification {
	public final static java.lang.String OBSERVED_ATTRIBUTE_ERROR = "jmx.monitor.error.attribute";
	public final static java.lang.String OBSERVED_ATTRIBUTE_TYPE_ERROR = "jmx.monitor.error.type";
	public final static java.lang.String OBSERVED_OBJECT_ERROR = "jmx.monitor.error.mbean";
	public final static java.lang.String RUNTIME_ERROR = "jmx.monitor.error.runtime";
	public final static java.lang.String STRING_TO_COMPARE_VALUE_DIFFERED = "jmx.monitor.string.differs";
	public final static java.lang.String STRING_TO_COMPARE_VALUE_MATCHED = "jmx.monitor.string.matches";
	public final static java.lang.String THRESHOLD_ERROR = "jmx.monitor.error.threshold";
	public final static java.lang.String THRESHOLD_HIGH_VALUE_EXCEEDED = "jmx.monitor.gauge.high";
	public final static java.lang.String THRESHOLD_LOW_VALUE_EXCEEDED = "jmx.monitor.gauge.low";
	public final static java.lang.String THRESHOLD_VALUE_EXCEEDED = "jmx.monitor.counter.threshold";
	public java.lang.Object getDerivedGauge() { return null; }
	public java.lang.String getObservedAttribute() { return null; }
	public javax.management.ObjectName getObservedObject() { return null; }
	public java.lang.Object getTrigger() { return null; }
	private MonitorNotification()  { super((java.lang.String) null, (java.lang.Object) null, 0l, (java.lang.String) null); } /* generated constructor to prevent compiler adding default public constructor */
}

