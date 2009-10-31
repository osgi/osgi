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
public interface DataContentHandler {
	java.lang.Object getContent(javax.activation.DataSource var0) throws java.io.IOException;
	java.lang.Object getTransferData(java.awt.datatransfer.DataFlavor var0, javax.activation.DataSource var1) throws java.awt.datatransfer.UnsupportedFlavorException, java.io.IOException;
	java.awt.datatransfer.DataFlavor[] getTransferDataFlavors();
	void writeTo(java.lang.Object var0, java.lang.String var1, java.io.OutputStream var2) throws java.io.IOException;
}

