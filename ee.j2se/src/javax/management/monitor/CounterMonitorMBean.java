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
public interface CounterMonitorMBean extends javax.management.monitor.MonitorMBean {
	/** @deprecated */
	@java.lang.Deprecated
	java.lang.Number getDerivedGauge();
	java.lang.Number getDerivedGauge(javax.management.ObjectName var0);
	/** @deprecated */
	@java.lang.Deprecated
	long getDerivedGaugeTimeStamp();
	long getDerivedGaugeTimeStamp(javax.management.ObjectName var0);
	boolean getDifferenceMode();
	java.lang.Number getInitThreshold();
	java.lang.Number getModulus();
	boolean getNotify();
	java.lang.Number getOffset();
	/** @deprecated */
	@java.lang.Deprecated
	java.lang.Number getThreshold();
	java.lang.Number getThreshold(javax.management.ObjectName var0);
	void setDifferenceMode(boolean var0);
	void setInitThreshold(java.lang.Number var0);
	void setModulus(java.lang.Number var0);
	void setNotify(boolean var0);
	void setOffset(java.lang.Number var0);
	/** @deprecated */
	@java.lang.Deprecated
	void setThreshold(java.lang.Number var0);
}

