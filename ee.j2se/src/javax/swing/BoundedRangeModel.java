/*
 * Copyright (c) OSGi Alliance (2001, 2013). All Rights Reserved.
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

package javax.swing;
public interface BoundedRangeModel {
	void addChangeListener(javax.swing.event.ChangeListener var0);
	int getExtent();
	int getMaximum();
	int getMinimum();
	int getValue();
	boolean getValueIsAdjusting();
	void removeChangeListener(javax.swing.event.ChangeListener var0);
	void setExtent(int var0);
	void setMaximum(int var0);
	void setMinimum(int var0);
	void setRangeProperties(int var0, int var1, int var2, int var3, boolean var4);
	void setValue(int var0);
	void setValueIsAdjusting(boolean var0);
}
