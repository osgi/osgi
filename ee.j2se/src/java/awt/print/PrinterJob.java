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
public abstract class PrinterJob {
	public PrinterJob() { } 
	public abstract void cancel();
	public java.awt.print.PageFormat defaultPage() { return null; }
	public abstract java.awt.print.PageFormat defaultPage(java.awt.print.PageFormat var0);
	public abstract int getCopies();
	public abstract java.lang.String getJobName();
	public javax.print.PrintService getPrintService() { return null; }
	public static java.awt.print.PrinterJob getPrinterJob() { return null; }
	public abstract java.lang.String getUserName();
	public abstract boolean isCancelled();
	public static javax.print.PrintService[] lookupPrintServices() { return null; }
	public static javax.print.StreamPrintServiceFactory[] lookupStreamPrintServices(java.lang.String var0) { return null; }
	public abstract java.awt.print.PageFormat pageDialog(java.awt.print.PageFormat var0);
	public java.awt.print.PageFormat pageDialog(javax.print.attribute.PrintRequestAttributeSet var0) { return null; }
	public abstract void print() throws java.awt.print.PrinterException;
	public void print(javax.print.attribute.PrintRequestAttributeSet var0) throws java.awt.print.PrinterException { }
	public abstract boolean printDialog();
	public boolean printDialog(javax.print.attribute.PrintRequestAttributeSet var0) { return false; }
	public abstract void setCopies(int var0);
	public abstract void setJobName(java.lang.String var0);
	public abstract void setPageable(java.awt.print.Pageable var0);
	public void setPrintService(javax.print.PrintService var0) throws java.awt.print.PrinterException { }
	public abstract void setPrintable(java.awt.print.Printable var0);
	public abstract void setPrintable(java.awt.print.Printable var0, java.awt.print.PageFormat var1);
	public abstract java.awt.print.PageFormat validatePage(java.awt.print.PageFormat var0);
}

