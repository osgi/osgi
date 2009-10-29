/*
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

package java.util.logging;
public class Logger {
	protected Logger(java.lang.String var0, java.lang.String var1) { }
	public void addHandler(java.util.logging.Handler var0) { }
	public void config(java.lang.String var0) { }
	public void entering(java.lang.String var0, java.lang.String var1) { }
	public void entering(java.lang.String var0, java.lang.String var1, java.lang.Object var2) { }
	public void entering(java.lang.String var0, java.lang.String var1, java.lang.Object[] var2) { }
	public void exiting(java.lang.String var0, java.lang.String var1) { }
	public void exiting(java.lang.String var0, java.lang.String var1, java.lang.Object var2) { }
	public void fine(java.lang.String var0) { }
	public void finer(java.lang.String var0) { }
	public void finest(java.lang.String var0) { }
	public static java.util.logging.Logger getAnonymousLogger() { return null; }
	public static java.util.logging.Logger getAnonymousLogger(java.lang.String var0) { return null; }
	public java.util.logging.Filter getFilter() { return null; }
	public java.util.logging.Handler[] getHandlers() { return null; }
	public java.util.logging.Level getLevel() { return null; }
	public static java.util.logging.Logger getLogger(java.lang.String var0) { return null; }
	public static java.util.logging.Logger getLogger(java.lang.String var0, java.lang.String var1) { return null; }
	public java.lang.String getName() { return null; }
	public java.util.logging.Logger getParent() { return null; }
	public java.util.ResourceBundle getResourceBundle() { return null; }
	public java.lang.String getResourceBundleName() { return null; }
	public boolean getUseParentHandlers() { return false; }
	public void info(java.lang.String var0) { }
	public boolean isLoggable(java.util.logging.Level var0) { return false; }
	public void log(java.util.logging.Level var0, java.lang.String var1) { }
	public void log(java.util.logging.Level var0, java.lang.String var1, java.lang.Object var2) { }
	public void log(java.util.logging.Level var0, java.lang.String var1, java.lang.Throwable var2) { }
	public void log(java.util.logging.Level var0, java.lang.String var1, java.lang.Object[] var2) { }
	public void log(java.util.logging.LogRecord var0) { }
	public void logp(java.util.logging.Level var0, java.lang.String var1, java.lang.String var2, java.lang.String var3) { }
	public void logp(java.util.logging.Level var0, java.lang.String var1, java.lang.String var2, java.lang.String var3, java.lang.Object var4) { }
	public void logp(java.util.logging.Level var0, java.lang.String var1, java.lang.String var2, java.lang.String var3, java.lang.Throwable var4) { }
	public void logp(java.util.logging.Level var0, java.lang.String var1, java.lang.String var2, java.lang.String var3, java.lang.Object[] var4) { }
	public void logrb(java.util.logging.Level var0, java.lang.String var1, java.lang.String var2, java.lang.String var3, java.lang.String var4) { }
	public void logrb(java.util.logging.Level var0, java.lang.String var1, java.lang.String var2, java.lang.String var3, java.lang.String var4, java.lang.Object var5) { }
	public void logrb(java.util.logging.Level var0, java.lang.String var1, java.lang.String var2, java.lang.String var3, java.lang.String var4, java.lang.Throwable var5) { }
	public void logrb(java.util.logging.Level var0, java.lang.String var1, java.lang.String var2, java.lang.String var3, java.lang.String var4, java.lang.Object[] var5) { }
	public void removeHandler(java.util.logging.Handler var0) { }
	public void setFilter(java.util.logging.Filter var0) { }
	public void setLevel(java.util.logging.Level var0) { }
	public void setParent(java.util.logging.Logger var0) { }
	public void setUseParentHandlers(boolean var0) { }
	public void severe(java.lang.String var0) { }
	public void throwing(java.lang.String var0, java.lang.String var1, java.lang.Throwable var2) { }
	public void warning(java.lang.String var0) { }
	public final static java.util.logging.Logger global; static { global = null; }
}

