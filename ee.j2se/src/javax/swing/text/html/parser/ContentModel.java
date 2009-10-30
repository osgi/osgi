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

package javax.swing.text.html.parser;
public final class ContentModel implements java.io.Serializable {
	public java.lang.Object content;
	public javax.swing.text.html.parser.ContentModel next;
	public int type;
	public ContentModel() { } 
	public ContentModel(int var0, java.lang.Object var1, javax.swing.text.html.parser.ContentModel var2) { } 
	public ContentModel(int var0, javax.swing.text.html.parser.ContentModel var1) { } 
	public ContentModel(javax.swing.text.html.parser.Element var0) { } 
	public boolean empty() { return false; }
	public javax.swing.text.html.parser.Element first() { return null; }
	public boolean first(java.lang.Object var0) { return false; }
	public void getElements(java.util.Vector<javax.swing.text.html.parser.Element> var0) { }
}

