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

package javax.swing.colorchooser;
public abstract class AbstractColorChooserPanel extends javax.swing.JPanel {
	public AbstractColorChooserPanel() { }
	protected abstract void buildChooser();
	protected java.awt.Color getColorFromModel() { return null; }
	public javax.swing.colorchooser.ColorSelectionModel getColorSelectionModel() { return null; }
	public abstract java.lang.String getDisplayName();
	public int getDisplayedMnemonicIndex() { return 0; }
	public abstract javax.swing.Icon getLargeDisplayIcon();
	public int getMnemonic() { return 0; }
	public abstract javax.swing.Icon getSmallDisplayIcon();
	public void installChooserPanel(javax.swing.JColorChooser var0) { }
	public void uninstallChooserPanel(javax.swing.JColorChooser var0) { }
	public abstract void updateChooser();
}

