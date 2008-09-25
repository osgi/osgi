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

package javax.swing.plaf.basic;
public class BasicColorChooserUI extends javax.swing.plaf.ColorChooserUI {
	public BasicColorChooserUI() { }
	protected javax.swing.colorchooser.AbstractColorChooserPanel[] createDefaultChoosers() { return null; }
	protected java.beans.PropertyChangeListener createPropertyChangeListener() { return null; }
	public static javax.swing.plaf.ComponentUI createUI(javax.swing.JComponent var0) { return null; }
	protected void installDefaults() { }
	protected void installListeners() { }
	protected void installPreviewPanel() { }
	protected void uninstallDefaultChoosers() { }
	protected void uninstallDefaults() { }
	protected void uninstallListeners() { }
	protected javax.swing.colorchooser.AbstractColorChooserPanel[] defaultChoosers;
	protected javax.swing.event.ChangeListener previewListener;
	protected java.beans.PropertyChangeListener propertyChangeListener;
	public class PropertyHandler implements java.beans.PropertyChangeListener {
		public PropertyHandler() { }
		public void propertyChange(java.beans.PropertyChangeEvent var0) { }
	}
}

