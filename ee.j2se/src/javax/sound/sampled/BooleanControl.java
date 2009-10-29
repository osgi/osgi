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
public abstract class BooleanControl extends javax.sound.sampled.Control {
	protected BooleanControl(javax.sound.sampled.BooleanControl.Type var0, boolean var1) { super((javax.sound.sampled.Control.Type) null); }
	protected BooleanControl(javax.sound.sampled.BooleanControl.Type var0, boolean var1, java.lang.String var2, java.lang.String var3) { super((javax.sound.sampled.Control.Type) null); }
	public java.lang.String getStateLabel(boolean var0) { return null; }
	public boolean getValue() { return false; }
	public void setValue(boolean var0) { }
	public static class Type extends javax.sound.sampled.Control.Type {
		protected Type(java.lang.String var0) { super((java.lang.String) null); }
		public final static javax.sound.sampled.BooleanControl.Type APPLY_REVERB; static { APPLY_REVERB = null; }
		public final static javax.sound.sampled.BooleanControl.Type MUTE; static { MUTE = null; }
	}
}

