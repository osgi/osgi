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

package javax.accessibility;
public abstract interface AccessibleComponent {
	public abstract void addFocusListener(java.awt.event.FocusListener var0);
	public abstract boolean contains(java.awt.Point var0);
	public abstract javax.accessibility.Accessible getAccessibleAt(java.awt.Point var0);
	public abstract java.awt.Color getBackground();
	public abstract java.awt.Rectangle getBounds();
	public abstract java.awt.Cursor getCursor();
	public abstract java.awt.Font getFont();
	public abstract java.awt.FontMetrics getFontMetrics(java.awt.Font var0);
	public abstract java.awt.Color getForeground();
	public abstract java.awt.Point getLocation();
	public abstract java.awt.Point getLocationOnScreen();
	public abstract java.awt.Dimension getSize();
	public abstract boolean isEnabled();
	public abstract boolean isFocusTraversable();
	public abstract boolean isShowing();
	public abstract boolean isVisible();
	public abstract void removeFocusListener(java.awt.event.FocusListener var0);
	public abstract void requestFocus();
	public abstract void setBackground(java.awt.Color var0);
	public abstract void setBounds(java.awt.Rectangle var0);
	public abstract void setCursor(java.awt.Cursor var0);
	public abstract void setEnabled(boolean var0);
	public abstract void setFont(java.awt.Font var0);
	public abstract void setForeground(java.awt.Color var0);
	public abstract void setLocation(java.awt.Point var0);
	public abstract void setSize(java.awt.Dimension var0);
	public abstract void setVisible(boolean var0);
}

