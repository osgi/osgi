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

package javax.swing.border;
public class BevelBorder extends javax.swing.border.AbstractBorder {
	public BevelBorder(int var0) { }
	public BevelBorder(int var0, java.awt.Color var1, java.awt.Color var2) { }
	public BevelBorder(int var0, java.awt.Color var1, java.awt.Color var2, java.awt.Color var3, java.awt.Color var4) { }
	public int getBevelType() { return 0; }
	public java.awt.Color getHighlightInnerColor() { return null; }
	public java.awt.Color getHighlightInnerColor(java.awt.Component var0) { return null; }
	public java.awt.Color getHighlightOuterColor() { return null; }
	public java.awt.Color getHighlightOuterColor(java.awt.Component var0) { return null; }
	public java.awt.Color getShadowInnerColor() { return null; }
	public java.awt.Color getShadowInnerColor(java.awt.Component var0) { return null; }
	public java.awt.Color getShadowOuterColor() { return null; }
	public java.awt.Color getShadowOuterColor(java.awt.Component var0) { return null; }
	protected void paintLoweredBevel(java.awt.Component var0, java.awt.Graphics var1, int var2, int var3, int var4, int var5) { }
	protected void paintRaisedBevel(java.awt.Component var0, java.awt.Graphics var1, int var2, int var3, int var4, int var5) { }
	public final static int LOWERED = 1;
	public final static int RAISED = 0;
	protected int bevelType;
	protected java.awt.Color highlightInner;
	protected java.awt.Color highlightOuter;
	protected java.awt.Color shadowInner;
	protected java.awt.Color shadowOuter;
}

