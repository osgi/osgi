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
public class ManagementFactory {
	public final static java.lang.String CLASS_LOADING_MXBEAN_NAME = "java.lang:type=ClassLoading";
	public final static java.lang.String COMPILATION_MXBEAN_NAME = "java.lang:type=Compilation";
	public final static java.lang.String GARBAGE_COLLECTOR_MXBEAN_DOMAIN_TYPE = "java.lang:type=GarbageCollector";
	public final static java.lang.String MEMORY_MANAGER_MXBEAN_DOMAIN_TYPE = "java.lang:type=MemoryManager";
	public final static java.lang.String MEMORY_MXBEAN_NAME = "java.lang:type=Memory";
	public final static java.lang.String MEMORY_POOL_MXBEAN_DOMAIN_TYPE = "java.lang:type=MemoryPool";
	public final static java.lang.String OPERATING_SYSTEM_MXBEAN_NAME = "java.lang:type=OperatingSystem";
	public final static java.lang.String RUNTIME_MXBEAN_NAME = "java.lang:type=Runtime";
	public final static java.lang.String THREAD_MXBEAN_NAME = "java.lang:type=Threading";
	public static java.lang.management.ClassLoadingMXBean getClassLoadingMXBean() { return null; }
	public static java.lang.management.CompilationMXBean getCompilationMXBean() { return null; }
	public static java.util.List<java.lang.management.GarbageCollectorMXBean> getGarbageCollectorMXBeans() { return null; }
	public static java.lang.management.MemoryMXBean getMemoryMXBean() { return null; }
	public static java.util.List<java.lang.management.MemoryManagerMXBean> getMemoryManagerMXBeans() { return null; }
	public static java.util.List<java.lang.management.MemoryPoolMXBean> getMemoryPoolMXBeans() { return null; }
	public static java.lang.management.OperatingSystemMXBean getOperatingSystemMXBean() { return null; }
	public static javax.management.MBeanServer getPlatformMBeanServer() { return null; }
	public static java.lang.management.RuntimeMXBean getRuntimeMXBean() { return null; }
	public static java.lang.management.ThreadMXBean getThreadMXBean() { return null; }
	public static <T> T newPlatformMXBeanProxy(javax.management.MBeanServerConnection var0, java.lang.String var1, java.lang.Class<T> var2) throws java.io.IOException { return null; }
	private ManagementFactory() { } /* generated constructor to prevent compiler adding default public constructor */
}

