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

package java.rmi.activation;
public final class ActivationDesc implements java.io.Serializable {
	public ActivationDesc(java.lang.String var0, java.lang.String var1, java.rmi.MarshalledObject<?> var2) throws java.rmi.activation.ActivationException { } 
	public ActivationDesc(java.lang.String var0, java.lang.String var1, java.rmi.MarshalledObject<?> var2, boolean var3) throws java.rmi.activation.ActivationException { } 
	public ActivationDesc(java.rmi.activation.ActivationGroupID var0, java.lang.String var1, java.lang.String var2, java.rmi.MarshalledObject<?> var3) { } 
	public ActivationDesc(java.rmi.activation.ActivationGroupID var0, java.lang.String var1, java.lang.String var2, java.rmi.MarshalledObject<?> var3, boolean var4) { } 
	public java.lang.String getClassName() { return null; }
	public java.rmi.MarshalledObject<?> getData() { return null; }
	public java.rmi.activation.ActivationGroupID getGroupID() { return null; }
	public java.lang.String getLocation() { return null; }
	public boolean getRestartMode() { return false; }
}

