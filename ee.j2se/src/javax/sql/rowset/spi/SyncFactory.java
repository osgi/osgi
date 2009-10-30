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

package javax.sql.rowset.spi;
public class SyncFactory {
	public static java.lang.String ROWSET_SYNC_PROVIDER;
	public static java.lang.String ROWSET_SYNC_PROVIDER_VERSION;
	public static java.lang.String ROWSET_SYNC_VENDOR;
	public static javax.sql.rowset.spi.SyncProvider getInstance(java.lang.String var0) throws javax.sql.rowset.spi.SyncFactoryException { return null; }
	public static java.util.logging.Logger getLogger() throws javax.sql.rowset.spi.SyncFactoryException { return null; }
	public static java.util.Enumeration<javax.sql.rowset.spi.SyncProvider> getRegisteredProviders() throws javax.sql.rowset.spi.SyncFactoryException { return null; }
	public static javax.sql.rowset.spi.SyncFactory getSyncFactory() { return null; }
	public static void registerProvider(java.lang.String var0) throws javax.sql.rowset.spi.SyncFactoryException { }
	public static void setJNDIContext(javax.naming.Context var0) throws javax.sql.rowset.spi.SyncFactoryException { }
	public static void setLogger(java.util.logging.Logger var0) { }
	public static void setLogger(java.util.logging.Logger var0, java.util.logging.Level var1) { }
	public static void unregisterProvider(java.lang.String var0) throws javax.sql.rowset.spi.SyncFactoryException { }
	private SyncFactory() { } /* generated constructor to prevent compiler adding default public constructor */
}

