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

package java.awt.im;
public class InputContext {
	protected InputContext() { } 
	public void dispatchEvent(java.awt.AWTEvent var0) { }
	public void dispose() { }
	public void endComposition() { }
	public java.lang.Object getInputMethodControlObject() { return null; }
	public static java.awt.im.InputContext getInstance() { return null; }
	public java.util.Locale getLocale() { return null; }
	public boolean isCompositionEnabled() { return false; }
	public void reconvert() { }
	public void removeNotify(java.awt.Component var0) { }
	public boolean selectInputMethod(java.util.Locale var0) { return false; }
	public void setCharacterSubsets(java.lang.Character.Subset[] var0) { }
	public void setCompositionEnabled(boolean var0) { }
}

