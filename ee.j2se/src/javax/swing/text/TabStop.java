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

package javax.swing.text;
public class TabStop implements java.io.Serializable {
	public final static int ALIGN_BAR = 5;
	public final static int ALIGN_CENTER = 2;
	public final static int ALIGN_DECIMAL = 4;
	public final static int ALIGN_LEFT = 0;
	public final static int ALIGN_RIGHT = 1;
	public final static int LEAD_DOTS = 1;
	public final static int LEAD_EQUALS = 5;
	public final static int LEAD_HYPHENS = 2;
	public final static int LEAD_NONE = 0;
	public final static int LEAD_THICKLINE = 4;
	public final static int LEAD_UNDERLINE = 3;
	public TabStop(float var0) { } 
	public TabStop(float var0, int var1, int var2) { } 
	public int getAlignment() { return 0; }
	public int getLeader() { return 0; }
	public float getPosition() { return 0.0f; }
}

