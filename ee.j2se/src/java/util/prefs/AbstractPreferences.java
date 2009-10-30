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

package java.util.prefs;
public abstract class AbstractPreferences extends java.util.prefs.Preferences {
	protected final java.lang.Object lock; { lock = null; }
	protected boolean newNode;
	protected AbstractPreferences(java.util.prefs.AbstractPreferences var0, java.lang.String var1) { } 
	public java.lang.String absolutePath() { return null; }
	public void addNodeChangeListener(java.util.prefs.NodeChangeListener var0) { }
	public void addPreferenceChangeListener(java.util.prefs.PreferenceChangeListener var0) { }
	protected final java.util.prefs.AbstractPreferences[] cachedChildren() { return null; }
	protected abstract java.util.prefs.AbstractPreferences childSpi(java.lang.String var0);
	public java.lang.String[] childrenNames() throws java.util.prefs.BackingStoreException { return null; }
	protected abstract java.lang.String[] childrenNamesSpi() throws java.util.prefs.BackingStoreException;
	public void clear() throws java.util.prefs.BackingStoreException { }
	public void exportNode(java.io.OutputStream var0) throws java.io.IOException, java.util.prefs.BackingStoreException { }
	public void exportSubtree(java.io.OutputStream var0) throws java.io.IOException, java.util.prefs.BackingStoreException { }
	public void flush() throws java.util.prefs.BackingStoreException { }
	protected abstract void flushSpi() throws java.util.prefs.BackingStoreException;
	public java.lang.String get(java.lang.String var0, java.lang.String var1) { return null; }
	public boolean getBoolean(java.lang.String var0, boolean var1) { return false; }
	public byte[] getByteArray(java.lang.String var0, byte[] var1) { return null; }
	protected java.util.prefs.AbstractPreferences getChild(java.lang.String var0) throws java.util.prefs.BackingStoreException { return null; }
	public double getDouble(java.lang.String var0, double var1) { return 0.0d; }
	public float getFloat(java.lang.String var0, float var1) { return 0.0f; }
	public int getInt(java.lang.String var0, int var1) { return 0; }
	public long getLong(java.lang.String var0, long var1) { return 0l; }
	protected abstract java.lang.String getSpi(java.lang.String var0);
	protected boolean isRemoved() { return false; }
	public boolean isUserNode() { return false; }
	public java.lang.String[] keys() throws java.util.prefs.BackingStoreException { return null; }
	protected abstract java.lang.String[] keysSpi() throws java.util.prefs.BackingStoreException;
	public java.lang.String name() { return null; }
	public java.util.prefs.Preferences node(java.lang.String var0) { return null; }
	public boolean nodeExists(java.lang.String var0) throws java.util.prefs.BackingStoreException { return false; }
	public java.util.prefs.Preferences parent() { return null; }
	public void put(java.lang.String var0, java.lang.String var1) { }
	public void putBoolean(java.lang.String var0, boolean var1) { }
	public void putByteArray(java.lang.String var0, byte[] var1) { }
	public void putDouble(java.lang.String var0, double var1) { }
	public void putFloat(java.lang.String var0, float var1) { }
	public void putInt(java.lang.String var0, int var1) { }
	public void putLong(java.lang.String var0, long var1) { }
	protected abstract void putSpi(java.lang.String var0, java.lang.String var1);
	public void remove(java.lang.String var0) { }
	public void removeNode() throws java.util.prefs.BackingStoreException { }
	public void removeNodeChangeListener(java.util.prefs.NodeChangeListener var0) { }
	protected abstract void removeNodeSpi() throws java.util.prefs.BackingStoreException;
	public void removePreferenceChangeListener(java.util.prefs.PreferenceChangeListener var0) { }
	protected abstract void removeSpi(java.lang.String var0);
	public void sync() throws java.util.prefs.BackingStoreException { }
	protected abstract void syncSpi() throws java.util.prefs.BackingStoreException;
	public java.lang.String toString() { return null; }
}

