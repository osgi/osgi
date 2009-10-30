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

package java.awt;
public interface Adjustable {
	public final static int HORIZONTAL = 0;
	public final static int NO_ORIENTATION = 2;
	public final static int VERTICAL = 1;
	void addAdjustmentListener(java.awt.event.AdjustmentListener var0);
	int getBlockIncrement();
	int getMaximum();
	int getMinimum();
	int getOrientation();
	int getUnitIncrement();
	int getValue();
	int getVisibleAmount();
	void removeAdjustmentListener(java.awt.event.AdjustmentListener var0);
	void setBlockIncrement(int var0);
	void setMaximum(int var0);
	void setMinimum(int var0);
	void setUnitIncrement(int var0);
	void setValue(int var0);
	void setVisibleAmount(int var0);
}

