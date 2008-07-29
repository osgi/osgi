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

package javax.swing;
public abstract class Spring {
	protected Spring() { }
	public static javax.swing.Spring constant(int var0) { return null; }
	public static javax.swing.Spring constant(int var0, int var1, int var2) { return null; }
	public abstract int getMaximumValue();
	public abstract int getMinimumValue();
	public abstract int getPreferredValue();
	public abstract int getValue();
	public static javax.swing.Spring max(javax.swing.Spring var0, javax.swing.Spring var1) { return null; }
	public static javax.swing.Spring minus(javax.swing.Spring var0) { return null; }
	public abstract void setValue(int var0);
	public static javax.swing.Spring sum(javax.swing.Spring var0, javax.swing.Spring var1) { return null; }
	public final static int UNSET = -2147483648;
}

