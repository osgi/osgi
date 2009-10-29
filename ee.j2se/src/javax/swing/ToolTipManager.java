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

package javax.swing;
public class ToolTipManager extends java.awt.event.MouseAdapter implements java.awt.event.MouseMotionListener {
	public int getDismissDelay() { return 0; }
	public int getInitialDelay() { return 0; }
	public int getReshowDelay() { return 0; }
	public boolean isEnabled() { return false; }
	public boolean isLightWeightPopupEnabled() { return false; }
	public void mouseDragged(java.awt.event.MouseEvent var0) { }
	public void mouseMoved(java.awt.event.MouseEvent var0) { }
	public void registerComponent(javax.swing.JComponent var0) { }
	public void setDismissDelay(int var0) { }
	public void setEnabled(boolean var0) { }
	public void setInitialDelay(int var0) { }
	public void setLightWeightPopupEnabled(boolean var0) { }
	public void setReshowDelay(int var0) { }
	public static javax.swing.ToolTipManager sharedInstance() { return null; }
	public void unregisterComponent(javax.swing.JComponent var0) { }
	protected boolean heavyWeightPopupEnabled;
	protected boolean lightWeightPopupEnabled;
	protected class insideTimerAction implements java.awt.event.ActionListener {
		protected insideTimerAction() { }
		public void actionPerformed(java.awt.event.ActionEvent var0) { }
	}
	protected class outsideTimerAction implements java.awt.event.ActionListener {
		protected outsideTimerAction() { }
		public void actionPerformed(java.awt.event.ActionEvent var0) { }
	}
	protected class stillInsideTimerAction implements java.awt.event.ActionListener {
		protected stillInsideTimerAction() { }
		public void actionPerformed(java.awt.event.ActionEvent var0) { }
	}
	private ToolTipManager() { } /* generated constructor to prevent compiler adding default public constructor */
}

