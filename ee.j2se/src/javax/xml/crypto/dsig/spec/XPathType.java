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

package javax.xml.crypto.dsig.spec;
public class XPathType {
	public static class Filter {
		public final static javax.xml.crypto.dsig.spec.XPathType.Filter INTERSECT; static { INTERSECT = null; }
		public final static javax.xml.crypto.dsig.spec.XPathType.Filter SUBTRACT; static { SUBTRACT = null; }
		public final static javax.xml.crypto.dsig.spec.XPathType.Filter UNION; static { UNION = null; }
		private Filter() { } /* generated constructor to prevent compiler adding default public constructor */
	}
	public XPathType(java.lang.String var0, javax.xml.crypto.dsig.spec.XPathType.Filter var1) { } 
	public XPathType(java.lang.String var0, javax.xml.crypto.dsig.spec.XPathType.Filter var1, java.util.Map var2) { } 
	public java.lang.String getExpression() { return null; }
	public javax.xml.crypto.dsig.spec.XPathType.Filter getFilter() { return null; }
	public java.util.Map getNamespaceMap() { return null; }
}

