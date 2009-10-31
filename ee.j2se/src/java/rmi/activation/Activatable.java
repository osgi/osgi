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
public abstract class Activatable extends java.rmi.server.RemoteServer {
	protected Activatable(java.lang.String var0, java.rmi.MarshalledObject<?> var1, boolean var2, int var3) throws java.rmi.RemoteException, java.rmi.activation.ActivationException { } 
	protected Activatable(java.lang.String var0, java.rmi.MarshalledObject<?> var1, boolean var2, int var3, java.rmi.server.RMIClientSocketFactory var4, java.rmi.server.RMIServerSocketFactory var5) throws java.rmi.RemoteException, java.rmi.activation.ActivationException { } 
	protected Activatable(java.rmi.activation.ActivationID var0, int var1) throws java.rmi.RemoteException { } 
	protected Activatable(java.rmi.activation.ActivationID var0, int var1, java.rmi.server.RMIClientSocketFactory var2, java.rmi.server.RMIServerSocketFactory var3) throws java.rmi.RemoteException { } 
	public static java.rmi.activation.ActivationID exportObject(java.rmi.Remote var0, java.lang.String var1, java.rmi.MarshalledObject<?> var2, boolean var3, int var4) throws java.rmi.RemoteException, java.rmi.activation.ActivationException { return null; }
	public static java.rmi.activation.ActivationID exportObject(java.rmi.Remote var0, java.lang.String var1, java.rmi.MarshalledObject<?> var2, boolean var3, int var4, java.rmi.server.RMIClientSocketFactory var5, java.rmi.server.RMIServerSocketFactory var6) throws java.rmi.RemoteException, java.rmi.activation.ActivationException { return null; }
	public static java.rmi.Remote exportObject(java.rmi.Remote var0, java.rmi.activation.ActivationID var1, int var2) throws java.rmi.RemoteException { return null; }
	public static java.rmi.Remote exportObject(java.rmi.Remote var0, java.rmi.activation.ActivationID var1, int var2, java.rmi.server.RMIClientSocketFactory var3, java.rmi.server.RMIServerSocketFactory var4) throws java.rmi.RemoteException { return null; }
	protected java.rmi.activation.ActivationID getID() { return null; }
	public static boolean inactive(java.rmi.activation.ActivationID var0) throws java.rmi.RemoteException, java.rmi.activation.ActivationException { return false; }
	public static java.rmi.Remote register(java.rmi.activation.ActivationDesc var0) throws java.rmi.RemoteException, java.rmi.activation.ActivationException { return null; }
	public static boolean unexportObject(java.rmi.Remote var0, boolean var1) throws java.rmi.NoSuchObjectException { return false; }
	public static void unregister(java.rmi.activation.ActivationID var0) throws java.rmi.RemoteException, java.rmi.activation.ActivationException { }
}

