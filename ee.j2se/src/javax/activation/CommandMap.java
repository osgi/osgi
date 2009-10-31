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

package javax.activation;
public abstract class CommandMap {
	public CommandMap() { } 
	public abstract javax.activation.DataContentHandler createDataContentHandler(java.lang.String var0);
	public javax.activation.DataContentHandler createDataContentHandler(java.lang.String var0, javax.activation.DataSource var1) { return null; }
	public abstract javax.activation.CommandInfo[] getAllCommands(java.lang.String var0);
	public javax.activation.CommandInfo[] getAllCommands(java.lang.String var0, javax.activation.DataSource var1) { return null; }
	public abstract javax.activation.CommandInfo getCommand(java.lang.String var0, java.lang.String var1);
	public javax.activation.CommandInfo getCommand(java.lang.String var0, java.lang.String var1, javax.activation.DataSource var2) { return null; }
	public static javax.activation.CommandMap getDefaultCommandMap() { return null; }
	public java.lang.String[] getMimeTypes() { return null; }
	public abstract javax.activation.CommandInfo[] getPreferredCommands(java.lang.String var0);
	public javax.activation.CommandInfo[] getPreferredCommands(java.lang.String var0, javax.activation.DataSource var1) { return null; }
	public static void setDefaultCommandMap(javax.activation.CommandMap var0) { }
}

