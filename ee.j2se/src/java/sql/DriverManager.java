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

package java.sql;
public class DriverManager {
	public static void deregisterDriver(java.sql.Driver var0) throws java.sql.SQLException { }
	public static java.sql.Connection getConnection(java.lang.String var0) throws java.sql.SQLException { return null; }
	public static java.sql.Connection getConnection(java.lang.String var0, java.lang.String var1, java.lang.String var2) throws java.sql.SQLException { return null; }
	public static java.sql.Connection getConnection(java.lang.String var0, java.util.Properties var1) throws java.sql.SQLException { return null; }
	public static java.sql.Driver getDriver(java.lang.String var0) throws java.sql.SQLException { return null; }
	public static java.util.Enumeration<java.sql.Driver> getDrivers() { return null; }
	/** @deprecated */
	public static java.io.PrintStream getLogStream() { return null; }
	public static java.io.PrintWriter getLogWriter() { return null; }
	public static int getLoginTimeout() { return 0; }
	public static void println(java.lang.String var0) { }
	public static void registerDriver(java.sql.Driver var0) throws java.sql.SQLException { }
	/** @deprecated */
	public static void setLogStream(java.io.PrintStream var0) { }
	public static void setLogWriter(java.io.PrintWriter var0) { }
	public static void setLoginTimeout(int var0) { }
	private DriverManager() { } /* generated constructor to prevent compiler adding default public constructor */
}

