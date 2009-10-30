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

package javax.swing.plaf.synth;
public abstract class SynthStyle {
	public SynthStyle() { } 
	public java.lang.Object get(javax.swing.plaf.synth.SynthContext var0, java.lang.Object var1) { return null; }
	public boolean getBoolean(javax.swing.plaf.synth.SynthContext var0, java.lang.Object var1, boolean var2) { return false; }
	public java.awt.Color getColor(javax.swing.plaf.synth.SynthContext var0, javax.swing.plaf.synth.ColorType var1) { return null; }
	protected abstract java.awt.Color getColorForState(javax.swing.plaf.synth.SynthContext var0, javax.swing.plaf.synth.ColorType var1);
	public java.awt.Font getFont(javax.swing.plaf.synth.SynthContext var0) { return null; }
	protected abstract java.awt.Font getFontForState(javax.swing.plaf.synth.SynthContext var0);
	public javax.swing.plaf.synth.SynthGraphicsUtils getGraphicsUtils(javax.swing.plaf.synth.SynthContext var0) { return null; }
	public javax.swing.Icon getIcon(javax.swing.plaf.synth.SynthContext var0, java.lang.Object var1) { return null; }
	public java.awt.Insets getInsets(javax.swing.plaf.synth.SynthContext var0, java.awt.Insets var1) { return null; }
	public int getInt(javax.swing.plaf.synth.SynthContext var0, java.lang.Object var1, int var2) { return 0; }
	public javax.swing.plaf.synth.SynthPainter getPainter(javax.swing.plaf.synth.SynthContext var0) { return null; }
	public java.lang.String getString(javax.swing.plaf.synth.SynthContext var0, java.lang.Object var1, java.lang.String var2) { return null; }
	public void installDefaults(javax.swing.plaf.synth.SynthContext var0) { }
	public boolean isOpaque(javax.swing.plaf.synth.SynthContext var0) { return false; }
	public void uninstallDefaults(javax.swing.plaf.synth.SynthContext var0) { }
}

