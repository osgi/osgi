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

package javax.swing.plaf;
public class BorderUIResource implements java.io.Serializable, javax.swing.border.Border, javax.swing.plaf.UIResource {
	public static class BevelBorderUIResource extends javax.swing.border.BevelBorder implements javax.swing.plaf.UIResource {
		public BevelBorderUIResource(int var0)  { super(0, (java.awt.Color) null, (java.awt.Color) null, (java.awt.Color) null, (java.awt.Color) null); } 
		public BevelBorderUIResource(int var0, java.awt.Color var1, java.awt.Color var2)  { super(0, (java.awt.Color) null, (java.awt.Color) null, (java.awt.Color) null, (java.awt.Color) null); } 
		public BevelBorderUIResource(int var0, java.awt.Color var1, java.awt.Color var2, java.awt.Color var3, java.awt.Color var4)  { super(0, (java.awt.Color) null, (java.awt.Color) null, (java.awt.Color) null, (java.awt.Color) null); } 
	}
	public static class CompoundBorderUIResource extends javax.swing.border.CompoundBorder implements javax.swing.plaf.UIResource {
		public CompoundBorderUIResource(javax.swing.border.Border var0, javax.swing.border.Border var1) { } 
	}
	public static class EmptyBorderUIResource extends javax.swing.border.EmptyBorder implements javax.swing.plaf.UIResource {
		public EmptyBorderUIResource(int var0, int var1, int var2, int var3)  { super((java.awt.Insets) null); } 
		public EmptyBorderUIResource(java.awt.Insets var0)  { super((java.awt.Insets) null); } 
	}
	public static class EtchedBorderUIResource extends javax.swing.border.EtchedBorder implements javax.swing.plaf.UIResource {
		public EtchedBorderUIResource() { } 
		public EtchedBorderUIResource(int var0) { } 
		public EtchedBorderUIResource(int var0, java.awt.Color var1, java.awt.Color var2) { } 
		public EtchedBorderUIResource(java.awt.Color var0, java.awt.Color var1) { } 
	}
	public static class LineBorderUIResource extends javax.swing.border.LineBorder implements javax.swing.plaf.UIResource {
		public LineBorderUIResource(java.awt.Color var0)  { super((java.awt.Color) null, 0, false); } 
		public LineBorderUIResource(java.awt.Color var0, int var1)  { super((java.awt.Color) null, 0, false); } 
	}
	public static class MatteBorderUIResource extends javax.swing.border.MatteBorder implements javax.swing.plaf.UIResource {
		public MatteBorderUIResource(int var0, int var1, int var2, int var3, java.awt.Color var4)  { super((javax.swing.Icon) null); } 
		public MatteBorderUIResource(int var0, int var1, int var2, int var3, javax.swing.Icon var4)  { super((javax.swing.Icon) null); } 
		public MatteBorderUIResource(javax.swing.Icon var0)  { super((javax.swing.Icon) null); } 
	}
	public static class TitledBorderUIResource extends javax.swing.border.TitledBorder implements javax.swing.plaf.UIResource {
		public TitledBorderUIResource(java.lang.String var0)  { super((javax.swing.border.Border) null, (java.lang.String) null, 0, 0, (java.awt.Font) null, (java.awt.Color) null); } 
		public TitledBorderUIResource(javax.swing.border.Border var0)  { super((javax.swing.border.Border) null, (java.lang.String) null, 0, 0, (java.awt.Font) null, (java.awt.Color) null); } 
		public TitledBorderUIResource(javax.swing.border.Border var0, java.lang.String var1)  { super((javax.swing.border.Border) null, (java.lang.String) null, 0, 0, (java.awt.Font) null, (java.awt.Color) null); } 
		public TitledBorderUIResource(javax.swing.border.Border var0, java.lang.String var1, int var2, int var3)  { super((javax.swing.border.Border) null, (java.lang.String) null, 0, 0, (java.awt.Font) null, (java.awt.Color) null); } 
		public TitledBorderUIResource(javax.swing.border.Border var0, java.lang.String var1, int var2, int var3, java.awt.Font var4)  { super((javax.swing.border.Border) null, (java.lang.String) null, 0, 0, (java.awt.Font) null, (java.awt.Color) null); } 
		public TitledBorderUIResource(javax.swing.border.Border var0, java.lang.String var1, int var2, int var3, java.awt.Font var4, java.awt.Color var5)  { super((javax.swing.border.Border) null, (java.lang.String) null, 0, 0, (java.awt.Font) null, (java.awt.Color) null); } 
	}
	public BorderUIResource(javax.swing.border.Border var0) { } 
	public static javax.swing.border.Border getBlackLineBorderUIResource() { return null; }
	public java.awt.Insets getBorderInsets(java.awt.Component var0) { return null; }
	public static javax.swing.border.Border getEtchedBorderUIResource() { return null; }
	public static javax.swing.border.Border getLoweredBevelBorderUIResource() { return null; }
	public static javax.swing.border.Border getRaisedBevelBorderUIResource() { return null; }
	public boolean isBorderOpaque() { return false; }
	public void paintBorder(java.awt.Component var0, java.awt.Graphics var1, int var2, int var3, int var4, int var5) { }
}

