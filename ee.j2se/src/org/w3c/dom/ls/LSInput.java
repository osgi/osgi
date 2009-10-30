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

package org.w3c.dom.ls;
public interface LSInput {
	java.lang.String getBaseURI();
	java.io.InputStream getByteStream();
	boolean getCertifiedText();
	java.io.Reader getCharacterStream();
	java.lang.String getEncoding();
	java.lang.String getPublicId();
	java.lang.String getStringData();
	java.lang.String getSystemId();
	void setBaseURI(java.lang.String var0);
	void setByteStream(java.io.InputStream var0);
	void setCertifiedText(boolean var0);
	void setCharacterStream(java.io.Reader var0);
	void setEncoding(java.lang.String var0);
	void setPublicId(java.lang.String var0);
	void setStringData(java.lang.String var0);
	void setSystemId(java.lang.String var0);
}

