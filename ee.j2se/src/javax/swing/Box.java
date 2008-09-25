/*
 * $Revision$
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
public class Box extends javax.swing.JComponent implements javax.accessibility.Accessible {
	public Box(int var0) { }
	public static java.awt.Component createGlue() { return null; }
	public static javax.swing.Box createHorizontalBox() { return null; }
	public static java.awt.Component createHorizontalGlue() { return null; }
	public static java.awt.Component createHorizontalStrut(int var0) { return null; }
	public static java.awt.Component createRigidArea(java.awt.Dimension var0) { return null; }
	public static javax.swing.Box createVerticalBox() { return null; }
	public static java.awt.Component createVerticalGlue() { return null; }
	public static java.awt.Component createVerticalStrut(int var0) { return null; }
	protected javax.accessibility.AccessibleContext accessibleContext;
	protected class AccessibleBox extends java.awt.Container.AccessibleAWTContainer {
		protected AccessibleBox() { }
	}
	public static class Filler extends javax.swing.JComponent implements javax.accessibility.Accessible {
		public Filler(java.awt.Dimension var0, java.awt.Dimension var1, java.awt.Dimension var2) { }
		public void changeShape(java.awt.Dimension var0, java.awt.Dimension var1, java.awt.Dimension var2) { }
		protected javax.accessibility.AccessibleContext accessibleContext;
		protected class AccessibleBoxFiller extends java.awt.Component.AccessibleAWTComponent {
			protected AccessibleBoxFiller() { }
		}
	}
}

