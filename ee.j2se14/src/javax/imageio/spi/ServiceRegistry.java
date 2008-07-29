/*
 * $Date$
 *
 * Copyright (c) OSGi Alliance (2001, 2008). All Rights Reserved.
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

package javax.imageio.spi;
public class ServiceRegistry {
	public ServiceRegistry(java.util.Iterator var0) { }
	public boolean contains(java.lang.Object var0) { return false; }
	public void deregisterAll() { }
	public void deregisterAll(java.lang.Class var0) { }
	public void deregisterServiceProvider(java.lang.Object var0) { }
	public boolean deregisterServiceProvider(java.lang.Object var0, java.lang.Class var1) { return false; }
	public void finalize() throws java.lang.Throwable { }
	public java.util.Iterator getCategories() { return null; }
	public java.lang.Object getServiceProviderByClass(java.lang.Class var0) { return null; }
	public java.util.Iterator getServiceProviders(java.lang.Class var0, javax.imageio.spi.ServiceRegistry.Filter var1, boolean var2) { return null; }
	public java.util.Iterator getServiceProviders(java.lang.Class var0, boolean var1) { return null; }
	public static java.util.Iterator lookupProviders(java.lang.Class var0) { return null; }
	public static java.util.Iterator lookupProviders(java.lang.Class var0, java.lang.ClassLoader var1) { return null; }
	public void registerServiceProvider(java.lang.Object var0) { }
	public boolean registerServiceProvider(java.lang.Object var0, java.lang.Class var1) { return false; }
	public void registerServiceProviders(java.util.Iterator var0) { }
	public boolean setOrdering(java.lang.Class var0, java.lang.Object var1, java.lang.Object var2) { return false; }
	public boolean unsetOrdering(java.lang.Class var0, java.lang.Object var1, java.lang.Object var2) { return false; }
	public static abstract interface Filter {
		public abstract boolean filter(java.lang.Object var0);
	}
}

