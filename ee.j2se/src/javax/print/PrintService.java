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
public interface PrintService {
	void addPrintServiceAttributeListener(javax.print.event.PrintServiceAttributeListener var0);
	javax.print.DocPrintJob createPrintJob();
	boolean equals(java.lang.Object var0);
	<T extends javax.print.attribute.PrintServiceAttribute> T getAttribute(java.lang.Class<T> var0);
	javax.print.attribute.PrintServiceAttributeSet getAttributes();
	java.lang.Object getDefaultAttributeValue(java.lang.Class<? extends javax.print.attribute.Attribute> var0);
	java.lang.String getName();
	javax.print.ServiceUIFactory getServiceUIFactory();
	java.lang.Class<?>[] getSupportedAttributeCategories();
	java.lang.Object getSupportedAttributeValues(java.lang.Class<? extends javax.print.attribute.Attribute> var0, javax.print.DocFlavor var1, javax.print.attribute.AttributeSet var2);
	javax.print.DocFlavor[] getSupportedDocFlavors();
	javax.print.attribute.AttributeSet getUnsupportedAttributes(javax.print.DocFlavor var0, javax.print.attribute.AttributeSet var1);
	int hashCode();
	boolean isAttributeCategorySupported(java.lang.Class<? extends javax.print.attribute.Attribute> var0);
	boolean isAttributeValueSupported(javax.print.attribute.Attribute var0, javax.print.DocFlavor var1, javax.print.attribute.AttributeSet var2);
	boolean isDocFlavorSupported(javax.print.DocFlavor var0);
	void removePrintServiceAttributeListener(javax.print.event.PrintServiceAttributeListener var0);
}

