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

package java.awt;
public class TextArea extends java.awt.TextComponent {
	public TextArea() { }
	public TextArea(int var0, int var1) { }
	public TextArea(java.lang.String var0) { }
	public TextArea(java.lang.String var0, int var1, int var2) { }
	public TextArea(java.lang.String var0, int var1, int var2, int var3) { }
	public void append(java.lang.String var0) { }
	/** @deprecated */ public void appendText(java.lang.String var0) { }
	public int getColumns() { return 0; }
	public java.awt.Dimension getMinimumSize(int var0, int var1) { return null; }
	public java.awt.Dimension getPreferredSize(int var0, int var1) { return null; }
	public int getRows() { return 0; }
	public int getScrollbarVisibility() { return 0; }
	public void insert(java.lang.String var0, int var1) { }
	/** @deprecated */ public void insertText(java.lang.String var0, int var1) { }
	/** @deprecated */ public java.awt.Dimension minimumSize(int var0, int var1) { return null; }
	/** @deprecated */ public java.awt.Dimension preferredSize(int var0, int var1) { return null; }
	public void replaceRange(java.lang.String var0, int var1, int var2) { }
	/** @deprecated */ public void replaceText(java.lang.String var0, int var1, int var2) { }
	public void setColumns(int var0) { }
	public void setRows(int var0) { }
	public final static int SCROLLBARS_BOTH = 0;
	public final static int SCROLLBARS_HORIZONTAL_ONLY = 2;
	public final static int SCROLLBARS_NONE = 3;
	public final static int SCROLLBARS_VERTICAL_ONLY = 1;
	protected class AccessibleAWTTextArea extends java.awt.TextComponent.AccessibleAWTTextComponent {
		protected AccessibleAWTTextArea() { }
	}
}

