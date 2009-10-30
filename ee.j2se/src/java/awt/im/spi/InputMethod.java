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

package java.awt.im.spi;
public interface InputMethod {
	void activate();
	void deactivate(boolean var0);
	void dispatchEvent(java.awt.AWTEvent var0);
	void dispose();
	void endComposition();
	java.lang.Object getControlObject();
	java.util.Locale getLocale();
	void hideWindows();
	boolean isCompositionEnabled();
	void notifyClientWindowChange(java.awt.Rectangle var0);
	void reconvert();
	void removeNotify();
	void setCharacterSubsets(java.lang.Character.Subset[] var0);
	void setCompositionEnabled(boolean var0);
	void setInputMethodContext(java.awt.im.spi.InputMethodContext var0);
	boolean setLocale(java.util.Locale var0);
}

