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

package javax.swing.text;
public abstract class FlowView extends javax.swing.text.BoxView {
	public FlowView(javax.swing.text.Element var0, int var1) { super((javax.swing.text.Element) null, 0); }
	protected abstract javax.swing.text.View createRow();
	public int getFlowAxis() { return 0; }
	public int getFlowSpan(int var0) { return 0; }
	public int getFlowStart(int var0) { return 0; }
	protected javax.swing.text.View layoutPool;
	protected int layoutSpan;
	protected javax.swing.text.FlowView.FlowStrategy strategy;
	public static class FlowStrategy {
		public FlowStrategy() { }
		protected void adjustRow(javax.swing.text.FlowView var0, int var1, int var2, int var3) { }
		public void changedUpdate(javax.swing.text.FlowView var0, javax.swing.event.DocumentEvent var1, java.awt.Rectangle var2) { }
		protected javax.swing.text.View createView(javax.swing.text.FlowView var0, int var1, int var2, int var3) { return null; }
		protected javax.swing.text.View getLogicalView(javax.swing.text.FlowView var0) { return null; }
		public void insertUpdate(javax.swing.text.FlowView var0, javax.swing.event.DocumentEvent var1, java.awt.Rectangle var2) { }
		public void layout(javax.swing.text.FlowView var0) { }
		protected int layoutRow(javax.swing.text.FlowView var0, int var1, int var2) { return 0; }
		public void removeUpdate(javax.swing.text.FlowView var0, javax.swing.event.DocumentEvent var1, java.awt.Rectangle var2) { }
	}
}

