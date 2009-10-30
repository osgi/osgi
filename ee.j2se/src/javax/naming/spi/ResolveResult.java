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

package javax.naming.spi;
public class ResolveResult implements java.io.Serializable {
	protected javax.naming.Name remainingName;
	protected java.lang.Object resolvedObj;
	protected ResolveResult() { } 
	public ResolveResult(java.lang.Object var0, java.lang.String var1) { } 
	public ResolveResult(java.lang.Object var0, javax.naming.Name var1) { } 
	public void appendRemainingComponent(java.lang.String var0) { }
	public void appendRemainingName(javax.naming.Name var0) { }
	public javax.naming.Name getRemainingName() { return null; }
	public java.lang.Object getResolvedObj() { return null; }
	public void setRemainingName(javax.naming.Name var0) { }
	public void setResolvedObj(java.lang.Object var0) { }
}

