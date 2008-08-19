/*
 * $Date$
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
public class EtchedBorder extends javax.swing.border.AbstractBorder {
	public EtchedBorder() { }
	public EtchedBorder(int var0) { }
	public EtchedBorder(int var0, java.awt.Color var1, java.awt.Color var2) { }
	public EtchedBorder(java.awt.Color var0, java.awt.Color var1) { }
	public int getEtchType() { return 0; }
	public java.awt.Color getHighlightColor() { return null; }
	public java.awt.Color getHighlightColor(java.awt.Component var0) { return null; }
	public java.awt.Color getShadowColor() { return null; }
	public java.awt.Color getShadowColor(java.awt.Component var0) { return null; }
	public final static int LOWERED = 1;
	public final static int RAISED = 0;
	protected int etchType;
	protected java.awt.Color highlight;
	protected java.awt.Color shadow;
}

