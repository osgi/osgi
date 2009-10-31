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

package java.util;
public abstract class ResourceBundle {
	public static class Control {
		public final static java.util.List<java.lang.String> FORMAT_CLASS; static { FORMAT_CLASS = null; }
		public final static java.util.List<java.lang.String> FORMAT_DEFAULT; static { FORMAT_DEFAULT = null; }
		public final static java.util.List<java.lang.String> FORMAT_PROPERTIES; static { FORMAT_PROPERTIES = null; }
		public final static long TTL_DONT_CACHE = -1l;
		public final static long TTL_NO_EXPIRATION_CONTROL = -2l;
		protected Control() { } 
		public java.util.List<java.util.Locale> getCandidateLocales(java.lang.String var0, java.util.Locale var1) { return null; }
		public final static java.util.ResourceBundle.Control getControl(java.util.List<java.lang.String> var0) { return null; }
		public java.util.Locale getFallbackLocale(java.lang.String var0, java.util.Locale var1) { return null; }
		public java.util.List<java.lang.String> getFormats(java.lang.String var0) { return null; }
		public final static java.util.ResourceBundle.Control getNoFallbackControl(java.util.List<java.lang.String> var0) { return null; }
		public long getTimeToLive(java.lang.String var0, java.util.Locale var1) { return 0l; }
		public boolean needsReload(java.lang.String var0, java.util.Locale var1, java.lang.String var2, java.lang.ClassLoader var3, java.util.ResourceBundle var4, long var5) { return false; }
		public java.util.ResourceBundle newBundle(java.lang.String var0, java.util.Locale var1, java.lang.String var2, java.lang.ClassLoader var3, boolean var4) throws java.io.IOException, java.lang.IllegalAccessException, java.lang.InstantiationException { return null; }
		public java.lang.String toBundleName(java.lang.String var0, java.util.Locale var1) { return null; }
		public final java.lang.String toResourceName(java.lang.String var0, java.lang.String var1) { return null; }
	}
	protected java.util.ResourceBundle parent;
	public ResourceBundle() { } 
	public final static void clearCache() { }
	public final static void clearCache(java.lang.ClassLoader var0) { }
	public boolean containsKey(java.lang.String var0) { return false; }
	public final static java.util.ResourceBundle getBundle(java.lang.String var0) { return null; }
	public final static java.util.ResourceBundle getBundle(java.lang.String var0, java.util.Locale var1) { return null; }
	public static java.util.ResourceBundle getBundle(java.lang.String var0, java.util.Locale var1, java.lang.ClassLoader var2) { return null; }
	public static java.util.ResourceBundle getBundle(java.lang.String var0, java.util.Locale var1, java.lang.ClassLoader var2, java.util.ResourceBundle.Control var3) { return null; }
	public final static java.util.ResourceBundle getBundle(java.lang.String var0, java.util.Locale var1, java.util.ResourceBundle.Control var2) { return null; }
	public final static java.util.ResourceBundle getBundle(java.lang.String var0, java.util.ResourceBundle.Control var1) { return null; }
	public abstract java.util.Enumeration<java.lang.String> getKeys();
	public java.util.Locale getLocale() { return null; }
	public final java.lang.Object getObject(java.lang.String var0) { return null; }
	public final java.lang.String getString(java.lang.String var0) { return null; }
	public final java.lang.String[] getStringArray(java.lang.String var0) { return null; }
	protected abstract java.lang.Object handleGetObject(java.lang.String var0);
	protected java.util.Set<java.lang.String> handleKeySet() { return null; }
	public java.util.Set<java.lang.String> keySet() { return null; }
	protected void setParent(java.util.ResourceBundle var0) { }
}

