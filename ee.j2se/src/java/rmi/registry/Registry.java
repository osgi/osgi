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

package java.rmi.registry;
public interface Registry extends java.rmi.Remote {
	public final static int REGISTRY_PORT = 1099;
	void bind(java.lang.String var0, java.rmi.Remote var1) throws java.rmi.AlreadyBoundException, java.rmi.RemoteException;
	java.lang.String[] list() throws java.rmi.RemoteException;
	java.rmi.Remote lookup(java.lang.String var0) throws java.rmi.NotBoundException, java.rmi.RemoteException;
	void rebind(java.lang.String var0, java.rmi.Remote var1) throws java.rmi.RemoteException;
	void unbind(java.lang.String var0) throws java.rmi.NotBoundException, java.rmi.RemoteException;
}

