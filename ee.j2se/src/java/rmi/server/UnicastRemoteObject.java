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

package java.rmi.server;
public class UnicastRemoteObject extends java.rmi.server.RemoteServer {
	protected UnicastRemoteObject() throws java.rmi.RemoteException { } 
	protected UnicastRemoteObject(int var0) throws java.rmi.RemoteException { } 
	protected UnicastRemoteObject(int var0, java.rmi.server.RMIClientSocketFactory var1, java.rmi.server.RMIServerSocketFactory var2) throws java.rmi.RemoteException { } 
	public java.lang.Object clone() throws java.lang.CloneNotSupportedException { return null; }
	public static java.rmi.server.RemoteStub exportObject(java.rmi.Remote var0) throws java.rmi.RemoteException { return null; }
	public static java.rmi.Remote exportObject(java.rmi.Remote var0, int var1) throws java.rmi.RemoteException { return null; }
	public static java.rmi.Remote exportObject(java.rmi.Remote var0, int var1, java.rmi.server.RMIClientSocketFactory var2, java.rmi.server.RMIServerSocketFactory var3) throws java.rmi.RemoteException { return null; }
	public static boolean unexportObject(java.rmi.Remote var0, boolean var1) throws java.rmi.NoSuchObjectException { return false; }
}

