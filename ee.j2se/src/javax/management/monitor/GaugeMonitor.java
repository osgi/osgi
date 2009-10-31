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
public class GaugeMonitor extends javax.management.monitor.Monitor implements javax.management.monitor.GaugeMonitorMBean {
	public GaugeMonitor() { } 
	/** @deprecated */
	@java.lang.Deprecated
	public java.lang.Number getDerivedGauge() { return null; }
	public java.lang.Number getDerivedGauge(javax.management.ObjectName var0) { return null; }
	/** @deprecated */
	@java.lang.Deprecated
	public long getDerivedGaugeTimeStamp() { return 0l; }
	public long getDerivedGaugeTimeStamp(javax.management.ObjectName var0) { return 0l; }
	public boolean getDifferenceMode() { return false; }
	public java.lang.Number getHighThreshold() { return null; }
	public java.lang.Number getLowThreshold() { return null; }
	public boolean getNotifyHigh() { return false; }
	public boolean getNotifyLow() { return false; }
	public void setDifferenceMode(boolean var0) { }
	public void setNotifyHigh(boolean var0) { }
	public void setNotifyLow(boolean var0) { }
	public void setThresholds(java.lang.Number var0, java.lang.Number var1) { }
	public void start() { }
	public void stop() { }
}

