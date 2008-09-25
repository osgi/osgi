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

package java.rmi.server;
public abstract class RMISocketFactory implements java.rmi.server.RMIClientSocketFactory, java.rmi.server.RMIServerSocketFactory {
	public RMISocketFactory() { }
	public static java.rmi.server.RMISocketFactory getDefaultSocketFactory() { return null; }
	public static java.rmi.server.RMIFailureHandler getFailureHandler() { return null; }
	public static java.rmi.server.RMISocketFactory getSocketFactory() { return null; }
	public static void setFailureHandler(java.rmi.server.RMIFailureHandler var0) { }
	public static void setSocketFactory(java.rmi.server.RMISocketFactory var0) throws java.io.IOException { }
}

