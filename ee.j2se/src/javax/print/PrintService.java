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

package javax.print;
public abstract interface PrintService {
	public abstract void addPrintServiceAttributeListener(javax.print.event.PrintServiceAttributeListener var0);
	public abstract javax.print.DocPrintJob createPrintJob();
	public abstract boolean equals(java.lang.Object var0);
	public abstract javax.print.attribute.PrintServiceAttribute getAttribute(java.lang.Class var0);
	public abstract javax.print.attribute.PrintServiceAttributeSet getAttributes();
	public abstract java.lang.Object getDefaultAttributeValue(java.lang.Class var0);
	public abstract java.lang.String getName();
	public abstract javax.print.ServiceUIFactory getServiceUIFactory();
	public abstract java.lang.Class[] getSupportedAttributeCategories();
	public abstract java.lang.Object getSupportedAttributeValues(java.lang.Class var0, javax.print.DocFlavor var1, javax.print.attribute.AttributeSet var2);
	public abstract javax.print.DocFlavor[] getSupportedDocFlavors();
	public abstract javax.print.attribute.AttributeSet getUnsupportedAttributes(javax.print.DocFlavor var0, javax.print.attribute.AttributeSet var1);
	public abstract int hashCode();
	public abstract boolean isAttributeCategorySupported(java.lang.Class var0);
	public abstract boolean isAttributeValueSupported(javax.print.attribute.Attribute var0, javax.print.DocFlavor var1, javax.print.attribute.AttributeSet var2);
	public abstract boolean isDocFlavorSupported(javax.print.DocFlavor var0);
	public abstract void removePrintServiceAttributeListener(javax.print.event.PrintServiceAttributeListener var0);
}

