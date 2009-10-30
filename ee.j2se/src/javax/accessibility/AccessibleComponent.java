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

package javax.accessibility;
public interface AccessibleComponent {
	void addFocusListener(java.awt.event.FocusListener var0);
	boolean contains(java.awt.Point var0);
	javax.accessibility.Accessible getAccessibleAt(java.awt.Point var0);
	java.awt.Color getBackground();
	java.awt.Rectangle getBounds();
	java.awt.Cursor getCursor();
	java.awt.Font getFont();
	java.awt.FontMetrics getFontMetrics(java.awt.Font var0);
	java.awt.Color getForeground();
	java.awt.Point getLocation();
	java.awt.Point getLocationOnScreen();
	java.awt.Dimension getSize();
	boolean isEnabled();
	boolean isFocusTraversable();
	boolean isShowing();
	boolean isVisible();
	void removeFocusListener(java.awt.event.FocusListener var0);
	void requestFocus();
	void setBackground(java.awt.Color var0);
	void setBounds(java.awt.Rectangle var0);
	void setCursor(java.awt.Cursor var0);
	void setEnabled(boolean var0);
	void setFont(java.awt.Font var0);
	void setForeground(java.awt.Color var0);
	void setLocation(java.awt.Point var0);
	void setSize(java.awt.Dimension var0);
	void setVisible(boolean var0);
}

