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
public class JToolTip extends javax.swing.JComponent implements javax.accessibility.Accessible {
	protected class AccessibleJToolTip extends javax.swing.JComponent.AccessibleJComponent {
		protected AccessibleJToolTip() { } 
	}
	public JToolTip() { } 
	public javax.swing.JComponent getComponent() { return null; }
	public java.lang.String getTipText() { return null; }
	public javax.swing.plaf.ToolTipUI getUI() { return null; }
	public void setComponent(javax.swing.JComponent var0) { }
	public void setTipText(java.lang.String var0) { }
}

