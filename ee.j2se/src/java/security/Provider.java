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

package java.security;
public abstract class Provider extends java.util.Properties {
	public static class Service {
		public Service(java.security.Provider var0, java.lang.String var1, java.lang.String var2, java.lang.String var3, java.util.List<java.lang.String> var4, java.util.Map<java.lang.String,java.lang.String> var5) { } 
		public final java.lang.String getAlgorithm() { return null; }
		public final java.lang.String getAttribute(java.lang.String var0) { return null; }
		public final java.lang.String getClassName() { return null; }
		public final java.security.Provider getProvider() { return null; }
		public final java.lang.String getType() { return null; }
		public java.lang.Object newInstance(java.lang.Object var0) throws java.security.NoSuchAlgorithmException { return null; }
		public boolean supportsParameter(java.lang.Object var0) { return false; }
	}
	protected Provider(java.lang.String var0, double var1, java.lang.String var2) { } 
	public java.util.Enumeration<java.lang.Object> elements() { return null; }
	public java.util.Set<java.util.Map.Entry<java.lang.Object,java.lang.Object>> entrySet() { return null; }
	public java.lang.Object get(java.lang.Object var0) { return null; }
	public java.lang.String getInfo() { return null; }
	public java.lang.String getName() { return null; }
	public java.security.Provider.Service getService(java.lang.String var0, java.lang.String var1) { return null; }
	public java.util.Set<java.security.Provider.Service> getServices() { return null; }
	public double getVersion() { return 0.0d; }
	public java.util.Set<java.lang.Object> keySet() { return null; }
	public java.util.Enumeration<java.lang.Object> keys() { return null; }
	public java.lang.Object put(java.lang.Object var0, java.lang.Object var1) { return null; }
	public void putAll(java.util.Map<?,?> var0) { }
	protected void putService(java.security.Provider.Service var0) { }
	public java.lang.Object remove(java.lang.Object var0) { return null; }
	protected void removeService(java.security.Provider.Service var0) { }
	public java.util.Collection<java.lang.Object> values() { return null; }
}

