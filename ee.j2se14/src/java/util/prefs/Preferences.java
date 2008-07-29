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

package java.util.prefs;
public abstract class Preferences {
	protected Preferences() { }
	public abstract java.lang.String absolutePath();
	public abstract void addNodeChangeListener(java.util.prefs.NodeChangeListener var0);
	public abstract void addPreferenceChangeListener(java.util.prefs.PreferenceChangeListener var0);
	public abstract java.lang.String[] childrenNames() throws java.util.prefs.BackingStoreException;
	public abstract void clear() throws java.util.prefs.BackingStoreException;
	public abstract void exportNode(java.io.OutputStream var0) throws java.io.IOException, java.util.prefs.BackingStoreException;
	public abstract void exportSubtree(java.io.OutputStream var0) throws java.io.IOException, java.util.prefs.BackingStoreException;
	public abstract void flush() throws java.util.prefs.BackingStoreException;
	public abstract java.lang.String get(java.lang.String var0, java.lang.String var1);
	public abstract boolean getBoolean(java.lang.String var0, boolean var1);
	public abstract byte[] getByteArray(java.lang.String var0, byte[] var1);
	public abstract double getDouble(java.lang.String var0, double var1);
	public abstract float getFloat(java.lang.String var0, float var1);
	public abstract int getInt(java.lang.String var0, int var1);
	public abstract long getLong(java.lang.String var0, long var1);
	public static void importPreferences(java.io.InputStream var0) throws java.io.IOException, java.util.prefs.InvalidPreferencesFormatException { }
	public abstract boolean isUserNode();
	public abstract java.lang.String[] keys() throws java.util.prefs.BackingStoreException;
	public abstract java.lang.String name();
	public abstract java.util.prefs.Preferences node(java.lang.String var0);
	public abstract boolean nodeExists(java.lang.String var0) throws java.util.prefs.BackingStoreException;
	public abstract java.util.prefs.Preferences parent();
	public abstract void put(java.lang.String var0, java.lang.String var1);
	public abstract void putBoolean(java.lang.String var0, boolean var1);
	public abstract void putByteArray(java.lang.String var0, byte[] var1);
	public abstract void putDouble(java.lang.String var0, double var1);
	public abstract void putFloat(java.lang.String var0, float var1);
	public abstract void putInt(java.lang.String var0, int var1);
	public abstract void putLong(java.lang.String var0, long var1);
	public abstract void remove(java.lang.String var0);
	public abstract void removeNode() throws java.util.prefs.BackingStoreException;
	public abstract void removeNodeChangeListener(java.util.prefs.NodeChangeListener var0);
	public abstract void removePreferenceChangeListener(java.util.prefs.PreferenceChangeListener var0);
	public abstract void sync() throws java.util.prefs.BackingStoreException;
	public static java.util.prefs.Preferences systemNodeForPackage(java.lang.Class var0) { return null; }
	public static java.util.prefs.Preferences systemRoot() { return null; }
	public abstract java.lang.String toString();
	public static java.util.prefs.Preferences userNodeForPackage(java.lang.Class var0) { return null; }
	public static java.util.prefs.Preferences userRoot() { return null; }
	public final static int MAX_KEY_LENGTH = 80;
	public final static int MAX_NAME_LENGTH = 80;
	public final static int MAX_VALUE_LENGTH = 8192;
}

