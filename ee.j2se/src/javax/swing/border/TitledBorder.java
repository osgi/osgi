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

package javax.swing.border;
public class TitledBorder extends javax.swing.border.AbstractBorder {
	public final static int ABOVE_BOTTOM = 4;
	public final static int ABOVE_TOP = 1;
	public final static int BELOW_BOTTOM = 6;
	public final static int BELOW_TOP = 3;
	public final static int BOTTOM = 5;
	public final static int CENTER = 2;
	public final static int DEFAULT_JUSTIFICATION = 0;
	public final static int DEFAULT_POSITION = 0;
	protected final static int EDGE_SPACING = 2;
	public final static int LEADING = 4;
	public final static int LEFT = 1;
	public final static int RIGHT = 3;
	protected final static int TEXT_INSET_H = 5;
	protected final static int TEXT_SPACING = 2;
	public final static int TOP = 2;
	public final static int TRAILING = 5;
	protected javax.swing.border.Border border;
	protected java.lang.String title;
	protected java.awt.Color titleColor;
	protected java.awt.Font titleFont;
	protected int titleJustification;
	protected int titlePosition;
	public TitledBorder(java.lang.String var0) { } 
	public TitledBorder(javax.swing.border.Border var0) { } 
	public TitledBorder(javax.swing.border.Border var0, java.lang.String var1) { } 
	public TitledBorder(javax.swing.border.Border var0, java.lang.String var1, int var2, int var3) { } 
	public TitledBorder(javax.swing.border.Border var0, java.lang.String var1, int var2, int var3, java.awt.Font var4) { } 
	public TitledBorder(javax.swing.border.Border var0, java.lang.String var1, int var2, int var3, java.awt.Font var4, java.awt.Color var5) { } 
	public javax.swing.border.Border getBorder() { return null; }
	protected java.awt.Font getFont(java.awt.Component var0) { return null; }
	public java.awt.Dimension getMinimumSize(java.awt.Component var0) { return null; }
	public java.lang.String getTitle() { return null; }
	public java.awt.Color getTitleColor() { return null; }
	public java.awt.Font getTitleFont() { return null; }
	public int getTitleJustification() { return 0; }
	public int getTitlePosition() { return 0; }
	public void setBorder(javax.swing.border.Border var0) { }
	public void setTitle(java.lang.String var0) { }
	public void setTitleColor(java.awt.Color var0) { }
	public void setTitleFont(java.awt.Font var0) { }
	public void setTitleJustification(int var0) { }
	public void setTitlePosition(int var0) { }
}

