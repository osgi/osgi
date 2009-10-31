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
public class SQLClientInfoException extends java.sql.SQLException {
	public SQLClientInfoException() { } 
	public SQLClientInfoException(java.lang.String var0, java.lang.String var1, int var2, java.util.Map<java.lang.String,java.sql.ClientInfoStatus> var3) { } 
	public SQLClientInfoException(java.lang.String var0, java.lang.String var1, int var2, java.util.Map<java.lang.String,java.sql.ClientInfoStatus> var3, java.lang.Throwable var4) { } 
	public SQLClientInfoException(java.lang.String var0, java.lang.String var1, java.util.Map<java.lang.String,java.sql.ClientInfoStatus> var2) { } 
	public SQLClientInfoException(java.lang.String var0, java.lang.String var1, java.util.Map<java.lang.String,java.sql.ClientInfoStatus> var2, java.lang.Throwable var3) { } 
	public SQLClientInfoException(java.lang.String var0, java.util.Map<java.lang.String,java.sql.ClientInfoStatus> var1) { } 
	public SQLClientInfoException(java.lang.String var0, java.util.Map<java.lang.String,java.sql.ClientInfoStatus> var1, java.lang.Throwable var2) { } 
	public SQLClientInfoException(java.util.Map<java.lang.String,java.sql.ClientInfoStatus> var0) { } 
	public SQLClientInfoException(java.util.Map<java.lang.String,java.sql.ClientInfoStatus> var0, java.lang.Throwable var1) { } 
	public java.util.Map<java.lang.String,java.sql.ClientInfoStatus> getFailedProperties() { return null; }
}

