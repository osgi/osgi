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

package javax.print;
public abstract class PrintServiceLookup {
	public PrintServiceLookup() { } 
	public abstract javax.print.PrintService getDefaultPrintService();
	public abstract javax.print.MultiDocPrintService[] getMultiDocPrintServices(javax.print.DocFlavor[] var0, javax.print.attribute.AttributeSet var1);
	public abstract javax.print.PrintService[] getPrintServices();
	public abstract javax.print.PrintService[] getPrintServices(javax.print.DocFlavor var0, javax.print.attribute.AttributeSet var1);
	public final static javax.print.PrintService lookupDefaultPrintService() { return null; }
	public final static javax.print.MultiDocPrintService[] lookupMultiDocPrintServices(javax.print.DocFlavor[] var0, javax.print.attribute.AttributeSet var1) { return null; }
	public final static javax.print.PrintService[] lookupPrintServices(javax.print.DocFlavor var0, javax.print.attribute.AttributeSet var1) { return null; }
	public static boolean registerService(javax.print.PrintService var0) { return false; }
	public static boolean registerServiceProvider(javax.print.PrintServiceLookup var0) { return false; }
}

