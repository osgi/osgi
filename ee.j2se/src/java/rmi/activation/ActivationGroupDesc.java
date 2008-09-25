/*
 * $Revision$
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

package java.rmi.activation;
public final class ActivationGroupDesc implements java.io.Serializable {
	public ActivationGroupDesc(java.lang.String var0, java.lang.String var1, java.rmi.MarshalledObject var2, java.util.Properties var3, java.rmi.activation.ActivationGroupDesc.CommandEnvironment var4) { }
	public ActivationGroupDesc(java.util.Properties var0, java.rmi.activation.ActivationGroupDesc.CommandEnvironment var1) { }
	public java.lang.String getClassName() { return null; }
	public java.rmi.activation.ActivationGroupDesc.CommandEnvironment getCommandEnvironment() { return null; }
	public java.rmi.MarshalledObject getData() { return null; }
	public java.lang.String getLocation() { return null; }
	public java.util.Properties getPropertyOverrides() { return null; }
	public int hashCode() { return 0; }
	public static class CommandEnvironment implements java.io.Serializable {
		public CommandEnvironment(java.lang.String var0, java.lang.String[] var1) { }
		public java.lang.String[] getCommandOptions() { return null; }
		public java.lang.String getCommandPath() { return null; }
		public int hashCode() { return 0; }
	}
}

