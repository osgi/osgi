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

package javax.swing.border;
public class EmptyBorder extends javax.swing.border.AbstractBorder implements java.io.Serializable {
	public EmptyBorder(int var0, int var1, int var2, int var3) { }
	public EmptyBorder(java.awt.Insets var0) { }
	public java.awt.Insets getBorderInsets() { return null; }
	protected int bottom;
	protected int left;
	protected int right;
	protected int top;
}

