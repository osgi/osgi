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

package java.awt.im.spi;
public abstract interface InputMethod {
	public abstract void activate();
	public abstract void deactivate(boolean var0);
	public abstract void dispatchEvent(java.awt.AWTEvent var0);
	public abstract void dispose();
	public abstract void endComposition();
	public abstract java.lang.Object getControlObject();
	public abstract java.util.Locale getLocale();
	public abstract void hideWindows();
	public abstract boolean isCompositionEnabled();
	public abstract void notifyClientWindowChange(java.awt.Rectangle var0);
	public abstract void reconvert();
	public abstract void removeNotify();
	public abstract void setCharacterSubsets(java.lang.Character.Subset[] var0);
	public abstract void setCompositionEnabled(boolean var0);
	public abstract void setInputMethodContext(java.awt.im.spi.InputMethodContext var0);
	public abstract boolean setLocale(java.util.Locale var0);
}

