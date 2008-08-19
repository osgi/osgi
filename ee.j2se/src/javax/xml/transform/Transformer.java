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

package javax.xml.transform;
public abstract class Transformer {
	protected Transformer() { }
	public abstract void clearParameters();
	public abstract javax.xml.transform.ErrorListener getErrorListener();
	public abstract java.util.Properties getOutputProperties();
	public abstract java.lang.String getOutputProperty(java.lang.String var0);
	public abstract java.lang.Object getParameter(java.lang.String var0);
	public abstract javax.xml.transform.URIResolver getURIResolver();
	public abstract void setErrorListener(javax.xml.transform.ErrorListener var0);
	public abstract void setOutputProperties(java.util.Properties var0);
	public abstract void setOutputProperty(java.lang.String var0, java.lang.String var1);
	public abstract void setParameter(java.lang.String var0, java.lang.Object var1);
	public abstract void setURIResolver(javax.xml.transform.URIResolver var0);
	public abstract void transform(javax.xml.transform.Source var0, javax.xml.transform.Result var1) throws javax.xml.transform.TransformerException;
}

