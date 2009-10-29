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

package java.awt.event;
public class AdjustmentEvent extends java.awt.AWTEvent {
	public AdjustmentEvent(java.awt.Adjustable var0, int var1, int var2, int var3) { super((java.lang.Object) null, 0); }
	public AdjustmentEvent(java.awt.Adjustable var0, int var1, int var2, int var3, boolean var4) { super((java.lang.Object) null, 0); }
	public java.awt.Adjustable getAdjustable() { return null; }
	public int getAdjustmentType() { return 0; }
	public int getValue() { return 0; }
	public boolean getValueIsAdjusting() { return false; }
	public final static int ADJUSTMENT_FIRST = 601;
	public final static int ADJUSTMENT_LAST = 601;
	public final static int ADJUSTMENT_VALUE_CHANGED = 601;
	public final static int BLOCK_DECREMENT = 3;
	public final static int BLOCK_INCREMENT = 4;
	public final static int TRACK = 5;
	public final static int UNIT_DECREMENT = 2;
	public final static int UNIT_INCREMENT = 1;
}

