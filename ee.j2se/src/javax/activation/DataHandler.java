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
public class DataHandler implements java.awt.datatransfer.Transferable {
	public DataHandler(java.lang.Object var0, java.lang.String var1) { } 
	public DataHandler(java.net.URL var0) { } 
	public DataHandler(javax.activation.DataSource var0) { } 
	public javax.activation.CommandInfo[] getAllCommands() { return null; }
	public java.lang.Object getBean(javax.activation.CommandInfo var0) { return null; }
	public javax.activation.CommandInfo getCommand(java.lang.String var0) { return null; }
	public java.lang.Object getContent() throws java.io.IOException { return null; }
	public java.lang.String getContentType() { return null; }
	public javax.activation.DataSource getDataSource() { return null; }
	public java.io.InputStream getInputStream() throws java.io.IOException { return null; }
	public java.lang.String getName() { return null; }
	public java.io.OutputStream getOutputStream() throws java.io.IOException { return null; }
	public javax.activation.CommandInfo[] getPreferredCommands() { return null; }
	public java.lang.Object getTransferData(java.awt.datatransfer.DataFlavor var0) throws java.awt.datatransfer.UnsupportedFlavorException, java.io.IOException { return null; }
	public java.awt.datatransfer.DataFlavor[] getTransferDataFlavors() { return null; }
	public boolean isDataFlavorSupported(java.awt.datatransfer.DataFlavor var0) { return false; }
	public void setCommandMap(javax.activation.CommandMap var0) { }
	public static void setDataContentHandlerFactory(javax.activation.DataContentHandlerFactory var0) { }
	public void writeTo(java.io.OutputStream var0) throws java.io.IOException { }
}

