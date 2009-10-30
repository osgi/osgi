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
public interface StringMonitorMBean extends javax.management.monitor.MonitorMBean {
	/** @deprecated */ java.lang.String getDerivedGauge();
	java.lang.String getDerivedGauge(javax.management.ObjectName var0);
	/** @deprecated */ long getDerivedGaugeTimeStamp();
	long getDerivedGaugeTimeStamp(javax.management.ObjectName var0);
	boolean getNotifyDiffer();
	boolean getNotifyMatch();
	java.lang.String getStringToCompare();
	void setNotifyDiffer(boolean var0);
	void setNotifyMatch(boolean var0);
	void setStringToCompare(java.lang.String var0);
}

