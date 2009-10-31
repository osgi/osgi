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

package javax.script;
public class SimpleScriptContext implements javax.script.ScriptContext {
	protected javax.script.Bindings engineScope;
	protected java.io.Writer errorWriter;
	protected javax.script.Bindings globalScope;
	protected java.io.Reader reader;
	protected java.io.Writer writer;
	public SimpleScriptContext() { } 
	public java.lang.Object getAttribute(java.lang.String var0) { return null; }
	public java.lang.Object getAttribute(java.lang.String var0, int var1) { return null; }
	public int getAttributesScope(java.lang.String var0) { return 0; }
	public javax.script.Bindings getBindings(int var0) { return null; }
	public java.io.Writer getErrorWriter() { return null; }
	public java.io.Reader getReader() { return null; }
	public java.util.List<java.lang.Integer> getScopes() { return null; }
	public java.io.Writer getWriter() { return null; }
	public java.lang.Object removeAttribute(java.lang.String var0, int var1) { return null; }
	public void setAttribute(java.lang.String var0, java.lang.Object var1, int var2) { }
	public void setBindings(javax.script.Bindings var0, int var1) { }
	public void setErrorWriter(java.io.Writer var0) { }
	public void setReader(java.io.Reader var0) { }
	public void setWriter(java.io.Writer var0) { }
}

