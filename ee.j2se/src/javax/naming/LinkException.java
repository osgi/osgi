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

package javax.naming;
public class LinkException extends javax.naming.NamingException {
	protected java.lang.String linkExplanation;
	protected javax.naming.Name linkRemainingName;
	protected javax.naming.Name linkResolvedName;
	protected java.lang.Object linkResolvedObj;
	public LinkException() { } 
	public LinkException(java.lang.String var0) { } 
	public java.lang.String getLinkExplanation() { return null; }
	public javax.naming.Name getLinkRemainingName() { return null; }
	public javax.naming.Name getLinkResolvedName() { return null; }
	public java.lang.Object getLinkResolvedObj() { return null; }
	public void setLinkExplanation(java.lang.String var0) { }
	public void setLinkRemainingName(javax.naming.Name var0) { }
	public void setLinkResolvedName(javax.naming.Name var0) { }
	public void setLinkResolvedObj(java.lang.Object var0) { }
}

