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

package java.beans;
public abstract class PersistenceDelegate {
	public PersistenceDelegate() { }
	protected void initialize(java.lang.Class var0, java.lang.Object var1, java.lang.Object var2, java.beans.Encoder var3) { }
	protected abstract java.beans.Expression instantiate(java.lang.Object var0, java.beans.Encoder var1);
	protected boolean mutatesTo(java.lang.Object var0, java.lang.Object var1) { return false; }
	public void writeObject(java.lang.Object var0, java.beans.Encoder var1) { }
}

