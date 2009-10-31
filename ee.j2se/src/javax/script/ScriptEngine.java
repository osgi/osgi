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
public interface ScriptEngine {
	public final static java.lang.String ARGV = "javax.script.argv";
	public final static java.lang.String ENGINE = "javax.script.engine";
	public final static java.lang.String ENGINE_VERSION = "javax.script.engine_version";
	public final static java.lang.String FILENAME = "javax.script.filename";
	public final static java.lang.String LANGUAGE = "javax.script.language";
	public final static java.lang.String LANGUAGE_VERSION = "javax.script.language_version";
	public final static java.lang.String NAME = "javax.script.name";
	javax.script.Bindings createBindings();
	java.lang.Object eval(java.io.Reader var0) throws javax.script.ScriptException;
	java.lang.Object eval(java.io.Reader var0, javax.script.Bindings var1) throws javax.script.ScriptException;
	java.lang.Object eval(java.io.Reader var0, javax.script.ScriptContext var1) throws javax.script.ScriptException;
	java.lang.Object eval(java.lang.String var0) throws javax.script.ScriptException;
	java.lang.Object eval(java.lang.String var0, javax.script.Bindings var1) throws javax.script.ScriptException;
	java.lang.Object eval(java.lang.String var0, javax.script.ScriptContext var1) throws javax.script.ScriptException;
	java.lang.Object get(java.lang.String var0);
	javax.script.Bindings getBindings(int var0);
	javax.script.ScriptContext getContext();
	javax.script.ScriptEngineFactory getFactory();
	void put(java.lang.String var0, java.lang.Object var1);
	void setBindings(javax.script.Bindings var0, int var1);
	void setContext(javax.script.ScriptContext var0);
}

