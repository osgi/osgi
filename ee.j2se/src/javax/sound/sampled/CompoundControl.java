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

package javax.sound.sampled;
public abstract class CompoundControl extends javax.sound.sampled.Control {
	protected CompoundControl(javax.sound.sampled.CompoundControl.Type var0, javax.sound.sampled.Control[] var1) { super((javax.sound.sampled.Control.Type) null); }
	public javax.sound.sampled.Control[] getMemberControls() { return null; }
	public static class Type extends javax.sound.sampled.Control.Type {
		protected Type(java.lang.String var0) { super((java.lang.String) null); }
	}
}

