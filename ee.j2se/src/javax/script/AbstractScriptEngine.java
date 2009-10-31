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
public abstract class AbstractScriptEngine implements javax.script.ScriptEngine {
	protected javax.script.ScriptContext context;
	public AbstractScriptEngine() { } 
	public AbstractScriptEngine(javax.script.Bindings var0) { } 
	public java.lang.Object eval(java.io.Reader var0) throws javax.script.ScriptException { return null; }
	public java.lang.Object eval(java.io.Reader var0, javax.script.Bindings var1) throws javax.script.ScriptException { return null; }
	public java.lang.Object eval(java.lang.String var0) throws javax.script.ScriptException { return null; }
	public java.lang.Object eval(java.lang.String var0, javax.script.Bindings var1) throws javax.script.ScriptException { return null; }
	public java.lang.Object get(java.lang.String var0) { return null; }
	public javax.script.Bindings getBindings(int var0) { return null; }
	public javax.script.ScriptContext getContext() { return null; }
	protected javax.script.ScriptContext getScriptContext(javax.script.Bindings var0) { return null; }
	public void put(java.lang.String var0, java.lang.Object var1) { }
	public void setBindings(javax.script.Bindings var0, int var1) { }
	public void setContext(javax.script.ScriptContext var0) { }
}

