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

package java.awt.print;
public class Book implements java.awt.print.Pageable {
	public Book() { } 
	public void append(java.awt.print.Printable var0, java.awt.print.PageFormat var1) { }
	public void append(java.awt.print.Printable var0, java.awt.print.PageFormat var1, int var2) { }
	public int getNumberOfPages() { return 0; }
	public java.awt.print.PageFormat getPageFormat(int var0) { return null; }
	public java.awt.print.Printable getPrintable(int var0) { return null; }
	public void setPage(int var0, java.awt.print.Printable var1, java.awt.print.PageFormat var2) { }
}

