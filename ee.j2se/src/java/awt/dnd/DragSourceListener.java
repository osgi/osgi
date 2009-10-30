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

package java.awt.dnd;
public interface DragSourceListener extends java.util.EventListener {
	void dragDropEnd(java.awt.dnd.DragSourceDropEvent var0);
	void dragEnter(java.awt.dnd.DragSourceDragEvent var0);
	void dragExit(java.awt.dnd.DragSourceEvent var0);
	void dragOver(java.awt.dnd.DragSourceDragEvent var0);
	void dropActionChanged(java.awt.dnd.DragSourceDragEvent var0);
}

