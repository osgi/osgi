/*
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

package javax.xml.transform.sax;
public abstract interface TransformerHandler extends org.xml.sax.ContentHandler, org.xml.sax.DTDHandler, org.xml.sax.ext.LexicalHandler {
	public abstract java.lang.String getSystemId();
	public abstract javax.xml.transform.Transformer getTransformer();
	public abstract void setResult(javax.xml.transform.Result var0);
	public abstract void setSystemId(java.lang.String var0);
}

