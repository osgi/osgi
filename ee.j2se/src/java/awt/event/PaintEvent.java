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

package java.awt.event;
public class PaintEvent extends java.awt.event.ComponentEvent {
	public final static int PAINT = 800;
	public final static int PAINT_FIRST = 800;
	public final static int PAINT_LAST = 801;
	public final static int UPDATE = 801;
	public PaintEvent(java.awt.Component var0, int var1, java.awt.Rectangle var2)  { super((java.awt.Component) null, 0); } 
	public java.awt.Rectangle getUpdateRect() { return null; }
	public void setUpdateRect(java.awt.Rectangle var0) { }
}

