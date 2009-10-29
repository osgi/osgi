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

package javax.sound.sampled;
public abstract class FloatControl extends javax.sound.sampled.Control {
	protected FloatControl(javax.sound.sampled.FloatControl.Type var0, float var1, float var2, float var3, int var4, float var5, java.lang.String var6) { super((javax.sound.sampled.Control.Type) null); }
	protected FloatControl(javax.sound.sampled.FloatControl.Type var0, float var1, float var2, float var3, int var4, float var5, java.lang.String var6, java.lang.String var7, java.lang.String var8, java.lang.String var9) { super((javax.sound.sampled.Control.Type) null); }
	public java.lang.String getMaxLabel() { return null; }
	public float getMaximum() { return 0.0f; }
	public java.lang.String getMidLabel() { return null; }
	public java.lang.String getMinLabel() { return null; }
	public float getMinimum() { return 0.0f; }
	public float getPrecision() { return 0.0f; }
	public java.lang.String getUnits() { return null; }
	public int getUpdatePeriod() { return 0; }
	public float getValue() { return 0.0f; }
	public void setValue(float var0) { }
	public void shift(float var0, float var1, int var2) { }
	public static class Type extends javax.sound.sampled.Control.Type {
		protected Type(java.lang.String var0) { super((java.lang.String) null); }
		public final static javax.sound.sampled.FloatControl.Type AUX_RETURN; static { AUX_RETURN = null; }
		public final static javax.sound.sampled.FloatControl.Type AUX_SEND; static { AUX_SEND = null; }
		public final static javax.sound.sampled.FloatControl.Type BALANCE; static { BALANCE = null; }
		public final static javax.sound.sampled.FloatControl.Type MASTER_GAIN; static { MASTER_GAIN = null; }
		public final static javax.sound.sampled.FloatControl.Type PAN; static { PAN = null; }
		public final static javax.sound.sampled.FloatControl.Type REVERB_RETURN; static { REVERB_RETURN = null; }
		public final static javax.sound.sampled.FloatControl.Type REVERB_SEND; static { REVERB_SEND = null; }
		public final static javax.sound.sampled.FloatControl.Type SAMPLE_RATE; static { SAMPLE_RATE = null; }
		public final static javax.sound.sampled.FloatControl.Type VOLUME; static { VOLUME = null; }
	}
}

