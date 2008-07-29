/*
 * $Date$
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

package java.rmi;
public final class Naming {
	public static void bind(java.lang.String var0, java.rmi.Remote var1) throws java.net.MalformedURLException, java.rmi.AlreadyBoundException, java.rmi.RemoteException { }
	public static java.lang.String[] list(java.lang.String var0) throws java.net.MalformedURLException, java.rmi.RemoteException { return null; }
	public static java.rmi.Remote lookup(java.lang.String var0) throws java.net.MalformedURLException, java.rmi.NotBoundException, java.rmi.RemoteException { return null; }
	public static void rebind(java.lang.String var0, java.rmi.Remote var1) throws java.net.MalformedURLException, java.rmi.RemoteException { }
	public static void unbind(java.lang.String var0) throws java.net.MalformedURLException, java.rmi.NotBoundException, java.rmi.RemoteException { }
	private Naming() { } /* generated constructor to prevent compiler adding default public constructor */
}

