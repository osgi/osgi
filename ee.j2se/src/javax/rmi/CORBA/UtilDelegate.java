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

package javax.rmi.CORBA;
public abstract interface UtilDelegate {
	public abstract java.lang.Object copyObject(java.lang.Object var0, org.omg.CORBA.ORB var1) throws java.rmi.RemoteException;
	public abstract java.lang.Object[] copyObjects(java.lang.Object[] var0, org.omg.CORBA.ORB var1) throws java.rmi.RemoteException;
	public abstract javax.rmi.CORBA.ValueHandler createValueHandler();
	public abstract java.lang.String getCodebase(java.lang.Class var0);
	public abstract javax.rmi.CORBA.Tie getTie(java.rmi.Remote var0);
	public abstract boolean isLocal(javax.rmi.CORBA.Stub var0) throws java.rmi.RemoteException;
	public abstract java.lang.Class loadClass(java.lang.String var0, java.lang.String var1, java.lang.ClassLoader var2) throws java.lang.ClassNotFoundException;
	public abstract java.rmi.RemoteException mapSystemException(org.omg.CORBA.SystemException var0);
	public abstract java.lang.Object readAny(org.omg.CORBA.portable.InputStream var0);
	public abstract void registerTarget(javax.rmi.CORBA.Tie var0, java.rmi.Remote var1);
	public abstract void unexportObject(java.rmi.Remote var0) throws java.rmi.NoSuchObjectException;
	public abstract java.rmi.RemoteException wrapException(java.lang.Throwable var0);
	public abstract void writeAbstractObject(org.omg.CORBA.portable.OutputStream var0, java.lang.Object var1);
	public abstract void writeAny(org.omg.CORBA.portable.OutputStream var0, java.lang.Object var1);
	public abstract void writeRemoteObject(org.omg.CORBA.portable.OutputStream var0, java.lang.Object var1);
}

