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
public class MemoryNotificationInfo {
	public final static java.lang.String MEMORY_COLLECTION_THRESHOLD_EXCEEDED = "java.management.memory.collection.threshold.exceeded";
	public final static java.lang.String MEMORY_THRESHOLD_EXCEEDED = "java.management.memory.threshold.exceeded";
	public MemoryNotificationInfo(java.lang.String var0, java.lang.management.MemoryUsage var1, long var2) { } 
	public static java.lang.management.MemoryNotificationInfo from(javax.management.openmbean.CompositeData var0) { return null; }
	public long getCount() { return 0l; }
	public java.lang.String getPoolName() { return null; }
	public java.lang.management.MemoryUsage getUsage() { return null; }
}

