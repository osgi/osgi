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

package java.lang.management;
public interface RuntimeMXBean {
	java.lang.String getBootClassPath();
	java.lang.String getClassPath();
	java.util.List<java.lang.String> getInputArguments();
	java.lang.String getLibraryPath();
	java.lang.String getManagementSpecVersion();
	java.lang.String getName();
	java.lang.String getSpecName();
	java.lang.String getSpecVendor();
	java.lang.String getSpecVersion();
	long getStartTime();
	java.util.Map<java.lang.String,java.lang.String> getSystemProperties();
	long getUptime();
	java.lang.String getVmName();
	java.lang.String getVmVendor();
	java.lang.String getVmVersion();
	boolean isBootClassPathSupported();
}

