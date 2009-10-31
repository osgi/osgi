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

package javax.swing;
public abstract class LayoutStyle {
	public enum ComponentPlacement {
		INDENT,
		RELATED,
		UNRELATED;
	}
	public LayoutStyle() { } 
	public abstract int getContainerGap(javax.swing.JComponent var0, int var1, java.awt.Container var2);
	public static javax.swing.LayoutStyle getInstance() { return null; }
	public abstract int getPreferredGap(javax.swing.JComponent var0, javax.swing.JComponent var1, javax.swing.LayoutStyle.ComponentPlacement var2, int var3, java.awt.Container var4);
	public static void setInstance(javax.swing.LayoutStyle var0) { }
}

