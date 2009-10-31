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
public interface ScriptContext {
	public final static int ENGINE_SCOPE = 100;
	public final static int GLOBAL_SCOPE = 200;
	java.lang.Object getAttribute(java.lang.String var0);
	java.lang.Object getAttribute(java.lang.String var0, int var1);
	int getAttributesScope(java.lang.String var0);
	javax.script.Bindings getBindings(int var0);
	java.io.Writer getErrorWriter();
	java.io.Reader getReader();
	java.util.List<java.lang.Integer> getScopes();
	java.io.Writer getWriter();
	java.lang.Object removeAttribute(java.lang.String var0, int var1);
	void setAttribute(java.lang.String var0, java.lang.Object var1, int var2);
	void setBindings(javax.script.Bindings var0, int var1);
	void setErrorWriter(java.io.Writer var0);
	void setReader(java.io.Reader var0);
	void setWriter(java.io.Writer var0);
}

