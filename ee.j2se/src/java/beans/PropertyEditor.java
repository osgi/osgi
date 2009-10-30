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

package java.beans;
public interface PropertyEditor {
	void addPropertyChangeListener(java.beans.PropertyChangeListener var0);
	java.lang.String getAsText();
	java.awt.Component getCustomEditor();
	java.lang.String getJavaInitializationString();
	java.lang.String[] getTags();
	java.lang.Object getValue();
	boolean isPaintable();
	void paintValue(java.awt.Graphics var0, java.awt.Rectangle var1);
	void removePropertyChangeListener(java.beans.PropertyChangeListener var0);
	void setAsText(java.lang.String var0);
	void setValue(java.lang.Object var0);
	boolean supportsCustomEditor();
}

