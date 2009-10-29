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

package javax.swing.plaf.basic;
public abstract class BasicLookAndFeel extends javax.swing.LookAndFeel implements java.io.Serializable {
	public BasicLookAndFeel() { }
	protected javax.swing.Action createAudioAction(java.lang.Object var0) { return null; }
	protected javax.swing.ActionMap getAudioActionMap() { return null; }
	protected void initClassDefaults(javax.swing.UIDefaults var0) { }
	protected void initComponentDefaults(javax.swing.UIDefaults var0) { }
	protected void initSystemColorDefaults(javax.swing.UIDefaults var0) { }
	protected void loadSystemColors(javax.swing.UIDefaults var0, java.lang.String[] var1, boolean var2) { }
	protected void playSound(javax.swing.Action var0) { }
}

