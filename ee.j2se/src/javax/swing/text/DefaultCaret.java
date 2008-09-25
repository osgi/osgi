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

package javax.swing.text;
public class DefaultCaret extends java.awt.Rectangle implements java.awt.event.FocusListener, java.awt.event.MouseListener, java.awt.event.MouseMotionListener, javax.swing.text.Caret {
	public DefaultCaret() { }
	public void addChangeListener(javax.swing.event.ChangeListener var0) { }
	protected void adjustVisibility(java.awt.Rectangle var0) { }
	protected void damage(java.awt.Rectangle var0) { }
	public void deinstall(javax.swing.text.JTextComponent var0) { }
	protected void fireStateChanged() { }
	public void focusGained(java.awt.event.FocusEvent var0) { }
	public void focusLost(java.awt.event.FocusEvent var0) { }
	public int getBlinkRate() { return 0; }
	public javax.swing.event.ChangeListener[] getChangeListeners() { return null; }
	protected final javax.swing.text.JTextComponent getComponent() { return null; }
	public int getDot() { return 0; }
	public java.util.EventListener[] getListeners(java.lang.Class var0) { return null; }
	public java.awt.Point getMagicCaretPosition() { return null; }
	public int getMark() { return 0; }
	protected javax.swing.text.Highlighter.HighlightPainter getSelectionPainter() { return null; }
	public void install(javax.swing.text.JTextComponent var0) { }
	public boolean isSelectionVisible() { return false; }
	public boolean isVisible() { return false; }
	public void mouseClicked(java.awt.event.MouseEvent var0) { }
	public void mouseDragged(java.awt.event.MouseEvent var0) { }
	public void mouseEntered(java.awt.event.MouseEvent var0) { }
	public void mouseExited(java.awt.event.MouseEvent var0) { }
	public void mouseMoved(java.awt.event.MouseEvent var0) { }
	public void mousePressed(java.awt.event.MouseEvent var0) { }
	public void mouseReleased(java.awt.event.MouseEvent var0) { }
	protected void moveCaret(java.awt.event.MouseEvent var0) { }
	public void moveDot(int var0) { }
	public void paint(java.awt.Graphics var0) { }
	protected void positionCaret(java.awt.event.MouseEvent var0) { }
	public void removeChangeListener(javax.swing.event.ChangeListener var0) { }
	protected final void repaint() { }
	public void setBlinkRate(int var0) { }
	public void setDot(int var0) { }
	public void setMagicCaretPosition(java.awt.Point var0) { }
	public void setSelectionVisible(boolean var0) { }
	public void setVisible(boolean var0) { }
	protected javax.swing.event.ChangeEvent changeEvent;
	protected javax.swing.event.EventListenerList listenerList;
}

